<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:param name="keep.relative.image.uris" select="1"/>
	<xsl:param name="toc.section.depth">1</xsl:param>

	<!-- control which TOC's are generated -->
	<xsl:param name="generate.toc">
		appendix  nop
		book      toc,title
		chapter   nop
		part      nop
		preface   nop
		qandadiv  nop
		qandaset  nop
		reference nop
		section   nop
		set       nop
	</xsl:param>


</xsl:stylesheet>
