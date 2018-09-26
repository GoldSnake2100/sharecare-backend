package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Random;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.UserAccount;


public class MyUtils
{
  public static final String ATT_NAME_CONNECTION = "ATTRIBUTE_FOR_CONNECTION";
  
  public MyUtils() {}
  
  public static void storeConnection(ServletRequest request, Connection conn)
  {
    request.setAttribute("ATTRIBUTE_FOR_CONNECTION", conn);
  }
  
  public static Connection getStoredConnection(ServletRequest request)
  {
    Connection conn = (Connection)request.getAttribute("ATTRIBUTE_FOR_CONNECTION");
    return conn;
  }
  

  public static void storeLoginedUser(HttpSession session, UserAccount loginedUser)
  {
    session.setAttribute("loginedUser", loginedUser);
  }
  
  public static UserAccount getLoginedUser(HttpSession session)
  {
    UserAccount loginedUser = (UserAccount)session.getAttribute("loginedUser");
    return loginedUser;
  }
  
  public static void storeUserCookie(HttpServletResponse response, UserAccount user)
  {
    System.out.println("Store user cookie");
    Cookie cookieUserName = new Cookie("ATTRIBUTE_FOR_STORE_USER_NAME_IN_COOKIE", String.valueOf(user.getUserID()));
    
    cookieUserName.setMaxAge(86400);
    response.addCookie(cookieUserName);
  }
  
  public static String getUserNameInCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("ATTRIBUTE_FOR_STORE_USER_NAME_IN_COOKIE".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
  
  public static void deleteUserCookie(HttpServletResponse response)
  {
    Cookie cookieUserName = new Cookie("ATTRIBUTE_FOR_STORE_USER_NAME_IN_COOKIE", null);
    
    cookieUserName.setMaxAge(0);
    response.addCookie(cookieUserName);
  }
  
  public static String getMD5String(String origin)
  {
    String md5Str = "";
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(origin.getBytes());
      byte[] digest = md.digest();
      
      StringBuffer sb = new StringBuffer();
      for (byte b : digest) {
        sb.append(String.format("%02x", new Object[] { Integer.valueOf(b & 0xFF) }));
      }
      md5Str = sb.toString();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return md5Str;
  }
  
  public static String createReferralCode() {
    int leftLimit = 97;
    int rightLimit = 122;
    int targetStringLength = 6;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
      int randomLimitedInt = leftLimit + 
        (int)(random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char)randomLimitedInt);
    }
    String generatedString = buffer.toString();
    return generatedString.toUpperCase();
  }
}