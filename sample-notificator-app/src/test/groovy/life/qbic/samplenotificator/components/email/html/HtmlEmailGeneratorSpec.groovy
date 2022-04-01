package life.qbic.samplenotificator.components.email.html

import life.qbic.business.notification.create.NotificationContent
import life.qbic.business.notification.send.EmailGenerator
import life.qbic.business.notification.send.NotificationEmail
import life.qbic.business.notification.unsubscription.UnsubscriptionLinkSupplier
import spock.lang.Specification

/**
 * <p>Tests the HtmlEmailGenerator</p>
 */
class HtmlEmailGeneratorSpec extends Specification {
  NotificationContent.Builder notificationContentBuilder = new NotificationContent.Builder(
          "max@muster.mann",
          "Maximilian",
          "Muster",
          "max@muster.mann",
          "Test Project",
          "QABCD",
          4,
          6
  )
  UnsubscriptionLinkSupplier unsubscriptionLinkSupplier = Stub()
  EmailGenerator<NotificationEmail> emailGenerator = new HtmlEmailGenerator(unsubscriptionLinkSupplier)

  def "when the generator is applied to a notification content then the generated email comes filled"() {
    given: "a notification content"
    NotificationContent notificationContent = notificationContentBuilder.build()
    unsubscriptionLinkSupplier.get(_ as String, _ as String) >> "my-awesome-test/unsubscribe"
    HtmlNotificationEmail expectedEmail = new HtmlNotificationEmail(unsubscriptionLinkSupplier)
    when: "the generator is applied to a notification content"
    def result = emailGenerator.apply(notificationContent)
    expectedEmail.fill(notificationContent)
    then: "the generated email comes filled"
    result instanceof NotificationEmail
    result == expectedEmail
  }
}
