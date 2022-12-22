package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;


import java.time.Duration;

import static com.learnwebclient.constants.EmployeeConstants.EMPLOYEE_BY_ID;
import static com.learnwebclient.constants.EmployeeConstants.ERROR_EMPLOYEE_V1;

@Slf4j
public class EmployeeRestClientWithRetryMechanism {

    private WebClient webClient;

    /*
    This process is deprecated now
     */
    Retry<?> fixedRetry = Retry.anyOf(WebClientResponseException.class)
            .fixedBackoff(Duration.ofSeconds(2))
            .retryMax(3)
            .doOnRetry((exception ->{
                 log.error("Exception is {} " , exception);
    }));

    Retry<?> fixedRetry5xx = Retry.anyOf(EmployeeServiceException.class)
            .fixedBackoff(Duration.ofSeconds(2))
            .retryMax(3)
            .doOnRetry((exception ->{
                log.error("Exception is {} " , exception);
            }));

    public EmployeeRestClientWithRetryMechanism(WebClient webClient) {
        this.webClient = webClient;
    }

//    public Employee retriveEmployeeByIDWithRetryMechanism(int employeeID){
//        try {
//            return webClient.get()
//                    .uri(EMPLOYEE_BY_ID, employeeID)
//                    .retrieve()
//                    .bodyToMono(Employee.class)
//                    .retryWhen(fixedRetry)
//                    .block();
//        }
//        catch (WebClientResponseException ex){
//            log.error("Error Response code is {} and response body is {}" , ex.getRawStatusCode(),
//                    ex.getResponseBodyAsString());
//            log.error("WebClientResponseException is retriveEmployeeByID " , ex);
//            throw ex;
//        }
//        catch (Exception ex){
//            log.error("Exception is retriveEmployeeByID " , ex);
//            throw ex;
//        }
//    }

//    public String errorEndPoint(){
//        //http://localhost:8081/employeeservice/v1/employee/error
//        return webClient.
//                get()
//                .uri(ERROR_EMPLOYEE_V1)
//                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxError(clientResponse))
//                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxError(clientResponse))
//                .bodyToMono(String.class)
//                .retryWhen(fixedRetry5xx)
//                .block();
//    }

    private Mono<? extends Throwable> handle5xxError(ClientResponse clientResponse) {
        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
        return errorMessage.flatMap((message) -> {
            log.error("Error Response code is "+ clientResponse.rawStatusCode()
                    + " and the message is  " + message);
            throw new EmployeeServiceException(message);
        });
    }

    private Mono<? extends Throwable> handle4xxError(ClientResponse clientResponse) {
        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
        return errorMessage.flatMap((message) -> {
            log.error("Error Response code is "+ clientResponse.rawStatusCode()
                    + " and the message is  " + message);
            throw new ClientDataException(message);
        });
    }
}
