package life.qbic.business.subscription.fetch

/**
 * <p>Defines what information is forwarded from the {@link FetchSubscriber} use case</p>
 *
 * @since 1.0.0
 *
*/
interface FetchSubscriberInput {

    /**
     * Trigger sending notifications to a list of subscribers about changes in their subscriptions
     * @param date A date in the format yyyy-mm-dd
     * @since 1.0.0
     */
    void fetchSubscriber(String date)
}