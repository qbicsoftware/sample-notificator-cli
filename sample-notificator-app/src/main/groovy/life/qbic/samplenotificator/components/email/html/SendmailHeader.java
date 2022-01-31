package life.qbic.samplenotificator.components.email.html;

/**
 * <p>A representation of the email header necessary for the sendmail command</p>
 */
public class SendmailHeader {
  String from = "noreply@qbic.life";
  String subject = "Sample Status Update";
  String contentType = "text/html";

  SendmailHeader withSubject(String subject) {
    this.subject = subject;
    return this;
  }
  /**
   * Creates a multi-line String with the values of this header. For example:
   *
   * <pre>
   *   From: noreply@qbic.life
   *   Subject: Sample Status Update
   *   Content-Type: text/html
   * </pre>
   *
   * @return the formatted output
   */
  public String format() {
    return "From: "
        + from
        + System.lineSeparator()
        + "Subject: "
        + subject
        + System.lineSeparator()
        + "Content-Type: "
        + contentType
        + System.lineSeparator();
  }
}
