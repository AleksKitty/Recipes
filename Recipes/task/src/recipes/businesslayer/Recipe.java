package recipes.businesslayer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(groups = BasicValidation.class)
    @Column(name = "name")
    String name;

    @NotBlank(groups = BasicValidation.class)
    @Column(name = "category")
    String category;

    @NotNull
    @Column(name = "date")
    Timestamp date;

    @NotBlank(groups = BasicValidation.class)
    @Column(name = "description")
    String description;

    @NotEmpty(groups = BasicValidation.class)
    @Size(min = 1)
    @ElementCollection
    List<String> ingredients;

    @NotEmpty(groups = BasicValidation.class)
    @Size(min = 1)
    @ElementCollection
    List<String> directions;

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }
}
