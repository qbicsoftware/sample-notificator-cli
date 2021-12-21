package life.qbic.samplenotificator.components.refactor

import life.qbic.business.notification.create.NotificationContent
import life.qbic.business.notification.refactor.EmailGenerator
import life.qbic.business.notification.refactor.NotificationEmail
import spock.lang.Specification

/**
 * <p>Tests the HtmlEmailGenerator</p>
 */
class HtmlEmailGeneratorSpec extends Specification {
  NotificationContent.Builder notificationContentBuilder = new NotificationContent.Builder(
          "Maximilian",
          "Muster",
          "max@muster.mann",
          "Test Project",
          "QABCD",
          4,
          6
  )
  EmailGenerator<NotificationEmail> emailGenerator = new HtmlEmailGenerator()

  def "when the generator is applied to a notification content then the generated email comes filled"() {
    given: "a notification content"
    NotificationContent notificationContent = notificationContentBuilder.build()
    HtmlNotificationEmail expectedEmail = new HtmlNotificationEmail()
    when: "the generator is applied to a notification content"
    def result = emailGenerator.apply(notificationContent)
    expectedEmail.fill(notificationContent)
    then: "the generated email comes filled"
    result instanceof NotificationEmail
    result == expectedEmail
  }
}
