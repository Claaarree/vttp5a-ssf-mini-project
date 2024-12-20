package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;

@Service
public class EntryService {
    
    public Food mapToFoodObject(MultiValueMap<String, String> map) {

        String [] foodToAdd = map.getFirst("foodToAdd").split(",");

        Food f = new Food();
        f.setId(Long.parseLong(foodToAdd[0]));
        f.setCustomId(foodToAdd[1]);
        f.setName(foodToAdd[2]);
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
        f.setServingId(Long.parseLong(foodToAdd[7]));
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
