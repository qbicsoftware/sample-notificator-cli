package life.qbic.business.subscription

import life.qbic.datamodel.samples.Status

/**
 * <h1>A person that subscribed to the notification table</h1>
 *
 * <p>A subscriber is described by his full name and an email address</p>
 *
 * @since 1.0.0
 *
*/
class Subscriber {
    final String firstName
    final String lastName
    final String email
    /**
     * Mapping sample codes to its new sample status
     */
    final Map<String, Status> subscriptions

    Subscriber(String firstName, String lastName, String email, Map<String,Status> subscriptions){
        this.firstName = Objects.requireNonNull(firstName, "First name must not be null")
        this.lastName = Objects.requireNonNull(lastName, "Last name must not be null")
        this.email = Objects.requireNonNull(email, "Email must not be null")
        this.subscriptions = Objects.requireNonNull(subscriptions, "The list of subscriptions must not be null")
    }


    @Override
    public String toString() {
        return  firstName + ' ' + lastName + ', ' + email + ', subscriptions: ' + subscriptions.toString()
    }
}