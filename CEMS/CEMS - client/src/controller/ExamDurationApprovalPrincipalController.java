package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.ExamWaitingForApproval;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class Exam Duration Approval Principal controller is reached from the principal menu.
 * <p>
 * The class allows the principal to view all the current ongoing exams that are awaiting new duration approval. 
 * <p>
 * the principal chooses the exam she wishes to approve/disapprove and clicks the relevant button.
 * <p>
 * @author EthanButorsky
 */
public class ExamDurationApprovalPrincipalController implements Initializable {

	@FXML
	private TableView<ExamWaitingForApproval> contentTableView;

	@FXML
	private TableColumn<ExamWaitingForApproval, String> examIdColumn;

	@FXML
	private TableColumn<ExamWaitingForApproval, String> subjectColumn;

	@FXML
	private TableColumn<ExamWaitingForApproval, String> courseColumn;

	@FXML
	private TableColumn<ExamWaitingForApproval, String> newDurationColumn;

	@FXML
	private TableColumn<ExamWaitingForApproval, String> detailsColumn;

	@FXML
	private Button approveButton;

	@FXML
	private Button disapproveButton;

	@FXML
	private Button backButton;

	private ObservableList<ExamWaitingForApproval> examsList = FXCollections.observableArrayList();
	URL location;
	ResourceBundle resources;

	@FXML
	void VBox(MouseEvent event) {

	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method checks which exam was chosen and sets it as approved in the DB examsawaitingdurationapproval table.
	 * <p>
	 * then, the examStudent controllers update the duration according to that table.
	 * <p>
	 * it will pop an error message if an exam was not chosen.
	 * <p>
	 * <b>Receive:</b> the method gets a click event on the approve button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@FXML
	void aprroveButtonClicked(ActionEvent event) {
		ExamWaitingForApproval e = contentTableView.getSelectionModel().getSelectedItem();
		if (e == null)
			SpecialCalls.customeError("Error", "No exams were chosen.", "Please choose an exam from the table.");
		else {
			ClientUI.clientHandler.handleMessageFromClientUI("extendExamDuration:::" + e.getId());
			String respond = (String) ClientHandler.returnMessage;
			if (respond.equals("good")) {
				SpecialCalls.callSuccessFrame("New duration succesfully approved !");
				contentTableView.getItems().removeAll(contentTableView.getSelectionModel().getSelectedItem());
			}
			else {
				SpecialCalls.customeError("Bad gateway: 502", "nothing happend", null);
			}
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method returns the principal to the principal menu.
	 * <p>
	 * <b>Receive:</b> the method gets a click event on the back button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.PrincipalMenu.toString());
	}

	/**
	 * <p>
	 * <b>Explanation:</b> checks which exam was chosen and disapproves its new duration by removing it from the examsawaitingapproval table from the DB.
	 * <p>
	 * it will pop an error message if an exam was not chosen.
	 * <p>
	 * <b>Receive:</b> the method gets a click event on the disapprove button.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@FXML
	void disapproveButtonClicked(ActionEvent event) {
		ExamWaitingForApproval e = contentTableView.getSelectionModel().getSelectedItem();
		if (e == null)
			SpecialCalls.customeError("Error", "No exams were chosen.", "Please choose an exam from the table.");
		else {
			ClientUI.clientHandler.handleMessageFromClientUI("disapproveExamDuration:::" + e.getId());
			contentTableView.getItems().removeAll(contentTableView.getSelectionModel().getSelectedItem());
			SpecialCalls.customeError("Message", (String) ClientHandler.returnMessage, null);
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> the method is executed once when entering the window.
	 * <p>
	 *  it adds all the relevant exams to the table on the window that are awaiting duration approval.
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path for the root object and the resorses that used to localize the root object.
	 * <p>
	 * <b>Return:</b> The method does not return anything. 
	 * <p>
	 * @author EthanButorsky
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.location = location;
		this.resources = resources;
		ArrayList<ExamWaitingForApproval> examList = new ArrayList<ExamWaitingForApproval>();
		// ask the server for all of the current ongoing exams of this teacher
		ClientUI.clientHandler.handleMessageFromClientUI("examsAwaitingApproval:::");
		// take the server data and create exams arrayList to place into the
		// TableView
		examList = SpecialCalls.callExamsAwaitingApproval((String) ClientHandler.returnMessage);
		examsList.addAll(examList);
		// set all the desired columns into the TableView
		examIdColumn.setCellValueFactory(new PropertyValueFactory<ExamWaitingForApproval, String>("id"));
		subjectColumn.setCellValueFactory(new PropertyValueFactory<ExamWaitingForApproval, String>("subject"));
		courseColumn.setCellValueFactory(new PropertyValueFactory<ExamWaitingForApproval, String>("course"));
		newDurationColumn.setCellValueFactory(new PropertyValueFactory<ExamWaitingForApproval, String>("newDuration"));
		detailsColumn.setCellValueFactory(new PropertyValueFactory<ExamWaitingForApproval, String>("explanation"));
		contentTableView.setItems(examsList);
		// System.out.println(examsList.get(0).toString());
	}

}
