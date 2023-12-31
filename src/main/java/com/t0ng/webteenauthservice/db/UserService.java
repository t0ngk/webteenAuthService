package com.t0ng.webteenauthservice.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }
    public User findById(String id) {
        return this.userRepository.findById(id).orElse(null);
    }
}
