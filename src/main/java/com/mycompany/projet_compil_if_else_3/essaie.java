/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_3;

/**
 *
 * @author InfoPro
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author InfoPro
 */

import IR_v3.ASTToHIR;
import IR_v3.HIR;
import IR_v3.HIRToLIR;
import IR_v3.LIR;
import IR_v3.LIRPrinter;
import  com.mycompany.projet_compil_if_else_3.semantique;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
//import static javax.swing.Spring.scale;

public class essaie extends JFrame {

    boolean eror = false;
    
private List<String> erreursLexicales = new ArrayList<>();
private List<String> erreursSyntaxiques = new ArrayList<>();
private List<String> erreursSemantiques = new ArrayList<>();
private parcer dernierParser;
private semantique Semantique;
private HIR.HIRNode hir;

 // --- 4️⃣ Analyse sémantique ---
        Stack<Map<String, semantique.InfoVariable>> pileScopes = new Stack<>();
        
        
        Stack<semantique.Contexte> pileContextes = new Stack<>(); // contexte
        
        Map<String, semantique.FonctionInfo> tableFonctions = new HashMap<>();
        
        // Pour chaque classe déclarée : nomClasse -> variables membres
        Map<String, Map<String, semantique.InfoVariable>> classesVariables = new HashMap<>();

        // Pour chaque classe déclarée : nomClasse -> méthodes
        Map<String, Map<String, semantique.FonctionInfo>> classesMethodes = new HashMap<>();


private final JTextArea codeArea;
private final JTextArea tokensArea;
private ArbrePanel arbrePanel; // Panneau pour afficher l’arbre
private final JButton boutonEreur = new JButton("Regarder les erreurs");
private FenetreIR fenetreIR = new FenetreIR(null);

public essaie() {
    setTitle("Analyseur lexical et syntaxique");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1000, 700);
    setLocationRelativeTo(null);
    
    

    // --- ZONE DE CODE SOURCE ---
    codeArea = new JTextArea();
    codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
    codeArea.setBorder(BorderFactory.createTitledBorder("Code source"));
    codeArea.setText("if (x > 0) { y = 1 + 2 * 3; } else { y = -1; }");

    // --- ZONE TOKENS ---
    tokensArea = new JTextArea();
    tokensArea.setFont(new Font("Consolas", Font.PLAIN, 13));
    tokensArea.setEditable(false);
    tokensArea.setBorder(BorderFactory.createTitledBorder("Analyse lexicale"));
    JScrollPane tokenScroll = new JScrollPane(tokensArea);

    // --- ZONE ARBRE ---
    arbrePanel = new ArbrePanel(null);
    arbrePanel.setBackground(Color.WHITE);
    JScrollPane arbreScroll = new JScrollPane(arbrePanel);
    arbreScroll.setBorder(BorderFactory.createTitledBorder("Arbre syntaxique"));

    // --- SPLIT HORIZONTAL (tokens | arbre)
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tokenScroll, arbreScroll);
    splitPane.setDividerLocation(300);
    splitPane.setResizeWeight(0.3);

    // --- ZONE DES BOUTONS (en bas)
    JPanel raslare = new JPanel(new FlowLayout());
    JButton analyserBtn = new JButton("Analyser");
    analyserBtn.addActionListener(e -> analyserCode());
    boutonEreur.setVisible(eror);
    boutonEreur.addActionListener(e -> afficherErreurs());
    raslare.add(analyserBtn);
    raslare.add(boutonEreur);
    int largeur = Math.max(analyserBtn.getPreferredSize().width, boutonEreur.getPreferredSize().width);
    Dimension d = new Dimension(largeur + 20, 35); // +20 pour une marge visuelle
    analyserBtn.setPreferredSize(d);
    boutonEreur.setPreferredSize(d);
    //boutonEreur.setVisible(false);



    // --- SPLIT VERTICAL (splitPane au-dessus, boutons en dessous)
    JSplitPane splitPaneS = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane, raslare);
    splitPaneS.setDividerLocation(560);
    splitPaneS.setResizeWeight(0.9);
    
    JButton zoomPlus = new JButton("+");
    zoomPlus.addActionListener(e -> arbrePanel.zoomIn());

    JButton zoomMoins = new JButton("–");
    zoomMoins.addActionListener(e -> arbrePanel.zoomOut());

    JButton resetZoom = new JButton("⟳");
    resetZoom.addActionListener(e -> arbrePanel.resetZoom());
    
    JButton ouvrirBtn = new JButton("📂 Ouvrir");
    JButton sauvegarderBtn = new JButton("💾 Sauvegarder");
    
    JButton boutonIR = new JButton("HIR / LIR");
    boutonIR.addActionListener(e -> afficherIR());

    raslare.add(boutonIR);
    
    

    analyserBtn.setPreferredSize(d);
    boutonEreur.setPreferredSize(d);
    boutonIR.setPreferredSize(d);

    // Action : ouvrir un fichier et charger le texte dans le codeArea
    ouvrirBtn.addActionListener(e -> {
        JFileChooser chooser = new JFileChooser();
        int resultat = chooser.showOpenDialog(this);
        if (resultat == JFileChooser.APPROVE_OPTION) {
            File fichier = chooser.getSelectedFile();
            try (BufferedReader lecteur = new BufferedReader(new FileReader(fichier))) {
                StringBuilder contenu = new StringBuilder();
                String ligne;
                while ((ligne = lecteur.readLine()) != null) {
                    contenu.append(ligne).append("\n");
                }
                codeArea.setText(contenu.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la lecture du fichier : " + ex.getMessage());
            }
        }
    });

    // Action : sauvegarder le contenu du codeArea dans un fichier
    sauvegarderBtn.addActionListener(e -> {
        JFileChooser chooser = new JFileChooser();
        int resultat = chooser.showSaveDialog(this);
        if (resultat == JFileChooser.APPROVE_OPTION) {
            File fichier = chooser.getSelectedFile();
            try (BufferedWriter ecrivain = new BufferedWriter(new FileWriter(fichier))) {
                ecrivain.write(codeArea.getText());
                JOptionPane.showMessageDialog(this, "Fichier sauvegardé avec succès !");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + ex.getMessage());
            }
        }
    });

    JButton resetBtn = new JButton("Réinitialiser");
    resetBtn.addActionListener(e -> reinitialiser());
    raslare.add(resetBtn);




    JPanel boutons = new JPanel();
    boutons.add(zoomMoins);
    boutons.add(zoomPlus);
    boutons.add(resetZoom);
    boutons.add(ouvrirBtn);
    boutons.add(sauvegarderBtn);

    raslare.add(boutons);

    // --- MISE EN PAGE FINALE ---
    add(new JScrollPane(codeArea), BorderLayout.NORTH);
    add(splitPaneS, BorderLayout.CENTER);

    setVisible(true);
}

// Méthode d'exemple pour le bouton erreurs
private void afficherErreurs() {
    
    FenetreErreurs fen = new FenetreErreurs(this, erreursLexicales, erreursSyntaxiques, erreursSemantiques); // `erreurs` est ta liste
    fen.setVisible(true);
    if(erreursLexicales.isEmpty() && erreursSyntaxiques.isEmpty() && erreursSemantiques.isEmpty()){
        JOptionPane.showMessageDialog(this, "Aucune erreur trouvée !");
    }
}

private void afficherIR() {
    fenetreIR.setVisible(true);
}

private void reinitialiser() {
    // Réinitialiser les zones de texte
    codeArea.setText("");
    tokensArea.setText("");

    // Réinitialiser l’arbre proprement
    if (arbrePanel != null) {
        arbrePanel.setRacine(null);  // ou arbrePanel.effacer(); si tu l’as nommée ainsi
        arbrePanel.revalidate();
        arbrePanel.repaint();
    }

    // Réinitialiser la logique
    erreursLexicales.clear();
    erreursSyntaxiques.clear();
    erreursSemantiques.clear();
    boutonEreur.setVisible(false);
    dernierParser = null;
    eror = false;

    JOptionPane.showMessageDialog(this, "L'interface a été réinitialisée.");
}




    private void analyserCode() {
        try {
            String code = codeArea.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer du code.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- ANALYSE LEXICALE ---
            lexer lex = new lexer(code);
            List<lexer.token> tokens = lex.tokeniser();
            
            erreursLexicales = lex.erreurs;

            StringBuilder sbTokens = new StringBuilder();
            for (lexer.token t : tokens) sbTokens.append(t).append("\n");
            tokensArea.setText(sbTokens.toString());

            // --- ANALYSE SYNTAXIQUE ---
            dernierParser = new parcer(tokens);
            parcer.programmeNoeud programm = dernierParser.parcerProgramme();
            
            // Récupère les erreurs du parser
            erreursSyntaxiques = dernierParser.getErreurs();
            
            pileScopes.push(new HashMap<>()); // scope global
            
            // --- ANALYSE SÉMANTIQUE ---
            semantique sem = new semantique();
            parcer.programmeNoeud programme = sem.analyserProgramme(programm, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);   // 🔥 C'EST ÇA QUI MANQUAIT
            //semantique.analyserProgramme(programme, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes); // <-- ta méthode sémantique

            erreursSemantiques = sem.getErreurs();
            
            ASTToHIR astToHIR = new ASTToHIR();

            HIR.HIRProgram hir = astToHIR.convert(programme);
            this.hir = hir;
            
            System.out.println("le hir ============================================="+hir);
            
            HIRToLIR hirToLIR = new HIRToLIR();

            LIR.LIRBlock lir = hirToLIR.lower(hir);
            String lirTexte = LIRPrinter.print(lir);
            
            System.out.println("le lir ============================================="+lir+"\nlirtext"+lirTexte);
            
            System.out.println("les intructions de lir"+lir.instructions.size());

            // --- AFFICHAGE DU BOUTON ERREURS ---
            eror = !(erreursLexicales.isEmpty() && erreursSyntaxiques.isEmpty() && erreursSemantiques.isEmpty());
            
            boutonEreur.setVisible(eror);

            // --- AFFICHAGE ARBRE ---
            arbrePanel.setRacine(programme);
            arbrePanel.revalidate();
            arbrePanel.repaint();
            fenetreIR.setHIR(hir);
            fenetreIR.setLIR(lirTexte);

        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Analyse échouée", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- PANNEAU DESSIN D'ARBRE ---
    // place this inside your Essaie class (replace existing ArbrePanel)
static class ArbrePanel extends JPanel {
    private parcer.noeud racine;

    // layout parameters (tweak to taste)
    private final int nodeWidth = 120;
    private final int nodeHeight = 34;
    private final int vGap = 80;   // vertical gap between levels
    private final int hGap = 30;   // minimal horizontal gap between sibling subtrees
    
    private double zoom = 1.0; // ← facteur de zoom (1.0 = normal)

    // caches
    private final java.util.Map<parcer.noeud, Integer> subtreeWidth = new java.util.HashMap<>();
    private final java.util.Map<parcer.noeud, Point> nodePositions = new java.util.HashMap<>();

    public ArbrePanel(parcer.noeud racine) {
        this.racine = racine;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1200, 800));
    }
    
    public void setRacine(parcer.noeud racine) {
        
        this.racine = racine;
        subtreeWidth.clear();
        nodePositions.clear();
        revalidate();
        repaint();
    }

    
    // --- Contrôles de zoom ---
    public void zoomIn() {
        zoom = Math.min(zoom * 1.2, 5.0); // limite zoom max
        revalidate();
        repaint();
    }

    public void zoomOut() {
        zoom = Math.max(zoom / 1.2, 0.2); // limite zoom min
        revalidate();
        repaint();
    }
    
    public void resetZoom() {
        zoom = 1.0;
        revalidate();
        repaint();
    }
    
    public void effacerArbre() {
    removeAll();
    revalidate();
    repaint();
}



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        subtreeWidth.clear();
        nodePositions.clear();
        if (racine == null) return;

        // 1) compute subtree widths (in pixels)
        int totalWidth = computeSubtreeWidth(racine);

        // 2) compute positions starting centered in panel (or at required width)
        int startX = Math.max(getWidth() / 2 - totalWidth / 2, 20);
        layoutNode(racine, startX, 40);

        // 3) optionally set preferred size so scrollpane can scroll to show everything
        int prefW = Math.max(getWidth(), totalWidth + 40);
        int depth = computeDepth(racine);
        int prefH = Math.max(getHeight(), depth * (nodeHeight + vGap) + 100);
        setPreferredSize(new Dimension(prefW, prefH));
        revalidate();

        // 4) draw
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Appliquer le zoom autour du centre du panneau
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2.translate(cx, cy);
        g2.scale(zoom, zoom);
        
        g2.translate(-cx, -cy);
        drawEdges(g2, racine);
        drawNodes(g2, racine);
    }

    // compute width required by subtree rooted at n (in pixels)
    private int computeSubtreeWidth(parcer.noeud n) {
        if (subtreeWidth.containsKey(n)) return subtreeWidth.get(n);

        java.util.List<parcer.noeud> children = getChildren(n);
        int w;
        if (children.isEmpty()) {
            w = nodeWidth;
        } else {
            int sum = 0;
            for (parcer.noeud ch : children) {
                int cw = computeSubtreeWidth(ch);
                sum += cw;
            }
            sum += hGap * Math.max(0, children.size() - 1);
            w = Math.max(nodeWidth, sum);
        }
        subtreeWidth.put(n, w);
        return w;
    }

    // layout node: assign x,y for node and recursively its children.
    // x = left-most x of the area allocated for this subtree
    private void layoutNode(parcer.noeud n, int leftX, int y) {
        int w = subtreeWidth.getOrDefault(n, nodeWidth);
        java.util.List<parcer.noeud> children = getChildren(n);

        if (children.isEmpty()) {
            // center the leaf inside its allocated width
            int centerX = leftX + w / 2;
            nodePositions.put(n, new Point(centerX, y));
        } else {
            // layout children sequentially inside [leftX, leftX + w]
            int curX = leftX;
            for (int i = 0; i < children.size(); i++) {
                parcer.noeud ch = children.get(i);
                int cw = subtreeWidth.get(ch);
                layoutNode(ch, curX, y + nodeHeight + vGap);
                curX += cw + hGap;
            }
            // after children laid out, parent X is center between first and last child centers
            Point first = nodePositions.get(children.get(0));
            Point last = nodePositions.get(children.get(children.size() - 1));
            int centerX = (first.x + last.x) / 2;
            nodePositions.put(n, new Point(centerX, y));
        }
    }

    // draw edges from parent to children
    /*private void drawEdges(Graphics2D g, parcer.noeud n) {
        Point p = nodePositions.get(n);
        if (p == null) return;
        java.util.List<parcer.noeud> children = getChildren(n);
        g.setColor(Color.GRAY);
        for (parcer.noeud ch : children) {
            Point c = nodePositions.get(ch);
            if (c != null) {
                g.drawLine(p.x, p.y + nodeHeight / 2, c.x, c.y - nodeHeight / 2);
                drawEdges(g, ch);
            }
        }
    }*/
    
    private void drawEdges(Graphics2D g, parcer.noeud n) {
        Point p = nodePositions.get(n);
        if (p == null) return;

        java.util.List<parcer.noeud> children = getChildren(n);
        for (parcer.noeud ch : children) {
            if (ch == null) {
                System.out.println("Enfant NULL trouvé !");
                continue;
            }
            Point c = nodePositions.get(ch);
            if (c != null) {
                // --- couleur selon l'état du nœud fils ---
                if (ch.erreur) {
                    g.setColor(Color.RED); // rouge pour erreur
                } else if (ch.messageErreur != null && !ch.messageErreur.isEmpty()) {
                    g.setColor(Color.ORANGE); // orange pour warning ou info
                } else {
                    g.setColor(Color.GRAY); // normal
                }

                // --- dessiner la ligne ---
                g.drawLine(p.x, p.y + nodeHeight / 2, c.x, c.y - nodeHeight / 2);

                // --- dessiner le message parallèle à la ligne ---
                if (ch.erreur || (ch.messageErreur != null && !ch.messageErreur.isEmpty())) {
                    String msg = ch.messageErreur != null ? ch.messageErreur : "⚠️ Erreur";
                    int midX = (p.x + c.x) / 2;
                    int midY = (p.y + nodeHeight / 2 + c.y - nodeHeight / 2) / 2;

                    g.setColor(Color.BLACK); // couleur texte
                    g.drawString(msg, midX + 5, midY); // légèrement décalé à droite
                }

                // --- récursion ---
                drawEdges(g, ch);
            }
        }
    }


    
    // --- DESSIN DES NOEUDS (rectangles + label) ---
    private void drawNodes(Graphics2D g, parcer.noeud n) {
        if (n == null) return;

        Point p = nodePositions.get(n);
        if (p == null) return;

        int x = p.x;
        int y = p.y;
        int baseW = nodeWidth;
        int h = nodeHeight;

        // --- COULEUR SELON TYPE DE NOEUD ---
        Color fillColor;
        if (n instanceof parcer.noeud_si) {
            fillColor = new Color(255, 230, 180);
        } else if (n instanceof parcer.block_de_noeuds) {
            fillColor = new Color(200, 255, 200);
        } else if (n instanceof parcer.noeud_sinonSi) {
            fillColor = new Color(255, 230, 180);
        } else if (n instanceof parcer.noeud_d_operation_binaire) {
            fillColor = new Color(180, 220, 255);
        } else if (n instanceof parcer.noeud_d_asignation) {
            fillColor = new Color(255, 200, 200);
        } else if (n instanceof parcer.noeud_de_valeur) {
            fillColor = new Color(255, 255, 180);
        } else if (n instanceof parcer.ClasseNode) {
            fillColor = new Color(165, 135, 255);
        } else if (n instanceof parcer.MethodeNode) {
            fillColor = new Color(255, 185, 223);
        } else if (n instanceof parcer.noeud_de_condition) {
            fillColor = new Color(222, 171, 109);
        } else if (n instanceof parcer.noeud_zakaria) {
            fillColor = new Color(255, 230, 130);
        } else if (n instanceof parcer.noeud_tafoukt) {
            fillColor = new Color(255, 122, 30);
        } else if (n instanceof parcer.noeud_return) {
            fillColor = new Color(0, 155, 108);
        } else if (n instanceof parcer.VariableNode) {
            fillColor = new Color(30, 155, 158);
        } else if (n instanceof parcer.noeud_parametre) {
            fillColor = new Color(255, 0, 0);
        }//--- Couleurs pour les nouveaux nœuds uniquement
        else if (n instanceof parcer.noeud_constructeur) {
            fillColor = new Color(255, 150, 220);
        } else if (n instanceof parcer.noeud_declaration_multiple) {
            fillColor = new Color(180, 255, 150);
        } else if (n instanceof parcer.noeud_tableau_init) {
            fillColor = new Color(150, 180, 255);
        } else if (n instanceof parcer.noeud_break) {
            fillColor = new Color(255, 100, 180);
        } else if (n instanceof parcer.noeud_continue) {
            fillColor = new Color(255, 180, 100);
        } else if (n instanceof parcer.noeud_case) {
            fillColor = new Color(180, 255, 220);
        } else if (n instanceof parcer.noeud_default) {
            fillColor = new Color(220, 180, 255);
        } else if (n instanceof parcer.noeud_switch) {
            fillColor = new Color(255, 220, 150);
        } else if (n instanceof parcer.noeud_ternaire) {
            fillColor = new Color(150, 255, 255);
        } else if (n instanceof parcer.NoeudPackage) {
            fillColor = new Color(255, 150, 150);
        } else if (n instanceof parcer.NoeudImport) {
            fillColor = new Color(150, 255, 180);
        } else if (n instanceof parcer.noeud_enum) {
            fillColor = new Color(180, 150, 255);
        } else if (n instanceof parcer.noeud_try) {
            fillColor = new Color(255, 180, 255);
        } else if (n instanceof parcer.noeud_catch) {
            fillColor = new Color(180, 255, 255);
        } else if (n instanceof parcer.noeud_tableau) {
            fillColor = new Color(255, 200, 150);
        } else if (n instanceof parcer.NoeudAccesTableau) {
            fillColor = new Color(200, 150, 255);
        } else if (n instanceof parcer.NoeudCreationTableau) {
            fillColor = new Color(150, 255, 200);
        } else if (n instanceof parcer.NoeudConditionBinaire) {
            fillColor = new Color(255, 255, 150);
        } else if (n instanceof parcer.NoeudConditionUnaire) {
            fillColor = new Color(255, 200, 255);
        } else if (n instanceof parcer.NoeudValeurCondition) {
            fillColor = new Color(200, 255, 150);
        } else if (n instanceof parcer.NoeudAccesChamp) {
            fillColor = new Color(150, 200, 255);
        } else if (n instanceof parcer.NoeudAppelMethodeObjet) {
            fillColor = new Color(255, 150, 200);
        } else {
            fillColor = new Color(230, 230, 250);
        }

        // --- LABEL LISIBLE ---
        String label = nodeLabel(n);
        if (label == null || label.isEmpty()) label = "(null)";

        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(label);

        // --- largeur dynamique avec limite max ---
        int maxWidth = 200;  // largeur maximale d’un nœud
        int minWidth = 90;   // largeur minimale
        int w = Math.min(Math.max(textW + 20, minWidth), maxWidth);

        // --- Clip le texte s’il dépasse la limite ---
        String drawLabel = label;
        if (textW > maxWidth - 10) {
            while (drawLabel.length() > 0 && fm.stringWidth(drawLabel + "...") > maxWidth - 10) {
                drawLabel = drawLabel.substring(0, drawLabel.length() - 1);
            }
            drawLabel = drawLabel + "...";
        }

        // --- DESSIN DU NOEUD ---
        g.setColor(fillColor);
        g.fillRoundRect(x - w / 2, y - 15, w, 30, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x - w / 2, y - 15, w, 30, 10, 10);

        // --- TEXTE ---
        g.drawString(drawLabel, x - fm.stringWidth(drawLabel) / 2, y + fm.getAscent() / 2 - 2);

        // --- RECURSION SUR LES ENFANTS ---
        for (parcer.noeud ch : getChildren(n)) {
            if (ch != null) drawNodes(g, ch);
        }
    }

    
    


    // --- LABEL D’UN NOEUD ---
    private String nodeLabels(parcer.noeud n) {
        if (n == null) return "❌ Erreur"; // 🔥 Protection anti-null

        try {
            if (n instanceof parcer.programmeNoeud) return "Programme";
            if (n instanceof parcer.noeud_si) return  "Si";
            if (n instanceof parcer.noeud_sinonSi) return  "Sinon si";
            if (n instanceof parcer.block_de_noeuds) return "Bloc" + " : " + ((parcer.block_de_noeuds) n).type;
            if (n instanceof parcer.noeud_d_asignation a) return "Assign: " + a.identificateur;
            if (n instanceof parcer.noeud_de_condition c)
                return c.operateur.name();/*(c.gauche != null ? c.gauche.toString() : "?") +
                       " " + (c.operateur != null ? c.operateur : "?") +
                       " " + (c.droite != null ? c.droite.toString() : "?");*/
            if (n instanceof parcer.noeud_de_valeur v)
                return "Val: " + (v.valeur != null ? v.valeur.toString() : "null");
            if (n instanceof parcer.noeud_d_operation_binaire op)
                return (op.operateur != null ? op.operateur.toString() : "op");
            if (n instanceof parcer.VariableNode)return n.getClass().getSimpleName() + " " + ((parcer.VariableNode) n).nom + " = " /*+ ((parcer.VariableNode) n).valeur*/;
            if (n instanceof parcer.ClasseNode)return ((parcer.ClasseNode) n).modificateur +" "+ n.getClass().getSimpleName() +" "+ ((parcer.ClasseNode) n).nom;
            if (n instanceof parcer.MethodeNode)return (((parcer.MethodeNode) n).modificateur != null ? ((parcer.MethodeNode) n).modificateur : "")+((parcer.MethodeNode) n).typeRetour +" "+ n.getClass().getSimpleName() +" "+ ((parcer.MethodeNode) n).nom;
            if (n instanceof parcer.noeud_parametre)return n.toString();
            if (n instanceof parcer.NoeudAccesChamp ac){return n.getClass().getSimpleName() + "  " + ((parcer.NoeudAccesChamp) n).champ;}
            if (n instanceof parcer.NoeudAppelMethodeObjet ac){return n.getClass().getSimpleName() + "   " + ((parcer.NoeudAppelMethodeObjet) n).methode;}
            if (n instanceof parcer.noeud_tafoukt)return ((parcer.noeud_tafoukt) n).toString() +" "+ n.getClass().getSimpleName() ;
            if (n instanceof parcer.noeud_zakaria)return ((parcer.noeud_zakaria) n).toString() +" "+ n.getClass().getSimpleName() ;
            
            return n.getClass().getSimpleName();
        } catch (Exception e) {
            return "⚠️ Erreur";
        }
    }
    
    private String nodeLabel(parcer.noeud n) {
        String baseLabel = nodeLabels(n); // ton label actuel
        /*if (((parcer.noeud)n).typeInfered != null) {
            baseLabel += " : " + n.typeInfered.base + "[" + n.typeInfered.dimension + "]";
        }
        if (((parcer.noeud)n).erreur) {
            baseLabel += " ⚠️ " + ((parcer.noeud)n).messageErreur;
        }else{
            
        }
        
        System.out.println(
            n.getClass().getSimpleName() +
            " type=" + n.typeInfered +
            " erreur=" + n.erreur
        );*/

        return baseLabel;
    }



    // helper: unify all child extraction into a list (handles arrays and lists)
    private java.util.List<parcer.noeud> getChildren(parcer.noeud n) {
        java.util.List<parcer.noeud> list = new java.util.ArrayList<>();
        if (n instanceof parcer.programmeNoeud p) {
            if (p.pkg != null) list.add(p.pkg);
            if (p.imports != null) list.addAll(p.imports);
            if (p.mots != null) list.addAll(p.mots);
        } else if (n instanceof parcer.noeud_si si) {
            if (si.condition != null) list.add(si.condition);
            if (si.blockAlors != null) list.add(si.blockAlors);
            if (si.block_sinon != null) list.add(si.block_sinon);
            if (si.block_sinonSi != null) list.add(si.block_sinonSi);
        } else if (n instanceof parcer.noeud_sinonSi si) {
            if (si.condition != null) list.add(si.condition);
            if (si.blockAlors != null) list.add(si.blockAlors);
            if (si.block_sinon != null) list.add(si.block_sinon);
            if (si.block_sinonSi != null) list.add(si.block_sinonSi);
        } else if (n instanceof parcer.block_de_noeuds b) {
            if (b.mots != null) list.addAll(b.mots);
        } else if (n instanceof parcer.noeud_d_asignation a) {
            if (a.cible != null) list.add(a.cible);
            if (a.valeur != null) list.add(a.valeur);
        } else if (n instanceof parcer.noeud_d_operation_binaire op) {
            if (op.gauche != null) list.add(op.gauche);
            if (op.droite != null) list.add(op.droite);
        } else if (n instanceof parcer.ClasseNode c) {
            if (c.membres != null) list.addAll(c.membres);
        } else if (n instanceof parcer.MethodeNode m) {
            if (m.instructions != null) list.addAll(m.instructions);
            if (m.Pretour != null) list.addAll(m.Pretour);
        } else if (n instanceof parcer.VariableNode v) {
            if (v.valeur != null) list.add(v.valeur);
        } else if (n instanceof parcer.noeud_tafoukt v) {
            if (v.valeur != null) list.add(v.valeur);
        } else if (n instanceof parcer.noeud_zakaria v) {
            if (v.valeur != null) list.add(v.valeur);
        } else if (n instanceof parcer.noeud_return v) {
            if (v.valeur != null) list.add(v.valeur);
        } else if (n instanceof parcer.noeud_while v) {
            if (v.condition != null) list.add(v.condition);
            if (v.block != null) list.add(v.block);
        } else if (n instanceof parcer.noeud_for v) {
            if (v.compteur != null) list.add(v.compteur);
            if (v.block != null) list.add(v.block);
        } else if (n instanceof parcer.noeud_compteur v) {
            if (v.valeurDepart != null) list.add(v.valeurDepart);
            if (v.limite != null) list.add(v.limite);
            if (v.pas != null) list.add(v.pas);
        } else if (n instanceof parcer.noeud_do v) {
            if (v.block != null) list.add(v.block);
            if (v.condition != null) list.add(v.condition);
        } else if (n instanceof parcer.noeud_constante v) {
            if (v.valeur != null) list.add(v.valeur);
        } else if (n instanceof parcer.noeud_appel_methode v) {
            if (v.arguments != null) list.addAll(v.arguments);
        } else if (n instanceof parcer.noeud_constructeur v) {
            if (v.arguments != null) list.addAll(v.arguments);
        } else if (n instanceof parcer.noeud_declaration_multiple v) {
            if (v.variables != null) list.addAll(v.variables);
        } else if (n instanceof parcer.noeud_tableau_init v) {
            if (v.valeurs != null) list.addAll(v.valeurs);
        } else if (n instanceof parcer.noeud_switch v) {
            if (v.expression != null) list.add(v.expression);
            if (v.cases != null) list.addAll(v.cases);
        } else if (n instanceof parcer.noeud_case v) {
            if (v.instructions != null) list.addAll(v.instructions);
        } else if (n instanceof parcer.noeud_default v) {
            if (v.instructions != null) list.addAll(v.instructions);
        } else if (n instanceof parcer.noeud_ternaire v) {
            if (v.condition != null) list.add(v.condition);
            if (v.vraiExpr != null) list.add(v.vraiExpr);
            if (v.fauxExpr != null) list.add(v.fauxExpr);
        } else if (n instanceof parcer.noeud_enum v) {
            if (v.valeurs != null) list.addAll(v.valeurs);
        } else if (n instanceof parcer.noeud_try v) {
            if (v.condTry != null) list.add(v.condTry);
            if (v.blockTry != null) list.add(v.blockTry);
            if (v.catches != null) list.addAll(v.catches);
        } else if (n instanceof parcer.noeud_catch v) {
            if (v.condCatch != null) list.add(v.condCatch);
            if (v.blockCatch != null) list.add(v.blockCatch);
        } else if (n instanceof parcer.noeud_tableau v) {
            if (v.valeurs != null) list.addAll(v.valeurs);
        } else if (n instanceof parcer.NoeudAccesTableau v) {
            if (v.indices != null) list.addAll(v.indices);
        } else if (n instanceof parcer.NoeudCreationTableau v) {
            if (v.tailles != null) list.addAll(v.tailles);
        } else if (n instanceof parcer.noeud_de_condition v) {
            if (v.gaucheExpr != null) list.add(v.gaucheExpr);
            if (v.droiteExpr != null) list.add(v.droiteExpr);
            if (v.suite != null) list.add(v.suite);
        } else if (n instanceof parcer.NoeudAccesChamp ac){
            if(ac.objet !=null) list.add(ac.objet);
        } else if (n instanceof parcer.NoeudAppelMethodeObjet ac){
            if(ac.objet !=null) list.add(ac.objet);
        } else if (n instanceof parcer.noeud_declaration_constructeur ac){
            if(ac.parametres !=null) list.addAll(ac.parametres);
            if(ac.block !=null) list.addAll(ac.block);
        }
        
        // valeur and condition leafs will return empty list
        return list;
    }

    // compute tree depth (levels) to estimate preferred height
    private int computeDepth(parcer.noeud n) {
        if (n == null) return 0;
        int max = 0;
        for (parcer.noeud ch : getChildren(n)) {
            max = Math.max(max, computeDepth(ch));
        }
        return 1 + max;
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(essaie::new);
    }
}



