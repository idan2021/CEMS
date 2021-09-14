package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.Question;
import enums.window;

/**
 * <p>
 * <b>Explanation:</b> The class presents all the questions in the system
 * <p>
 * 
 * @author DvirVahab
 */
public class ViewQuestionsPrincipalController implements Initializable {

	@FXML
	private TableView<Question> questionsTableView;
	@FXML
	private TableColumn<Question, String> subjectCol;
	@FXML
	private TextField searchTextField;
	@FXML
	private TableColumn<Question, String> courseCol;

	@FXML
	private TableColumn<Question, String> composerCol;

	private ArrayList<Question> questionArrayList;

	private ObservableList<Question> questionObservalbleList = FXCollections.observableArrayList();;

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
	 * @author DvirVahab
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		questionArrayList = new ArrayList<Question>();
		ClientUI.clientHandler.handleMessageFromClientUI("pullAllQuestions");
		try {
		questionArrayList = SpecialCalls.callQuestions((String) ClientHandler.returnMessage);
		} catch (Exception e) {
		}
		// take the server data and create questions arrayList to place into the
		// Table view in "AttachQuestion" frame

		questionObservalbleList.addAll(questionArrayList);

		// set all the desired columns into the TableView
		subjectCol.setCellValueFactory(new PropertyValueFactory<Question, String>("subject"));
		courseCol.setCellValueFactory(new PropertyValueFactory<Question, String>("courses"));
		composerCol.setCellValueFactory(new PropertyValueFactory<Question, String>("composer"));

		// contentColumn.setCellValueFactory(new
		// PropertyValueFactory<Question,String>("content"));
		// correctAnswerColumn.setCellValueFactory(new
		// PropertyValueFactory<Question,String>("correctAnswer"));
		questionsTableView.setItems(questionObservalbleList);
		questionsTableView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2)
				try {
					questionViewClick(event);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		});

		questionsTableView.setTooltip(new Tooltip("Click twice for extended details about the question") {

		});

		// set message for user how to search
		searchTextField.setTooltip(new Tooltip("Search by subject,course or composer") {

		});

		// Set search filter by subject,course or composer
		FilteredList<Question> searchFilter = new FilteredList<>(questionObservalbleList, b -> true);

		/**
		 * <p>
		 * <b>Explanation:</b> Searches the question by one of its details
		 * <p>
		 * <b>Receive:</b> The search string the question inputs in the text box
		 * <p>
		 * <b>Return:</b> The search by the question input of details
		 * <p>
		 * 
		 * @author DvirVahab
		 */
		// set listener whenever user type in the search text filed
		searchTextField.textProperty().addListener((observa, old, neo) -> {
			searchFilter.setPredicate(question -> {

				// cases when the lines will be true= will be shown to user
				if (question.getSubject().toLowerCase().contains(neo.toLowerCase())
						|| question.getCourses().toLowerCase().contains(neo.toLowerCase())
						|| question.getComposer().toLowerCase().contains(neo.toLowerCase()))
					return true;
				else
					return false;

			});
		});
		// sort the data received by the filter
		SortedList<Question> sortedData = new SortedList<>(searchFilter);
		sortedData.comparatorProperty().bind(questionsTableView.comparatorProperty());

		// update view table results
		questionsTableView.setItems(sortedData);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to the principal's menu
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Shows the data about the question
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	void questionViewClick(MouseEvent event) throws IOException {
		Question Q = questionsTableView.getSelectionModel().getSelectedItem();
		FXMLLoader loader = FXMLutil.fxmlCreator(window.viewQuestionsPopUpWindow.toString());
		Parent root = loader.load();
		ViewQuestionsPopupPrincipalController viewQuestionsPopupPrincipalController = loader.getController();
		viewQuestionsPopupPrincipalController.displayQuestion(Q);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The return of the back button to the principal's menu
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Principal's menu
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@FXML
	void backButtonClick(ActionEvent event) {
		try {
			FXMLutil.swapScene(event, window.PrincipalMenu.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			SpecialCalls.customeError("Error", "Error return to main menu", e.getMessage());
		}
	}
}



