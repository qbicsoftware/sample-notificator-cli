package life.qbic.samplenotificator.datasource.notification.create

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.notification.create.FetchProjectDataSource
import life.qbic.samplenotificator.datasource.database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement

/**
 * <b>Fetches the project information form the database</b>
 *
 * @since 1.0.0
 */
class FetchProjectDbConnector implements FetchProjectDataSource {

    private ConnectionProvider connectionProvider

    FetchProjectDbConnector(ConnectionProvider connectionProvider){
        this.connectionProvider = connectionProvider
    }

    @Override
    Map<String, String> fetchProjectsWithTitles() {

        Map<String, String> projectsWithTitles = new HashMap<>()
        try{
            Connection connection = connectionProvider.connect()

            connection.withCloseable { Connection con ->
                String sqlQuery = "select openbis_project_identifier, short_title from projects"

                PreparedStatement preparedStatement = con.prepareStatement(sqlQuery)
                preparedStatement.execute()
                def resultSet = preparedStatement.getResultSet()

                while(resultSet.next()){
                    String identifier = resultSet.getString("openbis_project_identifier")
                    String code = identifier.substring(identifier.length() - 5)
                    String title = resultSet.getString("short_title")

                    projectsWithTitles.put(code, title)
                }
            }
        }catch(Exception exception){
            throw new DatabaseQueryException(exception.message)
        }

        return projectsWithTitles
    }
}