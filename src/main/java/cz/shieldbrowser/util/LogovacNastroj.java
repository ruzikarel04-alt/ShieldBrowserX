package cz.shieldbrowser.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Nástroj pro logování aktivit aplikace
 */
public class LogovacNastroj {
    
    private static final String SLOZKA_LOGU = System.getProperty("user.home") + "/.shieldbrowserx/logs";
    private static final DateTimeFormatter FORMATOR = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private List<String> loguj;
    private String soubor;
    
    /**
     * Konstruktor - inicializuje nástroj logování
     */
    public LogovacNastroj() {
        loguj = new ArrayList<>();
        
        // Vytvoření složky pro logy
        File slozka = new File(SLOZKA_LOGU);
        if (!slozka.exists()) {
            slozka.mkdirs();
        }
        
        // Název souboru logy
        String datum = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.soubor = SLOZKA_LOGU + "/log_" + datum + ".txt";
    }
    
    /**
     * Loguje zprávu
     * @param uzel úzel zprávy (typ, třída)
     * @param zprava zpráva k zalogování
     */
    public void loguj(String uzel, String zprava) {
        String cas = LocalDateTime.now().format(FORMATOR);
        String radek = "[" + cas + "] [" + uzel + "] " + zprava;
        
        loguj.add(radek);
        System.out.println(radek);
        
        ulozNaSoubor(radek);
    }
    
    /**
     * Loguje informaci
     * @param zprava zpráva k zalogování
     */
    public void info(String zprava) {
        loguj("INFO", zprava);
    }
    
    /**
     * Loguje upozornění
     * @param zprava zpráva k zalogování
     */
    public void upozorneni(String zprava) {
        loguj("UPOZORNĚNÍ", zprava);
    }
    
    /**
     * Loguje chybu
     * @param zprava zpráva k zalogování
     * @param vyjimka chyba k zalogování
     */
    public void chyba(String zprava, Exception vyjimka) {
        loguj("CHYBA", zprava);
        if (vyjimka != null) {
            loguj("STACKTRACE", vyjimka.getMessage());
        }
    }
    
    /**
     * Loguje debug zprávu
     * @param zprava zpráva k zalogování
     */
    public void debug(String zprava) {
        loguj("DEBUG", zprava);
    }
    
    /**
     * Uloží log na soubor
     * @param radek řádek k uložení
     */
    private void ulozNaSoubor(String radek) {
        try (FileWriter zapisovac = new FileWriter(soubor, true)) {
            zapisovac.write(radek);
            zapisovac.write(System.lineSeparator());
            zapisovac.flush();
        } catch (IOException vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Vrací všechny logy
     * @return seznam logů
     */
    public List<String> vraciLogy() {
        return new ArrayList<>(loguj);
    }
    
    /**
     * Vrací cestu ke složce logů
     * @return cesta ke složce logů
     */
    public static String vraciSlozkuLogu() {
        return SLOZKA_LOGU;
    }
}
