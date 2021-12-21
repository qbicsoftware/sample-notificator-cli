package life.qbic.samplenotificator.components.refactor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import life.qbic.business.logging.Logger;
import life.qbic.business.logging.Logging;
import life.qbic.business.notification.refactor.EmailSender;

/**
 * <b>Sends emails formatted as Html and sends failure emails as well.</b>
 */
public class HtmlEmailSender implements EmailSender<HtmlNotificationEmail> {
  private static final Logging log = Logger.getLogger(HtmlEmailSender.class);
  private static final String SENDMAIL_HEADER =
      readInputStream(
          HtmlEmailSender.class
              .getClassLoader()
              .getResourceAsStream("notification-template/header.txt"));
  private final List<HtmlNotificationEmail> unsentEmails = new ArrayList<>();

  @Override
  public void accept(HtmlNotificationEmail notificationEmail) {
    try {
      sendNotificationEmail(notificationEmail);
    } catch (EmailSendException sendException) {
      unsentEmails.add(notificationEmail);
    }
  }

  @Override
  public List<HtmlNotificationEmail> notSent() {
    return Collections.unmodifiableList(unsentEmails);
  }



  private void sendNotificationEmail(HtmlNotificationEmail notificationEmail)
      throws EmailSendException {
    try {
      File emailFile = getSendmailFile(notificationEmail.body());
      sendSendmailEmail(emailFile, notificationEmail.recipient());
    } catch (IOException e) {
      throw new EmailSendException();
    }
  }

  private void sendSendmailEmail(File emailFile, String recipient) throws EmailSendException {
    try {
      ProcessBuilder builder =
          new ProcessBuilder("sendmail", "-t", recipient).redirectInput(emailFile)
              .redirectErrorStream(true);
      Process process = builder.start();
      log.info("Trying to send update mail with the following settings: " + builder.command());
      process.waitFor(10, TimeUnit.SECONDS);
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
      throw new EmailSendException();
    }
  }

  private File getSendmailFile(String content) throws IOException {
    File sendmailFile = File.createTempFile("HtmlEmail", ".html");
    try (FileWriter fileWriter = new FileWriter(sendmailFile, true)) {
      fileWriter.append(SENDMAIL_HEADER);
      fileWriter.append(content);
      fileWriter.flush();
    }
    return sendmailFile;
  }

  private static String readInputStream(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream))
        .lines()
        .collect(Collectors.joining("\n"));
  }
}
