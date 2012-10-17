/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class ExpedientDao extends HibernateGenericDao<Expedient, Long> {

	public ExpedientDao() {
		super(Expedient.class);
	}

	public List<Expedient> findAmbEntorn(Long entornId) {
		return findByCriteria(
				Restrictions.eq("entorn.id", entornId));
	}
	public Expedient findAmbEntornIId(Long entornId, Long id) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("id", id),
				Restrictions.eq("entorn.id", entornId));
		if (expedients.size() > 0)
			return expedients.get(0);
		return null;
	}
	public Expedient findAmbProcessInstanceId(String processInstanceId) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("processInstanceId", processInstanceId));
		if (expedients.size() > 0) {
			return expedients.get(0);
		} else {
			Expedient expedientIniciant = ExpedientIniciantDto.getExpedient();
			if (expedientIniciant != null && expedientIniciant.getProcessInstanceId().equals(processInstanceId))
				return expedientIniciant;
		}
		return null;
	}
	public int countAmbExpedientTipusId(Long expedientTipusId) {
		return getCountByCriteria(
				Restrictions.eq("tipus.id", expedientTipusId));
	}
	public Expedient findAmbEntornTipusITitol(
			Long entornId,
			Long expedientTipusId,
			String titol) {
		List<Expedient> expedients = findByCriteria(
				Restrictions.eq("entorn.id", entornId),
				Restrictions.eq("tipus.id", expedientTipusId),
				Restrictions.eq("titol", titol));
		if (expedients.size() > 0)
			return expedients.get(0);
		return null;
	}
	public Expedient findAmbEntornTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero) {
		Query query = getSession().createQuery(
				"from " +
				"    Expedient e " +
				"where " +
				"    e.entorn.id=? " +
				"and e.tipus.id=? " +
				"and e.numero=? ").
				setLong(0, entornId).
				setLong(1, expedientTipusId).
				setString(2, numero);
		return (Expedient)query.uniqueResult();
	}

	public int countAmbEntornConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats,
			String[] grupsUsuari) {
		return getCountByCriteria(getCriteriaForConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				expedientTipusIdPermesos,
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				grupsUsuari));
	}
	public List<Expedient> findAmbEntornConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats,
			String[] grupsUsuari) {
		return findByCriteria(getCriteriaForConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				expedientTipusIdPermesos,
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				grupsUsuari));
	}
	public List<Expedient> findAmbEntornConsultaGeneralPagedAndOrdered(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats,
			String[] grupsUsuari,
			int firstRow,
			int maxResults,
			String sort,
			boolean asc) {
		String sorts[] = null;
		if ("identificador".equals(sort)) {
			sorts = new String[] {
					"numero",
					"titol"};
		} else if (sort == null) {
			sorts = new String[] {"id"};
		} else {
			sorts = new String[] {sort};
		}
		return findPagedAndOrderedByCriteria(
				firstRow,
				maxResults,
				sorts,
				asc,
				getCriteriaForConsultaGeneral(
						entornId,
						titol,
						numero,
						dataInici1,
						dataInici2,
						expedientTipusId,
						expedientTipusIdPermesos,
						estatId,
						iniciat,
						finalitzat,
						geoPosX,
						geoPosY,
						geoReferencia,
						mostrarAnulats,
						grupsUsuari));
	}
	public List<Expedient> findAmbEntornLikeIdentificador(
			Long entornId,
			String text) {
		Criterion[] criteris = new Criterion[2];
		criteris[0] = Restrictions.ilike("titol", "%" + text + "%");
		criteris[1] = Restrictions.ilike("numero", "%" + text + "%");
		return findOrderedByCriteria(
				new String[] {"numero", "titol"},
				true,
				Restrictions.eq("entorn.id", entornId),
				Restrictions.or(
						Restrictions.ilike("titol", "%" + text + "%"),
						Restrictions.ilike("numero", "%" + text + "%")));
	}



	private Criteria getCriteriaForConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long[] expedientTipusIdPermesos,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats,
			String[] grupsUsuari) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		crit.createAlias("tipus", "tip");
		crit.add(Restrictions.eq("entorn.id", entornId));
		if (titol != null && titol.length() > 0)
			crit.add(Restrictions.ilike("titol", "%" + titol + "%"));
		if (numero != null && numero.length() > 0)
			crit.add(Restrictions.eq("numero", numero));
		if (dataInici1 != null && dataInici2 != null)
			crit.add(Restrictions.between("dataInici", dataInici1, dataInici2));
		if (expedientTipusId != null)
			crit.add(Restrictions.eq("tipus.id", expedientTipusId));
		if (expedientTipusIdPermesos != null && expedientTipusIdPermesos.length > 0)
			crit.add(Restrictions.in("tipus.id", expedientTipusIdPermesos));
		if (estatId != null && !finalitzat)
			crit.add(Restrictions.eq("estat.id", estatId));
		if (iniciat && !finalitzat) {
			crit.add(Restrictions.isNull("estat.id"));
			crit.add(Restrictions.isNull("dataFi"));
		} else if (finalitzat && !iniciat) {
			crit.add(Restrictions.isNotNull("dataFi"));
		} else if (iniciat && finalitzat) {
			crit.add(Restrictions.isNull("dataInici"));
		}
		if (geoPosX != null)
			crit.add(Restrictions.eq("geoPosX", geoPosX));
		if (geoPosY != null)
			crit.add(Restrictions.eq("geoPosY", geoPosY));
		if (geoReferencia != null && geoReferencia.length() > 0)
			crit.add(Restrictions.ilike("geoReferencia", "%" + geoReferencia + "%"));
		if (!mostrarAnulats) {
			crit.add(Restrictions.eq("anulat", false));
		}
		if (grupsUsuari != null && grupsUsuari.length > 0) {
			crit.add(Restrictions.or(
					Restrictions.eq("tip.restringirPerGrup", false),
					Restrictions.in("grupCodi", grupsUsuari)));
		} else {
			crit.add(Restrictions.eq("tip.restringirPerGrup", false));
		}
		return crit;
	}

}
