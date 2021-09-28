package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.NotificationContent
import life.qbic.business.subscription.Subscriber
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions
import life.qbic.samplenotificator.components.EmailGenerator
import life.qbic.samplenotificator.datasource.notification.create.FetchSubscriberDbConnector
import life.qbic.samplenotificator.components.CreateNotificationController
import life.qbic.samplenotificator.components.CreateNotificationPresenter
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
    private CreateNotificationPresenter createNotificationPresenter
    private CreateNotification createNotification
    private CreateNotificationController createNotificationController
    private Map<Subscriber, String> notificationPerSubscriber = new HashMap<Subscriber, String>()
    private EmailGenerator emailGenerator

    DependencyManager(NotificatorCommandLineOptions commandLineParameters){
        properties = getProperties(commandLineParameters.pathToConfig)
        initializeDependencies()
    }

    private void initializeDependencies(){
        setupDatabase()
        setupCreateNotification()
        setupSendEmail()
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

    private static Properties getProperties(String pathToConfig){
        Properties properties = new Properties()
        File propertiesFile = new File(pathToConfig)
        propertiesFile.withInputStream {
            properties.load(it)
        }
        return properties
    }

    private void setupCreateNotification(){
        FetchSubscriberDbConnector connector = new FetchSubscriberDbConnector(DatabaseSession.getInstance())
        createNotificationPresenter = new CreateNotificationPresenter(notificationPerSubscriber)
        createNotification = new CreateNotification(connector, createNotificationPresenter)
        createNotificationController = new CreateNotificationController(createNotification)
    }

    private void setupSendEmail(){
        //ToDo Remove after testing since this will be provided by the use case
        NotificationContent notificationContent = new NotificationContent.Builder("Jo", "My_Dude", "steffen.greiner@uni-tuebingen.de", "coolProject", "NICE1", 2, 1000).build()
        emailGenerator = new EmailGenerator(notificationContent)
    }

    CreateNotificationController getCreateNotificationController() {
        return createNotificationController
    }

    EmailGenerator getEmailGenerator(){
        return emailGenerator
    }


}