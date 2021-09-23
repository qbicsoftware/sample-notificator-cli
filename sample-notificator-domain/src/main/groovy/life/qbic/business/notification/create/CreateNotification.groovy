package life.qbic.business.notification.create

import life.qbic.business.subscription.Subscriber
import life.qbic.business.subscription.fetch.FetchSubscriber
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.business.subscription.fetch.FetchSubscriberInput
import life.qbic.business.subscription.fetch.FetchSubscriberOutput

import life.qbic.datamodel.samples.Status
import java.time.LocalDate


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
    private final FetchUpdatedDataSource dataSource
    private List<NotificationContent> notifications

    CreateNotification(FetchUpdatedDataSource dataSource, CreateNotificationOutput output) {
        this.output = output
        this.dataSource = dataSource
    }

    @Override
    void createNotifications(String date) {
      LocalDate localDate = LocalDate.parse(date)
      
      try {
          //1. get todays notifications
          Map<String, Status> sampleToStatus = dataSource.getUpdatedSamplesForDay(localDate)
          //2. get subscribers for projects
          Map<String,List<String>> projectsWithSamples = getProjectsWithSamples(sampleToStatus.keySet().asList())
          //3. get project names
          Map<String,String> projectsWithTitles = dataSource.fetchProjectsWithTitles()
          
          for(String projectCode : projectsWithSamples.keySet()) {
            List<String> samples = projectsWithSamples.get(project)
            int failedQCCount = filterSamplesByStatus(samples, sampleToStatus, "SAMPLE_QC_FAIL").size()
            int availableDataCount = filterSamplesByStatus(samples, sampleToStatus, "DATA_AVAILABLE").size()
            String title = projectsWithTitles.get(projectCode)
            notifications.add(new NotificationContent.Builder(customerFirstName, customerLastName, customerEmailAddress, 
              title, projectCode, failedQCCount, availableDataCount).build())
          }
          output.createdNotifications(notifications)    
      } catch (Exception e) {
          output.failNotification("An error occurred while fetching updates for projects")
          output.failNotification(e.message)
      }
    }
    
    private List<String> filterSamplesByStatus(List<String> samples, Map<String, Status> sampleToStatus, String statusName) {
        List<String> filteredCodes = []
        for(String code : samples) {
          Status status = sampleToStatus.get(code)
          // this way of comparing Strings hurts my java heart
          if(status.toString() == statusName) {
            filteredCodes.add(code)
          }
        }
        return filteredCodes
    }
    
    private Map<String, List<String>> getProjectsWithSamples(List<String> samples) {
        Map<String, List<String>> projectToSamples = new HashMap<>()
        samples.each { sample ->
            String project = sample.substring(0, 5)
            if(projectToSamples.containsKey(project)) {
              def list = [sample]
              projectToSamples.put(project, list)
            } else {
              projectToSamples.get(project).add(sample)
            }
        }
        return projects
    }
//
//    /**
//     * Transfers the generated list of subscribers to the implementing class
//     * @param subscribers the retrieved list of subscribers with modified subscriptions
//     * @since 1.0.0
//     */
//
//    @Override
//    void fetchedSubscribers(List<Subscriber> subscribers) {
//        try {
//            //The output of the fetchSubscriber Use case will be stored in the notificationPerSubscriber Map and used in the CreateNotification method
//            this.notificationPerSubscriber = createNotificationPerSubscriber(subscribers)
//        } catch(Exception e) {
//            output.failNotification("An error occurred during notification creation")
//        }
//    }
//
//    /**
//     * Sends failure notifications that have been
//     * recorded during the use case.
//     * @param notification containing a failure message
//     * @since 1.0.0
//     */
//    @Override
//    void failNotification(String notification) {
//        output.failNotification(notification)
//    }
}