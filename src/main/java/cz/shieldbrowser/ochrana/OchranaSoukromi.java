package cz.shieldbrowser.ochrana;

import cz.shieldbrowser.util.KonfiguraceNastroj;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Třída pro ochranu soukromí - implementuje všechny bezpečnostní funkce
 */
public class OchranaSoukromi {
    
    private KonfiguraceNastroj konfigurace;
    private Set<String> blokovaneTracery;
    private Set<String> blokovaneReklamy;
    private boolean soukromiMod;
    private boolean vynuceneHttps;
    
    /**
     * Konstruktor - inicializuje ochranu soukromí
     */
    public OchranaSoukromi(KonfiguraceNastroj konfigurace) {
        this.konfigurace = konfigurace;
        this.blokovaneTracery = new HashSet<>();
        this.blokovaneReklamy = new HashSet<>();
        this.soukromiMod = false;
        this.vynuceneHttps = konfigurace.vraciLogicka("vynucenoHttps", true);
        
        inicializujBlokované();
    }
    
    /**
     * Inicializuje seznamy blokovaných zdrojů
     */
    private void inicializujBlokované() {
        // Přidání známých trackerů
        blokovaneTracery.add("google-analytics.com");
        blokovaneTracery.add("facebook.com");
        blokovaneTracery.add("doubleclick.net");
        blokovaneTracery.add("analytics.google.com");
        blokovaneTracery.add("cdn.segment.com");
        blokovaneTracery.add("mixpanel.com");
        blokovaneTracery.add("amplitude.com");
        blokovaneTracery.add("intercom.io");
        
        // Přidání známých reklamních sítí
        blokovaneReklamy.add("ads.google.com");
        blokovaneReklamy.add("adservice.google.com");
        blokovaneReklamy.add("pagead2.googlesyndication.com");
        blokovaneReklamy.add("amazon-adsystem.com");
        blokovaneReklamy.add("criteo.com");
        blokovaneReklamy.add("taboola.com");
        blokovaneReklamy.add("outbrain.com");
    }
    
    /**
     * Vrací, zda je tracker blokován
     * @param domena doména trackeru
     * @return true pokud je blokován
     */
    public boolean jeBlokovanyTracker(String domena) {
        if (!konfigurace.vraciLogicka("blokovaniTrackers", true)) {
            return false;
        }
        
        for (String tracker : blokovaneTracery) {
            if (domena.contains(tracker)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vrací, zda je reklama blokována
     * @param domena doména reklamy
     * @return true pokud je blokována
     */
    public boolean jeBlokovanareklama(String domena) {
        if (!konfigurace.vraciLogicka("blokovaniReklam", true)) {
            return false;
        }
        
        for (String reklama : blokovaneReklamy) {
            if (domena.contains(reklama)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vynucuje HTTPS
     * @param url URL k přeměně
     * @return URL s HTTPS
     */
    public String vynucHttp(String url) {
        if (!vynuceneHttps) {
            return url;
        }
        
        if (url.startsWith("http://")) {
            return url.replace("http://", "https://");
        }
        return url;
    }
    
    /**
     * Povoluje/zakazuje soukromý režim
     * @param povoleno true pro povolení
     */
    public void nastavujesoukromiMod(boolean povoleno) {
        this.soukromiMod = povoleno;
    }
    
    /**
     * Vrací, zda je soukromý režim povolený
     * @return true pokud je soukromý režim povolený
     */
    public boolean jeSoukromiMod() {
        return soukromiMod;
    }
    
    /**
     * Povoluje/zakazuje vynucení HTTPS
     * @param povoleno true pro vynucení
     */
    public void nastavujeVynuceneHttps(boolean povoleno) {
        this.vynuceneHttps = povoleno;
        konfigurace.nastavujeLogicka("vynucenoHttps", povoleno);
    }
    
    /**
     * Vrací, zda je HTTPS vynuceno
     * @return true pokud je HTTPS vynuceno
     */
    public boolean jeSHttpsVynuceno() {
        return vynuceneHttps;
    }
    
    /**
     * Povoluje/zakazuje blokování trackerů
     * @param povoleno true pro povolení blokování
     */
    public void nastavujeBlokovaniTrackers(boolean povoleno) {
        konfigurace.nastavujeLogicka("blokovaniTrackers", povoleno);
    }
    
    /**
     * Povoluje/zakazuje blokování reklam
     * @param povoleno true pro povolení blokování
     */
    public void nastavujeBlokovaniReklam(boolean povoleno) {
        konfigurace.nastavujeLogicka("blokovaniReklam", povoleno);
    }
    
    /**
     * Přidává tracker do blocklist
     * @param domena doména trackeru
     */
    public void pridejTracker(String domena) {
        blokovaneTracery.add(domena);
    }
    
    /**
     * Přidává reklamu do blocklist
     * @param domena doména reklamy
     */
    public void pridejReklamu(String domena) {
        blokovaneReklamy.add(domena);
    }
    
    /**
     * Vrací seznam všech blokovaných trackerů
     * @return seznam trackerů
     */
    public List<String> vraciBlokovanéTracery() {
        return new ArrayList<>(blokovaneTracery);
    }
    
    /**
     * Vrací seznam všech blokovaných reklam
     * @return seznam reklam
     */
    public List<String> vraciBlokovanéReklamy() {
        return new ArrayList<>(blokovaneReklamy);
    }
    
    /**
     * Detekuje phishingovou stránku
     * @param url URL stránky
     * @return true pokud vypadá jako phishing
     */
    public boolean detekujePhishing(String url) {
        // Základní detekce: zkrácené domény, IP adresy, podezřelé znaky
        if (url.contains("@")) {
            return true; // @ v URL často znamená phishing
        }
        
        if (url.matches(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*")) {
            return true; // IP adresa místo domény
        }
        
        return false;
    }
}
