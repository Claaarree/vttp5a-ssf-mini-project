package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.Url;

@Service
public class TestService {

    @Value("${fatsecret.api.key}")
    String apiKey;
    
    RestTemplate template = new RestTemplate();

    public ResponseEntity<String> getSearch() {
        MultiValueMap <String, String> params = new LinkedMultiValueMap<>();
        params.add("search_expression", "corn");
        params.add("format", "json");
        params.add("max_results", "10");
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
