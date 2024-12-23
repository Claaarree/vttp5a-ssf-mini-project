package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.FoodService;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.SearchService;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    SearchService searchService;

    @Autowired
    FoodService foodService;

    @GetMapping()
    public ModelAndView getSearch(HttpSession session) {
        ModelAndView mav = new ModelAndView("search");
        String userId = (String) session.getAttribute("userId");
        List<Food> customFoodsList = foodService.getAllCustomFoods(userId);
        mav.addObject("foods", customFoodsList);

        Entry entry = (Entry)session.getAttribute("entry");
        
        if(entry != null) {
            String entryId = entry.getEntryId();
            mav.addObject("entryId", entryId);
        } else {
            Entry e = new Entry();
            mav.addObject("entryId", e.getEntryId());
            session.setAttribute("entry", e);
        }

        return mav;
    }

    @PostMapping()
    public ModelAndView showSearchResults(@RequestParam String searchTerm) {
        ModelAndView mav = new ModelAndView();

        if (searchTerm.isBlank()){
            mav.setViewName("search");
            mav.addObject("blankError", "Search Term cannot be blank!");
        } else {
            mav.setViewName("redirect:/search/{searchTerm}/{pageNumber}");
            mav.addObject("pageNumber", "0");
            mav.addObject("searchTerm", searchTerm);
        }
        
        return mav;
    }

    @GetMapping("/{searchTerm}/{pageNumber}")
    public ModelAndView nextNpreviousPage(@PathVariable String searchTerm, 
    @PathVariable String pageNumber, HttpSession session) {
        Entry entry = (Entry)session.getAttribute("entry");
        
        // System.out.println(entryId + "in get path");
        
        ModelAndView mav = new ModelAndView("search");
        List<Food> foodList = searchService.getSearch(searchTerm, pageNumber);
        mav.addObject("foods", foodList);
        
        if(entry != null) {
            String entryId = entry.getEntryId();
            mav.addObject("entryId", entryId);
        } else {
            Entry e = new Entry();
            mav.addObject("entryId", e.getEntryId());
            session.setAttribute("entry", e);
        }

        return mav;
    }

    @PostMapping("/customFood")
    public ModelAndView getCustomFoods(@RequestParam String customSearch, HttpSession session) {
        ModelAndView mav = new ModelAndView("search");

        String userId = (String) session.getAttribute("userId");
        Entry entry = (Entry)session.getAttribute("entry");
        mav.addObject("entryId", entry.getEntryId());
        
        // can i get food by name?
        List<Food> foodsFound = foodService.getFoodByName(customSearch, userId);

        mav.addObject("foods", foodsFound);

        return mav;
    }
}
