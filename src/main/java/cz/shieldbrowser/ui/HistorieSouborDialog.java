package cz.shieldbrowser.ui;

import cz.shieldbrowser.sprava.SpravceHistorie;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog pro správu historie
 */
public class HistorieSouborDialog extends JDialog {
    
    private SpravceHistorie spravceHistorie;
    private JList<String> listHistorie;
    private DefaultListModel<String> modelLista;
    private JTextField vyhledavaniField;
    
    /**
     * Konstruktor
     */
    public HistorieSouborDialog(Frame vlastnik, SpravceHistorie spravceHistorie) {
        super(vlastnik, "Historie", true);
        this.spravceHistorie = spravceHistorie;
        
        inicializujUi();
        nactiHistorii();
    }
    
    /**
     * Inicializuje UI
     */
    private void inicializujUi() {
        setSize(500, 500);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBackground(TmavyMotiv.vraPozadi());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TmavyMotiv.vraPozadi());
        
        // Panel vyhledávání
        JPanel hledejPanel = new JPanel(new BorderLayout());
        hledejPanel.setBackground(TmavyMotiv.vraPrvku());
        hledejPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel labelHledej = new JLabel("Hledat:");
        labelHledej.setForeground(TmavyMotiv.vraTextu());
        
        vyhledavaniField = new JTextField();
        vyhledavaniField.setBackground(TmavyMotiv.vraPozadi());
        vyhledavaniField.setForeground(TmavyMotiv.vraTextu());
        vyhledavaniField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                vyhledejVHistorii();
            }
        });
        
        hledejPanel.add(labelHledej, BorderLayout.WEST);
        hledejPanel.add(vyhledavaniField, BorderLayout.CENTER);
        
        // List historie
        modelLista = new DefaultListModel<>();
        listHistorie = new JList<>(modelLista);
        listHistorie.setBackground(TmavyMotiv.vraPrvku());
        listHistorie.setForeground(TmavyMotiv.vraTextu());
        listHistorie.setSelectionBackground(TmavyMotiv.vraAkcnich());
        
        JScrollPane scrollPane = new JScrollPane(listHistorie);
        scrollPane.setBackground(TmavyMotiv.vraPozadi());
        
        // Panel tlačítek
        JPanel tlacitkaPanel = new JPanel(new FlowLayout());
        tlacitkaPanel.setBackground(TmavyMotiv.vraPrvku());
        
        JButton tlacitkoMazat = new JButton("Smazat vybranou");
        tlacitkoMazat.setBackground(TmavyMotiv.vraPrvku());
        tlacitkoMazat.setForeground(TmavyMotiv.vraTextu());
        tlacitkoMazat.addActionListener(e -> smazzPolozku());
        
        JButton tlacitkoMazatVse = new JButton("Smazat vše");
        tlacitkoMazatVse.setBackground(TmavyMotiv.vraPrvku());
        tlacitkoMazatVse.setForeground(TmavyMotiv.vraTextu());
        tlacitkoMazatVse.addActionListener(e -> mazVsechno());
        
        tlacitkaPanel.add(tlacitkoMazat);
        tlacitkaPanel.add(tlacitkoMazatVse);
        
        panel.add(hledejPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tlacitkaPanel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    /**
     * Načítá historii
     */
    private void nactiHistorii() {
        modelLista.clear();
        
        for (SpravceHistorie.PolozkaHistorie polozka : spravceHistorie.vraciVsechnyPolozky()) {
            modelLista.addElement(polozka.vraciNazev() + " - " + polozka.vraciUrl() + " (" + polozka.vraciNavstiveno() + ")");
        }
    }
    
    /**
     * Vyhledává v historii
     */
    private void vyhledejVHistorii() {
        modelLista.clear();
        String hledejText = vyhledavaniField.getText();
        
        if (hledejText.isEmpty()) {
            nactiHistorii();
        } else {
            for (SpravceHistorie.PolozkaHistorie polozka : spravceHistorie.vyhledejVHistorii(hledejText)) {
                modelLista.addElement(polozka.vraciNazev() + " - " + polozka.vraciUrl());
            }
        }
    }
    
    /**
     * Maže vybranou položku
     */
    private void smazzPolozku() {
        int index = listHistorie.getSelectedIndex();
        if (index >= 0) {
            int potvrzeni = JOptionPane.showConfirmDialog(this, "Opravdu chcete smazat tuto položku?", "Potvrzení", JOptionPane.YES_NO_OPTION);
            if (potvrzeni == JOptionPane.YES_OPTION) {
                modelLista.remove(index);
                JOptionPane.showMessageDialog(this, "Položka smazána", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    /**
     * Maže veškerou historii
     */
    private void mazVsechno() {
        int potvrzeni = JOptionPane.showConfirmDialog(this, "Opravdu chcete smazat veškerou historii?", "Potvrzení", JOptionPane.YES_NO_OPTION);
        if (potvrzeni == JOptionPane.YES_OPTION) {
            spravceHistorie.mazCelkuHistorii();
            modelLista.clear();
            JOptionPane.showMessageDialog(this, "Historie vymazána", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
