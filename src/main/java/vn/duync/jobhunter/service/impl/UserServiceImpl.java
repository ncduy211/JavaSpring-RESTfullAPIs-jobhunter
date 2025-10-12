package vn.duync.jobhunter.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.duync.jobhunter.domain.User;
import vn.duync.jobhunter.domain.response.ResCreateUserDTO;
import vn.duync.jobhunter.domain.response.ResGetUserDTO;
import vn.duync.jobhunter.domain.response.ResUpdateUserDTO;
import vn.duync.jobhunter.domain.response.ResultPaginationDTO;
import vn.duync.jobhunter.repository.UserRepository;
import vn.duync.jobhunter.service.UserService;
import vn.duync.jobhunter.util.error.IdInvalidException;
import vn.duync.jobhunter.util.error.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handleCreateUser(User user) {
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new IdInvalidException("Email " + user.getEmail() + "đã tồn tại!");
        } else {
            this.userRepository.save(user);
        }

    }

    public User getOneUserById(Long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new ResourceNotFoundException("Không tìm thấy user với ID = " + id);
        }
    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO resultPage = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPage(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        List<ResGetUserDTO> listUser = new ArrayList<>();
        for (User user : pageUser.getContent()) {
            listUser.add(convertToResGetUserDTO(user));
        }

        resultPage.setMeta(meta);
        resultPage.setResult(listUser);
        return resultPage;
    }

    public User handleUpdateUser(Long id, User user) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            currentUser.setName(user.getName());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());

            return this.userRepository.save(currentUser);
        } else {
            throw new ResourceNotFoundException("Không tìm thấy user với ID = " + id);
        }
    }

    public void handleDeleteUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            userRepository.delete(existingUser);
        } else {
            throw new ResourceNotFoundException("Không tìm thấy user với ID = " + id);
        }
    }

    public User getOneUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreateAt(user.getCreateAt());

        return res;
    }

    public ResGetUserDTO convertToResGetUserDTO(User user) {
        ResGetUserDTO res = new ResGetUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreateAt(user.getCreateAt());
        res.setUpdateAt(user.getUpdateAt());

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdateAt(user.getUpdateAt());

        return res;
    }

    public void updateToken(String token, String email) {
        User currentUser = this.getOneUserByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);

            this.userRepository.save(currentUser);
        }
    }

    public User getOneUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
