package life.qbic.business.notification.create

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.subscription.Subscriber

import java.time.LocalDate

/**
 * Provides methods to retrieve subscribers from a data-source
 *
 * @since 1.0.0
 */
interface CreateNotificationDataSource {

    /**
     * Retrieves a list of subscribers, that need to be notified.
     * Based on a provided date a list of subscribers that subscribed to an updated project will be returned.
     * @param date A date in the format yyyy-mm-dd
     * @return a list of subscribers that need to be notified
     */
    List<Subscriber> getSubscribersForNotificationsAt(LocalDate date) throws DatabaseQueryException
}
