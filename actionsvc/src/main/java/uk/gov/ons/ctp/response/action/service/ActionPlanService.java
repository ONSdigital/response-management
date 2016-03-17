package uk.gov.ons.ctp.response.action.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;

/**
 * Created by Martin.Humphrey on 17/2/2016.
 */
public interface ActionPlanService extends CTPService {

  /**
   * This method returns all action plans.
   * @return List<ActionPlan> This returns all action plans.
   */
  List<ActionPlan> findActionPlans();

  /**
   * This method returns the action plan for the specified action plan id.
   * @param actionPlanId This is the action plan id
   * @return ActionPlan This returns the associated action plan.
   */
  ActionPlan findActionPlan(Integer actionPlanId);

  /**
   * This method returns the action plan after it has been updated. Note that only the description and
   * the lastGoodRunDatetime can be updated.
   * @param actionPlanId This is the action plan id of the action plan to be updated
   * @param actionPlan This is the action plan containing the potentially new description and lastGoodRunDatetime
   * @return ActionPlan This returns the updated action plan.
   */
  ActionPlan updateActionPlan(Integer actionPlanId, ActionPlan actionPlan);

  /**
   * Returns all action rules for the given action plan id.
   * @param actionPlanId the given action plan id
   * @return Returns all action rules for the given action plan id.
   */
  List<ActionRule> findActionRulesForActionPlan(Integer actionPlanId);
}
