package life.qbic.samplenotificator

import life.qbic.portal.utils.ConfigurationManager
import life.qbic.portal.utils.ConfigurationManagerFactory
import life.qbic.samplenotificator.datasource.database.DatabaseSession

/**
 * <b>Entry point to the sample notificator application</b>
 *
 * <p>//TODO</p>
 *
 * @since 1.0.0
 */
class EntryPoint {
    public static void main(String[] args){
        ConfigurationManager configurationManager = ConfigurationManagerFactory.getInstance()

        String user = Objects.requireNonNull(configurationManager.getMysqlUser(), "Mysql user missing.")
        String password = Objects.requireNonNull(configurationManager.getMysqlPass(), "Mysql password missing.")
        String host = Objects.requireNonNull(configurationManager.getMysqlHost(), "Mysql host missing.")
        String port = Objects.requireNonNull(configurationManager.getMysqlPort(), "Mysql port missing.")
        String sqlDatabase = Objects.requireNonNull(configurationManager.getMysqlDB(), "Mysql database name missing.")

        DatabaseSession.init(user, password, host, port, sqlDatabase)
    }
}
