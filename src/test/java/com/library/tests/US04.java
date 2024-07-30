package com.library.tests;

import com.library.base.TestBase_DB;
import com.library.base.TestBase_UI;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.ConfigurationReader;
import com.library.utility.DB_Util;
import com.library.utility.LibraryAPI_Util;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
Feature: As a librarian, I want to create a new user

  Scenario: Create a new user API
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "user" as request body
    When I send POST request to "/add_user" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The user has been created."
    And "user_id" field should not be null

  @db @ui
  Scenario: Create a new user all layers
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "user" as request body
    When I send POST request to "/add_user" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The user has been created."
    And "user_id" field should not be null
    And created user information should match with Database
    And created user should be able to login Library UI
    And created user name should appear in Dashboard Page
 */
@Tag("regression")
public class US04 {
    @Nested
    class AC01 {
        @Test
        public void Create_a_new_user_API() {
            // Construct necessary variables
            String token = LibraryAPI_Util.getToken("librarian");
            String baseURI = ConfigurationReader.getProperty("library.baseUri");
            Map<String, Object> randomUser = LibraryAPI_Util.getRandomUserMap();

            // Make the specified request and get the response
            Response response = RestAssured.given()
                    .header("x-library-token", token)
                    .accept("application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("full_name",     randomUser.get("full_name"))
                    .formParam("email",         randomUser.get("email"))
                    .formParam("password",      randomUser.get("password"))
                    .formParam("user_group_id", randomUser.get("user_group_id"))
                    .formParam("status",        randomUser.get("status"))
                    .formParam("start_date",    randomUser.get("start_date"))
                    .formParam("end_date",      randomUser.get("end_date"))
                    .formParam("address",       randomUser.get("address"))
                    .post(baseURI + "/add_user")
                    // .prettyPeek()
                    ;
            JsonPath jsonPath = response.jsonPath();

            // Do the assertions
            assertEquals(200, response.statusCode());
            assertEquals("application/json; charset=utf-8", response.contentType());
            assertEquals("The user has been created.", jsonPath.getString("message"));
            assertNotNull(jsonPath.getString("user_id"));
        }
    }

    @Nested
    class AC02 implements TestBase_DB, TestBase_UI {
        @Test
        public void Create_a_new_user_all_layers() {
            // Construct necessary variables
            String token = LibraryAPI_Util.getToken("librarian");
            String baseURI = ConfigurationReader.getProperty("library.baseUri");
            Map<String, Object> randomUser = LibraryAPI_Util.getRandomUserMap();

            // Make the specified request and get the response
            Response response = RestAssured.given()
                    .header("x-library-token", token)
                    .accept("application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("full_name", randomUser.get("full_name"))
                    .formParam("email", randomUser.get("email"))
                    .formParam("password", randomUser.get("password"))
                    .formParam("user_group_id", randomUser.get("user_group_id"))
                    .formParam("status", randomUser.get("status"))
                    .formParam("start_date", randomUser.get("start_date"))
                    .formParam("end_date", randomUser.get("end_date"))
                    .formParam("address", randomUser.get("address"))
                    .post(baseURI + "/add_user")
                    // .prettyPeek()
                    ;
            JsonPath jsonPath = response.jsonPath();

            // Do the assertions for the response
            assertEquals(200, response.statusCode());
            assertEquals("application/json; charset=utf-8", response.contentType());
            assertEquals("The user has been created.", jsonPath.getString("message"));
            assertNotNull(jsonPath.getString("user_id"));

            // Get the DB data
            String userId = jsonPath.getString("user_id");
            DB_Util.runQuery(
                    "select *\n" +
                            "from users\n" +
                            "where id = " + userId + ";"
            );
            Map<String, Object> userDB = DB_Util.getRowMap(1);

            // Do the assertions for DB
            assertEquals("" + userDB.get("full_name"), "" + randomUser.get("full_name"));
            assertEquals("" + userDB.get("email"), "" + randomUser.get("email"));
            // Assert.assertEquals("" + userDB.get("password"), "" + randomUser.get("password")); // DB Password is bugged
            assertEquals("" + userDB.get("user_group_id"), "" + randomUser.get("user_group_id"));
            assertEquals("" + userDB.get("status"), "" + randomUser.get("status"));
            assertEquals("" + userDB.get("start_date"), "" + randomUser.get("start_date"));
            assertEquals("" + userDB.get("end_date"), "" + randomUser.get("end_date"));
            assertEquals("" + userDB.get("address"), "" + randomUser.get("address"));

            // Get the UI data
            LoginPage loginPage = new LoginPage();
            loginPage.login((String) randomUser.get("email"), (String) randomUser.get("password"));

            // Do the assertions for UI
            BrowserUtil.waitForVisibility(loginPage.accountHolderName, 5);
            assertEquals(loginPage.accountHolderName.getText(), randomUser.get("full_name"));
        }
    }
}
