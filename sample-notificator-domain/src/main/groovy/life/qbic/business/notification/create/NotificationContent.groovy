package life.qbic.business.notification.create

import groovy.transform.EqualsAndHashCode

/**
 * <h1>A DTO containing the fields required in the email</h1>
 *
 * <p>The content of this class is based on all the fields that need be part of the final email send to the customer</p>
 *
 * @since 1.0.0*
 */

@EqualsAndHashCode
class NotificationContent {

    /*Customer Information */
    final String customerFirstName
    final String customerLastName
    final String customerEmailAddress
    final String userId

    /*Project Information*/
    final String projectTitle
    final String projectCode
    final int failedQCCount
    final int availableDataCount

    protected NotificationContent(Builder builder) {
        /*Customer Information */
        this.userId = builder.userId
        this.customerFirstName = builder.customerFirstName
        this.customerLastName = builder.customerLastName
        this.customerEmailAddress = builder.customerEmailAddress
        /*Project Information*/
        this.projectTitle = builder.projectTitle
        this.projectCode = builder.projectCode
        this.failedQCCount = builder.failedQCCount
        this.availableDataCount = builder.availableDataCount
    }

    static class Builder {
        /*Person Information */
        String customerFirstName
        String customerLastName
        String customerEmailAddress
        String userId
        /*Project Information*/
        String projectTitle
        String projectCode
        int failedQCCount
        int availableDataCount

        Builder(String userId, String customerFirstName, String customerLastName, String customerEmailAddress, String projectTitle, String projectCode, int failedQCCount, int availableDataCount) {

            /*Customer Information */
            this.userId = Objects.requireNonNull(userId)
            this.customerFirstName = Objects.requireNonNull(customerFirstName, "First name of customer must not be null")
            this.customerLastName = Objects.requireNonNull(customerLastName, "Last name of customer must not be null")
            this.customerEmailAddress = Objects.requireNonNull(customerEmailAddress, "Email address of customer must not be null")

            /*Project Information*/
            this.projectTitle = projectTitle ?: ""
            this.projectCode = Objects.requireNonNull(projectCode, "Project code must not be null")
            this.failedQCCount = Objects.requireNonNull(failedQCCount, "Count of samples with failed QC must not be null")
            this.availableDataCount = Objects.requireNonNull(availableDataCount, "Count of samples with available data must not be null")
        }

        NotificationContent build() {
            return new NotificationContent(this)
        }

    }

    @Override
    String toString() {
        return "NotificationContent{" +
                "customerFirstName='" + customerFirstName + '\'' +
                ", customerLastName='" + customerLastName + '\'' +
                ", customerEmailAddress='" + customerEmailAddress + '\'' +
                ", userId='" + userId + '\'' +
                ", projectTitle='" + projectTitle + '\'' +
                ", projectCode='" + projectCode + '\'' +
                ", failedQCCount=" + failedQCCount +
                ", availableDataCount=" + availableDataCount +
                '}'
    }

}
