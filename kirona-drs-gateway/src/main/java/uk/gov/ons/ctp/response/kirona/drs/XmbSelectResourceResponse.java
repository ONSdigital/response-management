
package uk.gov.ons.ctp.response.kirona.drs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmbSelectResourceResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmbSelectResourceResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://autogenerated.OTWebServiceApi.xmbrace.com/}commandResponse">
 *       &lt;sequence>
 *         &lt;element name="theResources" type="{http://autogenerated.OTWebServiceApi.xmbrace.com/}resource" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmbSelectResourceResponse", propOrder = {
    "theResources"
})
public class XmbSelectResourceResponse
    extends CommandResponse
{

    protected List<Resource> theResources;

    /**
     * Gets the value of the theResources property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the theResources property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTheResources().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Resource }
     * 
     * 
     */
    public List<Resource> getTheResources() {
        if (theResources == null) {
            theResources = new ArrayList<Resource>();
        }
        return this.theResources;
    }

}
