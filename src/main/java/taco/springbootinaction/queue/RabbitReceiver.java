package taco.springbootinaction.queue;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import taco.springbootinaction.TacoApplication;
import taco.springbootinaction.jpa.TacoOrder;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Log
@Component
public class RabbitReceiver {
    private RabbitTemplate rabbitTemplate;
    private MessageConverter messageConverter;

    @Autowired
    public RabbitReceiver(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Getter
    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    public void setMessageConverter() {
        messageConverter = new Jackson2JsonMessageConverter();
    }

    @RabbitListener(queues = {TacoApplication.RABBIT_QUEUE_NAME})
    public TacoOrder receiveOrder(TacoOrder tacoOrder) throws InterruptedException {
        //Message message = rabbitTemplate1.receive("tacocloud.order");
        /*while (message == null) {
            Thread.sleep(1000L);
            message = rabbitTemplate1.receive("tacocloud.order");
        }*/
        //TacoOrder tacoOrder = (TacoOrder) messageConverter.fromMessage(message);
        log.warning("RECEIVED MESSAGE " + tacoOrder.getName());
        latch.countDown();
        return tacoOrder;
    }
    //@RabbitListener(queues = {TacoApplication.RABBIT_QUEUE_INGREDIENTS})
    public void receiveOrder(List<String> message) throws InterruptedException {
        //Message message = rabbitTemplate1.receive("tacocloud.order");
        /*while (message == null) {
            Thread.sleep(1000L);
            message = rabbitTemplate1.receive("tacocloud.order");
        }*/
        //TacoOrder tacoOrder = (TacoOrder) messageConverter.fromMessage(message);
        log.warning("RECEIVED MESSAGE " + message);
        //return message;
    }


}
