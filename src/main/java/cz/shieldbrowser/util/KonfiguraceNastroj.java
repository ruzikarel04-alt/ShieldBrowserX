package cz.shieldbrowser.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Nástroj pro správu konfigurace aplikace
 * Ukládá a načítá nastavení v JSON formátu
 */
public class KonfiguraceNastroj {
    
    private static final String SLOZKA_KONFIGURACE = System.getProperty("user.home") + "/.shieldbrowserx";
    private static final String SOUBOR_KONFIGURACE = SLOZKA_KONFIGURACE + "/konfigurace.json";
    
    private JsonObject konfigurace;
    private Gson gson;
    
    /**
     * Konstruktor - inicializuje nástroj konfigurace
     */
    public KonfiguraceNastroj() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        konfigurace = new JsonObject();
        
        // Vytvoření složky pokud neexistuje
        File slozka = new File(SLOZKA_KONFIGURACE);
        if (!slozka.exists()) {
            slozka.mkdirs();
        }
        
        nactiKonfiguraci();
    }
    
    /**
     * Načítá konfiguraci ze souboru
     */
    private void nactiKonfiguraci() {
        File soubor = new File(SOUBOR_KONFIGURACE);
        
        if (soubor.exists()) {
            try (FileReader ctecka = new FileReader(soubor)) {
                konfigurace = gson.fromJson(ctecka, JsonObject.class);
                if (konfigurace == null) {
                    konfigurace = new JsonObject();
                }
            } catch (IOException vyjimka) {
                vyjimka.printStackTrace();
                konfigurace = new JsonObject();
            }
        } else {
            inicializujVychoziKonfiguraci();
        }
    }
    
    /**
     * Inicializuje výchozí konfiguraci
     */
    private void inicializujVychoziKonfiguraci() {
        // Obecné nastavení
        konfigurace.addProperty("verze", "1.0.0");
        konfigurace.addProperty("jazyk", "cs");
        konfigurace.addProperty("tmavyMotiv", true);
        konfigurace.addProperty("velikostOkna", "1280x720");
        
        // Bezpečnost
        konfigurace.addProperty("vynucenoHttps", true);
        konfigurace.addProperty("blokovaniTrackers", true);
        konfigurace.addProperty("blokovaniReklam", true);
        konfigurace.addProperty("ochranaSoukromi", true);
        
        // Výkon
        konfigurace.addProperty("hardwarovyZrychleni", true);
        konfigurace.addProperty("gpuAkcelerace", true);
        konfigurace.addProperty("kompresiDat", true);
        
        // WebView
        konfigurace.addProperty("javascriptPovoleno", true);
        konfigurace.addProperty("javascriptZDelatekPovoleno", true);
        konfigurace.addProperty("cssPovoleno", true);
        
        ulozKonfiguraci();
    }
    
    /**
     * Uloží konfiguraci do souboru
     */
    public void ulozKonfiguraci() {
        try (FileWriter zapisovac = new FileWriter(SOUBOR_KONFIGURACE)) {
            gson.toJson(konfigurace, zapisovac);
        } catch (IOException vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Vrací řetězec hodnotu konfigurace
     * @param klic klíč konfigurace
     * @param vychozi výchozí hodnota
     * @return hodnota konfigurace
     */
    public String vraciRetezec(String klic, String vychozi) {
        if (konfigurace.has(klic) && !konfigurace.get(klic).isJsonNull()) {
            return konfigurace.get(klic).getAsString();
        }
        return vychozi;
    }
    
    /**
     * Vrací logickou hodnotu konfigurace
     * @param klic klíč konfigurace
     * @param vychozi výchozí hodnota
     * @return hodnota konfigurace
     */
    public boolean vraciLogicka(String klic, boolean vychozi) {
        if (konfigurace.has(klic) && !konfigurace.get(klic).isJsonNull()) {
            return konfigurace.get(klic).getAsBoolean();
        }
        return vychozi;
    }
    
    /**
     * Vrací celočíselnou hodnotu konfigurace
     * @param klic klíč konfigurace
     * @param vychozi výchozí hodnota
     * @return hodnota konfigurace
     */
    public int vraciCele(String klic, int vychozi) {
        if (konfigurace.has(klic) && !konfigurace.get(klic).isJsonNull()) {
            return konfigurace.get(klic).getAsInt();
        }
        return vychozi;
    }
    
    /**
     * Nastavuje řetězec hodnotu konfigurace
     * @param klic klíč konfigurace
     * @param hodnota nová hodnota
     */
    public void nastavujeRetezec(String klic, String hodnota) {
        konfigurace.addProperty(klic, hodnota);
        ulozKonfiguraci();
    }
    
    /**
     * Nastavuje logickou hodnotu konfigurace
     * @param klic klíč konfigurace
     * @param hodnota nová hodnota
     */
    public void nastavujeLogicka(String klic, boolean hodnota) {
        konfigurace.addProperty(klic, hodnota);
        ulozKonfiguraci();
    }
    
    /**
     * Nastavuje celočíselnou hodnotu konfigurace
     * @param klic klíč konfigurace
     * @param hodnota nová hodnota
     */
    public void nastavujeCele(String klic, int hodnota) {
        konfigurace.addProperty(klic, hodnota);
        ulozKonfiguraci();
    }
    
    /**
     * Vrací cestu ke složce konfigurace
     * @return cesta ke složce konfigurace
     */
    public static String vraciSlozkuKonfigurace() {
        return SLOZKA_KONFIGURACE;
    }
    
    /**
     * Vrací objektu konfigurace
     * @return konfigurační objekt
     */
    public JsonObject vraciKonfiguraci() {
        return konfigurace;
    }
}
