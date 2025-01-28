package com.increff.invoice.service;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.Fop;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderItemData;
import org.springframework.beans.factory.annotation.Value;

@Service
public class InvoiceGenerator {

    private FopFactory fopFactory;
    private TransformerFactory transformerFactory;

    @Value("${server.baseDir}")
    private String baseDir;

    @PostConstruct
    public void init() throws IOException {
        // Create FOP factory with base URI pointing to server resources
        File baseFile = new File(baseDir);
        if (!baseFile.exists()) {
            throw new IOException("Base directory not found: " + baseDir);
        }
        URI baseUri = baseFile.toURI();
        fopFactory = FopFactory.newInstance(baseUri);
        transformerFactory = TransformerFactory.newInstance();
    }

    public byte[] generatePDF(OrderData order) throws Exception {
        // Load XSLT
        Source xsltSource = new StreamSource(getClass().getResourceAsStream("/xslt/invoice_template.xsl"));
        
        // Create transformer
        Transformer transformer = transformerFactory.newTransformer(xsltSource);
        
        // Set base directory parameter with proper URI format
        transformer.setParameter("base-dir", new File(baseDir).toURI().toString());
        
        // Generate XML
        String xml = generateXML(order);
        Source xmlSource = new StreamSource(new StringReader(xml));
        
        // Create PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
        Result result = new SAXResult(fop.getDefaultHandler());
        
        // Transform
        transformer.transform(xmlSource, result);
        
        return out.toByteArray();
    }

    private String generateXML(OrderData order) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<invoice>");
        xml.append("<order-id>").append(order.getOrderId()).append("</order-id>");
        
        // Format the date in Java
        String formattedDate = order.getOrderTime()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss 'IST'"));
        xml.append("<order-date>").append(formattedDate).append("</order-date>");
        
        xml.append("<total>").append(order.getOrderTotal()).append("</total>");
        
        xml.append("<items>");
        for (OrderItemData item : order.getOrderItems()) {
            xml.append("<item>");
            xml.append("<product-name>").append(item.getProductName()).append("</product-name>");
            xml.append("<barcode>").append(item.getBarcode()).append("</barcode>");
            xml.append("<quantity>").append(item.getQuantity()).append("</quantity>");
            xml.append("<price>").append(item.getSellingPrice()).append("</price>");
            xml.append("<total>").append(item.getItemTotal()).append("</total>");
            xml.append("</item>");
        }
        xml.append("</items>");
        xml.append("</invoice>");
        
        return xml.toString();
    }
} 