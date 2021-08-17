package life.qbic.samplenotificator.datasource.notification.create

import groovy.util.logging.Log4j2
import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.business.subscription.Subscriber
import life.qbic.datamodel.samples.Status
import life.qbic.samplenotificator.datasource.database.ConnectionProvider

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
    List<Subscriber> getSubscribersForNotificationsAt(LocalDate today) {
        List<Subscriber> subscriberList = []
        try{
            //1. get todays notifications
            Map<String, Status> sampleToStatus = getNotificationsForDay(today)
            // retrieve the project code
            Map<Integer,List<String>> subscriberIdsToSamples = getSubscriberIdForSamples(sampleToStatus)
            //2. get the subscribers for the subscriptions
            subscriberIdsToSamples.each { subscriberMap ->
                Map<String,Status> allSamplesToStatus = sampleToStatus.findAll {it.key in subscriberMap.value}
                subscriberList << getSubscriber(subscriberMap.key,allSamplesToStatus)
            }
        }catch(Exception e){
            log.error e.message
            log.error(e.stackTrace.join("\n"))

            throw new DatabaseQueryException(e.message)
        }

        return subscriberList
    }

    private Map<String, Status> getNotificationsForDay(LocalDate day){
        Instant startOfDay = day.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        Instant endOfDay = day.plusDays(1).atStartOfDay().minusNanos(1).atZone(ZoneId.systemDefault()).toInstant()
        String sqlQuery = SELECT_NOTIFICATIONS + " WHERE arrival_time BETWEEN ? AND ?"
        Map<String, Status> foundNotifications = new HashMap<>()

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
        return foundNotifications
    }

    private Map<Integer,List<String>> getSubscriberIdForSamples(Map<String,Status> sampleToStatus){

        Connection connection = connectionProvider.connect()
        Map<Integer,List<String>> userToString = new HashMap<>()

        connection.withCloseable { Connection con ->
            sampleToStatus.each {
                //get all subscriptions for a person, id to list of project codes
                String sqlQuery = SELECT_SUBSCRIPTIONS + " WHERE ? LIKE CONCAT(project_code ,'%') "

                PreparedStatement preparedStatement = con.prepareStatement(sqlQuery)
                preparedStatement.setString(1,it.key)
                preparedStatement.execute()
                def resultSet = preparedStatement.getResultSet()

                while(resultSet.next()){
                    int user = resultSet.getInt("subscriber_id")

                    if(userToString.containsKey(user)){
                        userToString.get(user) << it.key
                    }
                    else{
                        userToString.put(user,[it.key])
                    }
                }
            }
        }

        return userToString
    }

    private Subscriber getSubscriber(Integer subscriberId, Map<String,Status> sampleToStatus){
        Connection connection = connectionProvider.connect()
        Subscriber subscriber = null

        connection.withCloseable {
            String sqlStatement = SELECT_SUBSCRIBERS + " WHERE id = ?"

            PreparedStatement preparedStatement = it.prepareStatement(sqlStatement)
            preparedStatement.setInt(1,subscriberId)
            preparedStatement.execute()
            def resultSet = preparedStatement.getResultSet()

            while(resultSet.next()) {
                String firstName = resultSet.getString("first_name")
                String lastName = resultSet.getString("last_name")
                String email = resultSet.getString("email")
                subscriber = new Subscriber(firstName,lastName,email,sampleToStatus)
            }
        }
        return subscriber
    }


    private String SELECT_SUBSCRIBERS = "SELECT first_name, last_name, email FROM subscriber"
    private String SELECT_SUBSCRIPTIONS = "SELECT project_code, subscriber_id FROM subscription"
    private String SELECT_NOTIFICATIONS = "SELECT sample_code, sample_status FROM notification"

}
