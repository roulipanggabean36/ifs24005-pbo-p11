package org.delcom.app.controllers;

import org.delcom.app.dto.TodoForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.services.TodoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoControllerTests {

    @Mock private TodoService todoService;
    @Mock private CashFlowService cashFlowService;
    @Mock private Model model;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private TodoController todoController;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    private void mockLogin(boolean isAuthenticated) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        
        if (isAuthenticated) {
            User user = new User("Test", "test@test.com");
            user.setId(UUID.randomUUID());
            lenient().when(authentication.isAuthenticated()).thenReturn(true);
            lenient().when(authentication.getPrincipal()).thenReturn(user);
        } else {
            lenient().when(authentication.isAuthenticated()).thenReturn(false);
            lenient().when(authentication.getPrincipal()).thenReturn("anonymous");
        }
    }

    // --- INDEX & AUTH EDGE CASES ---
    
    @Test
    void testIndex_Authenticated() {
        mockLogin(true);
        when(todoService.getAllTodos(any(), any())).thenReturn(Collections.emptyList());
        
        String view = todoController.index(model);
        
        // PERBAIKAN: Sesuai error log, harus pages/home
        assertEquals("pages/home", view); 
    }

    @Test
    void testIndex_NotAuthenticated() {
        mockLogin(false);
        String view = todoController.index(model);
        assertEquals("redirect:/auth/logout", view);
    }

    // Case 1: Authentication object is NULL
    @Test
    void testIndex_AuthNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        String view = todoController.index(model);
        assertEquals("redirect:/auth/logout", view);
    }

    // Case 2: Authentication is AnonymousAuthenticationToken
    @Test
    void testIndex_AnonymousToken() {
        AnonymousAuthenticationToken anonToken = mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(anonToken);
        // Anggap dia authenticated true, tapi tipe class-nya anonymous
        when(anonToken.isAuthenticated()).thenReturn(true);

        String view = todoController.index(model);
        assertEquals("redirect:/auth/logout", view);
    }

    // Case 3: Principal is NOT User class (e.g. String "anonymousUser")
    @Test
    void testIndex_PrincipalNotUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("JustAStringUser");

        String view = todoController.index(model);
        assertEquals("redirect:/auth/logout", view);
    }

    // --- ADD ---
    @Test
    void testAddTodo_Authenticated() {
        mockLogin(true);
        todoController.addTodo(new TodoForm(null, "A", "B", false, null));
        verify(todoService).createTodo(any(), any(), any());
    }

    @Test
    void testAddTodo_NotAuthenticated() {
        mockLogin(false);
        todoController.addTodo(new TodoForm());
        verify(todoService, never()).createTodo(any(), any(), any());
    }

    // --- EDIT ---
    @Test
    void testEditTodo_Authenticated() {
        mockLogin(true);
        todoController.editTodo(UUID.randomUUID(), new TodoForm(null, "A", "B", true, null));
        verify(todoService).updateTodo(any(), any(), any(), any(), eq(true));
    }

    @Test
    void testEditTodo_NotAuthenticated() {
        mockLogin(false);
        todoController.editTodo(UUID.randomUUID(), new TodoForm());
        verify(todoService, never()).updateTodo(any(), any(), any(), any(), anyBoolean());
    }

    // --- DELETE ---
    @Test
    void testDeleteTodo_Authenticated() {
        mockLogin(true);
        todoController.deleteTodo(UUID.randomUUID());
        verify(todoService).deleteTodo(any(), any());
    }

    @Test
    void testDeleteTodo_NotAuthenticated() {
        mockLogin(false);
        todoController.deleteTodo(UUID.randomUUID());
        verify(todoService, never()).deleteTodo(any(), any());
    }
}