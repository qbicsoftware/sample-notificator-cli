package life.qbic.business.notification.refactor;

/**
 * <p>Sends a failure email to the administrator.</p>
 */
public interface FailureEmailSender {

  /**
   * Sends an email informing of system failure.
   */
  void sendFailure();
}
