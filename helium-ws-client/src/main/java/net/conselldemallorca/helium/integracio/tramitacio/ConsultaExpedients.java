
package net.conselldemallorca.helium.integracio.tramitacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for consultaExpedients complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultaExpedients">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg4" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="arg5" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="arg6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg7" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arg8" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg9" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="arg10" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="arg11" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="arg12" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaExpedients", propOrder = {
    "arg0",
    "arg1",
    "arg2",
    "arg3",
    "arg4",
    "arg5",
    "arg6",
    "arg7",
    "arg8",
    "arg9",
    "arg10",
    "arg11",
    "arg12"
})
public class ConsultaExpedients {

    protected String arg0;
    protected String arg1;
    protected String arg2;
    protected String arg3;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar arg4;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar arg5;
    protected String arg6;
    protected String arg7;
    protected boolean arg8;
    protected boolean arg9;
    protected Double arg10;
    protected Double arg11;
    protected String arg12;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg0(String value) {
        this.arg0 = value;
    }

    /**
     * Gets the value of the arg1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg1() {
        return arg1;
    }

    /**
     * Sets the value of the arg1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg1(String value) {
        this.arg1 = value;
    }

    /**
     * Gets the value of the arg2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg2() {
        return arg2;
    }

    /**
     * Sets the value of the arg2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg2(String value) {
        this.arg2 = value;
    }

    /**
     * Gets the value of the arg3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg3() {
        return arg3;
    }

    /**
     * Sets the value of the arg3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg3(String value) {
        this.arg3 = value;
    }

    /**
     * Gets the value of the arg4 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getArg4() {
        return arg4;
    }

    /**
     * Sets the value of the arg4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setArg4(XMLGregorianCalendar value) {
        this.arg4 = value;
    }

    /**
     * Gets the value of the arg5 property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getArg5() {
        return arg5;
    }

    /**
     * Sets the value of the arg5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setArg5(XMLGregorianCalendar value) {
        this.arg5 = value;
    }

    /**
     * Gets the value of the arg6 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg6() {
        return arg6;
    }

    /**
     * Sets the value of the arg6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg6(String value) {
        this.arg6 = value;
    }

    /**
     * Gets the value of the arg7 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg7() {
        return arg7;
    }

    /**
     * Sets the value of the arg7 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg7(String value) {
        this.arg7 = value;
    }

    /**
     * Gets the value of the arg8 property.
     * 
     */
    public boolean isArg8() {
        return arg8;
    }

    /**
     * Sets the value of the arg8 property.
     * 
     */
    public void setArg8(boolean value) {
        this.arg8 = value;
    }

    /**
     * Gets the value of the arg9 property.
     * 
     */
    public boolean isArg9() {
        return arg9;
    }

    /**
     * Sets the value of the arg9 property.
     * 
     */
    public void setArg9(boolean value) {
        this.arg9 = value;
    }

    /**
     * Gets the value of the arg10 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getArg10() {
        return arg10;
    }

    /**
     * Sets the value of the arg10 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setArg10(Double value) {
        this.arg10 = value;
    }

    /**
     * Gets the value of the arg11 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getArg11() {
        return arg11;
    }

    /**
     * Sets the value of the arg11 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setArg11(Double value) {
        this.arg11 = value;
    }

    /**
     * Gets the value of the arg12 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArg12() {
        return arg12;
    }

    /**
     * Sets the value of the arg12 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArg12(String value) {
        this.arg12 = value;
    }

}
