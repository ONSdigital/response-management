package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.AddressBuilder.*;
import static uk.gov.ons.ctp.response.caseframe.utility.MockAddressServiceFactory.*;

import javax.ws.rs.core.Application;

import org.junit.Test;

import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.service.AddressService;
import uk.gov.ons.ctp.response.caseframe.utility.*;

/**
 * Unit Tests for the Address Endpoint
 */
public class AddressEndpointUnitTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    return super.init(AddressEndpoint.class, AddressService.class, MockAddressServiceFactory.class);
  }

  @Test
  public void findByUprnPositiveScenario() {
    with("http://localhost:9998/addresses/%s", ADDRESS_UPRN)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertIntegerInBody("$.uprn", ADDRESS_UPRN.intValue())
            .assertStringInBody("$.addressType", ADDRESS_TYPE)
            .assertStringInBody("$.estabType", ADDRESS_ESTABLISH_TYPE)
            .assertStringInBody("$.addressLine1", ADDRESS_LINE1)
            .assertStringInBody("$.addressLine2", ADDRESS_LINE2)
            .assertStringInBody("$.townName", ADDRESS_TOWN_NAME)
            .assertStringInBody("$.outputArea", ADDRESS_OUTPUT_AREA)
            .assertStringInBody("$.lsoaArea", ADDRESS_LSOA)
            .assertStringInBody("$.msoaArea", ADDRESS_MSOA)
            .assertStringInBody("$.ladCode", ADDRESS_LAD)
            .assertStringInBody("$.regionCode", ADDRESS_REGION_CODE)
            .assertIntegerInBody("$.eastings", ADDRESS_EASTINGS)
            .assertIntegerInBody("$.northings", ADDRESS_NORTHINGS)
            .assertIntegerInBody("$.htc", ADDRESS_HTC)
            .assertDoubleInBody("$.latitude", ADDRESS_LATITUDE)
            .assertDoubleInBody("$.longitude", ADDRESS_LONGITUDE)
            .andClose();
  }

  @Test
  public void findByUprnScenarioNotFound() {
    with("http://localhost:9998/addresses/%s", ADDRESS_NON_EXISTING_UPRN)
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", String.format("No addresses found for uprn %s", ADDRESS_NON_EXISTING_UPRN))
            .andClose();
  }

  @Test
  public void findByUprnScenarioThrowCheckedException() {
    with("http://localhost:9998/addresses/%s", ADDRESS_WITH_UPRN_CHECKED_EXCEPTION)
            .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
            .assertStringInBody("$.error.code", CTPException.Fault.SYSTEM_ERROR.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", OUR_EXCEPTION_MESSAGE)
            .andClose();
  }

  @Test
  public void findByPostcodePositiveScenario() {
    with("http://localhost:9998/addresses/postcode/%s", ADDRESS_POSTCODE)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertArrayLengthInBodyIs(1)
            .assertStringListInBody("$..postcode", ADDRESS_POSTCODE)
            .assertStringListInBody("$..addressType", ADDRESS_TYPE)
            .assertStringListInBody("$..estabType", ADDRESS_ESTABLISH_TYPE)
            .assertStringListInBody("$..addressLine1", ADDRESS_LINE1)
            .assertStringListInBody("$..addressLine2", ADDRESS_LINE2)
            .assertStringListInBody("$..townName", ADDRESS_TOWN_NAME)
            .assertStringListInBody("$..outputArea", ADDRESS_OUTPUT_AREA)
            .assertStringListInBody("$..lsoaArea", ADDRESS_LSOA)
            .assertStringListInBody("$..msoaArea", ADDRESS_MSOA)
            .assertStringListInBody("$..ladCode", ADDRESS_LAD)
            .assertStringListInBody("$..regionCode", ADDRESS_REGION_CODE)
            .assertIntegerListInBody("$..eastings", ADDRESS_EASTINGS)
            .assertIntegerListInBody("$..northings", ADDRESS_NORTHINGS)
            .assertIntegerListInBody("$..htc", ADDRESS_HTC)
            .assertDoubleListInBody("$..latitude", ADDRESS_LATITUDE)
            .assertDoubleListInBody("$..longitude", ADDRESS_LONGITUDE)
            .andClose();
  }

  @Test
  public void findByPostcodeScenarioNotFound() {
    with("http://localhost:9998/addresses/postcode/%s", ADDRESS_NON_EXISTING_POSTCODE)
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", String.format("No addresses found for postcode %s", ADDRESS_NON_EXISTING_POSTCODE))
            .andClose();
  }
}