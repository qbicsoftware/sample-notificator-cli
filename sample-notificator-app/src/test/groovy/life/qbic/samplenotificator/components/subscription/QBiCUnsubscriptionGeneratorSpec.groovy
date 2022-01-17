package life.qbic.samplenotificator.components.subscription

import spock.lang.Ignore
import spock.lang.Specification

/**
 * <p>Tests unsubscription link generation functionality.</p>
 *
 * @since 1.2.0
 */
class QBiCUnsubscriptionGeneratorSpec extends Specification {

  private String subscriptionServiceUri = "http://localhost:8080"
  private String unsubscriptionPortletUrl = "http://www.test.services.qbic/endpoint"
  private String subscriptionServiceUser = "ChuckNorris"
  private String subscriptionServicePassword = "astrongpassphrase!"
  private String tokenGenerationEndpoint = "/subscriptions/tokens"

  @Ignore
  def "when test token then token there"() {
    when: "test token"
    def underTest = new QBiCUnsubscriptionGenerator(unsubscriptionPortletUrl, subscriptionServiceUri, tokenGenerationEndpoint, subscriptionServiceUser, subscriptionServicePassword)
    def token = underTest.get("QABCD", "test@user.id")
    then: "token there"
    println token
    token == "${unsubscriptionPortletUrl}?token=PrOmH8LTbcNC7j0cTYUtHwWtiT0XB0HjvdhEaJRF1sc="
  }
}
