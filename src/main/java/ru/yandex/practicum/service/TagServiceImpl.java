package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.repository.TagRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<String> getAllTags() {
        List<String> allTags = tagRepository.findAllTags();

        Set<String> uniqueTags = new HashSet<>();
        for (String tags : allTags) {
            if (tags != null && !tags.isBlank()) {
                uniqueTags.addAll(Arrays.asList(tags.split(",")));
            }
        }

        return new ArrayList<>(uniqueTags);
    }


}
