package org.bahmni_avni_integration.migrator.repository;

import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class OpenMRSRepository {
    @Autowired
    private ConnectionFactory connectionFactory;

    public void populateForms(List<OpenMRSForm> formList) throws SQLException {
        try (Connection connection = connectionFactory.getMySqlConnection()) {
            PreparedStatement formUuidPS = connection.prepareStatement("select uuid from concept where concept_id = ?");
            PreparedStatement formConceptPS = connection.prepareStatement(FileUtil.readFile("form-elements.sql"));
            for (OpenMRSForm form : formList) {
                formConceptPS.setInt(1, form.getFormId());
                formConceptPS.setInt(2, form.getFormId());
                formConceptPS.setInt(3, form.getFormId());
                formConceptPS.setInt(4, form.getFormId());

                ResultSet resultSet = formConceptPS.executeQuery();
                while (resultSet.next()) {
                    form.addConcept(resultSet.getString(1), resultSet.getString(2));
                }

                resultSet = formUuidPS.executeQuery();
                resultSet.next();
                form.setUuid(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}