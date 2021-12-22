package life.qbic.business.notification.unsubscription;

import java.util.function.Supplier;

/** Supplies the caller with an unsubscription link. Based on the configuration of the instance.*/
public interface UnsubscriptionLinkSupplier extends Supplier<String> {

  /**
   * @param projectCode the project code used by this supplier
   * @return itself
   */
  UnsubscriptionLinkSupplier projectCode(String projectCode);

  /**
   * @param userId the user id used by this supplier
   * @return itself
   */
  UnsubscriptionLinkSupplier userId(String userId);

  /**
   * Gets a new unsubscription link based on the configuration of this instance
   *
   * @return a valid unsubscription url
   */
  @Override
  String get();
}
