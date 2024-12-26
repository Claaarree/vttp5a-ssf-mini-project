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
import sg.edu.nus.iss.vttp5a_ssf_mini_project.Utility.EntryParser;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.exception.FilterDateException;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.repo.HashRepo;

@Service
public class EntryService {

    @Autowired
    HashRepo entryRepo;

    @Autowired
    EntryParser entryParser;

    public void saveEntry(String userId, Entry entryToSave) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(entryToSave.getConsumptionDate());

        JsonObject entryObjectToSave = entryParser.entryToJson(entryToSave, formattedDate);
        String hashKey = "ENT|" + formattedDate + "|" + entryToSave.getEntryId();
        entryRepo.addToHash(userId, hashKey, entryObjectToSave.toString());
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
            
            Entry e = entryParser.stringToEntry(entryString);
            entriesList.add(e);
        }


        return entriesList;
    }

   
    
    public Entry getEntryById(String entryId, String userId) {
        ScanOptions scanOpts = ScanOptions.scanOptions()
                .match("*" + entryId)
                .build();
        
        Cursor<java.util.Map.Entry<String, String>> entryFound = entryRepo.filter(userId, scanOpts);

        // System.out.println(entryFound.hasNext());

        String entryString = entryFound.next().getValue();

        return entryParser.stringToEntry(entryString);
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
