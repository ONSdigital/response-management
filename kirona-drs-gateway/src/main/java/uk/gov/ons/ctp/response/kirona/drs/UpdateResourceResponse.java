
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateResourceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateResourceResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}xmbUpdateResourceResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateResourceResponse", propOrder = {
    "_return"
})
public class UpdateResourceResponse {

    @XmlElement(name = "return")
    protected XmbUpdateResourceResponse _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link XmbUpdateResourceResponse }
     *     
     */
    public XmbUpdateResourceResponse getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmbUpdateResourceResponse }
     *     
     */
    public void setReturn(XmbUpdateResourceResponse value) {
        this._return = value;
    }

}
