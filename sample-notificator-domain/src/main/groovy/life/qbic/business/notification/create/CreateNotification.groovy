package life.qbic.business.notification.create

import life.qbic.business.subscription.Subscriber

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
    private final CreateNotificationDataSource ds

    CreateNotification(CreateNotificationDataSource ds){
        this.ds = ds
    }

    @Override
    void sendNotifications() {
        //todo when do we want to run the tool? Is "today" sufficient or do we want to do it for the day before?
        List<Subscriber> subscribers = ds.getSubscribersForTodaysNotifications(LocalDate.now())
        println subscribers
        //todo send notifications and fill template
    }
}