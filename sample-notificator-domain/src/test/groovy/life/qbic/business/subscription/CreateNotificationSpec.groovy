package life.qbic.business.subscription

import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.datamodel.samples.Status
import spock.lang.Specification
import java.time.LocalDate

/**
 * This test class tests for the {@link life.qbic.business.notification.create.CreateNotification} use case functionality
 *
 * @since: 1.0.0
 */
class CreateNotificationSpec extends Specification{

    def "Providing a valid Date will return notifications stored for that date"(){
        given: "The CreateNotification use case"
        FetchSubscriberDataSource ds = Stub(FetchSubscriberDataSource.class)
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(ds, output)

        and: "a dummy Subscriber list and a map containing Samples with their updated Sample status"
        Map<String, Status> updatedSamples = ["QMCDP007A3":Status.DATA_AVAILABLE,
                                              "QMCDP007A2":Status.SAMPLE_QC_FAIL,
                                              "QMCDP007A1":Status.DATA_AVAILABLE,
                                              "QMAAP007A3":Status.SAMPLE_QC_FAIL,
                                              "QMAAP018A2":Status.SAMPLE_RECEIVED,
                                              "QMAAP04525":Status.SAMPLE_QC_FAIL]

        Subscriber subscriber1 = new Subscriber("Awesome", "Customer", "awesome.customer@provider.com", updatedSamples)
        Subscriber subscriber2 = new Subscriber("Good", "Customer", "good.customer@provider.de", updatedSamples)
        List<Subscriber> subscribers = [subscriber1, subscriber2]

        and: "Datasource that returns the Subscriber list and the updated Sample Status"
        ds.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        ds.getSubscriberForProject(_ as String) >> subscribers

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "A map associating the notifications with the subscriber for the provided date is returned"
        1* output.createdNotifications(_ as Map<Subscriber, String>)
    }
}
