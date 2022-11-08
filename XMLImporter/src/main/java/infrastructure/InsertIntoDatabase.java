package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import xmlmodels.Staff;

public class InsertIntoDatabase {
  static void insertStaff(Connection connection, int companyId, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
      "INSERT INTO staff(id,company_id, first_name, last_name, nick_name) VALUES (?,?,?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setInt(2, companyId);
      preparedStatement.setString(3, staff.firstname);
      preparedStatement.setString(4, staff.lastname);
      preparedStatement.setString(5, staff.nickname);
      preparedStatement.executeUpdate();
    }
  }
  static void insertSalary(Connection connection, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
      "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setString(2, staff.salary.currency);
      preparedStatement.setInt(3, staff.salary.value);
      preparedStatement.executeUpdate();
    }
  }
}