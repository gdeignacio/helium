/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.List;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Persona;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus entorn
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class EntornDao extends HibernateGenericDao<Entorn, Long> {

	public EntornDao() {
		super(Entorn.class);
	}

	public Entorn findAmbCodi(String codi) {
		List<Entorn> entorns = findByCriteria(
				Restrictions.eq("codi", codi));
		if (entorns.size() > 0)
			return entorns.get(0);
		return null;
	}

	public List<Entorn> findActius() {
		return findByCriteria(
				Restrictions.eq("actiu", true));
	}

	@SuppressWarnings("unchecked")
	public List<Persona> findMembresEntorn(Long entornId) {
		return (List<Persona>)getSession().
				createQuery(
						"select " +
						"    m " +
						"from " +
						"    Entorn e join e.membres m " +
						"where " +
						"    e.id=? " +
						"order by " +
						"    m.nomSencer").
				setLong(0, entornId).
				list();
	}

}
