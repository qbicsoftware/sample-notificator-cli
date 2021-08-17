package life.qbic.business.subscription

import life.qbic.business.notification.create.CreateNotification
import life.qbic.business.notification.create.CreateNotificationDataSource
import life.qbic.business.notification.create.CreateNotificationInput
import life.qbic.business.notification.create.CreateNotificationOutput
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

import java.time.LocalDate

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
        Subscriber subscriber1 = new Subscriber("Jenny", "Bödker", "Jenny.bödker@uni-tuebingen.de", new HashMap<String, Status>())
        Subscriber subscriber2 = new Subscriber("Tobias", "Koch", "Tobias.Koch@uni-tuebingen.de", new HashMap<String, Status>())
        ds.getSubscribersForNotificationsAt(_ as LocalDate) >> ["Jenny", "Tobi", "Sven", "Steffen", "Andreas", "Luis"]

        when: "The Create Notification is triggered"
        createNotification.createNotifications("1998-04-12")

        then: "A Map associated notifications with their subscriber for the provided date is returned"
        1 * output.createdNotifications(_ as Map<Subscriber, String>)
    }
}
