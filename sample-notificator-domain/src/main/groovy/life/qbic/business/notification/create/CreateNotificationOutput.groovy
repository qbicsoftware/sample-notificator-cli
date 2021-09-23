package life.qbic.business.notification.create

/**
 * <p>Defines what information is forwarded from the {@link CreateNotification} use case</p>
 *
 * @since 1.0.0
 *
*/
interface CreateNotificationOutput {

    /**
     * Returns a NoticiationContent DTO per project containing the information for updated projects for the provided Date
     *
     * @param notifications List of DTOs containing the update information for one project each to send out
     * @since 1.0.0
     */

    void createdNotifications(List<NotificationContent> notifications)

    void failNotification(String notification)
}