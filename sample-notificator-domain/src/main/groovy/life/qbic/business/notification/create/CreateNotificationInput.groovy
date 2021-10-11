package life.qbic.business.notification.create

/**
 * <h1>Interface to access and trigger the {@link CreateNotification} use case</h1>
 *
 * @since 1.0.0
 *
*/
interface CreateNotificationInput {

    /**
     * Trigger creating template notification messages for a list of subscribers about changes in their subscriptions
     * @param date A date in the format yyyy-mm-dd
     */
    void createNotifications(String date)
}