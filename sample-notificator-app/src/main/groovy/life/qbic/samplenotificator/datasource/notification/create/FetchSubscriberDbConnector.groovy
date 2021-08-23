package life.qbic.samplenotificator.datasource.notification.create

import groovy.util.logging.Log4j2
import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.business.subscription.Subscriber
import life.qbic.datamodel.samples.Status
import life.qbic.samplenotificator.datasource.database.ConnectionProvider
import life.qbic.samplenotificator.datasource.database.DatabaseSession

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * <h1>A database connector to retrieve information for sending notifications</h1>
 *
 * <p>This connector reads the notification queue table and finds the associated people that subscribed for a notification</p>
 *
 * @since 1.0.0
 *
*/
@Log4j2
class FetchSubscriberDbConnector implements FetchSubscriberDataSource{
    private ConnectionProvider connectionProvider

    FetchSubscriberDbConnector(ConnectionProvider connectionProvider){
        this.connectionProvider = connectionProvider
    }

    @Override
    Map<String, Status> getUpdatedSamplesForDay(LocalDate day){
        Instant startOfDay = day.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        Instant endOfDay = day.plusDays(1).atStartOfDay().minusNanos(1).atZone(ZoneId.systemDefault()).toInstant()
        String sqlQuery = SELECT_NOTIFICATIONS + " WHERE arrival_time BETWEEN ? AND ?"
        Map<String, Status> foundNotifications = new HashMap<>()

        try{
            Connection connection = connectionProvider.connect()

            connection.withCloseable {
                PreparedStatement preparedStatement = it.prepareStatement(sqlQuery)
                preparedStatement.setTimestamp(1, Timestamp.from(startOfDay))
                preparedStatement.setTimestamp(2, Timestamp.from(endOfDay))
                preparedStatement.execute()

                def resultSet = preparedStatement.getResultSet()
                while (resultSet.next()) {
                    String sampleCode = resultSet.getString("sample_code")
                    Status status = Status.valueOf(resultSet.getString("sample_status"))
                    foundNotifications.put(sampleCode,status)
                }
            }
        }catch(Exception exception){
            throw new DatabaseQueryException(exception.message)
        }
        return foundNotifications
    }

    @Override
    List<Subscriber> getSubscriberForProject(String projectCode){

        List<Subscriber> subscribers = []
        try{
            Connection connection = connectionProvider.connect()

            connection.withCloseable { Connection con ->
                    //get all subscriptions for a person, id to list of project codes
                    String sqlQuery = JOIN_SUBSCRIBERS_SUBSCRIPTIONS + " WHERE ? = project_code"

                    PreparedStatement preparedStatement = con.prepareStatement(sqlQuery)
                    preparedStatement.setString(1,projectCode)
                    preparedStatement.execute()
                    def resultSet = preparedStatement.getResultSet()

                    while(resultSet.next()){
                        String firstName = resultSet.getString("first_name")
                        String lastName = resultSet.getString("last_name")
                        String email = resultSet.getString("email")

                        subscribers << new Subscriber(firstName,lastName,email)
                    }
            }
        }catch(Exception exception){
            throw new DatabaseQueryException(exception.message)
        }

        return subscribers
    }

    private static String JOIN_SUBSCRIBERS_SUBSCRIPTIONS = "SELECT * FROM subscription LEFT JOIN subscriber ON subscription.subscriber_id = subscriber.id"
    private static String SELECT_NOTIFICATIONS = "SELECT sample_code, sample_status FROM notification"

}
