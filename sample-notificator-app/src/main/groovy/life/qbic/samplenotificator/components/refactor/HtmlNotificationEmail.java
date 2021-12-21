package life.qbic.samplenotificator.components.refactor;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;
import life.qbic.business.notification.create.NotificationContent;
import life.qbic.business.notification.refactor.NotificationEmail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class HtmlNotificationEmail implements NotificationEmail {

  private static final Supplier<InputStream> TEMPLATE_RESOURCE_STREAM_SUPPLIER =
      () ->
          requireNonNull(
              HtmlNotificationEmail.class
                  .getClassLoader()
                  .getResourceAsStream("notification-template/email-update-template.html"));

  private String recipient;
  Document document = getCopyOfTemplate();


  private static Document getCopyOfTemplate() {
    try {
      return Jsoup.parse(TEMPLATE_RESOURCE_STREAM_SUPPLIER.get(), "UTF-8", "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void fill(NotificationContent content) {
    this.recipient = content.getCustomerEmailAddress();
    fillPersonInformation(content.getCustomerFirstName(), content.getCustomerLastName());
    fillProjectInformation(content.getProjectCode(), content.getProjectTitle());
    fillSampleStatusInformation(content.getAvailableDataCount(), content.getFailedQCCount());
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
      document.getElementById("sample-status-failed-qc").remove();
    }
    if (availableDataCount == 0) {
      document.getElementById("sample-status-available-data").remove();
    }
    fillIfPresent("sample-status-failed-qc-count", String.valueOf(failedQcCount));
    fillIfPresent("sample-status-available-data-count", String.valueOf(availableDataCount));
  }

  private void fillIfPresent(String cssId, String text) {
    Optional.ofNullable(document.getElementById(cssId))
        .ifPresent(element -> element.empty().text(text));
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
    return body().equals(that.body());
  }

  @Override
  public int hashCode() {
    int result = recipient().hashCode();
    result = 31 * result + body().hashCode();
    return result;
  }
}
