package taco.springbootinaction.repos;

import org.springframework.data.repository.CrudRepository;
import taco.springbootinaction.jpa.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {
}
