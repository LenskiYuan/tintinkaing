package com.example.demo.controller;

import com.example.demo.dto.BookDTO;
import com.example.demo.dto.BookResponseDTO;
import com.example.demo.entity.Book;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    // Convert entity to response DTO
    private BookResponseDTO convertToResponseDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublishedYear(book.getPublishedYear());
        dto.setPublisher(book.getPublisher());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        dto.setAuthorNames(book.getAuthors().stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .collect(Collectors.toList()));
        return dto;
    }

    // Convert request DTO to entity
    private Book convertToEntity(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublishedYear(dto.getPublishedYear());
        book.setPublisher(dto.getPublisher());

        // Set authors if provided
        if (dto.getAuthorIds() != null) {
            dto.getAuthorIds().forEach(authorId -> {
                authorRepository.findById(authorId).ifPresent(author -> book.getAuthors().add(author));
            });
        }

        return book;
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        List<BookResponseDTO> books = bookRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return ResponseEntity.ok(convertToResponseDTO(book));
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book already exists with ISBN: " + bookDTO.getIsbn());
        }

        Book book = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(savedBook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (!existingBook.getIsbn().equals(bookDTO.getIsbn()) && bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book already exists with ISBN: " + bookDTO.getIsbn());
        }

        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setPublishedYear(bookDTO.getPublishedYear());
        existingBook.setPublisher(bookDTO.getPublisher());

        existingBook.getAuthors().clear();
        if (bookDTO.getAuthorIds() != null) {
            bookDTO.getAuthorIds().forEach(authorId -> {
                authorRepository.findById(authorId).ifPresent(author -> existingBook.getAuthors().add(author));
            });
        }

        Book updatedBook = bookRepository.save(existingBook);
        return ResponseEntity.ok(convertToResponseDTO(updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
