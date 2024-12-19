package sg.edu.nus.iss.vttp5a_ssf_mini_project.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Food {
    private Long id;
    private String customId;

    @NotBlank(message = "Please input a name for the food!")
    private String name;

    @NotBlank(message = "Please choose an approprite food type!")
    private String type;

    List<String> allergens;

    // for vegetarian or vegan
    List<String> preferences;

    private Long servingId;
    private String servingDescription;
    // units in kcal
    private Double calories;

    // units in g
    private Double carbohydrate;
    private Double protein;
    private Double fat;

    private String url;

    @NotBlank(message = "Please input the brand of the food or NA if the food type is Generic!")
    @Pattern(regexp = "/[\\w].*", message = "Enter NA if the food type is Generic! Do not start with a special chracter!")
    private String brand;
    
    public Food() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return id + "," + customId + "," + name + "," + type + "," + description + "," + url
                + "," + brand;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }
    
    
}
