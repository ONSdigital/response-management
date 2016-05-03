package uk.gov.ons.ctp.response.action.service;

import java.util.List;

import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.caseframe.representation.AddressDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CaseDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.caseframe.representation.QuestionnaireDTO;

/**
 * A Service which utilises the CaseFrameSvc via RESTful client calls
 *
 */
public interface CaseFrameSvcClientService {
  /**
   * Create and post to caseframe service a new CaseEvent
   *
   * @param action the action for which we need the event
   * @param actionCategory the category for the event
   */
  void createNewCaseEvent(final Action action, String actionCategory);

  /**
   * Call CaseFrameSvc using REST to get the Address MAY throw a
   * RuntimeException if the call fails
   *
   * @param uprn identifies the Address to fetch
   * @return the Address we fetched
   */
  AddressDTO getAddress(final Integer uprn);

  /**
   * Call CaseFrameSvc using REST to get the Questionnaire MAY throw a
   * RuntimeException if the call fails
   *
   * @param caseId used to find the questionnaire
   * @return the Questionnaire we fetched
   */
  QuestionnaireDTO getQuestionnaire(final Integer caseId);

  /**
   * Call CaseFrameSvc using REST to get the Case details MAY throw a
   * RuntimeException if the call fails
   *
   * @param caseId identifies the Case to fetch
   * @return the Case we fetched
   * @throws RestClientException when we cannot connect or the service call
   *           errors
   */
  CaseDTO getCase(final Integer caseId);

  /**
   * Call CaseFrameSvc using REST to get the CaseEvents for the Case MAY throw a
   * RuntimeException if the call fails
   *
   * @param caseId identifies the Case to fetch events for
   * @return the CaseEvents we found for the case
   * @throws RestClientException when we cannot connect or the service call
   *           errors
   */
  List<CaseEventDTO> getCaseEvents(final Integer caseId);
}
