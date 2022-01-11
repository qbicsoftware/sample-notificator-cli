package life.qbic.samplenotificator.components.subscription

import spock.lang.Ignore
import spock.lang.Specification

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
class QBiCUnsubscriptionGeneratorSpec extends Specification {

  private String subscriptionServiceUri = "test"
  private String baseUrl = "http://www.test.services.qbic/endpoint/"
  private String subscriptionServiceUser = "user"
  private String subscriptionServicePassword = "password"
  private String tokenGenerationEndpoint = "/subscriptions/cancel/token/generate"

  def "when the instance is not configured then a NullPointerException is thrown"() {
    when: "the instance is not configured"
    def underTest = new QBiCUnsubscriptionGenerator(baseUrl, subscriptionServiceUri, tokenGenerationEndpoint, subscriptionServiceUser, subscriptionServicePassword)
    underTest.get()
    then: "a NullPointerException is thrown"
    thrown NullPointerException
  }
  def "when the instance only has a project code then a NullPointerException is thrown"() {
    when: "the instance is not configured"
    def underTest = new QBiCUnsubscriptionGenerator(baseUrl, subscriptionServiceUri, tokenGenerationEndpoint, subscriptionServiceUser, subscriptionServicePassword)
    underTest.projectCode("Test")
    underTest.get()
    then: "a NullPointerException is thrown"
    thrown NullPointerException
  }
  def "when the instance only has a user id then a NullPointerException is thrown"() {
    when: "the instance is not configured"
    def underTest = new QBiCUnsubscriptionGenerator(baseUrl, subscriptionServiceUri, tokenGenerationEndpoint, subscriptionServiceUser, subscriptionServicePassword)
    underTest.userId("test")
    underTest.get()
    then: "a NullPointerException is thrown"
    thrown NullPointerException
  }

  @Ignore
  def "when test token then token there"() {
    when: "test token"
    def underTest = new QBiCUnsubscriptionGenerator(baseUrl, "http://localhost:8080", tokenGenerationEndpoint, "ChuckNorris", "astrongpassphrase!")
    underTest.userId("test@user.id").projectCode("QABCD")
    def token = underTest.get()
    then: "token there"
    println token
    token == "$baseUrl?token=PrOmH8LTbcNC7j0cTYUtHwWtiT0XB0HjvdhEaJRF1sc="
  }
}
