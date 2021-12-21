package life.qbic.samplenotificator.components.refactor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import life.qbic.business.logging.Logger;
import life.qbic.business.logging.Logging;
import life.qbic.business.notification.refactor.FailureEmailSender;

/** Sends emails to the QBiC support */
public class SupportEmailSender implements FailureEmailSender {
  private static final Logging log = Logger.getLogger(SupportEmailSender.class);
  private static final String FAILURE_EMAIL_CONTENT =
      readInputStream(
          HtmlEmailSender.class
              .getClassLoader()
              .getResourceAsStream("notification-template/failureEmail.txt"));
  private static final String SUPPORT_EMAIL = "support@qbic.zendesk.com";

  @Override
  public void sendFailure() {
    try {
      File emailFile = getEmailFile();
      sendPlainEmail(emailFile, "Failure Notification sample-notificator-cli");
    } catch (IOException e) {
      log.error("Could not inform admin.", e);
    }
  }

  private File getEmailFile() throws IOException {
    File sendmailFile = File.createTempFile("HtmlEmail", ".html");
    try (FileWriter fileWriter = new FileWriter(sendmailFile, true)) {
      fileWriter.append(SupportEmailSender.FAILURE_EMAIL_CONTENT);
      fileWriter.flush();
    }
    return sendmailFile;
  }

  private void sendPlainEmail(File emailFile, String subject) throws EmailSendException {
    try {
      ProcessBuilder builder =
          new ProcessBuilder("mail", "-s", subject, SupportEmailSender.SUPPORT_EMAIL)
              .redirectInput(emailFile)
              .redirectErrorStream(true);
      Process process = builder.start();
      log.info(
          "Trying to notify sysadmin via Email about failure with the following settings: "
              + builder.command());
      process.waitFor(10, TimeUnit.SECONDS);
    } catch (IOException | InterruptedException e) {
      throw new EmailSendException();
    }
  }

  private static String readInputStream(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream))
        .lines()
        .collect(Collectors.joining("\n"));
  }
}
