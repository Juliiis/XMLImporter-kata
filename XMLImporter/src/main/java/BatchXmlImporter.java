import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import xmlmodels.Company;
import xmlmodels.Staff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;

public class BatchXmlImporter {

    public void importFiles(Path folderPath) throws IOException, JAXBException, SQLException {

        final String fileExtension = ".xml";
        List<Path> paths;

        // comprobar si el fichero es un directorio o no (walk) y listar lo Path en paths
        try (Stream<Path> pathStream = walk(folderPath)
          .filter(Files::isRegularFile)
          .filter(filePath ->
            filePath.toString()
              .endsWith(fileExtension))) {
            paths = pathStream
              .collect(Collectors.toList());
        }

        // crea un array list de companies
        ArrayList<Company> companies = new ArrayList<>();

        // recorre el path y va adhiriendo una nueva company a la company que es un array list
        for (Path path : paths) {
            File file = new File(path.toString());
            JAXBContext jaxbContext = JAXBContext.newInstance(Company.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Company company = (Company) jaxbUnmarshaller.unmarshal(file);
            companies.add(company);
        }

        // recorre el array de companies e intenta conectarse a postgres
        for (Company company : companies) {
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
