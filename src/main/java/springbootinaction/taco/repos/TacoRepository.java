package springbootinaction.taco.repos;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import springbootinaction.taco.jpa.Taco;

import java.util.List;

@Repository
public interface TacoRepository extends CrudRepository<Taco,Long> {
    List<Taco> findAll();
    List<Taco> findAllBy(PageRequest pageRequest);
}
