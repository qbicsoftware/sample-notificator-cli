package life.qbic.samplenotificator.components.email.html

import life.qbic.business.notification.create.NotificationContent
import life.qbic.business.notification.unsubscription.UnsubscriptionLinkSupplier
import life.qbic.samplenotificator.components.email.EmailSendException
import spock.lang.Specification

/**
 * <p>Tests email sending behaviour</p>
 */
class HtmlEmailSenderSpec extends Specification {
  static NotificationContent notificationContent
  static HtmlEmailGenerator htmlEmailGenerator
  static HtmlNotificationEmail htmlNotificationEmail

  void setupSpec() {
    UnsubscriptionLinkSupplier unsubscriptionLinkSupplier = Stub()
    unsubscriptionLinkSupplier.get(_ as String, _ as String) >> "awesome.page/unsubscribe"

    notificationContent = new NotificationContent.Builder(
            "Maximilian",
            "Muster",
            "max.muster@ma.nn",
            "Test project",
            "QABCD",
            5,
            6)
            .build()
    htmlEmailGenerator= new HtmlEmailGenerator(unsubscriptionLinkSupplier)
    htmlNotificationEmail = htmlEmailGenerator.apply(notificationContent)
  }

  def "when the email sender sends an email then no exception is thrown"() {
    when: "the email sender sends an email"
    HtmlEmailSender emailSender = new SuccessfulEmailSenderMock()
    emailSender.accept(htmlNotificationEmail)
    then: "no exception is thrown"
    noExceptionThrown()
    and: "no unsent emails exist"
    emailSender.notSent().isEmpty()
  }

  def "when the email sender fails to send an email then the email is provided by notSent()"() {
    when: "the email sender fails to send an email"
    HtmlEmailSender emailSender = new FailingEmailSenderMock()
    emailSender.accept(htmlNotificationEmail)
    then: "the email is provided by notSent()"
    emailSender.notSent().contains(htmlNotificationEmail)
  }

  class FailingEmailSenderMock extends HtmlEmailSender {
    @Override
    protected void sendSendmailEmail(File emailFile, String recipient) throws EmailSendException {
      throw new EmailSendException()
    }
  }

  class SuccessfulEmailSenderMock extends HtmlEmailSender {
    @Override
    protected void sendSendmailEmail(File emailFile, String recipient) throws EmailSendException {
      super.sendSendmailEmail(emailFile, "tobias.koch@qbic.uni-tuebingen.de")
    }
  }
}
