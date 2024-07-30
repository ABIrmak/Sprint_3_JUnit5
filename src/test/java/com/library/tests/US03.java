package com.library.tests;

import com.library.base.TestBase_DB;
import com.library.base.TestBase_UI;
import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.ConfigurationReader;
import com.library.utility.DB_Util;
import com.library.utility.LibraryAPI_Util;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
Feature: As a librarian, I want to create a new book

  Scenario: Create a new book API
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "book" as request body
    When I send POST request to "/add_book" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The book has been created."
    And "book_id" field should not be null

  @ui @db
  Scenario: Create a new book all layers
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "book" as request body
    And I logged in Library UI as "librarian"
    And I navigate to "Books" page
    When I send POST request to "/add_book" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The book has been created."
    And "book_id" field should not be null
    And UI, Database and API created book information must match
 */
@Tag("smoke")
public class US03 {
    @Nested
    class AC01 {
        @Test
        public void Create_a_new_book_all_layers() {
            // Construct necessary variables
            String token = LibraryAPI_Util.getToken("librarian");
            String baseURI = ConfigurationReader.getProperty("library.baseUri");
            Map<String, Object> randomBook = LibraryAPI_Util.getRandomBookMap();

            // Make the specified request and get the response
            Response response = RestAssured.given()
                    .header("x-library-token", token)
                    .accept("application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("name",             randomBook.get("name"))
                    .formParam("isbn",             randomBook.get("isbn"))
                    .formParam("year",             randomBook.get("year"))
                    .formParam("author",           randomBook.get("author"))
                    .formParam("book_category_id", randomBook.get("book_category_id"))
                    .formParam("description",      randomBook.get("description"))
                    .post(baseURI + "/add_book")
                    // .prettyPeek()
                    ;
            JsonPath jsonPath = response.jsonPath();

            // Do the assertions
            assertEquals(200, response.statusCode());
            assertEquals("application/json; charset=utf-8", response.contentType());
            assertEquals("The book has been created.", jsonPath.getString("message"));
            assertNotNull(jsonPath.getString("book_id"));
        }
    }

    @Nested
    class AC02 implements TestBase_DB, TestBase_UI {
        @Test
        public void Create_a_new_user_all_layers() {
            // Construct necessary variables
            String token = LibraryAPI_Util.getToken("librarian");
            String baseURI = ConfigurationReader.getProperty("library.baseUri");
            Map<String, Object> randomBook = LibraryAPI_Util.getRandomBookMap();

            // Make the specified request and get the response
            Response response = RestAssured.given()
                    .header("x-library-token", token)
                    .accept("application/json")
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("name",             randomBook.get("name"))
                    .formParam("isbn",             randomBook.get("isbn"))
                    .formParam("year",             randomBook.get("year"))
                    .formParam("author",           randomBook.get("author"))
                    .formParam("book_category_id", randomBook.get("book_category_id"))
                    .formParam("description",      randomBook.get("description"))
                    .post(baseURI + "/add_book")
                    // .prettyPeek()
                    ;
            JsonPath jsonPath = response.jsonPath();

            // Do the assertions for API
            assertEquals(200, response.statusCode());
            assertEquals("application/json; charset=utf-8", response.contentType());
            assertEquals("The book has been created.", jsonPath.getString("message"));
            assertNotNull(jsonPath.getString("book_id"));

            // Get the API data
            String nameAPI             = "" + randomBook.get("name");
            String isbnAPI             = "" + randomBook.get("isbn");
            String yearAPI             = "" + randomBook.get("year");
            String authorAPI           = "" + randomBook.get("author");
            String bookCategoryIdAPI   = "" + randomBook.get("book_category_id");
            String descriptionAPI      = "" + randomBook.get("description");

            // Get the UI data
            LoginPage loginPage = new LoginPage();
            loginPage.login("librarian");
            loginPage.navigateModule("Books");
            BookPage bookPage = new BookPage();
            (new Select(bookPage.showRecordsSelect)).selectByValue("500");
            BrowserUtil.waitFor(3);
            bookPage.search.sendKeys(nameAPI + Keys.ENTER);
            BrowserUtil.waitFor(3);
            for (WebElement row : bookPage.allRows) {
                if (row.findElement(By.xpath("./td[2]")).getText().equals(isbnAPI)) {
                    row.findElement(By.xpath("./td[1]")).click();
                    BrowserUtil.waitFor(3);
                    break;
                }
            }
            String nameUI           = bookPage.bookName.getAttribute("value");
            String isbnUI           = bookPage.isbn.getAttribute("value");
            String yearUI           = bookPage.year.getAttribute("value");
            String authorUI         = bookPage.author.getAttribute("value");
            // String bookCategoryIdUI = (new Select(bookPage.categoryDropdown)).getFirstSelectedOption().getAttribute("value");
            String descriptionUI    = bookPage.description.getAttribute("value");


            // Get the DB data
            ResultSet dbResult = DB_Util.runQuery(
                    "select id, name, isbn, year, author, book_category_id, description\n" +
                            "from books\n" +
                            "where id = " + jsonPath.getString("book_id") + ";"
            );
            List<String> DB_columns = DB_Util.getRowDataAsList(1);
            String nameDB           = DB_columns.get(1);
            String isbnDB           = DB_columns.get(2);
            String yearDB           = DB_columns.get(3);
            String authorDB         = DB_columns.get(4);
            String bookCategoryIdDB = DB_columns.get(5);
            String descriptionDB    = DB_columns.get(6);

            // Do the API-DB-UI assertions
            assertEquals(nameDB, nameAPI);
            assertEquals(nameUI, nameAPI);

            assertEquals(isbnDB, isbnAPI);
            assertEquals(isbnUI, isbnAPI);

            assertEquals(yearDB, yearAPI);
            assertEquals(yearUI, yearAPI);

            assertEquals(authorDB, authorAPI);
            assertEquals(authorUI, authorAPI);

            assertEquals(bookCategoryIdDB, bookCategoryIdAPI);
            // assertEquals(bookCategoryIdUI, bookCategoryIdAPI);

            assertEquals(descriptionDB, descriptionAPI);
            assertEquals(descriptionUI, descriptionAPI);
        }
    }
}
