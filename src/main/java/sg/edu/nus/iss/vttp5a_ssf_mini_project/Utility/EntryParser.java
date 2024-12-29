package sg.edu.nus.iss.vttp5a_ssf_mini_project.utility;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;

@Component
public class EntryParser {
    
    public JsonObject entryToJson(Entry entry, String formattedDate) {
        JsonArrayBuilder foodsConsumedArrayBuilder = Json.createArrayBuilder();
        for (Food f : entry.getFoodsConsumed()) {
            JsonObject fObject = null;
            if (f.getId() != null){
                fObject = Json.createObjectBuilder()
                        .add("id", f.getId())
                        .add("servingId", f.getServingId())
                        .add("quantity", f.getQuantity())
                        .build();
                
            } else {
                fObject = Json.createObjectBuilder()
                        .add("customId", f.getCustomId())
                        .add("quantity", f.getQuantity())
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

     public Entry stringToEntry(String entryString){
        JsonReader jReader = Json.createReader(new StringReader(entryString));
        JsonObject entryJObject = jReader.readObject();

        Entry e = new Entry();

        e.setEntryId(entryJObject.getString("entryId"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date consumptionDate = null;
        try {
            consumptionDate = sdf.parse(entryJObject.getString("consumptionDate"));
        } catch (ParseException e1) {
            System.out.println("Error in parsing consumption date in Entry parser stringToEntry!");
            e1.printStackTrace();
        }
        e.setConsumptionDate(consumptionDate);
        e.setConsumptionTime(LocalTime.parse(entryJObject.getString("consumptionTime")));

        List<Food> foodConsumedList = new ArrayList<>();
        JsonArray foodsArray = entryJObject.getJsonArray("foodsConsumed");
        for (int i = 0; i < foodsArray.size(); i++) {
            JsonObject fObject = foodsArray.getJsonObject(i);
       
            Food f = new Food();
            try {
                f.setId(fObject.getJsonNumber("id").longValueExact());
                f.setServingId(fObject.getJsonNumber("servingId").longValueExact());
                f.setQuantity(fObject.getInt("quantity"));
            } catch (Exception err) {
             
                f.setCustomId(fObject.getString("customId"));
                f.setQuantity(fObject.getInt("quantity"));
            }
            foodConsumedList.add(f);
        }
        e.setFoodsConsumed(foodConsumedList);

        return e;
    }
}
