package uk.gov.ons.ctp.response.action.message;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.integration.test.matcher.HeaderMatcher.hasHeader;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;

@ContextConfiguration(locations = { "/InstructionServiceTest-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class InstructionServiceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Autowired
	InstructionPublisher instructionService;
	
	@Autowired
	MessageChannel instructionOutbound;

	@Autowired
	QueueChannel testInstructionOutbound;
	
	@Autowired
	QueueChannel testInstructionXml;
	
	@Test
	public void testCreateOutBoundMessageToFieldHandler() {
		try {

			ActionInstruction instruction = new ActionInstruction();
			ActionRequest request = new ActionRequest();
			request.setActionType("testActionType");
			ActionRequests requests = new ActionRequests();
			requests.getActionRequests().add(request);
			instruction.setActionRequests(requests);

			instructionOutbound.send(MessageBuilder.withPayload(instruction).setHeader("HANDLER", "Field").build());

			Message<?> instructionMessage = testInstructionXml.receive(0);
			assertNotNull("instructionMessage should not be null", instructionMessage);
			System.out.println("instructionMessage: " + instructionMessage);

			// test instructionXml payload contains <actionType> value testActionType
			boolean payLoadContainsAdaptor = instructionMessage.getPayload().toString()
					.contains("<actionType>testActionType</actionType>");
			assertTrue("Payload does not contain reference to <actionType>testActionType</actionType>",
					payLoadContainsAdaptor);
			assertThat(instructionMessage, hasHeader("HANDLER", "Field"));

			instructionMessage = testInstructionXml.receive(0);
			assertNull("Only one message expected from instructionXml", instructionMessage);
			
			// TODO
			// test ActiveMQ message generated with expected content
		
		} catch (Exception ex) {
			fail("testCreateOutBoundMessageToFieldHandler has failed " + ex.getMessage());
		}
	}

//	@Test
	public void testSendRequestViaInstructionService() {
		try {
			String handler = "Field";
			
			ActionInstruction actionInstruction = instructionService.sendRequests(handler, new ArrayList<ActionRequest>());
			actionInstruction.getActionRequests();
			
			// test if instructionFieldMessage channel has an ActionInstruction payload
			Message<?> instructionFieldMessage = testInstructionOutbound.receive(0);
			assertNotNull("instructionMessage should not be null", instructionFieldMessage);
			String payload = instructionFieldMessage.getPayload().toString();
			System.out.println("instructionOutbound: " + payload);			
			assertTrue("instructionOutbound message missing content", payload.contains("uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction"));
			
			// test instructionXml payload contains <actionType> value testActionType
			Message<?> instructionMessage = testInstructionXml.receive(0);
			assertNotNull("instructionMessage should not be null", instructionMessage);
			System.out.println("instructionMessage: " + instructionMessage);
//			boolean payLoadContainsAdaptor = instructionMessage.getPayload().toString()
//					.contains("<actionType>testActionType</actionType>");
//			assertTrue("Payload does not contain reference to <actionType>testActionType</actionType>",
//					payLoadContainsAdaptor);
			assertThat(instructionMessage, hasHeader("HANDLER", "Field"));
			

		} catch (Exception ex) {
			fail("testSendRequestViaInstructionService has failed " + ex.getMessage());
		}

	}

}
