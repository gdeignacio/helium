package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ObjectTypeEditorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Controlador per a la tramitació de taques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class TascaTramitacioController extends BaseController {

	protected String TAG_PARAM_REGEXP = "<!--helium:param-(.+?)-->";
	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";
	public static final String VARIABLE_COMMAND_TRAMITACIO = "variableCommandTramitacio";

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected TascaService tascaService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected ExecucioMassivaService execucioMassivaService;

	@RequestMapping(value = "/massivaTramitacioTasca", method = RequestMethod.GET)
	public String massivaTramitacio(
			HttpServletRequest request,
			@RequestParam(value = "inici", required = false) String inici,
			@RequestParam(value = "correu", required = false) Boolean correu,
			Model model) throws IOException, ServletException {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(request, getMessage(request, "error.no.tasc.selec"));
			return "redirect:/v3/tasca";
		}
		String tascaId = String.valueOf(seleccio.iterator().next());
		SessionHelper.removeAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId);
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		getModelTramitacio(tascaId, model);
		model.addAttribute("command", populateCommand(request, tascaId, inici, correu, seleccio.size(), model));
		
		return getReturnUrl(request, tasca.getExpedientId(), tascaId);
	}

	@RequestMapping(value = "/massivaTramitacioTasca/taula", method = RequestMethod.GET)
	public String massivaTramitacioTaula(
			HttpServletRequest request,
			Model model) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();		
		model.addAttribute("tasques", tascaService.findDadesPerIds(seleccio));
		
		return "v3/import/tasquesMassivaTaula";
	}

	@SuppressWarnings("rawtypes")
	@ModelAttribute("command")
	public Object initializeCommand(
			HttpServletRequest request, 
			String tascaId, 
			String inici, 
			Boolean correu, 
			Integer numTascaMassiva,
			Model model) {		
		if (tascaId != null && !tascaId.isEmpty()) {
			try {
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionals.put("inici", inici);
				campsAddicionals.put("correu", correu);
				campsAddicionals.put("numTascaMassiva", numTascaMassiva);
				campsAddicionalsClasses.put("inici", String.class);
				campsAddicionalsClasses.put("correu", Boolean.class);
				campsAddicionalsClasses.put("numTascaMassiva", Integer.class);
				return TascaFormHelper.getCommandBuitForCamps(
						tascaService.findDades(tascaId),	// tascaDadas
						campsAddicionals,					// campsAddicionals
						campsAddicionalsClasses, 			// campsAddicionalsClasses
						false);								// perFiltre
			} catch (TascaNotFoundException ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut trobar la tasca: " + ex.getMessage(), ex);
			} catch (Exception ignored) {} 
		}
		return null;
	}

	@RequestMapping(value = "/formExtern", method = RequestMethod.GET)
	public String formExtern(
			HttpServletRequest request,
			@RequestParam(value = "width", required = false) final String width,
			@RequestParam(value = "height", required = false) final String height,
			@RequestParam(value = "url", required = false) final String url,
			ModelMap model) throws ServletException {
		try {
			model.addAttribute("tittle", getMessage(request, "tasca.form.dades_form"));
			model.addAttribute("url", url);
			model.addAttribute("height", height);
			return "v3/utils/modalIframe";
		} catch(Exception ex) {
			logger.error("Error al comprobar el formulario externo", ex);
			throw new ServletException(ex);
	    }
	}

	private Object populateCommand(HttpServletRequest request, String tascaId, String inici, boolean correu, int numTascaMassiva, Model model) {
		Object filtreCommand = SessionHelper.getAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId);
		if (filtreCommand != null) {
			return SessionHelper.getAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId);
		} else if (tascaId != null && !tascaId.isEmpty()) {
			try {				
				Map<String, Object> campsAddicionals = new HashMap<String, Object>();
				@SuppressWarnings("rawtypes")
				Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
				campsAddicionals.put("inici", inici);
				campsAddicionals.put("correu", correu);
				campsAddicionals.put("numTascaMassiva", numTascaMassiva);
				campsAddicionalsClasses.put("inici", String.class);
				campsAddicionalsClasses.put("correu", Boolean.class);
				campsAddicionalsClasses.put("numTascaMassiva", Integer.class);
				
				Object command = TascaFormHelper.getCommandForCamps(
						tascaService.findDades(tascaId),	// tascaDadas
						new HashMap<String, Object>(),		// valors
						campsAddicionals,					// campsAddicionals
						campsAddicionalsClasses, 			// campsAddicionalsClasses
						false);								// perFiltre
				SessionHelper.setAttribute(request,VARIABLE_COMMAND_TRAMITACIO+tascaId, command);
				return command;
			} catch (TascaNotFoundException ex) {
				MissatgesHelper.error(request, ex.getMessage());
				logger.error("No s'ha pogut trobar la tasca: " + ex.getMessage(), ex);
			} catch (Exception ignored) {} 
		}
		return null;
	}

	public Object populateCommand(
			HttpServletRequest request, 
			String tascaId,
			Model model) {
		return populateCommand(
				request, 
				tascaId, null, false, 0,
				model);		
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}", method = RequestMethod.GET)
	public String tramitar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		SessionHelper.removeAttribute(request, SessionHelper.VARIABLE_TASCA_ERRROR);
		return getReturnUrl(request, expedientId, tascaId);
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		// Omple les dades del formulari i les de només lectura
		getModelTramitacio(tascaId, model);
		model.addAttribute("command", populateCommand(request, tascaId, model));
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/guardar", method = RequestMethod.POST)
	public String formGuardar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@Valid @ModelAttribute("command") Object command,
			BindingResult result, 
			SessionStatus status,
			@RequestParam(value = "accio", required = false) String accio,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
			validator.setTasca(tascaDadas);
			guardarForm(validator, variables, command, result, request, tascaId);
			status.setComplete();
			getModelTramitacio(tascaId, (Model)model);
			TascaFormHelper.ompleMultiplesBuits(command, tascaDadas, false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/validar", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
			validator.setTasca(tascaDadas);
			validator.setRequest(request);
			guardarForm(validator, variables, command, result, request, tascaId);
			validarForm(validator, variables, command, result, request, tascaId);
			status.setComplete();
			getModelTramitacio(tascaId, (Model)model);
			TascaFormHelper.ompleMultiplesBuits(command, tascaDadas, false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/completar", method = RequestMethod.POST)
	public String formCompletar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "__transicio__", required = false) String transicio,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
			validator.setTasca(tascaDadas);
			validator.setRequest(request);
			guardarForm(validator, variables, command, result, request, tascaId);
			validarForm(validator, variables, command, result, request, tascaId);
			completarForm(request, tascaId, transicio, command);
			status.setComplete();
			getModelTramitacio(tascaId, (Model)model);
			TascaFormHelper.ompleMultiplesBuits(command, tascaDadas, false);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "v3/expedientTascaTramitacio";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/restaurar", method = RequestMethod.POST)
	public String formRestaurar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {		
		try {
			List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
			Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
			accioRestaurarForm(request, tascaId, variables, command); 	
        } catch (Exception ex) {
        	MissatgesHelper.error(request, ex.getMessage());
        	logger.error("No s'ha pogut restaurar el formulari en la tasca " + tascaId, ex);
        }
		
		return getReturnUrl(request, expedientId, tascaId);
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form/accio/{accioCamp}", method = RequestMethod.POST)
	public String formAccio(
	HttpServletRequest request,
	@PathVariable Long expedientId,
	@PathVariable String tascaId,
	@PathVariable String accioCamp,
	@ModelAttribute("command") Object command, 
	BindingResult result, 
	SessionStatus status, 
	ModelMap model) {
		List<TascaDadaDto> tascaDadas = tascaService.findDades(tascaId);
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
		TascaFormValidatorHelper validator = new TascaFormValidatorHelper(tascaService, false);
		validator.setTasca(tascaDadas);
		validator.setRequest(request);
		validator.validate(command, result);
		if (result.hasErrors() || !accioGuardarForm(request, tascaId, variables, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
		} else if (accioExecutarAccio(request, tascaId, accioCamp, command)) {
			model.addAttribute("campFocus", accioCamp);
		}
		
		return getReturnUrl(request, expedientId, tascaId);
	}
	
	private void guardarForm(
			TascaFormValidatorHelper validator, 
			Map<String, Object> variables, 
			Object command, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId) {
		validator.validate(command, result);
		if (result.hasErrors() || !accioGuardarForm(request, tascaId, variables, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
		}
	}
	
	private void validarForm(
			TascaFormValidatorHelper validator, 
			Map<String, Object> variables, 
			Object command, 
			BindingResult result, 
			HttpServletRequest request,
			String tascaId) {
		validator.setRequest(request);
		validator.setValidarObligatoris(true);
		validator.validate(command, result);
		if (result.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "error.validacio"));
			MissatgesHelper.errorGlobal(request, result, getMessage(request, "error.validacio"));
			System.out.println(result);
		} else if (!accioValidarForm(request, tascaId, variables, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		}
	}
	
	private void completarForm(
			HttpServletRequest request,
			String tascaId,
			String transicio,
			Object command) {
		if (!accioCompletarForm(request, tascaId, transicio, command)) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		}
	}
	
	private void getModelTramitacio(String tascaId, Model model) {		
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		if (tasca.getRecursForm() != null && tasca.getRecursForm().length() > 0) {
			try {
				byte[] contingut = dissenyService.getDeploymentResource(tasca.getDefinicioProces().getId(), tasca.getRecursForm());
				model.addAttribute("formRecursParams", getFormRecursParams(new String(contingut, "UTF-8")));
			} catch (Exception ex) {
				logger.error("No s'han pogut extreure els parametres del recurs", ex);
			}
		}
		model.addAttribute("tasca", tasca);
		
		List<TascaDadaDto> dadesNomesLectura = new ArrayList<TascaDadaDto>();
		List<TascaDadaDto> dades = tascaService.findDades(tascaId);
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				dadesNomesLectura.add(dada);
				itDades.remove();
			}
		}
		model.addAttribute("dadesNomesLectura", dadesNomesLectura);
		model.addAttribute("dades", dades);
		model.addAttribute("hasDocuments", tascaService.hasDocuments(tascaId));
		model.addAttribute("hasSignatures", tascaService.hasDocumentsSignar(tascaId));
		
		signatures(null, tasca.getExpedientId(), tascaId, model);
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/documents", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		// Omple els documents per adjuntar i els de només lectura
		List<TascaDocumentDto> documents = tascaService.findDocuments(tascaId);
		List<TascaDocumentDto> documentsNomesLectura = new ArrayList<TascaDocumentDto>();
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly()) {
				if (document.getId() != null)
					documentsNomesLectura.add(document);
				itDocuments.remove();
			}
		}
		model.addAttribute("documentsNomesLectura", documentsNomesLectura);
		model.addAttribute("documents", documents);
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("tascaId", tascaId);
		return "v3/expedientTascaTramitacioDocuments";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/signatures", method = RequestMethod.GET)
	public String signatures(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("tascaId", tascaId);
		model.addAttribute("signatures", tascaService.findDocumentsSignar(tascaId));
		return "v3/expedientTascaTramitacioSignar";
	}
	
	private boolean accioRestaurarForm(
			HttpServletRequest request, 
			String tascaId, 
			Map<String, Object> variables, 
			Object command) {
		boolean resposta = false;

		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();

		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");

				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {
					}
				}

				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId : seleccio) {
					tascaIds[i++] = tId.toString();
				}

				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();

				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);
				// dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Restaurar");
				Object[] params = new Object[3];
				params[0] = entorn.getId();
				params[1] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[2] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);

				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.restaurar", new Object[] { seleccio.size() }));

				tascaService.restaurar(tascaId);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar", new Object[] { seleccio.size() }));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut restaurat les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.restaurar(tascaId);
				MissatgesHelper.info(request, getMessage(request, "info.formulari.restaurat"));
				resposta = true;
			} catch (Exception ex) {
				// String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
				// MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
				// logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaId);
				logger.error("No s'ha pogut restaurat les dades del formulari en la tasca " + tascaId, ex);
			}
		}
		return resposta;
	}
	
	private boolean accioGuardarForm(
			HttpServletRequest request, 
			String tascaId, 
			Map<String, Object> variables, Object command) {
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Guardar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = variables;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.guardar(tascaId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar", new Object[] {seleccio.size()}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.guardar(tascaId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.dades.form.guardat"));
				resposta = true;
			} catch (Exception ex) {
//				String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
//				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
//				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaId);
				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaId, ex);
			}
		}
		return resposta;
	}

	private boolean accioValidarForm(
			HttpServletRequest request, 
			String tascaId, 
			Map<String, Object> variables,
			Object command) {
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Validar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = variables;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.validar(tascaId, variables);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.validar", new Object[] {seleccio.size()}));
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut validar el formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.validar(tascaId, variables);
	        	MissatgesHelper.info(request, getMessage(request, "info.formulari.validat"));
				resposta = true;
			} catch (Exception ex) {
//				String tascaIdLog = getIdTascaPerLogs(entornId, tascaId);
//				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
//				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				MissatgesHelper.error(request, getMessage(request, "error.validar.formulari") + " " + tascaId);
				logger.error("No s'ha pogut validar el formulari en la tasca " + tascaId, ex);
			}
		}
		return resposta;
	}

	private boolean accioExecutarAccio(
			HttpServletRequest request,
			String tascaId,
			String accio,
			Object command) {
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Accio");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = accio;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.accio", new Object[] {seleccio.size()}));
				
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.executarAccio(tascaId, accio);
				MissatgesHelper.info(request, getMessage(request, "info.accio.executat"));
				resposta = true;
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.executar.accio") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut executar l'acció " + tascaId, ex);
				}
			}
		}
		return resposta;
	}
	
	private boolean accioCompletarForm(
			HttpServletRequest request,
			String tascaId,
			String transicio,
			Object command) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		String transicio_sortida = null;
		for (String outcome: tasca.getOutcomes()) {
			if (outcome != null && outcome.equals(transicio)) {
				transicio_sortida = outcome;
				break;
			}
		}
		
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				Date dInici = new Date();
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);
//				dto.setExpedientTipusId(expTipusId);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("Completar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();
				params[1] = transicio;
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);
				
				tascaService.completar(tascaId, transicio_sortida);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {seleccio.size()}));
				resposta = true;
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.finalitzar.tasca") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut finalitzar la tasca massiu" + tascaId, ex);
				}
			}
		} else {
			try {
				tascaService.completar(tascaId, transicio_sortida);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.completat"));
				resposta = true;
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.validacio.tasca") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.finalitzar.tasca") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut finalitzar la tasca " + tascaId, ex);
				}
	        }
		}
		return resposta;
	}

	@RequestMapping(value = {"/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio", 
							"/{expedientId}/tasca/{tascaId}/form/camp/{campId}/valorsSeleccio"}, method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			Model model) {
		return tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				null,
				new HashMap<String, Object>());
	}

	@RequestMapping(value = {"/camp/{campId}/valorSeleccioInicial/{valor}"}, method = RequestMethod.GET)
	@ResponseBody
	public SeleccioOpcioDto valorsSeleccioInicial(
			HttpServletRequest request,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {		
		return valorsSeleccioInicial(
				request,
				null,
				campId,
				valor,
				model);
	}

	@RequestMapping(value = {"/camp/{campId}/valorsSeleccio"}, method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable Long campId,
			Model model) {
		return valorsSeleccio(
				request,
				null,
				campId,
				model);
	}

	@RequestMapping(value = {"/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio/{valor}", 
							"/{expedientId}/tasca/{tascaId}/form/camp/{campId}/valorsSeleccio/{valor}"}, method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {
		return tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				valor,
				new HashMap<String, Object>());
	}

	@RequestMapping(value = {"/{expedientId}/tasca/{tascaId}/camp/{campId}/valorSeleccioInicial/{valor}", 
							 "/{expedientId}/tasca/{tascaId}/form/camp/{campId}/valorSeleccioInicial/{valor}"}, method = RequestMethod.GET)
	@ResponseBody
	public SeleccioOpcioDto valorsSeleccioInicial(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			@PathVariable String valor,
			Model model) {
		List<SeleccioOpcioDto> valorsSeleccio = tascaService.findllistaValorsPerCampDesplegable(
				tascaId,
				campId,
				null,
				getMapDelsValors(valor));
		for (SeleccioOpcioDto sel : valorsSeleccio) {
			if (sel.getCodi().equals(valor)) {
				return sel;
			}
		}
		return new SeleccioOpcioDto();
	}

	@ModelAttribute("listTerminis")
	public List<ParellaCodiValorDto> valors12(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (int i=0; i <= 12 ; i++)		
			resposta.add(new ParellaCodiValorDto(String.valueOf(i), i));
		return resposta;
	}	
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/documentAdjuntar", method = RequestMethod.POST)
	@ResponseBody
	public String documentAdjuntar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "docId", required = false) Long docId,
			@RequestParam(value = "arxiu", required = false) final CommonsMultipartFile arxiu,	
			@RequestParam(value = "data", required = false) Date data,
			Model model) {
		String resposta = "";
		try {
			byte[] contingutArxiu = IOUtils.toByteArray(arxiu.getInputStream());
			String nomArxiu = arxiu.getOriginalFilename();
			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			if (!tasca.isValidada()) {
				MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
			} else if (!expedientService.isExtensioDocumentPermesa(nomArxiu)) {
				MissatgesHelper.error(request, getMessage(request, "error.extensio.document"));
			} else {
				resposta = accioDocumentAdjuntar(
						request,
						tascaId,
						docId,
						nomArxiu,
						contingutArxiu,
						data).toString();
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.guardar.document") + ": " + ex.getLocalizedMessage());
			logger.error("Error al adjuntar el document " + docId, ex);
		}
		return resposta;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/documentGenerar", method = RequestMethod.GET)
	@ResponseBody
	public String documentGenerarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "docId", required = true) Long docId,		
			@RequestParam(value = "data", required = false) Date data,
			Model model) {
		String generatId = "";
		try {
			ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
			if (!tasca.isValidada()) {
				MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
			} else {
				DocumentDto generat = accioDocumentGenerar(
						request,
						tascaId, 
						docId, 
						expedientId,
						data);
				if (generat != null) {
					generatId = String.valueOf(generat.getId());
					if (!generat.isAdjuntarAuto()) {
						model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, generat.getArxiuNom());
						model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, generat.getArxiuContingut());
						return "arxiuView";
					}
				}
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.generar.document") + ": " + ex.getLocalizedMessage());
			logger.error("Error generant el document " + docId, ex);
		}
		return generatId;
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/documentEsborrar", method = RequestMethod.GET)
	@ResponseBody
	public boolean documentEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "docId", required = true) Long docId,		
			@RequestParam(value = "data", required = false) Date data,
			Model model) {
		boolean response = false;
		ExpedientTascaDto tasca = tascaService.findAmbIdPerTramitacio(tascaId);
		if (!tasca.isValidada()) {
			MissatgesHelper.error(request, getMessage(request, "error.validar.dades"));
		} else {
			response = accioDocumentEsborrar(
					request,
					tascaId,
					docId,
					data);
		}
		return response;
	}

	private Long accioDocumentAdjuntar(
			HttpServletRequest request,
			String tascaId,
			Long docId,
			String nomArxiu,
			byte[] contingutArxiu,
			Date data) {
		Long documentStoreId = null;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		TascaDocumentDto document = tascaService.findDocument(tascaId, docId);
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				Object command = populateCommand(request, tascaId, null,false,0,null);
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				
				// Guardamos el documento de la primera tarea
				documentStoreId = tascaService.guardarDocumentTasca(
						entorn.getId(),
						tascaId,
						document.getDocumentCodi(),
						(data == null) ? new Date() : data,
						nomArxiu,
						contingutArxiu,
						null);
				
				Date dInici = (data == null) ? new Date() : data;
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}

				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();				
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocGuardar");
				Object[] params = new Object[7];
				params[0] = entorn.getId();				
				params[1] = document.getDocumentCodi();
				params[2] = (data == null) ? (data == null) ? new Date() : data : data;
				params[3] = contingutArxiu;
				params[4] = nomArxiu;
				params[5] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[6] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);

				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.document.guardar", new Object[] {seleccio.size()}));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				documentStoreId = tascaService.guardarDocumentTasca(
					entorn.getId(),
					tascaId,
					document.getDocumentCodi(),
					(data == null) ? new Date() : data,
					nomArxiu,
					contingutArxiu,
					null);
				MissatgesHelper.info(request, getMessage(request, "info.document.adjuntat"));
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.guardar.document") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.guardar.document") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut executar l'acció " + tascaId, ex);
				}
			}
		}
		return documentStoreId;
	}
	private boolean accioDocumentEsborrar(
			HttpServletRequest request,
			String tascaId,
			Long docId,
			Date data) {
		boolean resposta = false;
		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		TascaDocumentDto document = tascaService.findDocument(tascaId, docId);
		if (seleccio != null && !seleccio.isEmpty()) {
			try {				
				Date dInici = (data == null) ? new Date() : data;
				Object command = populateCommand(request, tascaId, null,false,0,null);
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}
				
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocEsborrar");
				Object[] params = new Object[4];
				params[0] = entorn.getId();				
				params[1] = document.getDocumentCodi();
				params[2] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[3] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);

				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.document.esborrar", new Object[] {seleccio.size()}));
				
				resposta = true;
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut guardar les dades del formulari massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				tascaService.esborrarDocument(
						tascaId,
						document.getDocumentCodi(),
						null);
				MissatgesHelper.info(request, getMessage(request, "info.document.esborrat"));
				resposta = true;
	        } catch (Exception ex) {
	        	if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.esborrar.document") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.executar.accio") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut esborrar el document '" + docId + "' a la tasca " + tascaId, ex);
		        }
	        }
		}
		return resposta;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/icones/{docId}", method = RequestMethod.GET)
	public String documentEsborrar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable Long docId,
			Model model) {
		model.addAttribute("document", tascaService.findDocument(tascaId, docId));
		model.addAttribute("tasca", tascaService.findAmbIdPerTramitacio(tascaId));
		return "v3/expedientTascaTramitacioSignarIcones";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/signarAmbToken", method = RequestMethod.POST)
	@ResponseBody
	public boolean signarAmbToken(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value="taskId", required = true) String taskId,
			@RequestParam(value="docId", required = true) Long docId,			
			@RequestParam(value="token", required = true) String token,
			@RequestParam(value="data", required = true) String[] data,
			Model model) {
		boolean signat = false;
		try {
			StringBuffer aData = new StringBuffer();
			for (String dat : data) {
				aData.append(dat);
			}			
			signat = tascaService.signarDocumentTascaAmbToken(
					expedientId,
					docId,
					taskId,
					token,
					Base64.decodeBase64(aData.toString().getBytes()));
			if (signat) {
				logger.info("Signatura del document amb el token " + token + " processada correctament");
				MissatgesHelper.info(
	        			request,
	        			getMessage(request, "info.signatura.doc.processat") );
			} else {
				logger.error("Signatura del document amb el token " + token + " processada amb error de custòdia");
				MissatgesHelper.error(
	        			request,
	        			getMessage(request, "error.validar.signatura") );
			}
		} catch(Exception ex) {
			logger.error("Error rebent la signatura del document", ex);
			MissatgesHelper.error(
        			request,
        			getMessage(request, "error.rebre.signatura") );
	    }
		return signat;
	}
	private DocumentDto accioDocumentGenerar(
			HttpServletRequest request,
			String tascaId,
			Long docId,
			Long expedientId,
			Date data) {		
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaTasca();
		DocumentDto generat = null;
		
		if (seleccio != null && !seleccio.isEmpty()) {
			try {
				Object command = populateCommand(request, tascaId, null,false,0,null);
				String inici = (String) PropertyUtils.getSimpleProperty(command, "inici");
				boolean correu = (Boolean) PropertyUtils.getSimpleProperty(command, "correu");
				Date dInici = (data == null) ? new Date() : data;
				if (inici != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					try {
						dInici = sdf.parse(inici);
					} catch (ParseException e) {}
				}
				
				String[] tascaIds = new String[seleccio.size()];
				int i = 0;
				for (Long tId: seleccio) {
					tascaIds[i++] = tId.toString();
				}
				
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				ExecucioMassivaDto dto = new ExecucioMassivaDto();
				dto.setDataInici(dInici);
				dto.setEnviarCorreu(correu);
				dto.setTascaIds(tascaIds);					
				dto.setExpedientTipusId(null);
				dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
				dto.setParam1("DocGuardar");
				Object[] params = new Object[7];
				params[0] = entorn.getId();
				TascaDocumentDto document = tascaService.findDocument(tascaId, docId);
				params[1] = document.getDocumentCodi();
				params[2] = (data == null) ? new Date() : data;
				generat = expedientService.generarDocumentPlantillaTasca(tascaId, docId, expedientId);
				params[3] = generat.getArxiuContingut();
				params[4] = generat.getArxiuNom();
				params[5] = auth.getCredentials();
				List<String> rols = new ArrayList<String>();
				for (GrantedAuthority gauth : auth.getAuthorities()) {
					rols.add(gauth.getAuthority());
				}
				params[6] = rols;
				dto.setParam2(execucioMassivaService.serialize(params));
				execucioMassivaService.crearExecucioMassiva(dto);

				MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.document.generar", new Object[] {seleccio.size()}));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				logger.error("No s'ha pogut generar l'document massiu en la tasca " + tascaId, ex);
			}
		} else {
			try {
				generat = expedientService.generarDocumentPlantillaTasca(tascaId, docId, expedientId);
				MissatgesHelper.info(request, getMessage(request, "info.document.generat"));
			} catch (Exception ex) {
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.generar.document") + " " + tascaId + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(
		        			request,
		        			getMessage(request, "error.generar.document") + " " + tascaId + ": " + 
		        					(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
					logger.error("No s'ha pogut generar l'document " + tascaId, ex);
				}
			}
		}
		return generat;
	}

	private String getReturnUrl(HttpServletRequest request, Long expedientId, String tascaId) {
		if (ModalHelper.isModal(request))
			return "redirect:/modal/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
		else
			return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
	
	}
	private Map<String, Object> getMapDelsValors(String valors) {
		if (valors == null)
			return null;
		Map<String, Object> resposta = new HashMap<String, Object>();
		String[] parelles = valors.split(",");
		for (int i = 0; i < parelles.length; i++) {
			String[] parts = parelles[i].split(":");
			if (parts.length == 2)
				resposta.put(parts[0], parts[1]);
		}
		return resposta;
	}
	
	private Map<String, String> getFormRecursParams(String text) {
		Map<String, String> params = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(TAG_PARAM_REGEXP);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String[] paramParts = matcher.group(1).split(":");
			if (paramParts.length == 2) {
				params.put(paramParts[0], paramParts[1]);
			}
		}
		return params;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
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
//				new CustomBooleanEditor(false));
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
//		binder.registerCustomEditor(
//				TerminiDto.class,
//				new TerminiTypeEditorHelper());
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}

	private static final Logger logger = LoggerFactory.getLogger(TascaTramitacioController.class);

}
