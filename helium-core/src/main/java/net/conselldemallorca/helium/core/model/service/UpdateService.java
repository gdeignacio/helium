/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.dao.PermisDao;
import net.conselldemallorca.helium.core.model.dao.PersonaDao;
import net.conselldemallorca.helium.core.model.dao.UsuariDao;
import net.conselldemallorca.helium.core.model.dao.VersioDao;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.hibernate.Usuari;
import net.conselldemallorca.helium.core.model.update.Versio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar la inicialització del sistema i les
 * actualitzacions automàtiques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class UpdateService {

	public static final String VERSIO_210_STR = "2.1.0";
	public static final int VERSIO_210_ORDRE = 210;
	public static final String VERSIO_211_STR = "2.1.1";
	public static final int VERSIO_211_ORDRE = 211;
	public static final String VERSIO_220_STR = "2.2.0";
	public static final int VERSIO_220_ORDRE = 220;
	public static final String VERSIO_221_STR = "2.2.1";
	public static final int VERSIO_221_ORDRE = 221;
	public static final String VERSIO_ACTUAL_STR = "2.2.1";
	public static final int VERSIO_ACTUAL_ORDRE = 221;

	private VersioDao versioDao;
	private PersonaDao personaDao;
	private UsuariDao usuariDao;
	private PermisDao permisDao;

	private DissenyService dissenyService;

	private MessageSource messageSource;

	private String errorUpdate;



	public void updateToLastVersion() throws Exception {
		List<Versio> versions = versioDao.findAllOrdered();
		if (versions.size() == 0) {
			logger.info("Actualitzant la base de dades a la versió inicial");
			createInitialData();
		}
		for (Versio versio: versions) {
			if (!versio.isProcesExecutat()) {
				if (versio.getOrdre() == VERSIO_210_ORDRE) {
					boolean actualitzat = actualitzarV210();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_220_ORDRE) {
					boolean actualitzat = actualitzarV220();
					if (!actualitzat) break;
				}
				if (versio.getOrdre() == VERSIO_221_ORDRE) {
					boolean actualitzat = actualitzarV221();
					if (!actualitzat) break;
				}
			}
		}
		Versio darrera = versioDao.findLast();
		if (darrera.getOrdre() < 221) {
			actualitzarV221();
		}
	}

	public String getVersioActual() {
		return VERSIO_ACTUAL_STR;
	}

	public String getErrorUpdate() {
		return errorUpdate;
	}



	@Autowired
	public void setVersioDao(VersioDao versioDao) {
		this.versioDao = versioDao;
	}
	@Autowired
	public void setPersonaDao(PersonaDao personaDao) {
		this.personaDao = personaDao;
	}
	@Autowired
	public void setUsuariDao(UsuariDao usuariDao) {
		this.usuariDao = usuariDao;
	}
	@Autowired
	public void setPermisDao(PermisDao permisDao) {
		this.permisDao = permisDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}



	private void createInitialData() throws Exception {
		Permis permisAdmin = new Permis(
				"HEL_ADMIN",
				"Administrador");
		permisDao.saveOrUpdate(permisAdmin);
		Permis permisUser = new Permis(
				"HEL_USER",
				"Usuari");
		permisDao.saveOrUpdate(permisUser);
		Usuari usuariAdmin = new Usuari(
				"admin",
				"admin",
				true);
		usuariAdmin.addPermis(permisAdmin);
		usuariDao.saveOrUpdate(usuariAdmin);
		usuariDao.canviContrasenya("admin", "admin");
		Persona personaAdmin = new Persona(
				"admin",
				"Usuari",
				"Administrador",
				"admin@helium.org");
		personaDao.saveOrUpdate(personaAdmin);
		Versio versioInicial = new Versio(
				"inicial",
				0);
		versioInicial.setProcesExecutat(true);
		versioInicial.setDataExecucioProces(new Date());
		versioInicial.setScriptExecutat(true);
		versioInicial.setDataExecucioScript(new Date());
		versioDao.saveOrUpdate(versioInicial);
	}

	private boolean actualitzarV210() {
		boolean actualitzat = false;
		Versio versio210 = obtenirOCrearVersio(VERSIO_210_STR, VERSIO_210_ORDRE);
		if (!versio210.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_210_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio210.isProcesExecutat()) {
			try {
				canviarMapeigSistraV210();
				canviarMapeigEnumeracionsV210();
				versio210.setProcesExecutat(true);
				versio210.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio210);
				logger.info("Actualització a la versió " + VERSIO_210_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_210_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_210_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}
	private void canviarMapeigSistraV210() throws Exception {
		if (dissenyService.findMapeigSistraTots().size() == 0) {
			List<ExpedientTipus> expedientsTipus = dissenyService.findExpedientTipusTots();
			for (ExpedientTipus expedientTipus : expedientsTipus) {
				if (expedientTipus.getSistraTramitMapeigCamps() != null) {
					String[] parts = expedientTipus.getSistraTramitMapeigCamps().split(";");
					for (int i = 0; i < parts.length; i++) {
						String[] parella = parts[i].split(":");
						if (parella.length > 1) {
							String varSistra = parella[0];
							String varHelium = parella[1];
							if (varHelium != null && (!"".equalsIgnoreCase(varHelium))) {
								if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null) {
									dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Variable, expedientTipus);
								}
							}
						}
					}
				}
				if (expedientTipus.getSistraTramitMapeigDocuments() != null) {
					String[] parts = expedientTipus.getSistraTramitMapeigDocuments().split(";");
					for (int i = 0; i < parts.length; i++) {
						String[] parella = parts[i].split(":");
						if (parella.length > 1) {
							String varSistra = parella[0];
							String varHelium = parella[1];
							if (varHelium != null && (!"".equalsIgnoreCase(varHelium))) {
								if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varHelium) == null) {
									dissenyService.createMapeigSistra(varHelium, varSistra, MapeigSistra.TipusMapeig.Document, expedientTipus);
								}
							}
						}
					}
				}
				if (expedientTipus.getSistraTramitMapeigAdjunts() != null) {
					String[] parts = expedientTipus.getSistraTramitMapeigAdjunts().split(";");
					for (int i = 0; i < parts.length; i++) {
						String varSistra = parts[i];
						if (varSistra != null && (!"".equalsIgnoreCase(varSistra))) {
							if (dissenyService.findMapeigSistraAmbExpedientTipusICodi(expedientTipus.getId(), varSistra) == null) {
								dissenyService.createMapeigSistra(varSistra, varSistra, MapeigSistra.TipusMapeig.Adjunt, expedientTipus);
							}
						}
					}
				}
			}
		}
	}
	private void canviarMapeigEnumeracionsV210() throws Exception {
		List<Enumeracio> enumeracions = dissenyService.findEnumeracions();
		if (enumeracions.size() > 0) {
			for (Enumeracio enumeracio : enumeracions) {
				if ((enumeracio.getValors() != null) && (!enumeracio.getValors().equals(""))) {
					List<ParellaCodiValor> valors = enumeracio.getLlistaValors();
					for (ParellaCodiValor parella : valors) {
						EnumeracioValors enumeracioValors = new EnumeracioValors();
						enumeracioValors.setCodi(parella.getCodi());
						enumeracioValors.setNom((String)parella.getValor());
						enumeracioValors.setEnumeracio(enumeracio);
						dissenyService.createEnumeracioValors(enumeracioValors);
					}
				}
			}
		}
	}

	private boolean actualitzarV220() {
		boolean actualitzat = false;
		Versio versio220 = obtenirOCrearVersio(VERSIO_210_STR, VERSIO_220_ORDRE);
		if (!versio220.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_220_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio220.isProcesExecutat()) {
			try {
				versio220.setProcesExecutat(true);
				versio220.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio220);
				logger.info("Actualització a la versió " + VERSIO_220_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_220_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_220_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}

	private boolean actualitzarV221() {
		boolean actualitzat = false;
		Versio versio221 = obtenirOCrearVersio(VERSIO_221_STR, VERSIO_221_ORDRE);
		if (!versio221.isScriptExecutat()) {
			errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_221_STR + ": " + getMessage("error.update.script.ko");
		} else if (!versio221.isProcesExecutat()) {
			try {
				actualitzarOrdreValorsEnumeracionsV221();
				versio221.setProcesExecutat(true);
				versio221.setDataExecucioProces(new Date());
				versioDao.saveOrUpdate(versio221);
				logger.info("Actualització a la versió " + VERSIO_221_STR + " realitzada correctament");
				actualitzat = true;
			} catch (Exception ex) {
				logger.error("Error al executar l'actualització a la versió " + VERSIO_221_STR, ex);
				errorUpdate =  getMessage("error.update.actualitzar.versio") + " " + VERSIO_221_STR + ": " + getMessage("error.update.proces.ko");
			}
		}
		return actualitzat;
	}
	private void actualitzarOrdreValorsEnumeracionsV221() {
		List<Enumeracio> enumeracions = dissenyService.findEnumeracions();
		for (Enumeracio enumeracio: enumeracions) {
			int i = 0;
			for (EnumeracioValors valor: enumeracio.getEnumeracioValors()) {
				valor.setOrdre(i++);
			}
		}
	}

	private String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	private String getMessage(String key) {
		return getMessage(key, null);
	}

	private Versio obtenirOCrearVersio(String codi, Integer ordre) {
		Versio versio = versioDao.findAmbCodi(codi);
		if (versio == null) {
			versio = new Versio(codi, ordre);
			versioDao.saveOrUpdate(versio);
		}
		return versio;
	}

	private static final Log logger = LogFactory.getLog(UpdateService.class);

}
