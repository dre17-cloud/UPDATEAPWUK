package models;

import java.io.Serializable;

/**
 * Abstract base class for all SmartShip users.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String userId;
    protected String name;
    protected String email;
    protected String password;
    protected String role;

    public User() 
    {
        this.userId = "";
        this.name = "";
        this.email = "";
        this.password = "";
        this.role = "";
    }

    public User(String userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Copy constructor
    public User(User other) {
        this.userId = other.userId;
        this.name = other.name;
        this.email = other.email;
        this.password = other.password;
        this.role = other.role;
    }

    // Getters & Setters
    public String getUserId() { 
        return userId; 
    }
    public String getName() { 
        return name; 
    }
    public String getEmail() { 
        return email; 
    }
    public String getRole() { 
        return role; 
    }

    public void setUserId(String userId){
        this.userId=userId;
    }

    public void setName(String name){
        this.name=name;
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
     public void setRole(String role) { 
        this.role = role; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }
}
