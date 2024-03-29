package vc.c4.deadletter.mq;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vc.c4.deadletter.config.MQConfig;
import vc.c4.deadletter.domain.ExampleObject;

@Component
public class DeadLetterSendReceive {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterSendReceive.class);

  @Autowired
  @Qualifier("outgoingSender")
  private RabbitTemplate outgoingSender;

  // Scheduled task to send an object every 5 seconds
  //@Scheduled(fixedDelay = 5000)
  public void sender() {
    ExampleObject ex = new ExampleObject();
    ex.setName(UUID.randomUUID().toString());
    LOGGER.info("Sending example object: " + ex.getName() + ", at: " + ex.getDate());
    outgoingSender.convertAndSend(ex);
  }

  // Annotation to listen for an ExampleObject
  //@RabbitListener(queues = MQConfig.INCOMING_QUEUE)
  //public void handleMessage(ExampleObject exampleObject) {
  //  LOGGER.info("Received incoming object name: " + exampleObject.getName()
  //      + ", now is :" + new Date());
  //}

}
