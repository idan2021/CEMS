package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import client.ClientHandler;
import client.ClientUI;
import enums.window;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import utils.FXMLutil;

/**
 * <p>
 * <b>Explanation:</b> The class for the find of the exam that performs now,
 * entering screen to the test
 * <p>
 * 
 * @author IdanDaar
 */
public class PerformAnExamStudentController implements Initializable {

	@FXML
	private PasswordField examCodeField;

	@FXML
	private Button backButton;

	@FXML
	private Button findExamButton;

	static String keyToExam;

	/**
	 * <p>
	 * <b>Explanation:</b> The method initialize exam that perform now by comparing
	 * the special code of exam that the teacher brings to the student and move
	 * forward to identification
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resources that used to localize the root object.
	 * <p>
	 * <b>Return:</b> A findExamButton fire to move to the next step to perform exam
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public void initialize(URL location, ResourceBundle resources) {
		examCodeField.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
		
				findExamButton.fire();
				
			}
		});

		/**
		 * <p>
		 * <b>Explanation:</b> The button to find the exam and move to the next page
		 * <p>
		 * 
		 * @author IdanDaar
		 */
		findExamButton.setOnAction(event -> {
//			String[] fileData = new String[1000];
//			try {
//				fileData = fileReader();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				customeError("Read file error", e1.toString(), "Couldn't read to the file");
//			}
//
//			for (int i = 0; i < fileData.length; i++) {
//				if (fileData[i].equals(examCodeField.getText())) {
//					keyToExam = examCodeField.getText();
//					try {
//						swapForwardSceneMenu(event);
//					} catch (Exception e) {
//						customeError("Wrong exam code error", "Incorrect passcode",
//								"Please re-type passcode for the exam");
//					}
//				}
//
//			}
			ClientUI.clientHandler.handleMessageFromClientUI("checkIfExamExists:::"+examCodeField.getText());
			String returnMsg = ((String) ClientHandler.returnMessage);
			if (returnMsg.equals("success"))
				try {
					keyToExam=examCodeField.getText();
					swapForwardSceneMenu(event);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					customeError("Error", "Error openeing next scene","Please contact system administrator");
				}
			else {
				customeError("Wrong exam code error", "Incorrect passcode","Please re-type passcode for the exam");
				
			}
		});

		/**
		 * <p>
		 * <b>Explanation:</b> The button to move back to the students menu
		 * <p>
		 * 
		 * @author IdanDaar
		 */
		backButton.setOnAction(event -> {
			try {
				swapBackSceneMenu(event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method for swapping of the screen to the students
	 * menu
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static void swapBackSceneMenu(ActionEvent eventer) throws IOException {
		try {
			FXMLutil.swapScene(eventer, window.StudentMenu.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method for swapping of the screen to the
	 * identification menu
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static void swapForwardSceneMenu(ActionEvent eventer) throws IOException {
		try {
			FXMLutil.swapScene(eventer, window.StartMyExamStudent.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String[] fileReader() throws IOException {
		String[] str = new String[1000];
		File fileName = new File("SpecialCode.txt");
		Scanner reader = new Scanner(fileName);
		int i = 0;
		while (reader.hasNextLine()) {
			str[i] = (reader.nextLine());
			i++;
		}
		reader.close();
		return str;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method for receiving a pop-up error screen
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static void customeError(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public static String getKeyToExam() {
		return keyToExam;
	}

	public static void setKeyToExam(String keyToExam) {
		PerformAnExamStudentController.keyToExam = keyToExam;
	}
}
