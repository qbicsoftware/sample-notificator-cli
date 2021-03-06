package life.qbic.business.subscription.fetch

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.subscription.Subscriber
import life.qbic.datamodel.samples.Status

import java.time.LocalDate

/**
 * <h1>Use case for fetching subscriber information from the database</h1>
 *
 * <p>Fetches the subscribers for whose subscriptions had updates on a given date</p>
 *
 * @since 1.0.0*
 */
class FetchSubscriber implements FetchSubscriberInput {
    private final FetchSubscriberDataSource dataSource
    private final FetchSubscriberOutput output

    private Map<String, Status> sampleToStatus
    private Set<Subscriber> foundSubscribers = new HashSet<>()
    private Set<String> updatedProjects
    private static final Logging log = Logger.getLogger(FetchSubscriber.class)

    FetchSubscriber(FetchSubscriberDataSource dataSource, FetchSubscriberOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void fetchSubscriber(String date) {
        LocalDate localDate = LocalDate.parse(date)
        try {
            //1. get todays notifications
            sampleToStatus = dataSource.getUpdatedSamplesForDay(localDate)
            //2. get subscribers for projects
            updatedProjects = getProjectsFromSamples(sampleToStatus.keySet().asList())
            //3. create subscribers and associate them with their subscriptions
            List subscribers = getSubscribersWithSubscriptions()
            output.fetchedSubscribers(subscribers)
        } catch (DatabaseQueryException databaseQueryException) {
            output.failNotification("An error occurred while trying to query the database during Subscriber retrieval for ${date}")
            log.error(databaseQueryException.message)
            log.error(databaseQueryException.stackTrace.join("\n"))
        }
        catch (Exception e) {
            output.failNotification("An error occurred while trying to fetch Subscribers for ${date}")
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
        }
    }

    private static Set<String> getProjectsFromSamples(List<String> samples) {
        Set<String> projects = new HashSet<>()
        samples.each { sample ->
            projects << sample.substring(0, 5)
        }
        return projects
    }

    /**
     * Collects all updated samples (from all projects) for the projects a user has subscribed to.
     * @return a list of subscribers with subscriptions included
     */
    private List<Subscriber> getSubscribersWithSubscriptions() {
        List<Subscriber> subscribers = []
        Map<String, Map<String, Status>> userIdToSamples = mapUsersToUpdatedSamples()

        userIdToSamples.each { id, updatedSamples ->
            Subscriber subscriber = foundSubscribers.find({it.email == id})
            subscribers << createSubscriberWithSubscriptions(subscriber,updatedSamples)
        }

        return subscribers
    }

    /**
     * Maps the projects and the updated samples to the subscribers (their id)
     * @return a map, mapping the subscriber id (email) to all updated samples of their subscribed project(s)
     */
    private Map mapUsersToUpdatedSamples(){
        Map<String, Map<String, Status>> userIdToSamples = new HashMap<>()

        updatedProjects.each { project ->
            Map samplesForProject = getSamplesForProject(project)
            List subscribersForProject = dataSource.getSubscriberForProject(project)
            //store the subscriber information for later
            foundSubscribers.addAll(subscribersForProject)

            subscribersForProject.each { subscriber ->
                final var subscriberUserId = subscriber.getUserId()
                if (userIdToSamples.containsKey(subscriberUserId)) {
                    //add new samples to existing subscriber
                    userIdToSamples.get(subscriberUserId).putAll(samplesForProject)
                } else {
                    //add new subscriber
                    userIdToSamples.put(subscriberUserId, samplesForProject)
                }
            }
        }

        return userIdToSamples
    }

    private static Subscriber createSubscriberWithSubscriptions(Subscriber subscriber, Map<String, Status> subscribedSamples) {
        return new Subscriber(subscriber.userId, subscriber.firstName, subscriber.lastName, subscriber.email, subscribedSamples)
    }

    private Map<String, Status> getSamplesForProject(String project) {
        return sampleToStatus.findAll { it.key.startsWith(project) }
    }

}
