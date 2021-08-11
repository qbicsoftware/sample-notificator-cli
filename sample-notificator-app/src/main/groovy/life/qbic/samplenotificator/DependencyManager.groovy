package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.samplenotificator.datasource.CreateNotificationDbConnector
import life.qbic.samplenotificator.datasource.database.DatabaseSession

/**
 * <h1>Sets up the use cases</h1>
 *
 * @since 1.0.0
 *
*/
@Log4j2
class DependencyManager {

    private Properties properties
    private CreateNotification createNotifcations

    DependencyManager(){
        //todo remove this after the commandline arguments were added
        properties = getProperties()
        initializeDependencies()
    }

    private void initializeDependencies(){
        setupDatabase()
        setupCreateNotification()
    }

    void sendNotifications(){
        createNotifcations.createNotifications()
    }

    private void setupDatabase(){
        try{
            String user = Objects.requireNonNull(properties.get("mysql.user"), "Mysql user missing.")
            String password = Objects.requireNonNull(properties.get("mysql.pass"), "Mysql password missing.")
            String host = Objects.requireNonNull(properties.get("mysql.host"), "Mysql host missing.")
            String port = Objects.requireNonNull(properties.get("mysql.port"), "Mysql port missing.")
            String sqlDatabase = Objects.requireNonNull(properties.get("mysql.db"), "Mysql database name missing.")

            DatabaseSession.init(user, password, host, port, sqlDatabase)
        }catch(Exception e){
            log.error "Could not setup connection to the database"
        }
    }

    private static Properties getProperties(){
        Properties properties = new Properties()
        //todo remove dependency on properties file and use commanline instead
        File propertiesFile = new File(NotificatorApp.class.getClassLoader().getResource('developer.properties').toURI())
        propertiesFile.withInputStream {
            properties.load(it)
        }
        return properties
    }

    private void setupCreateNotification(){
        CreateNotificationDbConnector connector = new CreateNotificationDbConnector(DatabaseSession.getInstance())
        CreateNotificationOutput someOutput = null //todo implement me
        createNotifcations = new CreateNotification(connector,someOutput)
    }

}