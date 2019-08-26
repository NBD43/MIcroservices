package com.bridgelabz.fundoo.utility;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.bridgelabz.fundoo.user.model.MailModel;


public class RabbitMqListner {
@Autowired 
Utility utility;
  
@RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
public void  receiveMessage(MailModel mailModel)
{
utility.send(mailModel.getTo(), mailModel.getSubject(), mailModel.getBody());
}
}