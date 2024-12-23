package sg.edu.nus.iss.vttp5a_ssf_mini_project;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    
    @Value("${fatsecret.api.key}")
    public static String apiKey;
    
}
