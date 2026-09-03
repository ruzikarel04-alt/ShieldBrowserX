package cz.shieldbrowser.sprava;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Správce historie - spravuje historii navštívených stránek
 */
public class SpravceHistorie {
    
    private static final String SLOZKA_HISTORIE = System.getProperty("user.home") + "/.shieldbrowserx/historie";
    private static final String SOUBOR_HISTORIE = SLOZKA_HISTORIE + "/historie.json";
    private static final DateTimeFormatter FORMATOR = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private List<PolozkaHistorie> historie;
    private Gson gson;
    
    /**
     * Konstruktor - inicializuje správce historie
     */
    public SpravceHistorie() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        historie = new ArrayList<>();
        
        // Vytvoření složky pro historii
        File slozka = new File(SLOZKA_HISTORIE);
        if (!slozka.exists()) {
            slozka.mkdirs();
        }
        
        nactiHistorii();
    }
    
    /**
     * Načítá historii ze souboru
     */
    private void nactiHistorii() {
        File soubor = new File(SOUBOR_HISTORIE);
        
        if (soubor.exists()) {
            try (FileReader ctecka = new FileReader(soubor)) {
                JsonObject objektJson = gson.fromJson(ctecka, JsonObject.class);
                
                if (objektJson != null && objektJson.has("historie")) {
                    JsonArray poljeHistorie = objektJson.getAsJsonArray("historie");
                    poljeHistorie.forEach(prvek -> {
                        JsonObject polozkaJson = prvek.getAsJsonObject();
                        PolozkaHistorie polozka = new PolozkaHistorie(
                            polozkaJson.get("id").getAsString(),
                            polozkaJson.get("url").getAsString(),
                            polozkaJson.get("nazev").getAsString(),
                            polozkaJson.get("navstiv").getAsString(),
                            polozkaJson.get("cas").getAsLong()
                        );
                        historie.add(polozka);
                    });
                }
            } catch (IOException vyjimka) {
                vyjimka.printStackTrace();
            }
        }
    }
    
    /**
     * Ukládá historii do souboru
     */
    public void ulozHistorii() {
        try (FileWriter zapisovac = new FileWriter(SOUBOR_HISTORIE)) {
            JsonObject objekt = new JsonObject();
            JsonArray poljeHistorie = new JsonArray();
            
            for (PolozkaHistorie polozka : historie) {
                JsonObject polozkaJson = new JsonObject();
                polozkaJson.addProperty("id", polozka.vraciId());
                polozkaJson.addProperty("url", polozka.vraciUrl());
                polozkaJson.addProperty("nazev", polozka.vraciNazev());
                polozkaJson.addProperty("navstiv", polozka.vraciNavstiveno());
                polozkaJson.addProperty("cas", polozka.vraciCas());
                poljeHistorie.add(polozkaJson);
            }
            
            objekt.add("historie", poljeHistorie);
            gson.toJson(objekt, zapisovac);
        } catch (IOException vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Přidává novou položku do historie
     * @param url URL stránky
     * @param nazev název stránky
     */
    public void pridejDoHistorie(String url, String nazev) {
        PolozkaHistorie polozka = new PolozkaHistorie(
            UUID.randomUUID().toString(),
            url,
            nazev,
            LocalDateTime.now().format(FORMATOR),
            System.currentTimeMillis()
        );
        
        // Odebrání duplikátu pokud existuje
        historie.removeIf(p -> p.vraciUrl().equals(url));
        
        // Přidání nové položky na začátek
        historie.add(0, polozka);
        ulozHistorii();
    }
    
    /**
     * Vyhledává v historii
     * @param hledejText text k hledání
     * @return seznam nalezených položek
     */
    public List<PolozkaHistorie> vyhledejVHistorii(String hledejText) {
        List<PolozkaHistorie> vysledky = new ArrayList<>();
        
        for (PolozkaHistorie polozka : historie) {
            if (polozka.vraciUrl().toLowerCase().contains(hledejText.toLowerCase()) ||
                polozka.vraciNazev().toLowerCase().contains(hledejText.toLowerCase())) {
                vysledky.add(polozka);
            }
        }
        
        return vysledky;
    }
    
    /**
     * Vrací všechny položky v historii
     * @return seznam všech položek
     */
    public List<PolozkaHistorie> vraciVsechnyPolozky() {
        return new ArrayList<>(historie);
    }
    
    /**
     * Vrací poslední N položek
     * @param pocet počet položek
     * @return seznam posledních položek
     */
    public List<PolozkaHistorie> vraciPosledniPolozky(int pocet) {
        List<PolozkaHistorie> vysledky = new ArrayList<>();
        
        for (int i = 0; i < Math.min(pocet, historie.size()); i++) {
            vysledky.add(historie.get(i));
        }
        
        return vysledky;
    }
    
    /**
     * Mazá položku z historie
     * @param id ID položky
     */
    public void mazPolozku(String id) {
        historie.removeIf(p -> p.vraciId().equals(id));
        ulozHistorii();
    }
    
    /**
     * Maže celou historii
     */
    public void mazCelkuHistorii() {
        historie.clear();
        ulozHistorii();
    }
    
    /**
     * Maže historii podle domény
     * @param domena doména k vymazání
     */
    public void mazHistoriiPoDomene(String domena) {
        historie.removeIf(p -> p.vraciUrl().contains(domena));
        ulozHistorii();
    }
    
    /**
     * Třída reprezentující jednu položku historie
     */
    public static class PolozkaHistorie {
        private String id;
        private String url;
        private String nazev;
        private String navstiveno;
        private long cas;
        
        public PolozkaHistorie(String id, String url, String nazev, String navstiveno, long cas) {
            this.id = id;
            this.url = url;
            this.nazev = nazev;
            this.navstiveno = navstiveno;
            this.cas = cas;
        }
        
        public String vraciId() { return id; }
        public String vraciUrl() { return url; }
        public String vraciNazev() { return nazev; }
        public String vraciNavstiveno() { return navstiveno; }
        public long vraciCas() { return cas; }
    }
}
