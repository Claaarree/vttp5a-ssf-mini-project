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
        String entryId = entry.getEntryId();

        System.out.println(entryId + "in get path");

        ModelAndView mav = new ModelAndView("search");
        List<Food> foodList = searchService.getSearch(searchTerm, pageNumber);
        mav.addObject("foods", foodList);

        if(entryId != null) {
            mav.addObject("entryId", entryId);
        } else {
            Entry e = new Entry();
            mav.addObject("entryId", entry.getEntryId());
            session.setAttribute("entry", e);
        }

        return mav;
    }
}
