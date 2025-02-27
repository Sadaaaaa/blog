package ru.yandex.practicum.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.PostService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Получение списка постов
    @GetMapping
    public String listPosts(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(required = false) String tag,
                            Model model) {
        Page<Post> postPage = postService.getAllPosts(page, size, tag);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("selectedTag", tag);

        return "list";
    }

    // Получение картинки поста
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) {
        byte[] image = postService.getImageByPostId(id);
        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(image);
    }

    // Просмотр одного поста
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/posts?error=not_found";
        }
        model.addAttribute("post", post);

        String base64Image = null;
        if (post.getImage() != null) {
            base64Image = Base64.getEncoder().encodeToString(post.getImage());
        }
        model.addAttribute("base64Image", base64Image);

        return "post";
    }

    // Добавление поста
    @PostMapping("/add")
    public String addPost(@RequestParam("title") String title,
                          @RequestParam("text") String text,
                          @RequestParam(value = "image", required = false) MultipartFile image,
                          @RequestParam(value = "tags", required = false) String tags) throws IOException {


        List<String> tagList = new ArrayList<>();
        if (tags != null && !tags.isEmpty()) {
            tagList = Arrays.asList(tags.replaceAll("\\s", "").split(","));
        }

        Post savedPost = postService.addPost(title, text, tagList, image);
        return "redirect:/posts";
    }

    // Редактирование поста
    @PostMapping("/edit")
    public String editPost(@RequestParam("id") Long id,
                           @RequestParam("title") String title,
                           @RequestParam("text") String text,
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           @RequestParam(value = "tags", required = false) String tags) throws IOException {

        if (id == null) {
            return "redirect:/posts?error=missing_id";
        }

        List<String> tagList = new ArrayList<>();
        if (tags != null && !tags.isEmpty()) {
            tagList = new Gson().fromJson(tags, new TypeToken<List<String>>() {
            }.getType());
        }

        postService.updatePost(id, title, text, tagList, image);
        return "redirect:/posts/" + id;
    }


    // Удаление поста
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    // Лайк поста
    @PostMapping("/like/{id}")
    @ResponseBody
    public ResponseEntity<String> likePost(@PathVariable Long id) {
        postService.likePost(id);
        return ResponseEntity.ok("Лайк добавлен");
    }
}

