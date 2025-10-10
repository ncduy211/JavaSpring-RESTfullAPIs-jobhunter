package vn.duync.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.duync.jobhunter.domain.User;
import vn.duync.jobhunter.domain.dto.ResultPaginationDTO;

public interface UserService {
    void handleCreateUser(User user);

    ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable);

    User getOneUserById(Long id);

    User handleUpdateUser(Long id, User user);

    void handleDeleteUserById(Long id);

    User getOneUserByEmail(String email);
}
