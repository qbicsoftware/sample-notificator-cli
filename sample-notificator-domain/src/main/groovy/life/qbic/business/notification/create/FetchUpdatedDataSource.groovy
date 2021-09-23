package life.qbic.business.notification.create

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.datamodel.samples.Status

import java.time.LocalDate

/**
 * <p>Fetches the subscribers from the database</p>
 *
 * @since 1.0.0
 */
interface FetchUpdatedDataSource {

    /**
     * Retrieves a map of sample codes with statuses, which has been updated at a given day.
     * @param day The date of a day in yyyy-MM-dd
     * @return a map with updated sample codes and new statuses
     * @throws DatabaseQueryException
     * @since 1.0.0
     */
    Map<String, Status> getUpdatedSamplesForDay(LocalDate day) throws DatabaseQueryException
    
    /**
     * Retrieves a map of project codes with project titles
     * @return a map with project codes and their respective titles
     * @throws DatabaseQueryException
     * @since 1.0.0
     */
    Map<String, String> fetchProjectsWithTitles() throws DatabaseQueryException

}
