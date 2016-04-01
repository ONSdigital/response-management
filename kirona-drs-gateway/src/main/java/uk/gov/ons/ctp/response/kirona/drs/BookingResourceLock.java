
package uk.gov.ons.ctp.response.kirona.drs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for bookingResourceLock.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="bookingResourceLock">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="lockAsSpecified"/>
 *     &lt;enumeration value="lockAfterSchedule"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "bookingResourceLock")
@XmlEnum
public enum BookingResourceLock {

    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("lockAsSpecified")
    LOCK_AS_SPECIFIED("lockAsSpecified"),
    @XmlEnumValue("lockAfterSchedule")
    LOCK_AFTER_SCHEDULE("lockAfterSchedule");
    private final String value;

    BookingResourceLock(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BookingResourceLock fromValue(String v) {
        for (BookingResourceLock c: BookingResourceLock.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
