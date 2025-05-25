package id.cs.ui.advprog.inthecost.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PenyewaanKosRequestDtoTest {

    @Test
    public void testGettersAndSetters() {
        PenyewaanKosRequestDto dto = new PenyewaanKosRequestDto();

        UUID id = UUID.randomUUID();
        String namaLengkap = "Jane Doe";
        String nomorTelepon = "081298765432";
        LocalDate tanggalCheckIn = LocalDate.of(2025, 7, 15);
        int durasiBulan = 6;
        UUID kostId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String status = "DIAJUKAN";

        dto.setId(id);
        dto.setNamaLengkap(namaLengkap);
        dto.setNomorTelepon(nomorTelepon);
        dto.setTanggalCheckIn(tanggalCheckIn);
        dto.setDurasiBulan(durasiBulan);
        dto.setKostId(kostId);
        dto.setUserId(userId);
        dto.setStatus(status);

        assertAll(
                () -> assertEquals(id, dto.getId(), "ID should match"),
                () -> assertEquals(namaLengkap, dto.getNamaLengkap(), "Nama lengkap should match"),
                () -> assertEquals(nomorTelepon, dto.getNomorTelepon(), "Nomor telepon should match"),
                () -> assertEquals(tanggalCheckIn, dto.getTanggalCheckIn(), "Tanggal check-in should match"),
                () -> assertEquals(durasiBulan, dto.getDurasiBulan(), "Durasi bulan should match"),
                () -> assertEquals(kostId, dto.getKostId(), "Kost ID should match"),
                () -> assertEquals(userId, dto.getUserId(), "User ID should match"),
                () -> assertEquals(status, dto.getStatus(), "Status should match")
        );
    }
}
