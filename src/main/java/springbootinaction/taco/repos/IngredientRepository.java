package springbootinaction.taco.repos;

import org.springframework.data.repository.CrudRepository;
import springbootinaction.taco.jpa.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {
}
