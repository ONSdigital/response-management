
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for objectType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="objectType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="customer"/>
 *     &lt;enumeration value="serviceOrder"/>
 *     &lt;enumeration value="job"/>
 *     &lt;enumeration value="worker"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "objectType")
@XmlEnum
public enum ObjectType {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("customer")
    CUSTOMER("customer"),
    @XmlEnumValue("serviceOrder")
    SERVICE_ORDER("serviceOrder"),
    @XmlEnumValue("job")
    JOB("job"),
    @XmlEnumValue("worker")
    WORKER("worker");
    private final String value;

    ObjectType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ObjectType fromValue(String v) {
        for (ObjectType c: ObjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
