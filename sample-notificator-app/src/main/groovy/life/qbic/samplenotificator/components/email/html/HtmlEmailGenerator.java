package life.qbic.samplenotificator.components.email.html;

import life.qbic.business.notification.create.NotificationContent;
import life.qbic.business.notification.send.EmailGenerator;
import life.qbic.business.notification.unsubscription.UnsubscriptionLinkSupplier;

/**
 * Generates {@link HtmlNotificationEmail}s and fills them with the provided {@link NotificationContent}
 */
public class HtmlEmailGenerator implements EmailGenerator<HtmlNotificationEmail> {

  private final UnsubscriptionLinkSupplier unsubscriptionLinkSupplier;

  public HtmlEmailGenerator(
      UnsubscriptionLinkSupplier unsubscriptionLinkSupplier) {
    this.unsubscriptionLinkSupplier = unsubscriptionLinkSupplier;
  }

  @Override
  public HtmlNotificationEmail apply(NotificationContent notificationContent) {
    HtmlNotificationEmail notificationEmail = new HtmlNotificationEmail(unsubscriptionLinkSupplier);
    notificationEmail.fill(notificationContent);
    return notificationEmail;
  }
}
