package com.example.SunbaseData.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Controller
public class CustomerController {

    private final String BASE_URL = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp";
    private final RestTemplate restTemplate;

    public CustomerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private boolean auth(HttpSession session){
        if(session.getAttribute("accessToken")=="" || session.getAttribute("accessToken")==null){
            return false;
        }
        return true;
    }
    @GetMapping("/customerList")
    public String customerList(HttpSession session, Model model) {
        if(!auth(session)){
            return "redirect:/login";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + session.getAttribute("accessToken"));


        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try{
            ResponseEntity<String> apiResponse = restTemplate.exchange(
                    BASE_URL + "?cmd=get_customer_list",
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );



            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                List<Map<String, String>> customerList = parseCustomerList(apiResponse.getBody());
                model.addAttribute("customerList", customerList);
                Iterator it= customerList.listIterator();

            } else {

                return "redirect/login";
            }

            return "customerList";
        }
        catch (Exception e){
            return "redirect:/login";
        }

    }
    private List<Map<String, String>> parseCustomerList(String apiResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(apiResponse, new TypeReference<List<Map<String, String>>>() {});
        } catch (JsonProcessingException e) {


            return Collections.emptyList();
        }
    }

    @GetMapping("/createCustomer")
    public String createCustomer(HttpSession session){
        if(!auth(session)){
            return "redirect:/login";
        }
        return "customerDetails";
    }
    @PostMapping("/createCustomer")
    public String createCustomer(HttpSession session,
            @RequestParam("first_name") String firstName,
            @RequestParam("last_name") String lastName,
            @RequestParam("street") String street,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            Model model,
            HttpServletResponse response
    ) {
        if(!auth(session)){
            return "redirect:/login";
        }

        String bearerToken = (String) session.getAttribute("accessToken");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+session.getAttribute("accessToken"));
        headers.set("Content-Type", "application/json");


        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("first_name", firstName);
        requestBody.put("last_name", lastName);
        requestBody.put("street", street);
        requestBody.put("address", address);
        requestBody.put("city", city);
        requestBody.put("state", state);
        requestBody.put("email", email);
        requestBody.put("phone", phone);
        try{


            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> apiResponse = restTemplate.exchange(
                    BASE_URL + "?cmd=create",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Handle the API response
            if (apiResponse.getStatusCode().is2xxSuccessful()) {

                model.addAttribute("message", "Customer created successfully");
            } else {
                return "redirect:/customerList";
            }

            // Redirect to the customer list page
        }
        catch (Exception e){
            return "redirect:/customerList";
        }

        return "redirect:/customerList";
    }

    @PostMapping("/deleteCustomer")
    public String deleteCustomer(HttpSession session,
                                 @RequestParam String uuid,
                                 Model model
    ) {
        if(!auth(session)){
            return "redirect:/login";
        }

        String bearerToken = (String) session.getAttribute("accessToken");


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + session.getAttribute("accessToken"));
        headers.set("Content-Type", "application/json");


        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("uuid", uuid);
        try{


            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> apiResponse = restTemplate.exchange(
                    BASE_URL + "?cmd=delete&uuid="+uuid,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );


            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                // Successful response
                model.addAttribute("message", "Customer deleted successfully");
            } else {

                model.addAttribute("error", "Failed to delete customer");
            }
        }
        catch (Exception e){
            return "redirect:/customerList";
        }

        // Redirect to the customer list page
        return "redirect:/customerList";
    }



    @GetMapping("/updateCustomer/{uuid}")
    public String updateCustomer(@PathVariable("uuid") String uuid,HttpSession session, Model model) {
        if(!auth(session)){
            return "redirect:/login";
        }

        Map<String, String> customerDetails = getCustomerDetailsByUUID(uuid);

        // Add customer details
        model.addAttribute("customerDetails", customerDetails);

        return "updateCustomer";
    }
    private Map<String, String> getCustomerDetailsByUUID(String uuid) {

        Map<String, String> dummyCustomerDetails = new HashMap<>();
        dummyCustomerDetails.put("uuid", uuid);
        dummyCustomerDetails.put("first_name", "");
        dummyCustomerDetails.put("last_name", "");

        return dummyCustomerDetails;
    }
    @PostMapping("/updateCustomer")
    public String updateCustomer(HttpSession session,
                                 @RequestParam("uuid") String uuid,
                                 @RequestParam("first_name") String firstName,
                                 @RequestParam("last_name") String lastName,
                                 @RequestParam("street") String street,
                                 @RequestParam("address") String address,
                                 @RequestParam("city") String city,
                                 @RequestParam("state") String state,
                                 @RequestParam("email") String email,
                                 @RequestParam("phone") String phone,
                                 Model model
    ) {
        if(!auth(session)){
            return "redirect:/login";
        }

        String bearerToken = (String) session.getAttribute("accessToken");

        // Prepare the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + session.getAttribute("accessToken"));
        headers.set("Content-Type", "application/json");

        // Prepare the request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("uuid", uuid);
        requestBody.put("first_name", firstName);
        requestBody.put("last_name", lastName);
        requestBody.put("street", street);
        requestBody.put("address", address);
        requestBody.put("city", city);
        requestBody.put("state", state);
        requestBody.put("email", email);
        requestBody.put("phone", phone);
        try{
            // Make the API request
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> apiResponse = restTemplate.exchange(
                    BASE_URL + "?cmd=update&uuid="+uuid,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Handle the API response
            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                // Successful response
                model.addAttribute("message", "Customer updated successfully");
            } else {
                // request failed
                model.addAttribute("error", "Failed to update customer");
            }
        }
        catch (Exception e){
            return "redirect:/customerList";
        }


        // Redirect to the customer list page
        return "redirect:/customerList";
    }

}


