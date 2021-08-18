package life.qbic.business.subscription

import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.CreateNotificationDataSource
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.datamodel.samples.Status
import java.time.LocalDate
import spock.lang.Specification

/**
 * This test class tests for the {@link life.qbic.business.notification.create.CreateNotification} use case functionality
 *
 * @since: 1.0.0
 */
class CreateNotificationSpec extends Specification{

    def "Providing a valid Date will return notifications stored for that date"(){
        given: "The CreateNotification use case"
        CreateNotificationDataSource ds = Stub(CreateNotificationDataSource.class)
        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(ds, output)

        and: "a date for which notifications are stored"
        LocalDate localDate = LocalDate.now()

        and: "a dummy subscriberList"
        Status failedQCStatus = Status.FAILED_QC
        Status sequencingStatus = Status.SEQUENCING
        Status dataAvailableStatus = Status.DATA_AVAILABLE
        Map<String, Status> subscriptions1 = [QABCDE1: failedQCStatus, QABCDE2: failedQCStatus, QABCDE3: failedQCStatus, QABCDE4: failedQCStatus, QFGHIJ1: sequencingStatus, QFGHIJ2: sequencingStatus, QFGHIJ3: sequencingStatus, QKLMNO1: dataAvailableStatus, QKLMNO2: dataAvailableStatus, QKLMNO3: dataAvailableStatus]
        Map<String, Status> subscriptions2 = [QABCDE1: sequencingStatus, QABCDE2: dataAvailableStatus, QABCDE3: sequencingStatus, QABCDE4: failedQCStatus, QFGHIJ1: dataAvailableStatus, QFGHIJ2: dataAvailableStatus, QFGHIJ3: sequencingStatus, QKLMNO1: dataAvailableStatus, QKLMNO2: failedQCStatus, QKLMNO3: sequencingStatus]
        Subscriber subscriber1 = new Subscriber("Jennifer", "Bödker", "Jenny.bödker@uni-tuebingen.de", subscriptions1)
        Subscriber subscriber2 = new Subscriber("Tobias", "Koch", "Tobias.Koch@uni-tuebingen.de", subscriptions2)
        List<Subscriber> subscribers = [subscriber1, subscriber2]

        and: "the dummy subscriberList is returned from the data source"
        ds.getSubscribersForNotificationsAt(localDate) >> subscribers

        when: "The CreateNotification use case is triggered"
        createNotification.createNotifications(localDate)

        then: "A map associating the notifications with the subscriber for the provided date is returned"
        1 * output.createdNotifications(_ as Map<Subscriber, String>) >> {arguments ->
            final Map<Subscriber, String> subscriberNotifications = arguments.get(0)
            assert subscriberNotifications.containsKey(subscriber1)
            assert subscriberNotifications.containsKey(subscriber2)
        }
    }
}
