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
 * <h1>Sets up the use cases</h1>
 *
 * @since 1.0.0
 *
*/
@Log4j2
class DependencyManager {

    private Properties properties
    private CreateNotificationConnector createNotificationConnector
    private CreateNotification createNotification
    private CreateNotificationController createNotificationController

    DependencyManager(NotificatorCommandLineOptions commandLineParameters){
        properties = getProperties(commandLineParameters.pathToConfig)
        initializeDependencies()
    }

    private void initializeDependencies(){
        setupDatabase()
        setupCreateNotification()
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
        try {
            Properties properties = new Properties()
            File propertiesFile = new File(pathToConfig)
            propertiesFile.withInputStream {
                properties.load(it)
            }
            return properties
        } catch (Exception e) {
            log.error("Could not get properties from provided property file")
            log.error(e.message)
        }
    }

    private void setupCreateNotification() {
        try {
            FetchSubscriberDbConnector subscriberDbConnector = new FetchSubscriberDbConnector(DatabaseSession.getInstance())
            FetchProjectDbConnector projectDbConnector = new FetchProjectDbConnector(DatabaseSession.getInstance())
            EmailGenerator emailGenerator = new EmailGenerator()
            createNotificationConnector = new CreateNotificationConnector(emailGenerator)
            createNotification = new CreateNotification(projectDbConnector, subscriberDbConnector, createNotificationConnector)
            createNotificationController = new CreateNotificationController(createNotification)
        } catch (Exception e) {
            log.error("Could not setup Create Notification Use Case")
            log.error(e.message)
        }
    }

    CreateNotificationController getCreateNotificationController() {
        return createNotificationController
    }
}