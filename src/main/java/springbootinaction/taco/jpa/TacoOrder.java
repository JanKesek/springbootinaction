package springbootinaction.taco.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "TacoOrder")
@Table(name = "Taco_Order")
@NoArgsConstructor
@AllArgsConstructor
public class TacoOrder {
    @Id
    private Long id;
    @Column
    private String name;
    private String street;
    private String city;
}

