package net.conselldemallorca.helium.integracio.plugins.persones;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona.Sexe;
import net.conselldemallorca.helium.model.exception.DominiException;
import net.conselldemallorca.helium.model.exception.PersonaPluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * Implementació de la interficie PersonesPlugin amb accés per JDBC.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */

public class PersonesPluginJdbc implements PersonesPlugin {
	
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	public Persona findAmbCodi(String codi) {
		try {
			String query = GlobalProperties.getInstance().getProperty("app.persones.plugin.jdbc.filter.code");
			Map<String, Object> parametres = new HashMap<String, Object>();
			parametres.put("codi", codi);
			
			List<Persona> resultat = consultaSql(query, parametres);
			
			if (resultat.size() > 0) {
				return resultat.get(0);
			}
			
			return null;
		} catch (Exception ex) {
			throw new PersonaPluginException("No s'ha pogut trobar la persona", ex);
		}
	}

	public List<Persona> findLikeNomSencer(String text) {
		try {
			String query = GlobalProperties.getInstance().getProperty("app.persones.plugin.jdbc.filter.name");
			Map<String, Object> parametres = new HashMap<String, Object>();
			parametres.put("nom", text);
			
			List<Persona> resultat = consultaSql(query, parametres);
			
			if (resultat.size() > 0) {
				return resultat;
			}
			
			return null;
		} catch (Exception ex) {
			throw new PersonaPluginException("No s'ha pogut trobar cap persona", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Persona> consultaSql(
			String query,
			Map<String, Object> parametres) throws PersonaPluginException {
		try {
			String jndi = GlobalProperties.getInstance().getProperty("app.persones.plugin.jdbc.jndi.parameter");
			Context initContext = new InitialContext();
			DataSource ds = (DataSource)initContext.lookup(jndi);
			namedJdbcTemplate = new NamedParameterJdbcTemplate(ds);
			MapSqlParameterSource parameterSource = new MapSqlParameterSource(parametres) {
				public boolean hasValue(String paramName) {
					return true;
				}
			};
			List<Persona> resultat = namedJdbcTemplate.query(
					query,
					parameterSource,
					new RowMapper() {
						public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
							String codi = rs.getString(1);
							String nom = rs.getString(2);
							String llinatges = rs.getString(3);
							String dni = rs.getString(4);
							String email = rs.getString(5);
							Persona persona = new Persona(codi,
													nom,
													llinatges,
													email,
													sexePerNom(nom));
							persona.setDni(dni);
							return persona;
						}
					});
			
			if (resultat.size() > 0) {
				return resultat;
			}
			
			return null;
		} catch (Exception ex) {
			throw new DominiException("No s'han pogut consultar les persones", ex);
		}
	}
	
	private Sexe sexePerNom(String nom) {
		String[] parts = nom.trim().split(" ");
		String norm = parts[0];
		norm = norm.replaceAll("[àâ]","a");
		norm = norm.replaceAll("[èéêë]","e");
		norm = norm.replaceAll("[ïî]","i");
		norm = norm.replaceAll("Ô","o");
		norm = norm.replaceAll("[ûù]","u");
		norm = norm.replaceAll("[ÀÂ]","A");
		norm = norm.replaceAll("[ÈÉÊË]","E");
		norm = norm.replaceAll("[ÏÎ]","I");
		norm = norm.replaceAll("Ô","O");
		norm = norm.replaceAll("[ÛÙ]","U");
		if (norm.toLowerCase().endsWith("a")) {
			return Sexe.SEXE_DONA;
		} else {
			return Sexe.SEXE_HOME;
		}
	}
}
