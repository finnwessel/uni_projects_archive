package social_media_platform.services;

import social_media_platform.dto.account.AccountDto;
import social_media_platform.utils.CommonUtils;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Stateful
public class MailService {

    @Resource(lookup = "mail")
    private Session session;

    /**
     * Sends a verfication email to an account
     *
     * @param account an account
     * @param code a verification code
     */
    public void sendVerificationMail(AccountDto account, String code) {
        String subject = "Registration SMP";
        String text = String.format("Welcome %s!<br><br>Thank you for your registration on SMP.<br>Make sure to verify your eMail address by entering the following code on the verification page:", account.getFirstname());
        String content = CommonUtils.getMailTemplate("registration.html");
        content = content.replace("$text", text);
        content = content.replace("$code", code);
        send(subject, content, account.getEmail());
    }

    /**
     * Sends an email
     *
     * @param subject the email subject
     * @param content the email content
     * @param recipient the email recipient
     */
    private void send(String subject, String content, String recipient) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(session.getProperty("mail.from"), "Social Media Platform"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (
                MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
