package com.library.tests;

import com.library.utility.ConfigurationReader;
import com.library.utility.LibraryAPI_Util;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

/*
Feature: As a user, I want to search for a specific user by their id
        so that I can quickly find the information I need.

  Scenario: Retrieve single user
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Path param is "1"
    When I send GET request to "/get_user_by_id/{id}" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And "id" field should be same with path param
    And following fields should not be null
      | full_name |
      | email     |
      | password  |
 */
@Tag("smoke")
public class US02 {
    @Nested
    class AC01 {
        @Test
        public void Retrieve_single_user() {
            // Construct necessary variables
            String token = LibraryAPI_Util.getToken("librarian");
            String baseURI = ConfigurationReader.getProperty("library.baseUri");

            // Make the specified request and get the response
            Response response = RestAssured.given()
                    .header("x-library-token", token)
                    .accept("application/json")
                    .pathParam("id", 1)
                    .get(baseURI + "/get_user_by_id/{id}")
                    // .prettyPeek()
                    ;
            JsonPath jsonPath = response.jsonPath();

            // Do the assertions
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("application/json; charset=utf-8", response.contentType());
            Assertions.assertEquals(1, jsonPath.getInt("id"));
            Assertions.assertNotNull(jsonPath.getString("full_name"));
            Assertions.assertNotNull(jsonPath.getString("email"));
            Assertions.assertNotNull(jsonPath.getString("password"));
        }
    }
}
