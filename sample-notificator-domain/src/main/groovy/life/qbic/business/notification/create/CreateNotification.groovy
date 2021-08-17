package life.qbic.business.notification.create

import life.qbic.business.subscription.Subscriber

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
    void createNotifications(List<Subscriber> subscribers) {
        Map<Subscriber, String> createdNotifications = createNotificationPerSubscriber(subscribers)
        output.createdNotifications(createdNotifications)
    }

    private static Map<Subscriber, String> createNotificationPerSubscriber(List<Subscriber> subscriber) {

        Map<Subscriber, String> notificationPerSubscriber = [:]
        subscriber.each {
            List<String> sampleCodes = getSampleCodesFromSubscriber(it)
            String failedNotification = failedQCMailMessage(it, sampleCodes)
            notificationPerSubscriber[it] = failedNotification
        }

        return notificationPerSubscriber
    }

    private static List<String> getSampleCodesFromSubscriber(Subscriber subscriber) {
        List<String> failedQCCodes = []
        subscriber.subscriptions.each {
            if(it.value.toString() == "FAILED_QC") {
                failedQCCodes.add(it.key)
            }}
        return failedQCCodes
    }

    private static String failedQCMailMessage(Subscriber subscriber, List<String> failedQCSampleCodes) {

        String notificationIntro = """
                                   Dear ${subscriber.firstName} ${subscriber.lastName},
                                   this is an automated email to inform you that samples in the following projects have failed the quality control step:
                                   <a href="Link to some explanation">Click here to learn more.</a>
                                   """
        String projectList = listProjects(failedQCSampleCodes)

        String notificationOutro = """
                                   If you would like to unsubscribe from this or other project updates, you can do so by clicking <a href="Link to subscription thingy">here</a>.
                                   Best regards,
                                   your QBiC team.
                                   <signature>
                                   """

        String completeMail = notificationIntro.concat(projectList).concat(notificationOutro)

        return completeMail
    }


    private static String listProjects(List<String> failedQCSampleCodes){

        StringBuffer listProjectsBuffer = new StringBuffer()

        String previousProjectCode = "NONEXISTENT PROJECT CODE"
        //Let's make sure the Samples are sorted by their ProjectCode before adding them to the generated Mail.
        failedQCSampleCodes.sort().each {String sampleCode ->
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
        String projectCode = sampleCode.substring(0, 5)
        return projectCode
    }
}