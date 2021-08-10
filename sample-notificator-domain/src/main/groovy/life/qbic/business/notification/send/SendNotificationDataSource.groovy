package life.qbic.business.notification.send

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.subscription.Subscriber

import java.time.Instant

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since 1.0.0
 */
interface SendNotificationDataSource {

    /**
     * Retrieves a list of subscribers, that need to be notified.
     * Based on the current date (today) a list of subscribers that subscribed to an updated project will be returned.
     * @param today The date of today
     * @return a list of subscribers that need to be notified
     */
    List<Subscriber> getSubscribersForTodaysNotifications(Instant today) throws DatabaseQueryException
}