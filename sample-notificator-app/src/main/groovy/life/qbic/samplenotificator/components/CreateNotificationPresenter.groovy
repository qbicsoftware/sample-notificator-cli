package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.business.subscription.Subscriber
import life.qbic.business.subscription.fetch.FetchSubscriberOutput

/**
 * Presenter for the CreateNotification Use Case
 *
 *  This presenter handles the output of the CreateNotification use case and prepares it for the sending process
 *
 * @since: 1.0.0
 *
 */
class CreateNotificationPresenter implements CreateNotificationOutput, FetchSubscriberOutput {

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

    /**
    * Transfers the generated list of subscribers to the implementing class
    * @param subscribers the retrieved list of subscribers with modified subscriptions
    * @since 1.0.0
    */
    @Override
    void fetchedSubscribers(List<Subscriber> subscribers) {

    }

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    @Override
    void failNotification(String notification) {

    }
}
