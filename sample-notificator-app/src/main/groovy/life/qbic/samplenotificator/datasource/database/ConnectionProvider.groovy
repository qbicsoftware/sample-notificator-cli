package life.qbic.samplenotificator.datasource.database

import java.sql.Connection
import java.sql.SQLException

/**
 * <p>A connection provider needs to provide a SQL connection. This interface serves a separation between the building up the connect
 * to any database and the actual connection itself.</p>
 *
 * @since 1.0.0
 *
*/
interface ConnectionProvider {
    Connection connect() throws SQLException
}