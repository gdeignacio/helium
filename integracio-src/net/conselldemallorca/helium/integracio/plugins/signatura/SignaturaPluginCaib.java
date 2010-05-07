/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import es.caib.signatura.api.SignatureException;
import es.caib.signatura.api.Signer;
import es.caib.signatura.api.SignerFactory;
import es.caib.signatura.api.UpgradeNeededException;

/**
 * Implementació del plugin de firma per la CAIB
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SignaturaPluginCaib implements SignaturaPlugin {

	private Signer signer;



	public void testSigner() throws NecessitaActualitzarException {
		getSigner();
	}

	public String[] getCertList(String params) throws ObtencioCertificatsException {
		try {
			return getSigner().getCertList(params);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ObtencioCertificatsException("No s'ha pogut obtenir la llista de certificats disponibles", ex);
		}
	}

	public Object sign(
			InputStream inputDocument,
			String certName,
			String password,
			String params) throws SignaturaException, ContrasenyaIncorrectaException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			getSigner().signPDF(
					inputDocument,
					baos,
					certName,
					new String(password),
					params,
					"http://www.limit.es",
					Signer.PDF_SIGN_POSITION_NONE,
					true);
			return baos.toByteArray();
			/*return getSigner().sign(
					inputDocument,
					certName,
					new String(password),
					params);*/
		} catch (SignatureException ex) {
			ex.printStackTrace();
			throw new ContrasenyaIncorrectaException("Contrasenya incorrecta", ex);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SignaturaException("No s'ha pogut signar el document", ex);
		}
	}

	public URL getUrlActualitzacio() {
		try {
			return new URL("http://www.caib.es/signaturacaib/");
		} catch (Exception ignored) {}
		return null;
	}



	private Signer getSigner() throws NecessitaActualitzarException {
		if (signer == null) {
			SignerFactory sf = new SignerFactory();
			try {
				return sf.getSigner();
			} catch (UpgradeNeededException ex) {
				throw new NecessitaActualitzarException("No s'ha pogut inicialitzar la instància del signer", getUrlActualitzacio(), ex);
			}
		}
		return signer;
	}

}
