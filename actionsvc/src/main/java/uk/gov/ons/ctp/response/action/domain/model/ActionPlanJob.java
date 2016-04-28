package uk.gov.ons.ctp.response.action.domain.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;

/**
 * Domain model object.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "actionplanjob", schema = "action")
public class ActionPlanJob {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "actionplanjobseq_gen")
  @SequenceGenerator(name = "actionplanjobseq_gen", sequenceName = "action.actionplanjobseq")
  @Column(name = "actionplanjobid")
  private Integer actionPlanJobId;

  @Column(name = "actionplanid")
  private Integer actionPlanId;

  @Column(name = "createdby")
  private String createdBy;

  @Enumerated(EnumType.STRING)
  private ActionPlanJobDTO.ActionPlanJobState state;

  @Column(name = "createddatetime")
  private Timestamp createdDatetime;

  @Column(name = "updateddatetime")
  private Timestamp updatedDateTime;
}
