/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un valor d'una enumeració que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnumeracioValorsRepository extends JpaRepository<EnumeracioValors, Long> {

	List<EnumeracioValors> findByEnumeracioOrderByOrdreAsc(
			Enumeracio enumeracio);

	@Query("select e from EnumeracioValors e "
			+ "where e.enumeracio.id = :enumeracioId "
			+ "order by e.ordre, e.id")
	List<EnumeracioValors> findByEnumeracioOrdenat(@Param("enumeracioId") Long enumeracioId);

}
