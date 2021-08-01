package lk.ijse.assignment.dbms.mysql.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Student implements Serializable {
    private int id;
    private String name;
    private ArrayList phones;

    public Student() {
    }

    public Student(int id, String name) {
        this.id = id;
        this.name = name;

    }

    public Student(int id, String name, ArrayList phones) {
        this.id = id;
        this.name = name;
        this.phones = phones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public ArrayList getPhones() {
        return phones;
    }

    public void setPhones(ArrayList phones) {
        this.phones = phones;
    }
}
