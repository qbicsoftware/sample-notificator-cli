package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.business.notification.create.NotificationContent

/**
 * Presenter for the CreateNotification Use Case
 *
 *  This presenter handles the output of the CreateNotification use case and prepares it for the sending process
 *
 * @since: 1.0.0
 *
 */
class CreateNotificationPresenter implements CreateNotificationOutput {

    List<NotificationContent> notifications

    CreateNotificationPresenter(List<NotificationContent> notifications){
        this.notifications = notifications
    }

    /**
     * Returns a NoticiationContent DTO per project containing the information for updated projects for the provided Date
     *
     * @param notifications List of DTOs containing the update information for one project each to send out
     * @since 1.0.0
     */
    @Override
    void createdNotifications(List<NotificationContent> notifications) {
        notifications.each {
            this.notifications.add(it)
        }
    }

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    @Override
    void failNotification(String notification) {
        //ToDo failNotification should be passed into dedicated logging file on executing server
        println(notification)
    }
}
