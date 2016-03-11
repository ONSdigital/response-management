package uk.gov.ons.ctp.response.action;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;
import uk.gov.ons.ctp.response.action.representation.ActionRuleDTO;

/**
 * The bean mapper to go from Entity objects to Presentation objects.
 */
@Named
public class ActionBeanMapper extends ConfigurableMapper {

  /**
   * This method configures the bean mapper.
   * @param factory the mapper factory
   */
  protected final void configure(final MapperFactory factory) {
    factory
      .classMap(Action.class, ActionDTO.class)
      .byDefault()
      .register();

    factory
      .classMap(ActionPlan.class, ActionPlanDTO.class)
      .byDefault()
      .register();

    factory
        .classMap(ActionRule.class, ActionRuleDTO.class)
        .byDefault()
        .register();
  }
}
