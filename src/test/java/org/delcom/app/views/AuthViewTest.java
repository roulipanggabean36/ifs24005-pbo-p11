package org.delcom.app.views;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.ConstUtil;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthViewTest {

    @Mock private UserService userService;
    @Mock private AuthTokenService authTokenService; // Diperlukan meski tidak dipakai di logic, karena ada di constructor
    @Mock private Model model;
    @Mock private HttpSession session;
    @Mock private BindingResult bindingResult;
    @Mock private RedirectAttributes redirectAttributes;
    
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private AuthView authView;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        // Kita perlu mock static SecurityContextHolder karena AuthView memanggilnya secara statis
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        securityContextHolderMock.when(SecurityContextHolder::createEmptyContext).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Wajib ditutup agar tidak mengganggu test lain
        securityContextHolderMock.close();
    }

    // --- TEST HALAMAN LOGIN (GET) ---

    @Test
    void testShowLogin_NotLoggedIn() {
        // Simulasi belum login (Authentication null atau anonymous)
        when(securityContext.getAuthentication()).thenReturn(null);

        String view = authView.showLogin(model, session);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN, view);
        verify(model).addAttribute(eq("loginForm"), any(LoginForm.class));
    }

    @Test
    void testShowLogin_AlreadyLoggedIn() {
        // Simulasi sudah login
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        // Pastikan bukan Anonymous token
        // (Di Mockito default mock bukan instance of AnonymousAuthenticationToken, jadi aman)

        String view = authView.showLogin(model, session);

        assertEquals("redirect:/", view);
    }

    // --- TEST PROSES LOGIN (POST) ---

    @Test
    void testPostLogin_ValidationError() {
        LoginForm form = new LoginForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = authView.postLogin(form, bindingResult, session, model);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN, view);
    }

    @Test
    void testPostLogin_UserNotFound() {
        LoginForm form = new LoginForm();
        form.setEmail("notfound@test.com");
        
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail(form.getEmail())).thenReturn(null);

        String view = authView.postLogin(form, bindingResult, session, model);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN, view);
        verify(bindingResult).rejectValue(eq("email"), anyString(), anyString());
    }

    @Test
    void testPostLogin_WrongPassword() {
        LoginForm form = new LoginForm();
        form.setEmail("user@test.com");
        form.setPassword("wrongpass");

        User mockUser = new User();
        mockUser.setEmail("user@test.com");
        // Password di DB sudah di-hash
        mockUser.setPassword(new BCryptPasswordEncoder().encode("correctpass"));

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail(form.getEmail())).thenReturn(mockUser);

        String view = authView.postLogin(form, bindingResult, session, model);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN, view);
        verify(bindingResult).rejectValue(eq("email"), anyString(), anyString());
    }

    @Test
    void testPostLogin_Success() {
        LoginForm form = new LoginForm();
        form.setEmail("user@test.com");
        form.setPassword("password123");

        User mockUser = new User();
        mockUser.setEmail("user@test.com");
        // Password input cocok dengan hash di DB
        mockUser.setPassword(new BCryptPasswordEncoder().encode("password123"));

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail(form.getEmail())).thenReturn(mockUser);

        String view = authView.postLogin(form, bindingResult, session, model);

        assertEquals("redirect:/", view);
        // Verifikasi session diset
        verify(session).setAttribute(anyString(), any());
    }

    // --- TEST HALAMAN REGISTER (GET) ---

    @Test
    void testShowRegister_NotLoggedIn() {
        when(securityContext.getAuthentication()).thenReturn(null);

        String view = authView.showRegister(model, session);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER, view);
        verify(model).addAttribute(eq("registerForm"), any(RegisterForm.class));
    }

    @Test
    void testShowRegister_AlreadyLoggedIn() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        String view = authView.showRegister(model, session);

        assertEquals("redirect:/", view);
    }

    // --- TEST PROSES REGISTER (POST) ---

    @Test
    void testPostRegister_ValidationError() {
        RegisterForm form = new RegisterForm();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = authView.postRegister(form, bindingResult, redirectAttributes, session, model);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER, view);
    }

    @Test
    void testPostRegister_EmailAlreadyExists() {
        RegisterForm form = new RegisterForm();
        form.setEmail("existing@test.com");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail(form.getEmail())).thenReturn(new User()); // User ditemukan

        String view = authView.postRegister(form, bindingResult, redirectAttributes, session, model);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER, view);
        verify(bindingResult).rejectValue(eq("email"), anyString(), anyString());
    }

    @Test
    void testPostRegister_CreateFailed() {
        RegisterForm form = new RegisterForm();
        form.setName("New User");
        form.setEmail("new@test.com");
        form.setPassword("pass");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail(form.getEmail())).thenReturn(null); // Email belum ada
        when(userService.createUser(anyString(), anyString(), anyString())).thenReturn(null); // Gagal create

        String view = authView.postRegister(form, bindingResult, redirectAttributes, session, model);

        assertEquals(ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER, view);
        verify(bindingResult).rejectValue(eq("email"), anyString(), anyString());
    }

    @Test
    void testPostRegister_Success() {
        RegisterForm form = new RegisterForm();
        form.setName("New User");
        form.setEmail("new@test.com");
        form.setPassword("pass");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail(form.getEmail())).thenReturn(null);
        when(userService.createUser(anyString(), anyString(), anyString())).thenReturn(new User());

        String view = authView.postRegister(form, bindingResult, redirectAttributes, session, model);

        assertEquals("redirect:/auth/login", view);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    // --- TEST LOGOUT ---

    @Test
    void testLogout() {
        String view = authView.logout(session);

        assertEquals("redirect:/auth/login", view);
        verify(session).invalidate();
    }
}