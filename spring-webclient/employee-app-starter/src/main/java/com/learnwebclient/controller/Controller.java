package com.learnwebclient.controller;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.service.EmployeeRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class Controller {

    @Autowired
    EmployeeRestClient employeeRestClient;

    @GetMapping("/employees")
    public List<Employee> employeeList(){
        return employeeRestClient.retriveAllEmployees();
    }

    @GetMapping("/{empid}")
    public Employee getEmployeeById(@PathVariable int empid){
         return employeeRestClient.retriveEmployeeByID(empid);
    }

    @GetMapping("/{empName}")
    public List<Employee> getEmployeeByName(@PathVariable String empName){
        return employeeRestClient.retriveEmployeeByName(empName);
    }

}
