package life.qbic.samplenotificator.components.email.support

import life.qbic.samplenotificator.components.email.EmailSendException
import spock.lang.Ignore
import spock.lang.Specification

/**
 * <p>Tests QBiC support email sending functionality.</p>
 */
@Ignore
class SupportEmailSenderSpec extends Specification {

  def "when the email sender sends an email then no exception is thrown"() {
    when: "the email sender sends an email"
    SupportEmailSender emailSender = new SucceedingEmailSenderMock()
    emailSender.sendFailure()
    then: "no exception is thrown"
    noExceptionThrown()
  }

  def "when the email sender fails to send an email then an EmailSendException is thrown"() {
    when: "the email sender fails to send an email"
    SupportEmailSender emailSender = new FailingEmailSenderMock()
    emailSender.sendFailure()
    then: "then an EmailSendException is thrown"
    thrown(EmailSendException)
  }

  class FailingEmailSenderMock extends SupportEmailSender {
    @Override
    protected void sendPlainEmail(File emailFile, String subject) throws EmailSendException {
      throw new EmailSendException()
    }
  }

  class SucceedingEmailSenderMock extends SupportEmailSender {
    SucceedingEmailSenderMock() {
      this.supportEmail = "localhost"
    }
  }
}
