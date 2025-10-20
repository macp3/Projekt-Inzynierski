package study.snacktrack.controllers;

import java.util.List;

import study.snacktrack.dto.ApiFoodResponseDetailed;
import study.snacktrack.entities.EssentialFood;
import study.snacktrack.services.FoodService;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import study.snacktrack.dto.ApiFoodResponse;
import study.snacktrack.entities.User;

@RestController
@RequestMapping("/food")
public class FoodController {

    private final FoodService foodService;
    private final UserService userService;
    private final JwtService jwtService;

    public FoodController(FoodService foodService, UserService userService, JwtService jwtService) {
        this.foodService = foodService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    //bledow nie ma ani errorow tylko jak wyszukuje "juice" to opdpowiada mi pusta tablicą, czyli prawdopodobnie nie mam tokena jakiesgos
    //przetestujesz ty bo ja nie wiem jak ten klucz
    //albo odpisz
    @GetMapping("/api/search")
    public List<ApiFoodResponse> getFoodFromApi(@RequestParam String query) {
        return foodService.getFoodFromApi(query);
    }

    //tutaj podobnie, ale wyakakuja bledy
    @GetMapping("/api/{id}")
    public ApiFoodResponseDetailed getFoodFromApiById(@PathVariable("id") int id) {
        return foodService.getFoodFromApiById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEssentialFood(@RequestBody EssentialFood food, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);

            User currentUser = userService.getUserByEmail(email);

            EssentialFood savedFood = foodService.addEssentialFood(food, currentUser);
            return ResponseEntity.ok(savedFood);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<EssentialFood>> searchEssentialFood(@RequestParam String query) {
        List<EssentialFood> results = foodService.searchEssentialFood(query);
        return ResponseEntity.ok(results);
    }
}
