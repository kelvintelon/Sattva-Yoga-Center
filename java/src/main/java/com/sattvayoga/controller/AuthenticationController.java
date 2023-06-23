package com.sattvayoga.controller;

import javax.validation.Valid;

import com.sattvayoga.dao.EmailSenderService;
import com.sattvayoga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.security.jwt.JWTFilter;
import com.sattvayoga.security.jwt.TokenProvider;

@RestController
@CrossOrigin
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    @Autowired
    private EmailSenderService senderService;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        
        YogaUser user = userDao.findByUsername(loginDto.getUsername());

        // set activated boolean to true for user in the database
        userDao.updateUserToActivated(user.getId());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new LoginResponse(jwt, user), httpHeaders, HttpStatus.OK);
    }


    @RequestMapping(value= "/emailResetLink/{email}", method = RequestMethod.GET)
    public String sendEmailResetLink(@PathVariable String email) {
        // find the username w/ email and plug it in
        YogaUser yogaUser = new YogaUser();

        // info@sattva-yoga-center.com
        // telon.kelvin77@gmail.com
        if (email.equals("info@sattva-yoga-center.com")) {
            yogaUser.setUsername("admin");
        } else {
            yogaUser = userDao.findByEmail(email);
        }

        // create token with user
        String jwt = tokenProvider.createEmailToken(yogaUser.getUsername());
        // prepare the link to send in mail
        // TODO: Change the following line when you have to
//        String website = "http://sattva-yoga.netlify.app/resetLink/";
        String website = "http://localhost:8080/resetLink?token=";
        String resetLink = website + jwt;
        senderService.sendEmail(email,"Sattva Yoga Center Email Reset Link For Account","Your Reset Link is: " +resetLink +  "\n" + "Note: Reset Link expires in 2 days" + "\n" + "PLEASE DO NOT REPLY BACK TO THIS EMAIL" + "\n" + "- Sattva Yoga Center");
        return email;
    }

    @RequestMapping(value = "/resetUsernameAndPassword", method = RequestMethod.PUT)
    public void resetUsernameAndPassword(@Valid @RequestBody ResetUsernameAndPasswordDTO newUser) {

        try {
            YogaUser user = userDao.findByUsername(newUser.getUsername());
            throw new UserAlreadyExistsException();
        } catch (UsernameNotFoundException e) {

            // validate the token
            String emailToken = newUser.getToken();
            String usernameToUpdate = "";
            if (tokenProvider.validateToken(emailToken)) {
                //extract the username from the token so you know which user to update
                usernameToUpdate = tokenProvider.getUsernameClaim(emailToken);

                // Don't create a new account, just update it on the email
                //  Use ResetUsernameAndPasswordDTO
                userDao.updateUsernameAndPassword(newUser.getUsername(),newUser.getPassword(), usernameToUpdate);
            }

        }
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.PUT)
    public void resetPassword(@Valid @RequestBody ResetPasswordDTO newPassword) {
        String emailToken = newPassword.getToken();
        String usernameToUpdate = "";
        if (tokenProvider.validateToken(emailToken)) {
            //extract the username from the token so you know which user to update
            usernameToUpdate = tokenProvider.getUsernameClaim(emailToken);

            // Don't create a new account, just update it on the email
            //  Use ResetUsernameAndPasswordDTO
            userDao.updatePassword(newPassword.getPassword(), usernameToUpdate);
        }
    }

    @RequestMapping(value = "/getUsernameFromEmailToken/{emailToken}", method = RequestMethod.GET)
    public String getUsernameFromEmailToken(@PathVariable String emailToken) {
        if (tokenProvider.validateToken(emailToken)) {
            return tokenProvider.getUsernameClaim(emailToken);
        }
        return "Invalid JWT";
    }

    @RequestMapping(value = "/validateEmailToken/{emailToken}", method = RequestMethod.GET)
    public String isTokenValid(@PathVariable String emailToken) {
        if (tokenProvider.validateToken(emailToken)) {
            return "Valid JWT";
        }
        return "Invalid JWT";
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDto newUser) {

        try {
            YogaUser user = userDao.findByUsername(newUser.getUsername());
            throw new UserAlreadyExistsException();
        } catch (UsernameNotFoundException e) {
            userDao.create(newUser.getUsername(),newUser.getPassword(), newUser.getRole());
        }
    }



    /**
     * Object to return as body in JWT Authentication.
     */
    static class LoginResponse {

        private String token;
        private YogaUser user;

        LoginResponse(String token, YogaUser user) {
            this.token = token;
            this.user = user;
        }

        @JsonProperty("token")
        String getToken() {
            return token;
        }

        void setToken(String token) {
            this.token = token;
        }

        @JsonProperty("user")
		public YogaUser getUser() {
			return user;
		}

		public void setUser(YogaUser user) {
			this.user = user;
		}
    }
}

