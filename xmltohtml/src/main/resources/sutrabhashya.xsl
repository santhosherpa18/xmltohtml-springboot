<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/body">
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title>Brahma Sutra Bhashya</title>
                <link rel="stylesheet" type="text/css" href="style.css"/>
            </head>
            <body>
                <main>
                    <xsl:apply-templates select="document"/>
                </main>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="document">
        <h1 class="site-title"><xsl:value-of select="@title"/></h1>
        <xsl:apply-templates select="adhyaya"/>
    </xsl:template>

    <xsl:template match="adhyaya">
        <h2 class="chp-title"><xsl:value-of select="adhyaya-heading"/></h2>
        <xsl:apply-templates select="pada"/>
    </xsl:template>


    <xsl:template match="introduction">
        <xsl:choose>

            <!-- If it contains a shloka -->
            <xsl:when test="em[@type='shloka']">
                <div class="shloka">
                    <xsl:for-each select="em[@type='shloka']">
                        <p><xsl:apply-templates/></p>
                    </xsl:for-each>
                </div>
            </xsl:when>

            <!-- If it is regular text (no em/shloka) -->
            <xsl:otherwise>
                <div class="introduction">
                    <p><xsl:apply-templates/></p>
                </div>
            </xsl:otherwise>

        </xsl:choose>
    </xsl:template>


    <xsl:template match="sutra">
        <div class="sutra subtitle"><xsl:value-of select="."/></div>
    </xsl:template>

    <xsl:template match="bhashya">
        <div class="bhashya"><xsl:apply-templates/></div>
    </xsl:template>

    <xsl:template match="em">
        <span style="font-style: italic;"><xsl:value-of select="."/></span>
    </xsl:template>

    <xsl:template match="br">
        <br/>
    </xsl:template>

</xsl:stylesheet>
