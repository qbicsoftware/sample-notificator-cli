package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.notification.create.CreateNotificationInput
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions

/**
 * <b>Builds up the app content and starts it</b>
 *
 * @since 1.0.0
 */
@Log4j2
class NotificatorApp implements Runnable{
    private NotificatorCommandLineOptions commandLineParameters

    NotificatorApp (NotificatorCommandLineOptions commandLineParameters){
        this.commandLineParameters = commandLineParameters
    }

    @Override
    void run() {
        DependencyManager dependencyManager = new DependencyManager(commandLineParameters)
        CreateNotificationInput createNotification = dependencyManager.getCreateNotification()
        createNotification.createNotifications()
    }
}
