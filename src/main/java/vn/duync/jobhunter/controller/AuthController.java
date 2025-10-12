package vn.duync.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.duync.jobhunter.domain.User;
import vn.duync.jobhunter.domain.dto.LoginDTO;
import vn.duync.jobhunter.domain.dto.ResLoginDTO;
import vn.duync.jobhunter.service.impl.UserServiceImpl;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.duync.jobhunter.util.SecurityUtil;
import vn.duync.jobhunter.util.annotation.ApiMessage;
import vn.duync.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
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
    @ApiMessage("Login")
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
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUserLogin());
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
    public ResponseEntity<ResLoginDTO.UserGetAccount> getMethodName() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userServiceImpl.getOneUserByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setName(currentUser.getName());
            userLogin.setEmail(currentUser.getEmail());
            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refreshToken") String refreshTokenStr) {
        // Check token
        Jwt decodeToken = this.securityUtil.checkValidRefreshToken(refreshTokenStr);

        // Check user
        String email = decodeToken.getSubject();
        User currentUser = this.userServiceImpl.getOneUserByRefreshTokenAndEmail(refreshTokenStr, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh token không hợp lệ");
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDTO = this.userServiceImpl.getOneUserByEmail(email);
        if (currentUserDTO != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDTO.getId(),
                    currentUserDTO.getName(),
                    currentUserDTO.getEmail());

            resLoginDTO.setUserLogin(userLogin);
        }

        // Tạo token JWT
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO.getUserLogin());

        resLoginDTO.setAccessToken(accessToken);

        // Tạo refresh token và update refresh token trong user
        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);

        this.userServiceImpl.updateToken(newRefreshToken, email);

        // Set cookies
        ResponseCookie resCookies = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout")
    public ResponseEntity<Void> logout() {
        // Tìm và xóa refresh token
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userServiceImpl.getOneUserByEmail(email);

        currentUser.setRefreshToken(null);
        this.userServiceImpl.updateToken(null, email);

        // Set cookies (để thời gian hết hạn là 0)
        ResponseCookie deleteCookies = ResponseCookie
                .from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookies.toString())
                .body(null);
    }

}
