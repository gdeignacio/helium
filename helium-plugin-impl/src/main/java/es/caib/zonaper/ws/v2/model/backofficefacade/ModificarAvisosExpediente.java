
package es.caib.zonaper.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="unidadAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="identificadorExpediente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="claveExpediente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="configuracionAvisosExpediente" type="{urn:es:caib:zonaper:ws:v2:model:ConfiguracionAvisosExpediente}ConfiguracionAvisosExpediente"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "unidadAdministrativa",
    "identificadorExpediente",
    "claveExpediente",
    "configuracionAvisosExpediente"
})
@XmlRootElement(name = "modificarAvisosExpediente")
public class ModificarAvisosExpediente {

    protected long unidadAdministrativa;
    @XmlElement(required = true)
    protected String identificadorExpediente;
    @XmlElement(required = true)
    protected String claveExpediente;
    @XmlElement(required = true)
    protected ConfiguracionAvisosExpediente configuracionAvisosExpediente;

    /**
     * Gets the value of the unidadAdministrativa property.
     * 
     */
    public long getUnidadAdministrativa() {
        return unidadAdministrativa;
    }

    /**
     * Sets the value of the unidadAdministrativa property.
     * 
     */
    public void setUnidadAdministrativa(long value) {
        this.unidadAdministrativa = value;
    }

    /**
     * Gets the value of the identificadorExpediente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorExpediente() {
        return identificadorExpediente;
    }

    /**
     * Sets the value of the identificadorExpediente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorExpediente(String value) {
        this.identificadorExpediente = value;
    }

    /**
     * Gets the value of the claveExpediente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveExpediente() {
        return claveExpediente;
    }

    /**
     * Sets the value of the claveExpediente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveExpediente(String value) {
        this.claveExpediente = value;
    }

    /**
     * Gets the value of the configuracionAvisosExpediente property.
     * 
     * @return
     *     possible object is
     *     {@link ConfiguracionAvisosExpediente }
     *     
     */
    public ConfiguracionAvisosExpediente getConfiguracionAvisosExpediente() {
        return configuracionAvisosExpediente;
    }

    /**
     * Sets the value of the configuracionAvisosExpediente property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfiguracionAvisosExpediente }
     *     
     */
    public void setConfiguracionAvisosExpediente(ConfiguracionAvisosExpediente value) {
        this.configuracionAvisosExpediente = value;
    }

}
