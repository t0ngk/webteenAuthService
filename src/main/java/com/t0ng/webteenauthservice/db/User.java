package com.t0ng.webteenauthservice.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "User")
public class User {
    @Id
    private String _id;
    private String email;
    private String username;
    private String password;
    private List role;
    private Date createdDate;
    private Date birthDate;

    public User() {}

    public User(String _id, String email, String username, String password, List role, Date createdDate, Date birthDate) {
        this._id = _id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdDate = createdDate;
        this.birthDate = birthDate;
    }
}
