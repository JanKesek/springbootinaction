package springbootinaction.taco.queue;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import springbootinaction.taco.jpa.TacoOrder;

@Service
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class RabbitService {
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public void sendOrder(TacoOrder tacoOrder) {
        MessageConverter converter = new Jackson2JsonMessageConverter();
        MessageProperties messageProperties = new MessageProperties();
        Message message = converter.toMessage(tacoOrder, messageProperties);
        rabbitTemplate.send(message);

        //rabbitTemplate.convertAndSend(tacoOrder);
    }
}
