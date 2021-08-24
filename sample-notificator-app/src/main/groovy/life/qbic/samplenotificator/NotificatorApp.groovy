package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.notification.create.CreateNotificationInput
import life.qbic.business.subscription.fetch.FetchSubscriberInput
import life.qbic.cli.QBiCTool
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions

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
        FetchSubscriberInput fetchSubscriberInput = dependencyManager.getFetchSubscriber()
        fetchSubscriberInput.fetchSubscriber(commandLineParameters.date)
    }
}
