package cz.shieldbrowser.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Nástroj pro vyhledávání v textu
 */
public class VyhledavacNastroj {
    
    /**
     * Vyhledá všechny výskyty hledaného textu v seznamu
     * @param seznam seznam pro vyhledávání
     * @param hledejText text k hledání
     * @return seznam výsledků
     */
    public static <T> List<T> vyhledajVSeznamu(List<T> seznam, String hledejText) {
        List<T> vysledky = new ArrayList<>();
        
        for (T prvek : seznam) {
            if (prvek.toString().toLowerCase().contains(hledejText.toLowerCase())) {
                vysledky.add(prvek);
            }
        }
        
        return vysledky;
    }
    
    /**
     * Provádí fulltextové vyhledávání
     * @param text text k vyhledávání
     * @param hledejText text k hledání
     * @return true pokud byl text nalezen
     */
    public static boolean fulltextoveVyhledavani(String text, String hledejText) {
        if (text == null || hledejText == null) {
            return false;
        }
        
        return text.toLowerCase().contains(hledejText.toLowerCase());
    }
    
    /**
     * Vrací pozici textu v řetězci
     * @param text text k vyhledávání
     * @param hledejText text k hledání
     * @return pozice textu, -1 pokud nebyl nalezen
     */
    public static int vratPozici(String text, String hledejText) {
        if (text == null || hledejText == null) {
            return -1;
        }
        
        return text.toLowerCase().indexOf(hledejText.toLowerCase());
    }
    
    /**
     * Počítá výskyty textu v řetězci
     * @param text text k vyhledávání
     * @param hledejText text k hledání
     * @return počet výskytů
     */
    public static int pocitejVyskyty(String text, String hledejText) {
        if (text == null || hledejText == null || hledejText.isEmpty()) {
            return 0;
        }
        
        int pocet = 0;
        int index = 0;
        
        while ((index = text.indexOf(hledejText, index)) != -1) {
            pocet++;
            index += hledejText.length();
        }
        
        return pocet;
    }
    
    /**
     * Provádí regulární výraz vyhledávání
     * @param text text k vyhledávání
     * @param regex regulární výraz
     * @return true pokud odpovídá
     */
    public static boolean regularniBýtVyhledavani(String text, String regex) {
        if (text == null || regex == null) {
            return false;
        }
        
        try {
            return text.matches(regex);
        } catch (Exception vyjimka) {
            return false;
        }
    }
}
