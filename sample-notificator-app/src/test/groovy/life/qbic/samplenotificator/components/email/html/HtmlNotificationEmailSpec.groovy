package life.qbic.samplenotificator.components.email.html

import life.qbic.business.notification.create.NotificationContent
import life.qbic.business.notification.send.NotificationEmail
import life.qbic.business.notification.unsubscription.UnsubscriptionLinkSupplier
import spock.lang.Specification

class HtmlNotificationEmailSpec extends Specification {
  NotificationContent.Builder notificationContentBuilder = new NotificationContent.Builder(
          "Maximilian",
          "Muster",
          "max.muster@ma.nn",
          "Test project",
          "QABCD",
          5,
          6)
  UnsubscriptionLinkSupplier unsubscriptionLinkSupplier = Stub()

  def "constructor works"() {
    given:
    notificationContentBuilder.setFailedQCCount(5)
    notificationContentBuilder.setAvailableDataCount(4)
    NotificationContent notificationContent = notificationContentBuilder.build()
    when:
    NotificationEmail notificationEmail = new HtmlNotificationEmail(unsubscriptionLinkSupplier)
    notificationEmail.fill(notificationContent)
    then:
    notificationEmail.recipient() == notificationContent.getCustomerEmailAddress()
    notificationEmail.body() as boolean
  }

  def "removal of empty statuses works"() {
    given:
    notificationContentBuilder.setFailedQCCount(0)
    notificationContentBuilder.setAvailableDataCount(0)
    NotificationContent notificationContent = notificationContentBuilder.build()
    when:
    NotificationEmail notificationEmail = new HtmlNotificationEmail(unsubscriptionLinkSupplier)
    notificationEmail.fill(notificationContent)
    then:
    notificationEmail.recipient() == notificationContent.getCustomerEmailAddress()
    !notificationEmail.body().contains("sample-status-failed-qc")
    !notificationEmail.body().contains("sample-status-available-data-count")
  }

  def "given a filled notification email, when the subject is retrieved, then the subject contains the project code"() {
    given: "a filled notification email"
    notificationContentBuilder.setProjectCode("MY_PROJECT_CODE")
    NotificationContent notificationContent = notificationContentBuilder.build()
    NotificationEmail notificationEmail = new HtmlNotificationEmail(unsubscriptionLinkSupplier)
    notificationEmail.fill(notificationContent)

    when: "the subject is retrieved"
    def subject = notificationEmail.subject()

    then: "the subject contains the project code"
    subject.contains("MY_PROJECT_CODE")
  }

}
