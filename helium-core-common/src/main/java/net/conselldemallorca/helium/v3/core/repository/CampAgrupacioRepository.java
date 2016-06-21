/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Dao pels objectes de tipus CampAgrupacio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface CampAgrupacioRepository extends JpaRepository<CampAgrupacio, Long> {
	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId " +
			"order by " +
			"    ordre")
	List<CampAgrupacio> findAmbDefinicioProcesOrdenats(
			@Param("definicioProcesId") Long definicioProcesId);

	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.expedientTipus.id=:expedientTipusId " +
			"order by " +
			"    ordre")
	List<CampAgrupacio> findAmbExpedientTipusOrdenats(
			@Param("expedientTipusId") Long expedientTipusId);

	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId " +
			"and ca.codi=:codi")
	CampAgrupacio findAmbDefinicioProcesICodi(@Param("definicioProcesId") Long definicioProcesId, @Param("codi") String codi);
	

	@Query("select " +
			"	 max(ca.ordre) " +
			"from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId ")
	int getNextOrder(@Param("definicioProcesId") Long definicioProcesId);

	@Query("select ca from " +
			"    CampAgrupacio ca " +
			"where " +
			"    ca.definicioProces.id=:definicioProcesId " +
			"and ca.ordre=:ordre")
	CampAgrupacio getAmbOrdre(@Param("definicioProcesId") Long definicioProcesId, 
			@Param("ordre") int ordre);

	/** Consulta el següent valor per a ordre de les agrupacions. */
	@Query(	"select coalesce( max( ca.ordre), 0) + 1 " +
			"from CampAgrupacio ca " +
			"where " +
			"    ca.expedientTipus.id = :expedientTipusId " )
	Integer getNextOrdre(@Param("expedientTipusId") Long expedientTipusId);

	/** Per trobar codis repetits. */
	CampAgrupacio findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);	
		
}
