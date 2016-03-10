package uk.gov.ons.ctp.response.action.endpoint;

import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLAN1_DESC;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLAN1_NAME;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLAN2_DESC;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLAN2_NAME;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLAN3_DESC;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLAN3_NAME;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLANID;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.ACTIONPLAN_SURVEYID;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.CREATED_BY;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.NON_EXISTING_ACTIONPLANID;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory.UNCHECKED_EXCEPTION;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.action.ActionBeanMapper;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;
import uk.gov.ons.ctp.response.action.utility.MockActionPlanServiceFactory;

/**
 * Unit tests for ActionPlan endpoint
 */
public class ActionPlanEndpointUnitTest extends CTPJerseyTest {

  private static final String CREATED_DATE_TIME = "2016-03-09T11:15:48.023+0000";
  private static final String LAST_GOOD_RUN_DATE_TIME = "2016-03-09T11:15:48.023+0000";

  @Override
  public Application configure() {
    return super.init(ActionPlanEndpoint.class, ActionPlanService.class, MockActionPlanServiceFactory.class, new ActionBeanMapper()); 
  }

  @Test
  public void findActionPlansFound() {
    with("http://localhost:9998/actionplans")
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(3)
        .assertIntegerListInBody("$..actionPlanId", 1, 2, 3)
        .assertIntegerListInBody("$..surveyId", ACTIONPLAN_SURVEYID, ACTIONPLAN_SURVEYID, ACTIONPLAN_SURVEYID)
        .assertStringListInBody("$..name", ACTIONPLAN1_NAME, ACTIONPLAN2_NAME, ACTIONPLAN3_NAME)
        .assertStringListInBody("$..description", ACTIONPLAN1_DESC, ACTIONPLAN2_DESC, ACTIONPLAN3_DESC)
        .assertStringListInBody("$..createdBy", CREATED_BY, CREATED_BY, CREATED_BY)
        .assertStringListInBody("$..createdDatetime", CREATED_DATE_TIME, CREATED_DATE_TIME, CREATED_DATE_TIME)
        .assertStringListInBody("$..lastGoodRunDatetime", LAST_GOOD_RUN_DATE_TIME, LAST_GOOD_RUN_DATE_TIME,
            LAST_GOOD_RUN_DATE_TIME)
        .andClose();
  }

  @Test
  public void findActionPlanFound() {
    with("http://localhost:9998/actionplans/%s", ACTIONPLANID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.actionPlanId", 3)
        .assertIntegerInBody("$.surveyId", ACTIONPLAN_SURVEYID)
        .assertStringInBody("$.name", ACTIONPLAN3_NAME)
        .assertStringInBody("$.description", ACTIONPLAN3_DESC)
        .assertStringInBody("$.createdBy", CREATED_BY)
        .assertStringInBody("$.createdDatetime", CREATED_DATE_TIME)
        .assertStringInBody("$.lastGoodRunDatetime", LAST_GOOD_RUN_DATE_TIME)
        .andClose();
  }

  @Test
  public void findActionPlanNotFound() {
    with("http://localhost:9998/actionplans/%s", NON_EXISTING_ACTIONPLANID)
      .assertResponseCodeIs(HttpStatus.NOT_FOUND)
      .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
      .assertTimestampExists()
      .assertMessageEquals("ActionPlan not found for id %s", NON_EXISTING_ACTIONPLANID)
      .andClose();
  }

  @Test
  public void findActionPlanUnCheckedException() {
    with("http://localhost:9998/actionplans/%s", UNCHECKED_EXCEPTION)
      .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
      .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
      .assertTimestampExists()
      .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
      .andClose();
  }
}
