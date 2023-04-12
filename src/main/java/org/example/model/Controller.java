package org.example.model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Controller {
    private final static String url = "jdbc:sqlserver://localhost:1433;databaseName=Students;encrypt=true;trustServerCertificate=true ";
    private static final String usr = "sa";
    private static final String pss = "Abcd1234";
    private static final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static List<Student> students = new ArrayList<Student>();

    // connect to database

    public static void ServerWrite(String FILE_PATH) {
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, usr, pss);
            String insertSql = "INSERT INTO students (firstname, lastname, age, gender, major) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(insertSql);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(FILE_PATH);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("student");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String firstName = element.getElementsByTagName("firstname").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastname").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    String gender = element.getElementsByTagName("gender").item(0).getTextContent();
                    String major = element.getElementsByTagName("major").item(0).getTextContent();
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setInt(3, age);
                    statement.setString(4, gender);
                    statement.setString(5, major);
                    statement.executeUpdate();
                }
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readXML(String filename) {
        try {
            // Create a new document builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Create a new document builder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file
            Document document = builder.parse(new File(filename));

            // Get the root element
            Element root = document.getDocumentElement();

            // Get a list of all student elements
            NodeList studentList = root.getElementsByTagName("student");

            // Loop through each student element
            for (int i = 0; i < studentList.getLength(); i++) {
                Node studentNode = studentList.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    // Get the student attributes and create a new Student object
                    int id = Integer.parseInt(studentElement.getAttribute("id"));
                    String firstName = studentElement.getElementsByTagName("firstname").item(0).getTextContent();
                    String lastName = studentElement.getElementsByTagName("lastname").item(0).getTextContent();
                    int age = Integer.parseInt(studentElement.getElementsByTagName("age").item(0).getTextContent());
                    String gender = studentElement.getElementsByTagName("gender").item(0).getTextContent();
                    String major = studentElement.getElementsByTagName("major").item(0).getTextContent();

                    Student student = new Student(id, firstName, lastName, age, gender, major);

                    // Add the student to the list
                    students.add(student);
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readStudentXML(String FILE_PATH) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                boolean isFirstName = false;
                boolean isLastName = false;
                boolean isAge = false;
                boolean isGender = false;
                boolean isMajor = false;
                int id;
                String firstName;
                String lastName;
                int age;
                String gender;
                String major;

                public void startElement(String uri, String localName, String qName, Attributes attributes)
                        throws SAXException {
                    if (qName.equalsIgnoreCase("student")) {
                        id = Integer.parseInt(attributes.getValue("id"));
                    } else if (qName.equalsIgnoreCase("firstname")) {
                        isFirstName = true;
                    } else if (qName.equalsIgnoreCase("lastname")) {
                        isLastName = true;
                    } else if (qName.equalsIgnoreCase("age")) {
                        isAge = true;
                    } else if (qName.equalsIgnoreCase("gender")) {
                        isGender = true;
                    } else if (qName.equalsIgnoreCase("major")) {
                        isMajor = true;
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    if (isFirstName) {
                        firstName = new String(ch, start, length);
                        isFirstName = false;
                    } else if (isLastName) {
                        lastName = new String(ch, start, length);
                        isLastName = false;
                    } else if (isAge) {
                        age = Integer.parseInt(new String(ch, start, length));
                        isAge = false;
                    } else if (isGender) {
                        gender = new String(ch, start, length);
                        isGender = false;
                    } else if (isMajor) {
                        major = new String(ch, start, length);
                        isMajor = false;
                    }
                }

                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.equalsIgnoreCase("student")) {
                        Student student = new Student(id, firstName, lastName, age, gender, major);
                        students.add(student);
                    }
                }
            };
            saxParser.parse(FILE_PATH, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeXML(String filename) {
        try {
            // Create a new document builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Create a new document builder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Create a new document
            Document document = builder.newDocument();

            // Create the root element
            Element root = document.createElement("students");
            document.appendChild(root);

            // Loop through each student in the list
            for (Student student : students) {
                // Create a new student element
                Element studentElement = document.createElement("student");
                root.appendChild(studentElement);

                // Set the student id attribute
                studentElement.setAttribute("id", Integer.toString(student.getId()));

                // Create the firstname, lastname, age, gender, and major elements
                Element firstNameElement = document.createElement("firstname");
                firstNameElement.appendChild(document.createTextNode(student.getFirstName()));
                studentElement.appendChild(firstNameElement);

                Element lastNameElement = document.createElement("lastname");
                lastNameElement.appendChild(document.createTextNode(student.getLastName()));
                studentElement.appendChild(lastNameElement);
                Element ageElement = document.createElement("age");
                ageElement.appendChild(document.createTextNode(Integer.toString(student.getAge())));
                studentElement.appendChild(ageElement);

                Element genderElement = document.createElement("gender");
                genderElement.appendChild(document.createTextNode(student.getGender()));
                studentElement.appendChild(genderElement);

                Element majorElement = document.createElement("major");
                majorElement.appendChild(document.createTextNode(student.getMajor()));
                studentElement.appendChild(majorElement);
            }

            // Write the document to the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    // Getters and setters

    public List<Student> getStudents() {
        return students;
    }

}
