package controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import entity.MyFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class UploadExamFilesDeveloperController {

	@FXML
	private Button upload;

	@FXML
	private TextField examID;

	@FXML
	void uploadAction(ActionEvent event) throws IOException {
		Connection dbConnect = DBConnect();
		FileChooser filechooser = new FileChooser();
		File selectedfile = filechooser.showOpenDialog(null);

		MyFile fileToUpload = new MyFile("null", examID.getText(), "null");

		byte[] myByteArray = new byte[(int) selectedfile.length()];

		FileInputStream fileIn = new FileInputStream(selectedfile);

		BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
		fileToUpload.initArray(myByteArray.length);
		fileToUpload.setSize(myByteArray.length);

		bufferIn.read(fileToUpload.getMybytearray(), 0, myByteArray.length);
		updateBlob(fileToUpload, dbConnect);
		bufferIn.close();
	}

	public static String updateBlob(MyFile file, Connection DBconnect) {
		InputStream is = new ByteArrayInputStream(((MyFile) file).getMybytearray());
		try {
			PreparedStatement pstmt = DBconnect.prepareStatement("INSERT INTO examfiles VALUES (?,?)");
			pstmt.setBlob(2, is);
			pstmt.setString(1, file.getExamID());

			int rs = pstmt.executeUpdate();
			return rs == 1 ? "success" : "failed";
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return null;

	}

	@SuppressWarnings("deprecation")
	public Connection DBConnect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

		} catch (Exception ex) {
			/* handle the error */

		}

		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?serverTimezone=IST", "root",
					"123");
			return conn;

			// Connection conn =
			// DriverManager.getConnection("jdbc:mysql://192.168.3.68/test","root","Root");

			// createTableCourses(conn);
			// printCourses(conn);
		} catch (SQLException ex) {/* handle any errors */

		}
		return null;

	}
}
