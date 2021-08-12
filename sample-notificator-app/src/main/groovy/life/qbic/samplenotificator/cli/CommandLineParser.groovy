package life.qbic.samplenotificator.cli

import picocli.CommandLine

/**
 * <h1>Parses the command line</h1>
 *
 * <p>A parser for processing the commandline input.</p>
 *
 * @since 1.0.0
 *
*/
class CommandLineParser {

    public static NotificatorCommandLineOptions parseAndVerifyCommandLineParameters(String[] args)
            throws IOException {
        if (args.length == 0) {
            CommandLine.usage(new NotificatorCommandLineOptions(), System.out)
            System.exit(0)
        }
        NotificatorCommandLineOptions commandLineParameters = new NotificatorCommandLineOptions()
        new CommandLine(commandLineParameters).parseArgs(args)


        if (commandLineParameters.helpRequested) {
            CommandLine.usage(new NotificatorCommandLineOptions(), System.out);
            System.exit(0);
        }

        if (commandLineParameters.pathToConfig == null || commandLineParameters.pathToConfig.isEmpty()) {
            System.out.println(
                    "You have to provide a config file.")
            System.exit(1)
        }

        return commandLineParameters
    }
}