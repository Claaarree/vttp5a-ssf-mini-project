package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Food;

@Controller
@RequestMapping("/foods")
public class FoodController {

    @GetMapping("/new")
    public ModelAndView newFoodForm(HttpSession session) {
        ModelAndView mav = new ModelAndView("foodForm");
        Food f = new Food();
        // later on can check if is id NaN
        // need to convert back to double first...
        // maybe if I don't set it then i can chekc for null?
        // f.setId(Double.doubleToLongBits(Math.sqrt(-1)));
    
        f.setCustomId("CUSTOM" + UUID.randomUUID().toString());
        mav.addObject("food", f);
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
    
}
