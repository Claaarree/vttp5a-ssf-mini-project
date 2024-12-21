package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Profile;

@Controller
@RequestMapping()
public class HomeController {

    @GetMapping
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("loginPage");
        mav.addObject("profile", new Profile());
        
        return mav;
    }
    
}
