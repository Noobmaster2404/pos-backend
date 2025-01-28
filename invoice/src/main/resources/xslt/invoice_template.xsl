<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <!-- Declare the parameter -->
    <xsl:param name="base-dir"/>
    
    <xsl:template match="invoice">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21.0cm">
                    <fo:region-body margin="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <!-- Logo -->
                    <fo:block space-after="1cm">
                        <fo:external-graphic src="url('file:{$base-dir}/logo.png')"
                                           content-height="2cm"
                                           content-width="scale-to-fit"
                                           scaling="uniform"/>
                    </fo:block>
                    
                    <!-- Main Heading -->
                    <fo:block font-size="24pt" font-family="Helvetica" font-weight="bold" text-align="center" color="#2c3e50">
                        POINT OF SALE
                    </fo:block>
                    <fo:block font-size="20pt" font-family="Helvetica" font-weight="bold" text-align="center" color="#2c3e50" space-after="1cm">
                        INVOICE
                    </fo:block>
                    
                    <!-- Order Details -->
                    <fo:block font-size="12pt" font-family="Helvetica" space-after="0.5cm">
                        Order ID: <fo:inline font-weight="bold">#<xsl:value-of select="order-id"/></fo:inline>
                    </fo:block>
                    
                    <fo:block font-size="12pt" font-family="Helvetica" space-after="1cm">
                        Date: <fo:inline font-weight="bold">
                            <xsl:value-of select="order-date"/>
                        </fo:inline>
                    </fo:block>
                    
                    <!-- Items Table -->
                    <fo:table table-layout="fixed" width="100%" border-collapse="separate" space-after="1cm">
                        <fo:table-column column-width="30%"/>
                        <fo:table-column column-width="20%"/>
                        <fo:table-column column-width="15%"/>
                        <fo:table-column column-width="15%"/>
                        <fo:table-column column-width="20%"/>
                        
                        <!-- Table Header -->
                        <fo:table-header>
                            <fo:table-row font-weight="bold" background-color="#2c3e50" color="white">
                                <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                    <fo:block font-family="Helvetica">Product</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                    <fo:block font-family="Helvetica">Barcode</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                    <fo:block font-family="Helvetica">Quantity</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                    <fo:block font-family="Helvetica">Price</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                    <fo:block font-family="Helvetica">Total</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        
                        <!-- Table Body -->
                        <fo:table-body>
                            <xsl:for-each select="items/item">
                                <fo:table-row>
                                    <xsl:if test="position() mod 2 = 0">
                                        <xsl:attribute name="background-color">#f5f6fa</xsl:attribute>
                                    </xsl:if>
                                    <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                        <fo:block font-family="Helvetica"><xsl:value-of select="product-name"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                        <fo:block font-family="Helvetica"><xsl:value-of select="barcode"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                        <fo:block font-family="Helvetica"><xsl:value-of select="quantity"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                        <fo:block font-family="Helvetica">Rs. <xsl:value-of select="format-number(price, '#,##0.00')"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid #bdc3c7" padding="6pt">
                                        <fo:block font-family="Helvetica">Rs. <xsl:value-of select="format-number(total, '#,##0.00')"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    
                    <!-- Total Amount -->
                    <fo:block font-size="14pt" font-family="Helvetica" font-weight="bold" text-align="right" space-before="1cm" color="#2c3e50">
                        Total Amount: Rs. <xsl:value-of select="format-number(total, '#,##0.00')"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet> 