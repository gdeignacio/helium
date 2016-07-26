package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;;

/**
 * Servei per al manteniment de tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTipusService {

	/**
	 * Crea un nou tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipus
	 *            La informació del tipus d'expedient a crear.
	 * @param sequenciesAny
	 *            Els anys de les seqüències.
	 * @param sequenciesValor
	 *            Els valors de les seqüències.
	 * @return el tipus d'expedient creat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto create(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) throws NoTrobatException, PermisDenegatException;

	/**
	 * Modificació d'un tipus d'expedient existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipus
	 *            La informació del tipus d'expedient per a fer la modificació.
	 * @param sequenciesAny
	 *            Els anys de les seqüències.
	 * @param sequenciesValor
	 *            Els valors de les seqüències.
	 * @return el tipus d'expedient modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto update(
			Long entornId,
			ExpedientTipusDto expedientTipus,
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void delete(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els tipus d'expedient d'un entorn que es poden consultar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return Els tipus d'expedient amb permis de consulta.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisConsultar(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna un tipus d'expedient donat el seu id per a consultar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto findAmbIdPermisConsultar(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els tipus d'expedient d'un entorn que es poden dissenyar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return Els tipus d'expedient amb permisos de disseny.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisDissenyar(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna un tipus d'expedient donat el seu id per a dissenyar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto findAmbIdPermisDissenyar(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els tipus d'expedient d'un entorn que es poden iniciar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return Els tipus d'expedient amb permis de creació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<ExpedientTipusDto> findAmbEntornPermisCrear(
			Long entornId) throws NoTrobatException;

	/**
	 * Retorna un tipus d'expedient donat el seu id per a iniciar.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return El tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ExpedientTipusDto findAmbIdPermisCrear(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un tipus d'expedient donat el seu codi.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param codi
	 *            El codi per a la consulta.
	 * @return El tipus d'expedient o null si no el troba.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ExpedientTipusDto findAmbCodiPerValidarRepeticio(
			Long entornId,
			String codi) throws NoTrobatException;

	/** 
	 * Retorna la llista de tipus d'expedient paginada per la datatable.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<ExpedientTipusDto> findPerDatatable(
			Long entornId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;

	/**
	 * Modifica un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permis
	 *            La informació del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void permisUpdate(
			Long entornId,
			Long expedientTipusId,
			PermisDto permis) throws NoTrobatException, PermisDenegatException;

	/**
	 * Esborra un permis existent d'un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permisId
	 *            Atribut id del permis.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void permisDelete(
			Long entornId,
			Long expedientTipusId,
			Long permisId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna els permisos per a un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return els permisos del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<PermisDto> permisFindAll(
			Long entornId,
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna un permis donat el seu id.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param permisId
	 *            Atribut id del permis.
	 * @return el permis amb l'id especificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public PermisDto permisFindById(
			Long entornId,
			Long expedientTipusId,
			Long permisId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna les agrupacions per a un tipus d'expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les agrupacions del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<CampAgrupacioDto> agrupacioFindAll(
			Long expedientTipusId) throws NoTrobatException, PermisDenegatException;
		
	/**
	 * Crea una nova agrupació.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param agrupacio
	 *            La informació de la agrupació a crear.
	 * @return la agrupació creada.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampAgrupacioDto agrupacioCreate(
			Long expedientTipusId,
			CampAgrupacioDto agrupacio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una agrupació existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param agrupacio
	 *            La informació de la agrupació a modificar.
	 * @return la agrupació modificada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampAgrupacioDto agrupacioUpdate(
			Long expedientTipusId,
			CampAgrupacioDto agrupacio) throws NoTrobatException, PermisDenegatException;
	
	/** Mou la agrupacio id cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean agrupacioMourePosicio(Long id, int posicio);
	
	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param agrupacioCampId
	 *            Atribut id de la agrupació.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void agrupacioDelete(
			Long agrupacioCampId) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Retorna una agrupació de camps d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param tipusExpedientId
	 * @param codi
	 *            El codi per a la consulta.
	 * @return La agrupació de camps del tipus d'expedient o null si no el troba.
	 */
	public CampAgrupacioDto agrupacioFindAmbCodiPerValidarRepeticio(
			Long tipusExpedientId,
			String codi) throws NoTrobatException;	

	/** 
	 * Retorna la agrupació de camps del tipus d'expedient donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La agrupació de camps del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public CampAgrupacioDto agrupacioFindAmbId(
			Long id) throws NoTrobatException;	

	/** 
	 * Retorna la llista d'agrupacions del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat d'agrupacions del tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<CampAgrupacioDto> agrupacioFindPerDatatable(
			Long tipusExpedientId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	
	
	/**
	 * Crea un nou camp.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param camp
	 *            La informació del camp a crear.
	 * @return el camp creat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampDto campCreate(
			Long expedientTipusId,
			CampDto camp) throws PermisDenegatException;
	
	/**
	 * Modificació d'un camp existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param camp
	 *            La informació del camp a modificar.
	 * @return el camp modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public CampDto campUpdate(
			CampDto camp) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param campCampId
	 *            Atribut id del camp.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void campDelete(
			Long campCampId) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna el camp del tipus d'expedient donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return El camp del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public CampDto campFindAmbId(
			Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna la llista de camps del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<CampDto> campFindPerDatatable(
			Long expedientTipusId,
			Long agrupacioId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	/** 
	 * Retorna la llista de camps del tipus d'expedient
	 * i del tipus data per llistar en un selector.
	 * 
	 * @param expedientTipusId
	 * 
	 * @return Llistat de camps.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public List<CampDto> campFindTipusDataPerExpedientTipus(
			Long expedientTipusId) throws NoTrobatException;
	
	/**
	 * Retorna un camp d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param tipusExpedientId
	 * @param codi
	 *            El codi per a la consulta.
	 * @return El camp del tipus d'expedient o null si no el troba.
	 */
	public CampDto campFindAmbCodiPerValidarRepeticio(
			Long tipusExpedientId,
			String codi) throws NoTrobatException;

	/**
	 * Afegeix un camp a una agrupació.
	 * @param id
	 * 			Identificador del camp
	 * @param agrupacioId
	 * 			Identificador de la agrupació
	 * @return
	 * 			Retorna true si ha anat bé o false si no s'ha trobat el camp o la agrupació 
	 * 			o el seu tipus d'expedient no és el mateix.
	 */
	public boolean campAfegirAgrupacio(Long campId, Long agrupacioId);

	/**
	 * Remou un camp de la seva agrupació.
	 * 
	 * @param id
	 * 			Identificador del camp
	 * @return
	 * 			Retorna cert si s'ha remogut correctament o false si no existia el camp o no tenia
	 * 			agrupació.
	 */
	public boolean campRemoureAgrupacio(Long campId);

	/** Mou el camp id cap a la posició indicada reassignant el valor pel camp ordre dins de la agrupació.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean campMourePosicio(Long id, int posicio);

	/**
	 * Crea una nova validacio.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param campId
	 *            Atribut id del camp del tipus d'expedient.
	 * @param validacio
	 *            La informació de la validacio a crear.
	 * @return la validacio creada.
	 * @throws ValidacioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ValidacioDto validacioCreate(
			Long campId,
			ValidacioDto validacio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una validacio existent.
	 * 
	 * @param validacio
	 *            La informació de la validacio a modificar.
	 * @return la validacio modificada.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public ValidacioDto validacioUpdate(
			ValidacioDto validacio) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra una validacio.
	 * 
	 * @param id
	 *            Atribut id de la validacio.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void validacioDelete(
			Long id) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna la validacio donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La validacio de la variable.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public ValidacioDto validacioFindAmbId(
			Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna la llista de validacios de la variable del tipus d'expedient paginada per la datatable.
	 * 
	 * @param campId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina de la llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<ValidacioDto> validacioFindPerDatatable(
			Long campId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	

	/** Mou la validació id de camp cap a la posició indicada reassignant el valor pel camp ordre.
	 * 
	 * @param id
	 * @param posicio
	 * @return Retorna true si ha anat bé o false si no té agrupació o la posició no és correcta.
	 */
	public boolean validacioMourePosicio(Long id, int posicio);
	
	/**
	 * Retorna les enumeracions per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les enumeracions del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<EnumeracioDto> enumeracioFindAll(Long expedientTipusId);

	/**
	 * Retorna els dominis per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return els dominis del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<DominiDto> dominiFindAll(Long expedientTipusId);

	/**
	 * Retorna les consultes per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @return les consultes del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public List<ConsultaDto> consultaFindAll(Long expedientTipusId);
	
	
	/***********************************************/
	/******************DOCUMENTS********************/
	/***********************************************/
	
	/** 
	 * Retorna la llista de camps del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<ExpedientTipusDocumentDto> documentFindPerDatatable(
			Long expedientTipusId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public ExpedientTipusDocumentDto documentCreate(
			Long expedientTipusId, 
			ExpedientTipusDocumentDto document) throws PermisDenegatException;
	
	public ExpedientTipusDocumentDto documentFindAmbCodi(
			Long expedientTipusId, 
			String codi) throws NoTrobatException;
	
	public void documentDelete(
			Long documentId) throws NoTrobatException, PermisDenegatException;
	
	public ExpedientTipusDocumentDto documentFindAmbId(
			Long documentId) throws NoTrobatException;
	
	public ExpedientTipusDocumentDto documentUpdate(
			ExpedientTipusDocumentDto document) throws NoTrobatException, PermisDenegatException;
		
	public ArxiuDto getArxiuPerDocument(
			Long id) throws NoTrobatException;
	
	/**
	 * Crea una nova accio.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param accio
	 *            La informació de la accio a crear.
	 * @return la accio creada.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public AccioDto accioCreate(
			Long expedientTipusId,
			AccioDto accio) throws PermisDenegatException;
	
	/**
	 * Modificació d'una accio existent.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param accio
	 *            La informació de la accio a modificar.
	 * @return la accio modificat.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public AccioDto accioUpdate(
			AccioDto accio) throws NoTrobatException, PermisDenegatException;
	
	/**
	 * Esborra un entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param accioId
	 *            Atribut id de la accio.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void accioDelete(
			Long accioId) throws NoTrobatException, PermisDenegatException;	
	
	/** 
	 * Retorna la accio del tipus d'expedient donat el seu identificador.
	 * 
	 * @param id
	 * 
	 * @return La accio del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public AccioDto accioFindAmbId(
			Long id) throws NoTrobatException;	
	
	/** 
	 * Retorna la llista d'accions del tipus d'expedient paginada per la datatable.
	 * 
	 * @param expedientTipusId
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public PaginaDto<AccioDto> accioFindPerDatatable(
			Long expedientTipusId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;	
	
	/**
	 * Retorna una accio d'un tipus d'expedient donat el seu codi.
	 * 
	 * @param tipusExpedientId
	 * @param codi
	 *            El codi per a la consulta.
	 * @return La accio del tipus d'expedient o null si no el troba.
	 */
	public AccioDto accioFindAmbCodiPerValidarRepeticio(
			Long tipusExpedientId,
			String codi) throws NoTrobatException;	

	/***********************************************/
	/*****************ENUMERACIONS******************/
	/***********************************************/

	public PaginaDto<ExpedientTipusEnumeracioDto> enumeracioFindPerDatatable(
			Long expedientTipusId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public ExpedientTipusEnumeracioDto enumeracioCreate(
			Long expedientTipusId, 
			Long entornId,
			ExpedientTipusEnumeracioDto enumeracio) throws PermisDenegatException;
	
	public ExpedientTipusEnumeracioDto enumeracioFindAmbCodi(
			Long expedientTipusId,
			String codi) throws NoTrobatException;
	
	public void enumeracioDelete(
			Long enumeracioId) throws NoTrobatException, PermisDenegatException;
	
	public ExpedientTipusEnumeracioDto enumeracioFindAmbId(
			Long enumeracioId) throws NoTrobatException;
	
	public ExpedientTipusEnumeracioDto enumeracioUpdate(
			ExpedientTipusEnumeracioDto enumeracio) throws NoTrobatException, PermisDenegatException;
	
	/***********************************************/
	/***************VALORS ENUMERACIO***************/
	/***********************************************/

	public PaginaDto<ExpedientTipusEnumeracioValorDto> enumeracioValorsFindPerDatatable(
			Long expedientTipusId,
			Long enumeracioId,
			String filtre, 
			PaginacioParamsDto paginacioParams) throws NoTrobatException;
	
	public ExpedientTipusEnumeracioValorDto enumeracioValorsCreate(
			Long expedientTipusId, 
			Long enumeracioId,
			Long entornId,
			ExpedientTipusEnumeracioValorDto enumeracio) throws PermisDenegatException;
	
	public void enumeracioValorDelete(
			Long valorId) throws NoTrobatException, PermisDenegatException;
	
	public void enumeracioDeleteAllByEnumeracio(Long enumeracioId) throws NoTrobatException, PermisDenegatException, ValidacioException;
	
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbCodi(
			Long expedientTipusId,
			Long enumeracioId,
			String codi) throws NoTrobatException;	
	
	public ExpedientTipusEnumeracioValorDto enumeracioValorFindAmbId(
			Long valorId) throws NoTrobatException;
	
	public ExpedientTipusEnumeracioValorDto enumeracioValorUpdate(
			ExpedientTipusEnumeracioValorDto enumeracio) throws NoTrobatException, PermisDenegatException;
	
	public boolean enumeracioValorMourer(Long valorId, int posicio) throws NoTrobatException;


	/** 
	 * Retorna la llista de definicions de procés del tipus d'expedient paginada per la datatable.
	 * 
	 * @param entornId
	 * @param expedientTipusId
	 * @param expedientTipusId2 
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat de tipus d'expedients.
	 */
	public PaginaDto<DefinicioProcesDto> definicioProcesFindPerDatatable(
			Long entornId, 
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre, 
			PaginacioParamsDto paginacioParams);

	/**
	 * Esborra una entitat.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn.
	 * @param id
	 *            Atribut id de la definicio de procés.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 * @throws AccioDenegatException
	 *             Si no es tenen els permisos necessaris.
	 */
	public void definicioProcesDelete(
			Long id) throws NoTrobatException, PermisDenegatException;

	/**
	 * Marca la definició de procés com a inicial al tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @param id
	 *            Identificador del tipus d'expedient.
	 */
	public boolean definicioProcesSetInicial(
			Long expedientTipusId, 
			Long id);

	
	/***********************************************/
	/*******************TERMINIS********************/
	/***********************************************/
	
	/**
	 * Retorna els terminis per a un tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 * @throws NoTrobatException
	 * @throws PermisDenegatException
	 */
	public List<TerminiDto> terminiFindAll(
			Long expedientTipusId,
			PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException;

	/** 
	 * Retorna el termini del tipus d'expedient donat el seu identificador.
	 * 
	 * @param terminiId
	 * 
	 * @return El termini del tipus d'expedient.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat el registre amb l'id especificat.
	 */
	public TerminiDto terminiFindAmbId(Long terminiId);

	/**
	 * Crea un nou termini.
	 * 
	 * @param entornId	Atribut id de l'entorn.
	 * @param expedientTipusId	Atribut id del tipus d'expedient.
	 * @param termini	La informació del camp a crear.
	 * @return el termini creat.
	 * @throws CampDenegatException Si no es tenen els permisos necessaris.
	 */
	public TerminiDto terminiCreate(Long expedientTipusId, TerminiDto termini);
	
	/**
	 * Modificació d'un termini existent.
	 * 
	 * @param entornId	Atribut id de l'entorn.
	 * @param expedientTipusId	Atribut id del tipus d'expedient.
	 * @param trmini	La informació del termini a modificar.
	 * @return el termini modificat.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException	Si no es tenen els permisos necessaris.
	 */
	public TerminiDto terminiUpdate(TerminiDto termini);
	
	/**
	 * Esborra un termini.
	 * 
	 * @param entornId	Atribut id de l'entorn.
	 * @param terminiId	Atribut id del termini.
	 * @throws NoTrobatException	Si no s'ha trobat el registre amb l'id especificat.
	 * @throws CampDenegatException	Si no es tenen els permisos necessaris.
	 */
	public void terminiDelete(Long terminiId) throws NoTrobatException, PermisDenegatException;

}