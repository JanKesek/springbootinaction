package springbootinaction.taco.queue;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import springbootinaction.taco.TacoApplication;
import springbootinaction.taco.jpa.Ingredient;
import springbootinaction.taco.jpa.TacoOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log
@Service
@AllArgsConstructor
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageConverter messageConverter;

    public void sendOrder(TacoOrder tacoOrder) {

        MessageProperties messageProperties = new MessageProperties();
        Message message = messageConverter.toMessage(tacoOrder, messageProperties);
        rabbitTemplate.send(TacoApplication.RABBIT_EXCHANGE_NAME, TacoApplication.RABBIT_QUEUE_NAME, message);
        log.info("SEND MESSAGE");
        //rabbitTemplate.convertAndSend(tacoOrder);
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void createIngredients() {
        MessageProperties messageProperties = new MessageProperties();
        List<String> ingredients = new ArrayList<>();
        int ingredientSize = Ingredient.ingredientNames.size();
        for(int i=0;i<5;i++) {
            ingredients.add(Ingredient.ingredientNames.get(new Random().nextInt(ingredientSize)));
        }
        Message message = messageConverter.toMessage(ingredients,messageProperties);
        rabbitTemplate.send(TacoApplication.RABBIT_EXCHANGE_NAME, TacoApplication.RABBIT_QUEUE_INGREDIENTS, message);
        log.info("SEND MESSAGE RANDOM INGREDIENTS " + ingredients);
    }
}
