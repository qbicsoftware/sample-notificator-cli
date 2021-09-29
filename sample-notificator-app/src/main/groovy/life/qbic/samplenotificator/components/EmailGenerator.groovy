package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.NotificationContent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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
    List<NotificationContent> notificationContentList
    private InputStream EMAIL_HTML_TEMPLATE_STREAM
    private EmailHTMLTemplate emailHTMLTemplate
    private List<Document> filledTemplates = []

    EmailGenerator(List<NotificationContent> notificationContentList) {
        this.notificationContentList = notificationContentList
    }

    /**
     * Triggers the creation and submission of each email
     * containing the provided notifications to the provided subscribers
     */
    void initializeEmailSubmission() {
        notificationContentList.each { NotificationContent notificationContent ->
            accessEmailTemplate()
            prepareHTMLEmail(notificationContent)
            //ToDo sendEmail will be handled by a different PR
            //sendEmail("Steffen.greiner@uni-tuebingen.de", filledTemplate.html())
        }
    }

    private void accessEmailTemplate() {
        this.EMAIL_HTML_TEMPLATE_STREAM = EmailHTMLTemplate.class.getClassLoader().getResourceAsStream("notification-template/email-update-template.html")
    }

    private void prepareHTMLEmail(NotificationContent notificationContent) {
        this.emailHTMLTemplate = new EmailHTMLTemplate(Jsoup.parse(EMAIL_HTML_TEMPLATE_STREAM, "UTF-8", ""))
        filledTemplates.add(emailHTMLTemplate.fillTemplate(notificationContent))
    }

    private void sendEmail(String emailRecipient, String notificationContent) {
        def tempNotificationFile = new File('TempNotificationFile.html')
        tempNotificationFile.write(notificationContent)
        //ToDo Replace this with Sendmail and add custom header
        ProcessBuilder builder = new ProcessBuilder("mail", "-s ${subject}", emailRecipient).redirectInput(tempNotificationFile)
        builder.redirectErrorStream(true)
        Process process = builder.start()
        process.waitFor(10, TimeUnit.SECONDS)
        //ToDo This has to be replaced with dedicated writing into a log on executing server
        process.getInputStream().eachLine {println(it)}
        //ToDo How should exit codes be handled with the cronjob? See https://mailutils.org/manual/html_section/mailutils.html for Exit Codes
        println("ExitCode: " + process.exitValue())
        tempNotificationFile.delete()
    }

}

