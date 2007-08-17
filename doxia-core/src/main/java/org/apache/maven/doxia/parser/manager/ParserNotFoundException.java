package org.apache.maven.doxia.parser.manager;

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

/**
 * Encapsulate a Doxia exception that indicates that a parser
 * does not exist or could not be found.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:ParserNotFoundException.java 348605 2005-11-24 12:02:44 +1100 (Thu, 24 Nov 2005) brett $
 */
public class ParserNotFoundException
    extends Exception
{
    /**
     * Construct a new ParserNotFoundException with the specified detail message.
     *
     * @param message The detailed message.
     * This can later be retrieved by the Throwable.getMessage() method.
     */
    public ParserNotFoundException( String message )
    {
        super( message );
    }

    /**
     * Constructs a new exception with the specified cause. The error
     * message is (cause == null ? null : cause.toString() ).
     *
     * @param cause the cause. This can be retrieved later by the
     * Throwable.getCause() method. (A null value is permitted, and indicates
     * that the cause is nonexistent or unknown.)
     */
    public ParserNotFoundException( Throwable cause )
    {
        super( cause );
    }

    /**
     * Construct a new ParserNotFoundException with the specified
     * detail message and cause.
     *
     * @param message The detailed message.
     * This can later be retrieved by the Throwable.getMessage() method.
     * @param cause the cause. This can be retrieved later by the
     * Throwable.getCause() method. (A null value is permitted, and indicates
     * that the cause is nonexistent or unknown.)
     */
    public ParserNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}