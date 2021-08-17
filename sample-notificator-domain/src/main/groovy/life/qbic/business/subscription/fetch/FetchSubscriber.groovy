package life.qbic.business.subscription.fetch


import life.qbic.business.subscription.Subscriber

import java.time.LocalDate

/**
 * <h1>Use case for fetching subscriber information from the database</h1>
 *
 * <p>Fetches the subscribers for whos subscriptions had updates on e.g a given date</p>
 *
 * @since 1.0.0
 *
*/
class FetchSubscriber implements FetchSubscriberInput{
    private final FetchSubscriberDataSource ds
    private final FetchSubscriberOutput output

    FetchSubscriber(FetchSubscriberDataSource ds, FetchSubscriberOutput output){
        this.ds = ds
        this.output = output
    }

    @Override
    void fetchSubscriber(String date) {
        LocalDate localDate = LocalDate.parse(date)
        List<Subscriber> subscribers = ds.getSubscribersForNotificationsAt(localDate)
        output.fetchedSubscribers(subscribers)
    }
}