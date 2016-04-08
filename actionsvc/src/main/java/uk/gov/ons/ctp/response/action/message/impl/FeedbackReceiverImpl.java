package uk.gov.ons.ctp.response.action.message.impl;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import uk.gov.ons.ctp.response.action.message.FeedbackReceiver;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

@MessageEndpoint
public class FeedbackReceiverImpl implements FeedbackReceiver {
  /* (non-Javadoc)
   * @see uk.gov.ons.ctp.response.action.message.impl.FeedbackReceiver#acceptFeedback(uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback)
   */
  @Override
  @ServiceActivator(inputChannel="feedbackTransformed")
  public void acceptFeedback(ActionFeedback feedback) {
    System.out.println("We have feedback with situation " + feedback.getSituation());
  }
}
