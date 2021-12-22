package life.qbic.samplenotificator.components.email.html;

import life.qbic.business.notification.create.NotificationContent;
import life.qbic.business.notification.refactor.EmailGenerator;

/**
 * Generates {@link HtmlNotificationEmail}s and fills them with the provided {@link NotificationContent}
 */
public class HtmlEmailGenerator implements EmailGenerator<HtmlNotificationEmail> {


  @Override
  public HtmlNotificationEmail apply(NotificationContent notificationContent) {
    HtmlNotificationEmail notificationEmail = new HtmlNotificationEmail();
    notificationEmail.fill(notificationContent);
    return notificationEmail;
  }
}
