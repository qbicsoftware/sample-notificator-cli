package life.qbic.business.notification.create

import life.qbic.business.subscription.Subscriber

/**
 * <p>Defines what information is forwarded from the {@link CreateNotification} use case</p>
 *
 * @since 1.0.0
 *
*/
interface CreateNotificationOutput {

    /**
     * Returns a map containing the notification messages associated with their relevant subscriber for the provided Date
     *
     * @param notificationPerSubscriber Map containing the created Notification Messages associated with the subscriber
     * @since 1.0.0
     */

    void createdNotifications(Map<Subscriber, String> notificationPerSubscriber)

}