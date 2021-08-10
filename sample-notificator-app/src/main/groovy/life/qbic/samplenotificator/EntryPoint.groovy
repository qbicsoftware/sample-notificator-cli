package life.qbic.samplenotificator

import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory
import life.qbic.samplenotificator.datasource.SendNotificationDbConnector
import life.qbic.samplenotificator.datasource.database.DatabaseSession

import java.time.Instant

/**
 * <b>Entry point to the sample notificator application</b>
 *
 * <p>//TODO</p>
 *
 * @since 1.0.0
 */
class EntryPoint {
    public static void main(String[] args){

        Properties properties = new Properties()
        File propertiesFile = new File(EntryPoint.class.getClassLoader().getResource('developer.properties').toURI())
        propertiesFile.withInputStream {
            properties.load(it)
        }

        String user = Objects.requireNonNull(properties.get("mysql.user"), "Mysql user missing.")
        String password = Objects.requireNonNull(properties.get("mysql.pass"), "Mysql password missing.")
        String host = Objects.requireNonNull(properties.get("mysql.host"), "Mysql host missing.")
        String port = Objects.requireNonNull(properties.get("mysql.port"), "Mysql port missing.")
        String sqlDatabase = Objects.requireNonNull(properties.get("mysql.db"), "Mysql database name missing.")

        DatabaseSession.init(user, password, host, port, sqlDatabase)

        SendNotificationDbConnector connector = new SendNotificationDbConnector(DatabaseSession.getInstance())
        connector.getSubscribersForTodaysNotifications(Instant.now())
    }
}
