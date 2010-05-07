/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.PersonaUsuariDto;
import net.conselldemallorca.helium.model.hibernate.Permis;
import net.conselldemallorca.helium.model.hibernate.Persona;
import net.conselldemallorca.helium.model.service.PersonaService;
import net.conselldemallorca.helium.model.service.PluginService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió d'entorns
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
@RequestMapping("/persona/*.html")
public class PersonaController extends BaseController {

	private PersonaService personaService;
	private PluginService pluginService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public PersonaController(
			PersonaService personaService,
			PluginService pluginService) {
		this.personaService = personaService;
		this.pluginService = pluginService;
		additionalValidator = new PersonaUsuariValidator(personaService);
	}

	@ModelAttribute("command")
	public PersonaUsuariCommand populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			PersonaUsuariDto dto = personaService.getPersonaUsuariById(id);
			PersonaUsuariCommand command = new PersonaUsuariCommand();
			command.setId(dto.getId());
			command.setCodi(dto.getCodi());
			command.setNom(dto.getNom());
			command.setLlinatge1(dto.getLlinatge1());
			command.setLlinatge2(dto.getLlinatge2());
			command.setDataNaixement(dto.getDataNaixement());
			command.setEmail(dto.getEmail());
			command.setSexe(dto.getSexe());
			command.setAvisCorreu(dto.getAvisCorreu());
			command.setFont(dto.getFont());
			command.setRelleu(dto.getRelleu());
			command.setLogin(dto.isLogin());
			command.setContrasenya(dto.getContrasenya());
			command.setPermisos(dto.getPermisos());
			return command;
		}
		return new PersonaUsuariCommand();
	}

	@ModelAttribute("permisos")
	public List<Permis> populatePersones() {
		return personaService.findPermisosAll();
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		int pagina = (page != null) ? new Integer(page).intValue() : 1;
		int firstRow = (pagina - 1) * getObjectsPerPage(objectsPerPage);
		boolean isAsc = "asc".equals(dir);
		model.addAttribute(
				"llistat",
				newPaginatedList(
						pagina,
						sort,
						isAsc,
						getObjectsPerPage(objectsPerPage),
						personaService.countPersonaUsuariAll(),
						personaService.findPersonaUsuariPagedAndOrderedAll(
								sort,
								isAsc,
								firstRow,
								getObjectsPerPage(objectsPerPage))));
		return "persona/llistat";
	}

	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String formGet() {
		return "persona/form";
	}
	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") PersonaUsuariCommand command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || submit.length() == 0) {
			annotationValidator.validate(command, result);
			additionalValidator.validate(command, result);
	        if (result.hasErrors()) {
	        	return "persona/form";
	        }
	        PersonaUsuariDto saved = null;
	        try {
	        	if (command.getId() == null)
	        		saved = personaService.createPersonaUsuari(command);
	        	else
	        		saved = personaService.updatePersonaUsuari(command);
	        	if (saved != null && command.getContrasenya() != null && command.getContrasenya().length() > 0)
	        		personaService.canviContrasenya(saved.getId(), command.getContrasenya());
	        	missatgeInfo(request, "La persona s'ha guardat correctament");
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	return "persona/form";
	        }
	        return "redirect:/persona/llistat.html";
		} else {
			return "redirect:/persona/llistat.html";
		}
	}

	@RequestMapping(value = "delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		personaService.deletePersona(id);
		missatgeInfo(request, "La persona s'ha esborrat correctament");
		return "redirect:/persona/llistat.html";
	}

	@RequestMapping(value = "suggest", method = RequestMethod.GET)
	public String suggestAction(
			@RequestParam(value = "q", required = true) String text,
			ModelMap model) {
		model.addAttribute("persones", pluginService.findPersonaLikeNomSencer(text));
		return "persona/suggest";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Permis.class,
				new PermisTypeEditor());
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private class PersonaUsuariValidator implements Validator {
		private PersonaService personaService;
		public PersonaUsuariValidator(PersonaService personaService) {
			this.personaService = personaService;
		}
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(PersonaUsuariCommand.class);
		}
		public void validate(Object target, Errors errors) {
			PersonaUsuariCommand command = (PersonaUsuariCommand)target;
			// El codi d'usuari no pot estar repetit
			Persona repetida = personaService.findPersonaAmbCodi(command.getCodi());
			if (repetida != null && !repetida.getId().equals(command.getId())) {
				errors.rejectValue("codi", "error.persona.codi.repetit");
			}
			// Si l'usuari pot fer login la contrasenya i la repetició són obligatoris
			if (command.isLogin() && command.getId() == null) {
				ValidationUtils.rejectIfEmpty(errors, "contrasenya", "not.blank");
				ValidationUtils.rejectIfEmpty(errors, "repeticio", "not.blank");
			}
			// La contrasenya i la repetició han de coincidir
			if (command.getContrasenya() != null) {
				if (command.getRepeticio() == null || !command.getRepeticio().equals(command.getContrasenya())) {
					errors.rejectValue("contrasenya", "error.contrasenya.repeticio");
					errors.rejectValue("repeticio", "error.contrasenya.repeticio");
				}
			}
		}
	}

	private static final Log logger = LogFactory.getLog(PersonaController.class);

}
