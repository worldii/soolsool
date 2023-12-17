package com.woowacamp.soolsool.acceptance;

import com.woowacamp.soolsool.config.DatabaseCleaner;
import com.woowacamp.soolsool.config.RedisTestConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(RedisTestConfig.class)
public abstract class AcceptanceTest {

    static final String BEARER = "Bearer ";

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.execute();
        databaseCleaner.insert();
    }
}
