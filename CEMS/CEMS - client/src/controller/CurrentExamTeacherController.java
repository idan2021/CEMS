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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class Current Exam Teacher controller is reached from the teacher menu.
 * <p>
 * The class allows the teacher to view all of its current ongoing exams, lock them, or request an approval to change their duration from the principals.
 * <p>
 * the Teacher chooses the exam and clicks lock or request duration approval buttons and fill the relevant fields if necessary .
 * <p>
 * @author EthanButorsky
 */
public class CurrentExamTeacherController implements Initializable {

	@FXML
	private TableView<Exam> examContentTableView;

	@FXML
	private TableColumn<Exam, String> subjectColumn;

	@FXML
	private TableColumn<Exam, String> courseColumn;

	@FXML
	private TableColumn<Exam, String> startTimeColumn;

	@FXML
	private TableColumn<Exam, String> examIDcolumn;

	@FXML
	private Text newExamDurationText;

	@FXML
	private TextField durationInMinutesTextField;

	@FXML
	private Text reasonForRequestText;

	@FXML
	private Button sendRequestBtn;

	@FXML
	private Button requestNewExamDurationBtn;

	@FXML
	private TextArea requestReasonTextArea;

	@FXML
	private Button lockExamBtn;

	@FXML
	private Button backButton;

	// global variables
	private boolean visibleValue = false;
	private ObservableList<Exam> examsList = FXCollections.observableArrayList();

	@FXML
	void VBox(MouseEvent event) {

	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method checks which exam was chosen and sets its examStatus parameter as 'locked' in the DB examsawaitingdurationapproval table.
	 * <p>
	 * then, the examStudent controllers lock the exam for the students.
	 * <p>
	 * it will pop an error message if an exam was not chosen.
	 * <p>
	 * <b>Receive:</b> the method gets a click event on the lock exam button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@FXML
	void lockExamBtnClicked(ActionEvent event) {
		Exam e = examContentTableView.getSelectionModel().getSelectedItem();
		if (e == null)
			SpecialCalls.customeError("Error", "No exams were chosen.", "Please choose an exam from the table.");
		else {
			try {
				ClientUI.clientHandler.handleMessageFromClientUI("lockExam:::" + e.getId());
				String returnMsg = (String) ClientHandler.returnMessage;
				if (returnMsg.equals("good")) {
					SpecialCalls.callSuccessFrame("Exam locked successfuly.");
					examContentTableView.getItems()
							.removeAll(examContentTableView.getSelectionModel().getSelectedItem());

				}
			} catch (NullPointerException ex) {
				SpecialCalls.customeError("Error", "Exam is already awaiting to approval.", "");
			}
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method makes the new exam duration text fields visible/invisible on a click.
	 * <p>
	 * <b>Receive:</b> the method gets a click event on the request new exam duration button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@FXML
	void requestNewExamDurationBtnClicked(ActionEvent event) {
		visibleValue = !visibleValue;
		newExamDurationText.setVisible(visibleValue);
		reasonForRequestText.setVisible(visibleValue);
		durationInMinutesTextField.setVisible(visibleValue);
		requestReasonTextArea.setVisible(visibleValue);
		sendRequestBtn.setVisible(visibleValue);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method collects the data from the new exam duration text fields and sends it to the principal for approval.
	 * <p>
	 * it will pop an error message if all relevant fields were not filled or an exam was not chosen.
	 * <p>
	 * <b>Receive:</b> the method gets a click event on the send request button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@FXML
	void sendRequestBtnClicked(ActionEvent event) {
		String newDuration = durationInMinutesTextField.getText();
		int duration = 0;
		boolean durationIsANumber = true;
		if (newDuration.isBlank())
			SpecialCalls.customeError("Error", "New duration was not entered.", "Please Enter a valid new duration.");
		else if (requestReasonTextArea.getText().isBlank()) {
			SpecialCalls.customeError("Error", "Reason for change not entered.",
					"Please fill the reason for the request.");
		} else {
			try {
				duration = Integer.parseInt(newDuration);
			} catch (NumberFormatException nfe) {
				SpecialCalls.customeError("Error", "New duration is not a number.",
						"Please Enter a valid new duration.");
				durationIsANumber = false;
			}
			if (durationIsANumber) {
				Exam e = examContentTableView.getSelectionModel().getSelectedItem();
				if (e == null)
					SpecialCalls.customeError("Error", "No exams were chosen.",
							"Please choose an exam from the table.");
				else {
					try {
						ClientUI.clientHandler.handleMessageFromClientUI("requestChangeExamDuration:::" + "examID: "
								+ e.getId() + "subject: " + e.getSubject() + "course: " + e.getCourse()
								+ "newDuration: " + duration + "causeOfChange: " + (requestReasonTextArea.getText()));
						// if field are valid pop success window or error for already exist question
						String returnMsg = (String) ClientHandler.returnMessage;
						if (returnMsg.equals("success")) {
							SpecialCalls.callSuccessFrame("Request successfuly sent.");
						}
					} catch (NullPointerException ex) {
						SpecialCalls.customeError("Error", "Exam is already awaiting approval.", "");
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method returns the teacher to the teacher menu.
	 * <p>
	 * <b>Receive:</b> the method gets a click event on the back button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.TeacherMenu.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method is executed once when entering the window.
	 * <p>
	 *  it adds all the relevant exams to the table on the window that are ongoing.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<Exam> examList = new ArrayList<Exam>();
		// ask the server for all of the current ongoing exams of this teacher
		ClientUI.clientHandler
				.handleMessageFromClientUI("getCurrentExams:::" + ClientUI.clientHandler.myDetails.getId());
		// take the server data and create exams arrayList to place into the
		// TableView
		try {
			examList = SpecialCalls.callCurrentExams((String) ClientHandler.returnMessage);
		} catch (Exception e) {
			SpecialCalls.customeError("Error", "No current exams",
					"If there are exams, please contact your system administartor");
			backButton.fire();
		}
		examsList.addAll(examList);
		System.out.println(examsList.toString());
		// set all the desired columns into the TableView
		subjectColumn.setCellValueFactory(new PropertyValueFactory<Exam, String>("subject"));
		courseColumn.setCellValueFactory(new PropertyValueFactory<Exam, String>("course"));
		startTimeColumn.setCellValueFactory(new PropertyValueFactory<Exam, String>("startingTime"));
		examIDcolumn.setCellValueFactory(new PropertyValueFactory<Exam, String>("id"));
		examContentTableView.setItems(examsList);
	}

}
