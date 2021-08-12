package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.samplenotificator.cli.CommandLineParser
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions

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
            NotificatorCommandLineOptions commandLineParameters =
                    CommandLineParser.parseAndVerifyCommandLineParameters(args)

            DependencyManager dependencyManager = new DependencyManager(commandLineParameters)
            dependencyManager.run()
        }catch(Exception exception){
            log.error "Could not run sample-notificator-cli"
        }
    }

}
