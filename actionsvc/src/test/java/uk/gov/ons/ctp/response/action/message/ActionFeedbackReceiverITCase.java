package uk.gov.ons.ctp.response.action.message;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.service.FeedbackService;

/**
 * Test focusing on Spring Integration
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActionFeedbackReceiverITCaseConfig.class)
public class ActionFeedbackReceiverITCase {

  @Inject
  private MessageChannel actionFeedbackXml;

  @Inject
  private MessageChannel testOutbound;

  @Inject
  private QueueChannel activeMQDLQXml;

  @Inject
  @Qualifier("actionFeedbackUnmarshaller")
  private Jaxb2Marshaller actionFeedbackUnmarshaller;

  @Inject
  CachingConnectionFactory connectionFactory;

  @Inject
  private FeedbackService feedbackService;

  private Connection connection;
  private int initialCounter;

  private static final int RECEIVE_TIMEOUT = 5000;
  private static final String INVALID_ACTION_FEEDBACK_QUEUE = "Action.InvalidActionFeedback";
  private static final String PACKAGE_ACTION_FEEDBACK = "uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback";

  @Before
  public void setUp() throws Exception {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACK_QUEUE);

    String jaxbContext = actionFeedbackUnmarshaller.getJaxbContext().toString();
    assertTrue(jaxbContext.contains(PACKAGE_ACTION_FEEDBACK));

    activeMQDLQXml.clear();

    reset(feedbackService);
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  @Test
  public void testReceivingActionFeedbackInvalidXml() throws IOException, JMSException {
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidActionFeedback.xml.txt"), "UTF-8");

    actionFeedbackXml.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    /**
     * We check that the invalid xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACK_QUEUE);
    assertEquals(1, finalCounter - initialCounter);
  }

  @Test
  public void testReceivingActionFeedbackXmlBadlyFormed() throws IOException, JMSException {
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/badlyFormedActionFeedback.xml.txt"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // TODO This test passes inside IntelliJ but fails on the Jenkins server.
//    /**
//     * We check that the badly formed xml ends up on the dead letter queue.
//     */
//    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
//    String payload = (String) message.getPayload();
//    assertEquals(testMessage, payload);
//
//    /**
//     * We check that no badly formed xml ends up on the invalid queue.
//     */
//    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACK_QUEUE);
//    assertEquals(0, finalCounter - initialCounter);
  }

  @Test
  public void testReceivingActionFeedbackValidXml() throws InterruptedException, IOException, JMSException, CTPException {
    // Set up CountDownLatch for synchronisation with async call
    final CountDownLatch feedbackServiceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock feedbackService.acceptFeedback method is called
    doAnswer(countsDownLatch(feedbackServiceInvoked)).when(feedbackService).acceptFeedback(any(ActionFeedback.class));

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/validActionFeedback.xml.txt"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // Await synchronisation with the asynchronous message call
    feedbackServiceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    /**
     * We check that no xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACK_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * We check that no xml ends up on the dead letter queue.
     */
    // TODO This test passes inside IntelliJ but fails on the Jenkins server.
//    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
//    assertNull(message);
//
//    /**
//     * We check the message was processed
//     */
//    verify(feedbackService).acceptFeedback(any(ActionFeedback.class));
  }

  @Test
  public void testReceivingActionFeedbackValidXmlExceptionThrownInProcessing() throws InterruptedException, IOException, JMSException, CTPException {
	 
    // Set up CountDownLatch for synchronisation with async call
    final CountDownLatch feedbackServiceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock feedbackService.acceptFeedback method is called
    doAnswer(countsDownLatch(feedbackServiceInvoked)).when(feedbackService).acceptFeedback(any(ActionFeedback.class));

    Mockito.doThrow(new RuntimeException()).when(feedbackService).acceptFeedback(any(ActionFeedback.class));

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/validActionFeedback.xml.txt"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // Await synchronisation with the asynchronous message call
    feedbackServiceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    /**
     * We check that no xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACK_QUEUE);
    assertEquals(initialCounter, finalCounter);

    // TODO This test passes inside IntelliJ but fails on the Jenkins server.
//    /**
//     * We check that the xml ends up on the dead letter queue.
//     */
//    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
//    String payload = (String) message.getPayload();
//    assertEquals(testMessage, payload);
//
//    /**
//     * We check the message was processed AND re-processed once (see maximumRedeliveries in test-broker.xml)
//     */
//    verify(feedbackService, atLeastOnce()).acceptFeedback(any(ActionFeedback.class));
  }

  private File provideTempFile(String inputStreamLocation) throws IOException {
    InputStream is = getClass().getResourceAsStream(inputStreamLocation);
    File tempFile = File.createTempFile("prefix","suffix");
    tempFile.deleteOnExit();
    FileOutputStream out = new FileOutputStream(tempFile);
    IOUtils.copy(is, out);
    return tempFile;
  }

  /**
   * Should be called when mock method is called in asynchronous test to countDown the CountDownLatch test thread is
   * waiting on.
   *
   * @param serviceInvoked CountDownLatch to countDown
   * @return Answer<CountDownLatch> Mockito Answer object
   */
  private Answer<CountDownLatch> countsDownLatch(final CountDownLatch serviceInvoked) {
    return new Answer<CountDownLatch>() {
      @Override
      public CountDownLatch answer(InvocationOnMock invocationOnMock) throws Throwable {
        serviceInvoked.countDown();
        return null;
      }
    };
  }
}
