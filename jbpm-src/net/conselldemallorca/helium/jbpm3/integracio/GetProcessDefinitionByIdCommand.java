/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;

/**
 * Command per obtenir la definició de procés de jBPM 3 donat el seu id
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class GetProcessDefinitionByIdCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetProcessDefinitionByIdCommand() {}

	public GetProcessDefinitionByIdCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		return retrieveProcessDefinition(jbpmContext.getGraphSession().getProcessDefinition(id));
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public GetProcessDefinitionByIdCommand id(long id) {
		setId(id);
	    return this;
	}

}
