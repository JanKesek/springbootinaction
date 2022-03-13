package springbootinaction.taco.queue;

import lombok.extern.java.Log;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springbootinaction.taco.jpa.TacoOrder;

@Log
@Component
public class RabbitReceiver {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private MessageConverter messageConverter;

    @Autowired
    public void setMessageConverter() {
        messageConverter = new Jackson2JsonMessageConverter();
    }

    @RabbitListener(queues = "tacocloud.order")
    public TacoOrder receiveOrder(TacoOrder tacoOrder) {
        Message message = rabbitTemplate.receive("tacocloud.order");
        tacoOrder = (TacoOrder) messageConverter.fromMessage(message);
        log.warning("RECEIVED MESSAGE " + tacoOrder.getName());
        return tacoOrder;
    }
}
