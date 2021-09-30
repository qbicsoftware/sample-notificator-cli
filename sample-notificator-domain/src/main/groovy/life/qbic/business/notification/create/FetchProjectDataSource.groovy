package life.qbic.business.notification.create

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.datamodel.samples.Status
import life.qbic.business.subscription.Subscriber

import java.time.LocalDate

/**
 * <p>Fetches the project from the database</p>
 *
 * @since 1.0.0
 */
interface FetchProjectDataSource {

    /**
     * Retrieves a map of project codes with project titles
     * @return a map with project codes and their respective titles
     * @throws DatabaseQueryException
     * @since 1.0.0
     */
    Map<String, String> fetchProjectsWithTitles() throws DatabaseQueryException

}
