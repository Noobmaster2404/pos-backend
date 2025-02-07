package com.increff.server.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;

public class ClientFlowTest extends AbstractUnitTest {

    @Autowired
    private ClientFlow clientFlow;

    // Helper method to create a test client
    private Client createTestClient(String name, String phone, String email) {
        Client client = new Client();
        client.setClientName(name);
        client.setPhone(phone);
        client.setEmail(email);
        client.setEnabled(true);
        return client;
    }

    @Test
    public void testAdd() throws ApiException {
        Client client = createTestClient("test client", "1234567890", "test@test.com");
        Client added = clientFlow.addClient(client);
        assertNotNull(added.getClientId());
        assertEquals("test client", added.getClientName());
    }

    @Test
    public void testGetAll() throws ApiException {
        Client client1 = createTestClient("client1", "1234567890", "test1@test.com");
        Client client2 = createTestClient("client2", "9876543210", "test2@test.com");
        clientFlow.addClient(client1);
        clientFlow.addClient(client2);
        
        List<Client> clients = clientFlow.getAllClients();
        assertEquals(2, clients.size());
    }

    @Test
    public void testUpdate() throws ApiException {
        Client client = createTestClient("test client", "1234567890", "test@test.com");
        Client added = clientFlow.addClient(client);
        
        Client updateData = createTestClient("updated client", "9876543210", "updated@test.com");
        Client updated = clientFlow.updateClientById(added.getClientId(), updateData);
        
        assertEquals("updated client", updated.getClientName());
        assertEquals("9876543210", updated.getPhone());
        assertEquals("updated@test.com", updated.getEmail());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicate() throws ApiException {
        Client client1 = createTestClient("test client", "1234567890", "test@test.com");
        Client client2 = createTestClient("test client", "9876543210", "test2@test.com");
        clientFlow.addClient(client1);
        clientFlow.addClient(client2); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testUpdateInvalidId() throws ApiException {
        Client updateData = createTestClient("updated client", "9876543210", "updated@test.com");
        clientFlow.updateClientById(999, updateData); // Should throw ApiException
    }
}