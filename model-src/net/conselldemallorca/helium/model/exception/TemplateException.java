/**
 * 
 */
package net.conselldemallorca.helium.model.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Excepció que indica que han sorgit errors durant la generació
 * d'un document amb una plantilla
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class TemplateException extends NestedRuntimeException {

	private static final long serialVersionUID = 1L;

	public TemplateException(String msg) {
        super(msg);
    }

    public TemplateException(String msg, Throwable t) {
        super(msg, t);
    }

}
