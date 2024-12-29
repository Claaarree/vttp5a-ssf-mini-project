package sg.edu.nus.iss.vttp5a_ssf_mini_project.restController;

import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.exception.FilterDateException;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.EntryService;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.FoodService;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.SearchService;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.utility.FoodParser;

@RestController
@RequestMapping("/api")
public class CpbRestController {
    
    @Autowired
    SearchService searchService;

    @Autowired
    FoodService foodService;

    @Autowired
    EntryService entryService;

    @Autowired
    FoodParser foodParser;

    // shows condensed list of results from external api
    // http://localhost:8080/api/food/list?food=bread&page_offset=0
    @GetMapping(path = "/food/list", produces = "application/json")
    public ResponseEntity<String> showJSONMapping(@RequestParam (name="food") String searchTerm,
    @RequestParam (name = "page_offset") String pageOffset) {
        List<Food> condensedList = searchService.getSearch(searchTerm, pageOffset);
        JsonArrayBuilder jArray = Json.createArrayBuilder();
        for (Food f : condensedList) {
            JsonObject fJObject = foodParser.foodToJson(f);
            jArray.add(fJObject);
        }

        return ResponseEntity.ok().body(jArray.build().toString());
    }

    // shows food eaten by user
    // http://localhost:8080/api/food/id?user_Id=a587a519-0321-4125-92e6-e7f07bb5fe57&food_Id=CUSTOM5b44a21b-1909-4a69-bb8f-1becb0dce26c
    @GetMapping(path="/food/id", produces = "application/json")
    public ResponseEntity<String> getFoodById(@RequestParam (name="food_Id") String foodId,
    @RequestParam (name="user_Id") String userId) {
        if (foodId.contains("CUSTOM")) {
            return ResponseEntity.ok(foodService.getCustomFoodById(foodId, userId));
        } else {
            return foodService.getRESTfoodByID(Long.parseLong(foodId));
        }
    }

    // get entries of food eaten by user based in date range
    // http://localhost:8080/api/entries?user_Id=a587a519-0321-4125-92e6-e7f07bb5fe57&from=2024-12-02&to=2024-12-09
    @GetMapping(path = "/entries", produces = "application/json")
    public ResponseEntity<String> getEntries(@RequestParam (required = false) String from,
    @RequestParam (required = false) String to, @RequestParam String user_Id) {
        List<Entry> entriesList = entryService.getAllEntries(user_Id);
        if (to != null && from != null) {
            MultiValueMap<String, String> datesMap = new LinkedMultiValueMap<>();
            datesMap.add("from", from);
            datesMap.add("to", to);
            try {
                entriesList = entryService.filterDates(entriesList, datesMap);
            } catch (FilterDateException e) {
                System.out.println("error in filtering dates in rest controller");
                e.printStackTrace();
            }
        }
        JsonArrayBuilder entriesArrayBuilder = Json.createArrayBuilder();
        for (Entry e : entriesList) {
            JsonObjectBuilder entryJObjectBuilder = Json.createObjectBuilder();
            List<Food> foodsConsumed = foodService.requestForFoodsById(user_Id, e);
            JsonArrayBuilder foodsConsumedArrayBuilder = Json.createArrayBuilder();
            for (Food f : foodsConsumed) {
                JsonObject fJObject = foodParser.foodToJson(f);
                foodsConsumedArrayBuilder.add(fJObject);
                
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = sdf.format(e.getConsumptionDate());
            entryJObjectBuilder.add("entryId", e.getEntryId())
                    .add("consumptionTime", e.getConsumptionTime().toString())
                    .add("consumptionDate", formattedDate)
                    .add("foodsConsumed", foodsConsumedArrayBuilder.build());

            entriesArrayBuilder.add(entryJObjectBuilder.build());
        }

        return ResponseEntity.ok(entriesArrayBuilder.build().toString());
    }
}
