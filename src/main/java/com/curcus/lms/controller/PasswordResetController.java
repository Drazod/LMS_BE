package com.curcus.lms.controller;

import com.curcus.lms.model.request.PasswordResetRequest;
import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.ResponseCode;
import com.curcus.lms.service.impl.PasswordResetImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/password-reset")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class PasswordResetController {
    @Autowired
    PasswordResetImpl passwordReset;
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Boolean>> request(@Valid @RequestBody PasswordResetRequest emailRequest,
                                                        BindingResult bindingResult) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        try {
            if (bindingResult.hasErrors()) {
                apiResponse.error(ResponseCode.getError(1));
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }
            Boolean result = passwordReset.requestPasswordReset(emailRequest.getEmail());
            if (result) {
                apiResponse.ok(true);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                apiResponse.error(ResponseCode.getError(8));
                return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            apiResponse.error(ResponseCode.getError(23));
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reset")
    public ResponseEntity<ApiResponse<Boolean>> reset(@RequestParam String token) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        try {
            Boolean result = passwordReset.resetPassword(token);
            if (result) {
                apiResponse.ok(true);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                apiResponse.error(ResponseCode.getError(3));
                return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
            }
        } catch(Exception e) {
                apiResponse.error(ResponseCode.getError(23));
                return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
