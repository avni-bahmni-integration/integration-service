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

    public void addConceptsToForms(List<OpenMRSForm> formList) throws SQLException {
        try (Connection mySQLConnection = connectionFactory.getMySqlConnection()) {
            PreparedStatement preparedStatement = mySQLConnection.prepareStatement(FileUtil.readFile("form-elements.sql"));
            for (OpenMRSForm form : formList) {
                preparedStatement.setInt(1, form.getFormId());
                preparedStatement.setInt(2, form.getFormId());
                preparedStatement.setInt(3, form.getFormId());
                preparedStatement.setInt(4, form.getFormId());

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    form.addConcept(resultSet.getString(1), resultSet.getString(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}