package vn.duync.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.duync.jobhunter.domain.User;
import vn.duync.jobhunter.domain.dto.ResultPaginationDTO;
import vn.duync.jobhunter.service.impl.UserServiceImpl;
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
    public ResponseEntity<User> createUser(@Valid @RequestBody User newUser) {
        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        this.userServiceImpl.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getOneUserById(@PathVariable("id") Long id) {
        User user = this.userServiceImpl.getOneUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @RequestParam("current") Optional<String> currentPageOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String stringCurrentPage = currentPageOptional.isPresent() ? currentPageOptional.get() : "";
        int currentPage = Integer.parseInt(stringCurrentPage);

        String stringPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
        int pageSize = Integer.parseInt(stringPageSize);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        ResultPaginationDTO listUsers = this.userServiceImpl.getAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listUsers);
    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody User updatedUser) {
        User user = this.userServiceImpl.handleUpdateUser(id, updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        this.userServiceImpl.handleDeleteUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

}
