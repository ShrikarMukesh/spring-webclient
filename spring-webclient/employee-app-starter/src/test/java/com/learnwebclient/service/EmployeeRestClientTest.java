package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeRestClientTest {

    private static final String baseURL = "http://localhost:8081/employeeservice";
    private WebClient webClient =  WebClient.create(baseURL);

    EmployeeRestClient employeeRestClient = new EmployeeRestClient(webClient);

    @Test
    void retriveAllEmployees(){

        List<Employee> employeeList =
                employeeRestClient.retriveAllEmployees();

        System.out.println(employeeList);
        assertTrue(employeeList.size() > 0);

    }

    @Test
    void retriveEmployeeById(){

        int empid = 1;
        Employee employee =
                employeeRestClient.retriveEmployeeByID(empid);

        System.out.println(employee);
        assertEquals("Chris",employee.getFirstName());
    }

    @Test
    void retriveEmployeeById_Not_Found(){

        int empid = 111;

        Assertions
                .assertThrows(WebClientResponseException.class,
                        ()-> employeeRestClient.retriveEmployeeByID(empid));
    }

    /*
    Exception Handling using spring WebFlux
     */
    @Test
    void retriveEmployeeById_custom_error_Handling(){

        int empid = 111;

        Assertions
                .assertThrows(ClientDataException.class,
                        ()-> employeeRestClient.retriveEmployeeByID_custom_error_handling(empid));
    }

    @Test
    void retriveEmployeeByName(){

        int empid = 1;
        List<Employee> employeeList =
                employeeRestClient.retriveEmployeeByName("Chris");

        System.out.println(employeeList);
        assertTrue(employeeList.size() > 0);
    }

    @Test
    void retriveEmployeeByName_Not_Found(){

        String name = "ABC";
        Assertions
                .assertThrows(WebClientResponseException.class,
                        ()-> employeeRestClient.retriveEmployeeByName(name));
    }

    @Test
    void addNewEmployee(){

        Employee employee =
                new Employee(null,54,"Iron","MAN","male", "Arcitect");

        Employee employee1 = employeeRestClient.addNewEmployee(employee);

        System.out.println("employee1: " + employee1);
        assertTrue(employee1.getId() != null);
    }

    @Test
    void addNewEmployee_BadRequest(){

        Employee employee =
                new Employee(null,54,null,"MAN","male", "Arcitect");
        Assertions.
                assertThrows(WebClientResponseException.class
                ,()->employeeRestClient.addNewEmployee(employee));
    }

    @Test
    void updateEmployee(){

        Employee employee =
                new Employee(null,54,"Adam2",
                        "LastName2","male", "Java Developer");

        Employee updatedEmployee = employeeRestClient.updateEmployee(2,employee);
        System.out.println(updatedEmployee);
        assertEquals("Adam2", updatedEmployee.getFirstName());

    }

    @Test
    void updateEmployee_NotFound(){

        Employee employee =
                new Employee(null,54,"Adam2",
                        "LastName2","male", "Java Developer");

        Assertions.
                assertThrows(WebClientResponseException.class
                        ,()->employeeRestClient.updateEmployee(200,employee));

    }

    @Test
    void errorEndpoint(){
        Assertions.assertThrows(
                EmployeeServiceException.class
                ,()->employeeRestClient.errorEndPoint()
        );
    }
}

