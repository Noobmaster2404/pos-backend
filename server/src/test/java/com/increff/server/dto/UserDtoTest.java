package com.increff.server.dto;

import com.increff.commons.model.LoginData;
import com.increff.commons.model.LoginForm;
import com.increff.commons.model.Role;
import com.increff.commons.model.SignupForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserDtoTest extends AbstractUnitTest {

    @Autowired
    private UserDto dto;

    private SignupForm createTestSignupForm(String email, String password) {
        SignupForm form = new SignupForm();
        form.setEmail(email);
        form.setPassword(password);
        return form;
    }

    private LoginForm createTestLoginForm(String email, String password) {
        LoginForm form = new LoginForm();
        form.setEmail(email);
        form.setPassword(password);
        return form;
    }

    @Test
    public void testSignupOperator() throws ApiException {
        SignupForm form = createTestSignupForm("operator@test.com", "password123");
        dto.signup(form);

        LoginData data = dto.login(createTestLoginForm("operator@test.com", "password123"));
        assertEquals("operator@test.com", data.getEmail());
        assertEquals(Role.OPERATOR.name(), data.getRole());
    }

    @Test
    public void testSignupSupervisor() throws ApiException {
        // Use the supervisor email from test.properties
        SignupForm form = createTestSignupForm("test@increff.com", "password123");
        dto.signup(form);

        LoginData data = dto.login(createTestLoginForm("test@increff.com", "password123"));
        assertEquals("test@increff.com", data.getEmail());
        assertEquals(Role.SUPERVISOR.name(), data.getRole());
    }

    @Test
    public void testSignupNonSupervisor() throws ApiException {
        // Use an email that's not in supervisor list
        String nonSupervisorEmail = "operator@test.com";
        
        SignupForm form = createTestSignupForm(nonSupervisorEmail, "password123");
        dto.signup(form);

        LoginData data = dto.login(createTestLoginForm(nonSupervisorEmail, "password123"));
        assertEquals(nonSupervisorEmail, data.getEmail());
        assertEquals(Role.OPERATOR.name(), data.getRole());
    }

    @Test(expected = ApiException.class)
    public void testSignupDuplicateEmail() throws ApiException {
        // Create and signup first user
        SignupForm form = createTestSignupForm("duplicate@test.com", "password123");
        dto.signup(form);
        
        // Try to signup again with same email - should throw ApiException
        dto.signup(form);
    }

    @Test(expected = ApiException.class)
    public void testLoginWithInvalidCredentials() throws ApiException {
        SignupForm signupForm = createTestSignupForm("test@test.com", "password123");
        dto.signup(signupForm);

        LoginForm loginForm = createTestLoginForm("test@test.com", "wrongpassword");
        dto.login(loginForm); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testLoginWithNonexistentUser() throws ApiException {
        LoginForm form = createTestLoginForm("nonexistent@test.com", "password123");
        dto.login(form); // Should throw ApiException
    }

    @Test
    public void testGetUserInfo() throws ApiException {
        // First signup and login
        SignupForm signupForm = createTestSignupForm("info@test.com", "password123");
        dto.signup(signupForm);
        dto.login(createTestLoginForm("info@test.com", "password123"));

        // Get user info
        LoginData userInfo = dto.getUserInfo();
        assertEquals("info@test.com", userInfo.getEmail());
        assertEquals(Role.OPERATOR.name(), userInfo.getRole());
    }

    @Test(expected = ApiException.class)
    public void testGetUserInfoWhenNotLoggedIn() throws ApiException {
        // Clear security context
        SecurityContextHolder.clearContext();
        dto.getUserInfo(); // Should throw ApiException
    }

    @Test
    public void testEmailCaseInsensitivity() throws ApiException {
        // Signup with lowercase
        SignupForm signupForm = createTestSignupForm("case@test.com", "password123");
        dto.signup(signupForm);

        // Login with uppercase
        LoginForm loginForm = createTestLoginForm("CASE@TEST.COM", "password123");
        LoginData data = dto.login(loginForm);
        assertEquals("case@test.com", data.getEmail());
    }

    @Test(expected = ApiException.class)
    public void testSignupWithInvalidEmail() throws ApiException {
        SignupForm form = createTestSignupForm("notanemail", "password123");
        dto.signup(form); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testSignupWithBlankPassword() throws ApiException {
        SignupForm form = createTestSignupForm("test@test.com", "");
        dto.signup(form); // Should throw ApiException
    }

    @Test
    public void testSuccessfulLoginSetsAuthenticationContext() throws ApiException {
        // Signup
        SignupForm signupForm = createTestSignupForm("auth@test.com", "password123");
        dto.signup(signupForm);

        // Login
        LoginForm loginForm = createTestLoginForm("auth@test.com", "password123");
        dto.login(loginForm);

        // Verify authentication context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("auth@test.com", 
            SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    public void testEmailNormalization() throws ApiException {
        // Signup with uppercase email
        SignupForm signupForm = createTestSignupForm("TRIM@TEST.COM", "password123");
        dto.signup(signupForm);

        // Login with lowercase email
        LoginForm loginForm = createTestLoginForm("trim@test.com", "password123");
        LoginData data = dto.login(loginForm);
        assertEquals("trim@test.com", data.getEmail());
    }

    @Test(expected = ApiException.class)
    public void testInvalidEmailFormat() throws ApiException {
        // Try to signup with invalid email format
        SignupForm form = createTestSignupForm("notanemail", "password123");
        dto.signup(form); // Should throw ApiException
    }
}
