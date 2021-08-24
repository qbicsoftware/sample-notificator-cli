package life.qbic.business.notification.create

import life.qbic.business.subscription.Subscriber
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource

import java.time.LocalDate

/**
 * <h1>Sends an email to a lists of subscribers</h1>
 *
 * <p>This use case collects a list of subscribers who's subscribed project got updated the current day</p>
 *
 * @since 1.0.0
 *
*/
class CreateNotification implements CreateNotificationInput{
    private final FetchSubscriberDataSource ds

    CreateNotification(FetchSubscriberDataSource ds, CreateNotificationOutput output){
        this.ds = ds
    }

    @Override
    void createNotifications(String date) {
        LocalDate localDate = LocalDate.parse(date)
        List<Subscriber> subscribers = ds.getSubscribersForNotificationsAt(localDate)
        println subscribers
        //todo send notifications and fill template
    }
}