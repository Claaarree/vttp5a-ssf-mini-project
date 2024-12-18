package sg.edu.nus.iss.vttp5a_ssf_mini_project.model;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public class Entry {

    private String entryId;
    
    @NotNull(message = "Please select the date of consumption!")
    @PastOrPresent(message = "Have you eaten yet? The date shouldn't be in the future!")
    private Date entryDate;

    @NotEmpty(message = "Please select the time of consumption!")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeOfConsumption;

    @NotEmpty(message = "Please add at least one food item eaten!")
    private List<Food> foodsConsumed;

    public Entry() {
        this.entryId = UUID.randomUUID().toString();
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public LocalTime getTimeOfConsumption() {
        return timeOfConsumption;
    }

    public void setTimeOfConsumption(LocalTime timeOfConsumption) {
        this.timeOfConsumption = timeOfConsumption;
    }

    public List<Food> getFoodsConsumed() {
        return foodsConsumed;
    }

    public void setFoodsConsumed(List<Food> foodsConsumed) {
        this.foodsConsumed = foodsConsumed;
    }

}
