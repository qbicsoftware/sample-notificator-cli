package life.qbic.samplenotificator.components.email.html

import life.qbic.business.notification.send.NotificationEmail
import spock.lang.Specification

class SendmailHeaderSpec extends Specification {

  def "when a new SendmailHeader is printed, then the default values are present"() {
    when: "a new SendmailHeader is printed"

    def sendmailHeader = new SendmailHeader()
    def printed = sendmailHeader.format()
    then: "the default values are present"
    printed == """\
      From: $sendmailHeader.from
      Subject: $sendmailHeader.subject
      Content-Type: $sendmailHeader.contentType
      """.stripIndent()

  }

  def "when a modified SendmailHeader is printed, then the modifications are present"() {
    given:
    String defaultSender = "noreply@qbic.life"
    String defaultContentType = "text/html"
    String subject = "Test subject"
    NotificationEmail notificationEmail = Stub()
    notificationEmail.subject() >> subject


    when: "a modified SendmailHeader is printed"
    def header = SendmailHeader.forNotificationEmail(notificationEmail)
    def printed = header.format()
    then: "the modifications are present"
    printed == """\
      From: $defaultSender
      Subject: Test subject
      Content-Type: $defaultContentType
      """.stripIndent()
  }
}
