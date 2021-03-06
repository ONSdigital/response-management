package uk.gov.ons.ctp.response.casesvc.message;


import java.util.List;

import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;

/**
 * Service responsible for publishing case lifecycle events to notification
 * channel
 *
 */
public interface CaseNotificationPublisher {

  /**
   * To put CaseNotifications on the outbound channel caseNotificationOutbound
   * @param caseNotifications the list of CaseNotification to put on the outbound channel
   */
  void sendNotifications(List<CaseNotification> caseNotifications);
}
