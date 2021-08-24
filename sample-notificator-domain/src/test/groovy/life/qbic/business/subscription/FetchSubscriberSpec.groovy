package life.qbic.business.subscription

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.subscription.fetch.FetchSubscriber
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.business.subscription.fetch.FetchSubscriberOutput
import life.qbic.datamodel.samples.Status
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since 1.0.0
 */
class FetchSubscriberSpec extends Specification {
    @Shared
    Subscriber subscriber1
    @Shared
    Subscriber subscriber2
    @Shared
    Subscriber subscriber3
    @Shared
    Subscriber subscriber1_without
    @Shared
    Subscriber subscriber2_without
    @Shared
    Subscriber subscriber3_without

    @Shared
    Map<String, Status> updatedSamples

    def setup(){

        updatedSamples = ["QMCDP007A3":Status.DATA_AVAILABLE,
                          "QMCDP007A2":Status.DATA_AVAILABLE,
                          "QMCDP007A1":Status.DATA_AVAILABLE,
                          "QMAAP007A3":Status.SAMPLE_RECEIVED,
                          "QMAAP018A2":Status.SAMPLE_RECEIVED,
                          "QMAAP04525":Status.SAMPLE_RECEIVED]
        subscriber1 = new Subscriber("John","Doe","john.doe@gmail.de", updatedSamples)
        subscriber2 = new Subscriber("Janet","Doe","janet.doe@gmail.de",updatedSamples)
        subscriber3 = new Subscriber("Janet","Doe","janet.doe@gmail.de",updatedSamples)
        subscriber1_without = new Subscriber("John","Doe","john.doe@gmail.de")
        subscriber2_without = new Subscriber("Janet","Doe","janet.doe@gmail.de")
        subscriber3_without = new Subscriber("Janet","Doe","janet.doe@gmail.de")

    }

    def "FetchSubscriber fetches all unique subscribers for updated projects"(){
        given:
        FetchSubscriberOutput output = Mock()
        FetchSubscriberDataSource ds = Stub(FetchSubscriberDataSource.class)
        FetchSubscriber fetchSubscriber = new FetchSubscriber(ds,output)

        ds.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        ds.getSubscriberForProject("QMCD") >> [subscriber1_without,subscriber2_without]
        ds.getSubscriberForProject("QMAA") >> [subscriber1_without,subscriber3_without]

        when:
        fetchSubscriber.fetchSubscriber("2021-08-17")

        then:
        1 * output.fetchedSubscribers([subscriber1,subscriber2])
    }

    def "FetchSubscriber returns an empty list if now samples where updated"(){
        given:
        FetchSubscriberOutput output = Mock()
        FetchSubscriberDataSource ds = Stub(FetchSubscriberDataSource.class)
        FetchSubscriber fetchSubscriber = new FetchSubscriber(ds,output)

        ds.getUpdatedSamplesForDay(_ as LocalDate) >> new HashMap<String, Status>()
        ds.getSubscriberForProject("QMCD") >> []
        ds.getSubscriberForProject("QMAA") >> []

        when:
        fetchSubscriber.fetchSubscriber("2021-08-17")

        then:
        1 * output.fetchedSubscribers([])
    }

    def "FetchSubscriber returns an empty list if no subscribers where found"(){
        given:
        FetchSubscriberOutput output = Mock()
        FetchSubscriberDataSource ds = Stub(FetchSubscriberDataSource.class)
        FetchSubscriber fetchSubscriber = new FetchSubscriber(ds,output)

        ds.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        ds.getSubscriberForProject("QMCD") >> []
        ds.getSubscriberForProject("QMAA") >> []

        when:
        fetchSubscriber.fetchSubscriber("2021-08-17")

        then:
        1 * output.fetchedSubscribers([])
    }

    def "No error is thrown when getting the subscribers for project fails"(){
        given:
        FetchSubscriberOutput output = Mock()
        FetchSubscriberDataSource ds = Stub(FetchSubscriberDataSource.class)
        FetchSubscriber fetchSubscriber = new FetchSubscriber(ds,output)

        ds.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        ds.getSubscriberForProject("QMCD") >> {throw new DatabaseQueryException("An error occurred")}

        when:
        fetchSubscriber.fetchSubscriber("2021-08-17")

        then:
        noExceptionThrown()
        1*output.failNotification("An error occurred")
    }

    def "No error is thrown when getting the updated samples failed"(){
        given:
        FetchSubscriberOutput output = Mock()
        FetchSubscriberDataSource ds = Stub(FetchSubscriberDataSource.class)
        FetchSubscriber fetchSubscriber = new FetchSubscriber(ds,output)

        ds.getUpdatedSamplesForDay(_ as LocalDate) >> {throw new DatabaseQueryException("An error occurred")}
        ds.getSubscriberForProject("QMCD") >> [subscriber1_without,subscriber2_without,subscriber3_without]

        when:
        fetchSubscriber.fetchSubscriber("2021-08-17")

        then:
        noExceptionThrown()
        1*output.failNotification("An error occurred")
    }
}
