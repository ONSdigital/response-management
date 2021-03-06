package uk.gov.ons.ctp.response.action.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;

/**
 * Impl of the service that centralizes all REST calls to the Case service
 *
 */
@Slf4j
@Service
public class CaseSvcClientServiceImpl implements CaseSvcClientService {
  @Autowired
  private AppConfig appConfig;

  @Autowired
  private RestClient caseSvcClient;

  @Override
  public PartyDTO getParty(final String partyId) {
    PartyDTO partyDTO = null;

//    AddressDTO addressDTO = caseSvcClient.getResource(appConfig.getCaseSvc().getAddressByUprnGetPath(),
//        AddressDTO.class, uprn);
    return partyDTO;
  }

  @Override
  public CaseDTO getCase(final Integer caseId) {
    CaseDTO caseDTO = caseSvcClient.getResource(appConfig.getCaseSvc().getCaseByCaseGetPath(),
        CaseDTO.class, caseId);
    return caseDTO;
  } 
  
  @Override
  public CaseGroupDTO getCaseGroup(final Integer caseGroupId) {
    CaseGroupDTO caseGroupDTO = caseSvcClient.getResource(appConfig.getCaseSvc().getCaseGroupPath(),
        CaseGroupDTO.class, caseGroupId);
    return caseGroupDTO;
  }
  

  @Override
  public List<CaseEventDTO> getCaseEvents(final Integer caseId) {
    List<CaseEventDTO> caseEventDTOs = caseSvcClient.getResources(
        appConfig.getCaseSvc().getCaseEventsByCaseGetPath(),
        CaseEventDTO[].class, caseId);
    return caseEventDTOs;
  }

  @Override
  public CaseEventDTO createNewCaseEvent(final Action action, CategoryDTO.CategoryType actionCategory) {
    log.debug("posting caseEvent for actionId {} to casesvc for category {} ", action.getActionId(),
        actionCategory);
    CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setCaseId(action.getCaseId());
    caseEventDTO.setCategory(actionCategory);
    caseEventDTO.setCreatedBy(action.getCreatedBy());
    caseEventDTO.setCreatedDateTime(new Date());
    caseEventDTO.setSubCategory(action.getActionType().getName());

    if (!StringUtils.isEmpty(action.getSituation())) {
      caseEventDTO.setDescription(String.format("%s (%s)",
          action.getActionType().getDescription(), action.getSituation()));
    } else {
      caseEventDTO.setDescription(action.getActionType().getDescription());
    }

    CaseEventDTO returnedCaseEventDTO = caseSvcClient.postResource(
        appConfig.getCaseSvc().getCaseEventsByCasePostPath(), caseEventDTO,
        CaseEventDTO.class,
        action.getCaseId());
    return returnedCaseEventDTO;
  }

}
