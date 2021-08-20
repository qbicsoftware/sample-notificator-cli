package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.business.subscription.Subscriber

/**
 * Presenter for the CreateNotification Use Case
 *
 *  This presenter handles the output of the CreateNotification use case and prepares it for the sending process
 *
 * @since: 1.0.0
 *
 */
class CreateNotificationPresenter implements CreateNotificationOutput {

    Map<Subscriber, String> notificationPerSubscriber

    //ToDo adapt output for emailService
    CreateNotificationPresenter(){

    }

    @Override
    void createdNotifications(Map<Subscriber, String> notificationPerSubscriber) {
        this.notificationPerSubscriber = notificationPerSubscriber
        this.notificationPerSubscriber.each {
            println(it.value)
        }
    }
}
