package life.qbic.business.subscription

import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

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

        and: "a date for which notifications are stored"
        String date = "2020-08-21"

        and: "a dummy Subscriber_Notification_Map"
        Status failedQCStatus = Status.FAILED_QC
        Status sequencingStatus = Status.SEQUENCING
        Status dataAvailableStatus = Status.DATA_AVAILABLE
        Map<String, Status> subscriptions1 = [QABCDE1: failedQCStatus, QABCDE2: failedQCStatus, QABCDE3: failedQCStatus, QABCDE4: failedQCStatus, QFGHIJ1: sequencingStatus, QFGHIJ2: sequencingStatus, QFGHIJ3: sequencingStatus, QKLMNO1: dataAvailableStatus, QKLMNO2: dataAvailableStatus, QKLMNO3: dataAvailableStatus]
        Map<String, Status> subscriptions2 = [QABCDE1: sequencingStatus, QABCDE2: dataAvailableStatus, QABCDE3: sequencingStatus, QABCDE4: failedQCStatus, QFGHIJ1: dataAvailableStatus, QFGHIJ2: dataAvailableStatus, QFGHIJ3: sequencingStatus, QKLMNO1: dataAvailableStatus, QKLMNO2: failedQCStatus, QKLMNO3: sequencingStatus]
        Subscriber subscriber1 = new Subscriber("Jennifer", "Bödker", "Jenny.bödker@uni-tuebingen.de", subscriptions1)
        Subscriber subscriber2 = new Subscriber("Tobias", "Koch", "Tobias.Koch@uni-tuebingen.de", subscriptions2)
        Map<Subscriber, String> foundNotifications = new HashMap<Subscriber, String>()
        foundNotifications.put(subscriber1, "FoundNotificationText1")
        foundNotifications.put(subscriber2, "FoundNotificationText2")

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications(date) >> foundNotifications

        then: "A map associating the notifications with the subscriber for the provided date is returned"
        1 * output.createdNotifications(foundNotifications)
    }
}
