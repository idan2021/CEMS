package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ClientHandler;
import client.ClientUI;
import entity.Principal;
import entity.Student;
import entity.Teacher;
import entity.User;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.FXMLutil;

/**
 * <p>
 * <b>Explanation:</b> The class presents the list of users in the CEMS system
 * <p>
 * 
 * @author DvirVahab
 */
public class ViewUsersPrincipalController implements Initializable {

	@FXML
	private TableView<User> usersTableView;

	@FXML
	private TableColumn<User, String> idCol;

	@FXML
	private TableColumn<User, String> firstNameCol;

	@FXML
	private TableColumn<User, String> lastNameCol;

	@FXML
	private TableColumn<User, String> userNameCol;

	@FXML
	private TableColumn<User, String> eMailCol;

	@FXML
	private TableColumn<User, String> roleCol;

	@FXML
	private TextField searchTextField;

	@FXML
	private Button backButton;
	private ArrayList<User> userArrayList;
	private ObservableList<User> userObservalbleList = FXCollections.observableArrayList();;

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the users data from the DB and
	 * prepares the query received to fit the table on which the users are presented
	 * on
	 * <p>
	 * <b>Receive:</b> The method gets the location used to resolve relative path
	 * for the root object and the resources that used to localize the root object.
	 * <p>
	 * <b>Return:</b> List of all the users in the system
	 * <p>
	 * 
	 * @author DvirVahab
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize variables
		userArrayList = new ArrayList<>();
		ClientUI.clientHandler.handleMessageFromClientUI("pullAllUsers");
		String returnMsg = ((String) ClientHandler.returnMessage);

		// split users row from data base
		String[] returnMsgArr = utils.stringSplitter.stringByComma(returnMsg);

		// split row to user entity
		for (int i = 0; i < returnMsgArr.length; i++) {
			String[] temp = utils.stringSplitter.stringBySpace(returnMsgArr[i]);
			switch (temp[5]) {
			case "Teacher":
				userArrayList.add(new Teacher(temp[0], temp[1], temp[2], temp[3], "NULL", temp[4], temp[5]));
				break;
			case "Student":
				userArrayList.add(new Student(temp[0], temp[1], temp[2], temp[3], "NULL", temp[4], temp[5]));
				break;
			case "Principal":
				userArrayList.add(new Principal(temp[0], temp[1], temp[2], temp[3], "NULL", temp[4], temp[5]));
				break;
			default:
				break;
			}

		}
		idCol.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
		firstNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
		lastNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
		userNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
		eMailCol.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
		roleCol.setCellValueFactory(new PropertyValueFactory<User, String>("role"));

		userObservalbleList.addAll(userArrayList);
		usersTableView.setItems(userObservalbleList);

		// set message for user how to search
		searchTextField.setTooltip(new Tooltip("Search by id, email, role, username,first name and last name") {

		});

		// Set search filter by id, email, role, user name,first name and last name
		FilteredList<User> searchFilter = new FilteredList<>(userObservalbleList, b -> true);

		/**
		 * <p>
		 * <b>Explanation:</b> Searches the user by one of his details
		 * <p>
		 * <b>Receive:</b> The search string the user inputs in the text box
		 * <p>
		 * <b>Return:</b> The search by the user input of details
		 * <p>
		 * 
		 * @author DvirVahab
		 */
		// set listener whenever user type in the search text filed
		searchTextField.textProperty().addListener((observa, old, neo) -> {
			searchFilter.setPredicate(user -> {

				// cases when the lines will be true= will be shown to user
				if (user.getFirstLast().toLowerCase().contains(neo.toLowerCase())
						|| user.getId().toLowerCase().contains(neo.toLowerCase())
						|| user.getFirstName().toLowerCase().contains(neo.toLowerCase())
						|| user.getLastName().toLowerCase().contains(neo.toLowerCase())
						|| user.getEmail().toLowerCase().contains(neo.toLowerCase())
						|| user.getRole().toString().toLowerCase().contains(neo.toLowerCase()))
					return true;
				else
					return false;

			});
		});
		// sort the data received by the filter
		SortedList<User> sortedData = new SortedList<>(searchFilter);
		sortedData.comparatorProperty().bind(usersTableView.comparatorProperty());

		// update view table results
		usersTableView.setItems(sortedData);

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
	void backButtonClick(ActionEvent event) throws IOException {
		FXMLutil.swapScene(event, window.PrincipalMenu.toString());
	}
}



