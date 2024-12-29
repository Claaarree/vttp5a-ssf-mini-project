package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.exception.FilterDateException;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.EntryService;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.FoodService;

@Controller
@RequestMapping("/entries")
public class EntryController {

    @Autowired
    EntryService entryService;

    @Autowired
    FoodService foodService;

    @GetMapping("/new")
    public ModelAndView newEntryForm(HttpSession session) {
        Entry entry = new Entry();
        String url = UriComponentsBuilder.fromPath("/entries/new")
                // either one works: path segment adds the / for you
                // .path("/{entryId}")
                .pathSegment("{entryId}")
                .build(entry.getEntryId())
                .toString();
               
        // System.out.println(url);
        ModelAndView mav = new ModelAndView("redirect:" + url);

        session.setAttribute("entry", entry);

        return mav;
    }

    @GetMapping("/new/{entryId}")
    public ModelAndView entryWithId(HttpSession session) {
        ModelAndView mav = new ModelAndView("addEntry");
        Entry entry = (Entry)session.getAttribute("entry");
    
        mav.addObject("entry", entry);
        return mav;
    }
    
    @PostMapping("/new/{entryId}")
    public ModelAndView addFoodToEntry(@RequestBody MultiValueMap<String, String> map, HttpSession session) {             
        Food f = foodService.mapToFoodObject(map);

        Entry entry = (Entry)session.getAttribute("entry");
        ModelAndView mav = new ModelAndView("addEntry");
       
        List<Food> foodsConsumedList = entry.getFoodsConsumed();
        Boolean isDuplicate = false;
        for(Food food: foodsConsumedList){
            if (f.getName().equals(food.getName())){
                String duplicateError = "Please do not add duplicated foods! Increase the quantity instead!";
                mav.addObject("duplicateError", duplicateError);
                isDuplicate = true;
            }
        }
        
        if(!isDuplicate) {
            foodsConsumedList.add(f);
        }
        entry.setFoodsConsumed(foodsConsumedList);

        session.setAttribute("entry", entry);
        mav.addObject("entry", entry);

        return mav;
    }

    @GetMapping("/remove/{foodName}")
    public ModelAndView removeFoodItem(@PathVariable String foodName, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/entries/new/{entryId}");
        Entry entry = (Entry)session.getAttribute("entry");
        mav.addObject("entryId", entry.getEntryId());

        List<Food> foodsConsumedList = entry.getFoodsConsumed();
        foodsConsumedList.removeIf(f -> f.getName().equals(foodName));
        
        session.setAttribute("entry", entry);
        mav.addObject("entry", entry);
        return mav;
    }

    @PostMapping("/add")
    public ModelAndView handleEntryForm(@Valid @ModelAttribute Entry e, BindingResult results, 
    HttpSession session) {
     
        ModelAndView mav = new ModelAndView();
        
        
        Entry entry = (Entry)session.getAttribute("entry");

        // checking if food(s) are added
        if (entry.getFoodsConsumed().isEmpty()) {
            // System.out.println("in no foods");
            ObjectError noFoodError = new ObjectError("entry", 
            "Please add at least one food item!");
            results.addError(noFoodError);
        } else {
            // get the entry from session
            // then set the qty from the model attribute e
            for (int i = 0; i < e.getFoodsConsumed().size(); i++){
                Food wQtd = e.getFoodsConsumed().get(i);
                Food noQtd = entry.getFoodsConsumed().get(i);

                // checking for null and non positive quantity
                if (wQtd.getQuantity() == null || wQtd.getQuantity() < 1) {
                    FieldError qtdError = new FieldError("entry", "foodsConsumed[" + i + "].quantity", 
                    "Please enter a quantity more than 0!");
                    results.addError(qtdError);
                } else {
                    noQtd.setQuantity(wQtd.getQuantity());
                }
            }
        }
        
        e.setFoodsConsumed(entry.getFoodsConsumed());
        
        if (results.hasErrors()){
            mav.setViewName("addEntry");
            e.setEntryId(entry.getEntryId());
        
        } else {
            // TODO change redirect to homepage once set up or maybe a successfully saved page
            mav.setViewName("redirect:/");
            
            // System.out.println(e);
            // save entry to redis
            String userId = (String)session.getAttribute("userId");
            // System.out.println("e: " + e.getEntryId());
            // System.out.println("entry: " + entry.getEntryId());

            e.setEntryId(entry.getEntryId());
            if (entry.getConsumptionDate() != null && e.getConsumptionDate().compareTo(entry.getConsumptionDate()) != 0){
                entryService.deleteEntry(e.getEntryId(), userId);
            }
            entryService.saveEntry(userId, e);
            session.removeAttribute("entry");
        }

        return mav;
    }

    @GetMapping("/history")
    public ModelAndView showEntriesHistory(@RequestParam(required = false) MultiValueMap<String, String> range,
    HttpSession session) {
        ModelAndView mav = new ModelAndView("entryHistory");
        String userId = (String)session.getAttribute("userId");
        List<Entry> entriesList = entryService.getAllEntries(userId);

        // System.out.println(userId);
        try {
            entriesList = entryService.filterDates(entriesList, range);
        } catch (FilterDateException e) {
            mav.addObject("date", e.getMessage());
        }
        
        mav.addObject("entries", entriesList);
        return mav;
    }

    @GetMapping("/history/{entryId}")
    public ModelAndView viewEntry(@PathVariable String entryId, HttpSession session) {
        ModelAndView mav = new ModelAndView("entry");
        String userId = (String)session.getAttribute("userId");
        Entry entryFound = entryService.getEntryById(entryId, userId);

        List<Food> foodsConsumedList = foodService.requestForFoodsById(userId, entryFound);
        entryFound.setFoodsConsumed(foodsConsumedList);
        // System.out.println(entryFound);
        mav.addObject("entry", entryFound);

        return mav;
    }

    @GetMapping("/edit/{entryId}")
    public ModelAndView editEntry(@PathVariable String entryId, HttpSession session) {
        ModelAndView mav = new ModelAndView("addEntry");
        String userId = (String)session.getAttribute("userId");
        Entry entryFound = entryService.getEntryById(entryId, userId);
        entryFound.setFoodsConsumed(foodService.requestForFoodsById(userId, entryFound));
        // System.out.println(entryFound.getFoodsConsumed() + " in edit");

        session.setAttribute("entry", entryFound);
        mav.addObject("entry", entryFound);
        return mav;
    }

    // @PostMapping("/save/{entryId}")
    // public ModelAndView saveEditEntry(@Valid @ModelAttribute Entry e, BindingResult results, 
    // HttpSession session) {
    //     ModelAndView mav = new ModelAndView();
    //     // System.out.println("in save entry");
    //     // System.out.println(e.getConsumptionDate() + "in save entry");
    //     // System.out.println("after trying to print");

    //     // check if foods consumed is empty
    //     if (e.getFoodsConsumed().isEmpty()) {
    //         ObjectError noFoodError = new ObjectError("entry", 
    //         """
    //             The entry must have at least one food item. \n
    //             Please delete the entry otherwise!
    //                 """);
    //         results.addError(noFoodError);
    //     }

    //     if (results.hasErrors()){
    //         mav.setViewName("editEntry");
    //         // System.out.println("in results has errors");
        
    //     } else {
    //         // TODO change redirect to homepage once set up or maybe a successfully saved page
    //         mav.setViewName("redirect:/");
            
    //         // System.out.println(e);
    //         // save entry to redis
    //         String userId = (String)session.getAttribute("userId");
    //         entryService.saveEntry(userId, e);
        
    //     }

    //     return mav;
    // }

    @GetMapping("/delete/{entryId}")
    public ModelAndView deleteEntry(@PathVariable String entryId, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/");
        String userId = (String)session.getAttribute("userId");
        entryService.deleteEntry(entryId, userId);

        return mav;
    }
}
