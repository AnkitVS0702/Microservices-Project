package com.ankitshiksharthi.microserviceproject.notification_service.service;

import com.ankitshiksharthi.microserviceproject.order_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("Received message from order-placed topic: {}", orderPlacedEvent);

        // Send email to the customer
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springshop@email.com");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(String.format("Your Order with Order Number %s is placed successfully",
                    orderPlacedEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                            Hi %s %s,

                            Your order with order number %s is now placed successfully.

                            Thank you for shopping with us!
                            
                            Best Regards,
                            Spring Shop
                            """,
                    orderPlacedEvent.getFirstName(),
                    orderPlacedEvent.getLastName(),
                    orderPlacedEvent.getOrderNumber()));
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Order notification email sent successfully to {}", orderPlacedEvent.getEmail());
        } catch (MailException e) {
            log.error("Exception occurred when sending mail to {}", orderPlacedEvent.getEmail(), e);
            throw new RuntimeException("Exception occurred when sending mail to " + orderPlacedEvent.getEmail(), e);
        }
    }
}
