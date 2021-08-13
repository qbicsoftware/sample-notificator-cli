package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.samplenotificator.cli.CommandLineParser
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions

/**
 * <h1>Entry point for SampleNotificator service</h1>
 *
 * <p>Starts the service</p>
 *
 * @since 1.0.0
 *
*/
@Log4j2
class SampleNotificatorEntryPoint {

    public static void main(String[] args) {
        try {
            NotificatorCommandLineOptions commandLineParameters =
                    CommandLineParser.parseAndVerifyCommandLineParameters(args)

            NotificatorApp app = new NotificatorApp(commandLineParameters)
            app.run()
        }catch(Exception exception){
            log.error "Could not run sample-notificator-cli"
        }
    }
}