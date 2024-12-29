package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.utility.FoodParser;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.utility.Url;

@Service
public class SearchService {

    @Autowired
    FoodParser foodParser;

    @Value("${fatsecret.api.key}")
    String apiKey;

    RestTemplate template = new RestTemplate();

    public List<Food> getSearch(String searchTerm, String pageNumber) {
        ResponseEntity<String> res = getRESTSearch(searchTerm, pageNumber);
    
        return foodParser.externalJsonToFood(res.getBody());
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

}
