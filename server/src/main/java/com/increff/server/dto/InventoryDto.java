package com.increff.server.dto;

import com.increff.server.entity.Inventory;
import com.increff.server.entity.Product;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.model.InventoryData;
import com.increff.server.flow.InventoryFlow;
import com.increff.server.flow.ProductFlow;
import com.increff.commons.exception.ApiException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryDto extends AbstractDto {
    
    @Autowired
    private InventoryFlow inventoryFlow;
    
    @Autowired
    private ProductFlow productFlow;

    public InventoryData addInventory(InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productFlow.getProductById(form.getProductId());
        Inventory inventory = ConversionClass.convert(form, product);
        return ConversionClass.convert(inventoryFlow.addInventory(inventory));
    }

    public List<InventoryData> getAllInventory() throws ApiException {
        return inventoryFlow.getAllInventory()
                .stream()
                .map(ConversionClass::convert)
                .collect(Collectors.toList());
    }

    public InventoryData getInventoryById(Integer productId) throws ApiException {
        return ConversionClass.convert(inventoryFlow.getInventoryById(productId));
    }

    public InventoryData updateInventoryById(Integer productId, InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productFlow.getProductById(form.getProductId());
        Inventory inventory = ConversionClass.convert(form, product);
        Inventory updatedInventory = inventoryFlow.updateInventoryById(productId, inventory);
        return ConversionClass.convert(updatedInventory);
    }

    @Override
    protected String getPrefix() {
        return "Inventory: ";
    }
}
