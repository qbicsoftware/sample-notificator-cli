package life.qbic.business.notification.send

import life.qbic.business.notification.create.NotificationContent
import spock.lang.Specification

/**
 * <p>Tests for the logic of when and how to send notification emails.</p>
 */
class SendEmailSpec extends Specification {

  EmailGenerator emailGenerator = Stub()
  EmailSender flawlessEmailSender = Mock()
  FailureEmailSender adminInformer = Mock()
  NotificationContent notificationContent = new NotificationContent.Builder(
          "max@muster.mann",
          "Max",
          "Mustermann",
          "max@muster.mann",
          "Test project",
          "QABCD",
          0,
          1)
          .build()
  NotificationContent notificationContentTwo = new NotificationContent.Builder(
          "max@muster.mann",
          "Max",
          "Mustermann",
          "max@muster.mann",
          "Test project",
          "QSTTS",
          2,
          1)
          .build()
  NotificationEmail emailMock = Mock()

  def "when #n notifications are present then send #n emails"() {
    given: "a notification content"
    emailGenerator.apply(notificationContent) >> emailMock
    var notifications = [notificationContent] * n

    when: "a notification is present"
    SendEmail sendEmail = new SendEmail(flawlessEmailSender, adminInformer, emailGenerator)
    sendEmail.sendEmailNotifications(notifications)

    then: "send an email"
    n * flawlessEmailSender.accept(emailMock)
    and: "the admin is not informed"
    0 * adminInformer.sendFailure()
    where:
    n << [0, 1, 5, 33, 100]
  }

  def "when the sending of at least one email fails then the admin is informed once"() {
    given:
    NotificationEmail email = Mock()
    emailGenerator.apply(notificationContent) >> emailMock
    emailGenerator.apply(notificationContentTwo) >> email
    var notifications = [notificationContent, notificationContentTwo]
    flawlessEmailSender = Stub()

    when: "the sending of at least one email fails"
    flawlessEmailSender.notSent() >> [emailMock, email]
    SendEmail sendEmail = new SendEmail(flawlessEmailSender, adminInformer, emailGenerator)
    sendEmail.sendEmailNotifications(notifications)

    then: "the admin is informed"
    1 * adminInformer.sendFailure()
  }


}
