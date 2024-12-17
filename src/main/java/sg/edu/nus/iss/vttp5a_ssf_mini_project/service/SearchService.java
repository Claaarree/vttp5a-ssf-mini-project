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
        MultiValueMap <String, String> params = new LinkedMultiValueMap<>();
        params.add("search_expression", searchTerm);
        params.add("format", "json");
        params.add("max_results", "20");
        params.add("page_number", pageNumber);
        // below 2 lines is for v3, v1 no need
        // params.add("language", "en");
        // params.add("region", "US");

        String url = UriComponentsBuilder.fromUriString(Url.baseUrl)
                // .path(Url.searchV3)
                .path(Url.searchV1)
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
    
        return jsonToFood(res.getBody());
    }
            
    private List<Food> jsonToFood(String body) {
        List<Food> foodList = new ArrayList<>();

        JsonReader jReader = Json.createReader(new StringReader(body));
        JsonObject jObject = jReader.readObject();
        JsonObject foods = jObject.getJsonObject("foods");
        JsonArray food = foods.getJsonArray("food");

        for (int i = 0; i < food.size(); i++) {
            JsonObject fJObject = food.getJsonObject(i);

            Food f = new Food();
            // f.setId(fJObject.getJsonNumber("food_id").longValueExact());
            f.setId(Long.valueOf(fJObject.getString("food_id")));
            f.setName(fJObject.getString("food_name"));
            f.setType(fJObject.getString("food_type"));
            f.setDescription(fJObject.getString("food_description"));
            f.setUrl(fJObject.getString("food_url"));

            if (f.getType().equals("Brand")) {
                f.setBrand(fJObject.getString("brand_name"));
            } else {
                f.setBrand("NA");
            }
            foodList.add(f);
        }
        return foodList;
    }

    public ResponseEntity<String> getRESTSearch() {
        MultiValueMap <String, String> params = new LinkedMultiValueMap<>();
        params.add("search_expression", "corn");
        params.add("format", "json");
        params.add("max_results", "10");
        params.add("page_number", "0");
        // below 2 lines is for v3, v1 no need
        // params.add("language", "en");
        // params.add("region", "US");

        String url = UriComponentsBuilder.fromUriString(Url.baseUrl)
                // .path(Url.searchV3)
                .path(Url.searchV1)
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
}
