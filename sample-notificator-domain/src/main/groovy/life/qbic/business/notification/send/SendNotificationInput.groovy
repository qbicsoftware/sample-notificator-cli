package life.qbic.business.notification.send
/**
 * <h1>Interface to access and trigger the {@link SendNotification} use case</h1>
 *
 * @since 1.0.0
 *
*/
interface SendNotificationInput {

    /**
     * Trigger sending notifications to a list of subscribers about changes in their subscriptions
     */
    void sendNotifications()
}