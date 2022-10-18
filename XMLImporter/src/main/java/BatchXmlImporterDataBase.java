import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import xmlmodels.Company;
import xmlmodels.Staff;

public class BatchXmlImporterDataBase {
  static void connectionWithPostgresDataBase(Company company) throws SQLException {
    try (Connection connection = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {

      final int companyId;

      companyId = insertCompanyValues(company, connection);

      // recorre un staff de company.staff y setea los valores de staff y de salary
      for (Staff staff : company.staff) {
        insertStaffValues(connection, companyId, staff);
        insertSalaryValues(connection, staff);
      }
    }
  }

  private static int insertCompanyValues(Company company, Connection conn) throws SQLException {
    final int companyId;
    try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO company(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, company.name);
      preparedStatement.executeUpdate();

      // envia un getLong si la generatedKeys existe sino envia una SQLexception
      companyId = checkIfCompanyIdExist(preparedStatement);

    }
    return companyId;
  }

  private static int checkIfCompanyIdExist(PreparedStatement preparedStatement) throws SQLException {
    final int companyId;
    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
      if (generatedKeys.next()) {
        companyId = (int) generatedKeys.getLong(1);
      } else throw new SQLException("No ID obtained.");
    }
    return companyId;
  }

  private static void insertStaffValues(Connection conn, int companyId, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = conn.prepareStatement(
      "INSERT INTO staff(id,company_id, first_name, last_name, nick_name) VALUES (?,?,?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setInt(2, companyId);
      preparedStatement.setString(3, staff.firstname);
      preparedStatement.setString(4, staff.lastname);
      preparedStatement.setString(5, staff.nickname);
      preparedStatement.executeUpdate();
    }
  }

  private static void insertSalaryValues(Connection conn, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = conn.prepareStatement(
      "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setString(2, staff.salary.currency);
      preparedStatement.setInt(3, staff.salary.value);
      preparedStatement.executeUpdate();
    }
  }

}
