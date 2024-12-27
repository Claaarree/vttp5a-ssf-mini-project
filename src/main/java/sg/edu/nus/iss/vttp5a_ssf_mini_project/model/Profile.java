package sg.edu.nus.iss.vttp5a_ssf_mini_project.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Profile implements UserDetails {
    
    private String id;

    @NotBlank(message = "Please enter your name to create a new profile!")
    private String name;

    @NotBlank(message = "Please input a new password for your profile!")
    @Size(min = 8, message = "Your password must be at least 8 characters!")
    private String password;

    @NotBlank(message = "Please input a valid email!")
    @Email(message = "Please input a valid email!")
    private String email;
   
    public Profile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Profile [id=" + id + ", name=" + name + ", password=" + password + ", email=" + email + "]";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    @Override
    public String getUsername() {
       return this.email;
    }

}
