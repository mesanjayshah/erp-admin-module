package com.mesanjay.admin.event.listner;

import com.mesanjay.admin.event.RegistrationCompleteEvent;
import com.mesanjay.admin.model.Admin;
import com.mesanjay.admin.service.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final VerificationTokenService tokenService;
    private final JavaMailSender mailSender;
    private Admin admin;

    public RegistrationCompleteEventListener(VerificationTokenService tokenService, JavaMailSender mailSender) {
        this.tokenService = tokenService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // gets the user
        admin = event.getAdmin();

        // generate token for user
        String vToken = UUID.randomUUID().toString();

        // save the token for user
        tokenService.saveVerificationTokenForAdmin(admin, vToken);

        // build verification url
        String url = event.getConfirmationUrl()+"/verifyEmail?token=" + vToken;

        // send email to the user
        try {
            sendVerificationEmail(admin, url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
//        log.info("Click the link to verify your registration :  {}", url);
    }

    public void sendVerificationEmail(Admin admin, String url) throws MessagingException, UnsupportedEncodingException{

        String subject = "Email verification";
        String senderName = "User registration portal";
        String mailContent = "<p> Hi, "+ admin.getFirstName() +", </p>" +
                "<p> Thank you for registering with us,"+
                "Please, follow the link below to complete your registration.</p> "+
                "<a href=\"" +url+ "\"> Verify your mail to activate you account</a>" +
                "<p>Thank you <br> Users registration portal</p>";

        emailMessage(subject, senderName, mailContent, mailSender, admin);

    }

    public void sendPasswordResetVerificationEmail(Admin admin, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request Verification";
        String senderName = "Users Verification Service";
        String mailContent = "<p> Hi, "+ admin.getFirstName()+ ", </p>"+
                "<p><b>You recently requested to reset your password,</b>"+
                "Please, follow the link below to complete the action.</p>"+
                "<a href=\"" +url+ "\">Reset password</a>"+
                "<p> Users Registration Portal Service";
        emailMessage(subject, senderName, mailContent, mailSender, admin);
    }

    private static void emailMessage(String subject, String senderName, String mailContent,
                                     JavaMailSender mailSender, Admin admin) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);

        // Replace these with your email and password
        String username = "messanjayshah31@gmail.com";
        String password = "ccahhdqsacmzxtvh";

        // Set the email sender's username and password
        ((JavaMailSenderImpl) mailSender).setUsername(username);
        ((JavaMailSenderImpl) mailSender).setPassword(password);

        messageHelper.setFrom(username, senderName);
        messageHelper.setSentDate(new Date());
        messageHelper.setTo(admin.getUsername());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        System.out.println("hello before");
        mailSender.send(message);
        System.out.println("hello after");
    }

}
