package com.ainigma100.departmentapi.controller;

import com.ainigma100.departmentapi.enums.Status;
import com.ainigma100.departmentapi.filter.RateLimitingFilter;
import com.ainigma100.departmentapi.service.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import static org.mockito.Mockito.doThrow;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * @WebMvcTest annotation will load all the components required
 * to test the Controller layer. It will not load the service or repository layer components
 */
@WebMvcTest(EmailController.class)
@Tag("unit")
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private RateLimitingFilter rateLimitingFilter;


    @AfterEach
    void resetRateLimitBuckets() {
        rateLimitingFilter.clearBuckets();
    }

    @Test
    void givenNoInput_whenSendEmailWithoutAttachment_thenReturnTrueIfMailWasSent() throws Exception {

        // given - precondition or setup
        given(emailService.sendEmailWithoutAttachment()).willReturn(true);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/v1/emails"));

        // then - verify the output
        response.andDo(print())
                // verify the status code that is returned
                .andExpect(status().isOk())
                // verify the actual returned value and the expected value
                // $ - root member of a JSON structure whether it is an object or array
                .andExpect(jsonPath("$.status", is(Status.SUCCESS.getValue())));

    }


    @Test
    void givenNoInput_whenSendEmailWithAttachment_thenReturnTrueIfMailWasSent() throws Exception {

        // given - precondition or setup
        given(emailService.sendEmailWithAttachment()).willReturn(true);

        // when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform(get("/api/v1/emails/with-attachment"));

        // then - verify the output
        response.andDo(print())
                // verify the status code that is returned
                .andExpect(status().isOk())
                // verify the actual returned value and the expected value
                // $ - root member of a JSON structure whether it is an object or array
                .andExpect(jsonPath("$.status", is(Status.SUCCESS.getValue())));

    }

    /*
     * LLM-Generated Test Cases Below
     */

    @Test
    void givenEmailServiceFails_whenSendEmailWithoutAttachment_thenReturnInternalServerError() throws Exception {
        doThrow(new RuntimeException("Email service failed")).when(emailService).sendEmailWithoutAttachment();
    
        // Perform request & store response
        MvcResult result = mockMvc.perform(get("/api/v1/emails"))
               .andDo(print()) // Debugging output
               .andExpect(status().isInternalServerError()) // Status code check
               .andReturn();
    
        // Print actual response for debugging
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Actual Response: " + jsonResponse);
    
        // JSON assertions directly on the result
        mockMvc.perform(get("/api/v1/emails"))
               .andExpect(jsonPath("$.status").exists())  // Ensures "status" exists
               .andExpect(jsonPath("$.errors").isArray())  // Ensures "errors" is an array
               .andExpect(jsonPath("$.errors[0].errorMessage").exists()); // Check for error message
    }
    
    @Test
    void givenRateLimitExceeded_whenSendEmailWithoutAttachment_thenReturnTooManyRequests() throws Exception {
        // Simulate exceeding rate limit
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/api/v1/emails"));
        }
    
        // Perform request & verify response
        MvcResult result = mockMvc.perform(get("/api/v1/emails"))
               .andDo(print())
               .andExpect(status().isTooManyRequests()) //Ensure 429 Too Many Requests
               .andReturn();
    
        // Extract actual response
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Actual Response: " + jsonResponse);
    
        assertEquals("Rate limit exceeded. Please try again later.", jsonResponse);
    }


    @Test
    @DisplayName("Email service failure when sending email with attachment should return Internal Server Error")
    void givenEmailServiceFails_whenSendEmailWithAttachment_thenReturnInternalServerError() throws Exception {
        doThrow(new RuntimeException("Email service failed")).when(emailService).sendEmailWithAttachment();
    
        ResultActions response = mockMvc.perform(get("/api/v1/emails/with-attachment"));
    
        response.andDo(print())
                .andExpect(status().isInternalServerError());
    }
    

}
