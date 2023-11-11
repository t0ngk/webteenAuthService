package com.t0ng.webteenauthservice.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "User")
public class User {
    @Id
    private String _id;
    private String username;
    private String password;

    public User() {}

    public User(String _id, String username, String password) {
        this._id = _id;
        this.username = username;
        this.password = password;
    }
}
