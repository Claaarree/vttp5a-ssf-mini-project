package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.exception.FilterDateException;
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

        JsonObject entryObjectToSave = entryToJson(entryToSave, formattedDate);
        String hashKey = "ENT|" + formattedDate + "|" + entryToSave.getEntryId();
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

    public List<Entry> getAllEntries(String userId) {
        List<Entry> entriesList = new ArrayList<>();

        // System.out.println(Pattern.matches("\\d.*", "02-12-2024|9127ef86-4517-4145-80ac-0144f886f52e"));

        ScanOptions scanOps = ScanOptions.scanOptions()
                .match("ENT*")
                .build();

        Cursor<java.util.Map.Entry<String, String>> entries = entryRepo.filter(userId, scanOps);


        while(entries.hasNext()) {
            java.util.Map.Entry<String, String> entry = entries.next();
            String entryString = entry.getValue();
            // System.out.println(entry.getKey());
            
            Entry e = stringToEntry(entryString);
            entriesList.add(e);
        }


        return entriesList;
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
            System.out.println("Error in parsing consumption date in Entry Service stringToEntry!");
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
    
    public Entry getEntryById(String entryId, String userId) {
        ScanOptions scanOpts = ScanOptions.scanOptions()
                .match("*" + entryId)
                .build();
        
        Cursor<java.util.Map.Entry<String, String>> entryFound = entryRepo.filter(userId, scanOpts);

        // System.out.println(entryFound.hasNext());

        String entryString = entryFound.next().getValue();

        return stringToEntry(entryString);
    }

    public List<Entry> filterDates(List<Entry> entriesList, 
    MultiValueMap<String, String> datesMap) throws FilterDateException {
        String from = datesMap.getFirst("from");
        String to = datesMap.getFirst("to");

        // does the pattern below correspond to the one in the model? does the pattern matter?
        // pattern matters and matches format in the model
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  
        Date fromDate;
        Date toDate;
        try {

            fromDate = sdf.parse(LocalDate.parse(from).minusDays(1).toString());
            toDate = sdf.parse(LocalDate.parse(to).plusDays(1).toString());          
            
            if (toDate.before(fromDate) && toDate != null){
                throw new FilterDateException("The to date has to be after the from date!");
            }

            // is there a way to combine these 2 lines?
            
            entriesList = entriesList.stream().filter(e -> e.getConsumptionDate().before(toDate))
                    .collect(Collectors.toList());
            
            entriesList = entriesList.stream().filter(e -> e.getConsumptionDate().after(fromDate))
            .collect(Collectors.toList());


        } catch (DateTimeParseException dtpe) {
            System.out.println("Error parsing dates in filter dates! DTPE");
        } catch (NullPointerException ne) {
            System.out.println("No dates found in date range!");
        } catch (ParseException pe) {
            System.out.println("Error parsing dates in filter dates! PE");
        }

        return entriesList;        
    }

    public void deleteEntry(String entryId, String userId) {
        ScanOptions scanOpts = ScanOptions.scanOptions()
                .match("*" + entryId)
                .build();
        
        Cursor<java.util.Map.Entry<String, String>> entryFound = entryRepo.filter(userId, scanOpts);
        entryRepo.deleteField(userId, entryFound.next().getKey());
    }
}
