package springbootinaction.taco.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import springbootinaction.taco.jpa.TacoOrder;

import java.util.Optional;

@Repository
public interface TacoOrderRepository extends CrudRepository<TacoOrder, Long> {
    Optional<TacoOrder> findById(Long id);
}
