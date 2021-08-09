package life.qbic.samplenotificator.datasource

import life.qbic.business.notification.send.SendNotificationDataSource

/**
 * <h1>A database connector to retrieve information for sending notifications</h1>
 *
 * <p>This connector reads the notification queue table and finds the associated people that subscribed for a notification</p>
 *
 * @since 1.0.0
 *
*/
class SendNotificationDbConnector implements SendNotificationDataSource{
}