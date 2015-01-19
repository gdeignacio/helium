/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTascaController extends BaseExpedientController {

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected TascaService tascaService;

	@RequestMapping(value = "/{expedientId}/tasca", method = RequestMethod.GET)
	public String tasques(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		
		Map<InstanciaProcesDto, List<ExpedientTascaDto>> tasques = new LinkedHashMap<InstanciaProcesDto, List<ExpedientTascaDto>>();
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			tasques.put(instanciaProces, expedientService.findTasquesPerInstanciaProces(expedientId, instanciaProces.getId()));
		}
		
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("expedient", expedient);
		model.addAttribute("tasques", tasques);
		return "v3/expedientTasca";
	}

	@RequestMapping(value = "/{expedientId}/tasquesPendents", method = RequestMethod.GET)
	public String tasquesPendents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("tasques", expedientService.findTasquesPendents(expedientId));
		return "v3/expedientTasquesPendents";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/cancelar")
	public String tascaCancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		try {
			expedientService.cancelarTasca(expedientId, tascaId);
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.cancelar.tasca", new Object[] {String.valueOf(tascaId)} ));
        	logger.error("No s'ha pogut cancel·lar la tasca " + String.valueOf(tascaId), ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/suspendre")
	public String tascaSuspendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {		
		try {
			expedientService.suspendreTasca(expedientId, tascaId);
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.suspendre.tasca", new Object[] {tascaId} ));
        	logger.error("No s'ha pogut suspendre la tasca " + tascaId, ex);
		}
			
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reprendre")
	public String tascaReprendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		try {
			expedientService.reprendreTasca(expedientId, tascaId);
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.reprendre.tasca", new Object[] {tascaId} ));
        	logger.error("No s'ha pogut reprendre la tasca " + tascaId, ex);
		}
		
		return "redirect:/v3/expedient/"+expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/delegacioCancelar", method = RequestMethod.POST)
	public String cancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		tascaService.delegacioCancelar(tascaId);
		MissatgesHelper.info(request, getMessage(request, "info.delegacio.cancelat"));
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{tascaId}/tascaAgafar", method = RequestMethod.GET)
	public String agafar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {		
		tascaService.agafar(tascaId);
		MissatgesHelper.info(request, getMessage(request, "info.tasca.disponible.personals"));			
		return "redirect:/v3/expedient/" + expedientId + "tasca/" + tascaId;
	}

	@RequestMapping(value = "/{expedientId}/{tascaId}/tascaAlliberar", method = RequestMethod.GET)
	public String alliberar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		tascaService.alliberar(tascaId);
		MissatgesHelper.info(request, getMessage(request, "info.tasca.alliberada"));
		return "redirect:/v3/expedient/" + expedientId + "tasca/" + tascaId;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaController.class);
}
