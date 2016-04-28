package uk.gov.ons.ctp.response.action.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "actionrule", schema = "action")
public class ActionRule implements Serializable {

  private static final long serialVersionUID = 4524689072566205066L;

  @Id
  @GeneratedValue
  @Column(name = "actionruleid")
  private Integer actionRuleId;

  @Column(name = "actionplanid")
  private Integer actionPlanId;

  private Integer priority;

  @Column(name = "surveydatedaysoffset")
  private Integer surveyDateDaysOffset;

  @Column(name = "actiontypename")
  private String actionTypeName;

  private String name;

  private String description;
}
