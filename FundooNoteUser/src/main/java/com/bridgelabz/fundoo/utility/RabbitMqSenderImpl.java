package com.bridgelabz.fundoo.utility;


import org.springframework.amqp.core.AmqpTemplate;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.bridgelabz.fundoo.user.model.MailModel;


@Component
public class RabbitMqSenderImpl 
{
@Autowired
private AmqpTemplate rabbitTemplate;

@Autowired
private JavaMailSender sender;

public void sendMessageToQueue(MailModel mailModel) 
{
final String exchange="QueueExchangeConn";//environment.getProperty("spring.rabbitmq.exchange"); 
final String routingKey="RoutingKey";//environment.getProperty("spring.rabbitmq.routingKey");
rabbitTemplate.convertAndSend(exchange, routingKey, mailModel);

}

@RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
public void send(MailModel email) {

SimpleMailMessage message = new SimpleMailMessage(); 
   message.setTo(email.getTo()); 
   message.setSubject(email.getSubject()); 
   message.setText(email.getBody());
   
   System.out.println("Sending Email ");
 
   sender.send(message);

   System.out.println("Email Sent Successfully!!");
}

}