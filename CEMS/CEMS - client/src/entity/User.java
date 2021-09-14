package entity;

/**
 * <p>
 * <b>Explanation:</b> The User class is the superclass of the Student, Teacher, and principal classes.
 * <p>
 * it contains variables such as ID, role and names that identify users.
 * <p>
 * it contains standard getters and setters, enum for its subclasses and an overriden "equals" method.
 * <p>
 * @author Everyone
 */
public abstract class User {

	String id, firstName, lastName, userName, password, email;
	Role role;

	public enum Role {
		student, teacher, principal
	}

	// constructor
	protected User(String id, String firstName, String lastName, String userName, String password, String email) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.userName = userName;
		this.password = password;
	}

	// setters and getters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstLast() {
		return this.getFirstName() + " " + this.getLastName();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * User will be equal if user id is the same.
	 */

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof User) {

		} else if (obj instanceof String) {
			String other = (String) obj;

			if (this.id == other)
				return true;
			else
				return false;
		}
		return false;

	}
}
