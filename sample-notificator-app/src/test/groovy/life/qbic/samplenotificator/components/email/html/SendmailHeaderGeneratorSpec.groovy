package life.qbic.samplenotificator.components.email.html


import spock.lang.Specification

class SendmailHeaderGeneratorSpec extends Specification {

  static final String DEFAULT_SENDER = "noreply@qbic.life"
  static final String DEFAULT_CONTENT_TYPE = "text/html"

  def "when a sendmail header is generated with only a subject, then the generated header is formatted correctly"() {
    given:
    String subject = "Test subject"

    when: "a sendmail header is generated with only a subject"
    String generatedHeader = SendmailHeaderGenerator.generateFromSubject(subject)
    then: "the generated header is formatted correctly"
    generatedHeader == """\
      From: $DEFAULT_SENDER
      Subject: Test subject
      Content-Type: $DEFAULT_CONTENT_TYPE
      """.stripIndent()
  }

  def "when a sendmail header is generated with no subject, then an IllegalArgumentException is thrown"() {
    given:
    String subject = null

    when: "a sendmail header is generated with no subject"
    SendmailHeaderGenerator.generateFromSubject(subject)
    then: "an IllegalArgumentException is thrown"
    thrown(IllegalArgumentException)
  }
}
