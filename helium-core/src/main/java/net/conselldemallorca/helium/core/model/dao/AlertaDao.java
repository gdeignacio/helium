/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Alerta;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus Alerta
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class AlertaDao extends HibernateGenericDao<Alerta, Long> {

	public AlertaDao() {
		super(Alerta.class);
	}

	public List<Alerta> findActivesAmbEntornIUsuari(Long entornId, String usuariCodi) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		crit.createAlias("expedient", "exp");
		crit.add(Restrictions.eq("entorn.id", entornId));
		crit.add(Restrictions.eq("destinatari", usuariCodi));
		crit.add(Restrictions.isNull("dataEliminacio"));
		crit.add(Restrictions.isNull("exp.dataFi"));
		return findByCriteria(crit);
	}

	public List<Alerta> findActivesAmbEntornITipusExpedient(
			Long entornId,
			Long expedientTipusId) {
		Criteria crit = getSession().createCriteria(
				getPersistentClass());
		crit.createAlias("expedient.tipus", "tip");
		crit.createAlias("expedient", "exp");
		crit.add(Restrictions.eq("entorn.id", entornId));
		crit.add(Restrictions.eq("tip.id", expedientTipusId));
		crit.add(Restrictions.isNull("dataEliminacio"));
		crit.add(Restrictions.isNull("exp.dataFi"));
		return findByCriteria(crit);
	}

	public List<Alerta> findActivesAmbTerminiIniciatId(Long terminiIniciatId) {
		return findByCriteria(
				Restrictions.eq("terminiIniciat.id", terminiIniciatId),
				Restrictions.isNull("dataEliminacio"));
	}

}
