package life.qbic.samplenotificator

import life.qbic.business.notification.send.SendNotification
import life.qbic.samplenotificator.datasource.SendNotificationDbConnector
import life.qbic.samplenotificator.datasource.database.DatabaseSession

/**
 * <b>Entry point to the sample notificator application</b>
 *
 * <p>//TODO</p>
 *
 * @since 1.0.0
 */
class NotificatorApp {
    public static void main(String[] args){
        //retrieve information from developer.properties
        Properties properties = getProperties()

        String user = Objects.requireNonNull(properties.get("mysql.user"), "Mysql user missing.")
        String password = Objects.requireNonNull(properties.get("mysql.pass"), "Mysql password missing.")
        String host = Objects.requireNonNull(properties.get("mysql.host"), "Mysql host missing.")
        String port = Objects.requireNonNull(properties.get("mysql.port"), "Mysql port missing.")
        String sqlDatabase = Objects.requireNonNull(properties.get("mysql.db"), "Mysql database name missing.")

        DatabaseSession.init(user, password, host, port, sqlDatabase)

        sendNotificationsToSubscriber()
    }

    private static Properties getProperties(){
        Properties properties = new Properties()
        File propertiesFile = new File(NotificatorApp.class.getClassLoader().getResource('developer.properties').toURI())
        propertiesFile.withInputStream {
            properties.load(it)
        }
        return properties
    }

    private static sendNotificationsToSubscriber(){
        SendNotificationDbConnector connector = new SendNotificationDbConnector(DatabaseSession.getInstance())
        SendNotification sendNotification = new SendNotification(connector)
        sendNotification.sendNotifications()
    }

}
