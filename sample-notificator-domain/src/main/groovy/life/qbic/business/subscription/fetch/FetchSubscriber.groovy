package life.qbic.business.subscription.fetch


import life.qbic.business.subscription.Subscriber
import life.qbic.datamodel.samples.Status

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

        try{
            //1. get todays notifications
            Map<String, Status> sampleToStatus = ds.getUpdatedSamplesForDay(localDate)
            // retrieve the project code
            List<Subscriber> subscribers = ds.getSubscriberIdForSamples(sampleToStatus)
            println subscribers
            output.fetchedSubscribers(subscribers)
        } catch(Exception e){
            throw new Exception(e.message)
        }
    }

}