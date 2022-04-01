package life.qbic.business.notification.create

import life.qbic.business.exception.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.subscription.Subscriber
import life.qbic.business.subscription.fetch.FetchSubscriberDataSource
import life.qbic.datamodel.samples.Status

import java.time.LocalDate
import java.util.function.Predicate
import java.util.stream.Collectors

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
    private final FetchProjectDataSource projectDataSource
    private final FetchSubscriberDataSource fetchSubscriberDataSource

    private Map<String, Status> updatedSamplesWithStatus
    private static final Logging log = Logger.getLogger(CreateNotification.class)


    CreateNotification(FetchProjectDataSource projectDataSource, FetchSubscriberDataSource fetchSubscriberDataSource, CreateNotificationOutput output) {
        this.output = output
        this.projectDataSource = projectDataSource
        this.fetchSubscriberDataSource = fetchSubscriberDataSource
    }

    @Override
    void createNotifications(String date) {
        LocalDate localDate = LocalDate.parse(date)
        try {
            updatedSamplesWithStatus = fetchSubscriberDataSource.getUpdatedSamplesForDay(localDate)
            List<Project> projectsWithSamples = getProjects().toList()
            Map<String, String> projectsWithTitles = projectDataSource.fetchProjectsWithTitles()
            //take out the assignment of the project title (another method?)
            List<NotificationContent> notifications = projectsWithSamples.stream()
                    .map(it -> fillProjectWithTitle(it, projectsWithTitles))
                    .map(this::getNotificationsForProject)
                    .filter((List projectNotifications) -> !projectNotifications.isEmpty())
                    .flatMap(Collection::stream) // flattens the list
                    .collect(Collectors.toList())
            if (notifications.isEmpty()) {
                log.info("No notifications created for ${date}.")
                return
            }
            output.createdNotifications(notifications)

        } catch (DatabaseQueryException databaseQueryException) {
            output.failNotification("An error occurred while trying to query the database during Notification creation for ${date}")
            log.error(databaseQueryException.message)
            log.error(databaseQueryException.stackTrace.join("\n"))
        }
        catch (Exception e) {
            output.failNotification("An error occurred while trying to create the Notifications for ${date}")
            log.error(e.message)
            log.error(e.stackTrace.join("\n"))
        }
    }

    private static Project fillProjectWithTitle(Project project, Map<String, String> projectCodeToTitle) {
        project.setTitle(projectCodeToTitle.get(project.getCode()))
        return project
    }

    private List<NotificationContent> getNotificationsForProject(Project project) {
        int failedQCCount = filterSamplesByStatus(project.sampleCodes, "SAMPLE_QC_FAIL").size()
        int availableDataCount = filterSamplesByStatus(project.sampleCodes, "DATA_AVAILABLE").size()

        if (!isRelevantStatusUpdated(failedQCCount, availableDataCount)) {
            log.info("Notification for project ${project.code} was not generated, since the sample status was not set to FAILED_QC or DATA_AVAILABLE")
            return noNotifications()
        }
        //4. get subscribers of this projects
        List<Subscriber> subscribers = fetchSubscriberDataSource.getSubscriberForProject(project.code)
        Collection<NotificationContent> notifications = subscribers.stream()
                .map(subscriber -> {
                    createNotificationForSubscriber(subscriber, project, failedQCCount, availableDataCount)
                }).collect()
        return notifications

    }

    private static NotificationContent createNotificationForSubscriber(Subscriber subscriber, Project project, int failedQCCount, int availableDataCount) {
        return new NotificationContent.Builder(subscriber.userId, subscriber.firstName, subscriber.lastName, subscriber.email,
                project.title, project.code, failedQCCount, availableDataCount).build()
    }


    private static List<NotificationContent> noNotifications() {
        return []
    }

    private List<String> filterSamplesByStatus(List<String> samples, String statusName) {

        Predicate isOfStatus = code -> updatedSamplesWithStatus.get(code).toString() == statusName
        List<String> filteredCodes = samples.stream().filter(isOfStatus).collect().toList()

        return filteredCodes
    }


    private Set<Project> getProjects() {
        Set<Project> projects = new HashSet<>()

        updatedSamplesWithStatus.keySet().each { sampleCode ->
            Project project = new Project()
            project.code = sampleCode.substring(0, 5)

            Optional<Project> foundProject = projects.stream().filter({it.code == project.code}).findFirst()

            if (foundProject.isPresent()) {
                foundProject.get().sampleCodes.add(sampleCode)
            } else {
                project.sampleCodes = [sampleCode]
                projects.add(project)
            }
        }
        return projects
    }

    private static boolean isRelevantStatusUpdated(int failedQCCount, int dataAvailableCount) {
        return (failedQCCount + dataAvailableCount) > 0
    }
}
