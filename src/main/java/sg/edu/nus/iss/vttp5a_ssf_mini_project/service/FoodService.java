package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.Constants;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.Url;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.repo.HashRepo;

@Service
public class FoodService {
    
    @Autowired
    HashRepo foodRepo;

    RestTemplate template = new RestTemplate();

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
                .add("name", f.getName())
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

    public List<Food> getAllCustomFoods(String userId) {
        List<Food> customFoodsList = new ArrayList<>();

        ScanOptions scanOps = ScanOptions.scanOptions()
                .match("CUSTOM*")
                .build();

        Cursor<java.util.Map.Entry<String, String>> customFoods = foodRepo.filter(userId, scanOps);
        
        while (customFoods.hasNext()){
            java.util.Map.Entry<String, String> customF = customFoods.next();
            String foodString = customF.getValue();
            Food f = jsonToFood(foodString);
            customFoodsList.add(f);
        }

        return customFoodsList;
    }

    public Food jsonToFood(String foodString) {
        JsonReader jReader = Json.createReader(new StringReader(foodString));
        JsonObject foodObject = jReader.readObject();
        
        JsonArray allergensArray = foodObject.getJsonArray("allergens");
        List<String> allergensList = new ArrayList<>();
        for (int i = 0; i < allergensArray.size(); i++) {
            String a = allergensArray.getString(i);
            allergensList.add(a);
        }
      
        Food f = new Food();

        // no need to set those attributes that's gonna be null
        // f.setId(Long.valueOf(String.valueOf(foodObject.getString("id"))));
        f.setCustomId(foodObject.getString("customId"));
        f.setName(foodObject.getString("name"));
        f.setType(foodObject.getString("type"));
        f.setAllergens(allergensList);
        f.setIsVegetarian(foodObject.getBoolean("isVegetarian"));
        f.setIsVegan(foodObject.getBoolean("isVegan"));
        // f.setServingId(foodObject.getJsonNumber("servingId").longValueExact());
        f.setServingDescription(foodObject.getString("servingDescription"));
        f.setCalories(foodObject.getJsonNumber("calories").doubleValue());
        f.setCarbohydrate(foodObject.getJsonNumber("carbohydrate").doubleValue());
        f.setProtein(foodObject.getJsonNumber("protein").doubleValue());
        f.setFat(foodObject.getJsonNumber("fat").doubleValue());
        f.setUrl(foodObject.getString("url"));
        f.setBrand(foodObject.getString("brand"));
        f.setQuantity(foodObject.getInt("quantity"));

        return f;
    }

    public List<Food> getFoodByName(String customSearch, String userId) {
        List<Food> customFoodsList = getAllCustomFoods(userId);

        List<Food> foodsFound = new ArrayList<>();
        for (Food f : customFoodsList) {
            if (f.getName().contains(customSearch)) {
                foodsFound.add(f);
            }
        }

        return foodsFound;
    }

    public List<Food> requestForFoodsById(String userId, Entry e) {
        List<Food> foodsConsumedId = e.getFoodsConsumed();
        for (Food f: foodsConsumedId) {
            if(f.getId() != null) {
                String url = UriComponentsBuilder.fromUriString(Url.baseUrl)
                        .path(Url.getById)
                        .queryParam("food_id", f.getId())
                        .queryParam("format", "json")
                        .queryParam("flag_default_serving", "true")
                        .queryParam("include_food_attributes", "true")
                        .toUriString();

                RequestEntity<Void> req = RequestEntity.get(url)
                        .header("Authorization", "Bearer " + Constants.apiKey)
                        .build();
                        
            } else {
                // TODO implement get by customid from redis use the cursor
                foodRepo.getFieldValue(userId);
            }
        }
    }

}
