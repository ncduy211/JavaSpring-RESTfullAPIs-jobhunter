package vn.duync.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.duync.jobhunter.domain.User;
import vn.duync.jobhunter.domain.dto.LoginDTO;
import vn.duync.jobhunter.domain.dto.ResLoginDTO;
import vn.duync.jobhunter.service.impl.UserServiceImpl;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.duync.jobhunter.util.SecurityUtil;
import vn.duync.jobhunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SecurityUtil securityUtil;

    private final UserServiceImpl userServiceImpl;

    @Value("${duync.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserServiceImpl userServiceImpl) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {

        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // Xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUser = this.userServiceImpl.getOneUserByEmail(loginDTO.getUsername());
        if (currentUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getName(),
                    currentUser.getEmail());

            resLoginDTO.setUserLogin(userLogin);
        }

        // Tạo token JWT
        String accessToken = this.securityUtil.createAccessToken(authentication, resLoginDTO.getUserLogin());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        resLoginDTO.setAccessToken(accessToken);

        // Tạo refresh token và update refresh token trong user
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        this.userServiceImpl.updateToken(refreshToken, loginDTO.getUsername());

        // Set cookies
        ResponseCookie resCookies = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get account")
    public ResponseEntity<ResLoginDTO.UserLogin> getMethodName() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userServiceImpl.getOneUserByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setName(currentUser.getName());
            userLogin.setEmail(currentUser.getEmail());
        }

        return ResponseEntity.ok().body(userLogin);
    }

}
