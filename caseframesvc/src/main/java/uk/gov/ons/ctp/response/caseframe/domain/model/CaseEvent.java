package uk.gov.ons.ctp.response.caseframe.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

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
@Table(name = "caseevent", schema = "caseframe")
public class CaseEvent implements Serializable {

  private static final long serialVersionUID = 6034836141646834386L;

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO, generator="caseeventseq_gen")
  @SequenceGenerator(name="caseeventseq_gen", sequenceName="caseframe.caseeventidseq")
  @Column(name = "caseeventid")
  private Integer caseEventId;

  @Column(name = "caseid")
  private Integer caseId;

  private String description;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_datetime")
  private Timestamp createdDatetime;

  private String category;

  // TODO Uncoment once the db scripts have been amended. Field is required: I checked with John.
  //private String subCategory;

}
