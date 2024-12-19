package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;

@Service
public class EntryService {
    
    public Food mapToFoodObject(MultiValueMap<String, String> map) {

        String [] foodToAdd = map.getFirst("foodToAdd").split(",");

        Food f = new Food();
        f.setId(Long.parseLong(foodToAdd[0]));
        f.setName(foodToAdd[1]);
        f.setType(foodToAdd[2]);
        f.setDescription(foodToAdd[3]);
        f.setDescription(foodToAdd[4]);
        f.setBrand(foodToAdd[5]);

        return f;
    }
}
