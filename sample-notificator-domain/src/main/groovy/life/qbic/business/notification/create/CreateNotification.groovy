package life.qbic.business.notification.create

import life.qbic.business.subscription.Subscriber
import life.qbic.business.subscription.fetch.FetchSubscriber
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.business.subscription.fetch.FetchSubscriberInput
import life.qbic.business.subscription.fetch.FetchSubscriberOutput

/**
 * This class implements the logic to create template email messages.
 *
 * The template message generated in this class inform a subscriber
 * about changes in the sample status for her projects after a provided date.
 *
 * @since: 1.0.0
 */

class CreateNotification implements CreateNotificationInput, FetchSubscriberOutput{
    private final CreateNotificationOutput output
    private final FetchSubscriberInput fetchSubscriberInput
    private Map<Subscriber, String> notificationPerSubscriber

    CreateNotification(FetchSubscriberDataSource dataSource, CreateNotificationOutput output){
        this.output = output
        this.fetchSubscriberInput = new FetchSubscriber(dataSource, this)
    }

    @Override
    void createNotifications(String date) {
        fetchSubscriberInput.fetchSubscriber(date)
        output.createdNotifications(notificationPerSubscriber)
    }

    /**
     * Method which returns a map containing the SAMPLE_QC_FAIL notification for each subscriber
     * @param subscribers list of subscribers, which are to be informed about a change in SampleStatus
     * @return map associating the generated notification with the to be informed subscriber
     */
    private static Map<Subscriber, String> createNotificationPerSubscriber(List<Subscriber> subscribers) {

        Map<Subscriber, String> notificationPerSubscriber = [:]
        subscribers.each {
            List<String> sampleCodes = getSampleCodesFromSubscriber(it)
            String failedNotification = failedQCMailMessage(it, sampleCodes)
            notificationPerSubscriber[it] = failedNotification
        }

        return notificationPerSubscriber
    }

    /**
     * Method which extracts the SampleCodes set to SAMPLE_QC_FAIL for a given subscriber
     * @param subscriber Subscriber which is to be notified about SAMPLE_QC_FAIL Sample Codes
     * @return List of SampleCodes whose status is set to SAMPLE_QC_FAIL
     */
    private static List<String> getSampleCodesFromSubscriber(Subscriber subscriber) {
        List<String> failedQCCodes = []
        subscriber.subscriptions.each {
            if(it.value.toString() == "SAMPLE_QC_FAIL") {
                failedQCCodes.add(it.key)
            }}
        return failedQCCodes
    }

    /**
     * Method which assembles the email Template informing a subscriber about the projects containing SAMPLE_QC_FAIL samples
     * @param subscriber Subscriber for whom the template email is generated
     * @param failedQCSampleCodes List of SampleCodes whose status were set to SAMPLE_QC_FAIL.
     * @return String containing the assembled SAMPLE_QC_FAIL template email message.
     */
    private static String failedQCMailMessage(Subscriber subscriber, List<String> failedQCSampleCodes) {

        String notificationIntro =
                """
                Dear ${subscriber.firstName} ${subscriber.lastName},
                this is an automated email to inform you that samples in the following projects have failed the quality control step:
                <a href="Link to some explanation">Click here to learn more.</a>
                """.stripIndent()

        String projectList = listProjects (failedQCSampleCodes)

        String notificationOutro =
                """
                If you would like to unsubscribe from this or other project updates, you can do so by clicking <a href="Link to subscription thingy">here</a>.
                Best regards,
                your QBiC team.
                <signature>
                """.stripIndent()

        String completeMail = notificationIntro.concat(projectList).concat(notificationOutro)

        return completeMail
    }

    /**
     * Method which generates the enumeration of project sample codes for which the status was set to SAMPLE_QC_FAIL
     * @param failedQCSampleCodes List of SampleCodes whose status were set to SAMPLE_QC_FAIL
     * @return String enumerating the projectCode with its Samples for which the status was set to SAMPLE_QC_FAIL
     */
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

    /**
     * Method comparing the currentProjectCode of a Sample with the projectCode of the previous Sample
     * @param currentProjectCode ProjectCode of current Sample in a SampleCode list
     * @param previousProjectCode ProjectCode of the previous Sample in a SampleCode list
     * @return true if ProjectCode if equal, false otherwise
     */
    private static boolean projectCodeIsSame(String currentProjectCode, String previousProjectCode) {
        return currentProjectCode == previousProjectCode
    }

    /**
     * Method extracting the ProjectCode from the SampleCode
     * @param sampleCode SampleCode, which contains the ProjectCode in its first 5 characters
     * @return projectCode of the provided SampleCode.
     */
    private static String getProjectCodeFromSample(String sampleCode) {
        String projectCode = sampleCode.substring(0, 5)
        return projectCode
    }
    /**
     * Transfers the generated list of subscribers to the implementing class
     * @param subscribers the retrieved list of subscribers with modified subscriptions
     * @since 1.0.0
     */

    @Override
    void fetchedSubscribers(List<Subscriber> subscribers) {
        try {
            this.notificationPerSubscriber = createNotificationPerSubscriber(subscribers)
        } catch(Exception e) {
            output.failNotification("An error occurred during notification creation")
        }
    }

    /**
     * Sends failure notifications that have been
     * recorded during the use case.
     * @param notification containing a failure message
     * @since 1.0.0
     */
    @Override
    void failNotification(String notification) {
        output.failNotification(notification)
    }
}