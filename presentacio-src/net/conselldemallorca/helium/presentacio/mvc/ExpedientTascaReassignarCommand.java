/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command per a la reassignació de tasques
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientTascaReassignarCommand {

	private String taskId;
	private String expression;



	public ExpedientTascaReassignarCommand() {}

	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
