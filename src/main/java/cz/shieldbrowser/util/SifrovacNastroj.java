package cz.shieldbrowser.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Nástroj pro šifrování a dešifrování dat
 * Používá AES-256 šifrování
 */
public class SifrovacNastroj {
    
    // Konstanty pro šifrování
    private static final String ALGORITMUS = "AES";
    private static final int DELKA_KLICE = 256;
    private static final int DELKA_HESLA = 32; // 256 bitů
    
    private SecretKey tajnyKlic;
    
    /**
     * Konstruktor - inicializuje šifrovací nástroj
     */
    public SifrovacNastroj() {
        inicializujKlic();
    }
    
    /**
     * Inicializuje nový šifrovací klíč
     */
    private void inicializujKlic() {
        try {
            KeyGenerator generatorKlice = KeyGenerator.getInstance(ALGORITMUS);
            generatorKlice.init(DELKA_KLICE, new SecureRandom());
            tajnyKlic = generatorKlice.generateKey();
        } catch (Exception vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Inicializuje klíč z hesla
     * @param heslo heslo pro generování klíče
     */
    public void inicializujKlicZHesla(String heslo) {
        try {
            // Rozšíření hesla na 256 bitů
            byte[] dekodovaneHeslo = heslo.getBytes();
            byte[] klicBajty = new byte[DELKA_HESLA];
            
            for (int i = 0; i < Math.min(dekodovaneHeslo.length, klicBajty.length); i++) {
                klicBajty[i] = dekodovaneHeslo[i];
            }
            
            tajnyKlic = new SecretKeySpec(klicBajty, 0, klicBajty.length, ALGORITMUS);
        } catch (Exception vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Šifruje text
     * @param text text k šifrování
     * @return šifrovaný text v Base64 formátu
     */
    public String sifrujeText(String text) {
        try {
            Cipher sifera = Cipher.getInstance(ALGORITMUS);
            sifera.init(Cipher.ENCRYPT_MODE, tajnyKlic);
            
            byte[] sifrovanyText = sifera.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(sifrovanyText);
        } catch (Exception vyjimka) {
            vyjimka.printStackTrace();
            return null;
        }
    }
    
    /**
     * Dešifruje text
     * @param sifrovanyText šifrovaný text v Base64 formátu
     * @return dešifrovaný text
     */
    public String desifrujeText(String sifrovanyText) {
        try {
            Cipher sifera = Cipher.getInstance(ALGORITMUS);
            sifera.init(Cipher.DECRYPT_MODE, tajnyKlic);
            
            byte[] dekodovanyText = Base64.getDecoder().decode(sifrovanyText);
            byte[] desifrovaneBajty = sifera.doFinal(dekodovanyText);
            
            return new String(desifrovaneBajty);
        } catch (Exception vyjimka) {
            vyjimka.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generuje náhodné heslo
     * @param delka délka hesla
     * @return vygenerované heslo
     */
    public static String generujeNahodneHeslo(int delka) {
        String znaky = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        StringBuilder heslo = new StringBuilder();
        SecureRandom nahodna = new SecureRandom();
        
        for (int i = 0; i < delka; i++) {
            heslo.append(znaky.charAt(nahodna.nextInt(znaky.length())));
        }
        
        return heslo.toString();
    }
    
    /**
     * Ověřuje sílu hesla
     * @param heslo heslo k ověření
     * @return úroveň síly (1-5, kde 5 je nejsilnější)
     */
    public static int kontrolujeMaxHesla(String heslo) {
        if (heslo == null || heslo.isEmpty()) {
            return 0;
        }
        
        int sila = 1;
        
        // Délka hesla
        if (heslo.length() >= 8) sila++;
        if (heslo.length() >= 12) sila++;
        
        // Typy znaků
        boolean maCisla = heslo.matches(".*\\d.*");
        boolean maKapitaly = heslo.matches(".*[A-Z].*");
        boolean maMinuskuly = heslo.matches(".*[a-z].*");
        boolean maSpecialni = heslo.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*");
        
        if (maCisla && maKapitaly && maMinuskuly) sila++;
        if (maSpecialni) sila++;
        
        return Math.min(sila, 5);
    }
    
    /**
     * Vrací tajný klíč
     * @return tajný klíč
     */
    public SecretKey vraciTajnyKlic() {
        return tajnyKlic;
    }
    
    /**
     * Nastavuje tajný klíč
     * @param klic nový tajný klíč
     */
    public void nastavujeKlic(SecretKey klic) {
        this.tajnyKlic = klic;
    }
}
