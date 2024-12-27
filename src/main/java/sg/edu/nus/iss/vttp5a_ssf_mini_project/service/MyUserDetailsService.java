package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Profile;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    ProfileService profileService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null) {
            throw new UsernameNotFoundException(email + "does not have an account!");
        }

        return profile;
    }
    
}
