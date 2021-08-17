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
     */
    void fetchedSubscribers(List<Subscriber> subscribers)

}