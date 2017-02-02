/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTerminiService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTerminiModificarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTerminiModificarCommand.TerminiModificacioTipus;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;

/**
 * Controlador per a la pàgina d'informació de l'termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTerminiV3Controller extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientTerminiService expedientTerminiService;



	@RequestMapping(value = "/{expedientId}/termini", method = RequestMethod.GET)
	public String terminis(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {		
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<TerminiDto>> terminis = new LinkedHashMap<InstanciaProcesDto, List<TerminiDto>>();
		Map<String, List<TerminiIniciatDto>> iniciats = new LinkedHashMap<String, List<TerminiIniciatDto>>();
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			List<TerminiDto> terminisInstanciaProces = null;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				terminisInstanciaProces = expedientTerminiService.findAmbProcessInstanceId(
						expedientId,
						instanciaProces.getId());
				iniciats.put(
						instanciaProces.getId(),
						expedientTerminiService.iniciatFindAmbProcessInstanceId(
								expedientId,
								instanciaProces.getId()));
			}
			terminis.put(instanciaProces, terminisInstanciaProces);
		}
		model.addAttribute("expedient", expedient);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("terminis", terminis);
		model.addAttribute("iniciats", iniciats);
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"terminis");
		}
		return "v3/expedientTermini";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/termini", method = RequestMethod.GET)
	public String dadesProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		model.addAttribute("expedient", expedient);
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);		
		Map<InstanciaProcesDto, List<TerminiDto>> terminis = new LinkedHashMap<InstanciaProcesDto, List<TerminiDto>>();
		Map<String, List<TerminiIniciatDto>> iniciats = new LinkedHashMap<String, List<TerminiIniciatDto>>();
		terminis.put(
				instanciaProces,
				expedientTerminiService.findAmbProcessInstanceId(
						expedientId,
						instanciaProces.getId()));
		iniciats.put(
				instanciaProces.getId(),
				expedientTerminiService.iniciatFindAmbProcessInstanceId(
						expedientId,
						instanciaProces.getId()));
		model.addAttribute("inicialProcesInstanceId", procesId);
		model.addAttribute("terminis",terminis);
		model.addAttribute("iniciats",iniciats);
		return "v3/procesTerminis";
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/termini/{terminiId}/iniciar", method = RequestMethod.GET)
	@ResponseBody
	public boolean iniciar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable Long terminiId,
			Model model) {
		boolean response = false; 
		try {			
			expedientTerminiService.iniciar(
					expedientId,
					procesId,
					terminiId,
					new Date(),
					false);
			MissatgesHelper.success(request, getMessage(request, "info.termini.iniciat"));
			response = true;
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.iniciar.termini"));
        	logger.error("No s'ha pogut iniciar el termini", ex);
		}
		return response;
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/termini/{terminiIniciatId}/suspendre", method = RequestMethod.GET)
	@ResponseBody
	public boolean suspendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable Long terminiIniciatId,
			Model model) {
		boolean response = false; 
		try {
			expedientTerminiService.suspendre(
					expedientId,
					procesId,
					terminiIniciatId,
					new Date());
			MissatgesHelper.success(request, getMessage(request, "info.termini.aturat"));
			response = true;
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.aturar.termini"));
			logger.error("No s'ha pogut aturar el termini", ex);
		}
		return response;
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/termini/{terminiIniciatId}/reprendre", method = RequestMethod.GET)
	@ResponseBody
	public boolean reprendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable Long terminiIniciatId,
			Model model) {
		boolean response = false; 
		try {
			expedientTerminiService.reprendre(
					expedientId,
					procesId,
					terminiIniciatId,
					new Date());
			MissatgesHelper.success(request, getMessage(request, "info.termini.continuat"));
			response = true;
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.continuat.termini"));
			logger.error("No s'ha pogut continuat el termini", ex);
		}
		return response;
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/termini/{terminiIniciatId}/cancelar", method = RequestMethod.GET)
	@ResponseBody
	public boolean cancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable Long terminiIniciatId,
			Model model) {
		boolean response = false; 
		try {
			expedientTerminiService.cancelar(
					expedientId,
					procesId,
					terminiIniciatId,
					new Date());
			MissatgesHelper.success(request, getMessage(request, "info.termini.cancelat"));
			response = true;
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.cancelat.termini"));
			logger.error("No s'ha pogut cancel·lar el termini", ex);
		}
		return response;
	}

	@RequestMapping(value = "/{expedientId}/proces/{procesId}/termini/{terminiIniciatId}/modificar", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable Long terminiIniciatId,
			Model model) {
		TerminiIniciatDto terminiIniciat = expedientTerminiService.iniciatFindAmbId(
				expedientId,
				procesId,
				terminiIniciatId);
		ExpedientTerminiModificarCommand expedientTerminiModificarCommand = new ExpedientTerminiModificarCommand();
		expedientTerminiModificarCommand.setTerminiId(terminiIniciatId);
		expedientTerminiModificarCommand.setNom(terminiIniciat.getTermini().getNom());
		expedientTerminiModificarCommand.setAnys(terminiIniciat.getAnys());
		expedientTerminiModificarCommand.setMesos(terminiIniciat.getMesos());
		expedientTerminiModificarCommand.setDies(terminiIniciat.getDies());
		expedientTerminiModificarCommand.setDataInici(terminiIniciat.getDataInici());
		expedientTerminiModificarCommand.setDataFi(terminiIniciat.getDataFi());
		model.addAttribute(expedientTerminiModificarCommand);
		model.addAttribute("listTerminis", valors12());
		model.addAttribute("listTipus", getTipus(request));
		return "v3/expedient/terminiModificar";
	}
	@RequestMapping(value = "/{expedientId}/proces/{procesId}/termini/{terminiIniciatId}/modificar", method = RequestMethod.POST)
	public String terminiModificar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			@PathVariable Long terminiIniciatId,
			@Valid @ModelAttribute ExpedientTerminiModificarCommand expedientTerminiModificarCommand,
			BindingResult result,
			SessionStatus status,
			Model model) {
		try {
			Date inicio = null;
			if (TerminiModificacioTipus.DURADA.name().equals(expedientTerminiModificarCommand.getTipus())) {
				TerminiIniciatDto terminiIniciat = expedientTerminiService.iniciatFindAmbId(
						expedientId,
						procesId,
						terminiIniciatId);
				inicio = terminiIniciat.getDataInici();
			} else if (TerminiModificacioTipus.DATA_INICI.name().equals(expedientTerminiModificarCommand.getTipus())) {
				inicio = expedientTerminiModificarCommand.getDataInici();
			} else {
				inicio = expedientTerminiModificarCommand.getDataFi();
			}
			expedientTerminiService.modificar(
					expedientId,
					procesId,
					terminiIniciatId,
					inicio,
					expedientTerminiModificarCommand.getAnys(),
					expedientTerminiModificarCommand.getMesos(),
					expedientTerminiModificarCommand.getDies(),
					TerminiModificacioTipus.DATA_FI.name().equals(expedientTerminiModificarCommand.getTipus()));
			MissatgesHelper.success(request, getMessage(request, "info.termini.modificat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.modificar.termini"));
        	logger.error("No s'ha pogut modificar el termini", ex);
		}
		return modalUrlTancar(false);
	}	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	public List<ParellaCodiValorDto> getTipus(HttpServletRequest request) {
		List<ParellaCodiValorDto> tipus = new ArrayList<ParellaCodiValorDto>();
		tipus.add(new ParellaCodiValorDto(getMessage(request, "termini.durada"),TerminiModificacioTipus.DURADA));
		tipus.add(new ParellaCodiValorDto(getMessage(request, "termini.data_fi"),TerminiModificacioTipus.DATA_FI));
		tipus.add(new ParellaCodiValorDto(getMessage(request, "termini.data_ini"),TerminiModificacioTipus.DATA_INICI));
		return tipus;
	}

	private List<ParellaCodiValorDto> valors12() {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValorDto(String.valueOf(i), i));
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTerminiV3Controller.class);

}
