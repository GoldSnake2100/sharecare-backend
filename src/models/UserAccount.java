package models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class UserAccount
{
  private int userID;
  private String userName;
  private String userPassword;
  private String email;
  private double latitude;
  private double longitude;
  private String push_token;
  private String image;
  private String referral_code;
  
  public UserAccount() {}
  
  public int getUserID()
  {
    return userID;
  }
  
  public String getReferral_code() {
    return referral_code;
  }
  
  public String getUserName() {
    return userName;
  }
  
  public String getUserPassword() {
    return userPassword;
  }
  
  public String getEmail() {
    return email;
  }
  
  public double getLongitude() {
    return longitude;
  }
  
  public double getLatitude() {
    return latitude;
  }
  
  public String getPush_token() {
    return push_token;
  }
  
  public String getImage() {
    return image;
  }
  
  @XmlElement
  public void setUserID(int userID) {
    this.userID = userID;
  }
  
  @XmlElement
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  @XmlElement
  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }
  
  @XmlElement
  public void setEmail(String email) {
    this.email = email;
  }
  
  @XmlElement
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
  
  @XmlElement
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }
  
  @XmlElement
  public void setPush_token(String push_token) {
    this.push_token = push_token;
  }
  
  @XmlElement
  public void setImage(String image) {
    this.image = image;
  }
  
  @XmlElement
  public void setReferral_code(String referral_code) {
    this.referral_code = referral_code;
  }
}