package com.learnwebclient.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.retry.RetryExhaustedException;

public class EmployeeRestClientTestWthRetry {

    private static final String baseURL = "http://localhost:8081/employeeservice";
    private WebClient webClient =  WebClient.create(baseURL);
    EmployeeRestClientWithRetryMechanism employeeRestClientWithRetryMechanism = new EmployeeRestClientWithRetryMechanism(webClient);
    @Test
    void retriveEmployeeById_withRetry(){
        int empid = 100;
        Assertions.assertThrows(WebClientResponseException.class,
                ()-> employeeRestClientWithRetryMechanism.retriveEmployeeByIDWithRetryMechanism(empid)
                );
    }

    @Test
    void errorEndpoint(){
        Assertions.assertThrows(
                RetryExhaustedException.class
                ,()->employeeRestClientWithRetryMechanism.errorEndPoint()
        );
    }
}

