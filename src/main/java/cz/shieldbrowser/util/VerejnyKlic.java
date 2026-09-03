package cz.shieldbrowser.util;

import java.io.Serializable;

/**
 * Třída pro správu veřejného klíče
 */
public class VerejnyKlic implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String klicData;
    private long vytvorenAVCase;
    private boolean jePlativy;
    
    /**
     * Konstruktor
     * @param id identifikátor klíče
     * @param klicData data klíče
     */
    public VerejnyKlic(String id, String klicData) {
        this.id = id;
        this.klicData = klicData;
        this.vytvorenAVCase = System.currentTimeMillis();
        this.jePlativy = true;
    }
    
    // Gettery a settery
    public String vraciId() {
        return id;
    }
    
    public void nastavujeId(String id) {
        this.id = id;
    }
    
    public String vraciKlicData() {
        return klicData;
    }
    
    public void nastavujeKlicData(String klicData) {
        this.klicData = klicData;
    }
    
    public long vraciVytvorenAVCase() {
        return vytvorenAVCase;
    }
    
    public boolean vraciJePlativy() {
        return jePlativy;
    }
    
    public void nastavujeJePlativy(boolean jePlativy) {
        this.jePlativy = jePlativy;
    }
    
    @Override
    public String toString() {
        return "VerejnyKlic{" +
                "id='" + id + '\'' +
                ", jePlativy=" + jePlativy +
                '}';
    }
}
