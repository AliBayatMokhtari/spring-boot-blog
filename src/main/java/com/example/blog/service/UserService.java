package com.example.blog.service;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.blog.dto.UserResponse;
import com.example.blog.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> allUsers() {
        log.info("Getting all users list");

        var list = userRepository.findAll();

        return list.stream().map(item -> new UserResponse(item.getName(), item.getEmail())).toList();
    }
}
