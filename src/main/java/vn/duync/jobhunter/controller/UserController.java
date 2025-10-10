package vn.duync.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.duync.jobhunter.domain.User;
import vn.duync.jobhunter.domain.dto.ResultPaginationDTO;
import vn.duync.jobhunter.service.impl.UserServiceImpl;
import vn.duync.jobhunter.util.annotation.ApiMessage;
import vn.duync.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserServiceImpl userServiceImpl, PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users/create")
    @ApiMessage("Create user")
    public ResponseEntity<User> createUser(@Valid @RequestBody User newUser) {
        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        this.userServiceImpl.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get one user by id")
    public ResponseEntity<User> getOneUserById(@PathVariable("id") Long id) {
        User user = this.userServiceImpl.getOneUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    @ApiMessage("Get all user")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {

        ResultPaginationDTO listUsers = this.userServiceImpl.getAllUsers(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listUsers);
    }

    @PutMapping("/users/update/{id}")
    @ApiMessage("Update user")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody User updatedUser) {
        User user = this.userServiceImpl.handleUpdateUser(id, updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/users/delete/{id}")
    @ApiMessage("Delete user")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        this.userServiceImpl.handleDeleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

}
