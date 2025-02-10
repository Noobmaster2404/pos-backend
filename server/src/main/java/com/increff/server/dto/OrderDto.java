package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.util.Objects;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;

import com.increff.server.flow.OrderFlow;
import com.increff.server.helper.ConversionHelper;
import com.increff.server.api.ProductApi;
import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Order;
import com.increff.server.entity.OrderItem;
import com.increff.server.entity.Product;
import com.increff.commons.model.OrderItemForm;
import com.increff.commons.model.OrderSearchForm;
import com.increff.commons.util.TimeZoneUtil;
import com.increff.commons.model.PaginatedData;
import com.increff.server.api.OrderApi;
import com.increff.server.api.OrderItemApi;
import com.increff.invoice.dto.InvoiceDto;

@Service
public class OrderDto extends AbstractDto {
    
    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private OrderApi orderApi;
    
    @Autowired
    private ProductApi productApi;

    @Autowired
    private OrderItemApi orderItemApi;

    @Autowired
    private InvoiceDto invoiceDto;

    @Value("${PAGE_SIZE}")
    private Integer PAGE_SIZE;

    @Value("${invoice.storage.path}")
    private String invoiceStoragePath;

    public OrderData addOrder(OrderForm form) throws ApiException {
        checkValid(form);
        normalize(form);

        for (OrderItemForm itemForm : form.getOrderItems()) {
            normalize(itemForm);
            checkValid(itemForm);
        }
        
        List<String> barcodes = form.getOrderItems().stream()
            .map(OrderItemForm::getBarcode)
            .distinct()
            .collect(Collectors.toList());

        List<Product> products = productApi.getCheckProductsByBarcodes(barcodes);
        Map<String, Product> barcodeToProduct = products.stream()
            .collect(Collectors.toMap(Product::getBarcode, product -> product));

        Order order = ConversionHelper.convertToOrder(form, barcodeToProduct);
        Order createdOrder = orderFlow.addOrder(order);
        List<OrderItem> orderItems = orderItemApi.getOrderItemsByOrderId(createdOrder.getOrderId());

        return ConversionHelper.convertToOrderData(createdOrder, orderItems, products);
    }

    public OrderData getOrder(Integer orderId) throws ApiException {
        Order order = orderFlow.getOrderById(orderId);
        List<OrderItem> orderItems = orderItemApi.getOrderItemsByOrderId(orderId);

        List<Integer> productIds = orderItems.stream()
            .map(item -> item.getProduct().getProductId())
            .collect(Collectors.toList());
        List<Product> products = productApi.getCheckProductsByIds(productIds);
 
        return ConversionHelper.convertToOrderData(order, orderItems, products);
    }

    public PaginatedData<OrderData> getOrdersByDateRange(OrderSearchForm form, Integer page) throws ApiException {
        checkValid(form);
        ZonedDateTime startDate = TimeZoneUtil.getStartOfDay(form.getStartDate());
        ZonedDateTime endDate = TimeZoneUtil.getEndOfDay(form.getEndDate());
        List<Order> orders = orderFlow.getOrdersByDateRange(startDate, endDate, page);

        List<Integer> orderIds = orders.stream()
            .map(Order::getOrderId)
            .collect(Collectors.toList());
            
        Map<Integer, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Integer orderId : orderIds) {
            orderItemsMap.put(orderId, orderItemApi.getOrderItemsByOrderId(orderId));
        }

        List<Integer> productIds = orderItemsMap.values().stream()
            .flatMap(List::stream)
            .map(item -> item.getProduct().getProductId())
            .distinct()
            .collect(Collectors.toList());
            
        List<Product> products = productApi.getCheckProductsByIds(productIds);
        Map<Integer, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getProductId, product -> product));
        List<OrderData> orderDataList = ConversionHelper.convertToOrderData(orders, orderItemsMap, productMap);

        return new PaginatedData<>(orderDataList, page, PAGE_SIZE);
    }


    public ResponseEntity<Resource> downloadInvoice(Integer orderId) throws ApiException {
        Order order = orderFlow.getOrderById(orderId);
        
        if (order.getInvoiceGenerated() == false || Objects.isNull(order.getInvoicePath())) {
            throw new ApiException("Invoice not yet generated for order: " + orderId);
        }

        try {
            Path path = Paths.get(order.getInvoicePath());
            Resource resource = new UrlResource(path.toUri());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"invoice_" + orderId + ".pdf\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new ApiException("Error downloading invoice: " + e.getMessage());
        }
    }

    public String generateInvoice(Integer orderId) throws ApiException {
        Order order = orderFlow.getOrderById(orderId);
        if(order.getInvoiceGenerated() == true) {
            return order.getInvoicePath();
        }

        List<OrderItem> orderItems = orderItemApi.getOrderItemsByOrderId(orderId);

        List<Integer> productIds = orderItems.stream()
            .map(item -> item.getProduct().getProductId())
            .collect(Collectors.toList());
        List<Product> products = productApi.getCheckProductsByIds(productIds);

        OrderData orderData = ConversionHelper.convertToOrderData(order, orderItems, products);
        String invoicePath = null;
        try {
            invoicePath = invoiceDto.generateInvoice(orderData);
            
            // Only update the order if invoice generation was successful
            order.setInvoicePath(invoicePath);
            order.setInvoiceGenerated(true);
            orderApi.updateOrder(order);
            
            return invoicePath;
        } catch (Exception e) {
            throw new ApiException("Error generating invoice: " + e.getMessage());
        }
    }
} 