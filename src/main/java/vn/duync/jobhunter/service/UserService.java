package vn.duync.jobhunter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.duync.jobhunter.domain.User;

@Service
public interface UserService {
    void handleCreateUser(User user);

    void handleDeleteUserById(Long id);

    User getOneUserById(Long id);

    List<User> getAllUsers();

    User handleUpdateUser(Long id, User user);

    User getOneUserByEmail(String email);
}
