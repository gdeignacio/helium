package net.conselldemallorca.helium.integracio.plugins.portasignatures.service;

import javax.jws.WebService;

import net.conselldemallorca.helium.integracio.bantel.plugin.DefaultIniciExpedientPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.ArrayOfLogMessage;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.CallbackRequest;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.CallbackResponse;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.MCGDws;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.ServiceProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementació del servei per processar automàticament els canvis del portasignatures.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl.MCGDws")
public class PortasignaturesPluginImpl implements MCGDws {

	private static final int DOCUMENT_BLOQUEJAT = 0;
	private static final int DOCUMENT_PENDENT = 1;
	private static final int DOCUMENT_SIGNAT = 2;
	private static final int DOCUMENT_REBUTJAT = 3;
	
	public CallbackResponse callback(CallbackRequest callbackRequest) {
		
		ArrayOfLogMessage arrayOfLogMessage = new ArrayOfLogMessage();
		CallbackResponse callbackResponse = new CallbackResponse();
		
		Integer document = callbackRequest.getApplication().getDocument().getId();
		Integer estat = -1;
		Double resposta = -1D;
		try {
			estat = callbackRequest.getApplication().getDocument().getAttributes().getState();
			ExpedientService expedientService = ServiceProxy.getInstance().getExpedientService();
			switch (estat) {
				case DOCUMENT_BLOQUEJAT:
					resposta = 1D;
					logger.info("Document " + document + " bloquejat (" + resposta + ").");
					break;
				case DOCUMENT_PENDENT:
					resposta = 1D;
					logger.info("Document " + document + " pendent (" + resposta + ").");
					break;
				case DOCUMENT_SIGNAT:
					resposta = expedientService.processarDocumentSignat(
							document);
					logger.info("Document " + document + " signat (" + resposta + ").");
					break;
				case DOCUMENT_REBUTJAT:
					resposta = expedientService.processarDocumentRebutjat(
							document,
							callbackRequest.getApplication().getDocument().getSigner().getRejection().getDescription());
					logger.info("Document " + document + " rebutjat (" + resposta + ").");
					break;
				default:
					break;
			}
			
			callbackResponse.setLogMessages(arrayOfLogMessage);
	        callbackResponse.setVersion("1.0");
	        callbackResponse.setReturn(resposta);
		} catch (Exception e) {
			logger.error("Error obtenint l'estat del document.", e);

			callbackResponse.setLogMessages(arrayOfLogMessage);
			callbackResponse.setVersion("1.0");
			callbackResponse.setReturn((double) -1);
		}
		
		return callbackResponse;
	}

	private static final Log logger = LogFactory.getLog(DefaultIniciExpedientPlugin.class);
	
	
}
