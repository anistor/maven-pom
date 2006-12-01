package org.apache.continuum.web.test;

/*
 * Copyright 2005-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.shared.web.test.XPathExpressionUtil;

public class AccountSecurityTest
    extends AbstractAuthenticatedAccessTestCase
{
    public final String SIMPLE_POM = getBasedir() + "/target/test-classes/unit/simple-project/pom.xml";
    
    // create user fields
    public static final String CREATE_FORM_USERNAME_FIELD = "userCreateForm_user_username";    
    
    public static final String CREATE_FORM_FULLNAME_FIELD = "userCreateForm_user_fullName";

    public static final String CREATE_FORM_EMAILADD_FIELD = "userCreateForm_user_email";

    public static final String CREATE_FORM_PASSWORD_FIELD = "userCreateForm_user_password";

    public static final String CREATE_FORM_CONFIRM_PASSWORD_FIELD = "userCreateForm_user_confirmPassword";

    public static final String PASSWORD_FIELD = "user.password";

    public static final String CONFIRM_PASSWORD_FIELD = "user.confirmPassword";

    // user account 1
    public static final String CUSTOM_USERNAME = "custom";

    public static final String CUSTOM_USERNAME2 = "custom2";

    public static final String CUSTOM_USERNAME3 = "custom3";

    public static final String CUSTOM_USERNAME4 = "custom4";

    public static final String CUSTOM_FULLNAME = "custom fullname";

    public static final String CUSTOM_EMAILADD = "custom@custom.com";

    public static final String CUSTOM_PASSWORD = "custompassword";

    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    public String getUsername()
    {
        return super.adminUsername;
    }

    public String getPassword()
    {
        return super.adminPassword;
    }

    public void testBasicUserAddDelete()
    {
        createUser( CUSTOM_USERNAME, CUSTOM_FULLNAME, CUSTOM_EMAILADD, CUSTOM_PASSWORD, true );

        // delete custom user
        deleteUser( CUSTOM_USERNAME, CUSTOM_FULLNAME, CUSTOM_EMAILADD );
        super.logout();
    }

    public void testPasswordConfirmation()
        throws Exception
    {
        // initial user account creation ignores the password creation checks
        createUser( CUSTOM_USERNAME2, CUSTOM_FULLNAME, CUSTOM_EMAILADD, CUSTOM_PASSWORD, true );
        super.logout();

        // start password creation validation test
        super.login( CUSTOM_USERNAME2, CUSTOM_PASSWORD );

        // select profile
        clickLinkWithText( CUSTOM_USERNAME2 );

        //TODO: verify account details page
        assertPage( "Account Details" );

        // test password confirmation
        setFieldValue( PASSWORD_FIELD, CUSTOM_PASSWORD );
        setFieldValue( CONFIRM_PASSWORD_FIELD, CUSTOM_PASSWORD + "error" );
        clickButtonWithValue( "Submit" );

        // we should still be in Account Details
        assertPage( "Account Details" );
        isTextPresent( "Password confirmation failed.  Passwords do not match" );

        super.logout();

        // house keeping
        super.login( super.adminUsername, super.adminPassword );
        deleteUser( CUSTOM_USERNAME2, CUSTOM_FULLNAME, CUSTOM_EMAILADD );
        super.logout();
    }

    public void testPasswordCreationValidation()
        throws Exception
    {
        // initial user account creation ignores the password creation checks
        createUser( CUSTOM_USERNAME3, CUSTOM_FULLNAME, CUSTOM_EMAILADD, CUSTOM_PASSWORD, true );
        super.logout();

        // start password creation validation test
        super.login( CUSTOM_USERNAME3, CUSTOM_PASSWORD );

        // password test
        String alphaTest = "abcdef";
        String numericalTest = "123456";
        String characterLengthTest = "aaaaaaa12";
        String validPassword = "abc123";

        // select profile
        clickLinkWithText( CUSTOM_USERNAME3 );

        //TODO: verify account details page
        assertPage( "Account Details" );

        // test all alpha
        setFieldValue( PASSWORD_FIELD, alphaTest );
        setFieldValue( CONFIRM_PASSWORD_FIELD, alphaTest );
        clickButtonWithValue( "Submit" );

        // we should still be in Account Details
        assertPage( "Account Details" );
        isTextPresent( "You must provide a password containing at least 1 numeric character(s)." );

        setFieldValue( PASSWORD_FIELD, numericalTest );
        setFieldValue( CONFIRM_PASSWORD_FIELD, numericalTest );
        clickButtonWithValue( "Submit" );

        // we should still be in Account Details
        assertPage( "Account Details" );
        isTextPresent( "You must provide a password containing at least 1 alphabetic character(s)." );

        setFieldValue( PASSWORD_FIELD, characterLengthTest );
        setFieldValue( CONFIRM_PASSWORD_FIELD, characterLengthTest );
        clickButtonWithValue( "Submit" );

        // we should still be in Account Details
        assertPage( "Account Details" );
        isTextPresent( "You must provide a password between 1 and 8 characters in length." );

        // we should still be in Account Details
        assertPage( "Account Details" );
        isTextPresent( "You must provide a password containing at least 1 alphabetic character(s)." );

        setFieldValue( PASSWORD_FIELD, validPassword );
        setFieldValue( CONFIRM_PASSWORD_FIELD, validPassword );
        clickButtonWithValue( "Submit" );

        // we should still be in Account Details
        assertPage( "Continuum - Group Summary" );

        super.logout();

        // house keeping
        super.login( super.adminUsername, super.adminPassword );
        deleteUser( CUSTOM_USERNAME3, CUSTOM_FULLNAME, CUSTOM_EMAILADD );
        super.logout();
    }


    public void testThreeStrikeRule()
        throws Exception
    {
        createUser( CUSTOM_USERNAME4, CUSTOM_FULLNAME, CUSTOM_EMAILADD, CUSTOM_PASSWORD, true );
        super.logout();

        int numberOfTries = 3;

        for ( int nIndex = 0; nIndex < numberOfTries; nIndex++ )
        {
            if ( nIndex < 2 )
            {
                super.login( this.CUSTOM_USERNAME4, this.CUSTOM_PASSWORD + "error", false, "Login Page" );
                // login should fail
                assertTextPresent( "You have entered an incorrect username and/or password" );
                assertFalse( "user is authenticated using wrong password", isAuthenticated() );
            }
            else
            {
                // on the 3rd try, account is locked and we are returned to the Group Summary Page
                super.login( this.CUSTOM_USERNAME4, this.CUSTOM_PASSWORD + "error", false,
                             "Continuum - Group Summary" );
                assertTextPresent( "Account Locked" );
            }
        }

        // house keeping
        super.login( super.adminUsername, super.adminPassword );
        deleteUser( CUSTOM_USERNAME4, CUSTOM_FULLNAME, CUSTOM_EMAILADD, false, true );
        super.logout();
    }
       
    public void testDefaultRolesOfNewSystemAdministrator()
    {
        // initialize
        createUser( CUSTOM_USERNAME, CUSTOM_FULLNAME, CUSTOM_EMAILADD, CUSTOM_PASSWORD, true);
                      
        // upgrade the role of the user to system administrator
        String[] columnValues = { CUSTOM_USERNAME, CUSTOM_FULLNAME, CUSTOM_EMAILADD, "false", "false" };
        clickLinkWithXPath( XPathExpressionUtil.getColumnElement( XPathExpressionUtil.ANCHOR, 6, "Edit", columnValues ) );
                        
        checkField( "addRolesToUser_addSelectedRolesSystem Administrator" );
        clickButtonWithValue( "Add Selected Roles" );

        // verify roles        
        String[] roleList = { "Project Developer - Test Project Group Name", 
                              "System Administrator", "User Administrator", 
                              "Project User - Test Project Group Name",
                              "Project Developer - Default Project Group", 
                              "Project User - Default Project Group" };
        
        assertElementPresent( XPathExpressionUtil.getList( roleList ) );                
    }
    
    private void createUser( String userName, String fullName, String emailAdd, String password, boolean valid )
    {
        createUser( userName, fullName, emailAdd, password, password, valid );
    }

    private void createUser( String userName, String fullName, String emailAdd, String password, String confirmPassword,
                             boolean valid )
    {
        clickLinkWithText( "Users" );
        assertPage( "[Admin] User List" );

        // create user
        // submit button : Create Users
        clickLinkWithText( "Create User" );
        getSelenium().type( CREATE_FORM_USERNAME_FIELD, userName );
        getSelenium().type( CREATE_FORM_FULLNAME_FIELD, fullName );
        getSelenium().type( CREATE_FORM_EMAILADD_FIELD, emailAdd );
        getSelenium().type( CREATE_FORM_PASSWORD_FIELD, password );
        getSelenium().type( CREATE_FORM_CONFIRM_PASSWORD_FIELD, confirmPassword );
        submit();

        if ( valid )
        {
            assertPage( "[Admin] User List" );

            String[] columnValues = {userName, fullName, emailAdd, "false", "false"};

            // check if custom user is created
            assertElementPresent( XPathExpressionUtil.getTableRow( columnValues ) );
        }
    }

    private void deleteUser( String userName, String fullName, String emailAdd )
    {
        deleteUser( userName, fullName, emailAdd, false, false );
    }

    private void deleteUser( String userName, String fullName, String emailAdd, boolean validated, boolean locked )
    {
        String[] columnValues =
            {userName, fullName, emailAdd, Boolean.toString( validated ), Boolean.toString( locked )};

        clickLinkWithText( "Users" );

        // delete user
        clickLinkWithXPath(
            XPathExpressionUtil.getColumnElement( XPathExpressionUtil.ANCHOR, 7, "Delete", columnValues ) );

        // confirm
        assertPage( "[Admin] User Delete" );
        super.submit();

        // check if account is successfuly deleted
        super.assertElementNotPresent( XPathExpressionUtil.getTableRow( columnValues ) );
    }

    public void tearDown()
    {
        super.tearDown();
    }
}