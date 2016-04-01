
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for onlyBestSlotsValue.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="onlyBestSlotsValue">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="no"/>
 *     &lt;enumeration value="day"/>
 *     &lt;enumeration value="fullPeriod"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "onlyBestSlotsValue")
@XmlEnum
public enum OnlyBestSlotsValue {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("no")
    NO("no"),
    @XmlEnumValue("day")
    DAY("day"),
    @XmlEnumValue("fullPeriod")
    FULL_PERIOD("fullPeriod");
    private final String value;

    OnlyBestSlotsValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OnlyBestSlotsValue fromValue(String v) {
        for (OnlyBestSlotsValue c: OnlyBestSlotsValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
