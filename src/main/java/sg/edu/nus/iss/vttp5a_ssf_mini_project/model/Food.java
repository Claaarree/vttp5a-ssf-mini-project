package sg.edu.nus.iss.vttp5a_ssf_mini_project.model;

public class Food {
    private Long id;
    private String name;
    private String type;
    private String description;
    private String url;
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
        return id + "," + name + "," + type + "," + description + "," + url
                + "," + brand;
    }
    
    
}
