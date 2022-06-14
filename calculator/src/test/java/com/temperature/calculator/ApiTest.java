package com.temperature.calculator;


import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ApiTest {

    @Autowired
    private MockMvc mvc;


    @Test
    public void getCorrectCalculation() throws Exception {
        mvc.perform(get("/api/calc/kelvin/{degrees}", 27).with(httpBasic("admin","password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fahrenheit", Is.is(-411.07)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kelvin",Is.is(27.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.celsius",Is.is(-246.15)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.calculationUnit",Is.is("KELVIN")))
                .andDo(print());
    }

    @Test
    public void getErrorCalculation() throws Exception{
        mvc.perform(get("/api/calc/kelvin/{degrees}", "degrees").with(httpBasic("user","password")))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]", Is.is("degrees should be of type double"))).andDo(print());
    }

    @Test
    public void checkAuthCorrectPassword() throws Exception{
        mvc.perform(get("/api/calc/history").with(httpBasic("user","password")))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void checkAuthWrongPassword() throws Exception{
        mvc.perform(get("/api/calc/history").with(httpBasic("user","password1")))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    public void checkHistoryLocal() throws Exception{
        mvc.perform(get("/api/calc/kelvin/{degrees}", 27).with(httpBasic("admin","password"))).andExpect(status().isOk());
        mvc.perform(get("/api/calc/fahrenheit/{degrees}", 27).with(httpBasic("user","password"))).andExpect(status().isOk());
        mvc.perform(get("/api/calc/history").with(httpBasic("admin","password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].fahrenheit", Is.is(-411.07)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].kelvin",Is.is(27.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].celsius",Is.is(-246.15)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].calculationUnit",Is.is("KELVIN")));
        mvc.perform(get("/api/calc/history").with(httpBasic("user","password")))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].fahrenheit", Is.is(27.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].kelvin",Is.is(270.37)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].celsius",Is.is(-2.78)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].calculationUnit",Is.is("FAHRENHEIT")));

    }


}
