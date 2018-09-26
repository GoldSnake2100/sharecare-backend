package services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.json.JSONObject;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import utils.DBUtils;
import utils.MyUtils;

@Path("/sendEmail")
public class SendEmail {
	
	@Context
		private HttpServletRequest request;
	@Context
		private HttpServletResponse response;

	@Path("/forgotPassword")
	@POST
	public String forgotPassword(String email) throws Exception {
		JSONObject object = new JSONObject();
		Connection conn = MyUtils.getStoredConnection(request);
		if(DBUtils.checkUser(conn, email)) {
			sendSimpleMessage(email);
			object.put("status", true);
		} else {
			object.put("status", false);
		}
		return object.toString();
	}
	
	public void sendSimpleMessage(String email) {
	    Configuration configuration = new Configuration()
	    		.domain("https://api.mailgun.net/v3/www.sharecareapp.com")
	    		.apiKey("key-9dc5a2410e5bb1027bd23b2a9cd2a571")
	    		.from("www.sharecareapp.com");
	    
	    String content =
	            "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
	         
		 content += "<html>" +
		   "<head>"
		   + "<link href=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css\" rel=\"stylesheet\" id=\"bootstrap-css\">\r\n" + 
		       "<script src=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js\"></script>\r\n" + 
		       "<script src=\"//code.jquery.com/jquery-1.11.1.min.js\"></script>\r\n" + 
		   "</head>" +
		   "<body bgcolor = \"#f0f0f0\">" + 
		      " <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css\">\r\n" + 
		      " <div class=\"form-gap\"></div>\r\n" + 
		      "<div class=\"container\">\r\n" + 
		      "	<div class=\"row\">\r\n" + 
		      "		<div class=\"col-md-4 col-md-offset-4\">\r\n" + 
		      "            <div class=\"panel panel-default\">\r\n" + 
		      "              <div class=\"panel-body\">\r\n" + 
		      "                <div class=\"text-center\">\r\n" + 
		      "                  <h3><i class=\"fa fa-lock fa-4x\"></i></h3>\r\n" + 
		      "                  <h2 class=\"text-center\">Forgot Password?</h2>\r\n" + 
		      "                  <p>You can reset your password here.</p>\r\n" + 
		      "                  <div class=\"panel-body\">\r\n" + 
		      "    \r\n" + 
		      "                    <form id=\"register-form\" role=\"form\" autocomplete=\"off\" class=\"form\" method=\"post\" action=\"/sendEmail/response\">\r\n" + 
		      "    \r\n" + 
		      "                      <div class=\"form-group\">\r\n" + 
		      "                        <div class=\"input-group\">\r\n" +  
		      "                          <input id=\"password\" name=\"password\" placeholder=\"password\" class=\"form-control\"  type=\"password\">\r\n" +
		      "                          <input id=\"confirm_password\" name=\"confirm_password\" placeholder=\"confirm password\" class=\"form-control\"  type=\"password\">\r\n" +
		      "                        </div>\r\n" + 
		      "                      </div>\r\n" + 
		      "                      <div class=\"form-group\">\r\n" + 
		      "                        <input name=\"recover-submit\" class=\"btn btn-lg btn-primary btn-block\" value=\"Reset Password\" type=\"submit\">\r\n" + 
		      "                      </div>\r\n" + 
		      "                      \r\n" + 
		      "                      <input type=\"hidden\" class=\"hide\" name=\"token\" id=\"token\" value=\"\"> \r\n" + 
		      "                    </form>\r\n" + 
		      "    \r\n" + 
		      "                  </div>\r\n" + 
		      "                </div>\r\n" + 
		      "              </div>\r\n" + 
		      "            </div>\r\n" + 
		      "          </div>\r\n" + 
		      "	</div>\r\n" + 
		      "</div>" +
		      "<style>" +
		      ".form-gap {\r\n" + 
		      "    padding-top: 70px;\r\n" + 
		      "}" +
		      "</style>" +
		   "</body>" +
		"</html>";
	    
	    Mail.using(configuration).to(email)
	    	.subject("Reset your Password")
	    	.html(content)
	    	.build()
	    	.send();
	}
	
	public boolean sendMail(String toEmail)
	      throws IOException {
	      
	      // Recipient's email ID needs to be mentioned.
	      String to = toEmail;
	      // Sender's email ID needs to be mentioned
	      String from = "ShareCare@gmail.com";
	 
	      // Assuming you are sending email from 
//	      String host = "74.50.54.231";
	      String host = "dev02.echo-alert.com";
	 
	      // Get system properties
	      Properties properties = System.getProperties();
	 
	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	 
	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);
	      
	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);
	         
	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));
	         
	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	         
	         // Set Subject: header field
	         message.setSubject("Reset your Password");
	         
	         String content =
	            "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
	         
	         content += 
	            "<html>" +
	               "<head>"
	               + "<link href=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css\" rel=\"stylesheet\" id=\"bootstrap-css\">\r\n" + 
		               "<script src=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js\"></script>\r\n" + 
		               "<script src=\"//code.jquery.com/jquery-1.11.1.min.js\"></script>\r\n" + 
	               "</head>" +
	               "<body bgcolor = \"#f0f0f0\">" + 
	                  " <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css\">\r\n" + 
	                  " <div class=\"form-gap\"></div>\r\n" + 
	                  "<div class=\"container\">\r\n" + 
	                  "	<div class=\"row\">\r\n" + 
	                  "		<div class=\"col-md-4 col-md-offset-4\">\r\n" + 
	                  "            <div class=\"panel panel-default\">\r\n" + 
	                  "              <div class=\"panel-body\">\r\n" + 
	                  "                <div class=\"text-center\">\r\n" + 
	                  "                  <h3><i class=\"fa fa-lock fa-4x\"></i></h3>\r\n" + 
	                  "                  <h2 class=\"text-center\">Forgot Password?</h2>\r\n" + 
	                  "                  <p>You can reset your password here.</p>\r\n" + 
	                  "                  <div class=\"panel-body\">\r\n" + 
	                  "    \r\n" + 
	                  "                    <form id=\"register-form\" role=\"form\" autocomplete=\"off\" class=\"form\" method=\"post\" action=\"/sendEmail/response\">\r\n" + 
	                  "    \r\n" + 
	                  "                      <div class=\"form-group\">\r\n" + 
	                  "                        <div class=\"input-group\">\r\n" +  
	                  "                          <input id=\"password\" name=\"password\" placeholder=\"password\" class=\"form-control\"  type=\"password\">\r\n" +
	                  "                          <input id=\"confirm_password\" name=\"confirm_password\" placeholder=\"confirm password\" class=\"form-control\"  type=\"password\">\r\n" +
	                  "                        </div>\r\n" + 
	                  "                      </div>\r\n" + 
	                  "                      <div class=\"form-group\">\r\n" + 
	                  "                        <input name=\"recover-submit\" class=\"btn btn-lg btn-primary btn-block\" value=\"Reset Password\" type=\"submit\">\r\n" + 
	                  "                      </div>\r\n" + 
	                  "                      \r\n" + 
	                  "                      <input type=\"hidden\" class=\"hide\" name=\"token\" id=\"token\" value=\"\"> \r\n" + 
	                  "                    </form>\r\n" + 
	                  "    \r\n" + 
	                  "                  </div>\r\n" + 
	                  "                </div>\r\n" + 
	                  "              </div>\r\n" + 
	                  "            </div>\r\n" + 
	                  "          </div>\r\n" + 
	                  "	</div>\r\n" + 
	                  "</div>" +
	                  "<style>" +
	                  ".form-gap {\r\n" + 
	                  "    padding-top: 70px;\r\n" + 
	                  "}" +
	                  "</style>" +
	               "</body>" +
	            "</html>"
	         ;
	         //	Now set the actual message
	         message.setContent(content, "text/html");
	         
	         // Send message
	         Transport.send(message);
	         return true;
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	         System.out.println("Exception:=======================" + mex.getMessage());
	         return false;
	      }
	}
	
	@Path("/response")
	@POST
	public void response(String params) throws IOException {
		PrintWriter out = response.getWriter();
		out.println(params);
	}
} 

