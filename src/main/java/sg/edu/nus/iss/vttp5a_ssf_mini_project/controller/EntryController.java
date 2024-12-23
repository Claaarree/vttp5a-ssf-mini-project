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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.EntryService;

@Controller
@RequestMapping("/entries")
public class EntryController {

    @Autowired
    EntryService entryService;

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
    public ModelAndView entryWithId(@PathVariable String entryId, HttpSession session) {
        ModelAndView mav = new ModelAndView("addEntry");
        Entry entry = (Entry)session.getAttribute("entry");
    
        mav.addObject("entry", entry);
        return mav;
    }
    
    @PostMapping("/new/{entryId}")
    public ModelAndView addFoodToEntry(@RequestBody MultiValueMap<String, String> map, HttpSession session) {     
        // System.out.println("map keyset: " + map.keySet());
        // System.out.println("foodToAdd value: " + map.getFirst("foodToAdd").toString());
        
        Food f = entryService.mapToFoodObject(map);
        // System.out.println(f.toString() + "in post");

        ModelAndView mav = new ModelAndView("redirect:/entries/new/{entryId}");
        Entry entry = (Entry)session.getAttribute("entry");
        List<Food> foodsConsumedList = entry.getFoodsConsumed();
        foodsConsumedList.add(f);
        entry.setFoodsConsumed(foodsConsumedList);

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
        
        if (results.hasErrors()){
            mav.setViewName("addEntry");
            e.setEntryId(entry.getEntryId());
            e.setFoodsConsumed(entry.getFoodsConsumed());
        
        } else {
            // TODO change redirect to homepage once set up or maybe a successfully saved page
            mav.setViewName("redirect:/search");
            session.removeAttribute("entry");

            System.out.println(entry.getFoodsConsumed());
            // save entry to redis
        }

        return mav;
    }

}
