package com.increff.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;

public class ClientApiTest extends AbstractUnitTest {

    @Autowired
    private ClientApi clientApi;

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
        Client added = clientApi.addClient(client);
        assertNotNull(added.getClientId());
        assertEquals("test client", added.getClientName());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicate() throws ApiException {
        Client client1 = createTestClient("test client", "1234567890", "test@test.com");
        Client client2 = createTestClient("test client", "9876543210", "test2@test.com");
        clientApi.addClient(client1);
        clientApi.addClient(client2); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        Client client1 = createTestClient("client1", "1234567890", "test1@test.com");
        Client client2 = createTestClient("client2", "9876543210", "test2@test.com");
        clientApi.addClient(client1);
        clientApi.addClient(client2);
        
        List<Client> clients = clientApi.getAllClients();
        assertEquals(2, clients.size());
    }

    @Test
    public void testGetCheckClientById() throws ApiException {
        Client client = createTestClient("test client", "1234567890", "test@test.com");
        Client added = clientApi.addClient(client);
        
        Client retrieved = clientApi.getCheckClientById(added.getClientId());
        assertEquals(added.getClientId(), retrieved.getClientId());
        assertEquals(added.getClientName(), retrieved.getClientName());
    }

    @Test(expected = ApiException.class)
    public void testGetCheckClientByIdInvalid() throws ApiException {
        clientApi.getCheckClientById(999); // Should throw ApiException
    }

    @Test
    public void testUpdateClient() throws ApiException {
        Client client = createTestClient("test client", "1234567890", "test@test.com");
        Client added = clientApi.addClient(client);
        
        Client updateData = createTestClient("updated client", "9876543210", "updated@test.com");
        Client updated = clientApi.updateClientById(added.getClientId(), updateData);
        
        assertEquals("updated client", updated.getClientName());
        assertEquals("9876543210", updated.getPhone());
        assertEquals("updated@test.com", updated.getEmail());
    }

    @Test(expected = ApiException.class)
    public void testUpdateClientDuplicateName() throws ApiException {
        Client client1 = createTestClient("client1", "1234567890", "test1@test.com");
        Client client2 = createTestClient("client2", "9876543210", "test2@test.com");
        clientApi.addClient(client1);
        Client added2 = clientApi.addClient(client2);
        
        // Try to update client2's name to client1's name
        Client updateData = createTestClient("client1", "9876543210", "test2@test.com");
        clientApi.updateClientById(added2.getClientId(), updateData); // Should throw ApiException
    }

    @Test
    public void testGetCheckClientNamesByIds() throws ApiException {
        Client client1 = createTestClient("client1", "1234567890", "test1@test.com");
        Client client2 = createTestClient("client2", "9876543210", "test2@test.com");
        Client added1 = clientApi.addClient(client1);
        Client added2 = clientApi.addClient(client2);
        
        Map<Integer, String> names = clientApi.getCheckClientNamesByIds(
            Arrays.asList(added1.getClientId(), added2.getClientId())
        );
        
        assertEquals(2, names.size());
        assertEquals("client1", names.get(added1.getClientId()));
        assertEquals("client2", names.get(added2.getClientId()));
    }

    @Test(expected = ApiException.class)
    public void testGetCheckClientNamesByIdsInvalid() throws ApiException {
        Client client = createTestClient("test client", "1234567890", "test@test.com");
        Client added = clientApi.addClient(client);
        
        clientApi.getCheckClientNamesByIds(Arrays.asList(added.getClientId(), 999)); // Should throw ApiException
    }

    @Test
    public void testGetCheckClientsByIds() throws ApiException {
        Client client1 = createTestClient("client1", "1234567890", "test1@test.com");
        Client client2 = createTestClient("client2", "9876543210", "test2@test.com");
        Client added1 = clientApi.addClient(client1);
        Client added2 = clientApi.addClient(client2);
        
        List<Client> clients = clientApi.getCheckClientsByIds(
            Arrays.asList(added1.getClientId(), added2.getClientId())
        );
        
        assertEquals(2, clients.size());
    }

    @Test(expected = ApiException.class)
    public void testGetCheckClientsByIdsInvalid() throws ApiException {
        Client client = createTestClient("test client", "1234567890", "test@test.com");
        Client added = clientApi.addClient(client);
        
        clientApi.getCheckClientsByIds(Arrays.asList(added.getClientId(), 999)); // Should throw ApiException
    }

    @Test
    public void testUpdateClientEnabled() throws ApiException {
        Client client = createTestClient("test client", "1234567890", "test@test.com");
        Client added = clientApi.addClient(client);
        
        Client updateData = createTestClient(added.getClientName(), added.getPhone(), added.getEmail());
        updateData.setEnabled(false);
        
        Client updated = clientApi.updateClientById(added.getClientId(), updateData);
        assertEquals(false, updated.getEnabled());
    }
}