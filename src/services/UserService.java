package services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.UserAccount;
import utils.DBUtils;
import utils.MyUtils;

@Path("/user")
public class UserService {
	@Context
	private HttpServletRequest request;
	private Gson gson = new Gson();

	public UserService() {
	}

	@Path("/login")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String dologin(@QueryParam("email") String email, @QueryParam("password") String password,
			@QueryParam("token") String token) throws Exception {
		Connection conn = MyUtils.getStoredConnection(request);

		UserAccount user = new UserAccount();
		
		user = DBUtils.login(conn, email, password, token);

		return gson.toJson(user);
	}

	@Path("/caregiver")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getCareGivers(@QueryParam("referralCode") String referralCode) throws Exception {
		Connection conn = MyUtils.getStoredConnection(request);
		List<UserAccount> user = DBUtils.getCareGivers(conn, referralCode);
		return gson.toJson(user);
	}

	@Path("/caregiver")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCareGiver(@QueryParam("userId") String userId, @QueryParam("referralCode") String referralCode)
			throws Exception {
		Connection conn = MyUtils.getStoredConnection(request);
		String res = DBUtils.deleteCareGiver(conn, userId, referralCode);
		return res;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsers() throws Exception {
		Connection conn = MyUtils.getStoredConnection(request);
		List<UserAccount> users = DBUtils.getAllUsers(conn);
		return gson.toJson(users);
	}

	@Path("/signup")
	@GET
	public String signup(@QueryParam("email") String email, @QueryParam("password") String password,
			@QueryParam("token") String token, @QueryParam("referral") String referral) throws Exception {
		Connection conn = MyUtils.getStoredConnection(request);
		
		JsonObject res = new JsonObject();
		boolean isValidReferral = true;
		if (!referral.isEmpty()) {
			isValidReferral = DBUtils.validateReferralCode(conn, referral);
		}
		
		if (isValidReferral) {
			res = DBUtils.signupWithMobile(conn, email, password, token);
		
			if (!referral.isEmpty()) {
				DBUtils.referUser(conn, referral, String.valueOf(DBUtils.getUserIdWithEmail(conn, email)));
			}
		} else {
			res.addProperty("status", "fail");
			res.addProperty("message", "The referral code is invalid.");
		}
		return gson.toJson(res);
	}

	@Path("/referUser")
	@POST
	public String referUser(@QueryParam("code") String code, @QueryParam("userId") String userId) throws Exception {
		Connection conn = MyUtils.getStoredConnection(request);
		String res = DBUtils.referUser(conn, code, userId);
		return res;
	}

	@Path("/updateLocation")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateLocation(String data) throws Exception {
		JSONObject object = new JSONObject(data);
		int id = object.getInt("userid");
		double latitude = object.getDouble("latitude");
		double longitude = object.getDouble("longitude");
		Connection conn = MyUtils.getStoredConnection(request);
		String res = DBUtils.updateUserLocation(conn, id, latitude, longitude);
		return res;
	}

	@Path("/updateUser")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateUser(String data) throws Exception {
		JSONObject object = new JSONObject(data);
		int id = object.getInt("userid");
		String name = object.getString("name");
		Connection conn = MyUtils.getStoredConnection(request);
		String res = DBUtils.updateUser(conn, id, name);
		return res;
	}

	@Path("/updateUserPhoto")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateUserPhoto(String data) throws Exception {
		JSONObject object = new JSONObject(data);
		int id = object.getInt("userid");
		String image = object.getString("image");
		Connection conn = MyUtils.getStoredConnection(request);
		String res = DBUtils.updateUserPhoto(conn, id, image);
		return res;
	}

	@Path("/searchUser")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String searchUser(@QueryParam("query") String query, @QueryParam("id") int id) throws Exception {
		Connection conn = MyUtils.getStoredConnection(request);
		List<UserAccount> users = DBUtils.getSearchUser(conn, query, id);
		return gson.toJson(users);
	}

	@Path("/photo/{email}")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String savePhotos(@PathParam("email") String email, @FormDataParam("file") List<FormDataBodyPart> bodyParts,
			@FormDataParam("file") FormDataContentDisposition fileDispositions) throws Exception {
		String strpath = "";
		java.nio.file.Path path = Paths.get(
				System.getProperty("user.dir") + File.separator + "uploadphoto" + File.separator + email,
				new String[0]);
		Files.createDirectories(path,
				new FileAttribute[] { PosixFilePermissions.asFileAttribute(getFullPermission()) });

		for (BodyPart body : bodyParts) {
			BodyPartEntity bodyEntity = (BodyPartEntity) body.getEntity();
			InputStream inputStream = bodyEntity.getInputStream();
			String fileName = body.getContentDisposition().getFileName();
			fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
			strpath = path.toString() + File.separator + fileName;
			try {
				File file = new File(strpath);
				file.setReadable(true);
				file.setExecutable(true);
				OutputStream outpuStream = new FileOutputStream(file);
				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outpuStream.write(bytes, 0, read);
				}
				outpuStream.flush();
				outpuStream.close();
			} catch (IOException e) {
				strpath = "";
			}
			inputStream.close();
		}

		return strpath;
	}

	@Path("/downloadLink")
	@GET
	public String getAppDownloadLink() throws Exception {
		return "http://";
	}

	public Set<PosixFilePermission> getFullPermission() throws IOException {
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);

		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);

		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);

		return perms;
	}

}
