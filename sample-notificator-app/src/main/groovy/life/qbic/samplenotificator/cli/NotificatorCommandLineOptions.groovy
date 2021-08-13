package life.qbic.samplenotificator.cli

import picocli.CommandLine

/**
 * <h1>Commandline options of the Notificator service</h1>
 *
 * <p>Contains the options for controlling the CLI</p>
 *
 * @since 1.0.0
 *
*/
@CommandLine.Command(
        name = "SampleNotificator",
        description = "A service to send notifications to subscribers to inform them about changes within their projects")
class NotificatorCommandLineOptions {

    @CommandLine.Option(
            names = ["-d", "--date"],
            required = true,
            description = "Date of the day, for which status update notifications should be send. Required format: yyyy-mm-dd")
    public String date

    @CommandLine.Option(
            names = ["-c", "--config"],
            required = true,
            description = "Path to a config file")
    public String pathToConfig

    @CommandLine.Option(
            names = ["-h", "--help"],
            usageHelp = true,
            description = "display a help message")
    public boolean helpRequested = false
}