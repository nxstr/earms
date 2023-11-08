package cz.cvut.kbss.ear.ms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;

@Service
public class DefaultEmailService{

    @Autowired
    public JavaMailSender emailSender;

    @Transactional
    public void sendSimpleEmail(String toAddress, String subject, String message) {

        try {
            MimeMessagePreparator preparator = (mimeMessage) -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(new InternetAddress("test.meeting.scheduler@gmail.com", "Meeting Scheduler Test"));
                helper.setTo(toAddress);
                helper.setSubject(subject);
                helper.setText(message, true);
            };
            emailSender.send(preparator);
        } catch (MailException exception) {
            throw exception;
        }
    }

}
