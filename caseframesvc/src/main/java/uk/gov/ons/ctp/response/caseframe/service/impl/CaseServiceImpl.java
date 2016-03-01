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
 * An implementation of the CaseService using JPA Repository class(es) The
 * business logic for the application should reside here.
 */
@Named
@Slf4j
public class CaseServiceImpl implements CaseService {

  @Inject
  private CaseRepository caseRepo;

  @Inject
  private QuestionnaireRepository questionnaireRepo;

  @Inject
  private CaseEventRepository eventRepo;

  public List<Case> findCasesByUprn(Integer uprn) {
    log.debug("Entering findCasesByUprn with uprn {}", uprn);
    return caseRepo.findByUprn(uprn);
  }

  public Case findCaseByQuestionnaireId(Integer qid) {
    log.debug("Entering findCaseByQuestionnaireId");
    Questionnaire questionnaire = questionnaireRepo.findByQuestionnaireId(qid);
    if (questionnaire == null) {
      return null;
    }
    return caseRepo.findOne(questionnaire.getCaseId());
  }

  public Case findCaseByCaseId(Integer caseId) {
    log.debug("Entering findCaseByCaseId");
    return caseRepo.findOne(caseId);
  }

  public List<CaseEvent> findCaseEventsByCaseId(Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId");
    return eventRepo.findByCaseId(caseId);
  }
}
