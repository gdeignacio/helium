/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Estat;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar l'estat d'un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientEstatModificarHandler extends AbstractHeliumActionHandler {

	private String estatCodi;
	private String varEstatCodi;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciant.getExpedient();
		String ec = (String)getValorOVariable(executionContext, estatCodi, varEstatCodi);
		if (ex != null) {
			Estat estat = getEstatPerExpedientTipusICodi(
					ex.getTipus().getId(),
					ec);
			if (estat != null) {
				ex.setEstat(estat);
			} else {
				throw new JbpmException("No existeix cap estat amb el codi '" + estatCodi + "'");
			}
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			if (expedient != null) {
				Estat estat = getEstatPerExpedientTipusICodi(
						expedient.getTipus().getId(),
						ec);
				if (estat != null) {
					getExpedientService().editar(
							expedient.getEntorn().getId(),
							expedient.getId(),
							expedient.getNumero(),
							expedient.getTitol(),
							expedient.getIniciadorCodi(),
							expedient.getResponsableCodi(),
							expedient.getDataInici(),
							expedient.getComentari(),
							estat.getId());
				} else {
					throw new JbpmException("No existeix cap estat amb el codi '" + ec + "'");
				}
			} else {
				throw new JbpmException("No s'ha trobat l'expedient per canviar l'estat");
			}
		}
	}

	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public void setVarEstatCodi(String varEstatCodi) {
		this.varEstatCodi = varEstatCodi;
	}

}
