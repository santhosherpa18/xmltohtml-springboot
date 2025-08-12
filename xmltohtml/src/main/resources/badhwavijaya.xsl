<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/body">
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title><xsl:value-of select="document/@title"/></title>
                <link rel="stylesheet" type="text/css" href="style.css"/>
            </head>
            <body>
                <main>
                    <xsl:apply-templates select="document"/>
                </main>
            </body>
        </html>
    </xsl:template>

    <!-- DOCUMENT -->
    <xsl:template match="document">
        <h1 class="site-title"><xsl:value-of select="@title"/></h1>
        <xsl:apply-templates select="chapter"/>
    </xsl:template>

    <!-- CHAPTER -->
    <xsl:template match="chapter">
        <section class="chapter">
            <h2 class="chp-title">
                <xsl:value-of select="chapter-heading"/>
            </h2>
            <xsl:if test="introduction[not(em[@type='shloka'])]">
                <h3 class="intro-subtitle">
                    <xsl:value-of select="introduction"/>
                </h3>
            </xsl:if>
            <xsl:apply-templates select="introduction[em[@type='shloka']] | shloka | author-note"/>
        </section>
    </xsl:template>

    <xsl:template match="introduction[em[@type='shloka']]">
        <div class="shloka">
            <xsl:for-each select="em[@type='shloka']">
                <p><xsl:apply-templates/></p>
            </xsl:for-each>
        </div>
    </xsl:template>

    <!-- SHLOKA -->
    <xsl:template match="shloka">
        <div class="shloka">
            <p><xsl:apply-templates/></p>
        </div>
    </xsl:template>

    <!-- AUTHOR NOTE -->
    <xsl:template match="author-note">
        <div class="author-note">
            <p><xsl:apply-templates/></p>
        </div>
    </xsl:template>

    <!-- EMPHASIS / ITALIC TEXT -->
    <xsl:template match="em">
        <span style="font-style: italic;"><xsl:apply-templates/></span>
    </xsl:template>

    <!-- LINE BREAKS -->
    <xsl:template match="br">
        <br/>
    </xsl:template>

</xsl:stylesheet>
