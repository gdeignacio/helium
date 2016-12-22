/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.core.model.hibernate.Remesa;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.NotificacioRepository;
import net.conselldemallorca.helium.v3.core.repository.RemesaRepository;

/**
 * Helper per a gestionar els entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {

	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private RemesaRepository remesaRepository;


	public Notificacio create(
			ExpedientDto expedient,
			NotificacioDto notificacioDto) {
		Notificacio notificacio = conversioTipusHelper.convertir(notificacioDto, Notificacio.class);
		notificacio.setExpedient(expedientRepository.findOne(expedient.getId()));
		notificacio.setDocument(documentStoreRepository.findById(notificacioDto.getDocument().getId()));
		
		List<DocumentStore> annexos = new ArrayList<DocumentStore>();
		for (DocumentNotificacioDto annex: notificacioDto.getAnnexos()) {
			annexos.add(documentStoreRepository.findById(annex.getId()));
		}
		notificacio.setAnnexos(annexos);
		
		return notificacioRepository.save(notificacio);
	}
	
	public List<Notificacio> findNotificacionsPerExpedientId(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId, 
				true, 
				false, 
				false, 
				false);
		
		return notificacioRepository.findByExpedientOrderByDataEnviamentDesc(expedient);
	}
	
	public List<Notificacio> findNotificacionsPerExpedientIdITipus(Long expedientId, DocumentNotificacioTipusEnumDto tipus) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId, 
				true, 
				false, 
				false, 
				false);
		
		return notificacioRepository.findByExpedientAndTipusOrderByDataEnviamentDesc(expedient, tipus);
	}
	
	public void obtenirJustificantNotificacio(Notificacio notificacio) {
		try {
			RespostaJustificantRecepcio resposta = pluginHelper.tramitacioObtenirJustificant(notificacio.getRegistreNumero());
			if (resposta != null && resposta.getData() != null)
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_OK);
		} catch (Exception ex) {
			logger.error(
					"Error actualitzant estat notificacio " + notificacio.getRegistreNumero(),
					ex);
			notificacio.setError(ex.getMessage());
			notificacio.setEstat(DocumentEnviamentEstatEnumDto.PROCESSAT_ERROR);
		}
	}

	public boolean delete(
			String numero,
			String clave,
			Long codigo) {
		Notificacio notificacio = notificacioRepository.findByRegistreNumeroAndRdsCodiAndRdsClau(
				numero,
				codigo,
				clave);
		if (notificacio != null) {
			notificacioRepository.delete(notificacio);
			return true;
		}
		return false;
	}
	
	
	public void enviarRemesa(
			String remesaCodi, 
			Date dataEmisio, 
			Date dataPrevistaDeposit, 
			Long expedientTipusId, 
			List<Long> expedientIds) throws Exception {
		
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		
		String codiProducte = fragmentFitxer(expedientTipus.getSicerProducteCodi(), 2, false);
		String codiClient = fragmentFitxer(expedientTipus.getSicerClientCodi(), 8, false);
		String codiRemesa = fragmentFitxer(remesaCodi, 4, false);
		
		Date dataFitxer = new Date();
	    Calendar fitxerData = Calendar.getInstance();
	    fitxerData.setTime(dataFitxer);
	    String fitxerAnyMesDia = anyMesDiaData(fitxerData);
		
		String nomFitxer = 
				codiProducte + 
				codiClient + 
				fitxerAnyMesDia +
				"." +
				fragmentFitxer(fitxerData.get(Calendar.HOUR_OF_DAY), 2, true) + fragmentFitxer(fitxerData.get(Calendar.MINUTE), 2, true);
		
		PrintWriter fitxer = null;
		File file = null;
		
		try {
			file = new File(nomFitxer);
			fitxer = new PrintWriter(file);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	    
		Calendar emisioData = Calendar.getInstance();
		emisioData.setTime(dataEmisio);
		String emisioAnyMesDia = anyMesDiaData(emisioData);
		    
		Calendar previstaDepositData = Calendar.getInstance();
		previstaDepositData.setTime(dataPrevistaDeposit);
		String previstaDepositAnyMesDia = anyMesDiaData(previstaDepositData);
		
		String headerFitxer = "FN";
		headerFitxer += codiProducte;
		headerFitxer += codiClient;
		headerFitxer += fragmentFitxer(expedientTipus.getSicerPuntAdmissioCodi(), 7, false);
		headerFitxer += fitxerAnyMesDia;
		headerFitxer += fragmentFitxer(fitxerData.get(Calendar.HOUR_OF_DAY), 2, true) + ":" + fragmentFitxer(fitxerData.get(Calendar.MINUTE), 2, true);
		headerFitxer = String.format("%1$-" + 315 + "s", headerFitxer);
		fitxer.println(headerFitxer);
		
		String headerRemesa = "C";
		headerRemesa += codiProducte;
		headerRemesa += codiClient;
		headerRemesa += codiRemesa;
		headerRemesa += emisioAnyMesDia;
		headerRemesa += previstaDepositAnyMesDia;
		headerRemesa = String.format("%1$-" + 315 + "s", headerRemesa);
		fitxer.println(headerRemesa);
		
		List<Notificacio> notificacionsPerEnviar = new ArrayList<Notificacio>();
		
		int countDetalls = 0;
		for (Long expeidentId: expedientIds) {
				
			Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
					expeidentId, 
					true, 
					false, 
					false, 
					false);
			
			List<Notificacio> notificacionsSicer = findNotificacionsPerExpedientIdITipus(expedient.getId(), DocumentNotificacioTipusEnumDto.SICER);
			for (Notificacio notificacio: notificacionsSicer) {
				String detall = "D";
				detall += codiProducte;
				detall += codiClient;
				detall += codiRemesa;
				detall += (codiRemesa + String.format("%05d", countDetalls));
				detall += fragmentFitxer(expedientTipus.getSicerNomLlinatges(), 50, false);
				detall += String.format("%" + 50 + "s", "");
				detall += fragmentFitxer(expedientTipus.getSicerDireccio(), 50, false);
				detall += fragmentFitxer(expedientTipus.getSicerPoblacio(), 40, false);
				detall += fragmentFitxer(expedientTipus.getSicerCodiPostal(), 5, true);
				detall += String.format("%" + 46 + "s", "");
				detall += fragmentFitxer(notificacio.getExpedient().getId(), 41, false);
				detall = String.format("%1$-" + 315 + "s", detall);
				fitxer.println(detall);
				countDetalls++;
				
				notificacionsPerEnviar.add(notificacio);
			}
		}
		
		String totalDetalls = fragmentFitxer(countDetalls, 9, true);
		
		String footerRemesa = "c";
		footerRemesa += codiProducte;
		footerRemesa += codiClient;
		footerRemesa += codiRemesa;
		footerRemesa += totalDetalls;
		footerRemesa = String.format("%1$-" + 315 + "s", footerRemesa);
		fitxer.println(footerRemesa);
		
		String footerFitxer = "f";
		footerFitxer += codiProducte;
		footerFitxer += codiClient;
		footerFitxer += String.format("%03d", 1);
		footerFitxer += totalDetalls;
		footerFitxer = String.format("%1$-" + 315 + "s", footerFitxer);
		fitxer.println(footerFitxer);
		
		fitxer.close();
		
		/**enviem el fitxer i creem la remesa**/
		boolean fitxerEnviat = enviamentFitxerSftpSicer(file);
		
		Date dataEnviament = new Date();
		
		Remesa remesa = remesaRepository.findByCodiAndExpedientTipus(codiRemesa, expedientTipus);
		
		if (remesa == null) {
			remesa = new Remesa();
			remesa.setCodi(codiRemesa);
			remesa.setDataCreacio(dataEnviament);
			remesa.setDataEmisio(dataEmisio);
			remesa.setDataPrevistaDeposit(dataPrevistaDeposit);
			remesa.setExpedientTipus(expedientTipus);
		}
		
		if (fitxerEnviat) {
			remesa.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
		} else {
			remesa.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT_ERROR);
		}
		remesa.setDataEnviament(dataEnviament);
		
		remesaRepository.save(remesa);
		/********************/
		for (Notificacio notificacio: notificacionsPerEnviar) {
			
			notificacio.setRemesa(remesa);
			if (fitxerEnviat) {
				notificacio.setDataEnviament(dataEnviament);
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT);
			} else {
				notificacio.setEstat(DocumentEnviamentEstatEnumDto.ENVIAT_ERROR);
			}
		}		
	}
	
	public void comprovarRemesaEnviada(Remesa remesa) {
		String nomFitxer = "";
		BufferedReader br = obtenirFitxerSftpSicer(nomFitxer);
		
		String sCurrentLine;
		try {
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void comprovarRemesaValidada(Remesa remesa) {
		
	}
	
	
	@SuppressWarnings("null")
	private boolean enviamentFitxerSftpSicer(File fitxer) {
		boolean enviat = false;
        String SFTPWORKINGDIR = "/home/limit/sftp-proves/";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        openSftpConnection(session, channel, channelSftp);
        try {
            channelSftp.cd(SFTPWORKINGDIR);
            channelSftp.put(new FileInputStream(fitxer), fitxer.getName());
            System.out.println("File transfered successfully to host.");
            enviat = true;
        } catch (Exception ex) {
             System.out.println("Exception found while tranfer the response.");
             ex.printStackTrace();
        }
        finally{
            closeSftpConnection(session, channel, channelSftp);
        }
        return enviat;
	}
	
	@SuppressWarnings("null")
	private BufferedReader obtenirFitxerSftpSicer(String fileName) {
		String SFTPWORKINGDIR = "/home/limit/sftp-proves/";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        openSftpConnection(session, channel, channelSftp);
        BufferedReader br = null;
        try {
            channelSftp.cd(SFTPWORKINGDIR);
            InputStream stream = channelSftp.get(fileName);
            br = new BufferedReader(new InputStreamReader(stream));
        } catch (Exception ex) {
             System.out.println("Exception found while tranfer the response.");
             ex.printStackTrace();
        }
        finally{
            closeSftpConnection(session, channel, channelSftp);
        }
        return br;
	}
	
	private void openSftpConnection(Session session, Channel channel, ChannelSftp channelSftp) {
		
		String SFTPHOST = "10.35.3.243";
        int SFTPPORT = 22;
        String SFTPUSER = "limit";
        String SFTPPASS = "tecnologies";

        System.out.println("preparing the host information for sftp.");
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
        } catch (Exception ex) {
             System.out.println("Exception found while tranfer the response.");
             ex.printStackTrace();
        }
	}
	
	private void closeSftpConnection(Session session, Channel channel, ChannelSftp channelSftp) {
		channelSftp.exit();
        System.out.println("sftp Channel exited.");
        channel.disconnect();
        System.out.println("Channel disconnected.");
        session.disconnect();
        System.out.println("Host Session disconnected.");
	}
	
	
	
	private String fragmentFitxer(Object value, int maxLength, boolean isNumeric) {
		if (String.valueOf(value).length() > maxLength) {
			value = String.valueOf(value).substring(0, maxLength);
		} else if (String.valueOf(value).length() < maxLength) {
			if (isNumeric) {
				value = (Integer)value;
				value = String.format("%0" + maxLength + "d", value);
			} else {
				value = String.format("%1$-" + maxLength + "s", value);
			}
		}
		return String.valueOf(value);
	}
	
	private String anyMesDiaData(Calendar data) {
		return fragmentFitxer(data.get(Calendar.YEAR), 4, true) + fragmentFitxer((data.get(Calendar.MONTH) + 1), 2, true) + fragmentFitxer(data.get(Calendar.DAY_OF_MONTH), 2, true);
	}

	private static final Log logger = LogFactory.getLog(NotificacioHelper.class);
}
