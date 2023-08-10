package org.example.controller;

import org.example.constant.AuthorityConstant;
import org.example.constant.DefaultProfileImage;
import org.example.domain.User;
import org.example.dto.request.LoginRequest;
import org.example.dto.request.RegisterRequest;
import org.example.dto.response.GeneralResponse;
import org.example.dto.response.StatusResponse;
import org.example.security.AuthUserDetail;
import org.example.security.JwtProvider;
import org.example.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.example.constant.AuthorityConstant.AUTHORITY_USER;

@RestController
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private AuthenticationService authenticationService;

    @Autowired
    public void setUserService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private JwtProvider jwtProvider;

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    //User trying to log in with username and password
    @PostMapping("/login")
    public GeneralResponse login(@Valid @RequestBody LoginRequest request){

        Authentication authentication;
        System.out.println(request.getEmail());
        System.out.println(request.getPassword());

        try{
          authentication = authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
          );
        } catch (AuthenticationException e){
            throw new BadCredentialsException("Provided credential is invalid.");
        }

        //Successfully authenticated user will be stored in the authUserDetail object
        //getPrincipal() returns the user object
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();

        //A token wil be created using the username/email/userId and permission
        String token = jwtProvider.createToken(authUserDetail);

        //Returns the token as a response to the frontend/postman
        return GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .message("Hello! " + request.getEmail())
                        .statusCode(200)
                        .success(true)
                        .build())
                .data(token)
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody RegisterRequest registerRequest,
                                                    BindingResult bindingResult) {
        System.out.println("Registering user");

        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            // TODO: return error response
            errors.forEach(error -> System.out.println(
                    "ValidationError in " + error.getObjectName() + ": " + error.getDefaultMessage()));

            GeneralResponse response = GeneralResponse.builder()
                    .status(StatusResponse.builder()
                            .success(false)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("Data binding error")
                            .build())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");

        //form
        String firstname = registerRequest.getFirstname();
        String lastname = registerRequest.getLastname();
        String email = registerRequest.getEmail(); //unique -> need error response
        String password = registerRequest.getPassword();

        String type = AuthorityConstant.AUTHORITY_USER;
        String dateJoined = dtf.format(LocalDate.now());
        String profile_pic_link = registerRequest.getImage_url() == null?
                DefaultProfileImage.DEFAULT_IMAGE_URL : registerRequest.getImage_url();

        int active = 0;

        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setType(type);
        user.setEmail(email);
        user.setPassword(password);
        user.setDateJoined(dateJoined);
        user.setActive(active);
        user.setProfile_image_url(profile_pic_link);

        boolean registerSuccess = authenticationService.registerAuser(user);

        GeneralResponse response = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .message(registerSuccess? "User " + email + " has been create successfully."
                                :
                                "Fail to register!")
                        .statusCode(registerSuccess? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                        .success(registerSuccess)
                        .build())
                .data(type)
                .build();

        HttpStatus status = registerSuccess? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

}
