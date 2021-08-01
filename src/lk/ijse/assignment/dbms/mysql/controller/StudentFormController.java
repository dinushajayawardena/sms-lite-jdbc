package lk.ijse.assignment.dbms.mysql.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private ObservableList<String> test = FXCollections.observableArrayList();

    public void initialize(){

        txtId.setEditable(false);
        txtId.setFocusTraversable(false);
        txtId.setMouseTransparent(true);

        test.add("ABC");
        test.add("PQR");
        test.add("SYZ");
        test.add("123");
        test.add("456");

        Platform.runLater(()->{

            txtName.requestFocus();
            lstPhone.setItems(phones);
            tblStudents.setFixedCellSize(80);
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
        TableColumn<Student, VBox> phoneColumn = (TableColumn<Student, VBox>) tblStudents.getColumns().get(2);

        phoneColumn.setCellValueFactory(param -> {
            ListView studentContactList = new ListView();
            studentContactList.setPrefWidth(10);
            studentContactList.setPrefHeight(80);
            studentContactList.setItems(phones);


            return new ReadOnlyObjectWrapper(new VBox(2, studentContactList));
        });

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

        ViewAll();

        tblStudents.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                btnRemoveStudent.setDisable(false);
            }
        });

        tblStudents.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue){
               // btnRemoveStudent.setDisable(true);
                   // tblStudents.getSelectionModel().clearSelection();
            }
        });

    }

    private void ViewAll() {
        try {
            Statement stm = connection.createStatement();

            ResultSet rst = stm.executeQuery("SELECT * FROM students");

            while (rst.next()){
                int id =  rst.getInt("id");
                String name = rst.getString("name");
             /*   tblStudents.getItems().add(new Student(id, name));*/

                PreparedStatement pstm = connection.prepareStatement("SELECT * FROM contacts WHERE student_id = '" + id + "';");
                ResultSet rst2 = pstm.executeQuery();


                ArrayList<String> phoneNumbers = new ArrayList<>();
                while (rst2.next()){
                    String phone = rst2.getString("phone");
                    phoneNumbers.add(phone);
                }
                tblStudents.getItems().add(new Student(id, name, phoneNumbers));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
        Student selectedItem = tblStudents.getSelectionModel().getSelectedItem();
        int id = selectedItem.getId();
        try {
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM contacts WHERE student_id = '" + id + "';");
            int affectedRows = pstm.executeUpdate();

            if (affectedRows == 1){
                tblStudents.getItems().remove(selectedItem);
                tblStudents.refresh();
                btnClear.fire();
            }
            affectedRows = 0;

            pstm = connection.prepareStatement("DELETE FROM students WHERE id = '"+ id +"';");
            affectedRows = pstm.executeUpdate();

            if (affectedRows == 1){
                tblStudents.getItems().remove(selectedItem);
                tblStudents.refresh();
                btnClear.fire();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void btnAddStudent_OnAction(ActionEvent actionEvent) {
        String name = txtName.getText();
        int id = Integer.parseInt(txtId.getText());
        try {
            PreparedStatement pstm_students = connection.prepareStatement("INSERT INTO students (name) VALUES ('"+ name +"');");
            int affectedRows = pstm_students.executeUpdate();

            if (affectedRows == 1){
                tblStudents.getItems().add(new Student(id, name));
                btnClear.fire();
            }

            PreparedStatement pstm_contacts = null;


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void btnClear_OnAction(ActionEvent actionEvent) {
        txtName.clear();
        txtPhone.clear();
        phones.clear();
    }
}
