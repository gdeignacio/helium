/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Collections;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DominiHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesVersioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

/**
 * Servei per gestionar les tasques de disseny.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("dissenyServiceV3")
public class DissenyServiceImpl implements DissenyService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private DominiHelper dominiHelper;
	
	@Transactional(readOnly=true)
	@Override
	public AreaDto findAreaById(Long areaId) {
		
		Area area = areaRepository.findOne(areaId);
		
		if (area == null)
			throw new NoTrobatException(Area.class, areaId);
		
		return conversioTipusHelper.convertir(
				area,
				AreaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<EstatDto> findEstatByExpedientTipus(Long expedientTipusId){
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(
				expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		return conversioTipusHelper.convertirList(
				estatRepository.findByExpedientTipusOrderByOrdreAsc(expedientTipus),
				EstatDto.class);
	}
	
	private void getAllDefinicioProcesOrderByVersio (DefinicioProcesDto definicioProcesDto) {	
		JbpmProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProcesDto.getJbpmId());
		definicioProcesDto.setEtiqueta(jb.getProcessDefinition().getName()+" v."+jb.getVersion());
		List<DefinicioProces> mateixaKeyIEntorn = definicioProcesRepository.findByEntornIdAndJbpmKeyOrderByVersioDesc(
				definicioProcesDto.getEntorn().getId(),
				definicioProcesDto.getJbpmKey());
		definicioProcesDto.setIdsWithSameKey(new Long[mateixaKeyIEntorn.size()]);
		definicioProcesDto.setIdsMostrarWithSameKey(new String[mateixaKeyIEntorn.size()]);
		definicioProcesDto.setJbpmIdsWithSameKey(new String[mateixaKeyIEntorn.size()]);
		for (int i = 0; i < mateixaKeyIEntorn.size(); i++) {
			definicioProcesDto.getIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getId();
			definicioProcesDto.getIdsMostrarWithSameKey()[i] = mateixaKeyIEntorn.get(i).getIdPerMostrar();
			definicioProcesDto.getJbpmIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getJbpmId();
		}
	}

	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesVersioDto getByVersionsInstanciaProcesById(String processInstanceId) {
		JbpmProcessInstance pi = jbpmHelper.getProcessInstance(processInstanceId);
		if (pi == null)
			throw new NoTrobatException(JbpmProcessInstance.class, processInstanceId);
		
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, pi.getProcessDefinitionId());
		
		DefinicioProcesVersioDto dto = new DefinicioProcesVersioDto();
		dto.setId(definicioProces.getId());
		dto.setVersio(definicioProces.getVersio());
		List<DefinicioProces> listDefProces = definicioProcesRepository.findByEntornIdAndJbpmKeyOrderByVersioDesc(definicioProces.getEntorn().getId(), definicioProces.getJbpmKey());
		for (DefinicioProces defProces : listDefProces) {
			dto.addVersioAmbEtiqueta(defProces.getVersio(), pi.getProcessInstance().getProcessDefinition().getName() + " v." + defProces.getVersio());
			if (defProces.getVersio() == definicioProces.getVersio()) {
				dto.setEtiqueta(pi.getProcessInstance().getProcessDefinition().getName() + " v." + defProces.getVersio());
			}
		}
		return dto;
	}

	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesExpedientDto getDefinicioProcesByTipusExpedientById(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus != null && expedientTipus.getJbpmProcessDefinitionKey() != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null) {
				JbpmProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProces.getJbpmId());
				return getDefinicioProcesByEntornIdAmbJbpmId(
						definicioProces.getEntorn().getId(), 
						jb.getKey(), 
						conversioTipusHelper.convertir(
								expedientTipus,
								ExpedientTipusDto.class));
			}
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<DefinicioProcesExpedientDto> getSubprocessosByProces(String jbpmId) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jbpmId);
		
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, jbpmId);
		
		List<String> jbpmIds = new ArrayList<String>(); 
		afegirJbpmIdProcesAmbSubprocessos(jbpmHelper.getProcessDefinition(jbpmId), jbpmIds, false);
		List<DefinicioProcesExpedientDto> subprocessos = new ArrayList<DefinicioProcesExpedientDto>();
		for(String id: jbpmIds){
			JbpmProcessDefinition jb = jbpmHelper.getProcessDefinition(id);
			subprocessos.add(getDefinicioProcesByEntornIdAmbJbpmId(definicioProces.getEntorn().getId(), jb.getKey(), null));
		}
		return subprocessos;
	}

	private DefinicioProcesExpedientDto getDefinicioProcesByEntornIdAmbJbpmId(Long entornId, String jbpmKey, ExpedientTipusDto expedientTipus) {
		DefinicioProcesExpedientDto dto = new DefinicioProcesExpedientDto();
		DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(entornId, jbpmKey);
		if (definicioProces != null) {
			JbpmProcessDefinition jb = jbpmHelper.getProcessDefinition(definicioProces.getJbpmId());			
			dto.setId(definicioProces.getId());
			dto.setJbpmId(definicioProces.getJbpmId());
			dto.setJbpmKey(definicioProces.getJbpmKey());
			dto.setVersio(definicioProces.getVersio());
			List<DefinicioProces> listDefProces = definicioProcesRepository.findByEntornIdAndJbpmKeyOrderByVersioDesc(definicioProces.getEntorn().getId(), definicioProces.getJbpmKey());
			boolean demanaNumeroTitol = false;
			if (expedientTipus != null)
				demanaNumeroTitol = (expedientTipus.isTeNumero() && expedientTipus.isDemanaNumero()) || (expedientTipus.isTeTitol() && expedientTipus.isDemanaTitol());
			for (DefinicioProces defProces : listDefProces) {				
				Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();			
				dto.addIdAmbEtiquetaId(
						defProces.getId(), 
						defProces.getJbpmId(),
						jb.getName() + " v." + defProces.getVersio(), 
						hasStartTask(definicioProces, hasStartTask), 
						demanaNumeroTitol);
				if (defProces.getVersio() == definicioProces.getVersio()) {
					dto.setEtiqueta(jb.getName() + " v." + defProces.getVersio());
				}
			}
			return dto;
		}
		return null;
	}
	
	private void afegirJbpmIdProcesAmbSubprocessos(
			JbpmProcessDefinition jpd,
			List<String> jbpmIds, 
			Boolean incloure) {
		if (jpd != null) {
			List<JbpmProcessDefinition> subPds = jbpmHelper.getSubProcessDefinitions(jpd.getId());
			if (subPds != null) {
				for (JbpmProcessDefinition subPd: subPds) {
					afegirJbpmIdProcesAmbSubprocessos(subPd, jbpmIds, true);
					if (!jbpmIds.contains(subPd.getId()))
						jbpmIds.add(subPd.getId());
				}
			}
			if (!jbpmIds.contains(jpd.getId()) && incloure)
				jbpmIds.add(jpd.getId());
		}
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto getById(Long id) {
		DefinicioProces definicioProces = definicioProcesRepository.findById(id);
		if (definicioProces != null) {	
			return conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class);
		}
		return null;
	}
	
	@Transactional(readOnly=true)
	@Override
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		ExpedientTipusDto expedientTipus = getExpedientTipusById(expedientTipusId);
		if (expedientTipus.getJbpmProcessDefinitionKey() != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null) {
				DefinicioProcesDto dto = conversioTipusHelper.convertir(definicioProces, DefinicioProcesDto.class);
				Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();
				dto.setHasStartTask(hasStartTask(definicioProces, hasStartTask));
				getAllDefinicioProcesOrderByVersio(dto);
				return dto;
			}
		}
		return null;
	}
	
	private boolean hasStartTask(DefinicioProces definicioProces, Map<Long, Boolean> hasStartTask) {
		Long definicioProcesId = definicioProces.getId();
		Boolean result = hasStartTask.get(definicioProcesId);
		if (result == null) {
			result = new Boolean(false);
			String startTaskName = jbpmHelper.getStartTaskName(
					definicioProces.getJbpmId());
			if (startTaskName != null) {
				Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProcesJbpmId(
						startTaskName,
						definicioProces.getJbpmId());
				if (tasca != null) {
					List<CampTasca> camps = campTascaRepository.findAmbTascaOrdenats(tasca.getId());
					result = new Boolean(camps.size() > 0);
				}
			}
			hasStartTask.put(definicioProcesId, result);
		}
		return result.booleanValue();
	}

	@Transactional(readOnly=true)
	@Override
	public ExpedientTipusDto getExpedientTipusById(Long id) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findById(id);
		if (expedientTipus == null)
			throw new NoTrobatException(ExpedientTipus.class, id);
		return conversioTipusHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId){
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(
			Long entornId) {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION});
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(
			Long entornId) {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.MANAGE,
						ExtendedPermission.ADMINISTRATION});
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(
			Long entornId) {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.CREATE,
						ExtendedPermission.ADMINISTRATION});
	}

	@Transactional(readOnly=true)
	@Override
	public ExpedientTipusDto findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId,
			Long expedientTipusId) {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null) {
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		}
		Permission[] permisosRequerits= new Permission[] {
				ExtendedPermission.READ,
				ExtendedPermission.ADMINISTRATION};
		boolean permes = permisosHelper.isGrantedAny(
				expedientTipusId,
				ExpedientTipus.class,
				permisosRequerits,
				SecurityContextHolder.getContext().getAuthentication());
		if (!permes) {
			throw new PermisDenegatException(
					expedientTipusId,
					ExpedientTipus.class,
					permisosRequerits,
					null);
		}
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(
				expedientTipusId);
		if (expedientTipus.getEntorn() != entorn) {
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		}
		List<ExpedientTipusDto> dtos = new ArrayList<ExpedientTipusDto>();
		dtos.add(conversioTipusHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class));
		expedientHelper.omplirPermisosExpedientsTipus(dtos);
		return dtos.get(0);
	}

	private List<ExpedientTipusDto> findExpedientTipusAmbPermisosUsuariActual(
			Long entornId,
			Permission[] permisos) {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findByEntornOrderByNomAsc(entorn);
		permisosHelper.filterGrantedAny(
				expedientsTipus,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				permisos,
				SecurityContextHolder.getContext().getAuthentication());
		List<ExpedientTipusDto> dtos = conversioTipusHelper.convertirList(
				expedientsTipus,
				ExpedientTipusDto.class);
		expedientHelper.omplirPermisosExpedientsTipus(dtos);
		return dtos;
	}
	
	@Transactional(readOnly=true)
	@Override
	@Cacheable(value="consultaCache", key="{#entornId, #expedientTipusId}")
	public List<ConsultaDto> findConsultesActivesAmbEntornIExpedientTipusOrdenat(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new NoTrobatException(
					Entorn.class, 
					entornId);
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(
				expedientTipusId);
		if (expedientTipus == null)
			throw new NoTrobatException(
					ExpedientTipus.class, 
					expedientTipusId);
		return conversioTipusHelper.convertirList(
				consultaRepository.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId),
				ConsultaDto.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public ConsultaDto findConsulteById(
			Long id) {
		Consulta consulta = consultaRepository.findOne(id);
		
		if (consulta == null)
			throw new NoTrobatException(Consulta.class, id);
		
		return conversioTipusHelper.convertir(consulta, ConsultaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public byte[] getDeploymentResource(
			Long definicioProcesId,
			String resourceName) {
		DefinicioProces definicioProces = definicioProcesRepository.findById(definicioProcesId);
		
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		
		return jbpmHelper.getResourceBytes(
				definicioProces.getJbpmId(),
				resourceName);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornDto) {
		Entorn entorn = conversioTipusHelper.convertir(entornDto, Entorn.class);
		if (entorn == null)
			throw new NoTrobatException(Entorn.class, entornDto.getCodi());
		return getExpedientTipusAmbEntorn(entorn);
	}

	@Transactional(readOnly=true)
	@Override
	public List<ParellaCodiValorDto> findTasquesAmbEntornIExpedientTipusPerSeleccio(
			Long entornId,
			Long expedientTipusId) {
		List<Long> ids = new ArrayList<Long>();
		Set<Object[]> tasques = new HashSet<Object[]>();
		if (expedientTipusId.equals(-1L)) {
			List<ExpedientTipusDto> tipusExpedient = findExpedientTipusAmbPermisReadUsuariActual(entornId);
			for (ExpedientTipusDto tipus : tipusExpedient) {
				ids.addAll(definicioProcesRepository.findIdsDarreraVersioAmbEntornIdIExpedientTipusId(
						tipus.getEntorn().getId(),
						tipus.getId()));
			}
			if (ids.size() > 0)
				tasques.addAll(
						tascaRepository.findIdNomByExpedientTipusOrderByExpedientTipusNomAndNomAsc(ids));
		}
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus != null && expedientTipus.getJbpmProcessDefinitionKey() != null) {
			ids.addAll(definicioProcesRepository.findIdsDarreraVersioAmbEntornIdIExpedientTipusId(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getId()));
			tasques.addAll(tascaRepository.findIdNomByDefinicioProcesIdsOrderByNomAsc(ids));
		}
		List<ParellaCodiValorDto> lista = new ArrayList<ParellaCodiValorDto>();
		for (Object[] tasca: tasques) {
			lista.add(new ParellaCodiValorDto(tasca[0].toString(), tasca[1]));
		}
		Collections.sort(
				lista, 
				new Comparator<ParellaCodiValorDto>() {
					public int compare(ParellaCodiValorDto p1, ParellaCodiValorDto p2) {
						if (p1 != null && p2 == null)
							return -1;
						if (p1 == null && p2 == null)
							return 0;
						if (p1 == null && p2 != null)
							return 1;
						String val1 = p1.getValor() == null ? "" : (String)p1.getValor();
						String val2 = p2.getValor() == null ? "" : (String)p2.getValor();
						int result = val1.compareTo(val2);
						return result != 0 ? result : p1.getCodi().compareTo(p2.getCodi());
					}
				});
		return lista;
	}
	
	@Transactional(readOnly=true)
	private List<ExpedientTipusDto> getExpedientTipusAmbEntorn(Entorn entorn) {
		List<ExpedientTipusDto> tipus = conversioTipusHelper.convertirList(expedientTipusRepository.findByEntornOrderByCodiAsc(entorn), ExpedientTipusDto.class);
		permisosHelper.filterGrantedAny(
				tipus,
				new ObjectIdentifierExtractor<ExpedientTipusDto>() {
					public Long getObjectIdentifier(ExpedientTipusDto expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.CREATE
				},
				SecurityContextHolder.getContext().getAuthentication());
		return tipus;
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<CampDto> findCampsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) {
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		
		if (definicioProces == null)
			throw new NoTrobatException(DefinicioProces.class, definicioProcesId);
		
		return conversioTipusHelper.convertirList(campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces), CampDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public List<FilaResultat> consultaDominiIntern(String id, List<ParellaCodiValor> parametres) throws Exception {
		return dominiHelper.consultaDominiIntern(id, parametres);
	}

}
