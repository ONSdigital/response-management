package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public final class CaseServiceImpl implements CaseService {

  /**
   * Spring Data Repository for Case entities.
   */
  @Inject
  private CaseRepository caseRepo;

  /**
   * Spring Data Repository for Questionnaire Entities.
   */
  @Inject
  private QuestionnaireRepository questionnaireRepo;

  /**
   * Spring Data Repository for CaseEvent Entities.
   */
  @Inject
  private CaseEventRepository eventRepo;

  /**
   * Find Case entities associated with an Address.
   *
   * @param uprn UPRN for an address
   * @return List of Case entities or empty List
   */
  @Override
  public List<Case> findCasesByUprn(final Integer uprn) {
    log.debug("Entering findCasesByUprn with uprn {}", uprn);
    return caseRepo.findByUprn(uprn);
  }

  /**
   * Find Case entity by Questionnaire Id.
   *
   * @param qid Unique Questionnaire Id
   * @return Case object or null
   */
  @Override
  public Case findCaseByQuestionnaireId(final Integer qid) {
    log.debug("Entering findCaseByQuestionnaireId");
    Questionnaire questionnaire = questionnaireRepo.findByQuestionnaireId(qid);
    if (questionnaire == null) {
      return null;
    }
    return caseRepo.findOne(questionnaire.getCaseId());
  }

  /**
   * Find Case entity by unique Id.
   *
   * @param caseId Unique Case Id
   * @return Case object or null
   */
  @Override
  public Case findCaseByCaseId(final Integer caseId) {
    log.debug("Entering findCaseByCaseId");
    return caseRepo.findOne(caseId);
  }

  /**
   * Find CaseEvent entities associated with a Case.
   *
   * @param caseId Case Id
   * @return List of CaseEvent entities or empty List
   */
  @Override
  public List<CaseEvent> findCaseEventsByCaseId(final Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId");
    return eventRepo.findByCaseId(caseId);
  }
}
