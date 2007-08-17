package org.apache.maven.doxia.module.xdoc;

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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id:XdocParserTest.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class XdocParserTest
    extends AbstractParserTest
{
    private XdocParser parser;

    /** @see junit.framework.TestCase#setUp() */
    protected void setUp()
        throws Exception
    {
        super.setUp();

        parser = (XdocParser) lookup( Parser.ROLE, "xdoc" );
    }

    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xml";
    }

    /** {@inheritDoc} */
    protected Parser createParser()
    {
        return parser;
    }

    /** @throws Exception  */
    public void testSnippetMacro()
        throws Exception
    {
        Writer output = null;
        Reader reader = null;

        try
        {
            output = getTestWriter( "macro" );
            reader = getTestReader( "macro" );

            Sink sink = new XdocSink( output );
            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        File f = getTestFile( getBasedir(), outputBaseDir() + getOutputDir() + "macro.xml" );
        assertTrue( "The file " + f.getAbsolutePath() + " was not created", f.exists() );

        String content;
        try
        {
            reader = new FileReader( f );
            content = IOUtil.toString( reader );
        }
        finally
        {
            IOUtil.close( reader );
        }

        assertTrue( content.indexOf( "&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;" ) != -1 );
    }

    /** @throws Exception  */
    public void testTocMacro()
        throws Exception
    {
        Writer output = null;
        Reader reader = null;

        try
        {
            output = getTestWriter( "toc" );
            reader = getTestReader( "toc" );

            Sink sink = new XdocSink( output );
            createParser().parse( reader, sink );
        }
        finally
        {
            IOUtil.close( output );
            IOUtil.close( reader );
        }

        File f = getTestFile( getBasedir(), outputBaseDir() + getOutputDir() + "toc.xml" );
        assertTrue( "The file " + f.getAbsolutePath() + " was not created", f.exists() );

        String content;
        try
        {
            reader = new FileReader( f );
            content = IOUtil.toString( reader );
        }
        finally
        {
            IOUtil.close( reader );
        }

        // No section, only subsection 1 and 2
        assertTrue( noNewLine( content ).indexOf( "<a href=\"#Section_11\">Section 11</a>" ) != -1 );
        assertTrue( noNewLine( content ).indexOf( "<a href=\"#Section_1211\">Section 1211</a>" ) == -1 );
    }

    /**
     * TODO move me!
     *
     * @param text
     * @return
     */
    private String noNewLine( String text )
    {
        String EOL = System.getProperty( "line.separator" );
        return text.replaceAll( EOL, "" );
    }
}