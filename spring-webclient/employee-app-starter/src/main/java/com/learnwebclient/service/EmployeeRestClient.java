package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.learnwebclient.constants.EmployeeConstants.*;

@Slf4j
public class EmployeeRestClient {

    private WebClient webClient;

    public EmployeeRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    //http://localhost:8081/employeeservice/v1/allEmployees
      public List<Employee> retriveAllEmployees(){
            return webClient.get()
                    .uri(GET_ALL_EMP_URI)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
      }

      public Employee retriveEmployeeByID(int employeeID){
        try {
            return webClient.get()
                    .uri(EMPLOYEE_BY_ID, employeeID)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        }
        catch (WebClientResponseException ex){
            log.error("Error Response code is {} and response body is {}" , ex.getRawStatusCode(),
                ex.getResponseBodyAsString());
            log.error("WebClientResponseException is retriveEmployeeByID " , ex);
            throw ex;
        }
        catch (Exception ex){
            log.error("Exception is retriveEmployeeByID " , ex);
            throw ex;
        }
      }

    public List<Employee> retriveEmployeeByName(String firstName){

        String uri = UriComponentsBuilder
                  .fromUriString(EMPLOYEE_BY_NAME)
                  .queryParam("employee_name",firstName)
                  .build()
                  .toUriString();

        try{
            return webClient.get().uri(uri)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
        }
        catch (WebClientResponseException ex){
            log.error("Error Response code is {} and response body is {}" , ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
            log.error("WebClientResponseException is retriveEmployeeByID " , ex);
            throw ex;
        }
        catch (Exception ex) {
            log.error("Exception is retriveEmployeeByID ", ex);
            throw ex;
        }
      }

      public Employee addNewEmployee(Employee employee){
          try {
             return webClient.post()
                    .uri(ADD_NEW_EMP)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
          }catch (WebClientResponseException ex){
              log.error("Error Response code is {} and response body is {}" , ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
              log.error("WebClientResponseException is retriveEmployeeByID " , ex);
              throw ex;
        } catch (Exception ex) {
              log.error("Exception is retriveEmployeeByID ", ex);
              throw ex;
        }
      }

    public Employee updateEmployee(int id,Employee employee){
        try {

            return webClient.put()
                    .uri(EMPLOYEE_BY_ID , id)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();

        }
        catch (WebClientResponseException ex){

            log.error("Error Response code is {} and response body is {}" , ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
            log.error("WebClientResponseException is retriveEmployeeByID " , ex);
            throw ex;

        } catch (Exception ex) {

            log.error("Exception is retriveEmployeeByID ", ex);
            throw ex;

        }
    }

    //Custom Error handling
    public Employee retriveEmployeeByID_custom_error_handling(int employeeID){

        return webClient.get()
                .uri(EMPLOYEE_BY_ID, employeeID)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxError(clientResponse))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxError(clientResponse))
                .bodyToMono(Employee.class)
                .block();
    }

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

    public String errorEndPoint(){
        //http://localhost:8081/employeeservice/v1/employee/error
        return webClient.
                get()
                .uri(ERROR_EMPLOYEE_V1)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxError(clientResponse))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxError(clientResponse))
                .bodyToMono(String.class)
                .block();
    }
}
