package id.cs.ui.advprog.inthecost.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
public class PenyewaanKosRequestDto {

    private UUID id;
    private String namaLengkap;
    private String nomorTelepon;
    private LocalDate tanggalCheckIn;
    private int durasiBulan;
    private UUID kostId;
    private UUID userId;
}
