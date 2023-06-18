package com.iftm.client.resources;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonFormatter;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientResourcesTestIntegracao {

    @Autowired
    private MockMvc mockMvc;

    private static Long DELETE_ID;


    /**
     * Caso de testes : Verificar se o endpoint get/clients/ retorna todos os clientes existentes
     * - Uma PageRequest default
     */

    @Test
    @Order(1)
    @DisplayName("Verificar se o endpoint get/clients/ retorna todos os clientes existentes")
    public void testarEndPointRetornaTodosClientesExistentes() throws Exception {

        mockMvc.perform(get("/clients/")
            .accept(APPLICATION_JSON)
            .characterEncoding("utf-8"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().json(ClienteResourcesITJsonResponses.TEST_1_RESPONSE))
          .andReturn();
    }

    /**
     * Caso de teste: verificar se o endPoint clients/id retorna o cliente correto quando o id existe
     * Arrange:     *
     * - Uma PageRequest default
     * - idExistente : 4L
     * {
     * "id": 4,
     * "name": "Carolina Maria de Jesus",
     * "cpf": "10419244771",
     * "income": 7500.0,
     * "birthDate": "1996-12-23T07:00:00Z",
     * "children": 0
     * }
     */
    @Test
    @Order(2)
    @DisplayName("verificar se o endPoint clients/id retorna o cliente correto quando o id existe")
    public void testarEndPointBuscaPorIDRetornaClienteIdExistente() throws Exception {
        long idExistente = 4L;
        mockMvc.perform(get("/clients/{id}", idExistente)
            .accept(APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().json(ClienteResourcesITJsonResponses.TEST_2_RESPONSE))
          .andReturn();
    }

    @Test
    @Order(3)
    @DisplayName("verificar se o endPoint clients/ cria registro")
    public void testarCriacaoDeProduto() throws Exception {
        String jsonRequest = "{\n" +
          "\t\"name\" : \"Tony Stark\",\n" +
          "\t\"cpf\" : \"78654908721\",\n" +
          "\t\"income\" : 6000000.0,\n" +
          "\t\"birthDate\" : \"1970-05-29T07:00:00Z\",\n" +
          "\t\"children\" : 1\n" +
          "}";

        MvcResult result = mockMvc.perform(post("/clients/")
            .characterEncoding("utf-8")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .content(jsonRequest))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(content().json(ClienteResourcesITJsonResponses.TEST_3_RESPONSE))
          .andReturn();

        DELETE_ID = Long.parseLong(JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString());
    }
    @Test
    @Order(4)
    @DisplayName("verifica se o endipoint /clients delete registro")
    public void testeDeletaRegistro() throws Exception {

        mockMvc.perform(delete("/clients/{id}", DELETE_ID)
            .accept(APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isNoContent())
          .andReturn();

        mockMvc.perform(delete("/clients/{id}", DELETE_ID)
            .accept(APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isNotFound())
          .andReturn();
    }

    @Test
    @Order(5)
    @DisplayName("Verificar se o endpoint get/clients/findByIncome retorna os clientes ")
    public void testarFindByIncome() throws Exception {

        MvcResult result = mockMvc.perform(get("/clients/findByIncome/{income}", 1500)
            .accept(APPLICATION_JSON)
            .characterEncoding("utf-8"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().json(ClienteResourcesITJsonResponses.TEST_5_RESPONSE))
          .andReturn();

    }

    @Test
    @Order(6)
    @DisplayName("Verificar se o endpoint put/clients/ altera os dados")
    public void testaUpdate() throws Exception {

        String jsonRequest = "{\n" +
          "  \"name\": \"Carolina Maria de Jesus\",\n" +
          "  \"cpf\": \"10419244771\",\n" +
          "  \"income\": 5000000.0,\n" +
          "  \"birthDate\": \"1996-12-23T07:00:00Z\",\n" +
          "  \"children\": 10\n" +
          "}";

        MvcResult result = mockMvc.perform(put("/clients/{id}", 4)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .characterEncoding("utf-8")
            .content(jsonRequest))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().json(ClienteResourcesITJsonResponses.TEST_6_RESPONSE))
          .andReturn();


        result = mockMvc.perform(put("/clients/{id}", 400)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .characterEncoding("utf-8")
            .content(jsonRequest))
          .andDo(print())
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error").value("Resource not found"))
          .andReturn();

    }
}
