package cz.shieldbrowser.ui;

import cz.shieldbrowser.sprava.SpravceZalozek;
import cz.shieldbrowser.sprava.SpravceHistorie;
import cz.shieldbrowser.sprava.SpravceHesel;
import cz.shieldbrowser.ochrana.OchranaSoukromi;
import cz.shieldbrowser.util.KonfiguraceNastroj;
import cz.shieldbrowser.util.LogovacNastroj;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hlavní okno prohlížeče ShieldBrowserX
 */
public class HlavniOkno extends JFrame {
    
    private KonfiguraceNastroj konfigurace;
    private LogovacNastroj logger;
    private SpravceZalozek spravceZalozek;
    private SpravceHistorie spravceHistorie;
    private SpravceHesel spravceHesel;
    private OchranaSoukromi ochranaS;
    
    // UI komponenty
    private JTabbedPane zalozkPanel;
    private JTextField adresniRadek;
    private JButton tlacitkoZpet;
    private JButton tlacitkoVpred;
    private JButton tlacitkoObnovit;
    private JButton tlacitkoHome;
    private JButton tlacitkoZalozka;
    private JButton tlacitkoMenu;
    private JLabel stavovyRadek;
    private JFXPanel jfxPanelWebView;
    private WebEngine webEngine;
    private JPanel toolbarPanel;
    private JScrollPane scrollPane;
    
    private List<ZalozkaProzoru> otevrenezalozky;
    
    /**
     * Konstruktor - inicializuje hlavní okno
     */
    public HlavniOkno(KonfiguraceNastroj konfigurace, LogovacNastroj logger,
                      SpravceZalozek spravceZalozek, SpravceHistorie spravceHistorie,
                      SpravceHesel spravceHesel, OchranaSoukromi ochrana) {
        super("ShieldBrowserX - Bezpečný webový prohlížeč");
        
        this.konfigurace = konfigurace;
        this.logger = logger;
        this.spravceZalozek = spravceZalozek;
        this.spravceHistorie = spravceHistorie;
        this.spravceHesel = spravceHesel;
        this.ochranaS = ochrana;
        this.otevrenezalozky = new ArrayList<>();
        
        // Inicializace komponenty
        inicializujOkno();
        inicializujToolbar();
        inicializujWebView();
        inicializujZalozkovy();
        inicializujStavovyRadek();
        inicializujMenuBar();
        
        logger.info("Hlavní okno inicializováno");
    }
    
    /**
     * Inicializuje okno
     */
    private void inicializujOkno() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(TmavyMotiv.vraPozadi());
    }
    
    /**
     * Inicializuje nástrojovou lištu
     */
    private void inicializujToolbar() {
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolbarPanel.setBackground(TmavyMotiv.vraPrvku());
        toolbarPanel.setPreferredSize(new Dimension(getWidth(), 50));
        
        // Tlačítko Zpět
        tlacitkoZpet = vytvorTlacitko("⬅ Zpět");
        tlacitkoZpet.addActionListener(e -> navigujZpet());
        toolbarPanel.add(tlacitkoZpet);
        
        // Tlačítko Vpřed
        tlacitkoVpred = vytvorTlacitko("Vpřed ➡");
        tlacitkoVpred.addActionListener(e -> navigujVpred());
        toolbarPanel.add(tlacitkoVpred);
        
        // Tlačítko Obnovit
        tlacitkoObnovit = vytvorTlacitko("🔄 Obnovit");
        tlacitkoObnovit.addActionListener(e -> obnoviStranku());
        toolbarPanel.add(tlacitkoObnovit);
        
        // Tlačítko Home
        tlacitkoHome = vytvorTlacitko("🏠 Domů");
        tlacitkoHome.addActionListener(e -> navigujDomů());
        toolbarPanel.add(tlacitkoHome);
        
        // Oddělení
        toolbarPanel.add(new JSeparator(JSeparator.VERTICAL));
        
        // Adresní řádek
        adresniRadek = new JTextField();
        adresniRadek.setBackground(TmavyMotiv.vraPrvku());
        adresniRadek.setForeground(TmavyMotiv.vraTextu());
        adresniRadek.setPreferredSize(new Dimension(400, 30));
        adresniRadek.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    navigujNaUrl(adresniRadek.getText());
                }
            }
        });
        toolbarPanel.add(adresniRadek);
        
        // Tlačítko Záložka
        tlacitkoZalozka = vytvorTlacitko("⭐ Záložka");
        tlacitkoZalozka.addActionListener(e -> pridejZalozku());
        toolbarPanel.add(tlacitkoZalozka);
        
        // Tlačítko Menu
        tlacitkoMenu = vytvorTlacitko("☰ Menu");
        tlacitkoMenu.addActionListener(e -> zobrazMenu());
        toolbarPanel.add(tlacitkoMenu);
        
        add(toolbarPanel, BorderLayout.NORTH);
    }
    
    /**
     * Vytváří tlačítko s vlastním stylem
     */
    private JButton vytvorTlacitko(String text) {
        JButton tlacitko = new JButton(text);
        tlacitko.setBackground(TmavyMotiv.vraPrvku());
        tlacitko.setForeground(TmavyMotiv.vraTextu());
        tlacitko.setFocusPainted(false);
        tlacitko.setBorderPainted(false);
        tlacitko.setPreferredSize(new Dimension(100, 35));
        tlacitko.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efekt při najetí
        tlacitko.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tlacitko.setBackground(TmavyMotiv.vraAkcnich());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                tlacitko.setBackground(TmavyMotiv.vraPrvku());
            }
        });
        
        return tlacitko;
    }
    
    /**
     * Inicializuje WebView pro zobrazení stránek
     */
    private void inicializujWebView() {
        jfxPanelWebView = new JFXPanel();
        jfxPanelWebView.setBackground(TmavyMotiv.vraPozadi());
        
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webEngine = webView.getEngine();
            
            // Nastavení WebEngine
            webEngine.setOnStatusChanged(event -> {
                stavovyRadek.setText(event.getData());
            });
            
            webEngine.load("about:blank");
            
            Scene scene = new Scene(webView);
            scene.setFill(javafx.scene.paint.Color.web("#1e1e23"));
            jfxPanelWebView.setScene(scene);
        });
        
        add(jfxPanelWebView, BorderLayout.CENTER);
    }
    
    /**
     * Inicializuje panel s tabulátory (záložkami)
     */
    private void inicializujZalozkovy() {
        zalozkPanel = new JTabbedPane();
        zalozkPanel.setBackground(TmavyMotiv.vraPrvku());
        zalozkPanel.setForeground(TmavyMotiv.vraTextu());
        
        // Přidání posluchače na změny
        zalozkPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = zalozkPanel.getSelectedIndex();
                if (index >= 0 && index < otevrenezalozky.size()) {
                    ZalozkaProzoru zalozka = otevrenezalozky.get(index);
                    adresniRadek.setText(zalozka.vraciUrl());
                }
            }
        });
        
        // Přidání první (prázdné) záložky
        pridejNovouZalozku();
    }
    
    /**
     * Inicializuje stavový řádek
     */
    private void inicializujStavovyRadek() {
        stavovyRadek = new JLabel("Připraven");
        stavovyRadek.setBackground(TmavyMotiv.vraPrvku());
        stavovyRadek.setForeground(TmavyMotiv.vraTextu());
        stavovyRadek.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        stavovyRadek.setOpaque(true);
        
        add(stavovyRadek, BorderLayout.SOUTH);
    }
    
    /**
     * Inicializuje menu bar
     */
    private void inicializujMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(TmavyMotiv.vraPrvku());
        menuBar.setForeground(TmavyMotiv.vraTextu());
        
        // Menu Soubor
        JMenu menuSoubor = new JMenu("Soubor");
        menuSoubor.setForeground(TmavyMotiv.vraTextu());
        
        JMenuItem novaZalozka = new JMenuItem("Nová záložka");
        novaZalozka.addActionListener(e -> pridejNovouZalozku());
        menuSoubor.add(novaZalozka);
        
        JMenuItem uzavritAplika = new JMenuItem("Zavřít aplikaci");
        uzavritAplika.addActionListener(e -> System.exit(0));
        menuSoubor.add(uzavritAplika);
        
        // Menu Úpravy
        JMenu menuUpravy = new JMenu("Úpravy");
        menuUpravy.setForeground(TmavyMotiv.vraTextu());
        
        JMenuItem nastaveni = new JMenuItem("Nastavení");
        nastaveni.addActionListener(e -> zobrazNastaveni());
        menuUpravy.add(nastaveni);
        
        // Menu Vyhledávání
        JMenu menuVyhledavani = new JMenu("Vyhledávání");
        menuVyhledavani.setForeground(TmavyMotiv.vraTextu());
        
        JMenuItem zalozky = new JMenuItem("Záložky");
        zalozky.addActionListener(e -> zobrazZalozky());
        menuVyhledavani.add(zalozky);
        
        JMenuItem historie = new JMenuItem("Historie");
        historie.addActionListener(e -> zobrazHistorii());
        menuVyhledavani.add(historie);
        
        // Menu Bezpečnost
        JMenu menuBezpecnost = new JMenu("Bezpečnost");
        menuBezpecnost.setForeground(TmavyMotiv.vraTextu());
        
        JMenuItem spravceHesel = new JMenuItem("Správce hesel");
        spravceHesel.addActionListener(e -> zobrazSpravceHesel());
        menuBezpecnost.add(spravceHesel);
        
        JMenuItem ochranaPrivacy = new JMenuItem("Ochrana soukromí");
        ochranaPrivacy.addActionListener(e -> zobrazOchranaSoukromi());
        menuBezpecnost.add(ochranaPrivacy);
        
        // Menu Nápověda
        JMenu menuNapoveda = new JMenu("Nápověda");
        menuNapoveda.setForeground(TmavyMotiv.vraTextu());
        
        JMenuItem oAplikaci = new JMenuItem("O aplikaci");
        oAplikaci.addActionListener(e -> zobrazOAplikaci());
        menuNapoveda.add(oAplikaci);
        
        menuBar.add(menuSoubor);
        menuBar.add(menuUpravy);
        menuBar.add(menuVyhledavani);
        menuBar.add(menuBezpecnost);
        menuBar.add(menuNapoveda);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Přidá novou záložku
     */
    private void pridejNovouZalozku() {
        ZalozkaProzoru zalozka = new ZalozkaProzoru("about:blank");
        otevrenezalozky.add(zalozka);
        
        JPanel panelZalozky = new JPanel();
        panelZalozky.setBackground(TmavyMotiv.vraPozadi());
        
        zalozkPanel.addTab("Nová záložka", panelZalozky);
        zalozkPanel.setSelectedIndex(zalozkPanel.getTabCount() - 1);
        
        logger.debug("Nová záložka přidána");
    }
    
    /**
     * Naviguje na zadanou URL
     */
    private void navigujNaUrl(String url) {
        if (url.isEmpty()) {
            return;
        }
        
        // Přidání http:// pokud chybí
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("about:")) {
            url = "https://" + url;
        }
        
        // Aplikace ochrany soukromí
        url = ochranaS.vynucHttp(url);
        
        // Navigace
        if (webEngine != null) {
            Platform.runLater(() -> {
                webEngine.load(url);
            });
        }
        
        // Aktualizace adresního řádku
        adresniRadek.setText(url);
        
        // Přidání do historie
        spravceHistorie.pridejDoHistorie(url, "Navštívená stránka");
        
        stavovyRadek.setText("Načítání: " + url);
        logger.info("Navigace na: " + url);
    }
    
    /**
     * Naviguje zpět
     */
    private void navigujZpet() {
        if (webEngine != null) {
            Platform.runLater(() -> {
                webEngine.executeScript("window.history.back();");
            });
        }
        logger.debug("Navigace zpět");
    }
    
    /**
     * Naviguje vpřed
     */
    private void navigujVpred() {
        if (webEngine != null) {
            Platform.runLater(() -> {
                webEngine.executeScript("window.history.forward();");
            });
        }
        logger.debug("Navigace vpřed");
    }
    
    /**
     * Obnovuje stránku
     */
    private void obnoviStranku() {
        if (webEngine != null) {
            Platform.runLater(() -> {
                webEngine.reload();
            });
        }
        stavovyRadek.setText("Obnovování stránky...");
        logger.debug("Stránka obnovena");
    }
    
    /**
     * Naviguje na domovskou stránku
     */
    private void navigujDomů() {
        navigujNaUrl("https://www.google.com");
    }
    
    /**
     * Přidá aktuální stránku do záložek
     */
    private void pridejZalozku() {
        String url = adresniRadek.getText();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nejdříve se prosím připojte na nějakou stránku.", "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String nazev = JOptionPane.showInputDialog(this, "Zadejte název záložky:", url);
        if (nazev != null && !nazev.isEmpty()) {
            spravceZalozek.pridejZalozku(nazev, url);
            stavovyRadek.setText("Záložka přidána: " + nazev);
            logger.info("Záložka přidána: " + nazev + " -> " + url);
        }
    }
    
    /**
     * Zobrazuje menu
     */
    private void zobrazMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(TmavyMotiv.vraPrvku());
        
        JMenuItem zalozky = new JMenuItem("Záložky");
        zalozky.addActionListener(e -> zobrazZalozky());
        menu.add(zalozky);
        
        JMenuItem historie = new JMenuItem("Historie");
        historie.addActionListener(e -> zobrazHistorii());
        menu.add(historie);
        
        menu.show(tlacitkoMenu, 0, tlacitkoMenu.getHeight());
    }
    
    /**
     * Zobrazuje dialog se záložkami
     */
    private void zobrazZalozky() {
        ZalozkySouborDialog dialog = new ZalozkySouborDialog(this, spravceZalozek);
        dialog.setVisible(true);
    }
    
    /**
     * Zobrazuje dialog s historií
     */
    private void zobrazHistorii() {
        HistorieSouborDialog dialog = new HistorieSouborDialog(this, spravceHistorie);
        dialog.setVisible(true);
    }
    
    /**
     * Zobrazuje dialog se správcem hesel
     */
    private void zobrazSpravceHesel() {
        SpravceHeselDialog dialog = new SpravceHeselDialog(this, spravceHesel);
        dialog.setVisible(true);
    }
    
    /**
     * Zobrazuje dialog s ochranou soukromí
     */
    private void zobrazOchranaSoukromi() {
        OchranaSoukromiDialog dialog = new OchranaSoukromiDialog(this, ochranaS);
        dialog.setVisible(true);
    }
    
    /**
     * Zobrazuje dialog s nastavením
     */
    private void zobrazNastaveni() {
        NastaveniDialog dialog = new NastaveniDialog(this, konfigurace);
        dialog.setVisible(true);
    }
    
    /**
     * Zobrazuje dialog "O aplikaci"
     */
    private void zobrazOAplikaci() {
        JOptionPane.showMessageDialog(this,
            "ShieldBrowserX v1.0.0\n\n" +
            "Bezpečný a soukromý webový prohlížeč\n\n" +
            "Vytvořeno s Java 17 a JavaFX\n\n" +
            "© 2024 ShieldBrowser Team",
            "O aplikaci",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Třída reprezentující jednu záložku okna
     */
    private static class ZalozkaProzoru {
        private String url;
        private String titulek;
        
        public ZalozkaProzoru(String url) {
            this.url = url;
            this.titulek = "Nová záložka";
        }
        
        public String vraciUrl() {
            return url;
        }
        
        public void nastavujeUrl(String url) {
            this.url = url;
        }
        
        public String vraciTitulek() {
            return titulek;
        }
        
        public void nastavujeTitulek(String titulek) {
            this.titulek = titulek;
        }
    }
}
