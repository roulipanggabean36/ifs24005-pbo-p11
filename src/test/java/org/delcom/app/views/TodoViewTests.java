package org.delcom.app.views;

import org.delcom.app.dto.CoverTodoForm;
import org.delcom.app.dto.TodoForm;
import org.delcom.app.entities.Todo;
import org.delcom.app.entities.User;
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.TodoService;
import org.delcom.app.utils.ConstUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoViewTests {

    @Mock private TodoService todoService;
    @Mock private FileStorageService fileStorageService;
    @Mock private RedirectAttributes redirectAttributes;
    @Mock private HttpSession session;
    @Mock private Model model;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private TodoView todoView;

    private User user;
    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        user = new User("Test User", "test@example.com");
        user.setId(UUID.randomUUID());
        
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    private void mockLogin(boolean isLoggedIn) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        if (isLoggedIn) {
            lenient().when(authentication.isAuthenticated()).thenReturn(true);
            lenient().when(authentication.getPrincipal()).thenReturn(user);
        } else {
            lenient().when(authentication.isAuthenticated()).thenReturn(false);
            lenient().when(authentication.getPrincipal()).thenReturn("anonymousUser");
        }
    }

    // --- ADD TODO ---
    @Test
    void testPostAddTodo_NotLoggedIn() {
        mockLogin(false);
        String view = todoView.postAddTodo(new TodoForm(), redirectAttributes, session, model);
        assertEquals("redirect:/auth/logout", view);
    }

    @Test
    void testPostAddTodo_Success() {
        mockLogin(true);
        TodoForm form = new TodoForm(null, "Judul", "Desc", false, null);
        when(todoService.createTodo(any(), any(), any())).thenReturn(new Todo());
        String view = todoView.postAddTodo(form, redirectAttributes, session, model);
        assertEquals("redirect:/", view);
        verify(redirectAttributes).addFlashAttribute(eq("success"), any());
    }

    // --- EDIT TODO ---
    @Test
    void testPostEditTodo_Success() {
        mockLogin(true);
        TodoForm form = new TodoForm(UUID.randomUUID(), "Judul", "Desc", false, null);
        when(todoService.updateTodo(any(), any(), any(), any(), anyBoolean())).thenReturn(new Todo());
        String view = todoView.postEditTodo(form, redirectAttributes, session, model);
        assertEquals("redirect:/", view);
    }

    // --- DELETE TODO ---
    @Test
    void testPostDeleteTodo_Success() {
        mockLogin(true);
        UUID id = UUID.randomUUID();
        TodoForm form = new TodoForm(id, null, null, false, null);
        when(todoService.getTodoById(any(), eq(id))).thenReturn(new Todo());
        when(todoService.deleteTodo(any(), eq(id))).thenReturn(true);
        String view = todoView.postDeleteTodo(form, redirectAttributes, session, model);
        assertEquals("redirect:/", view);
    }

    // --- DETAIL ---
    @Test
    void testGetDetailTodo_Success() {
        mockLogin(true);
        when(todoService.getTodoById(any(), any())).thenReturn(new Todo());
        String view = todoView.getDetailTodo(UUID.randomUUID(), model);
        // Pastikan ConstUtil.TEMPLATE_PAGES_TODOS_DETAIL benar (biasanya "pages/todos/detail")
        // Kita cek saja tidak redirect
        assertNotEquals("redirect:/", view);
    }

    // ===============================================
    // TEST BARU: FITUR EDIT COVER (Penyebab Coverage 90%)
    // ===============================================

    @Test
    void testPostEditCover_NotLoggedIn() {
        mockLogin(false);
        String view = todoView.postEditCoverTodo(new CoverTodoForm(), redirectAttributes, session, model);
        assertEquals("redirect:/auth/logout", view);
    }

    @Test
    void testPostEditCover_Empty() {
        mockLogin(true);
        CoverTodoForm form = mock(CoverTodoForm.class);
        UUID id = UUID.randomUUID();
        when(form.getId()).thenReturn(id);
        when(form.isEmpty()).thenReturn(true); // Simulasi file kosong

        String view = todoView.postEditCoverTodo(form, redirectAttributes, session, model);
        assertEquals("redirect:/todos/" + id, view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("kosong"));
    }

    @Test
    void testPostEditCover_TodoNotFound() {
        mockLogin(true);
        CoverTodoForm form = mock(CoverTodoForm.class);
        UUID id = UUID.randomUUID();
        when(form.getId()).thenReturn(id);
        when(form.isEmpty()).thenReturn(false);
        
        when(todoService.getTodoById(any(), eq(id))).thenReturn(null); // Todo tidak ada

        String view = todoView.postEditCoverTodo(form, redirectAttributes, session, model);
        assertEquals("redirect:/", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("tidak ditemukan"));
    }

    @Test
    void testPostEditCover_InvalidImage() {
        mockLogin(true);
        CoverTodoForm form = mock(CoverTodoForm.class);
        UUID id = UUID.randomUUID();
        when(form.getId()).thenReturn(id);
        when(form.isEmpty()).thenReturn(false);
        when(todoService.getTodoById(any(), eq(id))).thenReturn(new Todo());
        
        when(form.isValidImage()).thenReturn(false); // Bukan gambar valid

        String view = todoView.postEditCoverTodo(form, redirectAttributes, session, model);
        assertEquals("redirect:/todos/" + id, view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Format file"));
    }

    @Test
    void testPostEditCover_InvalidSize() {
        mockLogin(true);
        CoverTodoForm form = mock(CoverTodoForm.class);
        UUID id = UUID.randomUUID();
        when(form.getId()).thenReturn(id);
        when(form.isEmpty()).thenReturn(false);
        when(todoService.getTodoById(any(), eq(id))).thenReturn(new Todo());
        when(form.isValidImage()).thenReturn(true);
        
        when(form.isSizeValid(anyLong())).thenReturn(false); // Size kegedean

        String view = todoView.postEditCoverTodo(form, redirectAttributes, session, model);
        assertEquals("redirect:/todos/" + id, view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Ukuran file"));
    }

    @Test
    void testPostEditCover_Success() throws IOException {
        mockLogin(true);
        CoverTodoForm form = mock(CoverTodoForm.class);
        UUID id = UUID.randomUUID();
        when(form.getId()).thenReturn(id);
        when(form.isEmpty()).thenReturn(false);
        when(form.isValidImage()).thenReturn(true);
        when(form.isSizeValid(anyLong())).thenReturn(true);
        when(form.getCoverFile()).thenReturn(mock(MultipartFile.class));

        when(todoService.getTodoById(any(), eq(id))).thenReturn(new Todo());
        when(fileStorageService.storeFile(any(), any())).thenReturn("cover.jpg");

        String view = todoView.postEditCoverTodo(form, redirectAttributes, session, model);
        
        assertEquals("redirect:/todos/" + id, view);
        verify(redirectAttributes).addFlashAttribute(eq("success"), any());
        verify(todoService).updateCover(eq(id), eq("cover.jpg"));
    }

    @Test
    void testPostEditCover_IOException() throws IOException {
        mockLogin(true);
        CoverTodoForm form = mock(CoverTodoForm.class);
        UUID id = UUID.randomUUID();
        when(form.getId()).thenReturn(id);
        when(form.isEmpty()).thenReturn(false);
        when(form.isValidImage()).thenReturn(true);
        when(form.isSizeValid(anyLong())).thenReturn(true);
        when(todoService.getTodoById(any(), eq(id))).thenReturn(new Todo());

        // Simulasi Error IO
        when(fileStorageService.storeFile(any(), any())).thenThrow(new IOException("Disk Full"));

        String view = todoView.postEditCoverTodo(form, redirectAttributes, session, model);
        
        assertEquals("redirect:/todos/" + id, view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Gagal"));
    }

    // ===============================================
    // TEST BARU: GET COVER IMAGE (Penyebab Coverage 90%)
    // ===============================================

    @Test
    void testGetCoverByFilename_Success() {
        // Kita pakai file pom.xml yang pasti ada di proyek
        Path realPath = Paths.get("pom.xml");
        when(fileStorageService.loadFile("pom.xml")).thenReturn(realPath);

        Resource res = todoView.getCoverByFilename("pom.xml");
        assertNotNull(res);
    }

    @Test
    void testGetCoverByFilename_Fail() {
        when(fileStorageService.loadFile(anyString())).thenThrow(new RuntimeException("File not found"));
        Resource res = todoView.getCoverByFilename("invalid.jpg");
        assertNull(res);
    }
}