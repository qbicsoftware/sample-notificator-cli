package life.qbic.business.subscription.fetch

/**
 * <p>Interface to access and trigger the {@link FetchSubscriber} use case</p>
 *
 * @since 1.0.0
 *
*/
interface FetchSubscriberInput {

    /**
     * Trigger the retrieval of a list of subscribers whose subscriptions have been modified
     * @param date A date in the format yyyy-mm-dd
     * @since 1.0.0
     */
    void fetchSubscriber(String date)
}