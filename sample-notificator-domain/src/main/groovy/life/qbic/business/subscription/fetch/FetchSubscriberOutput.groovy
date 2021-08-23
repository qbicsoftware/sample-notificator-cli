package life.qbic.business.subscription.fetch

import life.qbic.business.subscription.Subscriber

/**
 * <p>Interface to access and trigger the {@link FetchSubscriber} use case</p>
 *
 * @since 1.0.0
 *
*/
interface FetchSubscriberOutput {

    /**
     * Trigger sending notifications to a list of subscribers about changes in their subscriptions
     * @param date A date in the format yyyy-mm-dd
     * @since 1.0.0
     */
    void fetchedSubscribers(List<Subscriber> subscribers)

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    void failNotification(String notification)

}