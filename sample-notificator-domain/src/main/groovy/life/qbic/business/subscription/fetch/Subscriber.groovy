package life.qbic.business.subscription.fetch

import life.qbic.datamodel.samples.Status


/**
 * <p>Creates Subscribers from </p>
 *
 * @since 1.0.0
 *
 */
class Subscriber {
    String firstName
    String lastName
    String email
    /**
     * Mapping sample codes to its new sample status
     */
    final Map<String, Status> subscriptions

    Subscriber(String firstName, String lastName, String email){
        this.firstName = Objects.requireNonNull(firstName, "First name must not be null")
        this.lastName = Objects.requireNonNull(lastName, "Last name must not be null")
        this.email = Objects.requireNonNull(email, "Email must not be null")
        this.subscriptions = new HashMap<>()
    }


    @Override
    String toString() {
        return  firstName + ' ' + lastName + ', ' + email + ', subscriptions: ' + subscriptions.toString()
    }

    /**
     * Creates an immutable subscriber DTO
     * @return
     */
    life.qbic.business.subscription.Subscriber toSubscriberDTO(){
        return new life.qbic.business.subscription.Subscriber(this.firstName,this.lastName,this.email,this.subscriptions)
    }

    static Subscriber convertToPOJOSubscriber(life.qbic.business.subscription.Subscriber subscriber){
        return new Subscriber(subscriber.firstName, subscriber.lastName, subscriber.email)
    }
}