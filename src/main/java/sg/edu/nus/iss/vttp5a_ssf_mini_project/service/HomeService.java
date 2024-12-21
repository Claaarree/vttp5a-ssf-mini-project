package sg.edu.nus.iss.vttp5a_ssf_mini_project.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
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
    
    public Boolean profileExists(String email) {
        return profileRepo.isFieldExists(profileRedisKey, email);
    }

    public Profile getProfileByEmail(String email) {
        String profileString = profileRepo.getFieldValue(profileRedisKey, email);
        return jsonToProfile(profileString);
    }

    public Profile jsonToProfile(String profileString) {
        JsonReader jReader = Json.createReader(new StringReader(profileString));
        JsonObject profileObject = jReader.readObject();

        Profile p = new Profile();
        p.setId(profileObject.getString("id"));
        p.setName(profileObject.getString("name"));
        p.setEmail(profileObject.getString("email"));
        p.setPassword(profileObject.getString("password"));

        return p;
    }
}
