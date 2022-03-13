package springbootinaction.taco.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "Taco")
@Table(name = "Taco")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Taco {
    @Id
    private Long id;
    private String name;
    @ManyToOne
    private TacoOrder tacoOrder;
    @Column(name = "createdat")
    private Date createdAt;

    @Size(min=1, message="You must choose at least 1 ingredient")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients = new ArrayList<>();

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }
}
