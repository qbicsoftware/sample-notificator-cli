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
 * Afterwards it sends the prepared email via a commandline call to the mailUtils sendmail tool(@link <a href=https://mailutils.org/>mailUtils</a> via a ProcessBuilder
 *
 * @since: 1.0.0
 */
class EmailGenerator {

    private EmailHTMLTemplate emailHTMLTemplate
    private Supplier<InputStream> emailHeaderTemplateSupplier
    private Supplier<InputStream> emailNotificationTemplateSupplier
    private Supplier<InputStream> emailFailureTemplateSupplier

    String emailTemplatePath = "notification-template/email-update-template.html"
    String emailHeaderPath = "notification-template/header.txt"
    String emailFailureTemplatePath = "notification-template/failureEmail.txt"
    //ToDo this path should be centralized
    String logPath = "./notification-cli.log"
    Boolean notifyAdmin = false

    String subject = "Failure Notification sample-notificator-cli"
    String supportEmail = "support@qbic.zendesk.com"

    EmailGenerator() {
        loadEmailTemplates()
    }

    /**
     * Generate an
     * containing the provided notifications to the provided subscribers
     */
    void sendEmails(List<NotificationContent> notificationContents) {
        notificationContents.each { NotificationContent notificationContent ->
            Document mailContent = fillEmailTemplate(notificationContent)
            File emailHTMLFile = convertToEmail(mailContent.html())
            send(emailHTMLFile, notificationContent.customerEmailAddress)
        }
        notifyAdminUponFailure(notifyAdmin)
    }

    /**
     * Loads the information stored in the Header and EmailTemplate File into Suppliers
     * since the underlying InputStreams are consumed upon first time usage.
     */
    private void loadEmailTemplates() {
        //Template Content is stored in Jar and can only be accessed via InputStream which is consumed for each Jsoup Parsing
        emailHeaderTemplateSupplier = () -> EmailHTMLTemplate.class.getClassLoader().getResourceAsStream(emailHeaderPath)
        emailNotificationTemplateSupplier = ()  -> EmailHTMLTemplate.class.getClassLoader().getResourceAsStream(emailTemplatePath)
        emailFailureTemplateSupplier = () -> EmailHTMLTemplate.class.getClassLoader().getResourceAsStream(emailFailureTemplatePath)
    }

    /**
     * Triggers the adaption of the emailTemplate to contain the information provided in the notificationContent
     */
    private Document fillEmailTemplate(NotificationContent notificationContent) {
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
     * @return concatenatedHTMLFile of the mailUtils sendmail tool for detailed information see(@link <a href=https://www.cs.ait.ac.th/~on/O/oreilly/tcpip/sendmail/ch36_05.htm/>here</a>)
     */
    private File convertToEmail(String notificationContent) {
        File concatenatedHTMLFile = File.createTempFile("HTMLEmail", ".html")
        concatenatedHTMLFile.append(emailHeaderTemplateSupplier.get())
        concatenatedHTMLFile.append(notificationContent)
        concatenatedHTMLFile.deleteOnExit()
        return concatenatedHTMLFile
    }

    /**
     * Sends the prepared html file to the email address provided in the notificationContent via sendmail
     *
     * The email is send via a commandline call to the mailUtils sendmail tool(@link <a href=https://mailutils.org/>mailUtils</a>) via a ProcessBuilder
     * The "-t" switch allows sendmail to extract information about the email sender, subject and content-type from the email header.
     *
     * @param emailHTMLFile Prepared HTMLFile containing a dedicated emailHeader and the filled in emailTemplate
     * @param emailRecipient String representation of the subscribers email address to which the email should be send
     * @return ExitCode of the mailUtils sendmail tool for detailed information see(@link <a href=https://www.cs.ait.ac.th/~on/O/oreilly/tcpip/sendmail/ch36_05.htm/>here</a>)
     *
     */
    private int send(File emailHTMLFile, String emailRecipient) {
        ProcessBuilder builder = new ProcessBuilder("sendmail", "-t", emailRecipient).redirectInput(emailHTMLFile)
        builder.redirectErrorStream(true)
        Process process = builder.start()
        process.waitFor(10, TimeUnit.SECONDS)
        logSendMailOutput(process)
        // If at least sendmail process fails the admin should be notified
        if (shouldAdminBeNotified(process.exitValue())) {
            notifyAdmin = true
        }
        return process.exitValue()
    }

    /**
     * Logs the output of a given process(in this case sendmail) to a dedicated logfile to enable debugging
     *
     * @param process process for which its terminal output should be logged into the defined logfile
     *
     */
    private void logSendMailOutput(Process process) {
        File logFile = new File(logPath)
        logFile.append(process.getInputStream())
    }

    /**
     * Sets a Boolean which determines if an admin has to be notified upon process failure
     *
     * This method evaluates the exitCodes returned by a process(in this case sendmail).
     * If the process returns anything else than exitCode 0 this means that a responsible party
     * has to be notified since there were issues performing the process. For detailed information
     * see(@link <a href=https://www.cs.ait.ac.th/~on/O/oreilly/tcpip/sendmail/ch36_05.htm/>here</a>)
     *
     * @param exitCode process for which its terminal output should be logged into the defined logfile
     * @return Boolean evaluation if ExitCode differs from 0
     *
     */
    private static Boolean shouldAdminBeNotified(int exitCode) {
        return exitCode != 0
    }

    /**
     * Creates a temporary File containing the failure Notification which will be sent to the responsible party
     *
     * This method creates a temporary file containing the template message notifying the responsible party that the sendmail process could not be completed
     *
     * @return failureEmailFile containing the failure Email Template Text
     */
    private File createFailureNotification() {
        File failureEmailFile = File.createTempFile("failureEmail", ".txt")
        failureEmailFile.deleteOnExit()
        failureEmailFile.append(emailFailureTemplateSupplier.get())
        return failureEmailFile
    }

    /**
     * Creates and sends the failure email to the responsible party if the sendmail process failed
     *
     * This method is provided a boolean representation indicating if an Error occurred during the sendmail process
     * If the original process failed this method will generate a text based email from the failureEmailTemplate and send it to the responsible party
     *
     * @param emailSendingFailed Boolean indicating that an error occurred during the original email sending to the subscribers
     */
    void notifyAdminUponFailure(boolean emailSendingFailed) {
        if (emailSendingFailed) {
            File failureEmailFile = createFailureNotification()
            ProcessBuilder builder = new ProcessBuilder("mail", "-s ${subject}", supportEmail).redirectInput(failureEmailFile)
            builder.redirectErrorStream(true)
            Process process = builder.start()
            process.waitFor(10, TimeUnit.SECONDS)
        }
    }

}

