package com.library.base;

import com.library.utility.ConfigurationReader;
import com.library.utility.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;

public interface TestBase_UI {
    @BeforeEach()
    default void setUpUI(){
        System.out.println("Opening browser...");
        Driver.getDriver().manage().window().maximize();
        Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        Driver.getDriver().get(ConfigurationReader.getProperty("library_url"));
    }

    @AfterEach()
    default void tearDownUI(){
        System.out.println("Closing browser...");
        Driver.closeDriver();
    }
}
