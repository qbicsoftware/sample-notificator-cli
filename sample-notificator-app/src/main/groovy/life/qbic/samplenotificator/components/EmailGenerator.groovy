package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.NotificationContent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * Class responsible for generating an sending an email from the provided project and subscriber information
 *
 * EmailGenerator extracts the information provided in the NotificationContent DTO and fills it into a prepared template structure.
 * Afterwards it sends the prepared email via a commandline call to the mailutils sendmail tool(@link <a href=https://mailutils.org/>mailutils</a> via a ProcessBuilder
 *
 * @since: 1.0.0
 */
class EmailGenerator {

    private EmailHTMLTemplate emailHTMLTemplate
    private Supplier<InputStream> emailHeaderTemplateSupplier
    private Supplier<InputStream> emailNotificationTemplateSupplier

    String emailTemplatePath = "notification-template/email-update-template.html"
    String emailHeaderPath = "notification-template/header.txt"

    EmailGenerator() {
        loadEmailTemplates()
    }

    /**
     * Generate an
     * containing the provided notifications to the provided subscribers
     */
    void sendEmails(List<NotificationContent> notificationContents) {
        notificationContents.each { NotificationContent notificationContent ->
            Document filledEmailContent = fillEmailContent(notificationContent)
            File emailHTMLFile = GroupEmailBodyAndHeaderIntoHTMLFile(filledEmailContent.html())
            send(emailHTMLFile, notificationContent.customerEmailAddress)
        }
    }

    /**
     * Loads the information stored in the Header and EmailTemplate File into Suppliers
     * since the underlying InputStreams are consumed upon first time usage.
     */
    private void loadEmailTemplates() {
        //Template Content is stored in Jar and can only be accessed via InputStream which is consumed for each Jsoup Parsing
        emailHeaderTemplateSupplier = () -> EmailHTMLTemplate.class.getClassLoader().getResourceAsStream(emailHeaderPath)
        emailNotificationTemplateSupplier = ()  -> EmailHTMLTemplate.class.getClassLoader().getResourceAsStream(emailTemplatePath)
    }

    /**
     * Triggers the adaption of the emailTemplate to contain the information provided in the notificationContent
     */
    private Document fillEmailContent(NotificationContent notificationContent) {
        emailHTMLTemplate = new EmailHTMLTemplate(Jsoup.parse(emailNotificationTemplateSupplier.get(), "UTF-8", ""))
        Document filledEmailTemplate = emailHTMLTemplate.fillTemplate(notificationContent)
        return filledEmailTemplate
    }

    /**
     * Concatenates email header information and the actual email content into a singular file
     *
     * Method to create a file containing the email header information{@see notification-template/header.txt} and the dynamically adapted email message.
     * This is necessary since sendmail doesn't provide a dedicated command line switch to set the content-type of the message
     * @param notificationContent String representation of the filled in HTML Email Template {@see notification-template/email-update-template.html}
     * @return concatenatedHTMLFile of the mailutils sendmail tool for detailed information see(@link <a href=https://www.cs.ait.ac.th/~on/O/oreilly/tcpip/sendmail/ch36_05.htm/>here</a>)
     */
    private File GroupEmailBodyAndHeaderIntoHTMLFile(String notificationContent) {
        File concatenatedHTMLFile = File.createTempFile("HTMLEmail", ".html")
        concatenatedHTMLFile.append(emailHeaderTemplateSupplier.get())
        concatenatedHTMLFile.append(notificationContent)
        concatenatedHTMLFile.deleteOnExit()
        return concatenatedHTMLFile
    }

    /**
     * Sends the prepared html file to the email address provided in the notificationContent via sendmail
     *
     * The email is send via a commandline call to the mailutils sendmail tool(@link <a href=https://mailutils.org/>mailutils</a>) via a ProcessBuilder
     * The "-t" switch allows sendmail to extract information about the email sender, subject and content-type from the email header.
     *
     * @param emailHTMLFile Prepared HTMLFile containing a dedicated emailHeader and the filled in emailTemplate
     * @param emailRecipient String representation of the subscribers email address to which the email should be send
     * @return ExitCode of the mailutils sendmail tool for detailed information see(@link <a href=https://www.cs.ait.ac.th/~on/O/oreilly/tcpip/sendmail/ch36_05.htm/>here</a>)
     *
     */
    //ToDo Move this into separate class
    private int send(File emailHTMLFile, String emailRecipient) {
            ProcessBuilder builder = new ProcessBuilder("sendmail", "-t", emailRecipient).redirectInput(emailHTMLFile)
            builder.redirectErrorStream(true)
            Process process = builder.start()
            process.waitFor(10, TimeUnit.SECONDS)
            //ToDo This has to be replaced with dedicated writing into a log on executing server
            process.getInputStream().eachLine { println(it) }
            //ToDo How should exit codes be handled with the cronjob? See https://mailutils.org/manual/html_section/mailutils.html for Exit Codes
            println(process.exitValue())
            return process.exitValue()
    }
}

