package sg.edu.nus.iss.vttp5a_ssf_mini_project.model;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class Food {
    private Long id;
    private String customId;

    @NotBlank(message = "Please input a name for the food!")
    private String name;

    @NotBlank(message = "Please choose an approprite food type!")
    private String type;

    @NotEmpty(message = "Please check all the known allergens or none if there are none!")
    private List<String> allergens;

    // @NotEmpty(message = "Please check if the food is suitable for vegetarians!")
    private Boolean isVegetarian = false;

    // @NotEmpty(message = "Please check if the food is suitable for vegans!")
    private Boolean isVegan = false;

    private Long servingId;

    @NotBlank(message = "Please enter the serving size!")
    private String servingDescription;
    
    // units in kcal
    @NotNull(message = "Please enter a rough estimate of the food calories in kcal!")
    @Min(value = (long) 0.0, message = "The value has to be greater than 0.0!")
    private Double calories;

    // units in g
    @NotNull(message = "Please enter a rough estimate of the carbohydrate content in g!")
    @Min(value = (long) 0.0, message = "The value has to be greater than 0.0!")
    private Double carbohydrate;

    @NotNull(message = "Please enter a rough estimate of the protein content in g!")
    @Min(value = (long) 0.0, message = "The value has to be greater than 0.0!")
    private Double protein;

    @NotNull(message = "Please enter a rough estimate of the fat content in g!")
    @Min(value = (long) 0.0, message = "The value has to be greater than 0.0!")
    private Double fat;

    private String url;

    @NotBlank(message = "Please input the brand of the food!")
    @Pattern(regexp = "[\\w].*", message = "Enter NA if the food type is Generic! Do not start with a special chracter!")
    private String brand;
    
    private Integer quantity;

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

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Boolean getIsVegan() {
        return isVegan;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String toStringForEntry() {
        return name + "," + servingDescription + "," + calories
                + "," + carbohydrate + "," + protein + "," + fat;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id + ",");
        sb.append(customId + ",");
        sb.append("\"" + name + "\",");
        sb.append(type + ",");
        for (String a : allergens){
            sb.append("|" + a);
        }
        sb.append("," + isVegetarian);
        sb.append("," + isVegan);
        sb.append("," + servingId);
        sb.append("," + servingDescription);
        sb.append("," + calories);
        sb.append("," + carbohydrate);
        sb.append("," + protein);
        sb.append("," + fat);
        sb.append("," + url);
        sb.append("," + brand);
        sb.append("," + quantity);

        return sb.toString();
    }


    
}
