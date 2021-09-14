package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import entity.Exam;
import entity.ExamWaitingForApproval;
import entity.QuestionInExam;

/**
 * <p>
 * <b>Explanation:</b> The class presents all the split methods to split strings
 * with
 * <p>
 * 
 * @author OrSteiner
 */
public class stringSplitter {

	
	public static String[] dollarSplit(String str) {

		return str.split(":::");
	}
	public static String[] sulamitSplit(String str) {

		return str.split("###");
	}
	public static String[] umpercentSplit(String str) {

		return str.split("&&&");
	}
	public static String[] stringBySpace(String str) {

		return str.split(" ");
	}

	public static String[] stringByComma(String str) {

		return str.split(",");
	}

	
	public static String[] stringByPrecent(String str) {

		return str.split("%%%");
	}
	
	

	public static String[] stringByQuestionParameters(String str) {
		return str.split(
				"composer: |subject: |content: |WrongAnswer1: |WrongAnswer2: |WrongAnswer3: |CorrectAnswer: |courses: |questionID: |]+");
	}
	
	// used in sql Queries of compose an exam (Exam.toString())
	public static String[] stringByExamParameters(String str) {
		return str.split(
				"examID: |dateOfExam: |examDuration: |examComposer: |examSubject: |examCourse: |startingTime: |]+");
	}
	
	// used in sql Queries of compose an exam (QuestionInExam.toString())
	public static String[] stringByQuestionInExamParameters(String str) {
		return str.split(
				"questionID: |points: |commentForTeacher: |commentForStudent: |examID: |]+");
	}
	
	//used in sql Queries to push an exam to duration approval table
	public static String[] stringByExamsAwaitingDurationApprovalParameters(String str) {
		return str.split(
				"examID: |subject: |course: |newDuration: |causeOfChange: |]+");
	}






}
