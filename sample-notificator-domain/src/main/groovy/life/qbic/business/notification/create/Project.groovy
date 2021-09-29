package life.qbic.business.notification.create

import groovy.transform.EqualsAndHashCode

/**
 * <b>A small object to represent projects</b>
 *
 * <p>A project is represented by a QBiC code, a title and a list of sample codes</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode (includes = "code")
class Project {

    String code = ""
    String title = ""
    List<String> sampleCodes = []


}