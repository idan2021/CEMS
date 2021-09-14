package controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ClientHandler;
import client.ClientUI;
import entity.Exam;
import entity.MyFile;
import entity.QuestionInExam;
import enums.window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> The class to upload a manual exam for the students to
 * download
 * <p>
 * 
 * @author OrSteiner
 */
public class UploadManualExamTeacherController implements Initializable {

	@FXML
	private ComboBox<String> subjectSelection;

	@FXML
	private TextField timeInMinTextField;

	@FXML
	private TextField commentForStudentsTxtFld;

	@FXML
	private ComboBox<String> courseSelection;

	@FXML
	private TextField commentForTeachersTxtFld;

	@FXML
	private Button uploadBtn;

	@FXML
	private ImageView approveImage;

	@FXML
	private DatePicker examDatePicker;
    

	// Global variables
	private ObservableList<String> SubjectList = FXCollections.observableArrayList();
	private ObservableList<String> CourseList = FXCollections.observableArrayList();
	private String examID;
	private boolean examupload = false;

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the courses data from DB
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> List of all the courses in the system
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void initializeCourses(ActionEvent event) {
		// after subject selected clear the courses box
		courseSelection.getItems().clear();
		// ask server to return courses related to the subject selected
		ClientUI.clientHandler.handleMessageFromClientUI("course:::" + subjectSelection.getValue());
		String str = (String) ClientHandler.returnMessage;
		String[] string = stringSplitter.stringByComma(str);
		CourseList.addAll(string);
		// set new items to show in courses box
		courseSelection.setItems(CourseList);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method pulls all the subjects data from DB
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> List of all the subjects in the system
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ClientUI.clientHandler.handleMessageFromClientUI("subject");
		String returnMsg = (String) ClientHandler.returnMessage;
		String[] string = stringSplitter.stringBySpace(returnMsg);
		SubjectList.addAll(string);
		subjectSelection.setItems(SubjectList);
		// Initialize the date of exam to todays date
		examDatePicker.setValue(LocalDate.now());

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method uploads exam ״.docx" from directory
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Exam uploaded successfully in case of success else error
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void uploadBtnClicked(ActionEvent event) throws IOException {
		// before upload make sure you selected course and subject (because the upload
		// needs the details)
		ArrayList<String> errorLog = new ArrayList<String>();
		String subject = subjectSelection.getValue();
		if (subject == null)
			errorLog.add("Subject must be selected before upload\n");
		String course = courseSelection.getValue();
		if (course == null)
			errorLog.add("Course must be selected before upload\n");
		FileChooser filechooser = new FileChooser();
		if (!errorLog.isEmpty())
			SpecialCalls.callErrorFrame(errorLog);
		else {
			
			filechooser.setTitle("upload finished exam");
			File selectedfile = filechooser.showOpenDialog(null);
			if (selectedfile != null && selectedfile.getName().endsWith(".docx")) {
				// here we need to implements upload file to sql DB
				ClientUI.clientHandler.handleMessageFromClientUI("examIDgenerator");
				StringBuilder examID = new StringBuilder();
				ClientUI.clientHandler.handleMessageFromClientUI("subjectIDAndcourseID:::"+course);
				examID.append((String) ClientHandler.returnMessage);
				ClientUI.clientHandler.handleMessageFromClientUI("examIDgenerator");
				examID.append((String) ClientHandler.returnMessage);
				this.examID=examID.toString();
				MyFile fileToUpload = new MyFile(ClientUI.clientHandler.myDetails.getId(), examID.toString(), "manual");
				fileToUpload.setUserType("teacher");
				byte[] myByteArray = new byte[(int) selectedfile.length()];
				FileInputStream fileIn = new FileInputStream(selectedfile);
				BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
				fileToUpload.initArray(myByteArray.length);
				fileToUpload.setSize(myByteArray.length);
				try {
					bufferIn.read(fileToUpload.getMybytearray(), 0, myByteArray.length);
					ClientUI.clientHandler.handleMessageFromClientUI(fileToUpload);
					String serverRespond = (String) ClientHandler.returnMessage;
					if (serverRespond.equals("success")) {
						approveImage.setVisible(true);
						SpecialCalls.callSuccessFrame("Exam uploaded successfuly");
						examupload = true;
						uploadBtn.setDisable(true);
						bufferIn.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				errorLog.add("Uploaded file is not valid");
				SpecialCalls.callErrorFrame(errorLog);
			}
		}
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
	void backBtnClicked(ActionEvent event) {
		try {
			FXMLutil.swapScene(event, window.TeacherMenu.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The upload of the exam by the teacher to the DB
	 * <p>
	 * <b>Receive:</b> Event action for the button to launch
	 * <p>
	 * <b>Return:</b> Exam added successfully in case of success else error
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void createBtnClicked(ActionEvent event) {
		ArrayList<String> errorLog = new ArrayList<String>();
		String subject = subjectSelection.getValue();
		if (subject == null)
			errorLog.add("Subject must be selected\n");
		String course = courseSelection.getValue();
		if (course == null)
			errorLog.add("Course must be selected\n");
		String examDuration = timeInMinTextField.getText();
		if (examDuration.isBlank())
			errorLog.add("Exam duration must be filled\n");
		if(!examupload)
			errorLog.add("Exam file must be uploaded\n");
		LocalDate myDate = examDatePicker.getValue();
		String examDate = myDate.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
		String TeacherComment = commentForTeachersTxtFld.getText();
		String StudentComment = commentForStudentsTxtFld.getText();
		String composer =ClientUI.clientHandler.myDetails.getId();
		if (examDate.isBlank())
			errorLog.add("Exam date must be picked\n");
		String Manual = "manual";
		if (!errorLog.isEmpty())
			SpecialCalls.callErrorFrame(errorLog);
		else {
			Exam newExam=new Exam(examID,examDate,examDuration,composer,subject,course,new ArrayList<QuestionInExam>(),Manual);
			// send the server the newExam to insert
			if (TeacherComment.isBlank())
				TeacherComment = " ";
			if (StudentComment.isBlank())
				StudentComment = " ";
			// send the server the newExam to insert
			ClientUI.clientHandler.handleMessageFromClientUI("insertExam:::" + newExam.toString()+":::"+StudentComment+":::"+TeacherComment);
			// get return message from server to indicate if the insertion succeed
			String insertExamReturnedMessage = (String) ClientHandler.returnMessage;
			if(insertExamReturnedMessage.equals("success")) {
				SpecialCalls.callSuccessFrame("Exam successfuly added");
				try {
					FXMLutil.swapScene(event, window.TeacherMenu.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
