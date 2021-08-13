package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.cli.ToolExecutor
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
            log.info "Starting sample-notificator-cli service"
            final ToolExecutor executor = new ToolExecutor()
            executor.invoke(NotificatorApp.class, NotificatorCommandLineOptions.class, args)
        }catch(Exception exception){
            log.error "Could not run sample-notificator-cli"
            log.error exception.stackTrace.toString()
        }
    }
}