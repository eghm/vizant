<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:output method = "xml" />

	<xsl:template match="graph">
       <TOUCHGRAPH_LB version="1.20">
         <NODESET>	
          <xsl:apply-templates select="node"/>
	    </NODESET>	  
        <EDGESET>	
          <xsl:apply-templates select="edge" />
	    </EDGESET>	  
        <PARAMETERS>
          <PARAM name="offsetX" value="627"/>
          <PARAM name="rotateSB" value="0"/>
          <PARAM name="zoomSB" value="-7"/>
          <PARAM name="offsetY" value="19"/>
        </PARAMETERS>
      </TOUCHGRAPH_LB>        
	</xsl:template>
<!--
	<xsl:template match="attributes">
	 <h3><xsl:value-of select="@type" /></h3> 
       <xsl:apply-templates select="attribute"/>
	</xsl:template>
	
	<xsl:template match="attribute">
	  <xsl:value-of select="@name" />"="<xsl:value-of select="@value" /><br />
	</xsl:template>
-->		
	<xsl:template match="node">
      
        <NODE>
             <xsl:attribute name="nodeID"><xsl:value-of select="@id"/></xsl:attribute>        
            <NODE_LOCATION x="534" y="87" visible="true"/>
            <NODE_LABEL shape="2" backColor="00A0F0" textColor="FFFFFF" fontSize="18">
              <xsl:attribute name="label"><xsl:value-of select="@id"/></xsl:attribute>
            </NODE_LABEL>
            
            <NODE_URL url="" urlIsLocal="false" urlIsXML="false"/>
            <NODE_HINT width="150" height="-1" isHTML="false"><!-- the description -->
              <xsl:attribute name="hint"><xsl:value-of select="@id"/></xsl:attribute>
            </NODE_HINT>
        </NODE>      
	</xsl:template>
	
	<xsl:template match="edge">
      <EDGE type="6" length="80" visible="true" color="A0A0A0">
         <xsl:attribute name="fromID"><xsl:value-of select="@from"/></xsl:attribute>
         <xsl:attribute name="toID"><xsl:value-of select="@to"/></xsl:attribute>
      </EDGE>		
	</xsl:template>

	<xsl:template match="subgraph">
        <NODE>
             <xsl:attribute name="nodeID"><xsl:value-of select="@label"/></xsl:attribute>        
            <NODE_LOCATION x="534" y="87" visible="true"/>
            <NODE_LABEL shape="2" backColor="00A0F0" textColor="FFFFFF" fontSize="18">
              <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
            </NODE_LABEL>
            
            <NODE_URL url="" urlIsLocal="false" urlIsXML="false"/>
            <NODE_HINT width="150" height="-1" isHTML="false"><!-- the description -->
              <xsl:attribute name="hint">subgraph:cluster:<xsl:value-of select="@numcluster"/>:<xsl:value-of select="@label"/></xsl:attribute>
            </NODE_HINT>
        </NODE>        	    
        <!-- <xsl:apply-templates /> --> 	     
	</xsl:template>
   
</xsl:stylesheet>