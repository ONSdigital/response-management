package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;

/**
 * The Case Service interface defines all business behaviours for operations on
 * the Case entity model.
 */
public interface CaseService extends CTPService {

  /**
   * Find Case entities associated with an Address.
   *
   * @param uprn UPRN for an address
   * @return List of Case entities or empty List
   */
  List<Case> findCasesByUprn(Integer uprn);

  /**
   * Find Case entity by Questionnaire Id.
   *
   * @param qid Unique Questionnaire Id
   * @return Case object or null
   */
  Case findCaseByQuestionnaireId(Integer qid);

  /**
   * Find Case entity by unique Id.
   *
   * @param caseId Unique Case Id
   * @return Case object or null
   */
  Case findCaseByCaseId(Integer caseId);

  /**
   * Find CaseEvent entities associated with a Case.
   *
   * @param caseId Case Id
   * @return List of CaseEvent entities or empty List
   */
  List<CaseEvent> findCaseEventsByCaseId(Integer caseId);
}
