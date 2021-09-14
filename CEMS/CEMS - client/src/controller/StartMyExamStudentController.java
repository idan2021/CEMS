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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class is a last exam's entrance gate presents the
 * exam that's about to be started and instruction
 * <p>
 * 
 * @author IdanDaar
 */
public class StartMyExamStudentController implements Initializable {

	@FXML
	private Text testDetailsTextField;

	@FXML
	private Text testInstructionTextField;

	@FXML
	private TextField IDTextField;

	@FXML
	private Button backButton;

	@FXML
	private Button startExamButton;

	/**
	 * student starts the exams he has by typing his id
	 * 
	 * @author Idan Daar
	 */

	public String examID;
	static String examType;

	/**
	 * <p>
	 * <b>Explanation:</b> The method presents exam instructions and the exam that
	 * about to perform
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resources that used to localize the root object.
	 * <p>
	 * 
	 * @author IdanDaar & DvirVahab
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the test instructions

		// needs to change the examID in the function sending pullTestDetail
		// gets the exam details to present as the window opens
		
		
		
	

				if (PerformAnExamStudentController.getKeyToExam().contains("m"))
					examType = "manual";
				else
					examType = "computerized";
				ClientUI.clientHandler.handleMessageFromClientUI("returmExamIDByCode:::"+PerformAnExamStudentController.getKeyToExam());
				String returnExam = (String) ClientHandler.returnMessage;
				examID = returnExam;
				
				if(!examID.equals("fail"))
				{
			
		ClientUI.clientHandler.handleMessageFromClientUI("getTestInstructions:::" + examID);
		testInstructionTextField.setText("Dear students...\n" + (String) ClientHandler.returnMessage);
		ClientUI.clientHandler.handleMessageFromClientUI("testDetails:::" + examID);
		String str = (String) ClientHandler.returnMessage;
		testDetailsTextField.setText(str);

		IDTextField.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER)
				startExamButton.fire();
		});

		IDTextField.setOnKeyTyped(KeyEvent -> {
			// incorrectInputTextField.setText(null);
			// Nedds to be connected to pop-up error screen
			if (IDTextField.getText().isBlank())
				customeError("ID input error", "Incorrect ID number", "Please enter valid ID");
		});

		/**
		 * <p>
		 * <b>Explanation:</b> The button move the student to his exam that about to
		 * perform
		 * <p>
		 * <b>Receive:</b> Event action for the button to launch
		 * <p>
		 * <b>Return:</b> The exam that about to perform (manual/computerized)
		 * <p>
		 * 
		 * @author IdanDaar&DvirVahab
		 */
		startExamButton.setOnAction(event -> {
			// BEFORE STARTING ! check if the student have ever tested in this exam before
					////////////////////////////////// Dvir addition////////////////////////////
					ClientUI.clientHandler.handleMessageFromClientUI(
							"updateStudentID:::" + examID + ":::" + PerformAnExamStudentController.getKeyToExam()
									+ ":::" + ClientUI.clientHandler.myDetails.getId());
//////////////////////////////////END OF Dvir addition////////////////////////////
					String returnMsg = (String)ClientHandler.returnMessage;
					if(returnMsg.equals("success")) {
					SpecialCalls.callSuccessFrame("Good luck " + ClientUI.clientHandler.myDetails.getFirstName());
					// choose the right exam format to swap to
					if (examType.equals("manual"))
						swapToManualExam(event);
					if (examType.equals("computerized"))
						swapToComputerizedExam(event);
					} else {
					customeError("Error", "Error exam code passowrd student already exist", returnMsg);
				}
			
		});

		backButton.setOnAction(event -> {
			try {
				swapSceneMenu(event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		
				}
				else {
					customeError("Error", "Error exam code passowrd student already exist",examID);
				}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to the student's menu
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> student's menu
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static void swapSceneMenu(ActionEvent eventer) throws IOException {
		try {
			FXMLutil.swapScene(eventer, window.StudentMenu.toString());
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
	 * <b>Explanation:</b> The error pop-up to present
	 * <p>
	 * <b>Receive:</b> title for the header of the pop-up, header for the message
	 * present error, content for the explanation of the error
	 * <p>
	 * <b>Return:</b> pop-up error
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

	// here start the split between computerized exam and manual exam

	/**
	 * <p>
	 * <b>Explanation:</b> The method for swapping to the manual exam
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Leads to manual exam
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	private void swapToManualExam(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(window.ManualExamStudent.toString()));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ManualExamStudentController CEScontroller = loader.getController();
		CEScontroller.initializeExam(examID);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		
		stage.setOnCloseRequest(EventHandler -> {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setContentText("This window cannot be closed");
	        alert.showAndWait();
	        EventHandler.consume();
	});
		stage.show();

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method for swapping to the computerized exam
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Leads to computerized exam
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	private void swapToComputerizedExam(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(window.ComputerizedExam.toString()));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ComputerizedExamStudentController CEScontroller = loader.getController();
		CEScontroller.initializeExam(examID);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.setOnCloseRequest(EventHandler -> {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setContentText("This window cannot be closed");
	        alert.showAndWait();
	        EventHandler.consume();
	});
		stage.show();
	}

}
