/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.common.ExpedientCamps;
import net.conselldemallorca.helium.core.common.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helper.ConsultaHelper;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.ExpedientRegistreHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.helper.VariableHelper;
import net.conselldemallorca.helium.core.helperv26.LuceneHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.jbpm3.integracio.ExecucioHandlerException;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;
import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AlertaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientErrorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientErrorDto.ErrorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioHandlerException;
import net.conselldemallorca.helium.v3.core.api.exception.TramitacioValidacioException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientHeliumRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientLoggerRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;


/**
 * Servei per a gestionar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("expedientServiceV3")
public class ExpedientServiceImpl implements ExpedientService {

	private String textBloqueigIniciExpedient;

	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHeliumRepository expedientHeliumRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ExpedientLoggerRepository expedientLogRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private TerminiRepository terminiRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ConsultaHelper consultaHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource(name="pluginHelperV3")
	private PluginHelper pluginHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private LuceneHelper luceneHelper;
//	@Resource
//	private MongoDBHelper mongoDBHelper;
	@Resource(name="permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private IndexHelper indexHelper;
//	@Resource
//	private MetricRegistry metricRegistry;



	@Override
	@Transactional
	public synchronized ExpedientDto create(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipusDto iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts) {
		logger.debug("Creant nou expedient (" +
				"entornId=" + entornId + ", " +
				"usuari=" + usuari + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"any=" + any + ", " +
				"numero=" + numero + ", " +
				"titol=" + titol + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		if (usuari != null)
			comprovarUsuari(usuari);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String usuariBo = (usuari != null) ? usuari : auth.getName();
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, expedientTipusId);
		
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom());
		mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Nou expedient");
		
		// Obté la llista de tipus d'expedient permesos
		List<ExpedientTipus> tipusPermesos = expedientTipusRepository.findByEntorn(entorn);
		permisosHelper.filterGrantedAny(
				tipusPermesos,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.CREATE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		
		textBloqueigIniciExpedient = auth.getName() + " (" +
				"entornCodi=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipus.getCodi() + ", " +
				"data=" + new Date() + ")";
		
		Expedient expedient = new Expedient();
		try {
			
			String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipusDto.INTERN)) ? usuariBo : iniciadorCodi;
			expedient.setTipus(expedientTipus);
			expedient.setIniciadorTipus(conversioTipusHelper.convertir(iniciadorTipus, IniciadorTipus.class));
			expedient.setIniciadorCodi(iniciadorCodiCalculat);
			expedient.setEntorn(entorn);
			expedient.setProcessInstanceId(UUID.randomUUID().toString());
			String responsableCodiCalculat = (responsableCodi != null) ? responsableCodi : expedientTipus.getResponsableDefecteCodi();
			if (responsableCodiCalculat == null)
				responsableCodiCalculat = iniciadorCodiCalculat;
			expedient.setResponsableCodi(responsableCodiCalculat);
			expedient.setRegistreNumero(registreNumero);
			expedient.setRegistreData(registreData);
			expedient.setUnitatAdministrativa(unitatAdministrativa);
			expedient.setIdioma(idioma);
			expedient.setAutenticat(autenticat);
			expedient.setTramitadorNif(tramitadorNif);
			expedient.setTramitadorNom(tramitadorNom);
			expedient.setInteressatNif(interessatNif);
			expedient.setInteressatNom(interessatNom);
			expedient.setRepresentantNif(representantNif);
			expedient.setRepresentantNom(representantNom);
			expedient.setAvisosHabilitats(avisosHabilitats);
			expedient.setAvisosEmail(avisosEmail);
			expedient.setAvisosMobil(avisosMobil);
			expedient.setNotificacioTelematicaHabilitada(notificacioTelematicaHabilitada);
			expedient.setAmbRetroaccio(expedientTipus.isAmbRetroaccio());
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Omplir dades");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Assignar numeros");

			expedient.setNumeroDefault(
					getNumeroExpedientDefaultActual(
							entorn,
							expedientTipus,
							any));
//			MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "2");
			if (expedientTipus.getTeNumero()) {
				if (numero != null && numero.length() > 0 && expedientTipus.getDemanaNumero()) {
					expedient.setNumero(numero);
				} else {
					expedient.setNumero(
							getNumeroExpedientActual(
									entornId,
									expedientTipusId,
									any));
				}
			}
	
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Assignar numeros");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Verificar numero repetit");

			// Verifica si l'expedient té el número repetit
			if (expedient.getNumero() != null && (expedientRepository.findByEntornIdAndTipusIdAndNumero(
					entorn.getId(),
					expedientTipus.getId(),
					expedient.getNumero()) != null)) {
				throw new ValidacioException(
						messageHelper.getMessage(
								"error.expedientService.jaExisteix",
								new Object[]{expedient.getNumero()}) );
			}
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Verificar numero repetit");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Actualitzar any i sequencia");
			
			// Actualitza l'any actual de l'expedient
			int anyActual = Calendar.getInstance().get(Calendar.YEAR);
			if (any == null || any.intValue() == anyActual) {
				if (expedientTipus.getAnyActual() == 0) {
					expedientTipus.setAnyActual(anyActual);
				} else if (expedientTipus.getAnyActual() < anyActual) {
					expedientTipus.setAnyActual(anyActual);
				}
			}
			// Actualitza la seqüència del número d'expedient
			if (expedientTipus.getTeNumero() && expedientTipus.getExpressioNumero() != null && !"".equals(expedientTipus.getExpressioNumero())) {
				if (expedient.getNumero().equals(
						getNumeroExpedientActual(
								entornId,
								expedientTipusId,
								any)))
					expedientTipus.updateSequencia(any, 1);
			}
			// Actualitza la seqüència del número d'expedient per defecte
			if (expedient.getNumeroDefault().equals(
					getNumeroExpedientDefaultActual(
							entorn,
							expedientTipus,
							any)))
				expedientTipus.updateSequenciaDefault(any, 1);
			// Configura el títol de l'expedient
			if (expedientTipus.getTeTitol()) {
				if (titol != null && titol.length() > 0)
					expedient.setTitol(titol);
				else
					expedient.setTitol("[Sense títol]");
			}

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Actualitzar any i sequencia");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");
			
			// Inicia l'instància de procés jBPM
			ExpedientIniciantDto.setExpedient(expedient);
			DefinicioProces definicioProces = null;
			if (definicioProcesId != null) {
				definicioProces = definicioProcesRepository.findById(definicioProcesId);
			} else {
				definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
						entornId,
						expedientTipus.getJbpmProcessDefinitionKey());
			}
			//MesurarTemps.diferenciaImprimirStdoutIReiniciar(mesuraTempsIncrementalPrefix, "7");
			JbpmProcessInstance processInstance = jbpmHelper.startProcessInstanceById(
					usuariBo,
					definicioProces.getJbpmId(),
					variables);
			expedient.setProcessInstanceId(processInstance.getId());
			
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar instancia de proces");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");
			
			// Emmagatzema el nou expedient
			expedientRepository.saveAndFlush(expedient);

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Desar el nou expedient");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir documents");
			
			// Afegim els documents
			if (documents != null){
				for (Map.Entry<String, DadesDocumentDto> doc: documents.entrySet()) {
					if (doc.getValue() != null) {
						documentHelper.actualitzarDocument(
								null,
								expedient.getProcessInstanceId(),
								doc.getValue().getCodi(), 
								null,
								doc.getValue().getData(), 
								doc.getValue().getArxiuNom(), 
								doc.getValue().getArxiuContingut(),
								false);
					}
				}
			}
			// Afegim els adjunts
			if (adjunts != null) {
				for (DadesDocumentDto adjunt: adjunts) {
					String documentCodi = new Long(new Date().getTime()).toString();
					documentHelper.actualitzarDocument(
							null,
							expedient.getProcessInstanceId(),
							documentCodi,
							adjunt.getTitol(),
							adjunt.getData(), 
							adjunt.getArxiuNom(), 
							adjunt.getArxiuContingut(),
							true);
				}
			}

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir documents");

			// Verificar la ultima vegada que l'expedient va modificar el seu estat
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");
		
			ExpedientLog log = expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstance.getId(),
					ExpedientLogAccioTipus.EXPEDIENT_INICIAR,
					null);
			log.setEstat(ExpedientLogEstat.IGNORAR);
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Afegir log");
			
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar flux");
			
			// Actualitza les variables del procés
			jbpmHelper.signalProcessInstance(expedient.getProcessInstanceId(), transitionName);

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Iniciar flux");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Indexar expedient");
			
			// Indexam l'expedient
			logger.debug("Indexant nou expedient (id=" + expedient.getProcessInstanceId() + ")");
			indexHelper.expedientIndexLuceneCreate(expedient.getProcessInstanceId());
			
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Indexar expedient");
			mesuresTemporalsHelper.mesuraIniciar("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");
			
			// Registra l'inici de l'expedient
			crearRegistreExpedient(
					expedient.getId(),
					usuariBo,
					Registre.Accio.INICIAR);
			// Retorna la informació de l'expedient que s'ha iniciat
			ExpedientDto dto = conversioTipusHelper.convertir(
					expedient,
					ExpedientDto.class);

			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom(), null, "Crear registre i convertir expedient");
			mesuresTemporalsHelper.mesuraCalcular("Iniciar", "expedient", expedientTipus.getNom());

			ExpedientIniciantDto.setExpedient(null);
			logger.debug("textBloqueigIniciExpedient: " + textBloqueigIniciExpedient);
			return dto;
		} catch (ExecucioHandlerException ex) {
			throw new TramitacioHandlerException(
					(expedient != null) ? expedient.getEntorn().getId() : null, 
					(expedient != null) ? expedient.getEntorn().getCodi() : null,
					(expedient != null) ? expedient.getEntorn().getNom() : null, 
					(expedient != null) ? expedient.getId() : null, 
					(expedient != null) ? expedient.getTitol() : null,
					(expedient != null) ? expedient.getNumero() : null,
					(expedient != null) ? expedient.getTipus().getId() : null, 
					(expedient != null) ? expedient.getTipus().getCodi() : null,
					(expedient != null) ? expedient.getTipus().getNom() : null,
					ex.getProcessInstanceId(),
					ex.getTaskInstanceId(),
					ex.getTokenId(),
					ex.getClassName(),
					ex.getMethodName(),
					ex.getFileName(),
					ex.getLineNumber(),
					"", 
					ex.getCause());
		} catch (ValidationException ex) {
			throw new TramitacioValidacioException("Error de validació en Handler", ex);
		} finally {
			textBloqueigIniciExpedient = null;
		}
	}

	private Registre crearRegistreExpedient(
			Long expedientId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.EXPEDIENT,
				String.valueOf(expedientId));
		return registreRepository.save(registre);
	}	

	private Registre crearRegistreTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.TASCA,
				tascaId);
		return registreRepository.save(registre);
	}
	private Registre crearRegistreInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		return registreRepository.save(registre);
	}

	@Override
	@Transactional
	public ExpedientDadaDto getDadaPerInstanciaProces(String processInstanceId, String variableCodi, boolean incloureVariablesBuides) {
		return variableHelper.getDadaPerInstanciaProces(processInstanceId, variableCodi, incloureVariablesBuides);
	}

	@Override
	@Transactional(readOnly = true)
	public TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(ExpedientDadaDto dadaPerInstanciaProces) {
		return variableHelper.getTascaDadaDtoFromExpedientDadaDto(dadaPerInstanciaProces);
	}

	@Override
	@Transactional
	public List<ExpedientDadaDto> findDadesPerInstanciaProces(String procesId) {
		return variableHelper.findDadesPerInstanciaProces(procesId);
	}

	@Override
	@Transactional
	public void update(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler) {
		logger.debug(
				"Modificar informació de l'expedient (" +
				"id=" + id + ", " +
				"numero=" + numero + ", " +
				"titol=" + titol + ", " +
				"responsableCodi=" + responsableCodi + ", " +
				"dataInici=" + dataInici + ", " +
				"comentari=" + comentari + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"grupCodi=" + grupCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		
		expedientHelper.update(
				expedient,
				numero,
				titol,
				responsableCodi,
				dataInici,
				comentari,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				grupCodi,
				execucioDinsHandler);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean luceneReindexarExpedient(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		
		indexHelper.expedientIndexLuceneRecrear(expedient);
		return true;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
		if (documentStore == null)
			throw new NoTrobatException(DocumentStore.class, documentStoreId);
		return documentHelper.getRespostasValidacioSignatura(documentStore);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		logger.debug("Esborrant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				false,
				true,
				false);
		
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: processInstancesTree){
			for (TerminiIniciat ti: terminiIniciatRepository.findByProcessInstanceId(pi.getId()))
				terminiIniciatRepository.delete(ti);
			
			jbpmHelper.deleteProcessInstance(pi.getId());
			
			for (DocumentStore documentStore: documentStoreRepository.findByProcessInstanceId(pi.getId())) {
				if (documentStore.isSignat()) {
					try {
						pluginHelper.custodiaEsborrarSignatures(documentStore.getReferenciaCustodia(), expedient);
					} catch (Exception ignored) {}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(), expedient);
				documentStoreRepository.delete(documentStore.getId());
			}
		}
		
		
		for (Portasignatures psigna: expedient.getPortasignatures()) {
			psigna.setEstat(TipusEstat.ESBORRAT);
		}
		for (ExecucioMassivaExpedient eme: execucioMassivaExpedientRepository.getExecucioMassivaByExpedient(id)) {
			execucioMassivaExpedientRepository.delete(eme);
		}
		expedientRepository.delete(expedient);
		luceneHelper.deleteExpedient(expedient);

		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ESBORRAR);
	}

	@Override
	@Transactional
	public void crearModificarDocument(Long expedientId, String processInstanceId, Long documentStoreId, String nom, String nomArxiu, Long docId, byte[] arxiu, Date data) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				false,
				false,
				false,
				true);
		
		boolean creat = false;
		String arxiuNomAntic = null;
		boolean adjunt = false;
		DocumentStore documentStore = null;
		if (documentStoreId == null) {
			creat = true;
			adjunt = true;
		} else {
			documentStore = documentStoreRepository.findById(documentStoreId);
			if (documentStore == null)
				throw new NoTrobatException(DocumentStore.class, documentStoreId);
			
			arxiuNomAntic = documentStore.getArxiuNom();
			adjunt = documentStore.isAdjunt();
		}
		DocumentDto document = null;
		String codi = null;
		
		if (docId != null) {
			document = findDocumentsPerId(docId);
			if (document == null) {
				document = documentHelper.getDocumentSenseContingut(documentStoreId);
			}		
			if (document != null) {
				adjunt = document.isAdjunt();
				codi = document.getCodi();
			}
		}
		
		if (codi == null && (document == null || document.isAdjunt())) { 
			codi = new Long(new Date().getTime()).toString();
		}
		
		if (arxiu == null && document != null) {
			arxiu = document.getArxiuContingut();
			nomArxiu = document.getArxiuNom();
		} else if (arxiu == null && documentStore != null) {
			arxiu = documentStore.getArxiuContingut();
			nomArxiu = documentStore.getArxiuNom();
		}
		
		if (document != null && document.isAdjunt()) {
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR,
					codi);			
		} else if (creat) {
				expedientLoggerHelper.afegirLogExpedientPerProces(
						processInstanceId,
						ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR,
						codi);
		} else {
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR,
					codi);
		}
		if (documentStoreId != null && adjunt) {
			documentStoreId = documentHelper.actualitzarAdjunt(
					documentStoreId,
					processInstanceId,
					codi,
					nom,
					data,
					nomArxiu,
					arxiu,
					adjunt);
		} else {
			documentStoreId = documentHelper.actualitzarDocument(
					null,
					processInstanceId,
					codi,
					nom,
					data,
					nomArxiu,
					arxiu,
					adjunt);
		}
		
		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		
		// Registra l'acció
		if (creat) {
			expedientRegistreHelper.crearRegistreCrearDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					codi,
					nomArxiu);
		} else {
			expedientRegistreHelper.crearRegistreModificarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					user,
					codi,
					arxiuNomAntic,
					nomArxiu);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Long findIdAmbProcessInstanceId(String processInstanceId) {
		return expedientRepository.findIdByProcessInstanceId(processInstanceId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbId(Long id) {
		logger.debug("Consultant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		ExpedientDto expedientDto = conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
		expedientHelper.omplirPermisosExpedient(expedientDto);
		expedientHelper.trobarAlertesExpedient(expedientDto);
		return expedientDto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findAmbIds(Set<Long> ids) {
		List<ExpedientDto> listExpedient = new ArrayList<ExpedientDto>();
		logger.debug("Consultant l'expedient (ids=" + ids + ")");
		Set<Long> ids1 = null;
		Set<Long> ids2 = null;
		Set<Long> ids3 = null;
		Set<Long> ids4 = null;
		Set<Long> ids5 = null;
		int index = 0;
		for (Long id: ids) {
			if (index == 0)
				ids1 = new HashSet<Long>();
			if (index == 1000)
				ids2 = new HashSet<Long>();
			if (index == 2000)
				ids3 = new HashSet<Long>();
			if (index == 3000)
				ids4 = new HashSet<Long>();
			if (index == 4000)
				ids5 = new HashSet<Long>();
			if (index < 1000)
				ids1.add(id);
			else if (index < 2000)
				ids2.add(id);
			else if (index < 3000)
				ids3.add(id);
			else if (index < 4000)
				ids4.add(id);
			else
				ids5.add(id);
			index++;
		}
		for (Expedient expedient : expedientRepository.findAmbIds(ids1, ids2, ids3, ids4, ids5)) {
			listExpedient.add(conversioTipusHelper.convertir(
					expedient,
					ExpedientDto.class));
		}
		return listExpedient;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		logger.debug("Consulta general d'expedients paginada (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					true);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndId(expedientTipus, estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class,estatId);
			}
		}
		// Calcula la data fi pel filtre
		if (dataInici2 != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici2);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataInici2.setTime(cal.getTime().getTime());
		}
		// Obté la llista de tipus d'expedient permesos
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		// Executa la consulta amb paginació
		ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
				entornId,
				auth.getName(),
				tipusPermesosIds,
				titol,
				numero,
				expedientTipusId,
				dataInici1,
				dataInici2,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				EstatTipusDto.INICIAT.equals(estatTipus),
				EstatTipusDto.FINALITZAT.equals(estatTipus),
				MostrarAnulatsDto.SI.equals(mostrarAnulats),
				MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				true, // nomesTasquesMeves, // TODO Si no te permis SUPERVISION nomesTasquesMeves = false
				paginacioParams,
				false);
		// Retorna la pàgina amb la resposta
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>(); 
		if (expedientsIds.getCount() > 0) {
			expedients = conversioTipusHelper.convertirList(
				expedientRepository.findByIdIn(expedientsIds.getLlista()),
				ExpedientDto.class);
		}
		// Després de la consulta els expedients es retornen en ordre invers
		Collections.reverse(expedients);
		if (expedients.size() > 0) {
			expedientHelper.omplirPermisosExpedients(expedients);
			expedientHelper.trobarAlertesExpedients(expedients);
		}
		return paginacioHelper.toPaginaDto(
				expedients,
				expedientsIds.getCount(),
				paginacioParams);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients només ids (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					true);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndId(expedientTipus, estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class, estatId);
			}
		}
		// Calcula la data fi pel filtre
		if (dataInici2 != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici2);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataInici2.setTime(cal.getTime().getTime());
		}
		// Obté la llista de tipus d'expedient permesos
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		// Executa la consulta amb paginació
		ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
				entornId,
				auth.getName(),
				tipusPermesosIds,
				titol,
				numero,
				expedientTipusId,
				dataInici1,
				dataInici2,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				EstatTipusDto.INICIAT.equals(estatTipus),
				EstatTipusDto.FINALITZAT.equals(estatTipus),
				MostrarAnulatsDto.SI.equals(mostrarAnulats),
				MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				true, // nomesTasquesMeves, // TODO Si no te permis SUPERVISION nomesTasquesMeves = false
				new PaginacioParamsDto(),
				false);
		return expedientsIds.getLlista();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isDiferentsTipusExpedients(Set<Long> ids) {
		Set<Long> ids1 = null;
		Set<Long> ids2 = null;
		Set<Long> ids3 = null;
		Set<Long> ids4 = null;
		Set<Long> ids5 = null;
		int index = 0;
		for (Long id: ids) {
			if (index == 0)
				ids1 = new HashSet<Long>();
			if (index == 1000)
				ids2 = new HashSet<Long>();
			if (index == 2000)
				ids3 = new HashSet<Long>();
			if (index == 3000)
				ids4 = new HashSet<Long>();
			if (index == 4000)
				ids5 = new HashSet<Long>();
			if (index < 1000)
				ids1.add(id);
			else if (index < 2000)
				ids2.add(id);
			else if (index < 3000)
				ids3.add(id);
			else if (index < 4000)
				ids4.add(id);
			else
				ids5.add(id);
			index++;
		}
		
		List<Long> idsTipusExpedients = expedientRepository.getIdsDiferentsTipusExpedients(
				ids1,
				ids2,
				ids3,
				ids4,
				ids5);
		return idsTipusExpedients.size() > 1;
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuDocumentPerMostrar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentStore document = documentStoreRepository.findById(documentStoreId);
		if (document == null)
			return null;
		DocumentDto dto = null;
		if (document.isSignat() || document.isRegistrat()) {
			dto = documentHelper.getDocumentVista(documentStoreId, false, false);
			if (dto == null)
				return null;
			return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
		} else {
			dto = documentHelper.getDocumentOriginal(documentStoreId, true);
			if (dto == null)
				return null;
			return new ArxiuDto(dto.getArxiuNom(), dto.getArxiuContingut());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto arxiuDocumentPerSignar(String token) {
		Long documentStoreId = documentHelper.getDocumentStoreIdPerToken(token);
		DocumentDto dto = documentHelper.getDocumentVista(documentStoreId, true, true);
		if (dto == null)
			return null;
		return new ArxiuDto(dto.getVistaNom(), dto.getVistaContingut());
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) {
		logger.debug("Consulta de la imatge de la definició de procés (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		DefinicioProces definicioProces;
		if (processInstanceId != null) {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
		} else {
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					expedient.getProcessInstanceId());
		}
		String resourceName = "processimage.jpg";
		ArxiuDto imatge = new ArxiuDto();
		imatge.setNom(resourceName);
		imatge.setContingut(
				jbpmHelper.getResourceBytes(
						definicioProces.getJbpmId(),
						resourceName));
		return imatge;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> findParticipants(Long id) {
		logger.debug("Consulta de participants per a l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedient(
				expedient,
				false,
				false);
		Set<String> codisPersona = new HashSet<String>();
		List<PersonaDto> resposta = new ArrayList<PersonaDto>();
		for (ExpedientTascaDto tasca: tasques) {
			if (tasca.getAssignee() != null && !codisPersona.contains(tasca.getAssignee())) {
				resposta.add(tasca.getResponsable());
				codisPersona.add(tasca.getAssignee());
			}
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> findAccionsVisiblesAmbProcessInstanceId(String processInstanceId, Long expedientId) {
		logger.debug("Consulta d'accions visibles de l'expedient amb processInstanceId(" +
				"processInstanceId=" + processInstanceId + ")");
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		List<Accio> accions = accioRepository.findAmbDefinicioProcesAndOcultaFalse(definicioProces);
		// Filtra les accions restringides per rol que no estan permeses per a l'usuari actual
		Iterator<Accio> it = accions.iterator();
		while (it.hasNext()) {
			Accio accio = it.next();
			if (!permetreExecutarAccioExpedient(accio, expedientId)) it.remove();
		}
		return conversioTipusHelper.convertirList(accions, AccioDto.class);
	}

	@Transactional
	@Override
	public void accioExecutar(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		Accio accio = accioRepository.findOne(accioId);
		if (accio == null)
			throw new NoTrobatException(Accio.class, accioId);
		
		if (this.permetreExecutarAccioExpedient(accio, expedientId)) {
			Expedient expedient = expedientRepository.findOne(expedientId);
			if (expedient == null)
				throw new NoTrobatException(Expedient.class, expedientId);
			
			mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
			expedientLoggerHelper.afegirLogExpedientPerProces(
					processInstanceId,
					ExpedientLogAccioTipus.EXPEDIENT_ACCIO,
					accio.getJbpmAction());
			try {
				jbpmHelper.executeActionInstanciaProces(
						processInstanceId,
						accio.getJbpmAction());
			} catch (Exception ex) {
				if (ex instanceof ExecucioHandlerException) {
					logger.error(
							"Error al executa l'acció '" + accio.getCodi() + "': " + ex.toString(),
							ex.getCause());
				} else {
					logger.error(
							"Error al executa l'acció '" + accio.getCodi() + "'",
							ex);
				}
				throw new TramitacioException(
						expedient.getEntorn().getId(), 
						expedient.getEntorn().getCodi(), 
						expedient.getEntorn().getNom(), 
						expedient.getId(), 
						expedient.getTitol(), 
						expedient.getNumero(), 
						expedient.getTipus().getId(), 
						expedient.getTipus().getCodi(), 
						expedient.getTipus().getNom(), 
						"Error al executa l'acció '" + accio.getCodi() + "'", 
						ex);
			}
			verificarFinalitzacioExpedient(processInstanceId, expedient);
			indexHelper.expedientIndexLuceneUpdate(processInstanceId);
			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
		} else {
			throw new PermisDenegatException(
					expedientId, 
					Expedient.class, 
					new Permission[]{ExtendedPermission.WRITE},
					null);
		}
	}

	@Transactional(readOnly=true)
	@Override
	public AccioDto findAccioAmbId(Long idAccio) {
		return conversioTipusHelper.convertir(accioRepository.findOne(idAccio), AccioDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> findAccionsVisibles(Long id) {
		logger.debug("Consulta d'accions visibles de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				expedient.getProcessInstanceId());
		List<Accio> accions = accioRepository.findAmbDefinicioProcesAndOcultaFalse(definicioProces);
		// Filtra les accions restringides per rol que
		// no estan permeses per a l'usuari actual
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Iterator<Accio> it = accions.iterator();
		while (it.hasNext()) {
			Accio accio = it.next();
			if (accio.getRols() != null) {
				boolean permesa = false;
				for (String rol: accio.getRols().split(",")) {
					if (isUserInRole(auth, rol)) {
						permesa = true;
						break;
					}
				}
				if (!permesa) it.remove();
			}
		}
		return conversioTipusHelper.convertirList(
				accions,
				AccioDto.class);
	}

	@Override
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPerInstanciaProces(Long expedientId, String processInstanceId, boolean mostrarDeOtrosUsuarios) {
		logger.debug("Consulta de tasques de l'expedient (" +
				"expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedientPerInstanciaProces(expedient, processInstanceId, false, mostrarDeOtrosUsuarios);
		tasques.addAll(tascaHelper.findTasquesPerExpedientPerInstanciaProces(expedient, processInstanceId, true, mostrarDeOtrosUsuarios));
		return tasques;
	}

	@Override
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		logger.debug("Consulta de tasques pendents de l'expedient (" +
				"id=" + expedientId + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean tasquesAltresUsuaris = permisosHelper.isGrantedAny(
					expedient.getTipus().getId(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.REASSIGNMENT,
						ExtendedPermission.ADMINISTRATION},
					auth);
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		for (JbpmProcessInstance jpi: jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId())) {
			resposta.addAll(
					tascaHelper.findTasquesPerExpedientPerInstanciaProces(
							jpi.getId(),
							expedient,
							tasquesAltresUsuaris,
							nomesTasquesPersonals,
							nomesTasquesGrup));
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDadaDto> findDadesPerInstanciaProces(
			Long id,
			String processInstanceId) {
		logger.debug("Consulta de dades de l'expedient (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		if (processInstanceId == null) {
			return variableHelper.findDadesPerInstanciaProces(
					expedient.getProcessInstanceId(), 
					true);
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			return variableHelper.findDadesPerInstanciaProces(
					processInstanceId,
					true);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> findAgrupacionsDadesPerInstanciaProces(
			Long id,
			String processInstanceId) {
		logger.debug("Consulta de les agrupacions de dades de l'expedient (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		DefinicioProces definicioProces;
		if (processInstanceId == null) {
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
		}
		return conversioTipusHelper.convertirList(
				definicioProces.getAgrupacions(),
				CampAgrupacioDto.class);
	}

	@Override
	@Transactional
	public void deleteSignatura(
			Long expedientId,
			Long documentStoreId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		
		DocumentStore documentStore = documentStoreRepository.findById(documentStoreId);
		if (documentStore != null && documentStore.isSignat()) {
				pluginHelper.custodiaEsborrarSignatures(
						documentStore.getReferenciaCustodia(),
						expedient);
			String jbpmVariable = documentStore.getJbpmVariable();
			documentStore.setReferenciaCustodia(null);
			documentStore.setSignat(false);
			expedientRegistreHelper.crearRegistreEsborrarSignatura(
					expedient.getId(),
					expedient.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					getVarNameFromDocumentStore(documentStore));
			List<JbpmTask> tasks = jbpmHelper.findTaskInstancesForProcessInstance(expedient.getProcessInstanceId());
			for (JbpmTask task: tasks) {
				jbpmHelper.deleteTaskInstanceVariable(
						task.getId(),
						jbpmVariable);
			}
		}
	}

	private String getVarNameFromDocumentStore(DocumentStore documentStore) {
		String jbpmVariable = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return jbpmVariable.substring(
					JbpmVars.PREFIX_ADJUNT.length());
		else
			return jbpmVariable.substring(
					JbpmVars.PREFIX_DOCUMENT.length());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDocumentDto> findDocumentsPerInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consulta els documents de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		if (processInstanceId == null) {
			return documentHelper.findDocumentsPerInstanciaProces(
					expedient.getProcessInstanceId());
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			return documentHelper.findDocumentsPerInstanciaProces(
					processInstanceId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findDocumentPerInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String documentCodi) {
		logger.debug("Consulta un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ", " +
				"documentCodi=" + documentCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		if (processInstanceId == null) {
			return documentHelper.findDocumentPerInstanciaProces(
					expedient.getProcessInstanceId(),
					documentStoreId,
					documentCodi);
		} else {
			return documentHelper.findDocumentPerInstanciaProces(
					processInstanceId,
					documentStoreId,
					documentCodi);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public ExpedientDocumentDto findDocumentPerDocumentStoreId(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		if (processInstanceId == null) {
			return documentHelper.findDocumentPerDocumentStoreId(
					expedient.getProcessInstanceId(),
					documentStoreId);
		} else {
			return documentHelper.findDocumentPerDocumentStoreId(
					processInstanceId,
					documentStoreId);
		}
	}

	@Override
	@Transactional
	public void esborrarDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		logger.debug("Consulta un document de l'instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		
		if (processInstanceId == null) {
			documentHelper.esborrarDocument(
					null,
					expedient.getProcessInstanceId(),
					documentStoreId);
		} else {
			documentHelper.esborrarDocument(
					null,
					processInstanceId,
					documentStoreId);
		}
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> findListDocumentsPerDefinicioProces(
			Long definicioProcesId,
			String processInstanceId,
			String expedientTipusNom) {
		List<Document> documents = documentRepository.findAmbDefinicioProces(definicioProcesId);
		return conversioTipusHelper.convertirList(
				documents,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentDto findDocumentsPerId(Long id) {
		Document document = documentRepository.findOne(id);
		if (document == null)
			throw new NoTrobatException(Document.class, id);
		
		return conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getArxiuPerDocument(
			Long id,
			Long documentStoreId) {
		logger.debug("btenint contingut de l'arxiu per l'expedient (" +
				"id=" + id + ", " +
				"documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null) {
			throw new NoTrobatException(DocumentStore.class,documentStoreId);
		}
		expedientHelper.comprovarInstanciaProces(
				expedient,
				documentStore.getProcessInstanceId());
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				false);
	}

	@Override
	@Transactional
	public void aturar(
			Long id,
			String motiu) {
		logger.debug("Aturant la tramitació de l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		/*Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.STOP,
						ExtendedPermission.ADMINISTRATION});*/
		expedientHelper.aturar(
				expedient,
				motiu,
				null);
	}
	@Override
	@Transactional
	public void reprendre(Long id) {
		logger.debug("Reprenent la tramitació de l'expedient (" +
				"id=" + id + ")");
		/*Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.STOP,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		expedientHelper.reprendre(expedient, null);
	}

	@Override
	@Transactional
	public void anular(
			Long id,
			String motiu) {
		logger.debug("Anulant l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		/*Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.CANCEL,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		mesuresTemporalsHelper.mesuraIniciar(
				"Anular",
				"expedient",
				expedient.getTipus().getNom());
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.suspendProcessInstances(ids);
		expedient.setAnulat(true);
		expedient.setComentariAnulat(motiu);
		luceneHelper.deleteExpedient(expedient);
		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ANULAR);
		mesuresTemporalsHelper.mesuraCalcular(
				"Anular",
				"expedient",
				expedient.getTipus().getNom());
	}
	@Override
	@Transactional
	public void desanular(Long id) {
		logger.debug("Activant l'expedient (id=" + id + ")");
		/*Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.CANCEL,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Reprenent les instàncies de procés associades a l'expedient (id=" + id + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.resumeProcessInstances(ids);
		expedient.setAnulat(false);
	}

	@Override
	@Transactional
	public void desfinalitzar(Long id) {
		logger.debug("Reprenent l'expedient (id=" + id + ")");
		/*Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.UNDO_END,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		ExpedientLog expedientLog = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_DESFINALITZAR,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Desfer finalització de l'expedient (id=" + id + ")");
		jbpmHelper.desfinalitzarExpedient(expedient.getProcessInstanceId());
		expedient.setDataFi(null);
		expedientRegistreHelper.crearRegistreReprendreExpedient(
				expedient.getId(),
				(expedient.getResponsableCodi() != null) ? expedient.getResponsableCodi() : SecurityContextHolder.getContext().getAuthentication().getName());
	}

	@Override
	@Transactional
	public void relacioCreate(
			Long origenId,
			Long destiId) {
		logger.debug("Creant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		/*Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				false,
				true,
				false,
				false);
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				true,
				false,
				false,
				false);
		ExpedientLog expedientLogOrigen = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				origenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				destiId.toString());
		expedientLogOrigen.setEstat(ExpedientLogEstat.IGNORAR);
		ExpedientLog expedientLogDesti = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				destiId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				origenId.toString());
		expedientLogDesti.setEstat(ExpedientLogEstat.IGNORAR);
		expedientHelper.relacioCrear(origen, desti);
	}

	@Transactional
	@Override
	public void relacioDelete(
			Long origenId,
			Long destiId) {
		logger.debug("Esborrant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		/*Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				false,
				true,
				false,
				false);
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				false,
				false,
				false,
				false);
		ExpedientLog expedientLogOrigen = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				origenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				destiId.toString());
		expedientLogOrigen.setEstat(ExpedientLogEstat.IGNORAR);
		ExpedientLog expedientLogDesti = expedientLoggerHelper.afegirLogExpedientPerExpedient(
				destiId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				origenId.toString());
		expedientLogDesti.setEstat(ExpedientLogEstat.IGNORAR);
		origen.removeRelacioOrigen(desti);
		desti.removeRelacioOrigen(origen);		
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> relacioFindAmbExpedient(Long id) {
		logger.debug("Consulta d'expedients relacionats amb l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<ExpedientDto> list = new ArrayList<ExpedientDto>();
		for (Expedient relacionat: expedient.getRelacionsOrigen()) {			
			list.add(findAmbId(relacionat.getId()));
		}
		return list;
	}

	@Override
	@Transactional
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) {
		logger.debug("Executa script sobre l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"script=" + script + ")");
		/*Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.SCRIPT_EXE,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				false,
				false,
				true);
		expedientHelper.comprovarInstanciaProces(expedient, processInstanceId);
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (MesuresTemporalsHelper.isActiu()) {
			mesuresTemporalsHelper.mesuraIniciar("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
		}
		jbpmHelper.evaluateScript(processInstanceId, script, new HashSet<String>());
		verificarFinalitzacioExpedient(expedient, pi);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_SCRIPT_EXECUTAR,
				script);
		if (MesuresTemporalsHelper.isActiu())
			mesuresTemporalsHelper.mesuraCalcular("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
	}

	@Override
	@Transactional
	public void procesDefinicioProcesActualitzar(
			String processInstanceId,
			int versio) {
		logger.debug("Canviant versió de la definició de procés (" +
				"processInstanceId=" + processInstanceId + ", " +
				"versio=" + versio + ")");
		/*ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(processInstanceId);
		expedientHelper.getExpedientComprovantPermisos(
				piexp.getId(),
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});*/
		DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		if (defprocAntiga == null)
			throw new NoTrobatException(DefinicioProces.class, processInstanceId);
		jbpmHelper.changeProcessInstanceVersion(processInstanceId, versio);
		// Apunta els terminis iniciats cap als terminis
		// de la nova definició de procés
		DefinicioProces defprocNova = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		updateTerminis(processInstanceId, defprocAntiga, defprocNova);
	}

	@Override
	@Transactional
	public void procesDefinicioProcesCanviVersio(
			Long expedientId,
			Long definicioProcesId,
			Long[] subProcesIds,
			List<DefinicioProcesExpedientDto> subDefinicioProces) {
		logger.debug("Canviant versió de les definicións de procés de l'expedient (" +
				"expedientId" + expedientId + ", " +
				"definicioProcesId" + definicioProcesId + ", " +
				"subProcesIds=" + subProcesIds + ")");
		/*Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});*/
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		if (!expedient.isAmbRetroaccio()) {
			jbpmHelper.deleteProcessInstanceTreeLogs(expedient.getProcessInstanceId());
		}
		if (definicioProcesId != null) {
			DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
			DefinicioProces defprocNova = definicioProcesRepository.findById(definicioProcesId);
			if (!defprocAntiga.equals(defprocNova)) {
				jbpmHelper.changeProcessInstanceVersion(expedient.getProcessInstanceId(), defprocNova.getVersio());
				updateTerminis(expedient.getProcessInstanceId(), defprocAntiga, defprocNova);
			}
		}
		// Subprocessos
		if (subProcesIds != null && subProcesIds.length > 0) {
			// Arriben amb el mateix ordre??
			List<JbpmProcessInstance> instanciesProces = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
			for (JbpmProcessInstance instanciaProces: instanciesProces) {
				DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId());
				int versio = findVersioDefProcesActualitzar(subDefinicioProces, subProcesIds, instanciaProces.getProcessInstance().getProcessDefinition().getName());
				if (versio != -1 && versio != defprocAntiga.getVersio()) {
					jbpmHelper.changeProcessInstanceVersion(instanciaProces.getId(), versio);
					DefinicioProces defprocNova =  expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId());
					updateTerminis(instanciaProces.getId(), defprocAntiga, defprocNova);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public SortedSet<Entry<InstanciaProcesDto, List<ExpedientLogDto>>> registreFindLogsOrdenatsPerData(
			Long expedientId,
			boolean detall) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		Map<InstanciaProcesDto, List<ExpedientLogDto>> resposta = new HashMap<InstanciaProcesDto, List<ExpedientLogDto>>();
		List<InstanciaProcesDto> arbre = getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedient.getId());
		List<String> taskIds = new ArrayList<String>();
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		for (InstanciaProcesDto ip : arbre) {
			resposta.put(ip, new ArrayList<ExpedientLogDto>());
			for (ExpedientLog log: logs) {
				if (log.getProcessInstanceId().toString().equals(ip.getId())) {
					// Inclourem el log si:
					//    - Estam mostrant el log detallat
					//    - El log no se correspon a una tasca
					//    - Si el log pertany a una tasca i encara
					//      no s'ha afegit cap log d'aquesta tasca 
					if (detall || !log.isTargetTasca() || !taskIds.contains(log.getTargetId())) {
						taskIds.add(log.getTargetId());				
						resposta.get(ip).addAll(
								getLogs(
										processos,
										log,
										parentProcessInstanceId,
										ip.getId(),
										detall));
					}
				}
			}
		}
		SortedSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>> sortedEntries = new TreeSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>(new Comparator<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>() {
			@Override
			public int compare(Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e1, Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e2) {
				if (e1.getKey() == null || e2.getKey() == null)
					return 0;
				int res = e1.getKey().getId().compareTo(e2.getKey().getId());
				if (e1.getKey().getId().equals(e2.getKey().getId())) {
					return res;
				} else {
					return res != 0 ? res : 1;
				}
			}
		});
		sortedEntries.addAll(resposta.entrySet());
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens tasca");
		return sortedEntries;
	}	

	@Override
	@Transactional(readOnly=true)
	public Map<String, ExpedientTascaDto> registreFindTasquesPerLogExpedient(
			Long expedientId) {
		logger.debug("Consultant tasques l'expedient (expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		List<ExpedientLog> logs = expedientLogRepository.findAmbExpedientIdOrdenatsPerData(expedientId);
		Map<String, ExpedientTascaDto> tasquesPerLogs = new HashMap<String, ExpedientTascaDto>();
		for (ExpedientLog log: logs) {
			if (log.isTargetTasca()) {
				JbpmTask task = jbpmHelper.getTaskById(log.getTargetId());
				if (task != null) {
					tasquesPerLogs.put(
							log.getTargetId(),
							tascaHelper.toExpedientTascaDto(
									task,
									expedient,
									true,
									false));
				}
			}
		}
		return tasquesPerLogs;
	}

	@Override
	@Transactional
	public void registreRetrocedir(
			Long expedientId,
			Long expedientLogId,
			boolean retrocedirPerTasques) {
		ExpedientLog log = expedientLogRepository.findById(expedientLogId);
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
		if (log.getExpedient().isAmbRetroaccio()) {
			ExpedientLog logRetroces = expedientLoggerHelper.afegirLogExpedientPerExpedient(
					log.getExpedient().getId(),
					retrocedirPerTasques ? ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR,
					expedientLogId.toString());
			expedientLoggerHelper.retrocedirFinsLog(log, retrocedirPerTasques, logRetroces.getId());
			logRetroces.setEstat(ExpedientLogEstat.IGNORAR);
			indexHelper.expedientIndexLuceneUpdate(
					log.getExpedient().getProcessInstanceId());
		}
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", log.getExpedient().getTipus().getNom());
	}

	@Override
	@Transactional
	public void registreBuidarLog(
			Long expedientId) {
		logger.debug("Buidant logs de l'expedient("
				+ "expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		jbpmHelper.deleteProcessInstanceTreeLogs(expedient.getProcessInstanceId());
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> registreFindLogsTascaOrdenatsPerData(
			Long expedientId,
			Long logId) {
		List<ExpedientLog> logs = expedientLogRepository.findLogsTascaByIdOrdenatsPerData(String.valueOf(logId));
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
	}

	@Override
	@Transactional(readOnly=true)
	public List<ExpedientLogDto> registreFindLogsRetroceditsOrdenatsPerData(
			Long expedientId,
			Long logId) {
		List<ExpedientLog> logs = expedientLoggerHelper.findLogsRetrocedits(logId);
		return conversioTipusHelper.convertirList(logs, ExpedientLogDto.class);
	}

	@Override
	@Transactional(readOnly=true)
	public ExpedientLogDto registreFindLogById(
			//Long expedientId,
			Long logId) {
		return conversioTipusHelper.convertir( expedientLogRepository.findById(logId), ExpedientLogDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AlertaDto> findAlertes(Long id) {
		logger.debug("Consulta alertes de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<Alerta> alertes = alertaRepository.findByExpedientAndDataEliminacioNull(expedient);
		// Convertir a AlertaDto
		return conversioTipusHelper.convertirList(alertes, AlertaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Object[] findErrorsExpedient(Long id) {
		logger.debug("Consulta errors de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<Portasignatures> portasignatures = portasignaturesRepository.findByExpedientAndEstat(expedient, TipusEstat.ERROR);
		List<ExpedientErrorDto> errors_int = new ArrayList<ExpedientErrorDto>();
		
		if(!portasignatures.isEmpty()){
			for(Portasignatures ps: portasignatures) {
				errors_int.add(new ExpedientErrorDto(ErrorTipusDto.INTEGRACIONS, ps.getErrorCallbackProcessant()));
			}
		}
		
		List<ExpedientErrorDto> errors_bas = new ArrayList<ExpedientErrorDto>();
		if (expedient.getErrorDesc() != null) {
			errors_bas.add(new ExpedientErrorDto(ErrorTipusDto.BASIC, expedient.getErrorDesc(), expedient.getErrorFull()));
		}
		
		if (expedient.isReindexarError()) {
			errors_bas.add(new ExpedientErrorDto(ErrorTipusDto.BASIC, 
					messageHelper.getMessage("expedient.consulta.reindexacio.error"),
					messageHelper.getMessage("expedient.consulta.reindexacio.error.full")));
		}
		
		return new Object[]{errors_bas,errors_int};
	}

	private int findVersioDefProcesActualitzar(List<DefinicioProcesExpedientDto> definicionsProces, Long[] definicionsProcesId, String key) {
		int versio = -1;
		int i = 0;
		while (i < definicionsProces.size() && !definicionsProces.get(i).getJbpmKey().equals(key)) 
			i++;
		if (i < definicionsProces.size() && definicionsProcesId[i] != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findById(definicionsProcesId[i]);
			if (definicioProces != null) 
				versio = definicioProces.getVersio();
		}
		return versio;
	}
	// Apunta els terminis iniciats cap als terminis de la nova definició de procés
	private void updateTerminis(String procesInstanceId, DefinicioProces defprocAntiga, DefinicioProces defprocNova) {
		List<TerminiIniciat> terminisIniciats = terminiIniciatRepository.findByProcessInstanceId(procesInstanceId);
		for (TerminiIniciat terminiIniciat: terminisIniciats) {
			Termini termini = terminiIniciat.getTermini();
			if (termini.getDefinicioProces().getId().equals(defprocAntiga.getId())) {
				for (Termini terminiNou: defprocNova.getTerminis()) {
					if (terminiNou.getCodi().equals(termini.getCodi())) {
						termini.removeIniciat(terminiIniciat);
						terminiNou.addIniciat(terminiIniciat);
						terminiIniciat.setTermini(terminiNou);
						break;
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornId, String text) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		List<Expedient> expedients = expedientRepository.findAmbEntornLikeIdentificador(entornId, text);
		for (Expedient expedient : expedients) {
			resposta.add(conversioTipusHelper.convertir(expedient,ExpedientDto.class));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<InstanciaProcesDto> getArbreInstanciesProces(
				Long processInstanceId) {
		List<InstanciaProcesDto> resposta = new ArrayList<InstanciaProcesDto>();
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(String.valueOf(processInstanceId));
		List<JbpmProcessInstance> piTree = jbpmHelper.getProcessInstanceTree(rootProcessInstance.getId());
		for (JbpmProcessInstance jpi: piTree) {
			resposta.add(getInstanciaProcesById(jpi.getId()));
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(
			final Long consultaId, 
			Map<String, Object> valorsPerService,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			 final PaginacioParamsDto paginacioParams) {
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");
		
		final List<ExpedientConsultaDissenyDto> expedientsConsultaDisseny = findConsultaDissenyPaginat(
			consultaId,
			valorsPerService,
			paginacioParams,
			nomesMeves, 
			nomesAlertes, 
			mostrarAnulats,
			nomesTasquesPersonals,
			nomesTasquesGrup,
			null
		);
		
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "1");
		
		final int numExpedients= findIdsPerConsultaInforme(
				consultaId,
				valorsPerService, 
				nomesMeves, 
				nomesAlertes, 
				mostrarAnulats,
				nomesTasquesPersonals,
				nomesTasquesGrup
			).size();
		
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "1");
		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "2");
		
		Page<ExpedientConsultaDissenyDto> paginaResultats = new Page<ExpedientConsultaDissenyDto>() {
			
			@Override
			public Iterator<ExpedientConsultaDissenyDto> iterator() {
				return getContent().iterator();
			}
			
			@Override
			public boolean isLastPage() {
				return false;
			}
			
			@Override
			public boolean isFirstPage() {
				return paginacioParams.getPaginaNum() == 0;
			}
			
			@Override
			public boolean hasPreviousPage() {
				return paginacioParams.getPaginaNum() > 0;
			}
			
			@Override
			public boolean hasNextPage() {
				return false;
			}
			
			@Override
			public boolean hasContent() {
				return !expedientsConsultaDisseny.isEmpty();
			}
			
			@Override
			public int getTotalPages() {
				return 0;
			}
			
			@Override
			public long getTotalElements() {
				return numExpedients;
			}
			
			@Override
			public Sort getSort() {
				List<Order> orders = new ArrayList<Order>();
				for (OrdreDto or : paginacioParams.getOrdres()) {
					orders.add(new Order(or.getDireccio().equals(OrdreDireccioDto.ASCENDENT) ? Direction.ASC : Direction.DESC, or.getCamp()));
				}
				return new Sort(orders);
			}
			
			@Override
			public int getSize() {
				return paginacioParams.getPaginaTamany();
			}
			
			@Override
			public int getNumberOfElements() {
				return 0;
			}
			
			@Override
			public int getNumber() {
				return 0;
			}
			
			@Override
			public List<ExpedientConsultaDissenyDto> getContent() {				
				return expedientsConsultaDisseny;
			}
		};

		PaginaDto<ExpedientConsultaDissenyDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientConsultaDissenyDto.class);
		
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "2");
		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta");
		return resposta;
	}

	@Override
	public boolean isExtensioDocumentPermesa(String nomArxiu) {
		return (new Document()).isExtensioPermesa(getExtension(nomArxiu));
	}

	private String getExtension(String nomArxiu) {
		int index = nomArxiu.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return nomArxiu.substring(index + 1);
		}
	}

	@Override
	@Transactional
	public ArxiuDto generarDocumentAmbPlantillaTasca(
			String taskInstanceId,
			String documentCodi) {
		JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
				taskInstanceId,
				true,
				true);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
				task.getProcessInstanceId());
		Document document = documentRepository.findByDefinicioProcesAndCodi(
				expedientHelper.findDefinicioProcesByProcessInstanceId(
						task.getProcessInstanceId()),
				documentCodi);
		Date documentData = new Date();
		ArxiuDto arxiu = documentHelper.generarDocumentAmbPlantillaIConvertir(
				expedient,
				document,
				taskInstanceId,
				task.getProcessInstanceId(),
				documentData);
		if (document.isAdjuntarAuto()) {
			documentHelper.actualitzarDocument(
					taskInstanceId,
					task.getProcessInstanceId(),
					document.getCodi(),
					null,
					documentData,
					arxiu.getNom(),
					arxiu.getContingut(),
					false);
			return null;
		} else {
			return arxiu;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto generarDocumentAmbPlantillaProces(
			Long expedientId,
			String processInstanceId,
			String documentCodi) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		if (processInstanceId != null) {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
		}
		Document document = documentRepository.findByDefinicioProcesAndCodi(
				expedientHelper.findDefinicioProcesByProcessInstanceId(
						processInstanceId),
				documentCodi);
		Date documentData = new Date();
		return documentHelper.generarDocumentAmbPlantillaIConvertir(
				expedient,
				document,
				null,
				(processInstanceId != null) ? processInstanceId : expedient.getProcessInstanceId(),
				documentData);
	}

	@Override
	@Transactional(readOnly=true)
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (pi.getProcessInstance() == null)
			return null;
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		if (pi.getDescription() != null && pi.getDescription().length() > 0)
			dto.setTitol(pi.getDescription());
		dto.setDefinicioProces(conversioTipusHelper.convertir(definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()), DefinicioProcesDto.class));
		return dto;
	}

	@Override
	@Transactional(readOnly=true)
	public List<CampDto> getCampsInstanciaProcesById(String processInstanceId) {
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);
		List<Camp> camps = new ArrayList<Camp>(definicioProces.getCamps());
		return conversioTipusHelper.convertirList(camps, CampDto.class);
	}

	private List<ExpedientLogDto> getLogs(
			Map<String, String> processos,
			ExpedientLog log,
			String parentProcessInstanceId,
			String piId,
			boolean detall) {
		// Obtenim el token de cada registre
		JbpmToken token = null;
		if (log.getJbpmLogId() != null) {
			token = expedientLoggerHelper.getTokenByJbpmLogId(log.getJbpmLogId());
		}
		String tokenName = null;
		String processInstanceId = null;
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		if (token != null && token.getToken() != null) {
			tokenName = token.getToken().getFullName();
			processInstanceId = token.getProcessInstanceId();
			
			// Entram per primera vegada
			if (parentProcessInstanceId == null) {
				parentProcessInstanceId = processInstanceId;
				processos.put(processInstanceId, "");
			} else {
				// Canviam de procés
				if (!parentProcessInstanceId.equals(token.getProcessInstanceId())){
					// Entram en un nou subproces
					if (!processos.containsKey(processInstanceId)) {
						processos.put(processInstanceId, token.getToken().getProcessInstance().getSuperProcessToken().getFullName());
						
						if (parentProcessInstanceId.equals(piId)){
							// Añadimos una nueva línea para indicar la llamada al subproceso
							ExpedientLogDto dto = new ExpedientLogDto();
							dto.setId(log.getId());
							dto.setData(log.getData());
							dto.setUsuari(log.getUsuari());
							dto.setEstat(ExpedientLogEstat.IGNORAR.name());
							dto.setAccioTipus(ExpedientLogAccioTipus.PROCES_LLAMAR_SUBPROCES.name());
							String titol = null;
							if (token.getToken().getProcessInstance().getKey() == null)
								titol = token.getToken().getProcessInstance().getProcessDefinition().getName() + " " + log.getProcessInstanceId();
							else 
								titol = token.getToken().getProcessInstance().getKey();
							dto.setAccioParams(titol);
							dto.setTargetId(log.getTargetId());
							dto.setTargetTasca(false);
							dto.setTargetProces(false);
							dto.setTargetExpedient(true);
							if (detall || (!("RETROCEDIT".equals(dto.getEstat()) || "RETROCEDIT_TASQUES".equals(dto.getEstat()) || "EXPEDIENT_MODIFICAR".equals(dto.getAccioTipus()))))
								resposta.add(dto);
						}
					}
				}
				tokenName = processos.get(processInstanceId) + tokenName;
			}
		}
		
		if (piId == null || log.getProcessInstanceId().equals(Long.parseLong(piId))) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(token == null ? ExpedientLogEstat.IGNORAR.name() : log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTokenName(tokenName);
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			if (detall || (!("RETROCEDIT".equals(dto.getEstat()) || "RETROCEDIT_TASQUES".equals(dto.getEstat()) || "EXPEDIENT_MODIFICAR".equals(dto.getAccioTipus()))))
				resposta.add(dto);
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId);		
		
		List<TascaDadaDto> listTascaDada = consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		
		// Quitamos las variables predefinidas de los filtros
		Iterator<TascaDadaDto> itListTascaDada = listTascaDada.iterator();
		while(itListTascaDada.hasNext()) {
			if (consulta.getMapValorsPredefinits().containsKey(itListTascaDada.next().getVarCodi())) {
				itListTascaDada.remove();
			}
		}
		
		return listTascaDada;
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
	}

	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		Consulta consulta = consultaRepository.findById(consultaId);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.PARAM);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(
			Long consultaId,
			Map<String, Object> valors,
			PaginacioParamsDto paginacioParams,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			Set<Long> ids) {
		Consulta consulta = consultaRepository.findById(consultaId);		
		
		List<Camp> campsFiltre = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		
		List<Camp> campsInforme = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME));
		
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		
		String sort = "expedient$identificador"; //ExpedientCamps.EXPEDIENT_CAMP_ID;
		boolean asc = false;
		int firstRow = 0;
		int maxResults = -1;
		
		if (paginacioParams != null) {
			for (OrdreDto or : paginacioParams.getOrdres()) {
				asc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
				String clau = or.getCamp().replace(
						ExpedientCamps.EXPEDIENT_PREFIX_JSP,
						ExpedientCamps.EXPEDIENT_PREFIX);
				if (or.getCamp().contains("dadesExpedient")) {
					sort = clau.replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort = clau.replace(
							".",
							ExpedientCamps.EXPEDIENT_PREFIX_SEPARATOR);
				}
				break;
			}
			firstRow = paginacioParams.getPaginaNum()*paginacioParams.getPaginaTamany();
			maxResults = paginacioParams.getPaginaTamany();
		}
		List<Long> llistaExpedientIds = new ArrayList<Long>();
		if (ids == null || ids.isEmpty()) {
			llistaExpedientIds = luceneHelper.findIdsAmbDadesExpedientPaginatV3(
					consulta.getEntorn(),
					consulta.getExpedientTipus(),
					campsFiltre,
					campsInforme,
					valors,
					sort,
					asc,
					0,
					-1);
		} else {
			llistaExpedientIds.addAll(ids);
		}
		boolean filtreTasques = nomesMeves || nomesTasquesPersonals || nomesTasquesGrup;
		if (filtreTasques) {
			filtrarExpedientsAmbTasques(
					llistaExpedientIds,
					nomesMeves,
					nomesAlertes,
					mostrarAnulats,
					nomesTasquesPersonals,
					nomesTasquesGrup);
		}
		List<Map<String, DadaIndexadaDto>> dadesExpedients = new ArrayList<Map<String,DadaIndexadaDto>>();
		if (!llistaExpedientIds.isEmpty())
			dadesExpedients = luceneHelper.findAmbDadesExpedientPaginatV3(
					consulta.getEntorn().getCodi(),
					llistaExpedientIds,
					campsInforme,
					sort,
					asc,
					firstRow,
					maxResults);
		
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.findOne(Long.parseLong(dadaExpedientId.getValorIndex()));
			if (expedient != null) {
				ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
				expedientHelper.omplirPermisosExpedient(expedientDto);
				fila.setExpedient(expedientDto);
				consultaHelper.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						campsInforme,
						expedient);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
		}

		if (paginacioParams == null) {
			Iterator<Map<String, DadaIndexadaDto>> it = dadesExpedients.iterator();
			while (it.hasNext()) {
				Map<String, DadaIndexadaDto> dadesExpedient = it.next();
				DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
				if (dadaExpedientId != null && !llistaExpedientIds.contains(Long.parseLong(dadaExpedientId.getValorIndex()))) {
					it.remove();
				}
			}
		}
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsPerConsultaInforme(
			Long consultaId,
			Map<String, Object> valors,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		Consulta consulta = consultaRepository.findById(consultaId);
		
		List<Camp> campsFiltre = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		
		List<Long> llistaExpedientIds = luceneHelper.findNomesIds(
				consulta.getEntorn(),
				consulta.getExpedientTipus(),
				campsFiltre,
				valors);
		boolean filtreTasques = nomesMeves || nomesTasquesPersonals || nomesTasquesGrup;
		if (filtreTasques) {
			filtrarExpedientsAmbTasques(
					llistaExpedientIds,
					nomesMeves,
					nomesAlertes,
					mostrarAnulats,
					nomesTasquesPersonals,
					nomesTasquesGrup);
		}
		return llistaExpedientIds;
	}
	
	private void filtrarExpedientsAmbTasques(
			List<Long> llistaExpedientIds,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		Set<Long> ids1 = null;
		Set<Long> ids2 = null;
		Set<Long> ids3 = null;
		Set<Long> ids4 = null;
		Set<Long> ids5 = null;
		int index = 0;
		for (Long id: llistaExpedientIds) {
			if (index == 0)
				ids1 = new HashSet<Long>();
			if (index == 1000)
				ids2 = new HashSet<Long>();
			if (index == 2000)
				ids3 = new HashSet<Long>();
			if (index == 3000)
				ids4 = new HashSet<Long>();
			if (index == 4000)
				ids5 = new HashSet<Long>();
			if (index < 1000)
				ids1.add(id);
			else if (index < 2000)
				ids2.add(id);
			else if (index < 3000)
				ids3.add(id);
			else if (index < 4000)
				ids4.add(id);
			else
				ids5.add(id);
			index++;
		}
		
		List<Object[]> idsInstanciesProces = expedientRepository.findAmbIdsByFiltreConsultesTipus(
				ids1,
				ids2,
				ids3,
				ids4,
				ids5,
				mostrarAnulats,
				nomesAlertes);
		
		List<String> idsPI = new ArrayList<String>();
		List<Long> idsExp = new ArrayList<Long>();
		for (Object[] id : idsInstanciesProces) {
			idsExp.add((Long) id[0]);
			idsPI.add((String) id[1]);
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		List<String> ids = jbpmHelper.findRootProcessInstancesWithTasksCommand(
						auth.getName(),
						idsPI,
						nomesMeves,
						nomesTasquesPersonals,
						nomesTasquesGrup);		
		
		Iterator<Long> itExps = llistaExpedientIds.iterator();
		ArrayList<Long> removeList = new ArrayList<Long>();
		while (itExps.hasNext()) {
			Long elem = itExps.next();
			int pos = idsExp.indexOf(elem);
			if (pos == -1 || !ids.contains(idsPI.get(pos))) {
				removeList.add(elem);
			}
		}
		llistaExpedientIds.removeAll(removeList);
	}

	@Override
	@Transactional(readOnly=true)
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		return !expedientRepository.findByEntornIdAndTipusIdAndTitol(entornId, expedientTipusId, titol == null, titol).isEmpty();
	}	
	
	@Override
	@Transactional
	public void suspendreTasca(
			Long expedientId,
			Long taskId) {
		logger.debug("Reprende tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		if (!expedient.isAnulat()) {
			JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
			expedientLoggerHelper.afegirLogExpedientPerProces(
					task.getProcessInstanceId(),
					ExpedientLogAccioTipus.TASCA_SUSPENDRE,
					null);
			jbpmHelper.suspendTaskInstance(String.valueOf(taskId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(taskId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.ATURAR);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	@Override
	@Transactional(readOnly=true)
	public String getNumeroExpedientActual(
			Long entornId,
			Long expedientTipusId,
			Integer any) {
		logger.debug("Consulta del número d'expedient pel tipus d'expedient(" +
				"entornId" + entornId + ", " +
				"expedientTipusId" + expedientTipusId + ", " +
				"any=" + any + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		return this.getNumeroExpedientActual(
				entorn,
				expedientTipus,
				any);
	}

	@Override
	@Transactional(readOnly=true)
	public ExpedientTascaDto getStartTask(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.findById(definicioProcesId);
		}

		if (definicioProces == null){
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + entornId + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = jbpmHelper.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null) {
			return tascaHelper.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
		}
		return null;
	}
	
	@Override
	@Transactional
	public void reprendreTasca(
			Long expedientId,
			Long taskId) {
		logger.debug("Reprende tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		if (!expedient.isAnulat()) {
			JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
			expedientLoggerHelper.afegirLogExpedientPerProces(
					task.getProcessInstanceId(),
					ExpedientLogAccioTipus.TASCA_CONTINUAR,
					null);
			jbpmHelper.resumeTaskInstance(String.valueOf(taskId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(taskId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.REPRENDRE);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}
	
	@Override
	@Transactional
	public void cancelarTasca(
			Long expedientId,
			Long taskId) {
		logger.debug("Cancelar tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		if (!expedient.isAnulat()) {
			JbpmTask task = jbpmHelper.getTaskById(String.valueOf(taskId));
			expedientLoggerHelper.afegirLogExpedientPerProces(
					task.getProcessInstanceId(),
					ExpedientLogAccioTipus.TASCA_CANCELAR,
					null);
			jbpmHelper.cancelTaskInstance(String.valueOf(taskId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(taskId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.CANCELAR);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	@Override
	@Transactional
	public void reassignarTasca(String taskId, String expression) {
		JbpmTask task = jbpmHelper.getTaskById(taskId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		logger.debug("Reassignar tasca l'expedient (id=" + expedient.getId() + ")");
		
		String previousActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
		ExpedientLog expedientLog = null;
		expedientLog = expedientLoggerHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				null);
		expedientHelper.getExpedientComprovantPermisos(
				expedientLog.getExpedient().getId(),
				false,
				false,
				false,
				false,
				true,
				false,
				false);
		if (!expedient.isAturat()) {
			jbpmHelper.reassignTaskInstance(taskId, expression, expedient.getEntorn().getId());
			String currentActors = expedientLoggerHelper.getActorsPerReassignacioTasca(taskId);
			expedientLog.setAccioParams(previousActors + "::" + currentActors);
			String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			crearRegistreRedirigirTasca(
					expedient.getId(),
					taskId,
					usuari,
					expression);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientConsultaDissenyDto> consultaFindPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			Set<Long> expedientIdsSeleccio,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta general d'expedients paginada (" +
				"consultaId=" + consultaId + ", " +
				"filtreValors=" + filtreValors + ", " +
				"expedientIdsSeleccio=" + expedientIdsSeleccio + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesMeves=" + nomesMeves + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"paginacioParams=" + paginacioParams + ")");
//		
//		// Mètriques - Timers
//		Timer.Context contextConsultaLuceneTotal = null;
//		Timer.Context contextConsultaMongoTotal = null;
//		
//		final Timer timerConsultaLuceneTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "consulta.lucene"));
//		final Timer timerConsultaMongoTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "consulta.mongoDB"));
//		
//		Counter countTotal = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "consulta.count"));
//		countTotal.inc();
//		
		// Comprova l'accés a la consulta
		Consulta consulta = consultaRepository.findById(consultaId);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class,consultaId);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					consulta.getExpedientTipus().getId(),
					true);
		// Obte la llista d'expedients permesos
		List<Long> expedientIdsPermesos;
		if (expedientIdsSeleccio != null && !expedientIdsSeleccio.isEmpty()) {
			expedientIdsPermesos = new ArrayList<Long>(expedientIdsSeleccio);
		} else {
			List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
					entorn.getId(),
					auth.getName(),
					tipusPermesosIds,
					null,
					null,
					expedientTipus.getId(),
					null,
					null,
					null,
					null,
					null,
					null,
					false,
					false,
					MostrarAnulatsDto.SI.equals(mostrarAnulats),
					MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
					nomesAlertes,
					nomesErrors,
					nomesTasquesPersonals,
					nomesTasquesGrup,
					nomesMeves,
					new PaginacioParamsDto(),
					false);
			expedientIdsPermesos = expedientsIds.getLlista();
		}
		// Obte la llista d'expedients de lucene passant els expedients permesos
		// com a paràmetres
		List<Camp> filtreCamps = consultaHelper.toListCamp(
				consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		afegirValorsPredefinits(consulta, filtreValors, filtreCamps);
		List<Camp> informeCamps = consultaHelper.toListCamp(
				consultaHelper.findCampsPerCampsConsulta(
						consulta,
						TipusConsultaCamp.INFORME));

		Object[] respostaLucene = null;
		
//		boolean ctxLuceneStoped = false;
//		try {
//			contextConsultaLuceneTotal = timerConsultaLuceneTotal.time();
			
			respostaLucene = luceneHelper.findPaginatAmbDadesV3(
					entorn,
					expedientTipus,
					expedientIdsPermesos,
					filtreCamps,
					filtreValors,
					informeCamps,
					paginacioParams);
			
//			contextConsultaLuceneTotal.stop();
//			ctxLuceneStoped = true;
//			contextConsultaMongoTotal = timerConsultaMongoTotal.time();
//			
//			mongoDBHelper.findPaginatAmbDadesV3(
//					expedientIdsPermesos, 
//					filtreCamps, 
//					filtreValors, 
//					informeCamps, 
//					paginacioParams);
//		} finally {
//			if (!ctxLuceneStoped) {
//				contextConsultaLuceneTotal.stop();
//			}
//			contextConsultaMongoTotal.stop();
//		}
		
		@SuppressWarnings("unchecked")
		List<Map<String, DadaIndexadaDto>> dadesExpedients = (List<Map<String, DadaIndexadaDto>>)respostaLucene[0];
		Long count = (Long)respostaLucene[1];
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.findOne(Long.parseLong(dadaExpedientId.getValorIndex()));
			if (expedient != null) {
				ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
				expedientHelper.omplirPermisosExpedient(expedientDto);
				fila.setExpedient(expedientDto);
				consultaHelper.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						informeCamps,
						expedient);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
		}
		return paginacioHelper.toPaginaDto(
				resposta,
				count.intValue(),
				paginacioParams);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<Long> consultaFindNomesIdsPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta general d'expedients paginada (" +
				"consultaId=" + consultaId + ", " +
				"filtreValors=" + filtreValors + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesMeves=" + nomesMeves + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"paginacioParams=" + paginacioParams + ")");
		// Comprova l'accés a la consulta
		Consulta consulta = consultaRepository.findById(consultaId);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class,consultaId);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					consulta.getExpedientTipus().getId(),
					true);
		// Obte la llista d'expedients permesos segons els filtres
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ResultatConsultaPaginadaJbpm<Long> expedientsIds = jbpmHelper.expedientFindByFiltre(
				entorn.getId(),
				auth.getName(),
				tipusPermesosIds,
				null,
				null,
				expedientTipus.getId(),
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				false,
				MostrarAnulatsDto.SI.equals(mostrarAnulats),
				MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesMeves,
				new PaginacioParamsDto(),
				false);
		// Obte la llista d'ids de lucene passant els expedients permesos
		// com a paràmetres
		List<Camp> filtreCamps = consultaHelper.toListCamp(
				consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		afegirValorsPredefinits(consulta, filtreValors, filtreCamps);
		Object[] respostaLucene = luceneHelper.findPaginatNomesIdsV3(
				entorn,
				expedientTipus,
				expedientsIds.getLlista(),
				filtreCamps,
				filtreValors,
				paginacioParams);
		@SuppressWarnings("unchecked")
		List<Long> ids = (List<Long>)respostaLucene[0];
		Long count = (Long)respostaLucene[1];
		return paginacioHelper.toPaginaDto(
				ids,
				count.intValue(),
				paginacioParams);
	}

	@SuppressWarnings("unused")
	@Override
	@Transactional
	public void createVariable(Long expedientId, String processInstanceId, String varName, Object value) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR,
				varName);
		optimitzarValorPerConsultesDominiGuardar(processInstanceId, varName, value);
		
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Crear variable '" + varName + "'");
		if (value != null)
			registre.setValorNou(value.toString());
	}
	
	@SuppressWarnings("unused")
	@Override
	@Transactional
	public void updateVariable(Long expedientId, String processInstanceId, String varName, Object varValue) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				varName);
		Object valorVell = variableHelper.getVariableJbpmProcesValor(processInstanceId, varName);
		optimitzarValorPerConsultesDominiGuardar(processInstanceId, varName, varValue);
		
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Modificar variable '" + varName + "'");
		if (valorVell != null)
			registre.setValorVell(valorVell.toString());
		if (varValue != null)
			registre.setValorNou(varValue.toString());
	}
	
	private void optimitzarValorPerConsultesDominiGuardar(
			String processInstanceId,
			String varName,
			Object varValue) {
		JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		Camp camp = campRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				varName);
		if (camp != null && camp.isDominiCacheText()) {
			if (varValue != null) {
				if (camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
					
					String text;
					try {
						text = variableHelper.getTextPerCamp(
								camp, 
								varValue, 
								null, 
								processInstanceId);
					} catch (Exception e) {
						text = "";
					}
					
					jbpmHelper.setProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
		jbpmHelper.setProcessInstanceVariable(processInstanceId, varName, varValue);
	}
	
	@SuppressWarnings("unused")
	@Override
	@Transactional
	public void deleteVariable(Long expedientId, String processInstanceId, String varName) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_ESBORRAR,
				varName);
		jbpmHelper.deleteProcessInstanceVariable(processInstanceId, varName);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Esborrar variable '" + varName + "'");
	}
	
	private void verificarFinalitzacioExpedient(Expedient expedient, JbpmProcessInstance pi) {
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatRepository.findByProcessInstanceId(pi.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						jbpmHelper.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
	}

	private void afegirValorsPredefinits(
			Consulta consulta,
			Map<String, Object> valors,
			List<Camp> campsFiltre) {
		for (Camp camp: campsFiltre) {
			if (consulta.getMapValorsPredefinits().containsKey(camp.getCodi())) {
				valors.put(
						camp.getDefinicioProces()!= null ? camp.getDefinicioProces().getJbpmKey() + "." + camp.getCodi() : camp.getCodi(),
						Camp.getComObject(
								camp.getTipus(),
								consulta.getMapValorsPredefinits().get(camp.getCodi())));
			}
		}
	}

	private Registre crearRegistreRedirigirTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String expression) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge("Redirecció de tasca amb expressió \"" + expression + "\"");
		return registreRepository.save(registre);
	}
	
	public boolean isUserInRole(
			Authentication auth,
			String role) {
		for (GrantedAuthority ga: auth.getAuthorities()) {
			if (role.equals(ga.getAuthority()))
				return true;
		}
		return false;
	}

	private String getNumeroExpedientActual(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = expedientHelper.getNumeroExpedientActual(
					expedientTipus,
					any.intValue(),
					increment);
			expedient = expedientRepository.findByEntornIdAndTipusIdAndNumero(
					entorn.getId(),
					expedientTipus.getId(),
					numero);
			increment++;
		} while (expedient != null);
		return numero;
	}
	
	private String getNumeroExpedientDefaultActual(
			Entorn entorn,
			ExpedientTipus expedientTipus,
			Integer any) {
		long increment = 0;
		String numero = null;
		Expedient expedient = null;
		if (any == null) 
			any = Calendar.getInstance().get(Calendar.YEAR);
		do {
			numero = expedientHelper.getNumeroExpedientDefaultActual(
					expedientTipus,
					any.intValue(),
					increment);
			expedient = expedientRepository.findByEntornIdAndTipusIdAndNumero(
					entorn.getId(),
					expedientTipus.getId(),
					numero);
			increment++;
		} while (expedient != null);
		if (increment > 1) {
			expedientTipus.updateSequenciaDefault(any, increment - 1);
		}
		return numero;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero) {
		return expedientRepository.findByEntornIdAndTipusIdAndNumero(
				entornId,
				expedientTipusId,
				numero) != null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long findDocumentStorePerInstanciaProcesAndDocumentCodi(String processInstanceId, String documentCodi) {
		return documentHelper.findDocumentStorePerInstanciaProcesAndDocumentCodi(processInstanceId, documentCodi);
	}
	
	private PersonaDto comprovarUsuari(String usuari) {
		try {
			PersonaDto persona = pluginHelper.personaFindAmbCodi(usuari);
			return persona;
		} catch (Exception ex) {
			logger.error("No s'ha pogut comprovar l'usuari (codi=" + usuari + ")");
			throw new NoTrobatException(PersonaDto.class,usuari);
		}
	}

	private boolean permetreExecutarAccioExpedient(Accio accio, Long expedientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean permesa = true;
		
		if (!accio.isPublica()) {
			try {
				expedientHelper.getExpedientComprovantPermisos(
					expedientId,
					false,
					true,
					false,
					false);
			} catch (PermisDenegatException ex) {
				permesa =  false;
			}
		}
		
		if (permesa && accio.getRols() != null) {
			permesa = false;
			for (String rol: accio.getRols().split(",")) {
				if (isUserInRole(auth, rol)) {
					permesa = true;
					break;
				}
			}
		}
		
		return permesa;
	}

	private void verificarFinalitzacioExpedient(
			String processInstanceId,
			Expedient expedient) {
		JbpmProcessInstance pi = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (pi.getEnd() != null) {
			// Actualitzar data de fi de l'expedient
			expedient.setDataFi(pi.getEnd());
			// Finalitzar terminis actius
			for (TerminiIniciat terminiIniciat: terminiIniciatRepository.findByProcessInstanceId(pi.getId())) {
				if (terminiIniciat.getDataInici() != null) {
					terminiIniciat.setDataCancelacio(new Date());
					long[] timerIds = terminiIniciat.getTimerIdsArray();
					for (int i = 0; i < timerIds.length; i++)
						jbpmHelper.suspendTimer(
								timerIds[i],
								new Date(Long.MAX_VALUE));
				}
			}
		}
	}
	

	@Override
	@Transactional(readOnly = true)
	public List<PortasignaturesDto> findDocumentsPendentsPortasignatures(String processInstanceId) {
		List<PortasignaturesDto> resposta = new ArrayList<PortasignaturesDto>();
		List<Portasignatures> pendents = pluginHelper.findPendentsPortasignaturesPerProcessInstanceId(processInstanceId);
		for (Portasignatures pendent: pendents) {
			PortasignaturesDto dto = new PortasignaturesDto();
			dto.setId(pendent.getId());
			dto.setDocumentId(pendent.getDocumentId());
			dto.setTokenId(pendent.getTokenId());
			dto.setDataEnviat(pendent.getDataEnviat());
			if (TipusEstat.ERROR.equals(pendent.getEstat())) {
				if (Transicio.SIGNAT.equals(pendent.getTransition()))
					dto.setEstat(TipusEstat.SIGNAT.toString());
				else
					dto.setEstat(TipusEstat.REBUTJAT.toString());
				dto.setError(true);
			} else {
				dto.setEstat(pendent.getEstat().toString());
				dto.setError(false);
			}
			if (pendent.getTransition() != null)
				dto.setTransicio(pendent.getTransition().toString());
			dto.setDocumentStoreId(pendent.getDocumentStoreId());
			dto.setMotiuRebuig(pendent.getMotiuRebuig());
			dto.setTransicioOK(pendent.getTransicioOK());
			dto.setTransicioKO(pendent.getTransicioKO());
			dto.setDataProcessamentPrimer(pendent.getDataProcessamentPrimer());
			dto.setDataProcessamentDarrer(pendent.getDataProcessamentDarrer());
			dto.setDataSignatRebutjat(pendent.getDataSignatRebutjat());
			dto.setDataCustodiaIntent(pendent.getDataCustodiaIntent());
			dto.setDataCustodiaOk(pendent.getDataCustodiaOk());
			dto.setDataSignalIntent(pendent.getDataSignalIntent());
			dto.setDataSignalOk(pendent.getDataSignalOk());
			dto.setErrorProcessant(pendent.getErrorCallbackProcessant());
			dto.setProcessInstanceId(pendent.getProcessInstanceId());
			resposta.add(dto);
		}
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
