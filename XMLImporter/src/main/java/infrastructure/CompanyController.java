package infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import xmlmodels.Company;

public class CompanyController {
  static int findIfTheLastIdExist(PreparedStatement preparedStatement) throws SQLException {
    final int companyId;
    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
      if (generatedKeys.next()) {
        companyId = (int) generatedKeys.getLong(1);
      } else throw new SQLException("No ID obtained.");
    }
    return companyId;
  }

}
