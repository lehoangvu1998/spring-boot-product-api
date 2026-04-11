package com.eureka.store.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class SendEmailOnFailureAOP {
    private final JavaMailSender mailSender;
    private final Map<String, Long> lastSentCache = new ConcurrentHashMap<>();
    private static final long MIN_INTERVAL = 5 * 60 * 1000; // 5 phút

    public SendEmailOnFailureAOP(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @AfterThrowing(pointcut = "@annotation(com.eureka.store.annotation.SendEmailOnFailure)", throwing = "ex")
    public void handleMethodError(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        long currentTime = System.currentTimeMillis();
        long lastSent = lastSentCache.getOrDefault(methodName, 0L);

        if (currentTime - lastSent > MIN_INTERVAL) {
            sendAlertEmail(joinPoint, ex);
            lastSentCache.put(methodName, currentTime);
        } else {
            log.warn(">>> Skip sending email for {} to avoid flooding.", methodName);
        }
    }

    private void sendAlertEmail(JoinPoint joinPoint, Throwable ex) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("admin-tech@eureka.com");
            message.setSubject("CRITICAL: Kafka Service Failure Alert");
            String content = """
            --- SYSTEM ALERT: KAFKA SEND FAILED ---
            
            Method     : %s
            Arguments  : %s
            Error      : %s
            Timestamp  : %s
            
            ---------------------------------------
            """.formatted(
                            joinPoint.getSignature().toShortString(),
                            Arrays.toString(joinPoint.getArgs()),
                            ex.getMessage(),
                            java.time.LocalDateTime.now()
                    );
            message.setText(content);
            mailSender.send(message);
            log.info("Alert email sent successfully to Admin.");
        } catch (Exception mailEx) {
            log.error("Failed to send alert email!", mailEx);
        }
    }

}
