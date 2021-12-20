package life.qbic.business.notification.refactor;

import java.util.List;
import java.util.function.Consumer;

/**
 * <p>An email sender. Accepted emails are sent or, in case of failing to send them,
 * are stored for later retrieval.</p>
 * @param <T> the type of email to be sent
 */
public interface EmailSender<T extends NotificationEmail> extends Consumer<T>  {

  /**
   * Returns all emails where an attempt to send failed.
   * @return a list of emails that were attempted to be sent but were not sent in the end
   */
  List<T> notSent();
}
