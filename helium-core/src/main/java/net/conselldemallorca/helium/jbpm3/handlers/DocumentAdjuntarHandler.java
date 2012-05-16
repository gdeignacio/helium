/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.core.model.dto.ArxiuDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a adjuntar un document al procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DocumentAdjuntarHandler extends AbstractHeliumActionHandler implements DocumentAdjuntarHandlerInterface {

	private String documentOrigen;
	private String varDocumentOrigen;
	private String titol;
	private String varTitol;
	private String data;
	private String varData;
	private String concatenarTitol;
	private String esborrarDocument;



	public void execute(ExecutionContext executionContext) throws Exception {
		String dor = (String)getValorOVariable(executionContext, documentOrigen, varDocumentOrigen);
		if (dor == null)
			throw new JbpmException("No s'ha especificat cap document origen");
		String varCodi = DocumentHelper.PREFIX_VAR_DOCUMENT + dor;
		Object valor = executionContext.getVariable(varCodi);
		if (valor != null && valor instanceof Long) {
			Long id = (Long)valor;
			DocumentStore docStore = getDocumentStoreDao().getById(id, false);
			if (docStore != null) {
				String processInstanceId = new Long(executionContext.getProcessInstance().getId()).toString();
				String tit = (String)getValorOVariable(executionContext, titol, varTitol);
				String adjuntTitol;
				DocumentDto document = getDocumentService().documentInfo(docStore.getId());
				if (isConcatenarTitol()) {
					adjuntTitol = document.getDocumentNom() + " " + tit;
				} else {
					adjuntTitol = tit;
				}
				Date adjuntData = getValorOVariableData(executionContext, data, varData);
				ArxiuDto arxiu = getDocumentService().arxiuDocumentPerMostrar(docStore.getId());
				getDocumentService().guardarAdjunt(
						processInstanceId,
						null,
						adjuntTitol,
						(adjuntData != null) ? adjuntData : docStore.getDataDocument(),
						arxiu.getNom(),
						arxiu.getContingut());
				if (isEsborrarDocument()) {
					getDocumentService().esborrarDocument(
							null,
							processInstanceId,
							document.getDocumentCodi());
				}
			} else {
				throw new JbpmException("No s'ha trobat el contingut del document especificat(" + dor + ")");
			}
		}
	}



	public void setDocumentOrigen(String documentOrigen) {
		this.documentOrigen = documentOrigen;
	}
	public void setVarDocumentOrigen(String varDocumentOrigen) {
		this.varDocumentOrigen = varDocumentOrigen;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setConcatenarTitol(String concatenarTitol) {
		this.concatenarTitol = concatenarTitol;
	}
	public void setEsborrarDocument(String esborrarDocument) {
		this.esborrarDocument = esborrarDocument;
	}



	private boolean isConcatenarTitol() {
		return "true".equalsIgnoreCase(concatenarTitol) || "si".equalsIgnoreCase(concatenarTitol);
	}
	private boolean isEsborrarDocument() {
		return "true".equalsIgnoreCase(esborrarDocument) || "si".equalsIgnoreCase(esborrarDocument);
	}

}
