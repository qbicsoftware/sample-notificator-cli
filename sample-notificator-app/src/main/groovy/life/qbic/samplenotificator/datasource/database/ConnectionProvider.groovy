package life.qbic.samplenotificator.datasource.database

import java.sql.Connection
import java.sql.SQLException

/**
 * <h1><short description></h1>
 *
 * <p><detailed description></p>
 *
 * @since <versiontag>
 *
*/
interface ConnectionProvider {
    Connection connect() throws SQLException
}