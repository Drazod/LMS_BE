package com.curcus.lms.service.impl;

import com.curcus.lms.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private VerificationTokenServiceImpl verificationTokenServiceImpl;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${mail.backend_host}")
    private String PREFIX;

    private class MailSenderRunnable implements Runnable {
        private JavaMailSender mailSender;
        private MimeMessage mimeMessage;

        public MailSenderRunnable(JavaMailSender mailSender, MimeMessage mimeMessage) {
            this.mailSender = mailSender;
            this.mimeMessage = mimeMessage;
        }

        public void run() {
            try {
                mailSender.send(mimeMessage);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public Boolean sendEmailToStudent(String to) {
        try {
            String token = verificationTokenServiceImpl.createVerificationToken(to)
                    .orElseThrow(() -> new IllegalArgumentException("Token generation failed"));
            // Create email content
            String subject = "Xác nhận địa chỉ email của bạn";
            String body1 = "Để xác thực địa chỉ email đã đăng ký vui lòng ấn";
            String body2 = "";
            String link = PREFIX + "/api/v1/auth/is-expired-verification?token=" + token;

            return sendHtmlEmailWithButton(to, subject, body1, body2, link);
        } catch (RuntimeException r) {
            r.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean sendEmailToInstructor(String to) {
        try {
            String token = verificationTokenServiceImpl.createVerificationToken(to)
                    .orElseThrow(() -> new IllegalArgumentException("Token generation failed"));
            // Create email content
            String subject = "Xác nhận địa chỉ email của bạn";
            String body1 = "Để xác thực địa chỉ email đã đăng ký vui lòng ấn";
            String body2 = "";
            String link = PREFIX + "/api/v1/auth/is-expired-verification?token=" + token;

            return sendHtmlEmailWithButton(to, subject, body1, body2, link);
        } catch (RuntimeException r) {
            r.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean sendPasswordResetConfirmation(String to, String token) {
        try {
            String subject = "Xác thực yêu cầu đặt lại mật khẩu";
            String body1 = "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Để tiếp tục quá trình này, vui lòng nhấp vào nút dưới đây:";
            String body2 = "Nếu bạn không yêu cầu đặt lại mật khẩu, bạn có thể bỏ qua email này và mật khẩu hiện tại của bạn sẽ vẫn được giữ nguyên. Xin lưu ý rằng liên kết đặt lại mật khẩu sẽ chỉ có hiệu lực trong vòng 1 ngày kể từ khi email này được gửi đi.";
            String link = PREFIX + "/api/password-reset/reset?token=" + token;
            return sendHtmlEmailWithButton(to, subject, body1, body2, link);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean sendPassword(String to, String password) {
        try {
            String subject = "Thông tin mật khẩu mới";
            String body1 = "Chúng tôi nhận được yêu cầu cung cấp lại mật khẩu cho tài khoản của bạn. Mật khẩu mới của bạn là: "
                    + password;
            String body2 = "Chúng tôi khuyên bạn nên thay đổi mật khẩu ngay sau khi đăng nhập để đảm bảo an toàn cho tài khoản của bạn. Nếu bạn không thực hiện yêu cầu này, xin vui lòng liên hệ với chúng tôi ngay lập tức.";
            return sendHtmlEmailWithoutButton(to, subject, body1, body2);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean sendHtmlEmailWithButton(String to, String subject, String body1, String body2, String link) {
        try {
            Context context = new Context();
            context.setVariable("body1", body1);
            context.setVariable("body2", body2);
            context.setVariable("link", link);
            context.setVariable("greetings", "Dear " + to + ",");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(mailProperties.getUsername(), "Curcus");
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process("email-with-button", context);
            helper.setText(htmlContent, true);
            Runnable runnable = new MailSenderRunnable(mailSender, mimeMessage);
            new Thread(runnable).start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean sendHtmlEmailWithoutButton(String to, String subject, String body1, String body2) {
        try {
            Context context = new Context();
            context.setVariable("body1", body1);
            context.setVariable("body2", body2);
            context.setVariable("greetings", "Dear " + to + ",");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom(mailProperties.getUsername(), "Curcus");
            helper.setTo(to);
            helper.setSubject(subject);
            String htmlContent = templateEngine.process("email-without-button", context);
            helper.setText(htmlContent, true);
            Runnable runnable = new MailSenderRunnable(mailSender, mimeMessage);
            new Thread(runnable).start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
