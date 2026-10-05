package com.example.javafx_reactivo.modelos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {

    private final IntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    private final StringProperty name = new SimpleStringProperty(this, "name", "");
    private final StringProperty username = new SimpleStringProperty(this, "username", "");
    private final StringProperty email = new SimpleStringProperty(this, "email", "");
    private final StringProperty phone = new SimpleStringProperty(this, "phone", "");

    public Usuario() {
    }

    public Usuario(int id, String name, String username, String email, String phone) {
        setId(id);
        setName(name);
        setUsername(username);
        setEmail(email);
        setPhone(phone);
    }

    // --- ID ---
    @JsonProperty("id")
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // --- NAME ---
    @JsonProperty("name")
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // --- USERNAME ---
    @JsonProperty("username")
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    // --- EMAIL ---
    @JsonProperty("email")
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    // --- PHONE ---
    @JsonProperty("phone")
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }
}