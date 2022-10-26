import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import xmlmodels.Company;

public class CompanyController {
  static int checkIfCompanyIdExist(PreparedStatement preparedStatement) throws SQLException {
    final int companyId;
    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
      if (generatedKeys.next()) {
        companyId = (int) generatedKeys.getLong(1);
      } else throw new SQLException("No ID obtained.");
    }
    return companyId;
  }
  static int insertCompanyValuesIntoDataBase(Company company, Connection connection) throws SQLException {
    final int companyId;
    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO company(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, company.name);
      preparedStatement.executeUpdate();

      companyId = checkIfCompanyIdExist(preparedStatement);
    }
    return companyId;
  }

}
