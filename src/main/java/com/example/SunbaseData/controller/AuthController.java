package com.example.SunbaseData.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;




import com.example.SunbaseData.model.AuthRequest;

@Controller
public class AuthController {
    private final String AUTH_ENDPOINT = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
@GetMapping("/login")
public String hello(){
    return"login";
}

    @PostMapping("/login")
    public String login( HttpSession session,
            @RequestParam("login_id") String login_id,
                        @RequestParam("password") String password,
                        Model model

    ){
        AuthRequest authRequest= new AuthRequest(login_id,password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthRequest> requestEntity = new HttpEntity<>(authRequest, headers);
        try {

            ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(AUTH_ENDPOINT, requestEntity, String.class);


            String responseBody = responseEntity.getBody();
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseBody != null) {

                String newresponseBody=extractAccessToken(responseBody);

                session.setAttribute("accessToken",newresponseBody);


                return "redirect:/customerList";
            } else {
                model.addAttribute("error", true);
                return "login";
            }
        }
        catch(Exception e){
            model.addAttribute("error", true);
            return "login";
        }


    }
    @GetMapping("/testList")
    public String testSession(HttpSession session){
        return (String)session.getAttribute("accessToken");
    }
    private String extractAccessToken(String responseBody) {

        return responseBody.split(":")[1].replace("\"", "").replace("}", "").trim();
    }

}
