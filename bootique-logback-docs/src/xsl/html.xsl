<?xml version="1.0" encoding="UTF-8"?>
<!--
	Licensed to the Apache Software Foundation (ASF) under one
	or more contributor license agreements.  See the NOTICE file
	distributed with this work for additional information
	regarding copyright ownership.  The ASF licenses this file
	to you under the Apache License, Version 2.0 (the
	"License"); you may not use this file except in compliance
	with the License.  You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an
	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	KIND, either express or implied.  See the License for the
	specific language governing permissions and limitations
	under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:d="http://docbook.org/ns/docbook">

    <xsl:import href="urn:docbkx:stylesheet"/>
    <xsl:include href="common-customizations.xsl"/>

    <!--<xsl:param name="highlight.source" select="1"/>-->
    <xsl:param name="html.stylesheet" select="'css/doc.css'"/>
    <xsl:param name="chunker.output.encoding">UTF-8</xsl:param>

    <!-- Only chapters start a new page -->
    <xsl:param name="chunk.section.depth">0</xsl:param>

    <!-- Don't add any embedded styles -->
    <xsl:param name="css.decoration">0</xsl:param>

    <xsl:param name="ignore.image.scaling">1</xsl:param>

    <xsl:param name="use.id.as.filename">1</xsl:param>

    <xsl:param name="navig.showtitles">1</xsl:param>

    <!-- make sure we are using "ul", not "dt" -->
    <xsl:param name="toc.list.type">ul</xsl:param>

    <!--
    Main doc structure ..
      Roughly following standard docbook subtemplate structure from profile-docbook.xsl,
      but actually using bootique.io site style...
    -->
    <xsl:template match="*" mode="process.root">
        <xsl:variable name="doc" select="self::*"/>

        <html>
            <head>
                <xsl:call-template name="user.head.content">
                    <xsl:with-param name="node" select="$doc"/>
                </xsl:call-template>
            </head>
            <body data-spy="scroll" data-target=".bs-docs-sidebar" data-offset="100" onload="prettyPrint()">
                <xsl:call-template name="user.header.content">
                    <xsl:with-param name="node" select="$doc"/>
                </xsl:call-template>

                <div class="container content docbook">

                    <!-- Title -->
                    <xsl:call-template name="book.titlepage"/>

                    <div class="pure-g">

                        <!-- Main Content -->
                        <div class="docs-main pure-u-sm-16-24">
                            <xsl:apply-templates select="*"/>
                        </div>

                        <!-- TOC -->
                        <div class="docs-side offset-sm-1-12 pure-u-sm-6-24">
                            <xsl:variable name="toc.params">
                                <xsl:call-template name="find.path.params">
                                    <xsl:with-param name="table" select="normalize-space($generate.toc)"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <xsl:call-template name="division.toc"/>
                        </div>
                    </div>
                </div>

                <xsl:call-template name="user.footer.content">
                    <xsl:with-param name="node" select="$doc"/>
                </xsl:call-template>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="user.head.content">
        <xsl:param name="node" select="."/>
        <xsl:param name="title">
            <xsl:apply-templates select="$node" mode="object.title.markup.textonly"/>
        </xsl:param>

        <title>
            <xsl:copy-of select="$title"/>
        </title>

        <meta name="keywords" content="Bootique Logback Documentation version ${bootique.version.major} "/>
        <meta name="description"
              content="Bootique Logback: integration of Logback logging framework with Bootique - Documentation version ${bootique.version.major}"/>

        <!-- viewport -->
        <meta name="viewport" content="width=device-width, initial-scale=1"/>

        <!-- favicon -->
        <link rel="apple-touch-icon" sizes="57x57" href="../../../images/favicon/apple-touch-icon-57x57.png"/>
        <link rel="apple-touch-icon" sizes="60x60" href="../../../images/favicon/apple-touch-icon-60x60.png"/>
        <link rel="apple-touch-icon" sizes="72x72" href="../../../images/favicon/apple-touch-icon-72x72.png"/>
        <link rel="apple-touch-icon" sizes="76x76" href="../../../images/favicon/apple-touch-icon-76x76.png"/>
        <link rel="apple-touch-icon" sizes="114x114" href="../../../images/favicon/apple-touch-icon-114x114.png"/>
        <link rel="apple-touch-icon" sizes="120x120" href="../../../images/favicon/apple-touch-icon-120x120.png"/>
        <link rel="apple-touch-icon" sizes="144x144" href="../../../images/favicon/apple-touch-icon-144x144.png"/>
        <link rel="apple-touch-icon" sizes="152x152" href="../../../images/favicon/apple-touch-icon-152x152.png"/>
        <link rel="apple-touch-icon" sizes="180x180" href="../../../images/favicon/apple-touch-icon-180x180.png"/>
        <link rel="icon" type="image/png" href="../../../images/favicon/favicon-32x32.png" sizes="32x32"/>
        <link rel="icon" type="image/png" href="../../../images/favicon/favicon-194x194.png" sizes="194x194"/>
        <link rel="icon" type="image/png" href="../../../images/favicon/favicon-96x96.png" sizes="96x96"/>
        <link rel="icon" type="image/png" href="../../../images/favicon/android-chrome-192x192.png" sizes="192x192"/>
        <link rel="icon" type="image/png" href="../../../images/favicon/favicon-16x16.png" sizes="16x16"/>
        <link rel="manifest" href="../../../images/favicon/manifest.json"/>
        <link rel="mask-icon" href="../../../images/favicon/safari-pinned-tab.svg" color="#5bbad5"/>
        <link rel="shortcut icon" href="../../../images/favicon/favicon.ico"/>
        <meta name="msapplication-TileColor" content="#da532c"/>
        <meta name="msapplication-TileImage" content="../../../images/favicon/mstile-144x144.png"/>
        <meta name="msapplication-config" content="../../../images/favicon/browserconfig.xml"/>
        <meta name="theme-color" content="#ffffff"/>

        <link rel="stylesheet" href="../../../styles/main.css"/>
        <script src="../../../scripts/vendor/modernizr.js"></script>

    </xsl:template>

    <xsl:template name="user.header.content">
        <div id="top-nav">
            <!-- Menu toggle -->
            <a href="#menu" id="menuLink" aria-label="Menu" class="menu-link">
                <!-- Hamburger icon -->
                <span></span>
            </a>
            <div id="menu" class="nav-menu pure-menu pure-menu-horizontal pure-menu-fixed">
                <!-- Menu Logo -->
                <a href="http://bootique.io/" class="nav-brand center">
                    <img src="../../../images/logo.svg" alt="Bootique"/>
                </a>

                <div class="container nav">
                    <div class="valign-wrapper-sm">
                        <div class="valign-sm">
                            <ul class="pure-menu-list pull-left">
                                <li class="pure-menu-item">
                                    <a href="http://bootique.io/docs/0/getting-started/" class="pure-menu-link">Get
                                        started
                                    </a>
                                </li>
                                <li class="pure-menu-item pure-menu-selected">
                                    <a href="http://bootique.io/docs/" class="pure-menu-link">Documentation</a>
                                </li>
                                <li class="pure-menu-item">
                                    <a href="http://groups.google.com/forum/#!forum/bootique-user"
                                       class="pure-menu-link">Forum
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <ul class="pure-menu-list pull-right">
                        <li class="pure-menu-item gh-btns" data-gh-owner="nhl" data-gh-repo="bootique">
                            <a href="http://github.com/bootique/bootique" class="pure-menu-link github-btn fork">
                                <i class="fa fa-github"></i>
                                <span>Code</span>
                            </a>
                            <a href="http://github.com/bootique/bootique/stargazers"
                               class="pure-menu-link github-btn star">
                                <i class="fa fa-github"></i>
                                <i class="fa fa-star"></i>
                                <span class="star-counter"></span>
                            </a>
                        </li>
                        <li class="pure-menu-item tw-btn">
                            <a href="http://twitter.com/intent/follow?screen_name=BootiqueProject"
                               class="pure-menu-link tw-btn">
                                <i class="fa fa-twitter"></i>
                                <span>Follow</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="user.footer.content">
        <p class="footer-p">&#169; <span class="current-year">2016</span> NHL, ObjectStyle and individual authors
        </p>

        <script src="../../../scripts/vendor.js"></script>
        <script src="../../../scripts/main.js"></script>
        <script type="text/javascript">
            (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

            ga('create', 'UA-73654436-1', 'auto');
            ga('send', 'pageview');
        </script>
    </xsl:template>

    <!-- called from division.toc template... -->
    <xsl:template name="make.toc">
        <xsl:param name="toc-context" select="."/>
        <xsl:param name="toc.title.p" select="true()"/>
        <xsl:param name="nodes" select="/NOT-AN-ELEMENT"/>

        <xsl:variable name="nodes.plus" select="$nodes | qandaset"/>

        <xsl:variable name="toc.title">
            <xsl:if test="$toc.title.p">
                <h2 class="toc-title">
                    <xsl:call-template name="gentext">
                        <xsl:with-param name="key">TableofContents</xsl:with-param>
                    </xsl:call-template>
                </h2>
            </xsl:if>
        </xsl:variable>

        <xsl:if test="$nodes">

            <nav class="bs-docs-sidebar">

                <xsl:copy-of select="$toc.title"/>
                <ul class="nav bs-docs-sidenav">
                    <xsl:apply-templates select="$nodes" mode="toc">
                        <xsl:with-param name="toc-context" select="$toc-context"/>
                    </xsl:apply-templates>
                </ul>
            </nav>
        </xsl:if>

    </xsl:template>

    <xsl:template name="book.titlepage">
        <div class="titlepage">
            <xsl:choose>
                <xsl:when test="d:bookinfo/d:title">
                    <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="d:bookinfo/d:title"/>
                </xsl:when>
                <xsl:when test="d:info/d:title">
                    <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="d:info/d:title"/>
                </xsl:when>
                <xsl:when test="d:title">
                    <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="d:title"/>
                </xsl:when>
            </xsl:choose>
            <xsl:call-template name="book.titlepage.separator"/>
        </div>
    </xsl:template>

</xsl:stylesheet>
