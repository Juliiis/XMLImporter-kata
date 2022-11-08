package infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import xmlmodels.Company;
import xmlmodels.Staff;

public class CompanyDataBase {
  static CompanyController companyController = new CompanyController();
  public static void connectWithPostgresDataBase(Company company) throws SQLException {
    try (Connection connection = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {

      final int companyId;

      companyId = companyController.insertCompanyIntoDataBase(company, connection);

      for (Staff staff : company.staff) {
        InsertIntoDatabase.insertStaff(connection, companyId, staff);
        InsertIntoDatabase.insertSalary(connection, staff);
      }
    }
  }

}
