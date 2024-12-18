package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.SearchService;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    SearchService searchService;
    
    @GetMapping()
    public ModelAndView getSearch(@RequestParam (required = false) String entryId) {
        ModelAndView mav = new ModelAndView("search");
        mav.addObject("entryId", entryId);
        System.out.println(entryId + "in get /");

        return mav;
    }

    @PostMapping()
    public ModelAndView showSearchResults(@RequestParam String searchTerm, 
    @RequestParam (required = false) String entryId) {
        ModelAndView mav = new ModelAndView();
        if (searchTerm.isBlank()){
            mav.setViewName("search");
            mav.addObject("blankError", "Search Term cannot be blank!");
        } else {
            mav.setViewName("redirect:/search/{searchTerm}/{pageNumber}/{entryId}");
            mav.addObject("pageNumber", "0");
            mav.addObject("searchTerm", searchTerm);
            if(entryId != null) {
                mav.addObject("entryId", entryId);
            } else {
                Entry entry = new Entry();
                mav.addObject("entryId", entry.getEntryId());
            }
            System.out.println(entryId + "in post /");
        }
        

        return mav;
    }

    @GetMapping("/{searchTerm}/{pageNumber}/{entryId}")
    public ModelAndView nextNpreviousPage(@PathVariable String searchTerm, 
    @PathVariable String pageNumber, @PathVariable String entryId) {
        System.out.println(entryId + "in get path");

        ModelAndView mav = new ModelAndView("search");
        List<Food> foodList = searchService.getSearch(searchTerm, pageNumber);
        mav.addObject("foods", foodList);
        mav.addObject("entryId", entryId);

        return mav;
    }

    @GetMapping("/{entryId}")
    public ModelAndView searchFromEntry(@PathVariable String entryId) {
        ModelAndView mav = new ModelAndView("redirect:/search");
        mav.addObject("entryId", entryId);
        System.out.println(entryId + "in get /search/entryid");
        
        return mav;
    }
}
