package com.increff.server.dto;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ClientData;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;

public class ClientDtoTest extends AbstractUnitTest {

    @Autowired
    private ClientDto dto;

    private ClientForm createTestClientForm(String name, String phone, String email) {
        ClientForm form = new ClientForm();
        form.setClientName(name);
        form.setPhone(phone);
        form.setEmail(email);
        form.setEnabled(true);
        return form;
    }

    @Test
    public void testAdd() throws ApiException {
        ClientForm form = createTestClientForm("Test Client", "1234567890", "test@test.com");
        ClientData data = dto.addClient(form);
        
        assertNotNull(data.getClientId());
        assertEquals("Test Client", data.getClientName());
        assertEquals("1234567890", data.getPhone());
        assertEquals("test@test.com", data.getEmail());
        assertTrue(data.getEnabled());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicate() throws ApiException {
        ClientForm form1 = createTestClientForm("Test Client", "1234567890", "test1@test.com");
        ClientForm form2 = createTestClientForm("Test Client", "9876543210", "test2@test.com");
        
        dto.addClient(form1);
        dto.addClient(form2); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        ClientForm form1 = createTestClientForm("Test Client 1", "1234567890", "test1@test.com");
        ClientForm form2 = createTestClientForm("Test Client 2", "9876543210", "test2@test.com");
        
        dto.addClient(form1);
        dto.addClient(form2);
        
        List<ClientData> clients = dto.getAllClients();
        assertEquals(2, clients.size());
    }

    @Test
    public void testUpdate() throws ApiException {
        ClientForm form = createTestClientForm("Test Client", "1234567890", "test@test.com");
        ClientData added = dto.addClient(form);
        
        ClientForm updateForm = createTestClientForm("Updated Client", "9876543210", "updated@test.com");
        ClientData updated = dto.updateClientById(added.getClientId(), updateForm);
        
        assertEquals("Updated Client", updated.getClientName());
        assertEquals("9876543210", updated.getPhone());
        assertEquals("updated@test.com", updated.getEmail());
    }

    @Test(expected = ApiException.class)
    public void testUpdateDuplicateName() throws ApiException {
        // Add first client
        ClientForm form1 = createTestClientForm("Test Client 1", "1234567890", "test1@test.com");
        dto.addClient(form1);
        
        // Add second client
        ClientForm form2 = createTestClientForm("Test Client 2", "9876543210", "test2@test.com");
        ClientData client2 = dto.addClient(form2);
        
        // Try to update second client with first client's name
        ClientForm updateForm = createTestClientForm("Test Client 1", "9876543210", "test2@test.com");
        dto.updateClientById(client2.getClientId(), updateForm); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentClient() throws ApiException {
        ClientForm form = createTestClientForm("Test Client", "1234567890", "test@test.com");
        dto.updateClientById(999, form); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testAddInvalidPhone() throws ApiException {
        ClientForm form = createTestClientForm("Test Client", "123", "test@test.com");
        dto.addClient(form); // Should throw ApiException for invalid phone number
    }

    @Test(expected = ApiException.class)
    public void testAddInvalidEmail() throws ApiException {
        ClientForm form = createTestClientForm("Test Client", "1234567890", "invalid-email");
        dto.addClient(form); // Should throw ApiException for invalid email
    }

    @Test(expected = ApiException.class)
    public void testAddBlankName() throws ApiException {
        ClientForm form = createTestClientForm("", "1234567890", "test@test.com");
        dto.addClient(form); // Should throw ApiException for blank name
    }

    @Test
    public void testAddNameNormalization() throws ApiException {
        ClientForm form = createTestClientForm("  Test   Client  ", "1234567890", "test@test.com");
        ClientData data = dto.addClient(form);
        assertEquals("Test Client", data.getClientName()); // Should be normalized
    }

    @Test
    public void testUpdateEnabled() throws ApiException {
        ClientForm form = createTestClientForm("Test Client", "1234567890", "test@test.com");
        ClientData added = dto.addClient(form);
        
        ClientForm updateForm = createTestClientForm("Test Client", "1234567890", "test@test.com");
        updateForm.setEnabled(false);
        
        ClientData updated = dto.updateClientById(added.getClientId(), updateForm);
        assertFalse(updated.getEnabled());
    }

    @Test(expected = ApiException.class)
    public void testAddNameTooLong() throws ApiException {
        StringBuilder longName = new StringBuilder();
        for(int i = 0; i < 256; i++) {
            longName.append("a");
        }
        ClientForm form = createTestClientForm(longName.toString(), "1234567890", "test@test.com");
        dto.addClient(form);
    }

    @Test(expected = ApiException.class)
    public void testAddEmailTooLong() throws ApiException {
        StringBuilder longEmail = new StringBuilder();
        for(int i = 0; i < 247; i++) {
            longEmail.append("a");
        }
        longEmail.append("@test.com");
        ClientForm form = createTestClientForm("Test Client", "1234567890", longEmail.toString());
        dto.addClient(form);
    }
} 