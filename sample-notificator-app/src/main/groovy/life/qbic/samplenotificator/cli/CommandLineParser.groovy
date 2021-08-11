package life.qbic.samplenotificator.cli

import picocli.CommandLine

/**
 * <h1><short description></h1>
 *
 * <p><detailed description></p>
 *
 * @since <versiontag>
 *
*/
class CommandLineParser {

    public static NotificatorCommandLineOptions parseAndVerifyCommandLineParameters(String[] args)
            throws IOException {
        if (args.length == 0) {
            CommandLine.usage(new NotificatorCommandLineOptions(), System.out);
            System.exit(0);
        }


        NotificatorCommandLineOptions commandLineParameters = new NotificatorCommandLineOptions();
        new CommandLine(commandLineParameters).parse(args) //todo fix me

        /**
        if (commandLineParameters.helpRequested) {
            CommandLine.usage(new NotificatorCommandLineOptions(), System.out);
            System.exit(0);
        }

        if ((commandLineParameters.ids == null || commandLineParameters.ids.isEmpty())
                && commandLineParameters.filePath == null) {
            System.out.println(
                    "You have to provide one ID as command line argument or a file containing IDs.");
            System.exit(1);
        } else if ((commandLineParameters.ids != null) && (commandLineParameters.filePath != null)) {
            System.out.println(
                    "Arguments --identifier and --file are mutually exclusive, please provide only one.");
            System.exit(1);
        } else if (commandLineParameters.filePath != null) {
            commandLineParameters.ids =
                    IdentifierParser.readProvidedIdentifiers(commandLineParameters.filePath.toFile());
        }
         */

        return commandLineParameters
    }
}