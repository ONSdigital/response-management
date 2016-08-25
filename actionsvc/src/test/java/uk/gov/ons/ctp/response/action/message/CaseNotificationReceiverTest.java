package uk.gov.ons.ctp.response.action.message;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType.CLOSED;
import static uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType.CREATED;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.action.service.CaseNotificationService;

/**
 * Test Spring Integration flow of Case Notification life cycle messages
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/CaseNotificationServiceTest-context.xml"})
public class CaseNotificationReceiverTest {

  private static final int RECEIVE_TIMEOUT = 20000;
  private static final String INVALID_CASE_NOTIFICATION_LOG_DIRECTORY =
      "/var/log/ctp/responsemanagement/actionsvc/notification";
  private static final String VALIDXML_PART1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
      + "<ns2:caseNotifications xmlns:ns2=\"http://ons.gov.uk/ctp/response/casesvc/message/notification\">"
      + "<caseNotification>"
      + "<caseId>1</caseId>"
      + "<actionPlanId>3</actionPlanId>";
  private static final String VALIDXML_PART2 = "</caseNotification>"
      + "<caseNotification>"
      + "<caseId>2</caseId>"
      + "<actionPlanId>3</actionPlanId>"
      + "<notificationType>CREATED</notificationType>"
      + "</caseNotification>"
      + "</ns2:caseNotifications>";

  @Inject
  private MessageChannel caseNotificationOutbound;

  @Inject
  private CaseNotificationService caseNotificationService;

  @Inject
  private MessageChannel caseNotificationXml;

  @Inject
  private PollableChannel activeMQDLQXml;

  /**
   * Initialise tests
   *
   * @throws IOException from FileUtils
   */
  @Before
  public void setUpAndInitialVerification() throws IOException {
    File logDir = new File(INVALID_CASE_NOTIFICATION_LOG_DIRECTORY);
    if (!logDir.exists()) {
      logDir.mkdir();
    }
    FileUtils.cleanDirectory(logDir);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length);

  }

  /**
   * Test whole SI flow with valid XML
   *
   * @throws Exception if CountDownLatch interrupted
   */
  @Test
  public void testNotificationXmlValid() throws Exception {
    String testMessage = VALIDXML_PART1
        + "<notificationType>CLOSED</notificationType>"
        + VALIDXML_PART2;

    // Test java objects that should be received
    List<ActionCase> lifeCycleEvents = new ArrayList<ActionCase>();
    lifeCycleEvents.add(new ActionCase(3, 1, CLOSED));
    lifeCycleEvents.add(new ActionCase(3, 2, CREATED));

    // SetUp CountDownLatch for synchronisation with async call
    final CountDownLatch serviceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock caseNotificationService
    // acceptNotification method is called
    doAnswer(countsDownLatch(serviceInvoked)).when(caseNotificationService).acceptNotification(lifeCycleEvents);
    // Send message
    caseNotificationOutbound.send(MessageBuilder.withPayload(testMessage).build());
    // Await synchronisation with the asynchronous message call
    serviceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    // Test not rejected to notificationXmlInvalid channel
    File logDir = new File(INVALID_CASE_NOTIFICATION_LOG_DIRECTORY);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length);

    verify(caseNotificationService).acceptNotification(lifeCycleEvents);

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
    caseNotificationXml.send(MessageBuilder.withPayload(testMessage).build());

    File logDir = new File(INVALID_CASE_NOTIFICATION_LOG_DIRECTORY);
    File[] files = logDir.listFiles();
    assertEquals(1, files.length);
  }

  /**
   * Test SI sent badly formed XML to generate a parse error results in ActiveMQ
   * dead letter queue message. Local Transaction should rollback and message be
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

    File logDir = new File(INVALID_CASE_NOTIFICATION_LOG_DIRECTORY);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length);

    verify(caseNotificationService, never()).acceptNotification(any());
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
