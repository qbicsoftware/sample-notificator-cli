package life.qbic.business.subscription

import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.business.notification.create.FetchProjectDataSource
import life.qbic.business.notification.create.NotificationContent
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

    def "If an exception is thrown in the notification creation process because no subscribers are found, a fail notification is returned"(){
        given: "The CreateNotification use case"
        FetchProjectDataSource projectDataSource = Stub()
        FetchSubscriberDataSource fetchSubscriberDataSource = Stub()
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(projectDataSource,fetchSubscriberDataSource, output)

        and: "a dummy Subscriber list and a map containing Samples with their updated Sample status"
        Map<String, Status> updatedSamples = ["QMCDP007A3":Status.DATA_AVAILABLE,
                                              "QMCDP007A2":Status.SAMPLE_QC_FAIL,
                                              "QMCDP007A1":Status.DATA_AVAILABLE,
                                              "QMAAP007A3":Status.SAMPLE_QC_FAIL,
                                              "QMAAP018A2":Status.SAMPLE_RECEIVED,
                                              "QMAAP04525":Status.SAMPLE_QC_FAIL]

        Map<String,String> projectsWithTitles = ["QMCDP": "first project",
                                                 "QMAAP": ""]

        and: "Datasource that returns various information needed, but throws an exception when getting subscribers"
        fetchSubscriberDataSource.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        fetchSubscriberDataSource.getSubscriberForProject(_ as String) >> {throw new Exception("An error when fetching subscribers")}
        projectDataSource.fetchProjectsWithTitles() >> projectsWithTitles

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "A fail notification is returned"
        0* output.createdNotifications(_ as List<NotificationContent>)
        1* output.failNotification(_ as String)
    }

    def "When an empty list of subscribers is returned from the data source, no notification is sent out"() {
        given: "The CreateNotification use case"
        FetchProjectDataSource projectDataSource = Stub()
        FetchSubscriberDataSource fetchSubscriberDataSource = Stub()
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(projectDataSource,fetchSubscriberDataSource, output)

        and: "Datasource that returns various information needed, but throws an exception when getting subscribers"
        fetchSubscriberDataSource.getSubscriberForProject(_ as String) >> []

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "No notifications are created"
        0 * output.createdNotifications(_ as List<NotificationContent>)
        0 * output.failNotification(_ as String)
    }

    def "Providing a valid Date will return notifications stored for that date"(){
        given: "The CreateNotification use case"
        FetchProjectDataSource projectDataSource = Stub()
        FetchSubscriberDataSource fetchSubscriberDataSource = Stub()
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(projectDataSource,fetchSubscriberDataSource, output)

        and: "a dummy Subscriber list and a map containing Samples with their updated Sample status"
        Map<String, Status> updatedSamples = ["QMCDP007A3":Status.DATA_AVAILABLE,
                                              "QMCDP007A2":Status.SAMPLE_QC_FAIL,
                                              "QMCDP007A1":Status.DATA_AVAILABLE,
                                              "QMAAP007A3":Status.SAMPLE_QC_FAIL,
                                              "QMAAP018A2":Status.SAMPLE_RECEIVED,
                                              "QMAAP04525":Status.SAMPLE_QC_FAIL]

        Subscriber subscriber1 = new Subscriber("Awesome", "Customer", "awesome.customer@provider.com")
        Subscriber subscriber2 = new Subscriber("Good", "Customer", "good.customer@provider.de")
        List<Subscriber> subscribers = [subscriber1, subscriber2]
        Map<String,String> projectsWithTitles = ["QMCDP": "first project",
                                                 "QMAAP": ""]

        and: "Datasource that returns various information needed"
        fetchSubscriberDataSource.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        fetchSubscriberDataSource.getSubscriberForProject(_ as String) >> subscribers
        projectDataSource.fetchProjectsWithTitles() >> projectsWithTitles

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "A map associating the notifications with the subscriber for the provided date is returned"
        1* output.createdNotifications(_ as List<NotificationContent>)
    }

    def "2 projects result in 2 email for the same subscriber"(){
        given: "The CreateNotification use case"
        FetchProjectDataSource projectDataSource = Stub()
        FetchSubscriberDataSource fetchSubscriberDataSource = Stub()
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(projectDataSource,fetchSubscriberDataSource, output)

        and: "a dummy Subscriber list and a map containing Samples with their updated Sample status"
        Map<String, Status> updatedSamples = ["QMCDP007A3":Status.DATA_AVAILABLE,
                                              "QMCDP007A2":Status.SAMPLE_QC_FAIL,
                                              "QMCDP007A1":Status.DATA_AVAILABLE,
                                              "QMAAP007A3":Status.SAMPLE_QC_FAIL,
                                              "QMAAP018A2":Status.SAMPLE_RECEIVED,
                                              "QMAAP04525":Status.SAMPLE_QC_FAIL]

        Subscriber subscriber1 = new Subscriber("Awesome", "Customer", "awesome.customer@provider.com")
        List<Subscriber> subscribers = [subscriber1]
        Map<String,String> projectsWithTitles = ["QMCDP": "first project",
                                                 "QMAAP": ""]

        and: "Datasource that returns various information needed"
        fetchSubscriberDataSource.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        fetchSubscriberDataSource.getSubscriberForProject(_ as String) >> subscribers
        projectDataSource.fetchProjectsWithTitles() >> projectsWithTitles

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "A map associating the notifications with the subscriber for the provided date is returned"
        1* output.createdNotifications(_ as List<NotificationContent>) >> {arguments ->
            List<NotificationContent> notifications = arguments.get(0)
            assert notifications.size() == 2
        }
    }

    def "The message contains the correct number of failing samples and available datasets"(){
        given: "The CreateNotification use case"
        FetchProjectDataSource projectDataSource = Stub()
        FetchSubscriberDataSource fetchSubscriberDataSource = Stub()
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(projectDataSource,fetchSubscriberDataSource, output)

        and: "a dummy Subscriber list and a map containing Samples with their updated Sample status"
        Map<String, Status> updatedSamples = ["QMCDP007A3":Status.DATA_AVAILABLE,
                                              "QMCDP007A2":Status.SAMPLE_QC_FAIL,
                                              "QMCDP007A1":Status.DATA_AVAILABLE,
                                              "QMAAP007A3":Status.SAMPLE_QC_FAIL,
                                              "QMAAP018A2":Status.SAMPLE_RECEIVED,
                                              "QMAAP04525":Status.SAMPLE_QC_FAIL]

        Subscriber subscriber1 = new Subscriber("Awesome", "Customer", "awesome.customer@provider.com")
        List<Subscriber> subscribers = [subscriber1]
        Map<String,String> projectsWithTitles = ["QMCDP": "first project",
                                                 "QMAAP": ""]

        and: "Datasource that returns various information needed"
        fetchSubscriberDataSource.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        fetchSubscriberDataSource.getSubscriberForProject(_ as String) >> subscribers
        projectDataSource.fetchProjectsWithTitles() >> projectsWithTitles

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "A map associating the notifications with the subscriber for the provided date is returned"
        1* output.createdNotifications(_ as List<NotificationContent>) >> {arguments ->
            List<NotificationContent> notifications = arguments.get(0)

            assert notifications.get(1).availableDataCount == 2
            assert notifications.get(1).failedQCCount == 1
            assert notifications.get(0).failedQCCount == 2
            assert notifications.get(0).availableDataCount == 0
        }
    }

    def "The message is created multiple times if there is more then one subscriber"(){
        given: "The CreateNotification use case"
        FetchProjectDataSource projectDataSource = Stub()
        FetchSubscriberDataSource fetchSubscriberDataSource = Stub()
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(projectDataSource,fetchSubscriberDataSource, output)

        and: "a dummy Subscriber list and a map containing Samples with their updated Sample status"
        Map<String, Status> updatedSamples = ["QMCDP007A3":Status.DATA_AVAILABLE,
                                              "QMCDP007A2":Status.SAMPLE_QC_FAIL,
                                              "QMCDP007A1":Status.DATA_AVAILABLE]

        Subscriber subscriber1 = new Subscriber("Awesome", "Customer", "awesome.customer@provider.com")
        Subscriber subscriber2 = new Subscriber("Good", "Customer", "good.customer@provider.de")
        List<Subscriber> subscribers = [subscriber1, subscriber2]
        Map<String,String> projectsWithTitles = ["QMCDP": "first project"]

        and: "Datasource that returns various information needed"
        fetchSubscriberDataSource.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        fetchSubscriberDataSource.getSubscriberForProject(_ as String) >> subscribers
        projectDataSource.fetchProjectsWithTitles() >> projectsWithTitles

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "A map associating the notifications with the subscriber for the provided date is returned"
        1* output.createdNotifications(_ as List<NotificationContent>) >> {arguments ->
            List<NotificationContent> notifications = arguments.get(0)
            assert notifications.size() == 2
            assert notifications.get(0).getCustomerEmailAddress() == subscriber1.getEmail()
            assert notifications.get(1).getCustomerEmailAddress() == subscriber2.getEmail()
        }
    }

    def "Notifications are only returned if a status was set to DATA_AVAILABLE or FAILED_QC"(){
        given: "The CreateNotification use case"
        FetchProjectDataSource projectDataSource = Stub()
        FetchSubscriberDataSource fetchSubscriberDataSource = Stub()
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(projectDataSource,fetchSubscriberDataSource, output)

        and: "a dummy subscriber list and a map containing samples with their updated sample status that should not trigger an notifaction email"
        Map<String, Status> updatedSamples = ["QMCDP007A3":Status.SAMPLE_RECEIVED,
                                              "QMCDP007A2":Status.SEQUENCING,
                                              "QMCDP007A1":Status.SAMPLE_QC_PASS,
                                              "QMCDP007A4":Status.WAITING,
                                              "QMCDP007A5":Status.PROCESSED,
                                              "QMCDP007A6":Status.PROCESSING,
                                              "QMAAP007A3":Status.METADATA_REGISTERED,
                                              "QMAAP018A2":Status.DATA_AT_QBIC,
                                              "QMAAP04525":Status.LIBRARY_PREP_FINISHED,
                                              "QMAAP04526":Status.SEQUENCING_COMPLETE]

        Subscriber subscriber1 = new Subscriber("Awesome", "Customer", "awesome.customer@provider.com")
        List<Subscriber> subscribers = [subscriber1]
        Map<String,String> projectsWithTitles = ["QMCDP": "A test project title",
                                                 "QMAAP": "Sequencing of bees"]

        and: "Datasource that returns various information needed"
        fetchSubscriberDataSource.getUpdatedSamplesForDay(_ as LocalDate) >> updatedSamples
        fetchSubscriberDataSource.getSubscriberForProject(_ as String) >> subscribers
        projectDataSource.fetchProjectsWithTitles() >> projectsWithTitles

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications("2020-08-17")

        then: "No notifications are returned"
        0 * output.createdNotifications(_)
        0 * output.failNotification(_)
    }
}
