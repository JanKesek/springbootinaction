package springbootinaction.taco.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Data
@Entity(name = "Ingredient")
@Table(name = "ingredient")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    public static final List<String> ingredientNames = Arrays.asList("paprika", "ham", "ketchup", "cheese");
    public enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
