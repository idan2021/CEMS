package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.Question;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;

/**
 * <p>
 * <b>Explanation:</b> The class presents all the questions in the system
 * <p>
 * 
 * @author Or Steiner
 */
public class ViewAndEditQuestion1TeacherController implements Initializable {

	@FXML
	private TableView<Question> questionTableView;

	@FXML
	private TableColumn<Question, String> subjectColumn;

	@FXML
	private TableColumn<Question, String> coursesColumn;

	@FXML
	private TableColumn<Question, String> contentColumn;

	@FXML
	private TableColumn<Question, String> correctAnswerColumn;

	@FXML
	private Button backbtn;

	@FXML
	private Button EditBtn;

	// global variables
	private ObservableList<Question> questionsList = FXCollections.observableArrayList();

	/**
	 * <p>
	 * <b>Explanation:</b> The method creates new scene with specific question to
	 * display in next window
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> The display of the next window "ViewAndEditQuestion2Teacher"
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void editBtnClicked(ActionEvent event) throws IOException {
		ArrayList<String> errorLog = new ArrayList<String>();
		Question Q = questionTableView.getSelectionModel().getSelectedItem();
		if (Q == null) {
			errorLog.add("A question must be selected");
			SpecialCalls.callErrorFrame(errorLog);
		} else {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(window.ViewAndEditQuestion2Teacher.toString()));
			Parent root = loader.load();

			ViewAndEditQuestion2TeacherController VAEQcontroller = loader.getController();
			VAEQcontroller.displayQuestion(Q);

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
	}

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
	 * @author OrSteiner
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<Question> questionArrayList = new ArrayList<Question>();
		// ask the server for the all of the teacher questions
		ClientUI.clientHandler.handleMessageFromClientUI("pullQuestions:::" + ClientUI.clientHandler.myDetails.getId());
		// take the server data and create questions arrayList to place into the
		// TableView
		try {
			questionArrayList = SpecialCalls.callQuestions((String) ClientHandler.returnMessage);
		} catch (Exception e) {

		}
		questionsList.addAll(questionArrayList);
		// set all the desired columns into the TableView
		subjectColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("subject"));
		coursesColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("courses"));
		contentColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("content"));
		correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("correctAnswer"));
		questionTableView.setItems(questionsList);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to the teacher's menu
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Teacher's menu
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void backBtnClicked(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.TeacherMenu.toString());
	}

}
