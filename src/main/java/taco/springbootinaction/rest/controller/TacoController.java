package taco.springbootinaction.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
import taco.springbootinaction.jpa.Taco;
import taco.springbootinaction.jpa.TacoOrder;
import taco.springbootinaction.queue.RabbitService;
import taco.springbootinaction.repos.TacoOrderRepository;
import taco.springbootinaction.repos.TacoRepository;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(produces = "application/json",path = "/api/taco")
public class TacoController {
    @Autowired
    private TacoRepository tacoRepository;
    @Autowired
    private TacoOrderRepository tacoOrderRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RabbitService rabbitService;
    /*
    @Autowired
    public TacoController(TacoRepository tacoRepository, TacoOrderRepository tacoOrderRepository, RabbitService rabbitService) {
        this.tacoRepository = tacoRepository;
        this.tacoOrderRepository = tacoOrderRepository;
        this.rabbitService = rabbitService;
    }
    */

    @GetMapping(params = "recent")
    public Iterable<Taco> recentTacos() {
        PageRequest page = PageRequest.of(0, 12);
        return tacoRepository.findAllBy(page);
    }

    @GetMapping
    public Iterable<Taco> allTacos() {
        return tacoRepository.findAll();
    }

    @GetMapping(params = "id")
    public void sendOrderToQueue(@RequestParam(name = "id") Long id) {
        Optional<TacoOrder> tacoOrderOptional = tacoOrderRepository.findById(id);
       //RabbitService rabbitService = applicationContext.getBean(RabbitService.class);
        tacoOrderOptional.ifPresent(rabbitService::sendOrder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeRequests()
            .antMatchers("/", "/**").permitAll()
            .and()
            .build();
    }
}
