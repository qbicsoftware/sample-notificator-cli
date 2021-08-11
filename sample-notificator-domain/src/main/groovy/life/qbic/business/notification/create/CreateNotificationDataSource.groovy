package life.qbic.business.notification.create

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.subscription.Subscriber

import java.time.LocalDate

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since 1.0.0
 */
interface CreateNotificationDataSource {

    /**
     * Retrieves a list of subscribers, that need to be notified.
     * Based on the current date (today) a list of subscribers that subscribed to an updated project will be returned.
     * @param today The date of today
     * @return a list of subscribers that need to be notified
     */
    List<Subscriber> getSubscribersForTodaysNotifications(LocalDate today) throws DatabaseQueryException
}