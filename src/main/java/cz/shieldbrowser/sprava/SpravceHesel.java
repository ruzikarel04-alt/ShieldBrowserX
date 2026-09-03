package cz.shieldbrowser.sprava;

import cz.shieldbrowser.util.SifrovacNastroj;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Správce hesel - spravuje všechna uložená hesla s šifrováním
 */
public class SpravceHesel {
    
    private static final String SLOZKA_HESEL = System.getProperty("user.home") + "/.shieldbrowserx/hesla";
    private static final String SOUBOR_HESEL = SLOZKA_HESEL + "/hesla.json";
    
    private List<Heslo> hesla;
    private Gson gson;
    private SifrovacNastroj sifrovac;
    
    /**
     * Konstruktor - inicializuje správce hesel
     */
    public SpravceHesel(SifrovacNastroj sifrovac) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        hesla = new ArrayList<>();
        this.sifrovac = sifrovac;
        
        // Vytvoření složky pro hesla
        File slozka = new File(SLOZKA_HESEL);
        if (!slozka.exists()) {
            slozka.mkdirs();
        }
        
        nactiHesla();
    }
    
    /**
     * Načítá hesla ze souboru
     */
    private void nactiHesla() {
        File soubor = new File(SOUBOR_HESEL);
        
        if (soubor.exists()) {
            try (FileReader ctecka = new FileReader(soubor)) {
                JsonObject objektJson = gson.fromJson(ctecka, JsonObject.class);
                
                if (objektJson != null && objektJson.has("hesla")) {
                    JsonArray poljeHesel = objektJson.getAsJsonArray("hesla");
                    poljeHesel.forEach(prvek -> {
                        JsonObject hesloJson = prvek.getAsJsonObject();
                        Heslo heslo = new Heslo(
                            hesloJson.get("id").getAsString(),
                            hesloJson.get("stranka").getAsString(),
                            hesloJson.get("uzivatelJmeno").getAsString(),
                            sifrovac.desifrujeText(hesloJson.get("heslo").getAsString()),
                            hesloJson.get("email").getAsString()
                        );
                        hesla.add(heslo);
                    });
                }
            } catch (IOException vyjimka) {
                vyjimka.printStackTrace();
            }
        }
    }
    
    /**
     * Ukládá hesla do souboru
     */
    public void ulozHesla() {
        try (FileWriter zapisovac = new FileWriter(SOUBOR_HESEL)) {
            JsonObject objekt = new JsonObject();
            JsonArray poljeHesel = new JsonArray();
            
            for (Heslo heslo : hesla) {
                JsonObject hesloJson = new JsonObject();
                hesloJson.addProperty("id", heslo.vraciId());
                hesloJson.addProperty("stranka", heslo.vraciStranku());
                hesloJson.addProperty("uzivatelJmeno", heslo.vraciUzivatelJmeno());
                hesloJson.addProperty("heslo", sifrovac.sifrujeText(heslo.vraciHeslo()));
                hesloJson.addProperty("email", heslo.vraciEmail());
                poljeHesel.add(hesloJson);
            }
            
            objekt.add("hesla", poljeHesel);
            gson.toJson(objekt, zapisovac);
        } catch (IOException vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Přidává nové heslo
     * @param stranka stránka
     * @param uzivatelJmeno uživatelské jméno
     * @param heslo heslo
     * @param email email
     */
    public void pridejHeslo(String stranka, String uzivatelJmeno, String heslo, String email) {
        Heslo noveheslo = new Heslo(
            UUID.randomUUID().toString(),
            stranka,
            uzivatelJmeno,
            heslo,
            email
        );
        hesla.add(noveheslo);
        ulozHesla();
    }
    
    /**
     * Vyhledává hesla podle stránky
     * @param hledejText text k hledání
     * @return seznam nalezených hesel
     */
    public List<Heslo> vyhledejHesla(String hledejText) {
        List<Heslo> vysledky = new ArrayList<>();
        
        for (Heslo heslo : hesla) {
            if (heslo.vraciStranku().toLowerCase().contains(hledejText.toLowerCase()) ||
                heslo.vraciUzivatelJmeno().toLowerCase().contains(hledejText.toLowerCase())) {
                vysledky.add(heslo);
            }
        }
        
        return vysledky;
    }
    
    /**
     * Vrací heslo pro určitou stránku
     * @param stranka stránka
     * @return heslo nebo null
     */
    public Heslo vraciHesloProStranku(String stranka) {
        for (Heslo heslo : hesla) {
            if (heslo.vraciStranku().equalsIgnoreCase(stranka)) {
                return heslo;
            }
        }
        return null;
    }
    
    /**
     * Mazá heslo
     * @param id ID hesla
     */
    public void mazHeslo(String id) {
        hesla.removeIf(h -> h.vraciId().equals(id));
        ulozHesla();
    }
    
    /**
     * Vrací všechna hesla
     * @return seznam všech hesel
     */
    public List<Heslo> vraciVsechnaHesla() {
        return new ArrayList<>(hesla);
    }
    
    /**
     * Generuje silné heslo
     * @param delka délka hesla
     * @return generované heslo
     */
    public String generujSilneHeslo(int delka) {
        return SifrovacNastroj.generujeNahodneHeslo(delka);
    }
    
    /**
     * Kontroluje sílu hesla
     * @param heslo heslo k kontrole
     * @return úroveň síly (1-5)
     */
    public int kontrolujSiluHesla(String heslo) {
        return SifrovacNastroj.kontrolujeMaxHesla(heslo);
    }
    
    /**
     * Třída reprezentující jedno heslo
     */
    public static class Heslo {
        private String id;
        private String stranka;
        private String uzivatelJmeno;
        private String heslo;
        private String email;
        
        public Heslo(String id, String stranka, String uzivatelJmeno, String heslo, String email) {
            this.id = id;
            this.stranka = stranka;
            this.uzivatelJmeno = uzivatelJmeno;
            this.heslo = heslo;
            this.email = email;
        }
        
        public String vraciId() { return id; }
        public String vraciStranku() { return stranka; }
        public String vraciUzivatelJmeno() { return uzivatelJmeno; }
        public String vraciHeslo() { return heslo; }
        public String vraciEmail() { return email; }
        
        public void nastavujeHeslo(String heslo) { this.heslo = heslo; }
    }
}
