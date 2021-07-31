package lk.ijse.assignment.dbms.mysql.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.assignment.dbms.mysql.model.Student;

import java.sql.*;
import java.util.ArrayList;

public class StudentFormController {
    public TextField txtId;
    public TextField txtName;
    public TextField txtPhone;
    public ListView lstPhone;
    public Button btnAdd;
    public Button btnRemove;
    public TableView<Student> tblStudents;
    public Button btnRemoveStudent;
    public Button btnAddStudent;
    public Button btnClear;
    private Connection connection;
    private ObservableList<String> phones = FXCollections.observableArrayList();

    public void initialize(){
        txtId.setEditable(false);
        txtId.setFocusTraversable(false);
        txtId.setMouseTransparent(true);

        Platform.runLater(()->{

            txtName.requestFocus();
            lstPhone.setItems(phones);
        });

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/smslite", "root", "root");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to connect to the database server");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }));

        tblStudents.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblStudents.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblStudents.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("phone"));

        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT id FROM students ORDER BY id DESC LIMIT 1");

            int dbId = 0;
            while (rst.next()){
                dbId = rst.getInt("id");

            }
            txtId.setText(String.valueOf(dbId+1));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            String name = txtName.getText();
            String phone = txtPhone.getText();

            btnAddStudent.setDisable(!(name.matches("[A-Za-z ]{3,}")));
            btnAdd.setDisable(!(phone.matches("\\d{3}-\\d{7}")));
        };

        lstPhone.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                btnRemove.setDisable(false);
            }else {
                btnRemove.setDisable(true);
            }
        });

        txtName.textProperty().addListener(listener);
        txtPhone.textProperty().addListener(listener);

        btnAddStudent.setDefaultButton(true);
        btnAddStudent.setDisable(true);
        btnRemoveStudent.setDisable(true);
        btnAdd.setDisable(true);
        btnRemove.setDisable(true);

    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        phones.add(txtPhone.getText());
        lstPhone.refresh();
        txtPhone.clear();
    }

    public void btnRemove_OnAction(ActionEvent actionEvent) {
       phones.remove(lstPhone.getSelectionModel().getSelectedItem());
        lstPhone.getSelectionModel().clearSelection();
    }

    public void btnRemoveStudent_OnAction(ActionEvent actionEvent) {
    }

    public void btnAddStudent_OnAction(ActionEvent actionEvent) {

    }

    public void btnClear_OnAction(ActionEvent actionEvent) {
        txtName.clear();
        txtPhone.clear();
        phones.clear();
    }
}
