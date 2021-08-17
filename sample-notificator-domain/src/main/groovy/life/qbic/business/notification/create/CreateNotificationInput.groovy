package life.qbic.business.notification.create

import life.qbic.business.subscription.Subscriber

/**
 * <h1>Interface to access and trigger the {@link CreateNotification} use case</h1>
 *
 * @since 1.0.0
 *
*/
interface CreateNotificationInput {

    /**
     * Trigger sending notifications to a list of subscribers about changes in their subscriptions
     * @param date A date in the format yyyy-mm-dd
     */
    void createNotifications(List<Subscriber> subscribers)
}