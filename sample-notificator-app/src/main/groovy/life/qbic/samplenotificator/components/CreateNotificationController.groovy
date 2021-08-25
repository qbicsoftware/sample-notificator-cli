package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.CreateNotificationInput

/**
 * Controller class adapter from command line input into use case input interface
 *
 * This class translates the information that was received from the command line into method calls for the use case
 *
 * @since: 1.0.0
 *
 */

class CreateNotificationController {

    private final CreateNotificationInput createNotificationInput

    CreateNotificationController(CreateNotificationInput createNotificationInput) {
        this.createNotificationInput = createNotificationInput
    }

    /**
     * This method creates a Notification based on the information provided in the commandLine
     *
     * @param date A date in the format yyyy-mm-dd
     */
    void createNotification(String date) {
        this.createNotificationInput.createNotifications(date)
    }

}
