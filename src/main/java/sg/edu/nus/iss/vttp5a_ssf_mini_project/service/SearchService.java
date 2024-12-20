package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.Url;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;

@Service
public class SearchService {

    @Value("${fatsecret.api.key}")
    String apiKey;
    
    RestTemplate template = new RestTemplate();

    public List<Food> getSearch(String searchTerm, String pageNumber) {
        ResponseEntity<String> res = getRESTSearch(searchTerm, pageNumber);
    
        return jsonToFood(res.getBody());
    }
            
    private List<Food> jsonToFood(String body) {
        List<Food> foodList = new ArrayList<>();

        JsonReader jReader = Json.createReader(new StringReader(body));
        JsonObject jObject = jReader.readObject();
        JsonObject foodSearch = jObject.getJsonObject("foods_search");
        JsonObject results = foodSearch.getJsonObject("results");
        JsonArray food = results.getJsonArray("food");

        for (int i = 0; i < food.size(); i++) {
            JsonObject fJObject = food.getJsonObject(i);

            Food f = new Food();
            
            f.setId(Long.valueOf(fJObject.getString("food_id")));
            f.setName(fJObject.getString("food_name"));
            f.setType(fJObject.getString("food_type"));
            f.setUrl(fJObject.getString("food_url"));
            
            if (f.getType().equals("Brand")) {
                f.setBrand(fJObject.getString("brand_name"));
            } else {
                f.setBrand("NA");
            }
            
            List<String> allergensList = new ArrayList<>();      
            JsonObject foodAttributes = fJObject.getJsonObject("food_attributes");
            if (foodAttributes != null) {
                allergensList = getAllergensList(foodAttributes);

                // getting preferences information
                JsonObject preferences = foodAttributes.getJsonObject("preferences");
                JsonArray preference = preferences.getJsonArray("preference");
                for (int k = 0; k < preference.size(); k++) {
                    JsonObject prefer = preference.getJsonObject(k);

                    if (prefer.getString("value").equals("1")) {

                        String p = prefer.getString("name");
                        if (p.equals("Vegetarian")){
                            f.setIsVegetarian(true);
                        } else if (p.equals("Vegan")) {
                            f.setIsVegan(true);
                        }
                    }
                }
                
            } else {
                allergensList.add("UNKNOWN");
            }
            f.setAllergens(allergensList);           


            // getting macronutrients for default serving
            JsonObject servings = fJObject.getJsonObject("servings");
            JsonArray serving = servings.getJsonArray("serving");
            for (int j = 0; j < serving.size(); j++) {
                JsonObject serve = serving.getJsonObject(j);
        
                if (serve.getString("is_default") == null){
                    continue;
                } else {
                    f.setServingId(Long.valueOf(serve.getString("serving_id")));
                    f.setServingDescription(serve.getString("serving_description"));
                    f.setCalories(Double.valueOf(serve.getString("calories")));
                    f.setCarbohydrate(Double.valueOf(serve.getString("carbohydrate")));
                    f.setProtein(Double.valueOf(serve.getString("protein")));
                    f.setFat(Double.valueOf(serve.getString("fat")));
                    break;
                }
            }

            foodList.add(f);
        }
        return foodList;
    }

    public ResponseEntity<String> getRESTSearch(String searchTerm, String pageNumber) {
        MultiValueMap <String, String> params = new LinkedMultiValueMap<>();
        params.add("search_expression", searchTerm);
        params.add("format", "json");
        params.add("max_results", "20");
        params.add("page_number", pageNumber);
        params.add("flag_default_serving", "true");
        params.add("include_food_attributes", "true");
        params.add("language", "en");
        params.add("region", "US");

        String url = UriComponentsBuilder.fromUriString(Url.baseUrl)
                .path(Url.searchV3)
                .queryParams(params)
                .toUriString();

        RequestEntity<Void> req = RequestEntity.get(url)
                .header("Authorization", "Bearer " + apiKey)
                .build();
        ResponseEntity<String> res = null;
        try {
            res = template.exchange(req, String.class);

            } catch (Exception e) {
                System.out.println("error in getting data from external api");
            }
    
        return res;
    }   

    public List<String> getAllergensList(JsonObject foodAttributes) {

        List<String> allergensList = new ArrayList<>();
         // getting allergen information
         JsonObject allergens = foodAttributes.getJsonObject("allergens");
         JsonArray allergen = allergens.getJsonArray("allergen");
         for (int j = 0; j < allergen.size(); j++) {
             JsonObject allergy = allergen.getJsonObject(j);

             // test out both... not sure which on will work
             if (allergy.getString("value").equals("1")){
            //  if (allergy.getInt("value") == 1){

                 String a = allergy.getString("name");
                 allergensList.add(a);
             }
         }
         if (allergensList.isEmpty()) {
            allergensList.add("NONE");
         }

         return allergensList;
    }
}
