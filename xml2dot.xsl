<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:output method = "text" />

	<xsl:template match="graph">
      digraph "<xsl:value-of select="@name" />" {
       <xsl:apply-templates />
      }
	</xsl:template>

	<xsl:template match="attributes">
	 <xsl:value-of select="@type" /> [  
       <xsl:apply-templates select="attribute"/>
      ];
	</xsl:template>
	
	<xsl:template match="attribute">
	 "<xsl:value-of select="@name" />"="<xsl:value-of select="@value" />",
	</xsl:template>
		
	<xsl:template match="node">
	   "<xsl:value-of select="@id" />";
	</xsl:template>
	
	<xsl:template match="edge">
	  "<xsl:value-of select="@from" />" -> "<xsl:value-of select="@to" />";
	</xsl:template>

	<xsl:template match="subgraph">
  	 subgraph 
  	   <if select="@numcluster">
  	             "cluster:<xsl:value-of select="@numcluster" />"
  	          </if>
  	   {
  	     "label"="<xsl:value-of select="@label" />";
         <xsl:apply-templates />  	     
       }
	</xsl:template>
   
</xsl:stylesheet>