package springbootinaction.taco;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.connection.RabbitUtils;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import springbootinaction.taco.jpa.Ingredient;
import springbootinaction.taco.jpa.Taco;
import springbootinaction.taco.jpa.TacoOrder;
import springbootinaction.taco.repos.IngredientRepository;
import springbootinaction.taco.repos.TacoOrderRepository;
import springbootinaction.taco.repos.TacoRepository;
import springbootinaction.taco.queue.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableJpaRepositories("springbootinaction.taco.repos")
//@ComponentScan
public class TacoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TacoApplication.class, args);
    }


    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setHibernateProperties(hibernateProperties());
        sessionFactory.setPackagesToScan("springbootinaction.taco.jpa");
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

    @Bean(name = "springbootinaction.taco.queue.RabbitService")
    public RabbitService rabbitService() {
        ConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareQueue(new Queue("tacocloud.order"));
        return new RabbitService(new RabbitTemplate(connectionFactory));
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
