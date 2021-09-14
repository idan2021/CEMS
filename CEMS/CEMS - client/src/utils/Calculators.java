package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import client.ClientHandler;
import client.ClientUI;
import entity.QuestionInExam;

/**
 * <p>
 * <b>Explanation:</b> The class contains methods to calculate stuff across the
 * program feel free to add your calculations in case needed
 * <p>
 * 
 * @author OrSteiner
 */
public class Calculators {

	/**
	 * <p>
	 * <b>Explanation:</b> This method calculate the grade for user exam
	 * <p>
	 * <b>Receive:</b> answersArray to get the grades from all questions and
	 * QIEarrayList to get the array size
	 * <p>
	 * <b>Return:</b> The grade of the exam by all summing up the questions grades
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static int calculateGrade(String[] answersArray, ArrayList<QuestionInExam> QIEarrayList) {
		int grade = 100;
		for (int i = 0; i < QIEarrayList.size(); i++)
			if (!(QIEarrayList.get(i).getQuestion().getCorrectAnswer().equals(answersArray[i]))) {
				grade -= QIEarrayList.get(i).getPoints();
			}
		return grade;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method randomize the order of the answers
	 * <p>
	 * <b>Receive:</b> All the answers in the exam
	 * <p>
	 * <b>Return:</b> A rendomize list of the questions in exams
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String[] calculateAnswersOrder(String ans1, String ans2, String ans3, String ans4) {
		String[] returnOrder = new String[4];
		// generate a List that contains the numbers 0 to 9
		List<Integer> digits = IntStream.range(0, 4).boxed().collect(Collectors.toList());
		// shuffle the List
		Collections.shuffle(digits);
		// take the first 4 elements of the List
		returnOrder[digits.get(0)] = ans1;
		returnOrder[digits.get(1)] = ans2;
		returnOrder[digits.get(2)] = ans3;
		returnOrder[digits.get(3)] = ans4;
		return returnOrder;
	}

	/**
	 * <p>
	 * <b>Explanation:</b> This method calculates if there is any suspicious
	 * submitted exams that the teacher might want to see
	 * <p>
	 * <b>Return:</b> AI result for cheating
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	public static String calculateSuspectedcopies() {
		// get the data from student exams table
		ClientUI.clientHandler.handleMessageFromClientUI("getPendingStudentExam");
		String serverRespond = (String) ClientHandler.returnMessage;
		// place the students exams into the array list
		String[] str = stringSplitter.stringByPrecent(serverRespond);
		studentExam[] SAL = new studentExam[str.length];
		String[] SALparam = new String[4];
		for (int i = 0; i < str.length; i++) {
			SALparam = stringSplitter.dollarSplit(str[i]);
			studentExam sE = new studentExam(SALparam[0], SALparam[1], SALparam[2], SALparam[3]);
			SAL[i] = sE;
		}
		// compare the answers of students that got a grade between 55 to 85
		StringBuilder AIresults = new StringBuilder();
		for (int i = 0; i < SAL.length; i++)
			for (int j = i + 1; j < SAL.length; j++)
				// if the grades are identical AND the bounds of the grade the check the answers
				if (SAL[i].getGrade().equals(SAL[j].getGrade()) && Integer.parseInt(SAL[i].getGrade()) < 85
						&& Integer.parseInt(SAL[j].getGrade()) > 55)
					// if the students answers are identical the teacher might want to check this
					// out
					if (SAL[i].getStudentAnswers().equals(SAL[j].getStudentAnswers()))
						// make a result string to bring back to the asker
						AIresults.append("Our AI found that In exam:" + SAL[i].getExamID() + ", the students: " + SAL[i].getStudentID()
								+ " and " + SAL[j].getStudentID()+", might have copied from each other\n");
		return AIresults.toString();
	}

	/**
	 * <p>
	 * <b>Explanation:</b> The class is for checking if there's cheating exams
	 * <p>
	 * 
	 * @author OrSteiner
	 */
	private static class studentExam {
		String studentID;
		String examID;
		String grade;
		String studentAnswers;

		studentExam(String stuID, String exID, String grd, String stuAns) {
			this.examID = exID;
			this.studentID = stuID;
			this.grade = grd;
			this.studentAnswers = stuAns;
		}

		public String getStudentID() {
			return studentID;
		}

		public String getExamID() {
			return examID;
		}

		public String getGrade() {
			return grade;
		}

		public String getStudentAnswers() {
			return studentAnswers;
		}
	}
}
