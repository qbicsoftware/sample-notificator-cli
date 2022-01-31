package life.qbic.samplenotificator.components.email.html;

import life.qbic.business.notification.send.NotificationEmail;

/**
 * <p>A representation of the email header necessary for the sendmail command</p>
 */
public class SendmailHeader {
  String from = "noreply@qbic.life";
  String subject = "Sample Status Update";
  String contentType = "text/html";

  static SendmailHeader forNotificationEmail(NotificationEmail notificationEmail) {
    SendmailHeader sendmailHeader = new SendmailHeader();
    sendmailHeader.subject = notificationEmail.subject();
    return sendmailHeader;
  }

  //  From: noreply@qbic.life.com
  //  Subject: Sample Status Update
  //  Content-Type: text/html

  /**
   * Creates a multi-line String with the values of this header.
   *
   * <pre>
   *   From: noreply@qbic.life.com
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
