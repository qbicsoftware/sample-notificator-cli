package life.qbic.business.notification.create


import life.qbic.business.subscription.Subscriber

import life.qbic.datamodel.samples.Status
import java.time.LocalDate
import java.util.function.Predicate


/**
 * This class implements the logic to create template email messages.
 *
 * The template message generated in this class inform a subscriber
 * about changes in the sample status for her projects after a provided date.
 *
 * @since: 1.0.0
 */

class CreateNotification implements CreateNotificationInput {
  
    private final CreateNotificationOutput output
    private final FetchUpdatedSamplesDataSource dataSource
    private Map<String, Status> updatedSamplesWithStatus

    CreateNotification(FetchUpdatedSamplesDataSource dataSource, CreateNotificationOutput output) {
        this.output = output
        this.dataSource = dataSource
    }

    @Override
    void createNotifications(String date) {
        LocalDate localDate = LocalDate.parse(date)
        List<NotificationContent> notifications = []

      try {
          updatedSamplesWithStatus = dataSource.getUpdatedSamplesForDay(localDate)

          Map<String,List<String>> projectsWithSamples = getProjectsWithSamples(updatedSamplesWithStatus.keySet().asList())

          projectsWithSamples.each {projectCode, samples -> notifications.addAll(createNotificationForProject(projectCode,samples))}

          output.createdNotifications(notifications)    
      } catch (Exception e) {
          output.failNotification("An error occurred while fetching updates for projects")
          output.failNotification(e.message)
      }
    }

    private List<NotificationContent> createNotificationForProject(String projectCode, List samples){
        List<NotificationContent> notifications = []
        //3. get project names
        Map<String,String> projectsWithTitles = dataSource.fetchProjectsWithTitles()

        int failedQCCount = filterSamplesByStatus(samples, updatedSamplesWithStatus, "SAMPLE_QC_FAIL").size()
        int availableDataCount = filterSamplesByStatus(samples, updatedSamplesWithStatus, "DATA_AVAILABLE").size()
        String title = projectsWithTitles.get(projectCode)

        //4. get subscribers of this projects
        List<Subscriber> subscribers = dataSource.getSubscriberForProject(projectCode)

        for(Subscriber subscriber : subscribers) {
            notifications << new NotificationContent.Builder(subscriber.firstName, subscriber.lastName, subscriber.email,
                    title, projectCode, failedQCCount, availableDataCount).build()
        }

        return notifications
    }
    
    private List<String> filterSamplesByStatus(List<String> samples, Map<String, Status> sampleToStatus, String statusName) {

        Predicate isOfStatus = code -> sampleToStatus.get(code).toString() == statusName
        List<String> filteredCodes = samples.stream().filter(isOfStatus).collect().toList()

        return filteredCodes
    }
    
    private Map<String, List<String>> getProjectsWithSamples(List<String> samples) {
        Map<String, List<String>> projectToSamples = new HashMap<>()
        samples.each { sample ->
            String project = sample.substring(0, 5)
            if(!projectToSamples.containsKey(project)) {
              def samplesForProject = [sample]
              projectToSamples.put(project, samplesForProject)
            } else {
              projectToSamples.get(project).add(sample)
            }
        }
        return projectToSamples
    }
}