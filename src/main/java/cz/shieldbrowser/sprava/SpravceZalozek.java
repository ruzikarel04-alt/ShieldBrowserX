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
 * Správce záložek - spravuje všechny uložené záložky
 */
public class SpravceZalozek {
    
    private static final String SLOZKA_ZALOZEK = System.getProperty("user.home") + "/.shieldbrowserx/zalozky";
    private static final String SOUBOR_ZALOZEK = SLOZKA_ZALOZEK + "/zalozky.json";
    private static final DateTimeFormatter FORMATOR = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private List<Zalozka> zalozky;
    private List<SlozkuZalozek> slozky;
    private Gson gson;
    
    /**
     * Konstruktor - inicializuje správce záložek
     */
    public SpravceZalozek() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        zalozky = new ArrayList<>();
        slozky = new ArrayList<>();
        
        // Vytvoření složky pro záložky
        File slozka = new File(SLOZKA_ZALOZEK);
        if (!slozka.exists()) {
            slozka.mkdirs();
        }
        
        nactiZalozky();
    }
    
    /**
     * Načítá záložky ze souboru
     */
    private void nactiZalozky() {
        File soubor = new File(SOUBOR_ZALOZEK);
        
        if (soubor.exists()) {
            try (FileReader ctecka = new FileReader(soubor)) {
                JsonObject objektJson = gson.fromJson(ctecka, JsonObject.class);
                
                if (objektJson != null && objektJson.has("zalozky")) {
                    JsonArray poljeZalozek = objektJson.getAsJsonArray("zalozky");
                    poljeZalozek.forEach(prvek -> {
                        JsonObject zalozkaJson = prvek.getAsJsonObject();
                        Zalozka zalozka = new Zalozka(
                            zalozkaJson.get("id").getAsString(),
                            zalozkaJson.get("nazev").getAsString(),
                            zalozkaJson.get("url").getAsString(),
                            zalozkaJson.get("slozkaId").getAsString(),
                            zalozkaJson.get("pridat").getAsString()
                        );
                        zalozky.add(zalozka);
                    });
                }
                
                if (objektJson != null && objektJson.has("slozky")) {
                    JsonArray poljeSlozek = objektJson.getAsJsonArray("slozky");
                    poljeSlozek.forEach(prvek -> {
                        JsonObject slozkaJson = prvek.getAsJsonObject();
                        SlozkuZalozek slozka = new SlozkuZalozek(
                            slozkaJson.get("id").getAsString(),
                            slozkaJson.get("nazev").getAsString(),
                            slozkaJson.get("rodiceId").getAsString()
                        );
                        slozky.add(slozka);
                    });
                }
            } catch (IOException vyjimka) {
                vyjimka.printStackTrace();
            }
        }
    }
    
    /**
     * Ukládá záložky do souboru
     */
    public void ulozZalozky() {
        try (FileWriter zapisovac = new FileWriter(SOUBOR_ZALOZEK)) {
            JsonObject objekt = new JsonObject();
            JsonArray poljeZalozek = new JsonArray();
            JsonArray poljeSlozek = new JsonArray();
            
            // Uložení záložek
            for (Zalozka zalozka : zalozky) {
                JsonObject zalozkaJson = new JsonObject();
                zalozkaJson.addProperty("id", zalozka.vraciId());
                zalozkaJson.addProperty("nazev", zalozka.vraciNazev());
                zalozkaJson.addProperty("url", zalozka.vraciUrl());
                zalozkaJson.addProperty("slozkaId", zalozka.vraciSlozkuId());
                zalozkaJson.addProperty("pridat", zalozka.vraciDatumPridani());
                poljeZalozek.add(zalozkaJson);
            }
            
            // Uložení složek
            for (SlozkuZalozek slozka : slozky) {
                JsonObject slozkaJson = new JsonObject();
                slozkaJson.addProperty("id", slozka.vraciId());
                slozkaJson.addProperty("nazev", slozka.vraciNazev());
                slozkaJson.addProperty("rodiceId", slozka.vraciRodiceId());
                poljeSlozek.add(slozkaJson);
            }
            
            objekt.add("zalozky", poljeZalozek);
            objekt.add("slozky", poljeSlozek);
            
            gson.toJson(objekt, zapisovac);
        } catch (IOException vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Přidává novou záložku
     * @param nazev název záložky
     * @param url URL záložky
     */
    public void pridejZalozku(String nazev, String url) {
        Zalozka zalozka = new Zalozka(
            UUID.randomUUID().toString(),
            nazev,
            url,
            "koren",
            LocalDateTime.now().format(FORMATOR)
        );
        zalozky.add(zalozka);
        ulozZalozky();
    }
    
    /**
     * Přidává novou záložku do složky
     * @param nazev název záložky
     * @param url URL záložky
     * @param slozkaId ID složky
     */
    public void pridejZalozku(String nazev, String url, String slozkaId) {
        Zalozka zalozka = new Zalozka(
            UUID.randomUUID().toString(),
            nazev,
            url,
            slozkaId,
            LocalDateTime.now().format(FORMATOR)
        );
        zalozky.add(zalozka);
        ulozZalozky();
    }
    
    /**
     * Vytváří novou složku pro záložky
     * @param nazev název složky
     * @return ID nové složky
     */
    public String vytvorSlozku(String nazev) {
        String id = UUID.randomUUID().toString();
        SlozkuZalozek slozka = new SlozkuZalozek(id, nazev, "koren");
        slozky.add(slozka);
        ulozZalozky();
        return id;
    }
    
    /**
     * Mazá záložku
     * @param id ID záložky
     */
    public void mazZalozku(String id) {
        zalozky.removeIf(z -> z.vraciId().equals(id));
        ulozZalozky();
    }
    
    /**
     * Mazá složku
     * @param id ID složky
     */
    public void mazSlozku(String id) {
        // Mazání všech záložek v této složce
        zalozky.removeIf(z -> z.vraciSlozkuId().equals(id));
        // Mazání samotné složky
        slozky.removeIf(s -> s.vraciId().equals(id));
        ulozZalozky();
    }
    
    /**
     * Vyhledává záložky podle názvu
     * @param hledejText text k hledání
     * @return seznam nalezených záložek
     */
    public List<Zalozka> vyhledejZalozky(String hledejText) {
        List<Zalozka> vysledky = new ArrayList<>();
        
        for (Zalozka zalozka : zalozky) {
            if (zalozka.vraciNazev().toLowerCase().contains(hledejText.toLowerCase()) ||
                zalozka.vraciUrl().toLowerCase().contains(hledejText.toLowerCase())) {
                vysledky.add(zalozka);
            }
        }
        
        return vysledky;
    }
    
    /**
     * Vrací všechny záložky
     * @return seznam všech záložek
     */
    public List<Zalozka> vraciVsechnyZalozky() {
        return new ArrayList<>(zalozky);
    }
    
    /**
     * Vrací všechny složky
     * @return seznam všech složek
     */
    public List<SlozkuZalozek> vraciVsechnySlozky() {
        return new ArrayList<>(slozky);
    }
    
    /**
     * Vrací záložky v určité složce
     * @param slozkaId ID složky
     * @return seznam záložek v složce
     */
    public List<Zalozka> vraciZalozkyVeSlozte(String slozkaId) {
        List<Zalozka> vysledky = new ArrayList<>();
        
        for (Zalozka zalozka : zalozky) {
            if (zalozka.vraciSlozkuId().equals(slozkaId)) {
                vysledky.add(zalozka);
            }
        }
        
        return vysledky;
    }
    
    /**
     * Exportuje záložky do HTML souboru
     * @param cesta cesta pro export
     */
    public void exportujDoHtml(String cesta) {
        try (FileWriter zapisovac = new FileWriter(cesta)) {
            zapisovac.write("<!DOCTYPE html>\n");
            zapisovac.write("<html>\n");
            zapisovac.write("<head>\n");
            zapisovac.write("<title>Záložky ShieldBrowserX</title>\n");
            zapisovac.write("<meta charset='UTF-8'>\n");
            zapisovac.write("</head>\n");
            zapisovac.write("<body>\n");
            
            for (Zalozka zalozka : zalozky) {
                zapisovac.write("<p><a href=\"" + zalozka.vraciUrl() + "\">" + zalozka.vraciNazev() + "</a></p>\n");
            }
            
            zapisovac.write("</body>\n");
            zapisovac.write("</html>\n");
        } catch (IOException vyjimka) {
            vyjimka.printStackTrace();
        }
    }
    
    /**
     * Třída reprezentující jednu záložku
     */
    public static class Zalozka {
        private String id;
        private String nazev;
        private String url;
        private String slozkaId;
        private String datumPridani;
        
        public Zalozka(String id, String nazev, String url, String slozkaId, String datumPridani) {
            this.id = id;
            this.nazev = nazev;
            this.url = url;
            this.slozkaId = slozkaId;
            this.datumPridani = datumPridani;
        }
        
        public String vraciId() { return id; }
        public String vraciNazev() { return nazev; }
        public String vraciUrl() { return url; }
        public String vraciSlozkuId() { return slozkaId; }
        public String vraciDatumPridani() { return datumPridani; }
        
        public void nastavujeNazev(String nazev) { this.nazev = nazev; }
        public void nastavujeUrl(String url) { this.url = url; }
    }
    
    /**
     * Třída reprezentující složku pro záložky
     */
    public static class SlozkuZalozek {
        private String id;
        private String nazev;
        private String rodiceId;
        
        public SlozkuZalozek(String id, String nazev, String rodiceId) {
            this.id = id;
            this.nazev = nazev;
            this.rodiceId = rodiceId;
        }
        
        public String vraciId() { return id; }
        public String vraciNazev() { return nazev; }
        public String vraciRodiceId() { return rodiceId; }
        
        public void nastavujeNazev(String nazev) { this.nazev = nazev; }
    }
}
