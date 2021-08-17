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
     * Returns the notification messages associated with the provided Date
     *
     * @param List containing the created Notification Messages
     * @since 1.0.0
     */

    void createdNotifications(Map<Subscriber, String> notificationsList)

}