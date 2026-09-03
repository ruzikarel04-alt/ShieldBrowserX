package cz.shieldbrowser.ui;

import cz.shieldbrowser.sprava.SpravceZalozek;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog pro správu záložek
 */
public class ZalozkySouborDialog extends JDialog {
    
    private SpravceZalozek spravceZalozek;
    private JList<String> listZalozek;
    private DefaultListModel<String> modelLista;
    private JTextField vyhledavaniField;
    
    /**
     * Konstruktor
     */
    public ZalozkySouborDialog(Frame vlastnik, SpravceZalozek spravceZalozek) {
        super(vlastnik, "Záložky", true);
        this.spravceZalozek = spravceZalozek;
        
        inicializujUi();
        nactiZalozky();
    }
    
    /**
     * Inicializuje UI
     */
    private void inicializujUi() {
        setSize(400, 500);
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
                vyhledejZalozky();
            }
        });
        
        hledejPanel.add(labelHledej, BorderLayout.WEST);
        hledejPanel.add(vyhledavaniField, BorderLayout.CENTER);
        
        // List záložek
        modelLista = new DefaultListModel<>();
        listZalozek = new JList<>(modelLista);
        listZalozek.setBackground(TmavyMotiv.vraPrvku());
        listZalozek.setForeground(TmavyMotiv.vraTextu());
        listZalozek.setSelectionBackground(TmavyMotiv.vraAkcnich());
        
        JScrollPane scrollPane = new JScrollPane(listZalozek);
        scrollPane.setBackground(TmavyMotiv.vraPozadi());
        
        // Panel tlačítek
        JPanel tlacitkaPanel = new JPanel(new FlowLayout());
        tlacitkaPanel.setBackground(TmavyMotiv.vraPrvku());
        
        JButton tlacitkoOtevrit = new JButton("Otevřít");
        tlacitkoOtevrit.setBackground(TmavyMotiv.vraPrvku());
        tlacitkoOtevrit.setForeground(TmavyMotiv.vraTextu());
        tlacitkoOtevrit.addActionListener(e -> otevriZalozku());
        
        JButton tlacitkoSmazat = new JButton("Smazat");
        tlacitkoSmazat.setBackground(TmavyMotiv.vraPrvku());
        tlacitkoSmazat.setForeground(TmavyMotiv.vraTextu());
        tlacitkoSmazat.addActionListener(e -> smazZalozku());
        
        tlacitkaPanel.add(tlacitkoOtevrit);
        tlacitkaPanel.add(tlacitkoSmazat);
        
        panel.add(hledejPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tlacitkaPanel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    /**
     * Načítá záložky
     */
    private void nactiZalozky() {
        modelLista.clear();
        
        for (SpravceZalozek.Zalozka zalozka : spravceZalozek.vraciVsechnyZalozky()) {
            modelLista.addElement(zalozka.vraciNazev() + " (" + zalozka.vraciUrl() + ")");
        }
    }
    
    /**
     * Vyhledává záložky
     */
    private void vyhledejZalozky() {
        modelLista.clear();
        String hledejText = vyhledavaniField.getText();
        
        if (hledejText.isEmpty()) {
            nactiZalozky();
        } else {
            for (SpravceZalozek.Zalozka zalozka : spravceZalozek.vyhledejZalozky(hledejText)) {
                modelLista.addElement(zalozka.vraciNazev() + " (" + zalozka.vraciUrl() + ")");
            }
        }
    }
    
    /**
     * Otevírá vybranou záložku
     */
    private void otevriZalozku() {
        int index = listZalozek.getSelectedIndex();
        if (index >= 0) {
            JOptionPane.showMessageDialog(this, "Otevření záložky", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Maže vybranou záložku
     */
    private void smazZalozku() {
        int index = listZalozek.getSelectedIndex();
        if (index >= 0) {
            int potvrzeni = JOptionPane.showConfirmDialog(this, "Opravdu chcete smazat tuto záložku?", "Potvrzení", JOptionPane.YES_NO_OPTION);
            if (potvrzeni == JOptionPane.YES_OPTION) {
                modelLista.remove(index);
                JOptionPane.showMessageDialog(this, "Záložka smazána", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
