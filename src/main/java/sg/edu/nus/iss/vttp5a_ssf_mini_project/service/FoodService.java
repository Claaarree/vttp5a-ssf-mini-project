package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.repo.HashRepo;

@Service
public class FoodService {
    
    @Autowired
    HashRepo foodRepo;

    public void saveCustomFood(Food f, String userId) {
        JsonObject foodObject = foodToJson(f);

        foodRepo.addToHash(userId, f.getCustomId(), foodObject.toString());
    }

    public JsonObject foodToJson(Food f) {
        JsonArrayBuilder allergensArrayBuilder = Json.createArrayBuilder();
        for (String a :f.getAllergens()) {
            allergensArrayBuilder.add(a);
        }

        JsonObject foodObject = Json.createObjectBuilder()
                .add("id", String.valueOf(f.getId()))
                .add("customId", f.getCustomId())
                .add("type", f.getType())
                .add("allergens", allergensArrayBuilder.build())
                .add("isVegetarian", f.getIsVegetarian())
                .add("isVegan", f.getIsVegan())
                .add("servingId", String.valueOf(f.getServingId()))
                .add("servingDescription", f.getServingDescription())
                .add("calories", f.getCalories())
                .add("carbohydrate", f.getCarbohydrate())
                .add("protein", f.getProtein())
                .add("fat", f.getFat())
                .add("url", String.valueOf(f.getUrl()))
                .add("brand", f.getBrand())
                .add("quantity", 0)
                .build();

        return foodObject;
    }
}
