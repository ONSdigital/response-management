package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.MockLocalAuthorityServiceFactory.*;
import javax.ws.rs.core.Application;

import org.junit.Test;

import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.service.LocalAuthorityService;
import uk.gov.ons.ctp.response.caseframe.utility.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.utility.MockLocalAuthorityServiceFactory;

/**
 * Created by philippe.brossier on 2/22/16.
 */
public class LocalAuthorityEndpointUnitTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    return super.init(LocalAuthorityEndpoint.class, LocalAuthorityService.class, MockLocalAuthorityServiceFactory.class);
  }

  @Test
  public void findByIdPositiveScenario() {
    with("http://localhost:9998/lads/%s", LAD_WITH_CODE_LAD123)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertStringInBody("$.regionCode", REGION_CODE_FOR_LAD123)
            .assertStringInBody("$.ladCode", LAD_WITH_CODE_LAD123)
            .assertStringInBody("$.ladName", String.format("%s%s", LAD_WITH_CODE_LAD123, MockLocalAuthorityServiceFactory.NAME))
            .andClose();
  }

  @Test
  public void findByIdScenarioNotFound() {
    with("http://localhost:9998/lads/%s", LAD_WITH_NON_EXISTING_CODE)
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", String.format("LAD not found for id %s", LAD_WITH_NON_EXISTING_CODE))
            .andClose();
  }

  @Test
  public void findByIdScenarioThrowCheckedException() {
    with("http://localhost:9998/lads/%s", LAD_WITH_CODE_CHECKED_EXCEPTION)
            .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
            .assertStringInBody("$.error.code", CTPException.Fault.SYSTEM_ERROR.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", MockLocalAuthorityServiceFactory.OUR_EXCEPTION_MESSAGE)
            .andClose();
  }

  @Test
  public void findAllMsoasForLadIdPositiveScenario() {
    with("http://localhost:9998/lads/%s/msoas", LAD_WITH_CODE_LAD123)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertArrayLengthInBodyIs(2)
            .assertStringListInBody("$..msoaCode", MSOA1_CODE, MSOA2_CODE)
            .assertStringListInBody("$..msoaName", MSOA1_NAME, MSOA2_NAME)
            .assertStringListInBody("$..ladCode", LAD_WITH_CODE_LAD123, LAD_WITH_CODE_LAD123)
            .andClose();
  }

  @Test
  public void findAllMsoasForLadIdScenario204() {
    with("http://localhost:9998/lads/%s/msoas", LAD_WITH_CODE_204)
            .assertResponseCodeIs(HttpStatus.NO_CONTENT)
            .andClose();
  }
}
