
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for availableValue.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="availableValue">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="NO"/>
 *     &lt;enumeration value="YES"/>
 *     &lt;enumeration value="MAYBE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "availableValue")
@XmlEnum
public enum AvailableValue {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    NO("NO"),
    YES("YES"),
    MAYBE("MAYBE");
    private final String value;

    AvailableValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AvailableValue fromValue(String v) {
        for (AvailableValue c: AvailableValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
