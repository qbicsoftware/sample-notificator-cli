package life.qbic.samplenotificator.components.subscription

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
  private String baseUrl = "baseUrl"
  private String subscriptionServiceUser = "user"
  private String subscriptionServicePassword = "password"

  def "when the instance is not configured then a NullPointerException is thrown"() {
    when: "the instance is not configured"
    def underTest = new QBiCUnsubscriptionGenerator(baseUrl, subscriptionServiceUri, subscriptionServiceUser, subscriptionServicePassword)
    underTest.get()
    then: "a NullPointerException is thrown"
    thrown NullPointerException
  }
  def "when the instance only has a project code then a NullPointerException is thrown"() {
    when: "the instance is not configured"
    def underTest = new QBiCUnsubscriptionGenerator(baseUrl, subscriptionServiceUri, subscriptionServiceUser, subscriptionServicePassword)
    underTest.projectCode("Test")
    underTest.get()
    then: "a NullPointerException is thrown"
    thrown NullPointerException
  }
  def "when the instance only has a user id then a NullPointerException is thrown"() {
    when: "the instance is not configured"
    def underTest = new QBiCUnsubscriptionGenerator(baseUrl, subscriptionServiceUri, subscriptionServiceUser, subscriptionServicePassword)
    underTest.userId("test")
    underTest.get()
    then: "a NullPointerException is thrown"
    thrown NullPointerException
  }
}
