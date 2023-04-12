package org.example;

import org.example.model.*;

public class Main {
    public static void main(String[] args) {
        Controller.readXML("src/main/java/org/example/model/xml/Students.xml");
        // for (Student student : Controller.students) {
        // System.out.println(student);
        // }
        // Student stu = new Student(5, "A", "B", 20, "Female", "Business");
        // Controller.students.add(stu);
        // Controller.writeXML("src/main/java/org/example/model/xml/Students.xml");
        Controller.ServerWrite("src/main/java/org/example/model/xml/Students.xml");
        System.out.println("Hello world!");
    }
}