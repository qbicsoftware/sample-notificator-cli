package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.NotificationContent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit


/**
 * Class responsible for generating an sending an email from the provided project and subscriber information
 *
 * EmailGenerator extracts the information provided in the NotificationContent DTO and fills it into a prepared template structure.
 * Afterwards it sends the prepared email via a commandline call to the mailutils sendmail tool(@link <a href=https://mailutils.org/>mailutils</a> via a ProcessBuilder
 *
 * @since: 1.0.0
 *
 */
class EmailGenerator {

    /**
     * Get the fixed email and email header template structure
     */
    private final InputStream EMAIL_HTML_HEADER_STREAM = EmailHTMLTemplate.class.getClassLoader().getResourceAsStream("notification-template/header.txt")
    private final InputStream EMAIL_HTML_TEMPLATE_STREAM = EmailHTMLTemplate.class.getClassLoader().getResourceAsStream("notification-template/email-update-template.html")

    /**
     * Paths for temporary files used to prepare the final html file
     */
    private final Path tempDir
    private final Path EMAIL_HTML_TEMPLATE_PATH
    private final Path EMAIL_HTML_HEADER_PATH
    private final Path EMAIL_HTML_CREATED_PATH

    NotificationContent notificationContent
    private EmailHTMLTemplate emailHTMLTemplate
    private Document filledTemplateMessage
    private File preparedEmailHTMLFile

    EmailGenerator(NotificationContent notificationContent) {
        this.notificationContent = notificationContent
        this.tempDir = Files.createTempDirectory("EmailHTML")
        this.EMAIL_HTML_HEADER_PATH = Paths.get(tempDir.toString(), "header.txt")
        this.EMAIL_HTML_TEMPLATE_PATH = Paths.get(tempDir.toString(), "email-update-template.html")
        this.EMAIL_HTML_CREATED_PATH = Paths.get(tempDir.toString(), "email.html")
        importTemplateResources()
    }

    /**
     * Triggers the creation and submission of each email
     * containing the provided notifications to the provided subscribers
     */
    void initializeEmailSubmission() {
        prepareHTMLEmail()
        this.preparedEmailHTMLFile = writeHTMLContentToFile(filledTemplateMessage.html())
        String emailRecipient = notificationContent.customerEmailAddress
        sendEmail(preparedEmailHTMLFile, emailRecipient)
    }

    /**
     * Prepare temporary files storing email message and email header templates
     */
    private void importTemplateResources() {
        Files.copy(EMAIL_HTML_TEMPLATE_STREAM, EMAIL_HTML_TEMPLATE_PATH, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(EMAIL_HTML_HEADER_STREAM, EMAIL_HTML_HEADER_PATH, StandardCopyOption.REPLACE_EXISTING)
    }

    /**
     * Fill in template with subscriber and project information provided in the notificationContent
     */
    private void prepareHTMLEmail() {
        this.emailHTMLTemplate = new EmailHTMLTemplate(Jsoup.parse(new File(EMAIL_HTML_TEMPLATE_PATH.toString()), "UTF-8"))
        this.filledTemplateMessage = emailHTMLTemplate.fillTemplate(notificationContent)
    }

    /**
     * Concatenates email header information and the actual email content into a singular file
     *
     * Method to create a file containing the email header information{@see notification-template/header.txt} and the dynamically adapted email message.
     * This is necessary since sendmail doesn't provide a dedicated command line switch to set the content-type of the message
     * @param notificationContent String representation of the filled in HTML Email Template {@see notification-template/email-update-template.html}
     * @return concatenatedHTMLFile of the mailutils sendmail tool for detailed information see(@link <a href=https://www.cs.ait.ac.th/~on/O/oreilly/tcpip/sendmail/ch36_05.htm/>here</a>)
     */
    private File writeHTMLContentToFile(String notificationContent){
        File concatenatedHTMLFile = new File(EMAIL_HTML_CREATED_PATH.toString())
        File emailHeaderFile = new File(EMAIL_HTML_HEADER_PATH.toString())
        concatenatedHTMLFile.append(emailHeaderFile.bytes)
        concatenatedHTMLFile.append(notificationContent)
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
    private int sendEmail(File emailHTMLFile, String emailRecipient) {
        ProcessBuilder builder = new ProcessBuilder("sendmail", "-t", emailRecipient).redirectInput(emailHTMLFile)
        builder.redirectErrorStream(true)
        Process process = builder.start()
        process.waitFor(10, TimeUnit.SECONDS)
        //ToDo This has to be replaced with dedicated writing into a log on executing server
        process.getInputStream().eachLine {println(it)}
        //ToDo How should exit codes be handled with the cronjob? See https://mailutils.org/manual/html_section/mailutils.html for Exit Codes
        return process.exitValue()
    }

}

