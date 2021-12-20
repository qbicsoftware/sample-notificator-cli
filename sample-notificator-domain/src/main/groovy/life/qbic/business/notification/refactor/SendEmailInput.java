package life.qbic.business.notification.refactor;

import java.util.List;
import life.qbic.business.notification.create.NotificationContent;


public interface SendEmailInput {

  /**
   * <p>Sends email notifications to the customers concerned with the notification.</p>
   * <p>In case the list of notifications is empty, does nothing. Should at least one email not be
   * sent, this method triggers the sending of a failure email {@link #sendFailureEmail}</p>
   * @param notifications a list of notification contents to be sent
   */
  void sendEmailNotifications(List<NotificationContent> notifications);

  /**
   * <p>This method should be used whenever there was a system failure and the administrator
   * is to be bothered.</p>
   * <p>It sends an email informing of a system failure.</p>
   */
  void sendFailureEmail();
}
