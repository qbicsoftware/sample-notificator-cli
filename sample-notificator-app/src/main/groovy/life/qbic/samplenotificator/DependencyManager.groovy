package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.notification.create.CreateNotification
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions
import life.qbic.samplenotificator.components.CreateNotificationConnector
import life.qbic.samplenotificator.components.CreateNotificationController
import life.qbic.samplenotificator.components.EmailGenerator
import life.qbic.samplenotificator.datasource.database.DatabaseSession
import life.qbic.samplenotificator.datasource.notification.create.FetchProjectDbConnector
import life.qbic.samplenotificator.datasource.notification.create.FetchSubscriberDbConnector

/**
 * <b>Sets up the use cases</b>
 *
 * @since 1.0.0
 *
*/
@Log4j2
class DependencyManager {

    private Properties properties
    private CreateNotificationController createNotificationController

    DependencyManager(NotificatorCommandLineOptions commandLineParameters){
        properties = getProperties(commandLineParameters.pathToConfig)
        initializeDependencies()
    }

    private void initializeDependencies() {
        setupDatabase()
        createNotificationController = setupCreateNotification()
    }

    private void setupDatabase(){
        try{
            String user = Objects.requireNonNull(properties.get("mysql.user"), "Mysql user missing.")
            String password = Objects.requireNonNull(properties.get("mysql.pass"), "Mysql password missing.")
            String host = Objects.requireNonNull(properties.get("mysql.host"), "Mysql host missing.")
            String port = Objects.requireNonNull(properties.get("mysql.port"), "Mysql port missing.")
            String sqlDatabase = Objects.requireNonNull(properties.get("mysql.db"), "Mysql database name missing.")

            DatabaseSession.init(user, password, host, port, sqlDatabase)
        } catch (Exception e) {
            log.error "Could not setup connection to the database"
            log.error(e.message)
        }
    }

    private static Properties getProperties(String pathToConfig) {
        Properties properties = new Properties()
        File propertiesFile = new File(pathToConfig)
        propertiesFile.withInputStream {
            properties.load(it)
        }
        return properties
    }

    private static CreateNotificationController setupCreateNotification() {
        FetchSubscriberDbConnector subscriberDbConnector = new FetchSubscriberDbConnector(DatabaseSession.getInstance())
        FetchProjectDbConnector projectDbConnector = new FetchProjectDbConnector(DatabaseSession.getInstance())
        EmailGenerator emailGenerator = new EmailGenerator()
        def createNotificationConnector = new CreateNotificationConnector(emailGenerator)
        def createNotification = new CreateNotification(projectDbConnector, subscriberDbConnector, createNotificationConnector)
        return new CreateNotificationController(createNotification)

    }

    CreateNotificationController getCreateNotificationController() {
        return createNotificationController
    }
}
