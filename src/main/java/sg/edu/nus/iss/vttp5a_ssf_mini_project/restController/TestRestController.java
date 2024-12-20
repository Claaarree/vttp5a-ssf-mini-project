package sg.edu.nus.iss.vttp5a_ssf_mini_project.restController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.SearchService;

@RestController
@RequestMapping("/api/food")
public class TestRestController {
    
    @Autowired
    SearchService testService;

    @GetMapping
    public ResponseEntity<String> getSearch() {
        return testService.getRESTSearch("bread", "0");
    }

    @GetMapping("/list")
    public List<Food> showJSONMapping() {
        return testService.getSearch("bread", "0");
    }
}
