package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.service.TagService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class TagControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
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

