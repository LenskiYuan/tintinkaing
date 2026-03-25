package com.example.demo.controller;

import com.example.demo.dto.AuthorDTO;
import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Convert entity to response DTO
    private AuthorDTO convertToResponseDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setBiography(author.getBiography());
        dto.setBirthDate(author.getBirthDate() != null ? author.getBirthDate().toString() : null);
        return dto;
    }

    // Convert request DTO to entity
    private Author convertToEntity(AuthorDTO dto) {
        Author author = new Author();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        author.setBiography(dto.getBiography());
        if (dto.getBirthDate() != null && !dto.getBirthDate().trim().isEmpty()) {
            author.setBirthDate(java.time.LocalDate.parse(dto.getBirthDate()));
        }
        return author;
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> authors = authorRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorRepository.findById(id);
        return author.map(a -> {
            AuthorDTO dto = convertToResponseDTO(a);
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorDTO dto) {
        Optional<Author> existingAuthor = authorRepository.findByLastName(dto.getLastName());
        if (existingAuthor.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Author author = convertToEntity(dto);
        Author savedAuthor = authorRepository.save(author);
        AuthorDTO responseDTO = new AuthorDTO();
        responseDTO.setId(savedAuthor.getId());
        responseDTO.setFirstName(savedAuthor.getFirstName());
        responseDTO.setLastName(savedAuthor.getLastName());
        responseDTO.setBiography(savedAuthor.getBiography());
        responseDTO.setBirthDate(savedAuthor.getBirthDate() != null ? savedAuthor.getBirthDate().toString() : null);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDTO dto) {
        Optional<Author> existingAuthor = authorRepository.findById(id);
        if (!existingAuthor.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Author author = existingAuthor.get();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        author.setBiography(dto.getBiography());
        if (dto.getBirthDate() != null && !dto.getBirthDate().trim().isEmpty()) {
            author.setBirthDate(java.time.LocalDate.parse(dto.getBirthDate()));
        }

        Author updatedAuthor = authorRepository.save(author);
        AuthorDTO responseDTO = new AuthorDTO();
        responseDTO.setId(updatedAuthor.getId());
        responseDTO.setFirstName(updatedAuthor.getFirstName());
        responseDTO.setLastName(updatedAuthor.getLastName());
        responseDTO.setBiography(updatedAuthor.getBiography());
        responseDTO.setBirthDate(updatedAuthor.getBirthDate() != null ? updatedAuthor.getBirthDate().toString() : null);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (!authorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        authorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}