package uk.gov.ons.ctp.response.action.utility;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

import static org.mockito.Matchers.any;

/**
 * A MockActionPlanServiceFactory
 */
public class MockActionPlanServiceFactory implements Factory<ActionPlanService> {

  public static final String ACTIONTYPE_NAME = "Action Type Name";
  public static final String ACTIONTYPE_DESC = "Action Type Desc";
  public static final String ACTIONTYPE_HANDLER = "Field";
  public static final boolean ACTIONTYPE_CANCANCEL = true;
  public static final boolean ACTIONTYPE_RESPONSEREQUIRED = true;
  public static final Integer ACTIONPLAN_SURVEYID = 1;
  public static final String ACTIONPLAN1_NAME = "HH_APL1";
  public static final String ACTIONPLAN2_NAME = "HGH_APL1";
  public static final String ACTIONPLAN3_NAME = "CH_APL1";
  public static final String ACTIONPLAN1_DESC = "Household Action Plan 1";
  public static final String ACTIONPLAN2_DESC = "Hotel and Guest House Action Plan 1";
  public static final String ACTIONPLAN3_DESC = "Care Home Action Plan 1";
  public static final Integer ACTIONPLANID = 3;
  public static final Integer ACTIONPLANID_WITHNOACTIONRULE = 13;
  public static final Integer ACTIONRULE_PRIORITY = 1;
  public static final Integer ACTIONRULE_SURVEYDATEDAYSOFFSET = 1;
  public static final Integer NON_EXISTING_ACTIONPLANID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String ACTIONRULE_ACTIONTYPENAME = "Action Type Name";
  public static final String ACTIONRULE_DESCRIPTION = "This is a Test Action Rule";
  public static final String ACTIONRULE_NAME = "Test Action Rule";
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final String PROVIDED_JSON_INCORRECT = "Provided json is incorrect.";
  public static final String CREATED_BY = "whilep1";
  private static final Timestamp ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP = Timestamp
      .valueOf("2016-03-09 11:15:48.023286");

  /**
   * mock it like you mean it
   * 
   * @return something
   */
  public ActionPlanService provide() {

    final ActionPlanService mockedService = Mockito.mock(ActionPlanService.class);

    Mockito.when(mockedService.findActionPlans()).thenAnswer(new Answer<List<ActionPlan>>() {
      public List<ActionPlan> answer(InvocationOnMock invocation)
          throws Throwable {
        List<ActionPlan> result = new ArrayList<>();
        result.add(new ActionPlan(1, ACTIONPLAN_SURVEYID, ACTIONPLAN1_NAME, ACTIONPLAN1_DESC, CREATED_BY,
            ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP));
        result.add(new ActionPlan(2, ACTIONPLAN_SURVEYID, ACTIONPLAN2_NAME, ACTIONPLAN2_DESC, CREATED_BY,
            ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP));
        result.add(new ActionPlan(3, ACTIONPLAN_SURVEYID, ACTIONPLAN3_NAME, ACTIONPLAN3_DESC, CREATED_BY,
            ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP));
        return result;
      }
    });

    Mockito.when(mockedService.findActionPlan(ACTIONPLANID)).thenAnswer(new Answer<ActionPlan>() {
      public ActionPlan answer(InvocationOnMock invocation)
          throws Throwable {
        return new ActionPlan(ACTIONPLANID, ACTIONPLAN_SURVEYID, ACTIONPLAN3_NAME, ACTIONPLAN3_DESC, CREATED_BY,
            ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP);
      }
    });

    Mockito.when(mockedService.findActionPlan(ACTIONPLANID_WITHNOACTIONRULE)).thenAnswer(new Answer<ActionPlan>() {
      public ActionPlan answer(InvocationOnMock invocation)
          throws Throwable {
        return new ActionPlan(ACTIONPLANID_WITHNOACTIONRULE, ACTIONPLAN_SURVEYID, ACTIONPLAN3_NAME, ACTIONPLAN3_DESC,
            CREATED_BY,
            ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP);
      }
    });

    Mockito.when(mockedService.findActionPlan(UNCHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findActionPlan(NON_EXISTING_ACTIONPLANID)).thenAnswer(new Answer<ActionPlan>() {
      public ActionPlan answer(InvocationOnMock invocation)
          throws Throwable {
        return null;
      }
    });

    Mockito.when(mockedService.findActionRulesForActionPlan(ACTIONPLANID)).thenAnswer(new Answer<List<ActionRule>>() {
      public List<ActionRule> answer(InvocationOnMock invocation)
          throws Throwable {
        ActionType actionType = new ActionType(1, ACTIONTYPE_NAME, ACTIONTYPE_DESC, ACTIONTYPE_HANDLER,
            ACTIONTYPE_CANCANCEL, ACTIONTYPE_RESPONSEREQUIRED);
        List<ActionRule> result = new ArrayList<>();
        result.add(new ActionRule(1, ACTIONPLANID, ACTIONRULE_PRIORITY, ACTIONRULE_SURVEYDATEDAYSOFFSET,
            actionType, ACTIONRULE_NAME, ACTIONRULE_DESCRIPTION));
        result.add(new ActionRule(2, ACTIONPLANID, ACTIONRULE_PRIORITY, ACTIONRULE_SURVEYDATEDAYSOFFSET,
            actionType, ACTIONRULE_NAME, ACTIONRULE_DESCRIPTION));
        result.add(new ActionRule(3, ACTIONPLANID, ACTIONRULE_PRIORITY, ACTIONRULE_SURVEYDATEDAYSOFFSET,
            actionType, ACTIONRULE_NAME, ACTIONRULE_DESCRIPTION));
        return result;
      }
    });

    Mockito.when(mockedService.findActionRulesForActionPlan(ACTIONPLANID_WITHNOACTIONRULE)).thenAnswer(
        new Answer<List<ActionRule>>() {
          public List<ActionRule> answer(InvocationOnMock invocation)
              throws Throwable {
            List<ActionRule> result = new ArrayList<>();
            return result;
          }
        });

    Mockito.when(mockedService.updateActionPlan(any(Integer.class), any(ActionPlan.class))).thenAnswer(
        new Answer<ActionPlan>() {
          public ActionPlan answer(InvocationOnMock invocation)
              throws Throwable {
            return new ActionPlan(ACTIONPLANID, ACTIONPLAN_SURVEYID, ACTIONPLAN3_NAME, ACTIONPLAN3_DESC,
                CREATED_BY, ACTIONPLAN_LAST_GOOD_RUN_DATE_TIMESTAMP);
          }
        });

    return mockedService;
  }

  /**
   * dispose of s*it
   * 
   * @param t the s*it to dispose
   */
  public void dispose(ActionPlanService t) {
  }
}
