package life.qbic.business.notification.send;

import java.util.List;
import java.util.function.Consumer;
import life.qbic.business.notification.create.NotificationContent;

/**
 * <p>Implements the business logic of sending emails for project updates.</p>
 */
public class SendEmail implements Consumer<List<NotificationContent>>, SendEmailInput{
    private final EmailSender<NotificationEmail> sendOrRemember;
    private final FailureEmailSender failureSender;
    private final EmailGenerator<NotificationEmail> generateEmail;

    public SendEmail(EmailSender<NotificationEmail> sendOrRemember, FailureEmailSender failureSender, EmailGenerator<NotificationEmail> generateEmail) {
        this.sendOrRemember = sendOrRemember;
        this.failureSender = failureSender;
        this.generateEmail = generateEmail;
    }

    /**
     * @see #sendEmailNotifications
     */
    @Override
    public void accept(List<NotificationContent> notificationContents) {
        // map notification contents to emails
        notificationContents.stream()
                .map(generateEmail)
                .forEach(sendOrRemember);
        if (unsentMailsExist()) {
            sendFailureEmail();
        }
    }

    private boolean unsentMailsExist() {
        if (sendOrRemember.notSent() == null) {
            return false;
        }
        return !sendOrRemember.notSent().isEmpty();
    }

    @Override
    public void sendEmailNotifications(List<NotificationContent> notifications) {
        accept(notifications);
    }

    @Override
    public void sendFailureEmail() {
        failureSender.sendFailure();
    }
}
