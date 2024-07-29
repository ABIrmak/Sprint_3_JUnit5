package com.library.tests;

import com.library.utility.ConfigurationReader;
import com.library.utility.LibraryAPI_Util;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

/*
Feature: : As a user, I want to view my own user information using the API
  so that I can see what information is stored about me

  Scenario Outline: Decode User
    Given I logged Library api with credentials "<email>" and "<password>"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I send token information as request body
    When I send POST request to "/decode" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "user_group_id" path should be equal to "<user_group_id>"
    And the field value for "email" path should be equal to "<email>"
    And "full_name" field should not be null
    And "id" field should not be null

    Examples:
      | email                | password    | user_group_id |
      | student5@library     | libraryUser | 3             |
      | librarian10@library  | libraryUser | 2             |
      | student10@library    | libraryUser | 3             |
      | librarian13@library  | libraryUser | 2             |
*/
@Tag("regression")
public class US05 {
    @Nested
    class AC01 {
        @ParameterizedTest(name = "{index} - Email is {0}")
        @MethodSource("getExamples")
        public void Decode_User(String email, String password, int user_group_id) {
            // Construct necessary variables
            String token = LibraryAPI_Util.getToken(email, password);
            String baseURI = ConfigurationReader.getProperty("library.baseUri");

            // Make the specified request and get the response
            Response response = RestAssured.given()
                    .header("x-library-token", token)
                    .accept("application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("token", token)
                    .post(baseURI + "/decode")
                    .prettyPeek();
            JsonPath jsonPath = response.jsonPath();

            // Do the assertions
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("application/json; charset=utf-8", response.contentType());
            Assertions.assertEquals(user_group_id, jsonPath.getInt("user_group_id"));
            Assertions.assertEquals(email, jsonPath.getString("email"));
            Assertions.assertNotNull(jsonPath.getString("full_name"));
            Assertions.assertNotNull(jsonPath.getString("id"));
        }

        public static List<Arguments> getExamples() {
            return List.of(
                    Arguments.of("student5@library", "libraryUser", 3),
                    Arguments.of("librarian10@library", "libraryUser", 2),
                    Arguments.of("student10@library", "libraryUser", 3),
                    Arguments.of("librarian13@library", "libraryUser", 2)
            );
        }
    }
}
