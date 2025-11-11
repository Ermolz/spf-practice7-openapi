package com.example.openapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUnauthenticatedUserCannotAccessBooks() throws Exception {
        // JWT filter may return 401 Unauthorized or 403 Forbidden when no token is present
        mockMvc.perform(get("/api/books"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status == 401 || status == 403, 
                        "Expected 401 or 403, but got " + status);
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCanGetBooks() throws Exception {
        // Перевірка 2: Користувач з роллю USER може отримати список книг
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotCreateBooks() throws Exception {
        // Перевірка 3: Користувач з роллю USER не може створювати книги
        String bookJson = "{\"title\":\"Test Book\",\"author\":\"Test Author\"}";
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanCreateBooks() throws Exception {
        // Перевірка 4: Адміністратор може створювати книги
        String bookJson = "{\"title\":\"Test Book\",\"author\":\"Test Author\"}";
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotAccessAdminPage() throws Exception {
        // Перевірка 5: Користувач з роллю USER не може отримати доступ до адмін-сторінки
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanAccessAdminPage() throws Exception {
        // Перевірка 6: Адміністратор може отримати доступ до адмін-сторінки
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUserCannotDeleteBooks() throws Exception {
        // Перевірка 7: Користувач з роллю USER не може видаляти книги
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminCanDeleteBooks() throws Exception {
        // Перевірка 8: Адміністратор може видаляти книги
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}

