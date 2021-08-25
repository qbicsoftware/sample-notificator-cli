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
     * @return a map with updated sample codes and new statuses
     * @throws DatabaseQueryException
     * @since 1.0.0
     */
    Map<String, Status> getUpdatedSamplesForDay(LocalDate day) throws DatabaseQueryException

    /**
     * Returns the subscribers of a project
     * @param projectCode The code of a project
     * @return A list of subscribers for the project
     * @throws DatabaseQueryException
     * @since 1.0.0
     */
    List<Subscriber> getSubscriberForProject(String projectCode) throws DatabaseQueryException


}
