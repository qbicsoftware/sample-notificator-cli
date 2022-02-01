package life.qbic.samplenotificator.components.email.html;

/**
 * <p>A generator for sendmail headers</p>
 */
public class SendmailHeaderGenerator {
  final static String EMAIL_SENDER = "noreply@qbic.life";
  final static String CONTENT_TYPE = "text/html";

  /**
   * Generates a formatted sendmail header multi-line string.
   *
   * <p>The header contains default configuration on email sender and content type as well as the
   * subject provided to this method.</p>
   *
   * <code>generateFromSubject("my test subject")</code> will generate
   *
   * <pre>
   * From: {@link #EMAIL_SENDER}
   * Subject: my test subject
   * Content-Type: {@link #CONTENT_TYPE}
   * </pre>
   *
   * @param subject the subject of the email
   * @return a configured sendmail header with default configuration and the subject set.
   */
  public static String generateFromSubject(String subject) {
    if (subject == null) {
      throw new IllegalArgumentException(
          "Cannot generate sendmail header. Please provide a subject.");
    }

    return "From: "
        + EMAIL_SENDER
        + System.lineSeparator()
        + "Subject: "
        + subject
        + System.lineSeparator()
        + "Content-Type: "
        + CONTENT_TYPE
        + System.lineSeparator();
  }
}
