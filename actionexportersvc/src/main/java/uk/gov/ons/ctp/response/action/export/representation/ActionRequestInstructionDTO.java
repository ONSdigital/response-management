package uk.gov.ons.ctp.response.action.export.representation;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.Priority;

/**
 * Representation of an ActionRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ActionRequestInstructionDTO {
  @Id
  private BigInteger actionId;
  private boolean responseRequired;
  private String actionPlan;
  private String actionType;
  private String questionSet;
  private ActionContact contact;
  private ActionAddress address;
  private BigInteger caseId;
  private Priority priority;
  private String caseRef;
  private String iac;
  private ActionEvent events;
  private Date dateStored;
  private Date dateSent;
}
