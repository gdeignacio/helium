/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pipella d'accions del tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusAccioController extends BaseExpedientTipusController {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{expedientTipusId}/accions")
	public String accions(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"accions");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
		}
		return "v3/expedientTipusAccio";
	}
	
	@RequestMapping(value="/{expedientTipusId}/accio/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(required = false) 
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				expedientTipusService.accioFindPerDatatable(
						expedientTipusId,
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
			
	@RequestMapping(value = "/{expedientTipusId}/accio/new", method = RequestMethod.GET)
	public String nova(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		ExpedientTipusAccioCommand command = new ExpedientTipusAccioCommand();
		command.setExpedientTipusId(expedientTipusId);
		model.addAttribute("expedientTipusAccioCommand", command);
		return "v3/expedientTipusAccioForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/accio/new", method = RequestMethod.POST)
	public String novaPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusAccioCommand.Creacio.class) ExpedientTipusAccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusAccioForm";
        } else {
        	// Verificar permisos
    		expedientTipusService.accioCreate(
    				expedientTipusId,
        			ExpedientTipusAccioCommand.asAccioDto(command));    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.accio.controller.creat"));
			return modalUrlTancar(false);			
        }
	}

	@RequestMapping(value = "/{expedientTipusId}/accio/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		AccioDto dto = expedientTipusService.accioFindAmbId(id);
		ExpedientTipusAccioCommand command = conversioTipusHelper.convertir(
				dto,
				ExpedientTipusAccioCommand.class);
		model.addAttribute("expedientTipusAccioCommand", command);
		return "v3/expedientTipusAccioForm";
	}
	@RequestMapping(value = "/{expedientTipusId}/accio/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			@Validated(ExpedientTipusAccioCommand.Modificacio.class) ExpedientTipusAccioCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/expedientTipusAccioForm";
        } else {
        	expedientTipusService.accioUpdate(
        			ExpedientTipusAccioCommand.asAccioDto(command));
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.tipus.accio.controller.modificat"));
			return modalUrlTancar(false);
        }
	}
	
	@RequestMapping(value = "/{expedientTipusId}/accio/{id}/delete", method = RequestMethod.GET)
	@ResponseBody
	public boolean delete(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long id,
			Model model) {
		
		try {
			expedientTipusService.accioDelete(id);
			
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"expedient.tipus.accio.llistat.accio.esborrar.correcte"));
			return true;
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"expedient.tipus.accio.llistat.accio.esborrar.error"));
			logger.error("S'ha produit un error al intentar eliminar la accio amb id '" + id + "' del tipus d'expedient amb id '" + expedientTipusId, e);
			return false;
		}
	}
		
	private static final Log logger = LogFactory.getLog(ExpedientTipusAccioController.class);
}