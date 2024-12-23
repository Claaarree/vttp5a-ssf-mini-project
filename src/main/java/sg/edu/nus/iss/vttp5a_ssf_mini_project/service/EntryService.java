package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.repo.HashRepo;

@Service
public class EntryService {

    @Autowired
    HashRepo entryRepo;
    
    public Food mapToFoodObject(MultiValueMap<String, String> map) {

        String [] foodToAdd = map.getFirst("foodToAdd").split(",");

        Food f = new Food();

        // System.out.println("foodToAdd[0] " + foodToAdd[0]);
        // System.out.println("foodToAdd[7] " + foodToAdd[7]);
        if (!foodToAdd[0].equals("null") && !foodToAdd[7].equals("null")) {
            f.setId(Long.parseLong(foodToAdd[0]));
            f.setServingId(Long.parseLong(foodToAdd[7]));
        }
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

    public void saveEntry(String userId, Entry entryToSave) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(entryToSave.getConsumptionDate());
        System.out.println(formattedDate);
        System.out.println(entryToSave.getConsumptionTime().toString());

        JsonObject entryObjectToSave = entryToJson(entryToSave, formattedDate);
        String hashKey = formattedDate + "|" + entryToSave.getEntryId();
        entryRepo.addToHash(userId, hashKey, entryObjectToSave.toString());
    }

    public JsonObject entryToJson(Entry entry, String formattedDate) {
        JsonArrayBuilder foodsConsumedArrayBuilder = Json.createArrayBuilder();
        for (Food f : entry.getFoodsConsumed()) {
            JsonObject fObject = null;
            if (f.getId() != null){
                fObject = Json.createObjectBuilder()
                        .add("id", f.getId())
                        .add("servingId", f.getServingId())
                        .build();
                
            } else {
                fObject = Json.createObjectBuilder()
                        .add("customId", f.getCustomId())
                        .build();
            }
            foodsConsumedArrayBuilder.add(fObject);
        }

        JsonObject entryObject = Json.createObjectBuilder()
                .add("entryId", entry.getEntryId())
                .add("consumptionDate", formattedDate)
                .add("consumptionTime", entry.getConsumptionTime().toString())
                .add("foodsConsumed", foodsConsumedArrayBuilder.build())
                .build();

        return entryObject;
    }

}
