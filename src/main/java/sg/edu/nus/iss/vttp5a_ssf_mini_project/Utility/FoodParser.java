package sg.edu.nus.iss.vttp5a_ssf_mini_project.utility;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;

@Component
public class FoodParser {
    
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

    public JsonObject foodToJson(Food f) {
        JsonArrayBuilder allergensArrayBuilder = Json.createArrayBuilder();
        for (String a :f.getAllergens()) {
            allergensArrayBuilder.add(a);
        }

        if(f.getQuantity() == null) {
            f.setQuantity(0);
        }

        JsonObject foodObject = Json.createObjectBuilder()
                .add("id", String.valueOf(f.getId()))
                .add("customId", String.valueOf(f.getCustomId()))
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
                .add("quantity", f.getQuantity())
                .build();

        return foodObject;
    }

    public List<Food> externalJsonToFood(String body) {
        List<Food> foodList = new ArrayList<>();

        JsonReader jReader = Json.createReader(new StringReader(body));
        JsonObject jObject = jReader.readObject();
        JsonObject foodSearch = jObject.getJsonObject("foods_search");
        JsonObject results = foodSearch.getJsonObject("results");
        JsonArray food = results.getJsonArray("food");

        for (int i = 0; i < food.size(); i++) {
            JsonObject fJObject = food.getJsonObject(i);
            Food f = mappingToFood(fJObject);
            foodList.add(f);
        }
        return foodList;
    }

    public Food mappingToFood(JsonObject fJObject) {
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
            // getting allergen information
            JsonObject allergens = foodAttributes.getJsonObject("allergens");
            JsonArray allergen = allergens.getJsonArray("allergen");
            for (int j = 0; j < allergen.size(); j++) {
                JsonObject allergy = allergen.getJsonObject(j);

                if (allergy.getString("value").equals("1")){

                    String a = allergy.getString("name");
                    allergensList.add(a);
                }
            }
            if (allergensList.isEmpty()) {
                allergensList.add("NONE");
            }

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
        return f;
    }

    public Food externalJsonToFoodById (String foodString) {
        JsonReader jReader = Json.createReader(new StringReader(foodString));
        JsonObject jObject = jReader.readObject();
        JsonObject foodObject = jObject.getJsonObject("food");
        Food f = mappingToFood(foodObject);

        return f;
    }

    public Food mapToFoodObject (MultiValueMap<String, String> map){
        System.out.println(map.getFirst("foodToAdd"));
        String [] foodToAdd = map.getFirst("foodToAdd").split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)");

        Food f = new Food();

        if (!foodToAdd[0].equals("null") && !foodToAdd[7].equals("null")) {
            f.setId(Long.parseLong(foodToAdd[0]));
            f.setServingId(Long.parseLong(foodToAdd[7]));
        }
        f.setCustomId(foodToAdd[1]);
        f.setName(foodToAdd[2].replace("\"", ""));
        f.setType(foodToAdd[3]);
        List<String> allergensList = new ArrayList<>();
        if (foodToAdd[4].isEmpty()){
            allergensList.add("UNKNOWN");
        } else {
            String[] allergens = foodToAdd[4].substring(1).split("|");
            for (int i = 0; i < allergens.length; i++){
                allergensList.add(allergens[i]);
            }
        }
        f.setAllergens(allergensList);
        f.setIsVegetarian(Boolean.parseBoolean(foodToAdd[5]));
        f.setIsVegan(Boolean.parseBoolean(foodToAdd[6]));
        f.setServingDescription(foodToAdd[8]);
        f.setCalories(Double.parseDouble(foodToAdd[9]));
        f.setCarbohydrate(Double.parseDouble(foodToAdd[10]));
        f.setProtein(Double.parseDouble(foodToAdd[11]));
        f.setFat(Double.parseDouble(foodToAdd[12]));
        f.setUrl(foodToAdd[13]);
        f.setBrand(foodToAdd[14]);
        // f.setQuantity(null);

        return f;
    }
}
