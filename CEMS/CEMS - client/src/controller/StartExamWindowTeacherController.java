package controller;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.Exam;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class presents the teacher all the exams that will be
 * perform today and let her start exam with a unique code
 * <p>
 * 
 * @author OrSteiner
 */
public class StartExamWindowTeacherController implements Initializable {

	@FXML
	private TableView<Exam> examsTable;

	@FXML
	private TableColumn<Exam, String> examID;

	@FXML
	private TableColumn<Exam, String> course;

	@FXML
	private TableColumn<Exam, String> lengthOfExam;

	@FXML
	private TableColumn<Exam, String> dateOfExam;

	@FXML
	private TextField TextField1;

	@FXML
	private TextField TextField2;

	@FXML
	private TextField TextField3;

	@FXML
	private TextField TextField4;

	@FXML
	private Button cancelButton;

	@FXML
	private Button nextButton;
	ArrayList<String> errorLog = new ArrayList<String>();
	ArrayList<Exam> examsList = new ArrayList<Exam>();
	ObservableList<Exam> examList = FXCollections.observableArrayList();
	// global variable
	String specialCode;

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the questions data from DB and
	 * presents all the question in a table
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resources that used to localize the root object.
	 * <p>
	 * <b>Return:</b> List of all the questions in the system
	 * <p>
	 * 
	 * @author IdanDaar & DvirVahab
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// ask the server for the all of the teacher exams
		ClientUI.clientHandler.handleMessageFromClientUI("todayExams:::" + ClientUI.clientHandler.myDetails.getId());
		if (((String) ClientHandler.returnMessage).isBlank())// neeed to fix empty table
			customeError("No exams error", "No exams today",
					"There are no exams today, please try again in a later date");
		else {
			examsList = SpecialCalls.callExams((String) ClientHandler.returnMessage);
			examList.addAll(examsList);
		}

		examID.setCellValueFactory(new PropertyValueFactory<Exam, String>("id"));
		dateOfExam.setCellValueFactory(new PropertyValueFactory<Exam, String>("startingTime"));
		lengthOfExam.setCellValueFactory(new PropertyValueFactory<Exam, String>("timeInMinutes"));
		course.setCellValueFactory(new PropertyValueFactory<Exam, String>("course"));

		examsTable.setItems(examList);

		TextField1.setOnKeyReleased(event -> {
			if (event.getCharacter().length() == 1)
				// Move to the next fieldText
				TextField2.requestFocus();
			if (TextField1.getCharacters().length() >= 2)// needs to be fixed
				TextField1.requestFocus();
		});

		TextField2.setOnKeyReleased(event -> {
			if (event.getCharacter().length() == 1)
				TextField3.requestFocus();
		});

		TextField3.setOnKeyReleased(event -> {
			if (event.getCharacter().length() == 1)
				TextField4.requestFocus();
		});

		TextField4.setOnKeyReleased(event -> {
			if (event.getCharacter().length() == 1 && event.getCode() == KeyCode.ENTER)
				if (TextField1.getCharacters().length() == 1 && TextField2.getCharacters().length() == 1
						&& TextField3.getCharacters().length() == 1 && TextField4.getCharacters().length() == 1)
					nextButton.fire();
				else {
					customeError("Text field error", "Too many characters",
							"Please re-write the code, 1 char per text field");

				}
		});

		/**
		 * <p>
		 * <b>Explanation:</b> The button to move back to the teachers menu
		 * <p>
		 * 
		 * @author IdanDaar
		 */
		cancelButton.setOnAction(event -> {
			try {
				swapSceneMenu(event);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

	}
	
	/**
	 * <p>
	 * <b>Explanation:</b> The button to push the special code to the server for the
	 * students to enter the specific exam
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public void nextButtonClick(ActionEvent event) {

		if (examsTable.getSelectionModel().isEmpty())// need to check**********
			errorLog.add("Please select an exam");
		if (TextField1.toString().isBlank())
			errorLog.add("1st field can't be empty");
		if (TextField2.toString().isBlank())
			errorLog.add("2nd field can't be empty");
		if (TextField3.toString().isBlank())
			errorLog.add("3rd field can't be empty");
		if (TextField4.toString().isBlank())
			errorLog.add("4th field can't be empty");
		StringBuilder megicalCode = new StringBuilder();
		megicalCode.append(TextField1.getText());
		megicalCode.append(TextField2.getText());
		megicalCode.append(TextField3.getText());
		megicalCode.append(TextField4.getText());
		this.specialCode = megicalCode.toString();

		////////////////////////////////// Dvir addition////////////////////////////

		// update exam ID at exam stats table for average and median calculation use
		ClientUI.clientHandler.handleMessageFromClientUI("updateExamStats:::"
				+ examsTable.getSelectionModel().getSelectedItem().getId() + ":::" + this.specialCode);
		
		////////////////////////////////// END OF Dvir
		////////////////////////////////// addition////////////////////////////

		if (((String) ClientHandler.returnMessage).equals("success"))
			SpecialCalls.callSuccessFrame("Exam code attached to the exam successfully");
		else if (((String) ClientHandler.returnMessage).equals("This exam is already started"))
			SpecialCalls.customeError("Error", "This exam already started",
					"Please contact your system administartor for further details");
		else
			SpecialCalls.customeError("Error", "Error in starting the exam",
					"This exam is already performed today");

		try {
			swapSceneMenu(event);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method for swapping of the screen to the teachers
	 * menu
	 * <p>
	 * 
	 * @author IdanDaar
	 */
	public static void swapSceneMenu(ActionEvent eventer) throws IOException {
		try {
			FXMLutil.swapScene(eventer, window.TeacherMenu.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getSpecialCode() {
		return specialCode;
	}

	public void setSpecialCode(String specialCode) {
		this.specialCode = specialCode;
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


}
