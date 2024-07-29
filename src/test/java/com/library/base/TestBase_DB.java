package com.library.base;

import com.library.utility.DB_Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public interface TestBase_DB {
    @BeforeEach()
    default void setUpDB(){
        System.out.println("Connecting to database...");
        DB_Util.createConnection();
    }

    @AfterEach()
    default void tearDownDB(){
        System.out.println("Closing the database connection...");
        DB_Util.destroy();
    }
}
