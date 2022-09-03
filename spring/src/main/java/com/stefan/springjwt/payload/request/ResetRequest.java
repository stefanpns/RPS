package com.stefan.springjwt.payload.request;

import java.util.Set;

import javax.validation.constraints.*;

public class ResetRequest {

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


}
