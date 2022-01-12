package life.qbic.business.notification.refactor;

import life.qbic.business.notification.create.NotificationContent;

/**
 * <p>An email containing information of a NotificationContent</p>
 */
public interface NotificationEmail {
  void fill(NotificationContent content);
  String body();
  String recipient();
}
