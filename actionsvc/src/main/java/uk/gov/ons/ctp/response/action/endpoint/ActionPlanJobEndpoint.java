package uk.gov.ons.ctp.response.action.endpoint;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;

import javax.validation.Valid;

/**
 * The REST endpoint controller for ActionPlanJobs.
 */
@RestController
@RequestMapping(value = "/actionplans", produces = "application/json")
@Slf4j
public class ActionPlanJobEndpoint implements CTPEndpoint {

  @Autowired
  private ActionPlanJobService actionPlanJobService;

  @Qualifier("actionBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * This method returns the associated action plan job for the specified action plan job id.
   *
   * @param actionPlanJobId This is the action plan job id
   * @return ActionPlanJobDTO This returns the associated action plan job for the specified action plan job id.
   * @throws CTPException if no action plan job found for the specified action plan job id.
   */
  @RequestMapping(value = "/jobs/{actionplanjobid}", method = RequestMethod.GET)
  public final ActionPlanJobDTO findActionPlanJobById(@PathVariable("actionplanjobid") final Integer actionPlanJobId)
      throws CTPException {
    log.info("Entering findActionPlanJobById with {}", actionPlanJobId);
    Optional<ActionPlanJob> actionPlanJob = actionPlanJobService.findActionPlanJob(actionPlanJobId);
    return mapperFacade.map(actionPlanJob.orElseThrow(() -> new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
            "ActionPlanJob not found for id %d", actionPlanJobId)), ActionPlanJobDTO.class);
  }

  /**
   * Returns all action plan jobs for the given action plan id.
   * @param actionPlanId the given action plan id.
   * @return Returns all action plan jobs for the given action plan id.
   * @throws CTPException summats went wrong
   */
  @RequestMapping(value = "/{actionplanid}/jobs", method = RequestMethod.GET)
  public final ResponseEntity<?> findAllActionPlanJobsByActionPlanId(@PathVariable("actionplanid") final Integer
      actionPlanId) throws CTPException {
    log.info("Entering findAllActionPlanJobsByActionPlanId with {}", actionPlanId);
    List<ActionPlanJob> actionPlanJobs = actionPlanJobService.findActionPlanJobsForActionPlan(actionPlanId);
    List<ActionPlanJobDTO> actionPlanJobDTOs = mapperFacade.mapAsList(actionPlanJobs, ActionPlanJobDTO.class);
    return CollectionUtils.isEmpty(actionPlanJobDTOs) ?
            ResponseEntity.noContent().build() : ResponseEntity.ok(actionPlanJobDTOs);
  }

  /**
   * To create a new Action Plan Job having received an action plan id and some json
   * @param actionPlanId the given action plan id.
   * @param actionPlanJobDTO the ActionPlanJobDTO representation of the provided json
   * @return the created ActionPlanJobDTO
   * @throws CTPException summats went wrong
   */
  @RequestMapping(value = "/{actionplanid}/jobs", method = RequestMethod.POST, consumes = "application/json")
  public final ResponseEntity<?> executeActionPlan(@PathVariable("actionplanid") final Integer actionPlanId,
      final @RequestBody @Valid ActionPlanJobDTO actionPlanJobDTO, BindingResult bindingResult) throws CTPException {
    log.info("Entering executeActionPlan with {}", actionPlanId);

    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for execute action plan: ", bindingResult);
    }

    if (actionPlanJobDTO == null) {
      throw new CTPException(CTPException.Fault.VALIDATION_FAILED, "Provided json is incorrect.");
    }

    ActionPlanJob job = mapperFacade.map(actionPlanJobDTO, ActionPlanJob.class);
    job.setActionPlanId(actionPlanId);
    Optional<ActionPlanJob> actionPlanJob = actionPlanJobService.createAndExecuteActionPlanJob(job);
    return ResponseEntity.created(URI.create("TODO")).body(
            mapperFacade.map(actionPlanJob.orElseThrow(() -> new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
            "ActionPlan not found for id %s", actionPlanId)), ActionPlanJobDTO.class));
  }
}
