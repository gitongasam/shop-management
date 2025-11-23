package com.devsam.shopmanagement.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String firstName, String to, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Your Verification Code");

            String htmlContent =
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; padding: 20px; background: #f6f6f6;'>" +
                            "<div style='max-width: 500px; margin: auto; background: white; padding: 25px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>" +
                            "<h2 style='color: #4CAF50; text-align: center;'>Email Verification</h2>" +
                            "<p style='font-size: 16px;'>Hello <strong>" + firstName + "</strong>,</p>" +
                            "<p style='font-size: 16px;'>Your verification code is:</p>" +

                            "<div style='font-size: 28px; font-weight: bold; color: #333; text-align: center; margin: 20px 0;'>" +
                            code +
                            "</div>" +

                            "<p style='font-size: 14px; color: #555;'>This code expires in <strong>15 minutes</strong>.</p>" +
                            "<br>" +
                            "<p style='font-size: 14px; text-align: center; color: #888;'>Thank you for using our service!</p>" +
                            "</div>" +
                            "</body>" +
                            "</html>";

            helper.setText(htmlContent, true); // true = send HTML

            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
