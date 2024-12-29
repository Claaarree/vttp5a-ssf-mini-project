package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Profile;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.service.ProfileService;

@Controller
@RequestMapping()
public class HomeController {

    @Autowired
    ProfileService profileService;

    @GetMapping("/login")
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

        if(profileService.profileExists(p.getEmail())){
            FieldError existingProfile = new FieldError("profile", "email", 
            "A profile with this email already exists! Please login to your account instead of creating a new one!");
            results.addError(existingProfile);
        }

        if (results.hasErrors()){
            mav.setViewName("profileForm");
        } else {
            p.setId(UUID.randomUUID().toString());
                        
            //save profile to redis
            profileService.saveProfile(p);
            // System.out.println(p.toString());

            mav.setViewName("redirect:/");
        }

        return mav;
    }

    @GetMapping()
    public ModelAndView homePage(HttpSession session, Authentication authentication) {
        ModelAndView mav = new ModelAndView("homePage");
        String name = authentication.getName();
        Profile p = profileService.getProfileByEmail(name);
        session.setAttribute("userId", p.getId());
        mav.addObject("userId", p.getId());
        mav.addObject("username", p.getName());

        return mav;
    }

    @GetMapping("/restDemo")
    public ModelAndView restDemo() {
        ModelAndView mav = new ModelAndView("restDemo");

        return mav;
    }
}
