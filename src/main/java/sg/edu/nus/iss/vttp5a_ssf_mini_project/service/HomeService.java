package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Profile;
import sg.edu.nus.iss.vttp5a_ssf_mini_project.repo.HashRepo;

@Service
public class HomeService {

    @Autowired
    HashRepo profileRepo;

    final String profileRedisKey = "profiles";

    public void saveProfile(Profile p) {
        JsonObject profileObject = Json.createObjectBuilder()
                .add("id", p.getId())
                .add("name", p.getName())
                .add("email", p.getEmail())
                .add("password", p.getPassword())
                .build();

        profileRepo.addToHash(profileRedisKey, p.getEmail(), profileObject.toString());;
    }
    
}
