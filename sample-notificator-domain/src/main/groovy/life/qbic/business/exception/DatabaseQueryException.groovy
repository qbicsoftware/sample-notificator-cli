package life.qbic.business.exception
/**
 * An exception for errors in database querying
 *
 * This class should be thrown when an error occurs during a database query
 *
 * @since: 1.0.0
 *
 */
class DatabaseQueryException extends RuntimeException {

    DatabaseQueryException() {
    }

    DatabaseQueryException(String message) {
        super(message)
    }

    DatabaseQueryException(String message, Throwable cause) {
        super(message, cause)
    }
}