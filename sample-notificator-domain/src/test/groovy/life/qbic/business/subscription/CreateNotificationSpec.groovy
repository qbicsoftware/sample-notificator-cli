package life.qbic.business.subscription

import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.CreateNotificationDataSource
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

/**
 * <class short description - One Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since: <versiontag>
 *
 */
class CreateNotificationSpec extends Specification{

    def "Providing a valid Date will return notifications stored for that date"(){
        given: "The CreateNotification use case"

        and: "a stubbed data source"
        CreateNotificationDataSource ds = Stub(CreateNotificationDataSource.class)

        CreateNotificationOutput output = Mock()
        CreateNotification createNotification = new CreateNotification(ds, output)

        and: "a mocked subscriberList that is returned from the data source"
        Status failedQCStatus = Status.FAILED_QC
        Status sequencingStatus = Status.SEQUENCING
        Status dataAvailableStatus = Status.DATA_AVAILABLE
        Map<String, Status> subscriptions1 = [QABCDE1: failedQCStatus, QABCDE2: failedQCStatus, QABCDE3: failedQCStatus, QABCDE4: failedQCStatus, QFGHIJ1: sequencingStatus, QFGHIJ2: sequencingStatus, QFGHIJ3: sequencingStatus, QKLMNO1: dataAvailableStatus, QKLMNO2: dataAvailableStatus, QKLMNO3: dataAvailableStatus]
        Map<String, Status> subscriptions2 = [QABCDE1: sequencingStatus, QABCDE2: dataAvailableStatus, QABCDE3: sequencingStatus, QABCDE4: failedQCStatus, QFGHIJ1: dataAvailableStatus, QFGHIJ2: dataAvailableStatus, QFGHIJ3: sequencingStatus, QKLMNO1: dataAvailableStatus, QKLMNO2: failedQCStatus, QKLMNO3: sequencingStatus]
        Subscriber subscriber1 = new Subscriber("J-Dog", "Bödker", "Jenny.bödker@uni-tuebingen.de", subscriptions1)
        Subscriber subscriber2 = new Subscriber("Tobias", "Koch", "Tobias.Koch@uni-tuebingen.de", subscriptions2)

        when: "The CreateNotification Use Case is triggered"
        createNotification.createNotifications([subscriber1, subscriber2])

        then: "A map containing the notifications associated with the subscriber for the provided date is returned"
        1 * output.createdNotifications(_ as Map<Subscriber, String>)
    }
}
