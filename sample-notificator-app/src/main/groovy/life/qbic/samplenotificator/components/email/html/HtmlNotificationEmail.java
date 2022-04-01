package life.qbic.samplenotificator.components.email.html;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;
import life.qbic.business.notification.create.NotificationContent;
import life.qbic.business.notification.send.NotificationEmail;
import life.qbic.business.notification.unsubscription.UnsubscriptionLinkSupplier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;


/**
 * An email containing information of a NotificationContent formatted using a http template
 */
public class HtmlNotificationEmail implements NotificationEmail {

  private static final Supplier<InputStream> TEMPLATE_RESOURCE_STREAM_SUPPLIER =
      () ->
          requireNonNull(
              HtmlNotificationEmail.class
                  .getClassLoader()
                  .getResourceAsStream("notification-template/email-update-template.html"));
  private final UnsubscriptionLinkSupplier unsubscriptionLinkSupplier;

  private final Document document = getCopyOfTemplate();
  private String recipient;
  private String subject;

  public HtmlNotificationEmail(
      UnsubscriptionLinkSupplier unsubscriptionLinkSupplier) {
    this.unsubscriptionLinkSupplier = unsubscriptionLinkSupplier;
  }


  @Override
  public void fill(NotificationContent content) {
    requireNonNull(unsubscriptionLinkSupplier, "No unsubscription link supplier set.");
    this.recipient = content.getCustomerEmailAddress();
    this.subject = formatSubject(content.getProjectCode());
    fillPersonInformation(content.getCustomerFirstName(), content.getCustomerLastName());
    fillProjectInformation(content.getProjectCode(), content.getProjectTitle());
    fillSampleStatusInformation(content.getAvailableDataCount(), content.getFailedQCCount());
    fillUnsubscriptionLink(content.getProjectCode(), content.getUserId());
  }

  @Override
  public String body() {
    return document.html();
  }

  @Override
  public String recipient() {
    requireNonNull(this.recipient);
    return recipient;
  }

  @Override
  public String subject() {
    requireNonNull(this.subject);
    return subject;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HtmlNotificationEmail)) {
      return false;
    }

    HtmlNotificationEmail that = (HtmlNotificationEmail) o;

    if (!recipient().equals(that.recipient())) {
      return false;
    }
    if (!subject().equals(that.subject())) {
      return false;
    }
    return body().equals(that.body());
  }

  @Override
  public int hashCode() {
    int result = recipient().hashCode();
    result = 31 * result + body().hashCode();
    result = 31 * result + subject().hashCode();
    return result;
  }

  private String formatSubject(String projectCode) {
    requireNonNull(projectCode);
    return String.format("Samples updated in project %s", projectCode);
  }

  private void fillUnsubscriptionLink(String projectCode, String userId) {
    String unsubscriptionUrl = unsubscriptionLinkSupplier.get(projectCode, userId);
    setUnsubscriptionLinkLocation(unsubscriptionUrl);
  }

  private void setUnsubscriptionLinkLocation(String href) {
    requireNonNull(href);

    Optional.ofNullable(document.getElementById("unsubscription-link"))
        .ifPresent(element -> element.attr("href", href));
  }

  private void fillPersonInformation(String firstName, String lastName) {
    requireNonNull(firstName);
    requireNonNull(lastName);

    String customerName = String.format("%s %s", firstName, lastName);
    fillIfPresent("person-information", customerName);
  }

  private void fillProjectInformation(String projectCode, String projectTitle) {
    requireNonNull(projectCode);
    requireNonNull(projectTitle);

    fillIfPresent("project-title", projectTitle);
    fillIfPresent("project-code", projectCode);
  }

  private void fillSampleStatusInformation(int availableDataCount, int failedQcCount) {
    if (failedQcCount == 0) {
      removeIfPresent("sample-status-failed-qc");
    }
    if (availableDataCount == 0) {
      removeIfPresent("sample-status-available-data");
    }
    fillIfPresent("sample-status-failed-qc-count", String.valueOf(failedQcCount));
    fillIfPresent("sample-status-available-data-count", String.valueOf(availableDataCount));
  }

  private void removeIfPresent(String cssId) {
    Optional.ofNullable(document.getElementById(cssId)).ifPresent(Node::remove);
  }

  private void fillIfPresent(String cssId, String text) {
    Optional.ofNullable(document.getElementById(cssId))
        .ifPresent(element -> element.empty().text(text));
  }

  private static Document getCopyOfTemplate() {
    try {
      return Jsoup.parse(TEMPLATE_RESOURCE_STREAM_SUPPLIER.get(), "UTF-8", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
