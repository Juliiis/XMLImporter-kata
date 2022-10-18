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

        // DATA BASE
        // recorre el array de companies e intenta conectarse a postgres
        for (Company company : companies) {
            BatchXmlImporterDataBase.connectionWithPostgresDataBase(company);
        }
    }



}
