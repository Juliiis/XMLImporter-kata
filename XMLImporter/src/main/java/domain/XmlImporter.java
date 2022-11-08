package domain;

import infrastructure.CompanyDataBase;
import infrastructure.FileActions;
import jakarta.xml.bind.JAXBException;
import java.sql.SQLException;
import xmlmodels.Company;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class XmlImporter {
    FileActions fileActions = new FileActions();
    CompanyDataBase companyDataBase = new CompanyDataBase();
    public void importFiles(Path folderPath) throws IOException, JAXBException, SQLException {

        final String fileExtension = ".xml";
        List<Path> paths;

        paths = fileActions.getPathList(folderPath, fileExtension);

        ArrayList<Company> companies = new ArrayList<>();

        fileActions.insertCompanyInPostgresDataBase(paths, companies);

        for (Company company : companies) companyDataBase.connectWithPostgresDataBase(company);
    }
}
