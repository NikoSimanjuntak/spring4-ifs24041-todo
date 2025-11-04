package org.delcom.starter.controllers;

import java.lang.reflect.Method;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeControllerTest {

    private HomeController controller;

    @BeforeEach
    void setUp() {
        controller = new HomeController();
    }

    // 1. Tes untuk informasiNim
    @Test
    void testInformasiNim_AllCases() {
        // Kasus valid
        String resultValid = controller.informasiNim("11S23001");
        assertTrue(resultValid.contains("Sarjana Informatika") && resultValid.contains("Angkatan: 2023"));

        // Kasus tidak valid (terlalu pendek atau null)
        assertTrue(controller.informasiNim("123").contains("minimal 8 karakter"));
        assertTrue(controller.informasiNim(null).contains("minimal 8 karakter"));

        // Kasus prodi tidak diketahui
        assertTrue(controller.informasiNim("99X23123").contains("Unknown"));
    }

    // 2. Tes untuk perolehanNilai
    @Test
    void testPerolehanNilai_Valid() {
        String data = "UAS|85|40\nUTS|75|30\nPA|90|20\nK|100|10";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertTrue(result.contains("84.50") && result.contains("Grade: B"));
    }

    // Tes tambahan untuk semua cabang kondisi di perolehanNilai
    @Test
    void testPerolehanNilai_FullBranchCoverage() {
        String data =
            "UAS|90|50\n" +        // Baris valid
            "\n" +                 // Baris kosong
            "Tugas|80|0\n" +       // Bobot 0
            "Invalid Line\n" +     // Tidak ada '|'
            "Hanya|Dua\n" +        // Kurang satu elemen
            "Nilai|abc|def\n" +    // Format angka salah
            "---\n" +              // Break loop
            "Ini|tidak|dihitung";

        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);

        assertEquals("Nilai Akhir: 45.00 (Total Bobot: 50%)\nGrade: E", result);
    }

    @Test
    void testPerolehanNilai_InvalidBase64() {
        assertThrows(IllegalArgumentException.class, () -> controller.perolehanNilai("!@#"));
    }

    // 3. Tes untuk perbedaanL
    @Test
    void testPerbedaanL_AllCases() {
        String b64Valid = Base64.getEncoder().encodeToString("UULL".getBytes());
        assertTrue(controller.perbedaanL(b64Valid).contains("Perbedaan Jarak: 8"));

        String b64InvalidChar = Base64.getEncoder().encodeToString("U R D L X Y Z".getBytes());
        assertTrue(controller.perbedaanL(b64InvalidChar).contains("Perbedaan Jarak: 0"));
    }

    // 4. Tes untuk palingTer
    @Test
    void testPalingTer_Valid() {
        String text = "terbaik terbaik termahal";
        String b64 = Base64.getEncoder().encodeToString(text.getBytes());
        assertTrue(controller.palingTer(b64).contains("'terbaik' (muncul 2 kali)"));
    }

    @Test
    void testPalingTer_FullBranchCoverage() {
        String noTer = Base64.getEncoder().encodeToString("hello world".getBytes());
        assertEquals("Tidak ditemukan kata yang berawalan 'ter'.", controller.palingTer(noTer));

        String doubleSpace = Base64.getEncoder().encodeToString("tercepat  terlambat".getBytes());
        assertTrue(controller.palingTer(doubleSpace).contains("muncul 1 kali"));

        String multiple = "terbaik terendah terbaik terburuk terendah terbaik";
        String b64Multiple = Base64.getEncoder().encodeToString(multiple.getBytes());
        assertTrue(controller.palingTer(b64Multiple).contains("'terbaik' (muncul 3 kali)"));
    }

    @Test
    void testPalingTer_SingleWordTer() {
        String singleTer = Base64.getEncoder().encodeToString("ter".getBytes());
        String result = controller.palingTer(singleTer);
        assertTrue(result.contains("'ter' (muncul 1 kali)"));
    }

    // Tes private method calculateGrade untuk 100% coverage
    @Test
    void testCalculateGrade_Coverage() throws Exception {
        Method method = HomeController.class.getDeclaredMethod("calculateGrade", double.class);
        method.setAccessible(true);
        assertEquals("A", method.invoke(controller, 90.0));
        assertEquals("B", method.invoke(controller, 80.0));
        assertEquals("C", method.invoke(controller, 70.0));
        assertEquals("D", method.invoke(controller, 60.0));
        assertEquals("E", method.invoke(controller, 50.0));
    }

    @Test
    void testPalingTer_WithEmptyStringInWords() {
        String text = "terbaik  "; // ada 2 spasi di akhir
        String b64 = Base64.getEncoder().encodeToString(text.getBytes());
        String result = controller.palingTer(b64);
        assertTrue(result.contains("'terbaik' (muncul 1 kali)"));
    }
}
