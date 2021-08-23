package life.qbic.business.subscription.fetch


import life.qbic.business.subscription.Subscriber
import life.qbic.datamodel.samples.Status

import java.time.LocalDate

/**
 * <h1>Use case for fetching subscriber information from the database</h1>
 *
 * <p>Fetches the subscribers for whos subscriptions had updates on e.g a given date</p>
 *
 * @since 1.0.0
 *
*/
class FetchSubscriber implements FetchSubscriberInput{
    private final FetchSubscriberDataSource ds
    private final FetchSubscriberOutput output

    private Map<String, Status> sampleToStatus

    FetchSubscriber(FetchSubscriberDataSource ds, FetchSubscriberOutput output){
        this.ds = ds
        this.output = output
    }

    @Override
    void fetchSubscriber(String date) {
        LocalDate localDate = LocalDate.parse(date)
        try{
            //1. get todays notifications
           sampleToStatus = ds.getUpdatedSamplesForDay(localDate)

            Set updatedProjects = getProjectsFromSamples(sampleToStatus.keySet().asList())

            Map<String, List<Subscriber>> projectToSubscribers = new HashMap<>()
            updatedProjects.each {project ->
                projectToSubscribers.put(project, ds.getSubscriberForProject(project))
            }
            List<Subscriber> subscribers = []
            getSubscribersFromProjects(projectToSubscribers).each {subscribers << it.toSubscriberDTO()}
            output.fetchedSubscribers(subscribers)
        } catch(Exception e){
            output.failNotification("An error occurred while fetching subscribers for updated projects")
            output.failNotification(e.message)
        }
    }

    private static Set<String> getProjectsFromSamples(List<String> samples){
        Set<String> projects = new HashSet<>()
        samples.each {sample ->
            projects << sample.substring(0,4)
        }
        return projects
    }

    private List<life.qbic.business.subscription.fetch.Subscriber> getSubscribersFromProjects(Map<String, List<Subscriber>> projectsToSubscribers){
        List<life.qbic.business.subscription.fetch.Subscriber> subscriberList = []

        projectsToSubscribers.each { projectCode, subscribers ->
            Map samplesForProject = getSamplesForProject(projectCode)
            subscribers.each {subscriber ->
                life.qbic.business.subscription.fetch.Subscriber foundSubscriber = subscriberList.find({it.email == subscriber.email})
                if(foundSubscriber){
                    foundSubscriber.subscriptions.putAll(samplesForProject)
                }else {
                    life.qbic.business.subscription.fetch.Subscriber newSubscriber = life.qbic.business.subscription.fetch.Subscriber.convertToPOJOSubscriber(subscriber)
                    newSubscriber.subscriptions.putAll(samplesForProject)
                    subscriberList << newSubscriber
                }
            }
        }
        return subscriberList
    }

    private Map<String, Status> getSamplesForProject(String project){
        return sampleToStatus.findAll {it.key.startsWith(project)}
    }

}