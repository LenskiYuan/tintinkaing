package com.example.demo.controller;

import com.example.demo.dto.BookDTO;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @Test
    void createBookWithDuplicateIsbnReturnsBadRequest() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setTitle("Example");
        dto.setIsbn("1234567890123");
        dto.setPublishedYear(2024);
        dto.setPublisher("Example Press");

        given(bookRepository.findByIsbn("1234567890123")).willReturn(Optional.of(new com.example.demo.entity.Book()));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Book already exists with ISBN: 1234567890123"));
    }
}
