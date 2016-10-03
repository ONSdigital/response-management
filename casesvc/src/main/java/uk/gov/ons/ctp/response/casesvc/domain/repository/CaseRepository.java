package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;

/**
 * JPA Data Repository.
 */
@Named
@Transactional
public interface CaseRepository extends JpaRepository<Case, Integer> {

  /**
   * Return all cases in the given states and not in the list of excluded ids
   * using the page specification provided
   *
   * @param states States of Case
   * @param caseIds caseIds to exclude
   * @param pageable the paging info for the query
   * @return List<Action> returns all cases in states, for the given page
   */
  List<Case> findByStateInAndCaseIdNotIn(List<CaseDTO.CaseState> states, List<Integer> caseIds, Pageable pageable);

  /**
   * set the case state for a given case.
   * 
   * @param state the case state value
   * @param caseid the case by id
   * @return the number of cases updated
   */
  @Modifying
  @Query(value = "UPDATE casesvc.case SET state = ?2 WHERE caseid = ?1", nativeQuery = true)
  int setState(Integer caseid, String state);
  
  
  /**
   * Find cases assigned to the given casegroupid
   * @param caseGroupId the group id
   * @return the cases in the group
   */
  List<Case> findByCaseGroupId(Integer caseGroupId);

  /**
   * Find a case by its external case reference
   * @param caseRef the external ref
   * @return the case
   */
  Case findByCaseRef(String caseRef);
  
}
