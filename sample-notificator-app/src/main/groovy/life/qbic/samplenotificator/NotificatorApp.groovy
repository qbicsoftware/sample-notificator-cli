package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.cli.QBiCTool
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions
import life.qbic.samplenotificator.components.CreateNotificationController
import life.qbic.samplenotificator.components.EmailGenerator

/**
 * <b>Builds up the app content and starts it</b>
 *
 * @since 1.0.0
 */
@Log4j2
class NotificatorApp extends QBiCTool<NotificatorCommandLineOptions>{

    NotificatorApp(NotificatorCommandLineOptions command) {
        super(command)
    }

    @Override
    void execute() {
        NotificatorCommandLineOptions commandLineParameters = super.command as NotificatorCommandLineOptions

        DependencyManager dependencyManager = new DependencyManager(commandLineParameters)
        CreateNotificationController createNotificationController = dependencyManager.getCreateNotificationController()
        createNotificationController.createNotification(commandLineParameters.date)
        EmailGenerator emailGenerator = dependencyManager.getEmailGenerator()
        emailGenerator.initializeEmailSubmission()
    }
}
