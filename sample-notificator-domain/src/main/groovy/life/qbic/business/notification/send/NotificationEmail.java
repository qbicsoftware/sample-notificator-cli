package life.qbic.business.notification.send;

import life.qbic.business.notification.create.NotificationContent;

/**
 * <p>An email containing information of a NotificationContent</p>
 */
public interface NotificationEmail {

  /**
   * Fills the email with the provided content
   * @param content the notification content that needs to be put into the email
   * @since 1.2.0
   */
  void fill(NotificationContent content);

  /**
   * Returns the body of the email
   * @return the body of the email
   * @since 1.2.0
   */
  String body();

  /**
   * Returns the subject of the email
   * @return the subject of the email
   */
  String subject();

  /**
   * Returns the email recipient
   * @return the email recipient
   * @since 1.2.0
   */
  String recipient();
}
