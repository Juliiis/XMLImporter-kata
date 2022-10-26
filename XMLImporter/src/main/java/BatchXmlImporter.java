import jakarta.xml.bind.JAXBException;
import java.sql.SQLException;
import xmlmodels.Company;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BatchXmlImporter {
    public void importFiles(Path folderPath) throws IOException, JAXBException, SQLException {

        final String fileExtension = ".xml";
        List<Path> paths;

        paths = ImporterFile.getPathList(folderPath, fileExtension);

        ArrayList<Company> companies = new ArrayList<>();

        ImporterFile.addFileIntoACompany(paths, companies);

        for (Company company : companies) {
            CompanyDataBase.connectWithPostgresDataBase(company);
        }
    }
}
