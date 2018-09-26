package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.UserAccount;

public class DBUtils {
	public DBUtils() {
	}

	public static List<UserAccount> getAllUsers(Connection conn) throws SQLException {
		List<UserAccount> users = new ArrayList<>();
		String sql = "Select * from tbl_user";

		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			UserAccount user = new UserAccount();
			user.setUserID(rs.getInt("id"));
			user.setUserName(rs.getString("name"));
			user.setUserPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setLongitude(rs.getDouble("longitude"));
			user.setLatitude(rs.getDouble("latitude"));
			user.setPush_token(rs.getString("push_token"));
			user.setImage(rs.getString("image"));
			users.add(user);
		}
		rs.close();
		pstm.close();
		return users;
	}

	public static List<UserAccount> getSearchUser(Connection conn, String query, int id) throws SQLException {
		List<UserAccount> users = new ArrayList<>();
		String sql = "Select * from tbl_user where (email LIKE '%" + query + "%' OR name LIKE '%" + query
				+ "%') AND id != " + id;

		PreparedStatement pstm = conn.prepareStatement(sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			UserAccount user = new UserAccount();
			user.setUserID(rs.getInt("id"));
			user.setUserName(rs.getString("name"));
			user.setUserPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setLongitude(rs.getDouble("longitude"));
			user.setLatitude(rs.getDouble("latitude"));
			user.setPush_token(rs.getString("push_token"));
			user.setImage(rs.getString("image"));
			users.add(user);
		}
		rs.close();
		pstm.close();
		return users;
	}

	public static UserAccount login(Connection conn, String email, String password, String token) throws SQLException {
		String sql = "Select * from tbl_user where email=? and password=?";

		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, email);
		pstm.setString(2, password);

		UserAccount user = new UserAccount();
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			user = new UserAccount();
			user.setUserID(rs.getInt("id"));
			user.setUserName(rs.getString("name"));
			user.setUserPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setImage(rs.getString("image"));
			user.setReferral_code(rs.getString("referral_code"));

			updateUserToken(conn, user.getUserID(), token);
			user.setPush_token(token);
		}

		rs.close();
		pstm.close();
		return user;
	}

	public static UserAccount findUser(Connection conn, int userID) throws SQLException {
		String sql = "Select * from tbl_user where id=?";

		UserAccount user = new UserAccount();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, userID);

		ResultSet rs = pstm.executeQuery();

		if (rs.next()) {
			user.setUserID(userID);
			user.setUserName(rs.getString("name"));
			user.setUserPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setLatitude(rs.getDouble("latitude"));
			user.setLongitude(rs.getDouble("longitude"));
			user.setPush_token(rs.getString("push_token"));
			user.setImage(rs.getString("image"));
		}
		rs.close();
		pstm.close();
		return user;
	}

	public static boolean checkUser(Connection conn, String email) throws SQLException {
		boolean flag = false;
		String sql = "Select Count(*) as count from tbl_user where email='" + email + "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if ((rs.next()) && (rs.getInt("count") > 0))
			flag = true;
		rs.close();
		return flag;
	}
	
	public static int getUserIdWithEmail(Connection conn, String email) throws SQLException {
		int id = 0;
		String sql = "Select id from tbl_user where email='" + email + "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (rs.next()) {
			id = rs.getInt("id");
		}
		rs.close();
		return id;
	}

	public static void adduser(Connection conn, UserAccount user) throws SQLException {
		String sql = "INSERT INTO tbl_user (email, password, name, longitude, latitude, push_token, image, referral_code) VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, user.getEmail());
		pstm.setString(2, user.getUserPassword());
		pstm.setString(3, user.getUserName());
		pstm.setDouble(4, user.getLongitude());
		pstm.setDouble(5, user.getLatitude());
		pstm.setString(6, user.getPush_token());
		pstm.setString(7, user.getImage());
		pstm.setString(8, user.getReferral_code());
		pstm.executeUpdate();
		pstm.close();
	}

	public static JsonObject signupWithMobile(Connection conn, String email, String password, String token)
			throws SQLException {
		JsonObject response = new JsonObject();
		
		if (checkUser(conn, email)) {
			response.addProperty("status", "fail");
			response.addProperty("message", "This user already signed up");
		} else {
			UserAccount user = new UserAccount();
			user.setUserPassword(password);
			user.setEmail(email);
			user.setPush_token(token);
			user.setUserName("");
			user.setLatitude(0.0D);
			user.setLongitude(0.0D);
			user.setImage("");
			user.setReferral_code(getReferralCode(conn));
			adduser(conn, user);

			response.addProperty("status", "success");
			response.addProperty("message", "Signed up successfully");
		}
		return response;
	}

	public static String referUser(Connection conn, String code, String userId) throws SQLException {
		JsonObject response = new JsonObject();
		Gson gson = new Gson();

		String sql = "Select Count(*) as count from tbl_user where referral_code='" + code + "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (rs.next())
			if (rs.getInt("count") > 0) {
				sql = "Insert into tbl_caregiver (userId, referral_code) values (?, ?)";
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setInt(1, Integer.valueOf(userId).intValue());
				pstm.setString(2, code);
				int count = pstm.executeUpdate();
				if (count > 0)
					response.addProperty("status", "success");
			} else {
				response.addProperty("status", "fail");
			}
		rs.close();

		return gson.toJson(response);
	}

	public static boolean validateReferralCode(Connection conn, String code) throws SQLException {

		boolean valid = false;

		String sql = "Select Count(*) as count from tbl_user where referral_code='" + code + "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if (rs.next())
			if (rs.getInt("count") > 0) {
				valid = true;
			}

		rs.close();
		return valid;

	}

	public static String updateUserLocation(Connection conn, int id, double latitude, double longitude)
			throws SQLException {
		String sql = "Update tbl_user set latitude=?, longitude=? where id=?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setDouble(1, latitude);
		pstm.setDouble(2, longitude);
		pstm.setInt(3, id);
		int result = pstm.executeUpdate();
		pstm.close();

		JsonObject response = new JsonObject();
		Gson gson = new Gson();
		if (result > 0) {
			response.addProperty("status", "success");
		} else {
			response.addProperty("status", "fail");
		}
		return gson.toJson(response);
	}

	public static boolean updateUserToken(Connection conn, int id, String token) throws SQLException {
		String sql = "Update tbl_user set push_token=? where id=?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, token);
		pstm.setInt(2, id);
		int result = pstm.executeUpdate();
		pstm.close();

		if (result > 0)
			return true;
		return false;
	}

	public static String updateUser(Connection conn, int id, String name) throws SQLException {
		String sql = "Update tbl_user set name=? where id=?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, name);
		pstm.setInt(2, id);
		int result = pstm.executeUpdate();
		pstm.close();

		JsonObject response = new JsonObject();
		Gson gson = new Gson();
		if (result > 0) {
			response.addProperty("status", "success");
		} else {
			response.addProperty("status", "fail");
		}
		return gson.toJson(response);
	}

	public static String updateUserPhoto(Connection conn, int id, String image) throws SQLException {
		String sql = "Update tbl_user set image=? where id=?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, image);
		pstm.setInt(2, id);
		int result = pstm.executeUpdate();
		pstm.close();

		JsonObject response = new JsonObject();
		Gson gson = new Gson();
		if (result > 0) {
			response.addProperty("status", "success");
		} else {
			response.addProperty("status", "fail");
		}
		return gson.toJson(response);
	}

	public static List<UserAccount> getCareGivers(Connection conn, String referralCode) throws SQLException {
		List<UserAccount> users = new ArrayList<>();
		String sql = "SELECT a.* FROM tbl_user a JOIN tbl_caregiver b ON a.id = b.userId WHERE b.referral_code=?";

		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, referralCode);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			UserAccount user = new UserAccount();
			user.setUserID(rs.getInt("id"));
			user.setUserName(rs.getString("name"));
			user.setUserPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setLongitude(rs.getDouble("longitude"));
			user.setLatitude(rs.getDouble("latitude"));
			user.setPush_token(rs.getString("push_token"));
			user.setImage(rs.getString("image"));
			users.add(user);
		}
		rs.close();
		pstm.close();
		return users;
	}

	public static String getReferralCode(Connection conn) throws SQLException {
		String code = MyUtils.createReferralCode();
		String sql = "Select Count(*) as count from tbl_user where referral_code='" + code + "'";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		if ((rs.next()) && (rs.getInt("count") > 0))
			code = "";
		rs.close();
		if (code.isEmpty())
			getReferralCode(conn);
		return code;
	}

	public static String deleteCareGiver(Connection conn, String userId, String referralCode) throws SQLException {
		String sql = "delete from tbl_caregiver where userId=? and referral_code=?";
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setInt(1, Integer.valueOf(userId).intValue());
		pstm.setString(2, referralCode);

		int result = pstm.executeUpdate();

		JsonObject response = new JsonObject();
		Gson gson = new Gson();
		if (result > 0) {
			response.addProperty("status", "success");
		} else {
			response.addProperty("status", "fail");
		}
		return gson.toJson(response);
	}
}