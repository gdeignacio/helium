/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.conselldemallorca.helium.core.extern.domini.DominiHelium;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.OrigenCredencials;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusAuthDomini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Domini
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class DominiDao extends HibernateGenericDao<Domini, Long> {

	private static final String CACHE_KEY_SEPARATOR = "#";
	private Ehcache dominiCache;

	private Map<Long, DominiHelium> wsCache = new HashMap<Long, DominiHelium>();
	private Map<Long, NamedParameterJdbcTemplate> jdbcTemplates = new HashMap<Long, NamedParameterJdbcTemplate>();



	public DominiDao() {
		super(Domini.class);
	}

	public List<Domini> findAmbEntorn(Long entornId) {
		return findByCriteria(Restrictions.eq("entorn.id", entornId),
				Restrictions.isNull("expedientTipus.id"));
	}
	public List<Domini> findAmbEntornITipusExp(Long entornId, Long tipusExpedientId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("expedientTipus.id", tipusExpedientId));
	}
	public List<Domini> findAmbEntornITipusExpONull(Long entornId, Long tipusExpedientId) {
		List<Domini> dominis = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.isNull("expedientTipus.id"));
		if (tipusExpedientId != null){
			dominis.addAll(findAmbEntornITipusExp(entornId, tipusExpedientId));
		}
		return dominis;
	}
	public Domini findAmbEntornICodi(Long entornId, String codi) {
		List<Domini> dominis = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("codi", codi));
		if (dominis.size() > 0)
			return dominis.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<FilaResultat> consultar(
			Long dominiId,
			String id,
			Map<String, Object> parametres) throws Exception {
		List<FilaResultat> resultat = null;
		Domini domini = getById(dominiId, false);
		String cacheKey = getCacheKey(domini.getId(), parametres);
		Element element = null;
		if (dominiCache != null)
			element = dominiCache.get(cacheKey);
		if (element == null) {
			if (domini.getTipus().equals(TipusDomini.CONSULTA_WS))
				resultat = consultaWs(domini, id, parametres);
			else if (domini.getTipus().equals(TipusDomini.CONSULTA_SQL))
				resultat = consultaSql(domini, parametres);
			if (domini.getCacheSegons() > 0) {
				element = new Element(cacheKey, resultat);
				element.setTimeToLive(domini.getCacheSegons());
				if (dominiCache != null) {
					dominiCache.put(element);
					//logger.info("Cache domini '" + cacheKey + "': " + resultat.size() + " registres");
				}
			}
		} else {
			resultat = (List<FilaResultat>)element.getValue();
			//logger.info("Resultat en cache");
		}
		if (resultat == null)
			resultat = new ArrayList<FilaResultat>();
		return resultat;
	}

	public void makeDirty(Long dominiId) {
		wsCache.remove(dominiId);
		jdbcTemplates.remove(dominiId);
	}



	public void setDominiCache(Ehcache dominiCache) {
		this.dominiCache = dominiCache;
	}



	private List<FilaResultat> consultaWs(
			Domini domini,
			String id,
			Map<String, Object> parametres) throws Exception {
		if ("intern".equalsIgnoreCase(domini.getCodi())) {
			parametres.put("entorn", domini.getEntorn().getCodi());
		}
		String usuari = null;
		String contrasenya = null;
		if (domini.getTipusAuth() != null && !TipusAuthDomini.NONE.equals(domini.getTipusAuth())) {
			if (OrigenCredencials.PROPERTIES.equals(domini.getOrigenCredencials())) {
				usuari = GlobalProperties.getInstance().getProperty(domini.getUsuari());
				contrasenya = GlobalProperties.getInstance().getProperty(domini.getContrasenya());
			} else {
				usuari = domini.getUsuari();
				contrasenya = domini.getContrasenya();
			}
		}
		String auth = "NONE";
		if (TipusAuthDomini.HTTP_BASIC.equals(domini.getTipusAuth()))
			auth = "BASIC";
		if (TipusAuthDomini.USERNAMETOKEN.equals(domini.getTipusAuth()))
			auth = "USERNAMETOKEN";
		DominiHelium client = (DominiHelium)WsClientUtils.getWsClientProxy(
				DominiHelium.class,
				domini.getUrl(),
				usuari,
				contrasenya,
				auth,
				false,
				false,
				true);
		List<ParellaCodiValor> paramsConsulta = new ArrayList<ParellaCodiValor>();
		if (parametres != null) {
			for (String codi: parametres.keySet()) {
				paramsConsulta.add(new ParellaCodiValor(
						codi,
						parametres.get(codi)));
			}
		}
		List<FilaResultat> resposta = client.consultaDomini(id, paramsConsulta);
		return resposta;
	}
	@SuppressWarnings("unchecked")
	private List<FilaResultat> consultaSql(
			Domini domini,
			Map<String, Object> parametres) throws DominiException {
		try {
			NamedParameterJdbcTemplate jdbcTemplate = getJdbcTemplateFromDomini(domini);
			MapSqlParameterSource parameterSource = new MapSqlParameterSource(parametres) {
				public boolean hasValue(String paramName) {
					return true;
				}
			};
			List<FilaResultat> resultat = jdbcTemplate.query(
					domini.getSql(),
					parameterSource,
					new RowMapper() {
						public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
							FilaResultat fr = new FilaResultat();
							ResultSetMetaData rsm = rs.getMetaData();
							for (int i = 1; i <= rsm.getColumnCount(); i++) {
								fr.addColumna(new ParellaCodiValor(
										rsm.getColumnName(i),
										rs.getObject(i)));
							}
							return fr;
						}
					});
			return resultat;
		} catch (Exception ex) {
			throw new DominiException("No s'ha pogut consultar el domini", ex);
		}
	}

	private NamedParameterJdbcTemplate getJdbcTemplateFromDomini(Domini domini) throws NamingException {
		NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplates.get(domini.getId());
		if (jdbcTemplate == null) {
			Context initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup(domini.getJndiDatasource());
			jdbcTemplate = new NamedParameterJdbcTemplate(ds);
			jdbcTemplates.put(domini.getId(), jdbcTemplate);
		}
		return jdbcTemplate;
	}

	private String getCacheKey(
			Long dominiId,
			Map<String, Object> parametres) {
		StringBuffer sb = new StringBuffer();
		sb.append(dominiId.toString());
		sb.append(CACHE_KEY_SEPARATOR);
		if (parametres != null) {
			for (String clau: parametres.keySet()) {
				sb.append(clau);
				sb.append(CACHE_KEY_SEPARATOR);
				Object valor = parametres.get(clau);
				if (valor == null)
					sb.append("<null>");
				else
					sb.append(valor.toString());
				sb.append(CACHE_KEY_SEPARATOR);
			}
		}
		return sb.toString();
	}

}
