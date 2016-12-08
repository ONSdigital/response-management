package uk.gov.ons.ctp.response.action.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRuleDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanService;

/**
 * The REST endpoint controller for ActionPlans.
 */
@Path("/actionplans")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Slf4j
public class ActionPlanEndpoint implements CTPEndpoint {

  @Inject
  private ActionPlanService actionPlanService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * This method returns all action plans.
   *
   * @return List<ActionPlanDTO> This returns all action plans.
   */
  @GET
  public final Response findActionPlans() {
    log.info("Entering findActionPlans...");
    List<ActionPlan> actionPlans = actionPlanService.findActionPlans();
    List<ActionPlanDTO> actionPlanDTOs = mapperFacade.mapAsList(actionPlans, ActionPlanDTO.class);
    ResponseBuilder responseBuilder = Response.ok(CollectionUtils.isEmpty(actionPlanDTOs) ? null : actionPlanDTOs);
    responseBuilder.status(CollectionUtils.isEmpty(actionPlanDTOs) ? Status.NO_CONTENT : Status.OK);
    return responseBuilder.build();
  }

  /**
   * This method returns the associated action plan for the specified action plan id.
   *
   * @param actionPlanId This is the action plan id
   * @return ActionPlanDTO This returns the associated action plan for the specified action plan id.
   * @throws CTPException if no action plan found for the specified action plan id.
   */
  @GET
  @Path("/{actionplanid}")
  public final Response findActionPlanByActionPlanId(@PathParam("actionplanid") final Integer actionPlanId)
      throws CTPException {
    log.info("Entering findActionPlanByActionPlanId with {}", actionPlanId);
    ActionPlan actionPlan = actionPlanService.findActionPlan(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "ActionPlan not found for id %s", actionPlanId);
    }
    return Response.ok(mapperFacade.map(actionPlan, ActionPlanDTO.class)).build();
  }

  /**
   * This method returns the associated action plan after it has been updated. Note that only the description and
   * the lastGoodRunDatetime can be updated.
   *
   * @param actionPlanId This is the action plan id
   * @param requestObject The object created by ActionPlanDTOMessageBodyReader from the json found in the request body
   * @return ActionPlanDTO This returns the updated action plan.
   * @throws CTPException if the json provided is incorrect or if the action plan id does not exist.
   */
  @PUT
  @Path("/{actionplanid}")
  public final Response updateActionPlanByActionPlanId(@PathParam("actionplanid") final Integer actionPlanId,
      final ActionPlanDTO requestObject) throws CTPException {
    log.info("UpdateActionPlanByActionPlanId with actionplanid {} - actionPlan {}", actionPlanId, requestObject);

    ActionPlan actionPlan = actionPlanService.updateActionPlan(actionPlanId,
        mapperFacade.map(requestObject, ActionPlan.class));
    if (actionPlan == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "ActionPlan not found for id %s", actionPlanId);
    } 
    return Response.ok(mapperFacade.map(actionPlan, ActionPlanDTO.class)).build();
  }

  /**
   * Returns all action rules for the given action plan id.
   * @param actionPlanId the action plan id
   * @return Returns all action rules for the given action plan id.
   * @throws CTPException summats went wrong
   */
  @GET
  @Path("/{actionplanid}/rules")
  public final Response returnActionRulesForActionPlanId(
      @PathParam("actionplanid") final Integer actionPlanId)
      throws CTPException {
    log.info("Entering returnActionRulesForActionPlanId with {}", actionPlanId);
    ActionPlan actionPlan = actionPlanService.findActionPlan(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "ActionPlan not found for id %s", actionPlanId);
    }

    List<ActionRule> actionRules = actionPlanService.findActionRulesForActionPlan(actionPlanId);
    List<ActionRuleDTO> actionRuleDTOs = mapperFacade.mapAsList(actionRules, ActionRuleDTO.class);
    ResponseBuilder responseBuilder = Response.ok(CollectionUtils.isEmpty(actionRuleDTOs) ? null : actionRuleDTOs);
    responseBuilder.status(CollectionUtils.isEmpty(actionRuleDTOs) ? Status.NO_CONTENT : Status.OK);
    return responseBuilder.build();
  }

}
