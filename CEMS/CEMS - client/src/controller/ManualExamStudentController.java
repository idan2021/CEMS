package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import client.ClientHandler;
import client.ClientUI;
import entity.MyFile;
import enums.window;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.FXMLutil;
import utils.SpecialCalls;
import utils.stringSplitter;

/**
 * <p>
 * <b>Explanation:</b> The class manual exam is the exam of the student. He can
 * download and upload the test and see the timer of the exam.
 * <p>
 * 
 * @author OrSteiner
 */
public class ManualExamStudentController {

	@FXML
	private Button downloadBtn;

	@FXML
	private Button uploadBtn;

	@FXML
	private Text TimerDisplayTxt;

	@FXML
	private Button mainMenuBtn;

	@FXML
	private Text uploadExamTxt;

	@FXML
	private ImageView approveImage;

	// Global variable
	private String examID;
	private int seconds = 0;
	private int minutes = 0;
	private int hours = 0;
	private String timeString;
	private MyFile examFile;
	private boolean flag = true;
	private boolean flag1 = true;
	private boolean flag2 = true;

	// Calculate and present the time passed
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
		/**
		 * <p>
		 * <b>Explanation:</b> The method is the timer logic.
		 * <p>
		 * <b>Receive:</b> The method doesn't get anything.
		 * <p>
		 * <b>Return:</b> The method doesn't return anything.
		 * <p>
		 * 
		 * @author OrSteiner
		 */
		public void run() {
			if (minutes == 0 && hours > 0) {
				hours--;
				minutes = 60;
			}
			if (seconds == 0 && minutes > 0) {
				minutes--;
				seconds = 60;
				ClientUI.clientHandler.handleMessageFromClientUI("getExamChanges:::" + examID);
				String[] respond = stringSplitter.dollarSplit((String) ClientHandler.returnMessage);
				if (respond[0].equals("approved") && flag) {
					flag = false;
					updateClock(respond[1]);
				}
				if (respond[0].equals("locked")) {
					hours = 0;
					minutes = 0;
					seconds = 1;
				}
			}
			seconds--;
			timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			TimerDisplayTxt.setText(timeString);
			if (hours == 0 && minutes == 0 && seconds == 0)
				exemTimeOver();
		}
	};

	/**
	 * <p>
	 * <b>Explanation:</b> The method is to add time to the timer.
	 * <p>
	 * <b>Receive:</b> The method get the time to add.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	private void updateClock(String timeToAdd) {
		int TTA = Integer.parseInt(timeToAdd);
		hours += TTA / 60;
		minutes += TTA % 60;
		if (minutes > 60) {
			hours++;
			minutes -= 60;
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method take the exam id and initialize the exam with
	 * it.
	 * <p>
	 * <b>Receive:</b> The method get examID.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public void initializeExam(String examID) {
		this.examID = examID;
		// ask the data base for the exam blob (examFiles table)
		ClientUI.clientHandler.handleMessageFromClientUI("getManualExamFile:::" + examID);
		examFile = (MyFile) ClientHandler.returnMessage;
		// ask the data base for the exam details (exams table)
		ClientUI.clientHandler.handleMessageFromClientUI("getExamDuration:::" + examID);
		String examDuration = (String) ClientHandler.returnMessage;
		// here we need to get the exam details as got from the previous frame
		setExamIDAndTimer(examID, examDuration);
		// start the timer
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method takes care of what happens if the exam time is
	 * over.
	 * <p>
	 * <b>Receive:</b> The method doesn't get anything.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	private void exemTimeOver() {
		uploadBtn.setVisible(false);
		approveImage.setVisible(false);
		uploadExamTxt.setText("Exam time is over, you can not upload files anymore");
		timer.cancel();
		// sign the student as someone who start the exam but did not submitted
		flag1 = false;
		summbitBlank();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method Submit blank exam.
	 * <p>
	 * <b>Receive:</b> The method doesn't get anything.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	private void summbitBlank() {
		MyFile fileToUpload = new MyFile(ClientUI.clientHandler.myDetails.getId(), examID, "manual");
		fileToUpload.setUserType("student");
		ClientUI.clientHandler.handleMessageFromClientUI(fileToUpload);

	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method let user upload a "docx" file from his
	 * computer.
	 * <p>
	 * <b>Receive:</b> The method get the event 'click' on 'upload' button.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void uploadBtnClicked(ActionEvent event) throws FileNotFoundException {
		ArrayList<String> errorLog = new ArrayList<String>();
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("upload finished exam");
		File selectedfile = filechooser.showOpenDialog(null);
		if (selectedfile != null && selectedfile.getName().endsWith(".docx")) {
			// here we need to implements upload file to sql DB
			MyFile fileToUpload = new MyFile(ClientUI.clientHandler.myDetails.getId(), examID, "manual");
			fileToUpload.setUserType("student");
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
					bufferIn.close();
					approveImage.setVisible(true);
					SpecialCalls.callSuccessFrame("Exam uploaded successfuly");
					flag2 = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			errorLog.add("Uploaded file is not valid");
			SpecialCalls.callErrorFrame(errorLog);
		}

	}

	/**
	 * <p>
	 * <b>Explanation:</b> By clicking on download the exam file (according to the
	 * examID) will be downloaded to the computer.
	 * <p>
	 * <b>Receive:</b> The method get the evnet 'click' on 'download' button.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void downloadBtnClicked(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter for docx files only
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DOCX files (*.docx)", "*.docx");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File theFile = fileChooser.showSaveDialog(window);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(theFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // get path
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		try {
			bos.write(examFile.getMybytearray(), 0, examFile.getSize());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method set the timer for the exam.
	 * <p>
	 * <b>Receive:</b> The method get examID and the exam duration.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public void setExamIDAndTimer(String examID, String examDuration) {
		this.examID = examID;
		minutes = Integer.parseInt(examDuration);
		while (minutes >= 60) {
			hours++;
			minutes -= 60;
		}
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The method let the student go back to the main menu and
	 * if submitted before exam time ends we ask the user if he sure of that.
	 * <p>
	 * <b>Receive:</b> The method get the event 'click' on 'main menu' button.
	 * <p>
	 * <b>Return:</b> The method doesn't return anything.
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	@FXML
	void mainMenuBtnClicked(ActionEvent event) {
		// if submitted before exam time ends ask the user if he sure of that
		if (flag1 && flag2) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Leaving the exam means you submitting a blank exam");
			alert.setContentText("Are you sure you want to do that?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				summbitBlank();
				try {
					FXMLutil.swapScene(event, window.StudentMenu.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				FXMLutil.swapScene(event, window.StudentMenu.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
