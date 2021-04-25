package de.basedefender.gdpr

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import javax.mail.*
import javax.mail.Folder
import kotlin.jvm.Throws


@Component
class MailClient(
    private val mailConfig: MailConfig
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun readMail() {
        val properties = Properties()

        properties["mail.pop3.host"] = mailConfig.host
        properties["mail.pop3.port"] = mailConfig.port
        properties["mail.pop3.starttls.enable"] = "true"
        val emailSession = Session.getDefaultInstance(properties)

        val store = emailSession.getStore("pop3s")
        store.connect(mailConfig.host, mailConfig.user, mailConfig.password)

        val emailFolder = store.getFolder("INBOX")
        emailFolder.open(Folder.READ_ONLY)

        val messages = emailFolder.messages
        messages.forEach {message -> println(message.subject) }

        emailFolder.close(false)
        store.close()
    }

}