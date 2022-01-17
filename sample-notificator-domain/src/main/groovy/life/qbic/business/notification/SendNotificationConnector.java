package life.qbic.business.notification;

import java.util.List;
import life.qbic.business.logging.Logger;
import life.qbic.business.logging.Logging;
import life.qbic.business.notification.create.CreateNotificationOutput;
import life.qbic.business.notification.create.NotificationContent;
import life.qbic.business.notification.send.SendEmailInput;

/**
 * <p>Connects the {@link CreateNotificationOutput} and the {@link SendEmailInput}</p>
 */
public class SendNotificationConnector implements CreateNotificationOutput {
  public static final Logging log = Logger.getLogger(SendNotificationConnector.class);
    private final SendEmailInput sendEmailInput;

    public SendNotificationConnector(SendEmailInput sendEmailInput) {
        this.sendEmailInput = sendEmailInput;
    }

    @Override
    public void createdNotifications(List<NotificationContent> notifications) {
        sendEmailInput.sendEmailNotifications(notifications);
    }

    @Override
    public void failNotification(String notification) {
        log.error(notification);
        sendEmailInput.sendFailureEmail();
    }

}
