package uk.gov.ons.ctp.response.action.schedule;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.TestHelper;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.CaseFrameSvc;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.message.InstructionPublisher;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.scheduled.impl.ActionDistributorImpl;
import uk.gov.ons.ctp.response.caseframe.representation.AddressDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CaseDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.caseframe.representation.QuestionnaireDTO;

//@RunWith(MockitoJUnitRunner.class)
public class ActionDistributorTest {

  @Mock
  AppConfig appConfig;
  
  @Mock
  InstructionPublisher instructionPublisher;

  @Mock
  MapperFacade mapperFacade;

  @Mock
  RestClient caseFrameClient;

  @Mock
  ActionRepository actionRepo;

  @Mock
  ActionTypeRepository actionTypeRepo;

  @Mock
  TransactionTemplate transactionTemplate;

  @Mock
  PlatformTransactionManager platformTransactionManager;

  @InjectMocks
  ActionDistributorImpl actionDistributor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

//  @Test
  public void testBlueSky() throws Exception {
    // set up dummy data
    CaseFrameSvc caseFrameSvcConfig = new CaseFrameSvc();

    List<ActionType> actionTypes = TestHelper.loadClassDummies(ActionType[].class);

    List<Action> actionsHHIC = TestHelper.loadClassDummies(Action[].class, "HH_IC");
    List<Action> actionsHHIACLOAD = TestHelper.loadClassDummies(Action[].class, "HH_IAC_LOAD");

    List<QuestionnaireDTO> questionnaireDTOs = TestHelper.loadClassDummies(QuestionnaireDTO[].class);

    List<CaseDTO> caseDTOs = TestHelper.loadClassDummies(CaseDTO[].class);
    
    List<AddressDTO> addressDTOsUprn1234 = TestHelper.loadClassDummies(AddressDTO[].class, "uprn1234");

    List<CaseEventDTO> caseEventDTOs = TestHelper.loadClassDummies(CaseEventDTO[].class);
    
    List<CaseEventDTO> caseEventDTOsPost = TestHelper.loadClassDummies(CaseEventDTO[].class, "post");

    // wire up mock responses
    Mockito.when(appConfig.getCaseFrameSvc()).thenReturn(caseFrameSvcConfig);
    Mockito.when(actionTypeRepo.findAll()).thenReturn(actionTypes);
    Mockito.when(actionRepo.findFirst100ByActionTypeNameAndStateOrderByCreatedDateTimeAsc("HH_IC", ActionState.SUBMITTED)).thenReturn(actionsHHIC);
    Mockito.when(actionRepo.findFirst100ByActionTypeNameAndStateOrderByCreatedDateTimeAsc("HH_IAC_LOAD", ActionState.SUBMITTED)).thenReturn(actionsHHIACLOAD);

    Mockito.when(caseFrameClient.getResources(anyString(), eq(QuestionnaireDTO[].class), eq(1))).thenReturn(Arrays.asList(new QuestionnaireDTO[] {questionnaireDTOs.get(0)}));
    Mockito.when(caseFrameClient.getResources(anyString(), eq(QuestionnaireDTO[].class), eq(2))).thenReturn(Arrays.asList(new QuestionnaireDTO[] {questionnaireDTOs.get(1)}));
    Mockito.when(caseFrameClient.getResources(anyString(), eq(QuestionnaireDTO[].class), eq(3))).thenReturn(Arrays.asList(new QuestionnaireDTO[] {questionnaireDTOs.get(2)}));
    Mockito.when(caseFrameClient.getResources(anyString(), eq(QuestionnaireDTO[].class), eq(4))).thenReturn(Arrays.asList(new QuestionnaireDTO[] {questionnaireDTOs.get(3)}));

    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseDTO.class), eq(1))).thenReturn(caseDTOs.get(0));
    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseDTO.class), eq(2))).thenReturn(caseDTOs.get(1));
    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseDTO.class), eq(3))).thenReturn(caseDTOs.get(2));
    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseDTO.class), eq(4))).thenReturn(caseDTOs.get(3));

    Mockito.when(caseFrameClient.getResource(anyString(), eq(AddressDTO.class), eq(1234))).thenReturn(addressDTOsUprn1234.get(0));

    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseEventDTO.class), eq(1))).thenReturn(caseEventDTOs.get(0));
    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseEventDTO.class), eq(2))).thenReturn(caseEventDTOs.get(1));
    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseEventDTO.class), eq(3))).thenReturn(caseEventDTOs.get(2));
    Mockito.when(caseFrameClient.getResource(anyString(), eq(CaseEventDTO.class), eq(4))).thenReturn(caseEventDTOs.get(3));

    Mockito.when(caseFrameClient.postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(1))).thenReturn(caseEventDTOsPost.get(0));
    Mockito.when(caseFrameClient.postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(2))).thenReturn(caseEventDTOsPost.get(1));
    Mockito.when(caseFrameClient.postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(3))).thenReturn(caseEventDTOsPost.get(2));
    Mockito.when(caseFrameClient.postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(4))).thenReturn(caseEventDTOsPost.get(3));

    
    // let it roll
    actionDistributor.wakeUp();

    // assert the right calls were made
    verify(actionTypeRepo).findAll();
    verify(actionRepo).findFirst100ByActionTypeNameAndStateOrderByCreatedDateTimeAsc("HH_IC", ActionState.SUBMITTED);
    verify(actionRepo).findFirst100ByActionTypeNameAndStateOrderByCreatedDateTimeAsc("HH_IAC_LOAD", ActionState.SUBMITTED);

    verify(caseFrameClient).getResources(anyString(), eq(QuestionnaireDTO[].class), eq(1));
    verify(caseFrameClient).getResources(anyString(), eq(QuestionnaireDTO[].class), eq(2));
    verify(caseFrameClient).getResources(anyString(), eq(QuestionnaireDTO[].class), eq(3));
    verify(caseFrameClient).getResources(anyString(), eq(QuestionnaireDTO[].class), eq(4));
    
    verify(caseFrameClient).getResource(anyString(), eq(CaseDTO.class), eq(1));
    verify(caseFrameClient).getResource(anyString(), eq(CaseDTO.class), eq(2));
    verify(caseFrameClient).getResource(anyString(), eq(CaseDTO.class), eq(3));
    verify(caseFrameClient).getResource(anyString(), eq(CaseDTO.class), eq(4));

    verify(caseFrameClient,times(4)).getResource(anyString(), eq(AddressDTO.class), eq(1234));

    verify(caseFrameClient).getResources(anyString(), eq(CaseEventDTO[].class), eq(1));
    verify(caseFrameClient).getResources(anyString(), eq(CaseEventDTO[].class), eq(2));
    verify(caseFrameClient).getResources(anyString(), eq(CaseEventDTO[].class), eq(3));
    verify(caseFrameClient).getResources(anyString(), eq(CaseEventDTO[].class), eq(4));

    verify(caseFrameClient).postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(1));
    verify(caseFrameClient).postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(2));
    verify(caseFrameClient).postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(3));
    verify(caseFrameClient).postResource(anyString(), any(CaseEventDTO.class), eq(CaseEventDTO.class), eq(4));

    verify(instructionPublisher, times(1)).sendRequests(eq("Printer"), anyListOf(ActionRequest.class));
    verify(instructionPublisher, times(1)).sendRequests(eq("HHSurvey"), anyListOf(ActionRequest.class));
  }
}
