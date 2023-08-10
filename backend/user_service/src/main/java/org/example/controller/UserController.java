package org.example.controller;


import org.example.constant.AuthorityConstant;
import org.example.constant.VerifyNeed;
import org.example.dto.request.EditProfileRequest;
import org.example.dto.response.GeneralResponse;
import org.example.dto.response.StatusResponse;
import org.example.exception.CantBeModifyException;
import org.example.service.UserService;
import org.example.domain.User;
import org.example.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/email")
    @PreAuthorize("hasAnyAuthority('super_admin_user')")
    public ResponseEntity<GeneralResponse> activateUserByUserEmail(@RequestParam(name="email") String emailAddr)
            throws CantBeModifyException {

        boolean isOk = userService.activateUserByUserEmail(emailAddr);

        GeneralResponse generalResponse = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .success(true)
                        .statusCode(!isOk? HttpStatus.BAD_REQUEST.value() : HttpStatus.OK.value())
                        .message("User status is changed successfully.")
                        .build())
                .data(isOk)
                .build();

        System.out.println(generalResponse.getData());
        return ResponseEntity.ok(generalResponse);
    }

    @GetMapping("/email/{emailAddr}")
    @PreAuthorize("hasAnyAuthority('user', 'admin_user','super_admin_user')")
    public ResponseEntity<GeneralResponse> getUserByEmail(@PathVariable String emailAddr) {

        System.out.println("Email: " + emailAddr);
        User user = userService.getUserUsingUsername(emailAddr);

        GeneralResponse generalResponse = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("User is obtained by email")
                        .build())
                .data(user)
                .build();

        System.out.println(generalResponse.getData());
        return ResponseEntity.ok(generalResponse);
    }

    //user/rergister

    //user/2
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('user', 'admin_user','super_admin_user')")
    public ResponseEntity<GeneralResponse> getUserByUserID(@PathVariable("id") int id) {

        User user = userService.getUserByUserID(id);

        GeneralResponse response = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .success(false)
                        .statusCode(user == null?HttpStatus.BAD_REQUEST.value() : HttpStatus.OK.value())
                        .message(user == null? "Use not exist!" : "Successfully get a user")
                        .build())
                .data(user)
                .build();

        return ResponseEntity.status(user == null? HttpStatus.BAD_REQUEST:HttpStatus.OK).body(response);
    }

    //user/edit
    @PatchMapping("/edit")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<GeneralResponse> editUserProfileInfo(@RequestBody EditProfileRequest request,
                                                               @AuthenticationPrincipal AuthUserDetail authUserDetail)
            throws CantBeModifyException {
        int userID = authUserDetail.getUserID();
        System.out.println("UserID: " + userID);

        int action = userService.editUserProfileInfo(userID, request);

        return ResponseEntity.status(action == VerifyNeed.SOMETHING_WRONG? HttpStatus.BAD_REQUEST : HttpStatus.OK)
                .body(GeneralResponse.builder()
                        .status(StatusResponse.builder()
                                .message(action == VerifyNeed.SOMETHING_WRONG? "Something went wrong! Action aborted"
                                        : action == VerifyNeed.NEED_DELETE_TOKEN?
                                                "Email has changed, and token need to be removed" : "Modify ok!")
                                .success(action != VerifyNeed.SOMETHING_WRONG)
                                .statusCode(action == VerifyNeed.SOMETHING_WRONG?
                                        HttpStatus.BAD_REQUEST.value() : HttpStatus.OK.value())
                                .build())
                        .data(action)
                        .build());
    }

    //user/all
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('admin_user', 'super_admin_user')")
    public ResponseEntity<GeneralResponse> getAllUsers() {

        List<User> users = userService.getAllUsers();
        GeneralResponse response = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .build())
                .data(users)
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status/{id}/{active}")
    @PreAuthorize("hasAnyAuthority('admin_user', 'super_admin_user')")
    public ResponseEntity<GeneralResponse> setActiveStatus(@PathVariable("id") int id,
                                                           @PathVariable("active") int active,
                                                           @AuthenticationPrincipal AuthUserDetail authUserDetail)
            throws CantBeModifyException {


        boolean isSuccess = userService.setActiveStatus(authUserDetail,id, active);

        GeneralResponse response = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .success(isSuccess)
                        .statusCode(isSuccess? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(isSuccess? "Successfully change status for user " + id
                                : "Fail to change status for user " + id)
                        .build())
                .build();

        return ResponseEntity
                .status(isSuccess? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(response);
    }

    @PatchMapping("/status/email")
    @PreAuthorize("hasAnyAuthority('user')")
    public ResponseEntity<GeneralResponse> activateAUserByEmail(
            @AuthenticationPrincipal AuthUserDetail authUserDetail) throws CantBeModifyException {

        int userID = authUserDetail.getUserID();

        boolean isSuccess = userService.activateAUserByEmail(authUserDetail,userID);

        GeneralResponse response = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .success(isSuccess)
                        .statusCode(isSuccess? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(isSuccess? "Email change status for user " + userID
                                : "Email Fail to change status for user " + userID)
                        .build())
                .build();

        return ResponseEntity
                .status(isSuccess? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(response);

    }

    @PatchMapping("/status/super/{id}/{role}")
    @PreAuthorize("hasAnyAuthority('super_admin_user')")
    public ResponseEntity<GeneralResponse> adminAssign_Or_revoke(@PathVariable("id") int id,
                                                                 @PathVariable("role") int role,
                                                      @AuthenticationPrincipal AuthUserDetail authUserDetail)
            throws CantBeModifyException {

        String type;
        if(role == 0) {
            type = AuthorityConstant.AUTHORITY_USER;
        } else if(role == 1) {
            type = AuthorityConstant.AUTHORITY_ADMIN;
        } else if(role == 2){
            type = AuthorityConstant.AUTHORITY_SUPER_ADMIN;
        } else {
            throw new CantBeModifyException("Unknown authority type. ");
        }

        boolean isSuccess = userService.adminAssign_Or_revoke(authUserDetail,id, type);

        GeneralResponse response = GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .success(isSuccess)
                        .statusCode(isSuccess? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(isSuccess? "Successfully assign/revoke admin for user " + id
                                : "Fail to assign/revoke admin for user " + id)
                        .build())
                .build();

        return ResponseEntity
                .status(isSuccess? HttpStatus.OK.value() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(response);
    }


}
