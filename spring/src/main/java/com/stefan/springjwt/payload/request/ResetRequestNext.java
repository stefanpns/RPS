package com.stefan.springjwt.payload.request;

import java.util.Set;

import javax.validation.constraints.*;

public class ResetRequestNext {

  
  @NotBlank
  private String resetcode;


  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  public String getResetcode() {
    return resetcode;
  }

  public void setResetcode(String resetcode) {
    this.resetcode = resetcode;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }



}
