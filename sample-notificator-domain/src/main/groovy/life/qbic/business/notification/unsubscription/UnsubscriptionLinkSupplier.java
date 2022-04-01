package life.qbic.business.notification.unsubscription;

/** Supplies the caller with an unsubscription link. Based on the configuration of the instance.*/
@FunctionalInterface
public interface UnsubscriptionLinkSupplier {

  /**
   * Gets a new unsubscription link for the project and email combination provided
   * @param project the project for which the unsubscription link should be generated
   * @param userId the user_id for the person for which this unsubscription link is generated
   * @return a valid unsubscription url
   */
  String get(String project, String userId);
}
