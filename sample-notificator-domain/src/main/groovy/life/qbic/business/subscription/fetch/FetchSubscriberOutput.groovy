package life.qbic.business.subscription.fetch

import life.qbic.business.subscription.Subscriber

/**
 * <p>Defines what information is forwarded from the {@link FetchSubscriber} use case</p>
 *
 * @since 1.0.0
 *
*/
interface FetchSubscriberOutput {

    /**
     * Transfers the generated list of subscribers to the implementing class
     * @param subscribers the retrieved list of subscribers with modified subscriptions
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