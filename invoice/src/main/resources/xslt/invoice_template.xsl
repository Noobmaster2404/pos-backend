<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="invoice">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21.0cm">
                    <fo:region-body margin="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            
            <fo:page-sequence master-reference="A4">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="24pt" font-weight="bold" text-align="center" space-after="1cm">
                        INVOICE
                    </fo:block>
                    
                    <fo:block font-size="12pt" space-after="0.5cm">
                        Order ID: <xsl:value-of select="order-id"/>
                    </fo:block>
                    
                    <fo:block font-size="12pt" space-after="1cm">
                        Date: <xsl:value-of select="order-date"/>
                    </fo:block>
                    
                    <fo:table table-layout="fixed" width="100%" border-collapse="separate">
                        <fo:table-column column-width="30%"/>
                        <fo:table-column column-width="20%"/>
                        <fo:table-column column-width="15%"/>
                        <fo:table-column column-width="15%"/>
                        <fo:table-column column-width="20%"/>
                        
                        <fo:table-header>
                            <fo:table-row font-weight="bold">
                                <fo:table-cell border="1pt solid black" padding="2pt">
                                    <fo:block>Product</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid black" padding="2pt">
                                    <fo:block>Barcode</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid black" padding="2pt">
                                    <fo:block>Quantity</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid black" padding="2pt">
                                    <fo:block>Price</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="1pt solid black" padding="2pt">
                                    <fo:block>Total</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        
                        <fo:table-body>
                            <xsl:for-each select="items/item">
                                <fo:table-row>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block><xsl:value-of select="product-name"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block><xsl:value-of select="barcode"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block><xsl:value-of select="quantity"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block><xsl:value-of select="price"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1pt solid black" padding="2pt">
                                        <fo:block><xsl:value-of select="total"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    
                    <fo:block font-size="12pt" font-weight="bold" text-align="right" space-before="1cm">
                        Total Amount: <xsl:value-of select="total"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet> 