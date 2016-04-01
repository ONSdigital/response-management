
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sectorPriority.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="sectorPriority">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="PRIMARY"/>
 *     &lt;enumeration value="SECONDARY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "sectorPriority")
@XmlEnum
public enum SectorPriority {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    PRIMARY("PRIMARY"),
    SECONDARY("SECONDARY");
    private final String value;

    SectorPriority(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SectorPriority fromValue(String v) {
        for (SectorPriority c: SectorPriority.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
