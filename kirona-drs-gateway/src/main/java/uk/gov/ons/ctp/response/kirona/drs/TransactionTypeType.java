
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="transactionTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="PLANNED"/>
 *     &lt;enumeration value="DESPATCHED"/>
 *     &lt;enumeration value="RESERVED"/>
 *     &lt;enumeration value="ACKNOWLEDGED"/>
 *     &lt;enumeration value="ACCEPTED"/>
 *     &lt;enumeration value="ARRIVED"/>
 *     &lt;enumeration value="STARTED"/>
 *     &lt;enumeration value="COMPLETED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "transactionTypeType")
@XmlEnum
public enum TransactionTypeType {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    PLANNED("PLANNED"),
    DESPATCHED("DESPATCHED"),
    RESERVED("RESERVED"),
    ACKNOWLEDGED("ACKNOWLEDGED"),
    ACCEPTED("ACCEPTED"),
    ARRIVED("ARRIVED"),
    STARTED("STARTED"),
    COMPLETED("COMPLETED");
    private final String value;

    TransactionTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransactionTypeType fromValue(String v) {
        for (TransactionTypeType c: TransactionTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
