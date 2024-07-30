package com.library.tests;

import com.library.utility.ConfigurationReader;
import com.library.utility.LibraryAPI_Util;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/*
Feature: As a librarian, I want to retrieve all users

  Scenario: Retrieve all users from the API endpoint
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    When I send GET request to "/get_all_users" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And Each "id" field should not be null
    And Each "name" field should not be null
 */
@Tag("smoke")
public class US01 {
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
                    .get(baseURI + "/get_all_users")
                    // .prettyPeek()
                    ;
            JsonPath jsonPath = response.jsonPath();

            // Do the assertions
            assertEquals(200, response.statusCode());
            assertEquals("application/json; charset=utf-8", response.contentType());
            assertFalse(jsonPath.getList("id").stream().anyMatch(Objects::isNull));
            assertFalse(jsonPath.getList("name").stream().anyMatch(Objects::isNull));
        }
    }
}
