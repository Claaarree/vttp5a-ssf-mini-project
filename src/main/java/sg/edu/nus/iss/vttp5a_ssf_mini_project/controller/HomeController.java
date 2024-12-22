package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
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

    @PostMapping("/login")
    public ModelAndView authenticateLogin(@RequestBody MultiValueMap <String, String> login, 
    BindingResult results, HttpSession session) {
        ModelAndView mav = new ModelAndView();

        String email = login.getFirst("email");
        String encodedPw = Base64.getEncoder()
                .withoutPadding()
                .encodeToString(login.getFirst("password").getBytes());

        if(homeService.profileExists(email)) {
            Profile p = homeService.getProfileByEmail(email);
            if (p.getPassword().equals(encodedPw)) {
                session.setAttribute("isAuthenticated", true);
                session.setAttribute("userId", p.getId());
                session.setAttribute("username", p.getName());
                // TODO set mapping for homepage
                mav.setViewName("redirect:/home");
            }else {
                // add error for wrong password
                ObjectError wrongPw = new ObjectError("wrongPw", 
                "The password you have entered is wrong... Please try again!");
                results.addError(wrongPw);
                mav.setViewName("loginPage");
            }

        } else {
            // add error for no profile exist, create new one
            ObjectError noProfile = new ObjectError("noProfile", 
            "It seems like you do not have an account! Please create one before trying to login!");
            results.addError(noProfile);
            mav.setViewName("loginPage");
        }

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
            homeService.saveProfile(p);
            // System.out.println(p.toString());

            mav.setViewName("redirect:/");
        }

        return mav;
    }

    @GetMapping("/home")
    public ModelAndView homePage(HttpSession session) {
        ModelAndView mav = new ModelAndView("homePage");
        String username = (String)session.getAttribute("username");
        mav.addObject("username", username);

        return mav;
    }
}
