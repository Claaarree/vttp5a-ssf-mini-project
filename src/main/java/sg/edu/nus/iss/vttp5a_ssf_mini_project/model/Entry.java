package sg.edu.nus.iss.vttp5a_ssf_mini_project.model;

import java.time.LocalDateTime;
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
    private Date consumptionDate;

    @NotEmpty(message = "Please select the time of consumption!")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime consumptionTime;

    // @NotEmpty(message = "Please select date and time of consuption!")
    // @PastOrPresent(message = "Have you eaten yet? The date and time shouldn't be in the future!")
    // @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm")
    // private LocalDateTime consumptionDateTime;

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

    public Date getConsumptionDate() {
        return consumptionDate;
    }

    public void setConsumptionDate(Date consumptionDate) {
        this.consumptionDate = consumptionDate;
    }

    public LocalTime getConsumptionTime() {
        return consumptionTime;
    }

    public void setConsumptionTime(LocalTime consumptionTime) {
        this.consumptionTime = consumptionTime;
    }

    public List<Food> getFoodsConsumed() {
        return foodsConsumed;
    }

    public void setFoodsConsumed(List<Food> foodsConsumed) {
        this.foodsConsumed = foodsConsumed;
    }

    // public LocalDateTime getConsumptionDateTime() {
    //     return consumptionDateTime;
    // }

    // public void setConsumptionDateTime(LocalDateTime consumptionDateTime) {
    //     this.consumptionDateTime = consumptionDateTime;
    // }

}
