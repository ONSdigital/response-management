package uk.gov.ons.ctp.response.action.message;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType.DISABLED;
import static uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType.ACTIVATED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.ons.ctp.response.action.service.CaseNotificationService;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;

/**
 * Test Spring Integration flow of Case Notification life cycle messages
 *
 */
@ContextConfiguration(locations = {"/springintegration/CaseNotificationServiceTest-context.xml"})
@TestPropertySource("classpath:/application-test.properties")
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseNotificationReceiverTest {

  private static final int RECEIVE_TIMEOUT = 20000;
  private static final String VALIDXML_PART1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
      + "<ns2:caseNotifications xmlns:ns2=\"http://ons.gov.uk/ctp/response/casesvc/message/notification\">"
      + "<caseNotification>"
      + "<caseId>1</caseId>"
      + "<actionPlanId>3</actionPlanId>";
  private static final String VALIDXML_PART2 = "</caseNotification>"
      + "<caseNotification>"
      + "<caseId>2</caseId>"
      + "<actionPlanId>3</actionPlanId>"
      + "<notificationType>ACTIVATED</notificationType>"
      + "</caseNotification>"
      + "</ns2:caseNotifications>";

  @Inject
  private MessageChannel caseNotificationOutbound;

  @Inject
  private CaseNotificationService caseNotificationService;

  @Inject
  private MessageChannel caseLifecycleEventsXml;

  @Inject
  private PollableChannel activeMQDLQXml;

  /**
   * Initialise tests
   *
   * @throws IOException from FileUtils
   */
  @Before
  public void setUpAndInitialVerification() throws IOException {
    // TODO
  }

  /**
   * Test whole SI flow with valid XML
   *
   * @throws Exception if CountDownLatch interrupted
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  //@Test - commented out until such time that it runs reliably in both eclipse and command line!
  public void testNotificationXmlValid() throws Exception {
    String testMessage = VALIDXML_PART1
        + "<notificationType>DISABLED</notificationType>"
        + VALIDXML_PART2;

    // SetUp CountDownLatch for synchronisation with async call
    final CountDownLatch serviceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock caseNotificationService.acceptNotification method is called
    doAnswer(countsDownLatch(serviceInvoked)).when(caseNotificationService).acceptNotification(any());

    // Send message
    caseNotificationOutbound.send(MessageBuilder.withPayload(testMessage).build());
    // Await synchronisation with the asynchronous message call
    serviceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    // TODO Check no msg ends up on invalid queue

    // Test java objects that should be received
    List<CaseNotification> lifeCycleEvents = new ArrayList<CaseNotification>();
    lifeCycleEvents.add(new CaseNotification(1, 3, DISABLED));
    lifeCycleEvents.add(new CaseNotification(2, 3, ACTIVATED));

    ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
    verify(caseNotificationService).acceptNotification((List<CaseNotification>) argumentCaptor.capture());
    assertThat(argumentCaptor.getValue().get(0), samePropertyValuesAs(lifeCycleEvents.get(0)));
    assertThat(argumentCaptor.getValue().get(1), samePropertyValuesAs(lifeCycleEvents.get(1)));
  }

  /**
   * Test invalid well formed XML should go to file
   */
  @Test
  public void testNotificationXmlInvalid() {
    String testMessage = VALIDXML_PART1
        + "<notificationType>RUBBISH</notificationType>"
        + VALIDXML_PART2;

    // Send direct to flow rather than JMS queue to avoid problems with
    // asynchronous threads
    caseLifecycleEventsXml.send(MessageBuilder.withPayload(testMessage).build());

    // TODO Check msg ends up on invalid queue
  }

  /**
   * SI sent badly formed XML to generate a parse error results in ActiveMQ dead
   * letter queue message. Local transaction should rollback and message should be
   * considered a poisoned bill.
   */
  @Test
  public void testNotificationXmlBadlyFormed() {
    String testMessage = VALIDXML_PART1
        + "<notificationType>NO CLOSING TAG<notificationType>"
        + VALIDXML_PART2;

    caseNotificationOutbound.send(MessageBuilder.withPayload(testMessage).build());

    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    String payload = (String) message.getPayload();
    assertEquals(testMessage, payload);

    // TODO Check NO msg ends up on invalid queue
  }

  /**
   * Should be called when mock method is called in asynchronous test to
   * countDown the CountDownLatch test thread is waiting on.
   *
   * @param serviceInvoked CountDownLatch to countDown
   * @return Answer<CountDownLatch> Mockito Answer object
   */
  private Answer<CountDownLatch> countsDownLatch(final CountDownLatch serviceInvoked) {
    return new Answer<CountDownLatch>() {
      @Override
      public CountDownLatch answer(InvocationOnMock invocationOnMock)
          throws Throwable {
        serviceInvoked.countDown();
        return null;
      }
    };
  }
}
