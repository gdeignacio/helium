/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;

import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPlugin;
import net.conselldemallorca.helium.integracio.plugins.gesdoc.GestioDocumentalPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Dao per accedir a la funcionalitat del plugin de gestió documental
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginGestioDocumentalDao {

	private GestioDocumentalPlugin gestioDocumentalPlugin;



	public String createDocument(
			Expedient expedient,
			String documentCodi,
			String documentDescripcio,
			Date documentData,
			String documentArxiuNom,
			byte[] documentArxiuContingut) {
		try {
			String expedientTipus = null;
			if (isTipusExpedientDirecte()) {
				expedientTipus = expedient.getTipus().getCodi();
			} else if (isTipusExpedientNou()) {
				expedientTipus = expedient.getEntorn().getCodi() + "_" + expedient.getTipus().getCodi();
			} else {
				expedientTipus = expedient.getEntorn().getCodi() + "#" + expedient.getTipus().getCodi();
			}
			return getGestioDocumentalPlugin().createDocument(
					expedient.getNumeroIdentificador(),
					expedientTipus,
					documentCodi,
					documentDescripcio,
					documentData,
					documentArxiuNom,
					documentArxiuContingut);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al guardar el document a la gestió documental", ex);
			throw new PluginException("Error al guardar el document a la gestió documental", ex);
		}
	}

	public byte[] retrieveDocument(String documentId) {
		try {
			return getGestioDocumentalPlugin().retrieveDocument(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al obtenir el document de la gestió documental", ex);
			throw new PluginException("Error al obtenir el document de la gestió documental", ex);
		}
	}

	public void deleteDocument(String documentId) {
		try {
			if (isGestioDocumentalActiu())
				getGestioDocumentalPlugin().deleteDocument(documentId);
		} catch (GestioDocumentalPluginException ex) {
			logger.error("Error al esborrar el document de la gestió documental", ex);
			throw new PluginException("Error al esborrar el document de la gestió documental", ex);
		}
	}

	public boolean isGestioDocumentalActiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
		if (pluginClass == null || pluginClass.length() == 0) {
			String alfrescoActiu = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.actiu");
			if ("true".equals(alfrescoActiu))
				pluginClass = "net.conselldemallorca.helium.integracio.gesdoc.GestioDocumentalPluginAlfrescoCaib";
		}
		return (pluginClass != null && pluginClass.length() > 0);
	}



	@SuppressWarnings("rawtypes")
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (gestioDocumentalPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de gestió documental", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de gestió documental", ex);
				}
			}
		}
		return gestioDocumentalPlugin;
	}

	private boolean isTipusExpedientNou() {
		return "true".equalsIgnoreCase(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.tipus.nou"));
	}

	private boolean isTipusExpedientDirecte() {
		return "true".equalsIgnoreCase(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.tipus.directe"));
	}

	private static final Log logger = LogFactory.getLog(PluginGestioDocumentalDao.class);

}
