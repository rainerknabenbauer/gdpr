package de.basedefender.gdpr

import de.basedefender.gdpr.email.MailClient
import de.basedefender.gdpr.email.value.Users
import de.basedefender.gdpr.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InboxScheduler(
    private val mailClient: MailClient,
    private val userService: UserService
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Regularly fetches eMails from the inbox and stores them in the database.
     */
    @Scheduled(fixedDelay = 30000)
    fun fetchEmails() {
        log.info("Fetching eMails ...")
        val eMails = mailClient.fetchMails()
        log.info("Fetched ${eMails.size} new emails.")
        userService.addEmails(Users.fromEmailAdapters(eMails))
    }

    /**
     * Send 'cease and delete' notifications to companies.
     */
    @Scheduled(fixedDelay = 60000)
    fun sendEmails() {
        val users = userService.getPendingNotifications()
        log.info("Found ${users.size} users with pending notifications.")

        userService.sendGdprNotifications(users)
        log.info("Sent 'cease and delete' notifications.")
    }

}