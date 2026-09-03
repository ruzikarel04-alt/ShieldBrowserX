package cz.shieldbrowser.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * Vlastní UI theme pro tmavý motiv aplikace
 */
public class TmavyMotiv extends UIManager.LookAndFeelInfo {
    
    private static final Color BARVA_POZADI = new Color(30, 30, 35);
    private static final Color BARVA_PRVKU = new Color(45, 45, 50);
    private static final Color BARVA_TEXTU = new Color(230, 230, 230);
    private static final Color BARVA_AKCNICH = new Color(100, 150, 255);
    private static final Color BARVA_OKRAJE = new Color(60, 60, 65);
    
    /**
     * Konstruktor
     */
    public TmavyMotiv() {
        super("Shield Dark", "cz.shieldbrowser.ui.TmavyMotiv", null);
    }
    
    /**
     * Aplikuje motiv
     */
    public static void aplikujMotiv() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Barva pozadí
            UIManager.put("Panel.background", BARVA_POZADI);
            UIManager.put("Frame.background", BARVA_POZADI);
            UIManager.put("Dialog.background", BARVA_POZADI);
            
            // Barva textu
            UIManager.put("Label.foreground", BARVA_TEXTU);
            UIManager.put("TextField.foreground", BARVA_TEXTU);
            UIManager.put("TextArea.foreground", BARVA_TEXTU);
            UIManager.put("Button.foreground", BARVA_TEXTU);
            
            // Pozadí prvků
            UIManager.put("TextField.background", BARVA_PRVKU);
            UIManager.put("TextArea.background", BARVA_PRVKU);
            UIManager.put("Button.background", BARVA_PRVKU);
            UIManager.put("ComboBox.background", BARVA_PRVKU);
            UIManager.put("MenuBar.background", BARVA_PRVKU);
            UIManager.put("Menu.background", BARVA_PRVKU);
            UIManager.put("MenuItem.background", BARVA_PRVKU);
            UIManager.put("List.background", BARVA_PRVKU);
            UIManager.put("Table.background", BARVA_PRVKU);
            
            // Barvy akcí
            UIManager.put("Button.select", BARVA_AKCNICH);
            UIManager.put("Menu.selectionBackground", BARVA_AKCNICH);
            UIManager.put("MenuItem.selectionBackground", BARVA_AKCNICH);
            UIManager.put("List.selectionBackground", BARVA_AKCNICH);
            UIManager.put("Table.selectionBackground", BARVA_AKCNICH);
            
            // Okraje
            UIManager.put("TextField.border", BorderFactory.createLineBorder(BARVA_OKRAJE));
            UIManager.put("TextArea.border", BorderFactory.createLineBorder(BARVA_OKRAJE));
            UIManager.put("ComboBox.border", BorderFactory.createLineBorder(BARVA_OKRAJE));
            
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Vrací barvu pozadí
     */
    public static Color vraPozadi() {
        return BARVA_POZADI;
    }
    
    /**
     * Vrací barvu prvků
     */
    public static Color vraPrvku() {
        return BARVA_PRVKU;
    }
    
    /**
     * Vrací barvu textu
     */
    public static Color vraTextu() {
        return BARVA_TEXTU;
    }
    
    /**
     * Vrací barvu akčních prvků
     */
    public static Color vraAkcnich() {
        return BARVA_AKCNICH;
    }
    
    /**
     * Vrací barvu okraje
     */
    public static Color vraOkraje() {
        return BARVA_OKRAJE;
    }
}
