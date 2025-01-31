package com.increff.server.dto;

import com.increff.server.entity.Inventory;
import com.increff.server.entity.Product;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.model.InventoryData;
import com.increff.server.flow.InventoryFlow;
import com.increff.server.api.ProductApi;
import com.increff.commons.exception.ApiException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
@Component
public class InventoryDto extends AbstractDto {
    
    @Autowired
    private InventoryFlow inventoryFlow;
    
    @Autowired
    private ProductApi productApi;

    public InventoryData addInventory(InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productApi.getProductByBarcode(form.getBarcode());
        Inventory inventory = ConversionHelper.convertToInventory(form, product);
        return ConversionHelper.convertToInventoryData(inventoryFlow.addInventory(inventory));
    }

    public List<InventoryData> getAllInventory() throws ApiException {
        return ConversionHelper.convertToInventoryData(inventoryFlow.getAllInventory());
    }

    public InventoryData getInventoryById(Integer productId) throws ApiException {
        return ConversionHelper.convertToInventoryData(inventoryFlow.getInventoryById(productId));
    }

    public InventoryData updateInventoryById(Integer productId, InventoryForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Product product = productApi.getProductByBarcode(form.getBarcode());
        Inventory inventory = ConversionHelper.convertToInventory(form, product);
        Inventory updatedInventory = inventoryFlow.updateInventoryById(productId, inventory);
        return ConversionHelper.convertToInventoryData(updatedInventory);
    }

    public List<InventoryData> bulkAddInventory(List<InventoryForm> forms) throws ApiException {
        if (forms.size() > 5000) {
            throw new ApiException(getPrefix() + "Cannot process more than 5000 inventory items at once");
        }

        for (InventoryForm form : forms) {
            normalize(form);
        }
        Map<InventoryForm, Product> inventoryProductMap = new HashMap<>();
        for (InventoryForm form : forms) {
            try{
                Product product = productApi.getProductByBarcode(form.getBarcode());
                inventoryProductMap.put(form, product);
            } catch (ApiException e) {
                throw new ApiException(getPrefix() + e.getMessage());
            }
        }

        List<Inventory> addedInventory = inventoryFlow.bulkAddInventory(
            ConversionHelper.convertToInventory(inventoryProductMap));

        return ConversionHelper.convertToInventoryData(addedInventory);
    }

    @Override
    protected String getPrefix() {
        return "Inventory: ";
    }
}
