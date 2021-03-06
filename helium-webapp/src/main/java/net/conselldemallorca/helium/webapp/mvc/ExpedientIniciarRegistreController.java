/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió dels formularis dels camps de tipus registre
 * a dins el formulari d'inici d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientIniciarRegistreController extends CommonRegistreController {

	private ExpedientService expedientService;



	@Autowired
	public ExpedientIniciarRegistreController(
			ExpedientService expedientService,
			DissenyService dissenyService) {
		super(dissenyService);
		this.expedientService = expedientService;
	}

	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientTipusId) {
		ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		return expedientTipus;
	}

	@ModelAttribute("definicioProces")
	public DefinicioProces populateDefinicioProces(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId) {
		if (definicioProcesId != null) {
			return dissenyService.getById(definicioProcesId, false);
		} else {
			return dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId, false);
		}
	}

	@Override
	public void populateOthers(
			HttpServletRequest request,
			String id,
			Object command,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Long expedientTipusId = new Long(id);
			model.addAttribute(
					"expedientTipus",
					dissenyService.getExpedientTipusById(expedientTipusId));
			model.addAttribute(
					"tasca",
					expedientService.getStartTask(
							entorn.getId(),
							expedientTipusId,
							null,
							null));
		}
	}

	@RequestMapping(value = "/expedient/iniciarRegistre", method = RequestMethod.GET)
	public String registreGet(HttpServletRequest request) {
		return super.registreGet(request);
	}
	@RequestMapping(value = "/expedient/iniciarRegistre", method = RequestMethod.POST)
	public String registrePost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = false) Integer index,
			@RequestParam(value = "submit", required = true) String submit,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		return super.registrePost(
				request,
				id,
				registreId,
				index,
				submit, 
				command,
				result,
				status,
				model);
	}

	@Override
	public void esborrarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			int index) {
		esborrarRegistre(request, campCodi, multiple, index);
	}
	@Override
	public Object[] getValorRegistre(
			HttpServletRequest request,
			Long entornId,
			String id,
			String campCodi) {
		return (Object[])request.getSession().getAttribute(
				ExpedientIniciarController.getClauSessioCampRegistre(campCodi));
	}
	@Override
	public void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors,
			int index) {
		guardarRegistre(request, campCodi, multiple, valors, index);
	}
	@Override
	public void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors) {
		guardarRegistre(request, campCodi, multiple, valors, -1);
	}
	@Override
	public String redirectUrl(String id, String campCodi) {
		return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + id;
	}
	@Override
	public String registreUrl() {
		return "expedient/iniciarRegistre";
	}



	private void guardarRegistre(
			HttpServletRequest request,
			String campCodi,
			boolean multiple,
			Object[] valors,
			int index) {
		if (multiple) {
			Object valor = request.getSession().getAttribute(
					ExpedientIniciarController.getClauSessioCampRegistre(campCodi));
			if (valor == null) {
				request.getSession().setAttribute(
						ExpedientIniciarController.getClauSessioCampRegistre(campCodi),
						new Object[]{valors});
			} else {
				Object[] valorMultiple = (Object[])valor;
				if (index != -1) {
					valorMultiple[index] = valors;
					request.getSession().setAttribute(
							ExpedientIniciarController.getClauSessioCampRegistre(campCodi),
							valor);
				} else {
					Object[] valorNou = new Object[valorMultiple.length + 1];
					for (int i = 0; i < valorMultiple.length; i++)
						valorNou[i] = valorMultiple[i];
					valorNou[valorMultiple.length] = valors;
					request.getSession().setAttribute(
							ExpedientIniciarController.getClauSessioCampRegistre(campCodi),
							valorNou);
				}
			}
		} else {
			request.getSession().setAttribute(
					ExpedientIniciarController.getClauSessioCampRegistre(campCodi),
					valors);
		}
	}
	public void esborrarRegistre(
			HttpServletRequest request,
			String campCodi,
			boolean multiple,
			int index) {
		if (multiple) {
			Object valor = request.getSession().getAttribute(
					ExpedientIniciarController.getClauSessioCampRegistre(campCodi));
			if (valor != null) {
				Object[] valorMultiple = (Object[])valor;
				if (valorMultiple.length > 0) {
					Object[] valorNou = new Object[valorMultiple.length - 1];
					for (int i = 0; i < valorNou.length; i++)
						valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
						request.getSession().setAttribute(
								ExpedientIniciarController.getClauSessioCampRegistre(campCodi),
								valorNou);
				}
			}
		} else {
			request.getSession().removeAttribute(
					ExpedientIniciarController.getClauSessioCampRegistre(campCodi));
		}
	}

}
