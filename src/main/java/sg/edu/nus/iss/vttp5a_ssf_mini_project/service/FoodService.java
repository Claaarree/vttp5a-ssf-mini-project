package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.JsonObject;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.repo.HashRepo;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.utility.FoodParser;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.utility.Url;

@Service
public class FoodService {
    
    @Autowired
    HashRepo foodRepo;

    @Autowired
    FoodParser foodParser;

    @Value("${fatsecret.api.key}")
    String apiKey;

    RestTemplate template = new RestTemplate();

    public void saveCustomFood(Food f, String userId) {
        JsonObject foodObject = foodParser.foodToJson(f);

        foodRepo.addToHash(userId, f.getCustomId(), foodObject.toString());
    }

    public List<Food> getAllCustomFoods(String userId) {
        List<Food> customFoodsList = new ArrayList<>();
        Cursor<java.util.Map.Entry<String, String>> customFoods = getCursor("CUSTOM*", userId);

        while (customFoods.hasNext()){
            java.util.Map.Entry<String, String> customF = customFoods.next();
            String foodString = customF.getValue();
            Food f = foodParser.jsonToFood(foodString);
            customFoodsList.add(f);
        }

        return customFoodsList;
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
        List<Food> foodsConsumedDetails = new ArrayList<>();
        for (Food f: foodsConsumedId) {
            Food fd = new Food();
            if(f.getId() != null) {

                ResponseEntity<String> res = getRESTfoodByID(f.getId());
                
                fd = foodParser.externalJsonToFoodById(res.getBody());
                        
            } else {
                String foodString = getCustomFoodById(f.getCustomId(), userId);
                fd = foodParser.jsonToFood(foodString);

            }
            fd.setQuantity(f.getQuantity());
            foodsConsumedDetails.add(fd);
        }
        
        return foodsConsumedDetails;
    }

    public ResponseEntity<String> getRESTfoodByID(Long id) {
        String url = UriComponentsBuilder.fromUriString(Url.baseUrl)
        .path(Url.getById)
        .queryParam("food_id", id)
        .queryParam("format", "json")
        .queryParam("flag_default_serving", "true")
        .queryParam("include_food_attributes", "true")
        .toUriString();

        RequestEntity<Void> req = RequestEntity.get(url)
                .header("Authorization", "Bearer " + apiKey)
                .build();
        ResponseEntity <String> res = null;
        try {
            res = template.exchange(req, String.class);
        } catch (Exception err) {
            System.out.println("error in getting data from external api");
        }
        return res;
    }

    public String getCustomFoodById(String customId, String userId) {
        Cursor<java.util.Map.Entry<String, String>> foodFound = getCursor("*" + customId, userId);
        String foodString = foodFound.next().getValue();

        return foodString;
    }

    public Food mapToFoodObject(MultiValueMap<String, String> map) {
        return foodParser.mapToFoodObject(map);
    }

    public Cursor<java.util.Map.Entry<String, String>> getCursor(String pattern, String userId) {
        ScanOptions scanOpts = ScanOptions.scanOptions()
        .match(pattern)
        .build();
        
       return foodRepo.filter(userId, scanOpts);
    }

}
