/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.model.dao.PluginRegistreDao;
import net.conselldemallorca.helium.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.model.dao.SistraDao;
import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Estat;
import net.conselldemallorca.helium.model.hibernate.Termini;
import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.model.service.AlertaService;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.PluginService;
import net.conselldemallorca.helium.model.service.ServiceProxy;
import net.conselldemallorca.helium.model.service.TerminiService;

import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler base amb accés a la funcionalitat de Helium
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
abstract class AbstractHeliumActionHandler implements ActionHandler {

	public abstract void execute(ExecutionContext executionContext) throws Exception;



	ExpedientDto getExpedient(ExecutionContext executionContext) {
		return getExpedientService().findExpedientAmbProcessInstanceId(
				getProcessInstanceId(executionContext));
	}
	DefinicioProcesDto getDefinicioProces(ExecutionContext executionContext) {
		return getDissenyService().findDefinicioProcesAmbProcessInstanceId(
				getProcessInstanceId(executionContext));
	}

	Estat getEstatPerExpedientTipusICodi(Long expedientTipusId, String codi) {
		return ServiceProxy.getInstance().getDissenyService().findEstatAmbExpedientTipusICodi(
				expedientTipusId,
				codi);
	}
	Persona getPersonaAmbCodi(String codi) {
		return ServiceProxy.getInstance().getPluginService().findPersonaAmbCodi(codi);
	}
	Termini getTerminiAmbCodi(ExecutionContext executionContext, String codi) {
		return getDissenyService().findTerminiAmbDefinicioProcesICodi(
				getDefinicioProces(executionContext).getId(),
				codi);
	}
	TerminiIniciat getTerminiIniciatAmbCodi(ExecutionContext executionContext, String codi) {
		Termini termini = getTerminiAmbCodi(executionContext, codi);
		if (termini != null)
			return getTerminiService().findIniciatAmbTerminiIdIProcessInstanceId(
					termini.getId(),
					getProcessInstanceId(executionContext));
		return null;
	}

	ExpedientService getExpedientService() {
		return ServiceProxy.getInstance().getExpedientService();
	}
	PluginService getPluginService() {
		return ServiceProxy.getInstance().getPluginService();
	}
	DissenyService getDissenyService() {
		return ServiceProxy.getInstance().getDissenyService();
	}
	TerminiService getTerminiService() {
		return ServiceProxy.getInstance().getTerminiService();
	}
	AlertaService getAlertaService() {
		return ServiceProxy.getInstance().getAlertaService();
	}
	SistraDao getSistraService() {
		return DaoProxy.getInstance().getSistraDao();
	}
	PluginRegistreDao getPluginRegistreDao() {
		return DaoProxy.getInstance().getPluginRegistreDao();
	}
	PluginTramitacioDao getPluginTramitacioDao() {
		return DaoProxy.getInstance().getPluginTramitacioDao();
	}
	DocumentStoreDao getDocumentStoreDao() {
		return DaoProxy.getInstance().getDocumentStoreDao();
	}

	protected String getProcessInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getProcessInstance().getId()).toString();
	}
	protected Date getVariableComData(ExecutionContext executionContext, String var) {
		Object obj = executionContext.getVariable(var);
		if (obj instanceof Date)
			return (Date)obj;
		throw new JbpmException("La variable amb el codi '"+ var + "' no és de tipus Date");
	}
	protected Object getValorOVariable(ExecutionContext executionContext, Object value, String var) {
		if (value != null)
			return value;
		if (var != null)
			return executionContext.getVariable(var);
		return null;
	}
	protected Date getValorOVariableData(ExecutionContext executionContext, Object value, String var) {
		if (value != null) {
			if (value instanceof Date) {
				return (Date)value;
			} else {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					return sdf.parse(value.toString());
				} catch (Exception ignored) {}
			}
		}
		if (var != null) {
			Object valor = executionContext.getVariable(var);
			if (valor instanceof Date) {
				return (Date)valor;
			} else {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					return sdf.parse(valor.toString());
				} catch (Exception ignored) {}
			}
		}
		return null;
	}
	protected Integer getValorOVariableInteger(ExecutionContext executionContext, Object value, String var) {
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer)value;
			} else {
				return new Integer(value.toString());
			}
		}
		if (var != null) {
			Object valor = executionContext.getVariable(var);
			if (valor instanceof Integer) {
				return (Integer)valor;
			} else {
				return new Integer(valor.toString());
			}
		}
		return null;
	}

}
