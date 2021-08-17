package life.qbic.business.subscription.fetch

import life.qbic.business.exception.DatabaseQueryException
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

        List<Subscriber> subscriberList = []
        try{
            //1. get todays notifications
            Map<String, Status> sampleToStatus = ds.getNotificationsForDay(localDate)
            // retrieve the project code
            Map<Integer,List<String>> subscriberIdsToSamples = ds.getSubscriberIdForSamples(sampleToStatus)
            //2. get the subscribers for the subscriptions
            subscriberIdsToSamples.each { subscriberMap ->
                Map<String,Status> allSamplesToStatus = sampleToStatus.findAll {it.key in subscriberMap.value}
                subscriberList << ds.getSubscriber(subscriberMap.key,allSamplesToStatus)
            }
        }catch(Exception e){
            throw new Exception(e.message)
        }

        println subscriberList

        output.fetchedSubscribers(subscriberList)
    }
}