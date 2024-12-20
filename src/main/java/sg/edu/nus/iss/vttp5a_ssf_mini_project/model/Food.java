package sg.edu.nus.iss.vttp5a_ssf_mini_project.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class Food {
    private Long id;
    private String customId;

    @NotBlank(message = "Please input a name for the food!")
    private String name;

    @NotBlank(message = "Please choose an approprite food type!")
    private String type;

    @NotBlank(message = "Please check all the known allergens or none if there are none!")
    List<String> allergens;

    // for vegetarian or vegan
    @NotBlank(message = "Please check if the food is suitable for vegetarians and vegans or neither!")
    List<String> preferences;

    private Long servingId;

    @NotBlank(message = "Please enter the serving size!")
    private String servingDescription;
    
    // units in kcal
    @NotEmpty(message = "Please enter a rough estimate of the food calories in kcal!")
    private Double calories;

    // units in g
    @NotEmpty(message = "Please enter a rough estimate of the carbohydrate content in g!")
    private Double carbohydrate;

    @NotEmpty(message = "Please enter a rough estimate of the protein content in g!")
    private Double protein;

    @NotEmpty(message = "Please enter a rough estimate of the fat content in g!")
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

    // TODO look at this to string!!
    @Override
    public String toString() {
        return id + "," + customId + "," + name + "," + type + "," + "," + url
                + "," + brand;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public Long getServingId() {
        return servingId;
    }

    public void setServingId(Long servingId) {
        this.servingId = servingId;
    }

    public String getServingDescription() {
        return servingDescription;
    }

    public void setServingDescription(String servingDescription) {
        this.servingDescription = servingDescription;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getCarbohydrate() {
        return carbohydrate;
    }

    public void setCarbohydrate(Double carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }
    
    
}
