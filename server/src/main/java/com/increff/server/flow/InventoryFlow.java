package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Inventory;
import com.increff.server.api.InventoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class InventoryFlow {

    @Autowired
    private InventoryApi inventoryApi;

    public void add(Inventory inventory) throws ApiException {
        inventoryApi.add(inventory);
    }

    public List<Inventory> getAll() {
        return inventoryApi.getAll();
    }

    public Inventory get(Integer id) throws ApiException {
        return inventoryApi.get(id);
    }

    public Inventory update(Integer id, Inventory inventory) throws ApiException {
        return inventoryApi.update(id, inventory);
    }
}
