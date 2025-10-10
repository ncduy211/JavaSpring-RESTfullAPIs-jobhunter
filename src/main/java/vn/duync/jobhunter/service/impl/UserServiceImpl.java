package vn.duync.jobhunter.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.duync.jobhunter.domain.User;
import vn.duync.jobhunter.domain.dto.Meta;
import vn.duync.jobhunter.domain.dto.ResultPaginationDTO;
import vn.duync.jobhunter.repository.UserRepository;
import vn.duync.jobhunter.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handleCreateUser(User user) {
        this.userRepository.save(user);
    }

    public User getOneUserById(Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        return null;
    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO resultPage = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPage(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        resultPage.setMeta(meta);
        resultPage.setResult(pageUser.getContent());
        return resultPage;
    }

    public User handleUpdateUser(Long id, User user) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPassword(user.getPassword());

            return this.userRepository.save(currentUser);
        }
        return null;
    }

    public void handleDeleteUserById(Long id) {
        this.userRepository.deleteById(id);
    }

    public User getOneUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

}
