/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

/**
 * Implementació del servei per a gestionar documents dels tipus d'expedients o definicions de procés.
 * 
 */
@Service
public class DocumentServiceImpl implements DocumentService {

	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<DocumentDto> findPerDatatable(
			Long expedientTipusId,
			Long definicioProcesId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els documents per al tipus d'expedient per datatable (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"filtre=" + filtre + ")");
				
		return paginacioHelper.toPaginaDto(
				documentRepository.findByFiltrePaginat(
						expedientTipusId,
						definicioProcesId,
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
						DocumentDto.class);
	}
	
	@Override
	@Transactional
	public DocumentDto create(
			Long expedientTipusId, 
			Long definicioProcesId, 
			DocumentDto document) {

		logger.debug(
				"Creant nou document per un tipus d'expedient (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"document=" + document + ")");		
		Document entity = new Document();
		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setArxiuNom(document.getArxiuNom());
		entity.setArxiuContingut(document.getArxiuContingut());
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.getCampData() != null)
			entity.setCampData(campRepository.findOne(document.getCampData().getId()));
		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
		
		if (expedientTipusId != null)
			entity.setExpedientTipus(expedientTipusRepository.findOne(expedientTipusId));
		if (definicioProcesId != null)
			entity.setDefinicioProces(definicioProcesRepository.findOne(definicioProcesId));

		return conversioTipusHelper.convertir(
				documentRepository.save(entity),
				DocumentDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public DocumentDto findAmbCodi(
			Long expedientTipusId, 
			Long definicioProcesId, 
			String codi) {
		DocumentDto ret = null; 
		logger.debug(
				"Consultant el document del tipus d'expedient per codi (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ", " +
				"codi = " + codi + ")");
		Document document = null;
		if (expedientTipusId != null)
			document = documentRepository.findByExpedientTipusAndCodi(
											expedientTipusRepository.findOne(expedientTipusId), 
											codi);
		else if(definicioProcesId != null)
			document = documentRepository.findByDefinicioProcesAndCodi(
											definicioProcesRepository.findOne(definicioProcesId), 
											codi);
		if (document != null)
		ret = conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
		return ret;
	}
	
	@Override
	@Transactional
	public void delete(Long documentId) {
		logger.debug(
				"Esborrant el document del tipus d'expedient (" +
				"documentId=" + documentId +  ")");
		Document entity = documentRepository.findOne(documentId);

		if (entity != null) {
			for (DocumentTasca documentTasca: entity.getTasques()) {
				documentTasca.getTasca().removeDocument(documentTasca);
				int i = 0;
				for (DocumentTasca dt: documentTasca.getTasca().getDocuments())
					dt.setOrder(i++);
			}
		} else {
			throw new NoTrobatException(Document.class);
		}
		documentRepository.delete(entity);	
	}
	
	@Override
	@Transactional
	public DocumentDto findAmbId(Long id) {
		logger.debug(
				"Consultant el document del tipus d'expedient amb id (" +
				"documentId=" + id +  ")");
		Document document = documentRepository.findOne(id);
		if (document == null) {
			throw new NoTrobatException(Document.class, id);
		}
		return conversioTipusHelper.convertir(
				document,
				DocumentDto.class);
	}
	
	@Override
	@Transactional
	public DocumentDto update(DocumentDto document) {
		logger.debug(
				"Modificant el document del tipus d'expedient existent (" +
				"document.id=" + document.getId() + ", " +
				"document =" + document + ")");
		Document entity = documentRepository.findOne(document.getId());
		entity.setCodi(document.getCodi());
		entity.setNom(document.getNom());
		entity.setDescripcio(document.getDescripcio());
		entity.setPlantilla(document.isPlantilla());
		entity.setArxiuNom(document.getArxiuNom());
		entity.setArxiuContingut(document.getArxiuContingut());
		entity.setConvertirExtensio(document.getConvertirExtensio());
		entity.setAdjuntarAuto(document.isAdjuntarAuto());
		if (document.getCampData() != null)
			entity.setCampData(campRepository.findOne(document.getCampData().getId()));
		else
			entity.setCampData(null);
		
		entity.setExtensionsPermeses(document.getExtensionsPermeses());
		entity.setContentType(document.getContentType());
		entity.setCustodiaCodi(document.getCustodiaCodi());
		entity.setTipusDocPortasignatures(document.getTipusDocPortasignatures());

		return conversioTipusHelper.convertir(
				documentRepository.save(entity),
				DocumentDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getArxiu(
			Long id) {
		logger.debug("obtenint contingut de l'arxiu pel document (" +
				"id=" + id + ")");
		
		Document document = documentRepository.findOne(id);
		if (document == null) {
			throw new NoTrobatException(Document.class,id);
		}
		
		ArxiuDto resposta = new ArxiuDto();
		
		resposta.setNom(document.getArxiuNom());
		resposta.setContingut(document.getArxiuContingut());
		
		return resposta;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentDto> findAll(
			Long expedientTipusId,
			Long definicioProcesId) {
		logger.debug("Consultant tots els documents (" +
				"expedientTipusId =" + expedientTipusId + ", " +
				"definicioProcesId =" + definicioProcesId + ")");
		
		List<Document> documents;
		if (expedientTipusId != null)
			documents = documentRepository.findByExpedientTipusIdOrderByCodiAsc(expedientTipusId);
		else
			documents = documentRepository.findByDefinicioProcesIdOrderByCodiAsc(definicioProcesId);
		
		return conversioTipusHelper.convertirList(
				documents, 
				DocumentDto.class);
	}		
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
}