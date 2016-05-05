package uk.gov.ons.ctp.response.caseframe.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.caseframe.config.ActionSvc;
import uk.gov.ons.ctp.response.caseframe.config.AppConfig;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.representation.CaseDTO;

/**
 * Created by philippe.brossier on 3/31/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseServiceImplTest {

  @Mock
  CaseRepository caseRepo;

  @Mock
  QuestionnaireRepository questionnaireRepo;

  @Mock
  CaseEventRepository caseEventRepository;

  @Mock
  CategoryRepository categoryRepo;

  @Mock
  AppConfig appConfig;
  
  @Mock
  RestClient actionSvcRestClient;
  
  @InjectMocks
  CaseServiceImpl caseService;

  private static final Integer EXISTING_PARENT_CASE_ID = 2;
  private static final Integer NON_EXISTING_PARENT_CASE_ID = 1;

  private static final String CASEEVENT_CATEGORY = "category";
  private static final String CASEEVENT_CREATEDBY = "unit test";
  private static final String CASEEVENT_DESCRIPTION = "a desc";
  private static final String CASEEVENT_SUBCATEGORY = "sub category";

  private static final Integer CASE_UPRN = 1;
  private static final Integer CASE_TYPEID = 1;
  private static final CaseDTO.CaseState CASE_STATE = CaseDTO.CaseState.INIT;
  private static final String CASE_CREATEDBY = "unit test";
  private static final String CASE_QUESTIONSET = "case question set";
  private static final Integer CASE_SAMPLEID = 1;
  private static final Integer CASE_ACTIONPLANID = 1;
  private static final Integer CASE_SURVEYID = 1;

  private static final String CATEGORY_NAME = "category name";
  private static final String CATEGORY_DESC = "category desc";
  private static final String CATEGORY_ROLE = "category role";
  private static final String CATEGORY_EMPTYACTIONTYPE = "";
  private static final Boolean CATEGORY_CLOSECASE_FALSE = new Boolean(false);
  private static final Boolean CATEGORY_CLOSECASE_TRUE = new Boolean(true);
  private static final Boolean CATEGORY_MANUAL_FALSE = new Boolean(false);

  private static final Integer QUESTIONNAIRE_ID = 1;
  private static final String QUESTIONNAIRE_IAC = "A1B2C3";
  private static final String QUESTIONNAIRE_STATUS = "quest status";
  private static final String QUESTIONNAIRE_QUESTIONSET = "question set";
  
  private static final String ACTIONSVC_CANCEL_ACTIONS_PATH = "actions/case/123/cancel";

  @Test
  public void testCreateCaseEventNoParentCase() {
    Mockito.when(caseRepo.findOne(NON_EXISTING_PARENT_CASE_ID)).thenReturn(null);

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    CaseEvent caseEvent = new CaseEvent(1, NON_EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY, CASEEVENT_SUBCATEGORY);

    CaseEvent result = caseService.createCaseEvent(caseEvent);

    verify(caseRepo).findOne(NON_EXISTING_PARENT_CASE_ID);
    assertNull(result);
  }
// TODO - is there just to much mocking required in the test below to make it worthwhile?
//  @Test
  public void testCreateCaseEventParentCaseFoundAndCloseCaseAtFalseAndEmptyActiontype() {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    Case parentCase = new Case(EXISTING_PARENT_CASE_ID, CASE_UPRN, CASE_STATE, CASE_TYPEID, currentTime,
        CASE_CREATEDBY, CASE_SAMPLEID, CASE_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET);
    Mockito.when(caseRepo.findOne(EXISTING_PARENT_CASE_ID)).thenReturn(parentCase);

    Category category = new Category(CATEGORY_NAME, CATEGORY_DESC, CATEGORY_ROLE, CATEGORY_EMPTYACTIONTYPE,
        CATEGORY_CLOSECASE_FALSE, CATEGORY_MANUAL_FALSE);
    Mockito.when(categoryRepo.findByName(CASEEVENT_CATEGORY)).thenReturn(category);

    CaseEvent caseEvent = new CaseEvent(1, EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY, CASEEVENT_SUBCATEGORY);
    Mockito.when(caseEventRepository.save(caseEvent)).thenReturn(caseEvent);

    CaseEvent result = caseService.createCaseEvent(caseEvent);
    verify(caseRepo).findOne(EXISTING_PARENT_CASE_ID);
    verify(categoryRepo).findByName(CASEEVENT_CATEGORY);
    verify(caseRepo, never()).setStatusFor(QuestionnaireServiceImpl.CLOSED, EXISTING_PARENT_CASE_ID);
    verify(questionnaireRepo, never()).findByCaseId(EXISTING_PARENT_CASE_ID);
    verify(caseEventRepository).save(caseEvent);
    assertEquals(caseEvent, result);
  }

//TODO - is there just to much mocking required in the test below to make it worthwhile?
//  @Test
  public void testCreateCaseEventParentCaseFoundAndCloseCaseAtTrueAndEmptyActiontype() {
    
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    Case parentCase = new Case(EXISTING_PARENT_CASE_ID, CASE_UPRN, CASE_STATE, CASE_TYPEID, currentTime,
        CASE_CREATEDBY, CASE_SAMPLEID, CASE_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET);
    Mockito.when(caseRepo.findOne(EXISTING_PARENT_CASE_ID)).thenReturn(parentCase);

    Category category = new Category(CATEGORY_NAME, CATEGORY_DESC, CATEGORY_ROLE, CATEGORY_EMPTYACTIONTYPE,
        CATEGORY_CLOSECASE_TRUE, CATEGORY_MANUAL_FALSE);
    Mockito.when(categoryRepo.findByName(CASEEVENT_CATEGORY)).thenReturn(category);

    List<Questionnaire> associatedQuestionnaires = new ArrayList<>();
    Questionnaire questionnaire = new Questionnaire(QUESTIONNAIRE_ID, QUESTIONNAIRE_IAC, EXISTING_PARENT_CASE_ID,
        QUESTIONNAIRE_STATUS, currentTime, currentTime, currentTime, QUESTIONNAIRE_QUESTIONSET);
    associatedQuestionnaires.add(questionnaire);
    Mockito.when(questionnaireRepo.findByCaseId(EXISTING_PARENT_CASE_ID)).thenReturn(associatedQuestionnaires);

    CaseEvent caseEvent = new CaseEvent(1, EXISTING_PARENT_CASE_ID, CASEEVENT_DESCRIPTION, CASEEVENT_CREATEDBY,
        currentTime, CASEEVENT_CATEGORY, CASEEVENT_SUBCATEGORY);
    Mockito.when(caseEventRepository.save(caseEvent)).thenReturn(caseEvent);

    ActionSvc actionSvc = new ActionSvc();
    actionSvc.setCancelActionsPath(ACTIONSVC_CANCEL_ACTIONS_PATH);
    Mockito.when(appConfig.getActionSvc()).thenReturn(actionSvc);
    
    ActionDTO actionDTO = new ActionDTO();
    actionDTO.setCaseId(EXISTING_PARENT_CASE_ID);
    ActionDTO[] actionDTOList = new ActionDTO[1]; 
    actionDTOList[0] = actionDTO;  
    Mockito.when(actionSvcRestClient.putResource(ACTIONSVC_CANCEL_ACTIONS_PATH, null, ActionDTO[].class, EXISTING_PARENT_CASE_ID)).thenReturn(actionDTOList);

    CaseEvent result = caseService.createCaseEvent(caseEvent);
    verify(caseRepo).findOne(EXISTING_PARENT_CASE_ID);
    verify(categoryRepo).findByName(CASEEVENT_CATEGORY);
    verify(caseRepo).setStatusFor(QuestionnaireServiceImpl.CLOSED, EXISTING_PARENT_CASE_ID);
    verify(questionnaireRepo).findByCaseId(EXISTING_PARENT_CASE_ID);
    verify(questionnaireRepo).setResponseDatetimeFor(any(Timestamp.class), any(Integer.class));
    verify(caseEventRepository).save(caseEvent);
    verify(appConfig).getActionSvc();
    verify(actionSvcRestClient).putResource(ACTIONSVC_CANCEL_ACTIONS_PATH, null, ActionDTO[].class, EXISTING_PARENT_CASE_ID);
    assertEquals(caseEvent, result);
  }
}
