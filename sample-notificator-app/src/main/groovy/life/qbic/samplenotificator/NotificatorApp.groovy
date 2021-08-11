package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.notification.create.CreateNotification
import life.qbic.samplenotificator.datasource.CreateNotificationDbConnector
import life.qbic.samplenotificator.datasource.database.DatabaseSession

/**
 * <b>Entry point to the sample notificator application</b>
 *
 * <p>//TODO</p>
 *
 * @since 1.0.0
 */
@Log4j2
class NotificatorApp {
    public static void main(String[] args) {
        try {
            DependencyManager dependencyManager = new DependencyManager()
            dependencyManager.sendNotifications()
        }catch(Exception exception){
            log.error "Could not run sample-notificator-cli"
        }
    }

}
