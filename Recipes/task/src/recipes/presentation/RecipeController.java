package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.businesslayer.BasicValidation;
import recipes.businesslayer.Recipe;
import recipes.businesslayer.services.RecipeService;
import recipes.businesslayer.services.UserService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
public class RecipeController {

    @Autowired
    UserService userService;

    @Autowired
    RecipeService recipeService;

    // add new recipe to db
    @PostMapping("/api/recipe/new")
    public Map<String, Long> saveRecipe(@Validated(BasicValidation.class)  @RequestBody Recipe recipe,
                                        Authentication auth) {

        Recipe recipeSaved = recipeService.save(new Recipe(recipe.getId(),
                userService.findUserByEmail(auth.getName()),
                recipe.getName(),
                recipe.getCategory(),
                Timestamp.from(java.time.Clock.systemUTC().instant()),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getDirections()));

        return Map.of("id", recipeSaved.getId());
    }

    // update recipe in the db
    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable Long id,
                                               @Validated(BasicValidation.class) @RequestBody Recipe recipe,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        Recipe foundRecipe = recipeService.findRecipeById(id);
        if (foundRecipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!userDetails.getUsername().equals(recipeService.findRecipeById(id).getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        foundRecipe.setName(recipe.getName());
        foundRecipe.setCategory(recipe.getCategory());
        foundRecipe.setDate(Timestamp.from(java.time.Clock.systemUTC().instant()));
        foundRecipe.setDescription(recipe.getDescription());
        foundRecipe.setIngredients(recipe.getIngredients());
        foundRecipe.setDirections(recipe.getDirections());
        recipeService.save(foundRecipe);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/api/recipe/{id}")
    public Recipe getRecipe(@PathVariable Long id) {

        Recipe foundRecipe = recipeService.findRecipeById(id);
        if (foundRecipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return foundRecipe;
    }

    // search recipe by category or by name (contains part of the input)
    @GetMapping("/api/recipe/search")
    public ResponseEntity<List<Recipe>> searchRecipe(@RequestParam(required = false) String category,
                                                     @RequestParam(required = false) String name) {

        if (category == null && name == null || category != null && name != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (category != null) {
            return new ResponseEntity<>(recipeService.findByCategoryIgnoreCaseOrderByDateDesc(category),
                    new HttpHeaders(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(recipeService.findByNameIgnoreCaseContainsOrderByDateDesc(name),
                    new HttpHeaders(), HttpStatus.OK);
        }
    }

    // delete recipe
    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        Recipe foundRecipe = recipeService.findRecipeById(id);
        if (foundRecipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!userDetails.getUsername().equals(recipeService.findRecipeById(id).getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        recipeService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
