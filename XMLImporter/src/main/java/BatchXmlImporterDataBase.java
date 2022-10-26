import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import xmlmodels.Company;
import xmlmodels.Staff;

public class BatchXmlImporterDataBase {
  static CompanyController companyController = new CompanyController();
  static void connectWithPostgresDataBase(Company company) throws SQLException {
    try (Connection connection = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {

      final int companyId;

      companyId = companyController.insertCompanyValuesIntoDataBase(company, connection);

      for (Staff staff : company.staff) {
        ImporterInsert.insertStaffValues(connection, companyId, staff);
        ImporterInsert.insertSalaryValues(connection, staff);
      }
    }
  }

}
