import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import xmlmodels.Company;
import xmlmodels.Staff;

public class BatchXmlImporterDataBase {
  static void connectWithPostgresDataBase(Company company) throws SQLException {
    try (Connection connection = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {

      final int companyId;

      companyId = BatchXmlImporterInsert.insertCompanyValues(company, connection);

      for (Staff staff : company.staff) {
        BatchXmlImporterInsert.insertStaffValues(connection, companyId, staff);
        BatchXmlImporterInsert.insertSalaryValues(connection, staff);
      }
    }
  }

  static int checkIfCompanyIdExist(PreparedStatement preparedStatement) throws SQLException {
    final int companyId;
    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
      if (generatedKeys.next()) {
        companyId = (int) generatedKeys.getLong(1);
      } else throw new SQLException("No ID obtained.");
    }
    return companyId;
  }

}
