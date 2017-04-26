package uk.gov.ons.ctp.response.action.utility;

import static org.mockito.Matchers.any;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;

/**
 * Created by philippe.brossier on 3/15/16.
 */
public class MockActionPlanJobServiceFactory {
  public static final Integer ACTIONPLANJOBID = 1;
  public static final Integer ACTIONPLANJOBID_ACTIONPLANID = 1;
  public static final String ACTIONPLANJOBID_CREATED_BY = "theTester";
  public static final ActionPlanJobDTO.ActionPlanJobState
    ACTIONPLANJOBID_STATE = ActionPlanJobDTO.ActionPlanJobState.SUBMITTED;
  public static final Timestamp ACTIONPLANJOBID_CREATEDDATE_TIMESTAMP = Timestamp
      .valueOf("2016-03-09 11:15:48.023286");
  public static final Timestamp ACTIONPLANJOBID_UPDATED_DATE_TIMESTAMP = Timestamp
      .valueOf("2016-04-09 11:15:48.023286");

  public static final Integer NON_EXISTING_ACTIONPLANJOBID = 998;
  public static final Integer UNCHECKED_EXCEPTION_ACTIONPLANJOBID = 999;

  public static final Integer ACTIONPLANID = 1;
  public static final Integer ACTIONPLANID_WITHNOACTIONPLANJOB = 13;

  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final String PROVIDED_JSON_INVALID = "Provided json fails validation.";

  /**
   * mock up the action plan job service
   * @return the mock service created
   */
  public ActionPlanJobService provide() {
    final ActionPlanJobService mockedService = Mockito.mock(ActionPlanJobService.class);

    Mockito.when(mockedService.findActionPlanJob(ACTIONPLANJOBID)).thenAnswer(new Answer<Optional<ActionPlanJob>>() {
      public Optional<ActionPlanJob> answer(InvocationOnMock invocation)
          throws Throwable {
        return Optional.of(new ActionPlanJob(ACTIONPLANJOBID, ACTIONPLANJOBID_ACTIONPLANID, ACTIONPLANJOBID_CREATED_BY,
            ACTIONPLANJOBID_STATE, ACTIONPLANJOBID_CREATEDDATE_TIMESTAMP, ACTIONPLANJOBID_UPDATED_DATE_TIMESTAMP));
      }
    });

    Mockito.when(mockedService.findActionPlanJob(UNCHECKED_EXCEPTION_ACTIONPLANJOBID))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findActionPlanJob(NON_EXISTING_ACTIONPLANJOBID)).thenAnswer(new Answer<Optional<ActionPlanJob>>() {
      public Optional<ActionPlanJob> answer(InvocationOnMock invocation)
          throws Throwable {
        return Optional.empty();
      }
    });

    Mockito.when(mockedService.findActionPlanJobsForActionPlan(ACTIONPLANID_WITHNOACTIONPLANJOB)).thenAnswer(
        new Answer<List<ActionPlanJob>>() {
          public List<ActionPlanJob> answer(InvocationOnMock invocation)
              throws Throwable {
            List<ActionPlanJob> result = new ArrayList<>();
            return result;
          }
        });

    Mockito.when(mockedService.findActionPlanJobsForActionPlan(ACTIONPLANID))
        .thenAnswer(new Answer<List<ActionPlanJob>>() {
          public List<ActionPlanJob> answer(InvocationOnMock invocation)
              throws Throwable {
            List<ActionPlanJob> result = new ArrayList<>();
            result.add(new ActionPlanJob(1, ACTIONPLANJOBID_ACTIONPLANID, ACTIONPLANJOBID_CREATED_BY,
                ACTIONPLANJOBID_STATE, ACTIONPLANJOBID_CREATEDDATE_TIMESTAMP, ACTIONPLANJOBID_UPDATED_DATE_TIMESTAMP));
            result.add(new ActionPlanJob(2, ACTIONPLANJOBID_ACTIONPLANID, ACTIONPLANJOBID_CREATED_BY,
                ACTIONPLANJOBID_STATE, ACTIONPLANJOBID_CREATEDDATE_TIMESTAMP, ACTIONPLANJOBID_UPDATED_DATE_TIMESTAMP));
            result.add(new ActionPlanJob(3, ACTIONPLANJOBID_ACTIONPLANID, ACTIONPLANJOBID_CREATED_BY,
                ACTIONPLANJOBID_STATE, ACTIONPLANJOBID_CREATEDDATE_TIMESTAMP, ACTIONPLANJOBID_UPDATED_DATE_TIMESTAMP));
            return result;
          }
        });

    Mockito.when(mockedService.createAndExecuteActionPlanJob(any(ActionPlanJob.class))).thenAnswer(
        new Answer<Optional<ActionPlanJob>>() {
          public Optional<ActionPlanJob> answer(InvocationOnMock invocation) throws Throwable {
            return Optional.of(new ActionPlanJob(ACTIONPLANJOBID, ACTIONPLANJOBID_ACTIONPLANID, ACTIONPLANJOBID_CREATED_BY,
                ACTIONPLANJOBID_STATE, ACTIONPLANJOBID_CREATEDDATE_TIMESTAMP, ACTIONPLANJOBID_UPDATED_DATE_TIMESTAMP));
          }
        });

    return mockedService;
  }
}
