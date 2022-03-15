package taco.springbootinaction;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import taco.springbootinaction.jpa.Ingredient;
import taco.springbootinaction.jpa.Taco;
import taco.springbootinaction.jpa.TacoOrder;
import taco.springbootinaction.queue.RabbitService;
import taco.springbootinaction.repos.IngredientRepository;
import taco.springbootinaction.repos.TacoOrderRepository;
import taco.springbootinaction.repos.TacoRepository;
import taco.springbootinaction.jpa.Ingredient;
import taco.springbootinaction.queue.RabbitReceiver;
import taco.springbootinaction.queue.RabbitReceiver;
import taco.springbootinaction.repos.IngredientRepository;
import taco.springbootinaction.repos.TacoOrderRepository;
import taco.springbootinaction.repos.TacoRepository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableJpaRepositories("taco.springbootinaction.repos")
@EntityScan
@EnableScheduling
public class TacoApplication {
    public final static String RABBIT_QUEUE_NAME = "tacocloud.order";
    public final static String RABBIT_QUEUE_INGREDIENTS = "tacocloud.ingredient";
    public final static String RABBIT_EXCHANGE_NAME = "amq.topic";


    public static void main(String[] args) {
        SpringApplication.run(TacoApplication.class, args);
    }


    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setHibernateProperties(hibernateProperties());
        sessionFactory.setPackagesToScan("taco.springbootinaction.jpa");
        sessionFactory.setDataSource(dataSource());
        return sessionFactory;
    }
    @Bean(name = "transactionManager")
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());
        //hibernateTransactionManager.setDataSource(dataSource());
        hibernateTransactionManager.setAutodetectDataSource(true);
        return hibernateTransactionManager;
    }

    @Bean(name = "RabbitService")
    public RabbitService rabbitService() {
        ConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);
        TopicExchange topicExchange = topicExchange();
        Queue queue = new Queue(RABBIT_QUEUE_NAME);
        Queue queueIngredients = new Queue(RABBIT_QUEUE_INGREDIENTS);
        admin.declareQueue(queue);
        admin.declareQueue(queueIngredients);
        admin.declareExchange(topicExchange);
        admin.declareBinding(binding(topicExchange, queue, RABBIT_QUEUE_NAME));
        admin.declareBinding(binding(topicExchange, queueIngredients, RABBIT_QUEUE_INGREDIENTS));

        return new RabbitService(rabbitTemplate(connectionFactory), messageConverter());
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RabbitReceiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver,"receiveOrder");
        messageListenerAdapter.setMessageConverter(messageConverter());
        messageListenerAdapter.setResponseExchange(RABBIT_EXCHANGE_NAME);
        return messageListenerAdapter;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setupMessageListener(listenerAdapter);
        container.setQueueNames(RABBIT_QUEUE_NAME);
        container.setDefaultRequeueRejected(false);
        return container;
    }

    private TopicExchange topicExchange() {
        return new TopicExchange(RABBIT_EXCHANGE_NAME);
    }

    private Binding binding(TopicExchange exchange, Queue queue, String queueName) {
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    private RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        if(connectionFactory == null) {
            connectionFactory = new CachingConnectionFactory("localhost");
        }
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setDefaultReceiveQueue(RABBIT_QUEUE_NAME);
        rabbitTemplate.setExchange(RABBIT_EXCHANGE_NAME);
        return rabbitTemplate;
    }

    private DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        return dataSourceBuilder
            .driverClassName("org.postgresql.Driver")
            .url("jdbc:postgresql://localhost:5432/taco_db")
            .username("taco").password("taco")
            .build();
    }
    private Properties hibernateProperties() {
        return new Properties() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
                setProperty("hibernate.hbm2ddl.auto", "create");
                setProperty("hibernate.show_sql", "false");
                setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");

            }
        };
    }

    @Bean
    public CommandLineRunner dataLoader(IngredientRepository ingredientRepository, TacoRepository repo, TacoOrderRepository orderRepository) {
        return args -> {
            Ingredient ingredient = new Ingredient("ARG","Carrot", Ingredient.Type.VEGGIES);
            TacoOrder tacoOrder = new TacoOrder(1L,"name","street","city");
            orderRepository.save(tacoOrder);
            ingredientRepository.save(ingredient);
            repo.save(new Taco(1L,"name", tacoOrder, new Date(), Arrays.asList(ingredient)));
        };
    }
}
