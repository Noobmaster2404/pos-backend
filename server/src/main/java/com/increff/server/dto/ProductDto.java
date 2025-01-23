package com.increff.server.dto;

import com.increff.server.entity.Product;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ProductData;
import com.increff.server.flow.ClientFlow;
import com.increff.server.flow.ProductFlow;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Client;
import com.increff.server.entity.Inventory;
import com.increff.server.flow.InventoryFlow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Component
public class ProductDto extends AbstractDto {
    
    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ClientFlow clientFlow;

    @Autowired
    private InventoryFlow inventoryFlow;

    public ProductData addProduct(ProductForm form) throws ApiException {
        try {
            checkValid(form);
            normalize(form);
            Client client = clientFlow.getClientById(form.getClientId());
            Product product = ConversionClass.convert(form, client);
            return ConversionClass.convert(productFlow.addProduct(product));
        } catch (Exception e) {
            throw new ApiException(getPrefix() + e.getMessage());
        }
    }

    public ProductData updateProductById(Integer productId, ProductForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = clientFlow.getClientById(form.getClientId());
        Product product = ConversionClass.convert(form, client);
        Product updatedProduct = productFlow.updateProductById(productId, product);
        ProductData data = ConversionClass.convert(updatedProduct);
        data.setClientName(client.getClientName());
        data.setClientId(client.getClientId());
        try {
            Inventory inventory = inventoryFlow.getInventoryById(productId);
            data.setQuantity(inventory.getQuantity().toString());
        } catch (Exception e) {
            data.setQuantity("0");
        }
        return data;
    }

    public List<ProductData> getAllProducts() throws ApiException {
        //should adding the quantity to the product data be here or in convertToData?
        return productFlow.getAllProducts()
                .stream()
                .map(product -> {
                    try {
                        ProductData data = ConversionClass.convert(product);
                        Client client = clientFlow.getClientById(product.getClient().getClientId());
                        data.setClientName(client.getClientName());
                        data.setClientId(client.getClientId());
                        try {
                            List<Inventory> inventories = inventoryFlow.getAllInventory();
                            Optional<Inventory> inventory = inventories.stream()
                                .filter(inv -> inv.getProduct().getProductId().equals(product.getProductId()))
                                .findFirst();
                            
                            data.setQuantity(inventory.map(inv -> inv.getQuantity().toString())
                                                    .orElse("0"));
                        } catch (Exception e) {
                            data.setQuantity("0");
                        }
                        
                        return data;
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    //The method makes separate queries for each product to get client and inventory information.
    //Consider using joins or batch fetching like below.
    // public List<ProductData> getAll() throws ApiException {
    //     List<Product> products = productFlow.getAll();
    //     List<Inventory> inventories = inventoryFlow.getAll();
    //     Map<Integer, Inventory> inventoryMap = inventories.stream()
    //         .collect(Collectors.toMap(
    //             inv -> inv.getProduct().getProductId(),
    //             inv -> inv
    //         ));
        
    //     return products.stream()
    //             .map(product -> {
    //                 try {
    //                     ProductData data = convertToData(product);
    //                     data.setClientName(product.getClient().getClientName()); // Client is already loaded due to @ManyToOne
    //                     data.setQuantity(Optional.ofNullable(inventoryMap.get(product.getProductId()))
    //                             .map(inv -> inv.getQuantity().toString())
    //                             .orElse("0"));
    //                     return data;
    //                 } catch (ApiException e) {
    //                     throw new RuntimeException(e);
    //                 }
    //             })
    //             .collect(Collectors.toList());
    // }

    public ProductData getProductById(Integer productId) throws ApiException {
        Product product = productFlow.getProductById(productId);
        ProductData data = ConversionClass.convert(product);
        Client client = clientFlow.getClientById(product.getClient().getClientId());
        data.setClientName(client.getClientName());
        data.setClientId(client.getClientId());
        try {
            Inventory inventory = inventoryFlow.getInventoryById(productId);
            data.setQuantity(inventory.getQuantity().toString());
        } catch (Exception e) {
            data.setQuantity("0");
        }
        
        return data;
    }

    // public List<ProductData> getByClient(Integer clientId) throws ApiException {
    //     return productFlow.getByClient(clientId)
    //             .stream()
    //             .map(this::convertToData)
    //             .collect(Collectors.toList());
    // }
    //instead of doing this, we can fetch all clients and then make the user select one
    //then we can fetch all products for that client and then make the user select one

    public void bulkAddProducts(List<ProductForm> forms) throws ApiException {
        if (forms.size() > 5000) {
            throw new ApiException(getPrefix() + "Cannot process more than 5000 products at once");
        }

        for (ProductForm form : forms) {
            normalize(form);
        }
        
        productFlow.bulkAddProducts(forms.stream()
                .map(form -> {
                    try {
                        Client client = clientFlow.getClientById(form.getClientId());
                        return ConversionClass.convert(form, client);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList()));
    }

    @Override
    protected String getPrefix() {
        return "Product: ";
    }

    private void normalize(ProductForm form) throws ApiException {
        super.normalize(form);
        // Special handling for image path - keep original case
        if (Objects.nonNull(form.getImagePath())) {
            String imagePath = form.getImagePath().trim();
            String lowerCasePath = imagePath.toLowerCase();
            if (!lowerCasePath.endsWith(".png") && 
                !lowerCasePath.endsWith(".jpg") && 
                !lowerCasePath.endsWith(".jpeg")) {
                throw new ApiException(getPrefix() + "Invalid image format. Only PNG, JPG, and JPEG files are allowed");
            }
            form.setImagePath(imagePath);
        }
    }
}
