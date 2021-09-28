package life.qbic.business.notification.create

import groovy.transform.EqualsAndHashCode

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
@EqualsAndHashCode (includes = "code")
class Project {

    String code
    String title
    List<String> sampleCodes


}