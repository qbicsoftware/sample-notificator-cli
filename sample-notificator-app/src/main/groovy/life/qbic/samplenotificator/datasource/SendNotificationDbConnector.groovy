package life.qbic.samplenotificator.datasource

import life.qbic.business.notification.send.SendNotificationDataSource
import life.qbic.business.subscription.Subscriber
import life.qbic.datamodel.samples.Status
import life.qbic.samplenotificator.datasource.database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.Instant

/**
 * <h1>A database connector to retrieve information for sending notifications</h1>
 *
 * <p>This connector reads the notification queue table and finds the associated people that subscribed for a notification</p>
 *
 * @since 1.0.0
 *
*/
class SendNotificationDbConnector implements SendNotificationDataSource{
    private ConnectionProvider connectionProvider

    SendNotificationDbConnector(ConnectionProvider connectionProvider){
        this.connectionProvider = connectionProvider
    }


    @Override
    List<Subscriber> getSubscribersForTodaysNotifications(Instant today) {
        List<Subscriber> subscriberList = []
        //1. get todays notifications
        println getTodaysNotifications(today)
        // retrieve the project code
        //2. get the subscribers for the subscriptions

        return subscriberList
    }

    private Map<String, Status> getTodaysNotifications(Instant today){
        String sqlQuery = SELECT_NOTIFICATIONS + " WHERE year(arrival_time) = year(?) AND month(arrival_time) = month(?) AND day(arrival_time) = day(?)"
        Map<String, Status> foundNotifications = new HashMap<>()

        Connection connection = connectionProvider.connect()

        connection.withCloseable {
            PreparedStatement preparedStatement = it.prepareStatement(sqlQuery)
            //todo create a timestemp for the beginning of the current day
            Timestamp todaysTimeStamp = Timestamp.from(today)
            preparedStatement.setTimestamp(1, todaysTimeStamp)
            preparedStatement.setTimestamp(2, todaysTimeStamp)
            preparedStatement.setTimestamp(3, todaysTimeStamp)
            preparedStatement.execute()

            def resultSet = preparedStatement.getResultSet()
            while (resultSet.next()) {
                String sampleCode = resultSet.getString("sample_code")
                Status status = Status.valueOf(resultSet.getString("sample_status"))
                foundNotifications.put(sampleCode,status)
            }
        }
        return foundNotifications
    }

    private String SELECT_SUBSCRIBERS = "SELECT first_name, last_name, email FROM subscriber"
    private String SELECT_SUBSCRIPTIONS = "SELECT project_code, subscriber_id"
    private String SELECT_NOTIFICATIONS = "SELECT sample_code, sample_status FROM notification"

}