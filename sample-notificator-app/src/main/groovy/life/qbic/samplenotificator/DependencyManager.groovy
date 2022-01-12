package life.qbic.samplenotificator

import groovy.util.logging.Log4j2
import life.qbic.business.notification.SendNotificationConnector
import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.refactor.*
import life.qbic.business.notification.unsubscription.UnsubscriptionLinkSupplier
import life.qbic.samplenotificator.cli.NotificatorCommandLineOptions
import life.qbic.samplenotificator.components.CreateNotificationController
import life.qbic.samplenotificator.components.email.html.HtmlEmailGenerator
import life.qbic.samplenotificator.components.email.html.HtmlEmailSender
import life.qbic.samplenotificator.components.email.support.SupportEmailSender
import life.qbic.samplenotificator.components.subscription.QBiCUnsubscriptionGenerator
import life.qbic.samplenotificator.datasource.database.DatabaseSession
import life.qbic.samplenotificator.datasource.notification.create.FetchProjectDbConnector
import life.qbic.samplenotificator.datasource.notification.create.FetchSubscriberDbConnector

import static java.util.Objects.requireNonNull

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
            String user = requireNonNull(properties.get("mysql.user"), "Mysql user missing.")
            String password = requireNonNull(properties.get("mysql.pass"), "Mysql password missing.")
            String host = requireNonNull(properties.get("mysql.host"), "Mysql host missing.")
            String port = requireNonNull(properties.get("mysql.port"), "Mysql port missing.")
            String sqlDatabase = requireNonNull(properties.get("mysql.db"), "Mysql database name missing.")

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

        UnsubscriptionLinkSupplier unsubscriptionLinkSupplier = setupUnsubscriptionLinkSupplier()

        EmailSender<NotificationEmail> emailSender = new HtmlEmailSender()
        FailureEmailSender failureEmailSender = new SupportEmailSender()
        EmailGenerator<NotificationEmail> notificationEmailGenerator = new HtmlEmailGenerator(unsubscriptionLinkSupplier)

        SendEmailInput sendEmail = new SendEmail(emailSender, failureEmailSender, notificationEmailGenerator)

        SendNotificationConnector sendNotificationConnector = new SendNotificationConnector(sendEmail)
        def createNotification = new CreateNotification(projectDbConnector, subscriberDbConnector, sendNotificationConnector)
        return new CreateNotificationController(createNotification)
    }

    private UnsubscriptionLinkSupplier setupUnsubscriptionLinkSupplier() {
        String subscriptionServicePassword = requireNonNull(properties.get("services.subscriptions.password"), "Subscription service password missing.")
        String subscriptionServiceTokengenerationEndpoint = requireNonNull(properties.get("services.tokengeneration.endpoint"), "Subscription service token endpoint missing.")
        String subscriptionServiceUrl = requireNonNull(properties.get("services.subscriptions.url"), "Subscription service url missing.")
        String subscriptionServiceUser = requireNonNull(properties.get("services.subscriptions.user"), "Subscription service user missing.")
        String unsubscriptionBaseUrl = requireNonNull(properties.get("portal.unsubscription.baseurl"), "Unsubscription endpoint is missing.")

        return new QBiCUnsubscriptionGenerator(unsubscriptionBaseUrl, subscriptionServiceUrl, subscriptionServiceTokengenerationEndpoint, subscriptionServiceUser, subscriptionServicePassword)
    }

    CreateNotificationController getCreateNotificationController() {
        return createNotificationController
    }
}
