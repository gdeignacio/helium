package net.conselldemallorca.helium.integracio.plugins.registre;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.regweb.ws.model.ListaResultados;
import es.caib.regweb.ws.model.ParametrosRegistroSalidaWS;
import es.caib.regweb.ws.services.regwebfacade.RegwebFacadeServiceLocator;
import es.caib.regweb.ws.services.regwebfacade.RegwebFacade_PortType;
import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
import es.caib.regweb3.ws.api.v3.IdentificadorWs;
import es.caib.regweb3.ws.api.v3.InteresadoWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroEntradaWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.RegistroSalidaWs;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;
import es.caib.regweb3.ws.api.v3.utils.WsClientUtils;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.registre.RegistreInteressatTipusEnum;


/**
 * Implementació del plugin de registre per a la interficie de
 * serveis web del registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegweb3 extends RegWeb3Utils implements RegistrePluginRegWeb3{
	
	protected static RegWebRegistroEntradaWs registroEntradaApi;
	protected static RegWebRegistroSalidaWs registroSalidaApi;

	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
        registroSalidaApi = getRegistroSalidaApi();
    }

	public RespostaAnotacioRegistre registrarSortida(
			RegistreAssentament registreSortida) throws RegistrePluginException {
		RegistroSalidaWs registroSalidaWs = new RegistroSalidaWs();

        registroSalidaWs.setOrigen(registreSortida.getOrgan());
        registroSalidaWs.setOficina(registreSortida.getOficinaCodi());
        registroSalidaWs.setLibro(registreSortida.getLlibreCodi());

        registroSalidaWs.setExtracto(registreSortida.getExtracte());
        registroSalidaWs.setDocFisica(registreSortida.getDocumentacioFisicaCodi() != null ? new Long(registreSortida.getDocumentacioFisicaCodi()) : (long)3);
        registroSalidaWs.setIdioma(registreSortida.getIdiomaCodi());
        registroSalidaWs.setTipoAsunto(registreSortida.getAssumpteTipusCodi());

        registroSalidaWs.setAplicacion("Helium");
        registroSalidaWs.setVersion("3.2");

        registroSalidaWs.setCodigoUsuario(registreSortida.getUsuariCodi());
        registroSalidaWs.setContactoUsuario(registreSortida.getUsuariContacte());

        registroSalidaWs.setNumExpediente(registreSortida.getExpedientNumero());
//        registroSalidaWs.setNumTransporte("");
//        registroSalidaWs.setObservaciones("");

//        registroSalidaWs.setRefExterna("");
//        registroSalidaWs.setCodigoAsunto(null);
//        registroSalidaWs.setTipoTransporte("");

//        registroSalidaWs.setExpone("expone");
//        registroSalidaWs.setSolicita("solicita");

        // Interesados
        for (RegistreAssentamentInteressat inter: registreSortida.getInteressats()) {
        	InteresadoWs interesadoWs = new InteresadoWs();

            DatosInteresadoWs interesado = new DatosInteresadoWs();
            interesado.setTipoInteresado((long)RegistreInteressatTipusEnum.valorAsEnum(inter.getTipus()).ordinal());
            interesado.setTipoDocumentoIdentificacion(inter.getDocumentTipus());
            interesado.setDocumento(inter.getDocumentNum());
            interesado.setEmail(inter.getEmail());
            interesado.setNombre(inter.getNom());
            interesado.setApellido1(inter.getLlinatge1());
            interesado.setApellido2(inter.getLlinatge2());
            interesado.setPais(inter.getPais() != null ? new Long(inter.getPais()) : null);
            interesado.setProvincia(inter.getProvincia() != null ? new Long(inter.getProvincia()) : null);
            interesadoWs.setInteresado(interesado);

            if (inter.getRepresentant() != null) {
            	RegistreAssentamentInteressat repre = inter.getRepresentant();
	            DatosInteresadoWs representante = new DatosInteresadoWs();
	            representante.setTipoInteresado((long)RegistreInteressatTipusEnum.valorAsEnum(repre.getTipus()).ordinal());
	            representante.setTipoDocumentoIdentificacion(repre.getDocumentTipus());
	            representante.setDocumento(repre.getDocumentNum());
	            representante.setEmail(repre.getEmail());
	            representante.setNombre(repre.getNom());
	            representante.setApellido1(repre.getLlinatge1());
	            representante.setApellido2(repre.getLlinatge2());
	            representante.setPais(repre.getPais() != null ? new Long(repre.getPais()) : null);
	            representante.setProvincia(repre.getProvincia() != null ? new Long(repre.getProvincia()) : null);
	            interesadoWs.setRepresentante(representante);
            }
            
            registroSalidaWs.getInteresados().add(interesadoWs);
        }

       
        try {
            IdentificadorWs identificadorWs = registroSalidaApi.altaRegistroSalida(registroSalidaWs);
            System.out.println("NumeroSalida: " + identificadorWs.getNumero());
            System.out.println("Fecha: " + identificadorWs.getFecha());
        } catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistrePluginException("Error WsValidationException: " + msg);
        }
        return null;
	}

	
	
	public RespostaConsulta consultarSortida(
			String organCodi,
			String oficinaCodi,
			String registreNumero) throws RegistrePluginException {
		try {
			ParametrosRegistroSalidaWS paramsws = new ParametrosRegistroSalidaWS();
			paramsws.setUsuarioConexion(getUsuarioConexion());
			paramsws.setPassword(getPassword());
			paramsws.setUsuarioRegistro(getUsuariRegistre());
			paramsws.setOficina(organCodi);
			paramsws.setOficinafisica(oficinaCodi);
			int index = registreNumero.indexOf(SEPARADOR_NUMERO);
			if (index == -1)
				throw new RegistrePluginException("El número de registre a consultar (" + registreNumero + ") no té el format correcte");
			paramsws.setNumeroSalida(registreNumero.substring(0, index));
			paramsws.setAnoSalida(registreNumero.substring(index + 1));
			ParametrosRegistroSalidaWS llegit = getRegistreService().leerSalida(paramsws);
			RespostaConsulta resposta = new RespostaConsulta();
			resposta.setRegistreNumero(registreNumero);
			resposta.setRegistreData(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(llegit.getDatasalida() + " " + llegit.getHora()));
			DadesOficina dadesOficina = new DadesOficina();
			dadesOficina.setOrganCodi(llegit.getRemitent());
			dadesOficina.setOficinaCodi(llegit.getOficina() + SEPARADOR_ENTITAT + llegit.getOficinafisica());
			resposta.setDadesOficina(dadesOficina);
			DadesInteressat dadesInteressat = new DadesInteressat();
			if (llegit.getEntidad1() != null && !"".equals(llegit.getEntidad1()))
				dadesInteressat.setEntitatCodi(
						llegit.getEntidad1() + SEPARADOR_ENTITAT + llegit.getEntidad2());
			dadesInteressat.setNomAmbCognoms(llegit.getAltres());
			dadesInteressat.setMunicipiCodi(llegit.getBalears());
			dadesInteressat.setMunicipiNom(llegit.getFora());
			resposta.setDadesInteressat(dadesInteressat);
			DadesAssumpte dadesAssumpte = new DadesAssumpte();
			dadesAssumpte.setUnitatAdministrativa(llegit.getRemitent());
			dadesAssumpte.setIdiomaCodi(llegit.getIdioex());
			dadesAssumpte.setTipus(llegit.getTipo());
			dadesAssumpte.setAssumpte(llegit.getComentario());
			resposta.setDadesAssumpte(dadesAssumpte);
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			DocumentRegistre document = new DocumentRegistre();
			document.setIdiomaCodi(llegit.getIdioma());
			if (llegit.getData() != null)
				document.setData(new SimpleDateFormat("dd/MM/yyyy").parse(llegit.getData()));
			documents.add(document);
			resposta.setDocuments(documents);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al consultar la sortida", ex);
			throw new RegistrePluginException("Error al consultar la sortida", ex);
		}
	}

	public RespostaAnotacioRegistre registrarNotificacio(
			RegistreNotificacio registreNotificacio) throws RegistrePluginException {
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
	}
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) throws RegistrePluginException {
		throw new RegistrePluginException("Mètode no implementat en aquest plugin");
	}

	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		try {
			if (oficinaCodi != null) {
				int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
				if (indexBarra != -1) {
					ListaResultados lr = getRegistreService().buscarOficinasFisicasDescripcion(
							getUsuarioConexion(),
							getPassword(),
							"tots",
							"totes");
					Iterator<String> it = Arrays.asList(lr.getResultado()).iterator();
					while (it.hasNext()) {
						String codiOficina = it.next();
						String codiOficinaFisica = it.next();
						@SuppressWarnings("unused")
						String nomOficinaFisica = it.next();
						String nomOficina = it.next();
						String textComparacio = codiOficina + SEPARADOR_ENTITAT + codiOficinaFisica;
						if (textComparacio.equals(oficinaCodi))
							return nomOficina;
					}
				}
			}
			return null;
		} catch (Exception ex) {
			logger.error("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
			throw new RegistrePluginException("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
		}
	}



	private RegwebFacade_PortType getRegistreService() throws Exception {
		String url = GlobalProperties.getInstance().getProperty("app.registre.plugin.ws.url") + "?wsdl";
	    RegwebFacadeServiceLocator service = new RegwebFacadeServiceLocator();
	    service.setRegwebFacadeEndpointAddress(url);
	    return service.getRegwebFacade();
	}

	private String getUsuariRegistre() {
		String usuari = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null)
			usuari = auth.getName();
		return usuari;
		/*if (usuari != null)
			return usuari;
		else
			return getUsuarioConexion();*/
	}
	private String getUsuarioConexion() {
		return GlobalProperties.getInstance().getProperty("app.registre.plugin.ws.usuari");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.registre.plugin.ws.password");
	}


	private static final Log logger = LogFactory.getLog(RegistrePluginRegweb3.class);

}
