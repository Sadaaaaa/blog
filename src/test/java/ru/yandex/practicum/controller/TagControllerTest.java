package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.service.TagService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(TagController.class)
class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getAllTags_ShouldReturnTagsList() throws Exception {
        List<String> tags = Arrays.asList("Java", "Spring", "JUnit");
        when(tagService.getAllTags()).thenReturn(tags);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tags"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value("Spring"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2]").value("JUnit"));
    }
}

