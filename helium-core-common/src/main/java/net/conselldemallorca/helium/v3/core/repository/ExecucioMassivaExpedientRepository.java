/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;

/**
 * Dao pels objectes del tipus ExecucioMassivaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaExpedientRepository extends JpaRepository<ExecucioMassivaExpedient, Long> {

	@Query("select e " +
			"from	ExecucioMassivaExpedient e " +
			"where 	e.expedient.id = :expedientId ")
	public List<ExecucioMassivaExpedient> getExecucioMassivaByExpedient(
			@Param("expedientId") Long expedientId
	);
	
	@Query("select count(e) " +
			"from	ExecucioMassivaExpedient e " +
			"where 	e.expedient.id = :execucioMassivaId " +
			" and e.dataFi is null")
	public Long findProgresExecucioMassivaActiveById(
			@Param("execucioMassivaId") Long execucioMassivaId);

	@Query("select e.execucioMassiva.id, " +
			"sum(case when e.error is not null then 1 else 0 end) as error, " +
			"sum(case when e.error is null and e.dataFi is null and e.estat is 'ESTAT_PENDENT' then 1 else 0 end) as pendent, " +
			"count(*) as total " +
			"from ExecucioMassivaExpedient e " +
			"where e.execucioMassiva in (:execucionsMassives) " +
			"group by e.execucioMassiva.id, " +
			"e.execucioMassiva.dataInici " +
			"order by e.execucioMassiva.dataInici DESC")
	public List<Object[]> findResultatsExecucionsMassives(
			@Param("execucionsMassives") List<ExecucioMassiva> execucionsMassives);

}