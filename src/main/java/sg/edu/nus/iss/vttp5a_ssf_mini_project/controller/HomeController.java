package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Profile;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.HomeService;

@Controller
@RequestMapping()
public class HomeController {

    @Autowired
    HomeService homeService;

    @GetMapping
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView("loginPage");

        return mav;
    }
    
    @GetMapping("/profiles/new")
    public ModelAndView showNewProfileForm() {
        ModelAndView mav = new ModelAndView("profileForm");
        mav.addObject("profile", new Profile());

        return mav;
    }

    @PostMapping("/profiles/new")
    public ModelAndView handleProfileForm(@Valid @ModelAttribute Profile p, BindingResult results) {
        ModelAndView mav = new ModelAndView();

        if (results.hasErrors()){
            mav.setViewName("profileForm");
        } else {
            p.setId(UUID.randomUUID().toString());
            byte[] passwordPT = p.getPassword().getBytes();
            String encodedPw = Base64.getEncoder().withoutPadding().encodeToString(passwordPT);
            p.setPassword(encodedPw);
            //save profile to redis
            // homeService.saveProfile(p);
            System.out.println(p.toString());
            // to change to login later
            mav.setViewName("redirect:/search");
        }

        return mav;
    }
}
