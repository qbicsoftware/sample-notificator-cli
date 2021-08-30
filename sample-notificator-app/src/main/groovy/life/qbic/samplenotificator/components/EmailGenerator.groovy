package life.qbic.samplenotificator.components

import life.qbic.business.subscription.Subscriber

import java.util.concurrent.TimeUnit

/**
 * Class responsible for generating an sending an email from the provided notification and subscriber information
 *
 * EmailGenerator converts the notification created in the CreateNotification Use case into an emailBody
 * The contactInformation such as e.g. the emailAddress of the recipient is retrieved from the associated provided Subscriber
 * The email is then send via a commandline call to the mailutils tool(@link <a href=https://mailutils.org/>mailutils</a> via a ProcessBuilder
 *
 * @since: 1.0.0
 *
 */
class EmailGenerator {

    private String subject = "Project Update"
    Map<Subscriber, String> notificationPerSubscriber

    EmailGenerator(Map<Subscriber, String> notificationPerSubscriber) {
        this.notificationPerSubscriber = notificationPerSubscriber
    }

    /**
     * Triggers the creation and submission of each email
     * containing the provided notifications to the provided subscribers
     */
    void initializeEmailSubmission(){
        notificationPerSubscriber.each {
            //ToDo Replace this with subscriber mail
            sendEmail("Steffen.greiner@uni-tuebingen.de", it.value)
        }
    }

    private void sendEmail(String emailRecipient, String notificationContent) {
        def tempNotificationFile = new File('TempNotificationFile.txt')
        tempNotificationFile.write(notificationContent)
        ProcessBuilder builder = new ProcessBuilder("mail", "-s ${subject}", emailRecipient).redirectInput(tempNotificationFile)
        builder.redirectErrorStream(true)
        Process process = builder.start()
        process.waitFor(10, TimeUnit.SECONDS)
        //ToDo This has to be replaced with dedicated logging
        process.getInputStream().eachLine {println(it)}
        tempNotificationFile.delete()
    }

}

