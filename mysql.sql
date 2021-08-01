USE smslite;

CREATE TABLE students(
     id INT PRIMARY KEY AUTO_INCREMENT,
     name VARCHAR(15) NOT NULL
);

CREATE TABLE contacts(
    phone VARCHAR(15),
    student_id INT NOT NULL,
    CONSTRAINT PRIMARY KEY (phone, student_id),
    CONSTRAINT fk_contact FOREIGN KEY (student_id) REFERENCES students(id)
);

INSERT INTO students (name) VALUES ('Amal');