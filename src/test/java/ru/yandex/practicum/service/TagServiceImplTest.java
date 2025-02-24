package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.repository.TagRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void getAllTags_WhenTagsExist_ShouldReturnUniqueTagsList() {
        List<String> rawTags = Arrays.asList(
                "java,spring,hibernate",
                "spring,junit,java",
                "docker,java"
        );
        when(tagRepository.findAllTags()).thenReturn(rawTags);

        List<String> result = tagService.getAllTags();

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.containsAll(Arrays.asList("java", "spring", "hibernate", "junit", "docker")));
        verify(tagRepository).findAllTags();
    }

    @Test
    void getAllTags_WhenEmptyTags_ShouldReturnEmptyList() {
        List<String> emptyTags = Arrays.asList("");
        when(tagRepository.findAllTags()).thenReturn(emptyTags);

        List<String> result = tagService.getAllTags();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagRepository).findAllTags();
    }

    @Test
    void getAllTags_WhenNullTags_ShouldReturnEmptyList() {
        List<String> tagsWithNull = Arrays.asList(null, "java,spring", null);
        when(tagRepository.findAllTags()).thenReturn(tagsWithNull);

        List<String> result = tagService.getAllTags();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList("java", "spring")));
        verify(tagRepository).findAllTags();
    }

    @Test
    void getAllTags_WhenMixedTags_ShouldReturnUniqueNonEmptyTags() {
        List<String> mixedTags = Arrays.asList(
                "java,spring",
                "",
                null,
                "spring,java,junit",
                "  "
        );
        when(tagRepository.findAllTags()).thenReturn(mixedTags);

        List<String> result = tagService.getAllTags();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList("java", "spring", "junit")));
        verify(tagRepository).findAllTags();
    }
}
