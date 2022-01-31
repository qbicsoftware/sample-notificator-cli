package life.qbic.samplenotificator.components.email.html;

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
import life.qbic.business.notification.send.EmailSender;
import life.qbic.samplenotificator.components.email.EmailSendException;

/**
 * <b>Sends emails formatted as Html and sends failure emails as well.</b>
 */
public class HtmlEmailSender implements EmailSender<HtmlNotificationEmail> {
  private static final Logging log = Logger.getLogger(HtmlEmailSender.class);
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
      File emailFile = getSendmailFile(notificationEmail);
      sendSendmailEmail(emailFile, notificationEmail.recipient());
    } catch (IOException | NullPointerException e) {
      log.error(e.getMessage(), e);
      throw new EmailSendException();
    }
  }

  protected void sendSendmailEmail(File emailFile, String recipient) throws EmailSendException {
    try {
      ProcessBuilder builder =
          new ProcessBuilder("sendmail", "-t", recipient).redirectInput(emailFile)
              .redirectErrorStream(true);
      Process process = builder.start();
      log.info("Trying to send update mail with the following settings: " + builder.command());
      process.waitFor(10, TimeUnit.SECONDS);
      logOutput(process);
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
      throw new EmailSendException();
    }
  }

  private void logOutput(Process process) {
    String output = getOutput(process.getInputStream());
    if (process.exitValue() > 0) {
      log.error(output);
    } else {
      log.info(output);
    }
  }

  private static String getOutput(InputStream inputStream) {
    try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File getSendmailFile(HtmlNotificationEmail notificationEmail) throws IOException {
    File sendmailFile = File.createTempFile("HtmlEmail", ".html");
    SendmailHeader sendmailHeader = SendmailHeader.forNotificationEmail(notificationEmail);
    try (FileWriter fileWriter = new FileWriter(sendmailFile, true)) {
      fileWriter.append(sendmailHeader.format());
      fileWriter.append(notificationEmail.body());
      fileWriter.flush();
    }
    return sendmailFile;
  }

}
