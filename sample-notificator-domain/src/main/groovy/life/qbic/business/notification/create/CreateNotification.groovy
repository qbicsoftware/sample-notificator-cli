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
    private final CreateNotificationOutput output

    CreateNotification(CreateNotificationDataSource ds, CreateNotificationOutput output){
        this.ds = ds
        this.output = output
    }

    @Override
    void createNotifications(String date) {
        LocalDate localDate = LocalDate.parse(date)
        List<Subscriber> subscribers = ds.getSubscribersForNotificationsAt(localDate)
        println subscribers
        Map<Subscriber, String> createdNotifications = createNotificationPerSubscriber(subscribers)
        println(createdNotifications)
        output.createdNotifications(createdNotifications)
    }

    private static Map<Subscriber, String> createNotificationPerSubscriber(List<Subscriber> subscriber) {

        //ToDo replace this with sampleCodes associated with subscriber
        List<String> sampleCodes = ["ABCDESAMPLE1", "ABCDESAMPLE2", "ABCDESAMPLE3", "FGHIJSAMPLE1", "FGHIJSAMPLE2", "FGHIJSAMPLE3", "KLMNOPSAMPLE1", "KLMNOPSAMPLE2", "KLMNOPSAMPLE3"]
        Map<Subscriber, String> notificationPerSubscriber = [:]
        subscriber.each {
            String failedNotification = failedQCMailMessage(it, sampleCodes)
            notificationPerSubscriber[it] = failedNotification
        }

        return notificationPerSubscriber
    }


    private static String failedQCMailMessage(Subscriber subscriber, List<String> failedQCSampleCodes) {
        String failedQCMessage = """Dear ${subscriber.firstName} ${subscriber.lastName},
                                 this is an automated email to inform you that samples in the following projects have failed the quality control step:
                                 <a href="Link to some explanation">Click here to learn more.</a>
                                 If you would like to unsubscribe from this or other project updates, you can do so by clicking <a href="Link to subscription thingy">here</a>.
                                 Best regards,
                                 your QBiC team.
                                 <signature>
             """
        return failedQCMessage
    }


    private static String listProjects(List<String> failedQCSampleCodes){

        StringBuffer listProjectsBuffer

        String previousProjectCode = "TestProject"
        failedQCSampleCodes.each {String sampleCode ->
            if (projectCodeIsSame(getProjectCodeFromSample(sampleCode), previousProjectCode)){
                listProjectsBuffer.append(sampleCode)
                listProjectsBuffer << "\n"
            }
            else{
                previousProjectCode = getProjectCodeFromSample(sampleCode)
                listProjectsBuffer.append(previousProjectCode)
                listProjectsBuffer << ":"
                listProjectsBuffer << "\n"
                listProjectsBuffer.append(sampleCode)
                listProjectsBuffer << "\n"
            }
        }
        return listProjectsBuffer.toString()
    }

    private static boolean projectCodeIsSame(String currentProjectCode, String previousProjectCode) {
        return currentProjectCode == previousProjectCode
    }

    private static String getProjectCodeFromSample(String sampleCode) {
        return sampleCode(0, 5)
    }
}