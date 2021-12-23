package life.qbic.samplenotificator.components.subscription;

import static java.util.Objects.requireNonNull;

import life.qbic.business.notification.unsubscription.UnsubscriptionLinkSupplier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class QBiCUnsubscriptionGenerator implements UnsubscriptionLinkSupplier {
  private String projectCode;
  private String userId;

  private final String unsubscriptionBaseUri;
  private final String subscriptionServiceUri;
  private final String tokenGenerationEndpoint;
  private final String subscriptionServiceUser;
  private final String subscriptionServicePassword;

  public QBiCUnsubscriptionGenerator(
      String unsubscriptionBaseUri,
      String subscriptionServiceUri,
      String tokenGenerationEndpoint, String subscriptionServiceUser,
      String subscriptionServicePassword) {
    requireNonNull(subscriptionServicePassword);
    requireNonNull(subscriptionServiceUri);
    requireNonNull(subscriptionServiceUser);
    requireNonNull(tokenGenerationEndpoint);
    requireNonNull(unsubscriptionBaseUri);
    this.subscriptionServicePassword = subscriptionServicePassword;
    this.subscriptionServiceUri = subscriptionServiceUri;
    this.subscriptionServiceUser = subscriptionServiceUser;
    this.tokenGenerationEndpoint = tokenGenerationEndpoint;
    this.unsubscriptionBaseUri = unsubscriptionBaseUri;
  }

  @Override
  public UnsubscriptionLinkSupplier projectCode(String projectCode) {
    requireNonNull(projectCode);
    this.projectCode = projectCode;
    return this;
  }

  @Override
  public UnsubscriptionLinkSupplier userId(String userId) {
    requireNonNull(userId);
    this.userId = userId;
    return this;
  }

  @Override
  public String get() {
    requireNonNull(projectCode);
    requireNonNull(userId);
    String token = getToken(projectCode, userId);
    return getLink(token);
  }

  private String getToken(String projectCode, String userId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBasicAuth(subscriptionServiceUser, subscriptionServicePassword);

    String requestBody =
        String.format("{\"project\":\"%s\",\"userId\":\"%s\"}", projectCode, userId);
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

    String endpoint = subscriptionServiceUri + tokenGenerationEndpoint;
    ResponseEntity<String> response = new RestTemplate().exchange(endpoint,
        HttpMethod.POST, request, String.class);

    if (response.hasBody() && response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }
    return null;
  }

  private String getLink(String token) {
    return unsubscriptionBaseUri + "?token=" + token;
  }
}
