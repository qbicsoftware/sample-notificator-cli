package life.qbic.business.notification.send;

import java.util.function.Function;
import life.qbic.business.notification.create.NotificationContent;

/**
 * <p>Generates emails from notification contents.</p>
 */
public interface EmailGenerator<T extends NotificationEmail> extends Function<NotificationContent, T> {
}
