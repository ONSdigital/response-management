package uk.gov.ons.ctp.response.action.state;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.state.StateTransitionException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;

/**
 * A test of the state transition manager
 * It simply has to test a single good and a single bad transition - all it is testing is the underlying mechanism,
 * not a real implementation, where we will want to assert all of the valid and invalid transitions
 *
 */
@Slf4j
public class TestActionStateTransitionManager {

  
  private Map<ActionDTO.ActionState, Map<ActionEvent, ActionDTO.ActionState>> validTransitions = new HashMap<>();

  /**
   * Setup the transitions
   */
  @Before
  public void setup() {
    Map<ActionEvent, ActionDTO.ActionState> submittedTransitions = new HashMap<> ();
    submittedTransitions.put(ActionEvent.REQUEST_DISTRIBUTED, ActionDTO.ActionState.PENDING);
    submittedTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.ABORTED);
    validTransitions.put(ActionDTO.ActionState.SUBMITTED, submittedTransitions);

    Map<ActionEvent, ActionDTO.ActionState> pendingTransitions = new HashMap<> ();
    pendingTransitions.put(ActionEvent.REQUEST_FAILED, ActionDTO.ActionState.SUBMITTED);
    pendingTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    pendingTransitions.put(ActionEvent.REQUEST_ACCEPTED, ActionDTO.ActionState.ACTIVE);
    pendingTransitions.put(ActionEvent.REQUEST_COMPLETED, ActionDTO.ActionState.COMPLETED);
    validTransitions.put(ActionDTO.ActionState.PENDING, pendingTransitions);

    Map<ActionEvent, ActionDTO.ActionState> activeTransitions = new HashMap<> ();
    activeTransitions.put(ActionEvent.REQUEST_FAILED, ActionDTO.ActionState.SUBMITTED);
    activeTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    activeTransitions.put(ActionEvent.REQUEST_COMPLETED, ActionDTO.ActionState.COMPLETED);
    validTransitions.put(ActionDTO.ActionState.ACTIVE, activeTransitions);

    Map<ActionEvent, ActionDTO.ActionState> completedTransitions = new HashMap<> ();
    completedTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.COMPLETED);
    validTransitions.put(ActionDTO.ActionState.COMPLETED, completedTransitions);

    Map<ActionEvent, ActionDTO.ActionState> cancelSubmittedTransitions = new HashMap<> ();
    cancelSubmittedTransitions.put(ActionEvent.REQUEST_FAILED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    cancelSubmittedTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    cancelSubmittedTransitions.put(ActionEvent.REQUEST_ACCEPTED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    cancelSubmittedTransitions.put(ActionEvent.REQUEST_COMPLETED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    cancelSubmittedTransitions.put(ActionEvent.CANCELLATION_DISTRIBUTED, ActionDTO.ActionState.CANCEL_PENDING);
    validTransitions.put(ActionDTO.ActionState.CANCEL_SUBMITTED, cancelSubmittedTransitions);

    Map<ActionEvent, ActionDTO.ActionState> cancelPendingTransitions = new HashMap<> ();
    cancelPendingTransitions.put(ActionEvent.REQUEST_FAILED, ActionDTO.ActionState.CANCEL_PENDING);
    cancelPendingTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.CANCEL_PENDING);
    cancelPendingTransitions.put(ActionEvent.REQUEST_ACCEPTED, ActionDTO.ActionState.CANCEL_PENDING);
    cancelPendingTransitions.put(ActionEvent.REQUEST_COMPLETED, ActionDTO.ActionState.CANCEL_PENDING);
    cancelPendingTransitions.put(ActionEvent.CANCELLATION_FAILED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    cancelPendingTransitions.put(ActionEvent.CANCELLATION_ACCEPTED, ActionDTO.ActionState.CANCELLING);
    cancelPendingTransitions.put(ActionEvent.CANCELLATION_COMPLETED, ActionDTO.ActionState.CANCELLED);
    validTransitions.put(ActionDTO.ActionState.CANCEL_PENDING, cancelPendingTransitions);

    Map<ActionEvent, ActionDTO.ActionState> cancellingTransitions = new HashMap<> ();
    cancellingTransitions.put(ActionEvent.CANCELLATION_FAILED, ActionDTO.ActionState.CANCEL_SUBMITTED);
    cancellingTransitions.put(ActionEvent.CANCELLATION_COMPLETED, ActionDTO.ActionState.CANCELLED);
    cancellingTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.CANCELLING);
    validTransitions.put(ActionDTO.ActionState.CANCELLING, cancellingTransitions);

    Map<ActionEvent, ActionDTO.ActionState> cancelledTransitions = new HashMap<> ();
    cancelledTransitions.put(ActionEvent.REQUEST_CANCELLED, ActionDTO.ActionState.CANCELLED);
    validTransitions.put(ActionDTO.ActionState.CANCELLED, cancelledTransitions);
  }

  /**
   * test a valid transition
   * @throws StateTransitionException shouldn't!
   */
  @Test
  public void testActionTransitions() throws StateTransitionException {
    StateTransitionManagerFactory stmFactory = new ActionSvcStateTransitionManagerFactory();
    StateTransitionManager<ActionDTO.ActionState,  ActionEvent> stm = stmFactory.getStateTransitionManager(ActionSvcStateTransitionManagerFactory.ACTION_ENTITY);

    for (Map.Entry<ActionDTO.ActionState, Map<ActionEvent, ActionDTO.ActionState>> me : validTransitions.entrySet()) {
      ActionDTO.ActionState sourceState = me.getKey();
      for (Map.Entry<ActionEvent, ActionDTO.ActionState> statesTransition : me.getValue().entrySet()) {
        log.debug("Asserting valid transition {}({}) -> {}", sourceState, statesTransition.getKey(), statesTransition.getValue());
        Assert.assertEquals(statesTransition.getValue(), stm.transition(sourceState, statesTransition.getKey()));
      }
      ActionEvent [] allEvents = ActionEvent.values();
      for (ActionEvent event : allEvents) {
        if (!me.getValue().keySet().contains(event)) {
          boolean caught = false;
          try {
            log.debug("Asserting invalid transition {}({})", sourceState, event);
            stm.transition(sourceState, event);
          } catch (StateTransitionException ste) {
            caught = true;
          }
          Assert.assertTrue("Transition "+sourceState+"(" + event + ") should be invalid", caught);
        }
      }
    }
  }

}
