package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class KosTest {

    private Kos kos;
    private Observer observerMock;

    @BeforeEach
    void setUp() {
        kos = new Kos("Kos Mawar", 2); // awalnya 2 kamar
        observerMock = mock(Observer.class);
    }

    @Test
    void testAddObserverTriggersUpdateWhenRoomAvailable() {
        kos.setAvailableRooms(0); // set full dulu
        kos.addObserver(observerMock);
        kos.setAvailableRooms(1); // tersedia kamar

        verify(observerMock, times(1)).update(kos);
    }

    @Test
    void testRemoveObserverDisablesUpdate() {
        kos.addObserver(observerMock);
        kos.removeObserver(observerMock);
        kos.setAvailableRooms(1);

        verify(observerMock, never()).update(kos);
    }

    @Test
    void testNoUpdateWhenRoomCountUnchanged() {
        kos.setAvailableRooms(1);
        kos.addObserver(observerMock);
        kos.setAvailableRooms(1); // tidak berubah

        verify(observerMock, never()).update(kos);
    }

    @Test
    void testNotifyOnlyWhenChangingFromFullToAvailable() {
        kos.setAvailableRooms(0);
        kos.addObserver(observerMock);

        // Ini harus trigger observer karena status berubah dari penuh ke available
        kos.setAvailableRooms(2);

        verify(observerMock, times(1)).update(kos);
    }
}
