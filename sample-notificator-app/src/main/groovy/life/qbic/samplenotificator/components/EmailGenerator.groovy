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
 */
class EmailGenerator {

    private String subject = "Project Update"
    private EmailHTMLTemplate emailHTMLTemplate
    private Map<Document, String> emails = [:]

    EmailGenerator(String templatePath, List<NotificationContent> notificationContents) {
        notificationContents.each { NotificationContent notificationContent ->
            prepareEmailTemplate(templatePath)
            prepareEmails(notificationContent)
        }
    }

    /**
     * Reads in the HTML Template provided TemplatePath
     * containing the provided notifications to the provided subscribers
     */
    private void prepareEmailTemplate(String templatePath) {
        //Template Content is stored in Jar and can only be accessed via InputStream which is consumed for each Jsoup Parsing
        InputStream EMAIL_HTML_TEMPLATE_STREAM = EmailHTMLTemplate.class.getClassLoader().getResourceAsStream(templatePath)
        this.emailHTMLTemplate = new EmailHTMLTemplate(Jsoup.parse(EMAIL_HTML_TEMPLATE_STREAM, "UTF-8",""))
    }

    /**
     * Triggers the creation and submission of each email
     * containing the provided notifications to the provided subscribers
     */
    private void prepareEmails(NotificationContent notificationContent) {
        Document filledEmail = emailHTMLTemplate.fillTemplate(notificationContent)
        emails.put(filledEmail, notificationContent.customerEmailAddress)
    }

    void sendEmails() {
        emails.each { Document emailContent, String emailRecipient ->
            def tempNotificationFile = new File('TempNotificationFile.html')
            tempNotificationFile.write(emailContent.html())
            ProcessBuilder builder = new ProcessBuilder("mail", "-s ${subject}", emailRecipient).redirectInput(tempNotificationFile)
            builder.redirectErrorStream(true)
            Process process = builder.start()
            process.waitFor(10, TimeUnit.SECONDS)
            //ToDo This has to be replaced with dedicated writing into a log on executing server
            process.getInputStream().eachLine { println(it) }
            //ToDo How should exit codes be handled with the cronjob? See https://mailutils.org/manual/html_section/mailutils.html for Exit Codes
            println("ExitCode: " + process.exitValue())
            tempNotificationFile.delete()
        }
    }
}

