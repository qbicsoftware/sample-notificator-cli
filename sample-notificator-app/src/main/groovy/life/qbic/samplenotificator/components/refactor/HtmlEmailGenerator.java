package life.qbic.samplenotificator.components.refactor;

import life.qbic.business.notification.create.NotificationContent;
import life.qbic.business.notification.refactor.EmailGenerator;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class HtmlEmailGenerator implements EmailGenerator<HtmlNotificationEmail> {

  /**
   * Applies this function to the given argument.
   *
   * @param notificationContent the function argument
   * @return the function result
   */
  @Override
  public HtmlNotificationEmail apply(NotificationContent notificationContent) {
    HtmlNotificationEmail notificationEmail = new HtmlNotificationEmail();
    notificationEmail.fill(notificationContent);
    return notificationEmail;
  }
}
