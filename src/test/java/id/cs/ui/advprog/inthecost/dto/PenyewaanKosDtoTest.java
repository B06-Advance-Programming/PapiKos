package id.cs.ui.advprog.inthecost.dto;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PenyewaanKosDtoTest {

    @Test
    void testAllGetters() {
        UUID id = UUID.randomUUID();
        String namaLengkap = "John Doe";
        String nomorTelepon = "08123456789";
        LocalDate tanggalCheckIn = LocalDate.of(2025, 6, 1);
        int durasiBulan = 3;
        StatusPenyewaan status = StatusPenyewaan.DIAJUKAN;
        UUID userId = UUID.randomUUID();
        String namaKos = "bukpis";

        PenyewaanKosDto dto = new PenyewaanKosDto(
                id,
                namaLengkap,
                nomorTelepon,
                tanggalCheckIn,
                durasiBulan,
                status,
                userId,
                namaKos
        );

        assertAll(
                () -> assertEquals(id, dto.getId(), "ID should match"),
                () -> assertEquals(namaLengkap, dto.getNamaLengkap(), "Nama lengkap should match"),
                () -> assertEquals(nomorTelepon, dto.getNomorTelepon(), "Nomor telepon should match"),
                () -> assertEquals(tanggalCheckIn, dto.getTanggalCheckIn(), "Tanggal check-in should match"),
                () -> assertEquals(durasiBulan, dto.getDurasiBulan(), "Durasi bulan should match"),
                () -> assertEquals(status, dto.getStatus(), "Status should match"),
                () -> assertEquals(userId, dto.getUserId(), "User ID should match"),
                () -> assertEquals(namaKos, dto.getNamaKos(), "Kost ID should match")
        );
    }
}
