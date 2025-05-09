package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void testGetInboxNotifications() throws Exception {
        UUID userId = UUID.randomUUID();
        User u = new User();
        u.setId(userId);
        u.setUsername("userX");
        InboxNotification notif = new InboxNotification(u, "Alert!");
        when(notificationService.getInbox(userId.toString())).thenReturn(List.of(notif));

        mockMvc.perform(get("/api/inbox/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].message").value("Alert!"));
    }
}