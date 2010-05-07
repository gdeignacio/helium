/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per a controlar si un usuari té permisos per
 * accedir al menu de disseny per als tipus d'expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PermisosDissenyInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_SESSION_PERMISOS_DISSENY = "potDissenyarExpedientTipus";

	private DissenyService dissenyService;
	private PermissionService permissionService;



	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getSession().getAttribute(VARIABLE_SESSION_PERMISOS_DISSENY) == null) {
			Entorn entorn = getEntornActiu(request);
			boolean permisosDisseny = false;
			if (entorn != null) {
				List<ExpedientTipus> llistat = dissenyService.findExpedientTipusAmbEntorn(entorn.getId());
				for (ExpedientTipus expedientTipus: llistat) {
					if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
						permisosDisseny = true;
						break;
					}
				}			
			}
			request.getSession().setAttribute(VARIABLE_SESSION_PERMISOS_DISSENY, new Boolean(permisosDisseny));
		}
		return true;
	}



	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}



	private Entorn getEntornActiu(HttpServletRequest request) {
		return (Entorn)request.getSession().getAttribute(EntornInterceptor.VARIABLE_SESSIO_ENTORN_ACTUAL);
	}
	private boolean potDissenyarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

}
