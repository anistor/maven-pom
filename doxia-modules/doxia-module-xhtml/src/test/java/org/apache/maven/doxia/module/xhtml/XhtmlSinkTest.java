package org.apache.maven.doxia.module.xhtml;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.Sink;

import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;

/**
 * @author Jason van Zyl
 * @version $Id:XhtmlSinkTest.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class XhtmlSinkTest
    extends AbstractSinkTest
{
    protected String outputExtension()
    {
        return "xhtml";
    }

    protected Sink createSink( Writer writer )
    {
        String apt = "test.apt";

        RenderingContext renderingContext =
            new RenderingContext( getBasedirFile(), new File( getBasedirFile(), apt ).getPath(), "apt" );

        //PLXAPI: This horrible fake map is being used because someone neutered the directives approach in the
        // site renderer so that it half worked. Put it back and make it work properly.

        return new XhtmlSink( writer, renderingContext, new FakeMap() );
    }

    public void testLinks()
        throws Exception
    {
        Writer writer = getTestWriter( "links" );
        XhtmlSink sink = (XhtmlSink) createSink( writer );
        sink.link( "http:/www.xdoc.com" );
        sink.link_();
        sink.link( "./index.html#anchor" );
        sink.link_();
        sink.link( "../index.html#anchor" );
        sink.link_();
        sink.link( "index.html" );
        sink.link_();
        sink.close();
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "<title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return author;
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "<div class=\"section\"><h2>" + title + "</h2></div>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "<div class=\"section\"><h3>" + title + "</h3></div>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "<div class=\"section\"><h4>" + title + "</h4></div>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "<div class=\"section\"><h5>" + title + "</h5></div>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "<div class=\"section\"><h6>" + title + "</h6></div>";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "<ul><li>" + item + "</li></ul>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "<ol type=\"i\"><li>" + item + "</li></ol>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<dl><dt>" + definum + "</dt><dd>" + definition + "</dd></dl>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return "<img src=\"" + source + "\" alt=\"" + caption + "\" />";
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "<table class=\"bodyTable\"><tbody><tr class=\"a\"><td align=\"center\">"
            + cell + "</td></tr></tbody><caption>" + caption + "</caption></table>";
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return "<p>" + text + "</p>";
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "<div class=\"source\"><pre>" + text + "</pre></div>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "<hr />";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "<a name=\"" + anchor + "\">" + anchor + "</a>";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return "<a href=\"#" + link + "\">" + text + "</a>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "<i>" + text + "</i>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "<b>" + text + "</b>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "<tt>" + text + "</tt>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<br />";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "&#160;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: need to be able to retreive those from outside the sink
        return "~, =, -, +, *, [, ], &lt;, &gt;, {, }, \\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return text;
    }


    class FakeMap
        extends HashMap
    {
        public Object get( Object key )
        {
            return "fake";
        }
    }
}