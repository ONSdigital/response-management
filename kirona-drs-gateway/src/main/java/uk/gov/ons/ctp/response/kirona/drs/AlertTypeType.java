
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alertTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="alertTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="RESCHEDULEFAILURES"/>
 *     &lt;enumeration value="SUSPENDEDJOBS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "alertTypeType")
@XmlEnum
public enum AlertTypeType {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    RESCHEDULEFAILURES("RESCHEDULEFAILURES"),
    SUSPENDEDJOBS("SUSPENDEDJOBS");
    private final String value;

    AlertTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AlertTypeType fromValue(String v) {
        for (AlertTypeType c: AlertTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
