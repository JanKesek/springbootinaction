package springbootinaction.taco.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import taco.springbootinaction.rest.controller.TacoController;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TacoController.class)
public class TacoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRecentTacos() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/taco?recent"))
            .andExpect(status().isOk());
    }
    @Test
    void testAllTacos() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/taco"))
            .andExpect(status().isOk());
    }

    @Test
    void testSendToQueue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/taco?id=1"))
            .andExpect(status().isOk());
    }

}
