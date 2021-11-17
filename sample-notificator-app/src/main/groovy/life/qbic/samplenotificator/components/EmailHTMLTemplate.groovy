package life.qbic.samplenotificator.components

import life.qbic.business.notification.create.NotificationContent
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * <b>An email template that can be filled with a notificationContent</b>
 *
 * <p>This class represents a html document structure. It provides filled versions of the same document.</p>
 *
 * @since 1.0.0
 */
class EmailHTMLTemplate {

    private static Document emailHTMLTemplate

    EmailHTMLTemplate(Document emailHTMLTemplate) {
        this.emailHTMLTemplate = emailHTMLTemplate
    }

    static Document fillTemplate(NotificationContent notificationContent) {
        Document result = emailHTMLTemplate.clone()
        fillPersonInformation(result, notificationContent)
        fillProjectInformation(result, notificationContent)
        fillSampleStatusInformation(result, notificationContent)
        return result
    }

    static void fillPersonInformation(Document document, NotificationContent notificationContent) {
        Element personInformation = document.getElementById("person-information")
        personInformation.empty()
        String customerName = String.format(
                "%s %s",
                notificationContent.getCustomerFirstName(),
                notificationContent.getCustomerLastName())
        personInformation.text(customerName)
    }

    static void fillProjectInformation(Document document, NotificationContent notificationContent) {
        Element projectTitle = document.getElementById("project-title")
        Element projectCode = document.getElementById("project-code")
        projectTitle.empty()
        projectCode.empty()
        String titleContent = notificationContent.getProjectTitle() ?: ""
        projectTitle.text(titleContent)
        projectCode.text(notificationContent.getProjectCode())
    }

    static void fillSampleStatusInformation(Document document, NotificationContent notificationContent) {

        if (notificationContent.availableDataCount == 0) {
            document.getElementById("sample-status-available-data").remove()
        } else {
            Element dataAvailableCount = document.getElementById("sample-status-available-data-count")
            dataAvailableCount.empty()
            dataAvailableCount.text(notificationContent.getAvailableDataCount().toString())
        }

        if (notificationContent.failedQCCount == 0) {
            document.getElementById("sample-status-failed-qc").remove()
        } else {
            Element failedQCCount = document.getElementById("sample-status-failed-qc-count")
            failedQCCount.empty()
            failedQCCount.text(notificationContent.getFailedQCCount().toString())
        }
    }
}
