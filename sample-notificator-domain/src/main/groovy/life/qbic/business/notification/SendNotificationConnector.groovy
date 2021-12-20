package life.qbic.business.notification

import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.business.notification.create.NotificationContent
import life.qbic.business.notification.refactor.SendEmailInput

/**
 * <p>Connects the {@link CreateNotificationOutput} and the {@link SendEmailInput}</p>
 */
class SendNotificationConnector implements CreateNotificationOutput {
    public static final Logging log = Logger.getLogger(SendNotificationConnector.class)
    private final SendEmailInput sendEmailInput

    SendNotificationConnector(SendEmailInput sendEmailInput) {
        this.sendEmailInput = sendEmailInput
    }

    @Override
    void createdNotifications(List<NotificationContent> notifications) {
        sendEmailInput.sendEmailNotifications(notifications)
    }

    @Override
    void failNotification(String notification) {
        log.error(notification)
        sendEmailInput.sendFailureEmail()
    }

}
