/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.Registre;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * Utilitats comunes pels serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServiceUtils {

	private DefinicioProcesDao definicioProcesDao;
	private CampDao campDao;
	private ConsultaCampDao consultaCampDao;
	private DtoConverter dtoConverter;
	private JbpmDao jbpmDao;
	private MessageSource messageSource;



	public ServiceUtils(
			DefinicioProcesDao definicioProcesDao,
			CampDao campDao,
			ConsultaCampDao consultaCampDao,
			DtoConverter dtoConverter,
			JbpmDao jbpmDao,
			MessageSource messageSource) {
		this.definicioProcesDao = definicioProcesDao;
		this.campDao = campDao;
		this.consultaCampDao = consultaCampDao;
		this.dtoConverter = dtoConverter;
		this.jbpmDao = jbpmDao;
		this.messageSource = messageSource;
	}



	/*
	 * Mètodes per a la  construcció d'estructures per a l'indexació d'expedients
	 */
	public Map<String, DefinicioProces> getMapDefinicionsProces(Expedient expedient) {
		Map<String, DefinicioProces> resposta = new HashMap<String, DefinicioProces>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					definicioProcesDao.findAmbJbpmId(pi.getProcessDefinitionId()));
		return resposta;
	}
	public Map<String, Set<Camp>> getMapCamps(Expedient expedient) {
		Map<String, Set<Camp>> resposta = new HashMap<String, Set<Camp>>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree) {
			resposta.put(
					pi.getId(),
					definicioProcesDao.findAmbJbpmId(pi.getProcessDefinitionId()).getCamps());
		}
		return resposta;
	}
	public Map<String, Map<String, Object>> getMapValors(Expedient expedient) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		List<JbpmProcessInstance> tree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					jbpmDao.getProcessInstanceVariables(pi.getId()));
		return resposta;
	}
	public Map<String, Map<String, String>> getMapValorsDomini(
			Map<String, Set<Camp>> mapCamps,
			Map<String, Map<String, Object>> mapValors) {
		Map<String, Map<String, String>> resposta = new HashMap<String, Map<String, String>>();
		for (String clau: mapCamps.keySet()) {
			Map<String, String> textDominis = new HashMap<String, String>();
			for (Camp camp: mapCamps.get(clau)) {
				if (mapValors.get(clau) != null) {
					Object valor = mapValors.get(clau).get(camp.getCodi());
					if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
						if (valor != null) {
							String[] columnesRegistre = new String[camp.getRegistreMembres().size()];
							for (int i = 0; i < camp.getRegistreMembres().size(); i++) {
								columnesRegistre[i] = camp.getRegistreMembres().get(i).getMembre().getCodi();
							}
							List<Registre> registres = new ArrayList<Registre>();
							if (camp.isMultiple()) {
								Object[] filesValor = (Object[])valor;
								for (int i = 0; i < filesValor.length; i++) {
									registres.add(
											new Registre(columnesRegistre, (Object[])filesValor[i]));
								}
							} else {
								registres.add(
										new Registre(columnesRegistre, (Object[])valor));
							}
							for (Registre registre: registres) {
								for (CampRegistre campRegistre: camp.getRegistreMembres()) {
									guardarValorDominiPerCamp(
											textDominis,
											clau,
											campRegistre.getMembre(),
											registre.getValor(campRegistre.getMembre().getCodi()));
								}
							}
						}
					} else {
						guardarValorDominiPerCamp(
								textDominis,
								clau,
								camp,
								valor);
					}
				}
			}
			resposta.put(clau, textDominis);
		}
		return resposta;
	}

	/*
	 * Mètodes per a obtenir els camps de les consultes per tipus
	 */
	public List<Camp> findCampsPerCampsConsulta(Long consultaId, TipusConsultaCamp tipus) {
		List<Camp> resposta = new ArrayList<Camp>();
		List<ConsultaCamp> camps = consultaCampDao.findCampsConsulta(consultaId, tipus);
		for (ConsultaCamp camp: camps) {
			if (!camp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmKeyIVersio(
						camp.getDefprocJbpmKey(),
						camp.getDefprocVersio());
				if (definicioProces != null) {
					resposta.add(campDao.findAmbDefinicioProcesICodi(
							definicioProces.getId(),
							camp.getCampCodi()));
				}
			} else {
				Camp campExpedient = getCampExpedient(camp.getCampCodi());
				if (campExpedient != null)
					resposta.add(campExpedient);
			}
		}
		return resposta;
	}
	public List<Camp> findAllCampsExpedientConsulta() {
		List<Camp> resposta = new ArrayList<Camp>();
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ID));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_NUMERO));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_TITOL));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ESTAT));
		return resposta;
	}
	private Camp getCampExpedient(String campCodi) {
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.INTEGER);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.id"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.numero"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.titol"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.comentari"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.iniciador"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.responsable"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.data_ini"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.estat"));
			return campExpedient;
		}
		return null;
	}

	/*
	 * Mètodes pel multiidioma
	 */
	public String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	public String getMessage(String key) {
		return getMessage(key, null);
	}



	private void guardarValorDominiPerCamp(
			Map<String, String> textDominis,
			String processInstanceId,
			Camp camp,
			Object valor) {
		if (!(valor instanceof String) || ((String)valor).length() > 0) {
			if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) {
				if (valor != null) {
					String valorDomini = dtoConverter.getTextConsultaDomini(
							null,
							processInstanceId,
							camp,
							valor);
					textDominis.put(
							camp.getCodi() + "@" + valor.toString(),
							valorDomini);
				}
			}
		}
	}

}
