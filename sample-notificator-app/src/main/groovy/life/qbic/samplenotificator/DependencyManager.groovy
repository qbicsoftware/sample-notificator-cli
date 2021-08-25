package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.subscription.Subscriber
import life.qbic.business.subscription.fetch.FetchSubscriber
import life.qbic.business.subscription.fetch.FetchSubscriberInput
import life.qbic.business.subscription.fetch.FetchSubscriberOutput
import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.subscription.Subscriber
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions
import life.qbic.samplenotificator.datasource.notification.create.FetchSubscriberDbConnector
import life.qbic.samplenotificator.components.CreateNotificationController
import life.qbic.samplenotificator.components.CreateNotificationPresenter
import life.qbic.samplenotificator.datasource.notification.create.CreateNotificationDbConnector
import life.qbic.samplenotificator.datasource.database.DatabaseSession

/**
 * <h1>Sets up the use cases</h1>
 *
 * @since 1.0.0
 *
*/
@Log4j2
class DependencyManager implements FetchSubscriberOutput{

    private Properties properties
    private FetchSubscriberInput fetchSubscriber
    private CreateNotificationPresenter createNotificationPresenter
    private CreateNotification createNotification
    private CreateNotificationController createNotificationController
    private Map<Subscriber, String> notificationPerSubscriber = new HashMap<Subscriber, String>()

    DependencyManager(NotificatorCommandLineOptions commandLineParameters){
        properties = getProperties(commandLineParameters.pathToConfig)
        initializeDependencies()
    }

    private void initializeDependencies(){
        setupDatabase()
        setupFetchSubscriber()
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

    private void setupFetchSubscriber(){
        FetchSubscriberDbConnector connector = new FetchSubscriberDbConnector(DatabaseSession.getInstance())
        FetchSubscriberOutput someOutput = this
        fetchSubscriber = new FetchSubscriber(connector,someOutput)
    }

    FetchSubscriberInput getFetchSubscriber() {
        return fetchSubscriber
    }

    //todo we might not want to have the dependency manager implement the output interface
    @Override
    void fetchedSubscribers(List<Subscriber> subscribers) {
        println "received the subscribers"
        println subscribers
    private void setupCreateNotification(){
        CreateNotificationDbConnector connector = new CreateNotificationDbConnector(DatabaseSession.getInstance())
        createNotificationPresenter = new CreateNotificationPresenter(notificationPerSubscriber)
        createNotification = new CreateNotification(connector, createNotificationPresenter)
        createNotificationController = new CreateNotificationController(createNotification)
    }

    @Override
    void failNotification(String notification) {
        log.error(notification)
    CreateNotificationController getCreateNotificationController() {
        return createNotificationController
    }

}