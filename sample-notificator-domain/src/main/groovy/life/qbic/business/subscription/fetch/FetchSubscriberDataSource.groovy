package life.qbic.business.subscription.fetch

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.subscription.Subscriber
import life.qbic.datamodel.samples.Status

import java.time.LocalDate

/**
 * <p>Fetches the subscribers from the database</p>
 *
 * @since 1.0.0
 */
interface FetchSubscriberDataSource {

    /**
     * Retrieves a map of sample codes with statuses, which has been updated at a given day.
     * @param day The date of a day in yyyy-MM-dd
     * @return a mip with updated sample codes and new statuses
     */
    Map<String, Status> getNotificationsForDay(LocalDate day) throws DatabaseQueryException

    Map<Integer,List<String>> getSubscriberIdForSamples(Map<String,Status> sampleToStatus) throws DatabaseQueryException

    Subscriber getSubscriber(Integer subscriberId, Map<String,Status> sampleToStatus) throws DatabaseQueryException

}
