package life.qbic.business.subscription

import life.qbic.business.notification.create.NotificationContent
import spock.lang.Shared
import spock.lang.Specification

/**
 * <h1>Tests for the Notification Content DTO</h1>
 *
 * @since 1.0.0
 */
class NotificationContentSpec extends Specification {
    @Shared
    String customerFirstName
    @Shared
    String customerLastName
    @Shared
    String customerTitle
    @Shared
    String projectTitle
    @Shared
    String projectCode
    @Shared
    int failedQCCount
    @Shared
    int availableDataCount

    def "NotificationContent is created successfully"() {
        when: "a NotificationContent is created"
        new NotificationContent.Builder(customerFirstName, customerLastName, customerTitle, projectTitle, projectCode, failedQCCount, availableDataCount).build()
        then: "no error is thrown"
        noExceptionThrown()
        where:
        customerFirstName | customerLastName | customerTitle | projectTitle              | projectCode | failedQCCount | availableDataCount
        "John"            | "Do"             | "Dr."         | "My awesome project"      | "ABCDE"     | 10            | 1000
        "Jane"            | "Doe"            | "Prof."       | "another awesome project" | "FGHIJ"     | 0             | 100
        "Janet"           | "Done"           | ""            | "third time's the charm"  | "KLMNOP"    | 1             | 10
    }

    def "NotificationContents with the same content are equal"() {
        when: "two NotificationContent are created with the same content"
        NotificationContent NotificationContent = new NotificationContent.Builder(customerFirstName, customerLastName, customerTitle, projectTitle, projectCode, failedQCCount, availableDataCount).build()
        NotificationContent sameNotificationContent = new NotificationContent.Builder(customerFirstName, customerLastName, customerTitle, projectTitle, projectCode, failedQCCount, availableDataCount).build()

        then: "NotificationContents are the same"
        NotificationContent.equals(sameNotificationContent)
        where:
        customerFirstName | customerLastName | customerTitle | projectTitle              | projectCode | failedQCCount | availableDataCount
        "John"            | "Do"             | "Dr."         | "My awesome project"      | "ABCDE"     | 10            | 1000
        "Jane"            | "Doe"            | "Prof."       | "another awesome project" | "FGHIJ"     | 0             | 100
        "Janet"           | "Done"           | ""            | "third time's the charm"  | "KLMNOP"    | 1             | 10
    }

    def "NotificationContents with the different content are different"() {
        when: "two NotificationContent are created with different content"
        NotificationContent NotificationContent = new NotificationContent.Builder(customerFirstName, customerLastName, customerTitle, projectTitle, projectCode, failedQCCount, availableDataCount).build()
        NotificationContent differentNotificationContent = new NotificationContent.Builder("NotJohn", "NotDo", "NotProf", "This project should not exist", "WrongCode", 1234, 0).build()

        then: "NotificationContents are the different"
        !NotificationContent.equals(differentNotificationContent)
        where:
        customerFirstName | customerLastName | customerTitle | projectTitle              | projectCode | failedQCCount | availableDataCount
        "John"            | "Do"             | "Dr."         | "My awesome project"      | "ABCDE"     | 10            | 1000
        "Jane"            | "Doe"            | "Prof."       | "another awesome project" | "FGHIJ"     | 0             | 100
        "Janet"           | "Done"           | ""            | "third time's the charm"  | "KLMNOP"    | 1             | 10
    }
}