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

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.SearchService;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    SearchService searchService;
    
    @GetMapping()
    public ModelAndView getSearch() {
        ModelAndView mav = new ModelAndView("search");

        return mav;
    }

    @PostMapping()
    public ModelAndView showSearchResults(@RequestParam String searchTerm) {
        
        ModelAndView mav = new ModelAndView("redirect:/search/{searchTerm}/{pageNumber}");
        mav.addObject("pageNumber", "0");
        mav.addObject("searchTerm", searchTerm);

        return mav;
    }

    @GetMapping("/{searchTerm}/{pageNumber}")
    public ModelAndView nextNpreviousPage(@PathVariable String searchTerm, @PathVariable String pageNumber) {
        ModelAndView mav = new ModelAndView("search");
        List<Food> foodList = searchService.getSearch(searchTerm, pageNumber);
        mav.addObject("foods", foodList);

        return mav;
    }
}
