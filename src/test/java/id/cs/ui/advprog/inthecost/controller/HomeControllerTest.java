package id.cs.ui.advprog.inthecost.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    private final HomeController homeController = new HomeController();

    @Test
    void home_shouldReturnWelcomeMessage() {
        String response = homeController.home();
        assertEquals("Welcome to InTheKost Staging!", response);
    }
}
