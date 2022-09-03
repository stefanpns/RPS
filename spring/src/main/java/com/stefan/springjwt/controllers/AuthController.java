package com.stefan.springjwt.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.scheduling.annotation.Async;

import com.stefan.springjwt.models.ERole;
import com.stefan.springjwt.models.Role;
import com.stefan.springjwt.models.User;
import com.stefan.springjwt.payload.request.LoginRequest;
import com.stefan.springjwt.payload.request.SignupRequest;
import com.stefan.springjwt.payload.request.ResetRequest;
import com.stefan.springjwt.payload.request.ResetRequestNext;
import com.stefan.springjwt.payload.response.JwtResponse;
import com.stefan.springjwt.payload.response.MessageResponse;
import com.stefan.springjwt.repository.RoleRepository;
import com.stefan.springjwt.repository.UserRepository;
import com.stefan.springjwt.security.jwt.JwtUtils;
import com.stefan.springjwt.security.services.UserDetailsImpl;


import org.springframework.web.bind.annotation.RequestParam;

// od SPRINGBOOTEMAILVER
// UserServices...

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import net.bytebuddy.utility.RandomString;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;
  
  @Autowired
  JavaMailSender mailSender;

  @Autowired 
  HttpServletRequest request;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error register: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error register: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), 
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }


    
    String randomCode = RandomString.make(64);
    user.setVerificationCode(randomCode);

    user.setRoles(roles);

    user.setEnabled(false);
    
      
      try {
        String siteURL = request.getRequestURL().toString();
        siteURL = siteURL.replace(request.getServletPath(), "");
        this.sendVerificationEmail(user, siteURL);   
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered email pending"));
      } catch (UnsupportedEncodingException e){
        return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error v email: UnsupportedEncodingException!"));
      } catch(MessagingException e){
        return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error v email: MessagingException!"));
      }
      
    

  }


	@Async
  private void sendVerificationEmail(User user, String siteURL)
        throws MessagingException, UnsupportedEncodingException {


    if (1==1){
      return;
    }
    String toAddress = user.getEmail();
    String fromAddress = "stefankorisnik@outlook.com";
    String senderName = "Stefan Korisnik";
    String subject = "verify your registration";
    String content = "Dear [[name]],<br>"
            + "click the link below to verify your registration:<br>"
            + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
            + "Thank you,<br>"
            + "Your company name.";
    
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    
    helper.setFrom(fromAddress, senderName);
    helper.setTo(toAddress);
    helper.setSubject(subject);
    
    content = content.replace("[[name]]", user.getUsername());
    String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
    
    content = content.replace("[[URL]]", verifyURL);
    
    helper.setText(content, true);
    
    mailSender.send(message);
    
  }


  public boolean verify(String verificationCode) {
		User user = userRepository.findByVerificationCode(verificationCode);
		
		if (user == null || user.isEnabled()) {
			return false;
		} else {
			user.setVerificationCode(null);
			user.setEnabled(true);
			userRepository.save(user);
			
			return true;
		}
		
	}

  @GetMapping("/verify")
  public String verifyUser(@RequestParam(required = true) String code){  //@Param("code") String code) {
      if (this.verify(code)) {
          return "verify_success";
      } else {
          return "verify_fail";
      }
  }






  @PostMapping("/resetfs")
  public ResponseEntity<?> resetUser(@Valid @RequestBody ResetRequest resetRequest) {
    if (userRepository.existsByEmail(resetRequest.getEmail()) == false) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email doesn't exists."));
    }
    
		User user = userRepository.findByEmail(resetRequest.getEmail());
    if (user.isEnabled() == false){
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: The user is not yet been approved."));
    }
    
    String randomCode = RandomString.make(64);
    user.setResetCode(randomCode);
    
      
      try {
        String siteURL = request.getRequestURL().toString();
        siteURL = siteURL.replace(request.getServletPath(), "");
        this.sendResetEmail(user, siteURL);   
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User reset email pending"));
      } catch (UnsupportedEncodingException e){
        return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error reset email: UnsupportedEncodingException!"));
      } catch(MessagingException e){
        return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error reset email: MessagingException!"));
      }
      
    

  }

  
  @Async
  private void sendResetEmail(User user, String siteURL)
        throws MessagingException, UnsupportedEncodingException {

    if (1==1){
      return;
    }
   
    String toAddress = user.getEmail();
    String fromAddress = "stefankorisnik@outlook.com";
    String senderName = "Stefan Korisnik";
    String subject = "reset your password";
    String content = "Dear user,<br>"
            + "click the link below to reset your password:<br>"
            + "<h3><a href=\"[[URL]]\" target=\"_self\">CHANGE PASSWORD</a></h3>"
            + "Thank you,<br>"
            + "Your company name.";
    
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    
    helper.setFrom(fromAddress, senderName);
    helper.setTo(toAddress);
    helper.setSubject(subject);
    
    String verifyURL = siteURL + "/resetts?resetcode=" + user.getResetCode();
    
    content = content.replace("[[URL]]", verifyURL);
    
    helper.setText(content, true);
    
    mailSender.send(message);
    
  }

  

  // @GetMapping("/resetss")
  // public ResponseEntity<?> verifyResetCode(@RequestParam(required = true) String code){  //@Param("code") String code) {
      
      
	// 	  User user = userRepository.findByResetCode(code);
      
  //     if (user != null) {
  //         //return "verify_reset_code_success";
  //         return ResponseEntity.ok(new MessageResponse("User reset code valid"));
  //     } else {
  //         //return "verify_reset_code_fail";
  //         return ResponseEntity
  //           .badRequest()
  //           .body(new MessageResponse("Error reset code not valid"));
  //     }

  // }

  
  @PostMapping("/resetts")
  public ResponseEntity<?> resetUser(@Valid @RequestBody ResetRequestNext resetRequest) {
    
		User user = userRepository.findByResetCode(resetRequest.getResetcode());

    if (user == null) {

      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Reset code doesn't exists."));

    }

    if (user.isEnabled() == false){
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: The user is not yet been approved."));
    }
    
    user.setPassword(encoder.encode(resetRequest.getPassword()));
    user.setResetCode(null);
    userRepository.save(user);
    
    return ResponseEntity.ok(new MessageResponse("User password been changed"));

  }


  

}
