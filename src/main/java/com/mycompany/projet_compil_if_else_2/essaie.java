/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_2;

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
/*import com.mycompany.projet_compil_if_else_2.lexer.token;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class essaie extends JFrame {

    private JTextArea codeArea;
    private JTextArea tokensArea;
    private JTextArea astArea;
    /*private JScrollPane tokenAr = new JScrollPane(tokensArea);
    private JScrollPane astAr = new JScrollPane(astArea);*
    

    public essaie() {
        setTitle("Analyseur lexical et syntaxique");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // --- ZONE DE SAISIE CODE ---
        codeArea = new JTextArea();
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setBorder(BorderFactory.createTitledBorder("Code source"));
        codeArea.setText("if (x > 0) { y = 1 + 2 * 3; } else { y = -1; }");

        // --- ZONES DE SORTIE ---
        tokensArea = new JTextArea();
        tokensArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        tokensArea.setEditable(false);
        tokensArea.setBorder(BorderFactory.createTitledBorder("Analyse lexicale"));
        tokensArea.setSize(300,500);

        astArea = new JTextArea();
        astArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        astArea.setEditable(false);
        astArea.setBorder(BorderFactory.createTitledBorder("Arbre syntaxique"));
        
        JScrollPane tokenScroll = new JScrollPane(tokensArea);
        JScrollPane astScroll = new JScrollPane(astArea);


        // --- PANELS ---
        JButton analyserBtn = new JButton("Analyser");
        analyserBtn.addActionListener(e -> analyserCode());

        /*JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(tokensArea), BorderLayout.WEST);
        centerPanel.add(new JScrollPane(astArea), BorderLayout.CENTER);

        tokensArea.setPreferredSize(new Dimension(250, 0)); // largeur 250 px*
        
        JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        tokenScroll,
        astScroll
);
splitPane.setDividerLocation(300); // largeur initiale de tokensArea
splitPane.setResizeWeight(0.3);    // 30% / 70%
add(splitPane, BorderLayout.CENTER);

        

        add(new JScrollPane(codeArea), BorderLayout.NORTH);
        //add(centerPanel, BorderLayout.CENTER);
        add(analyserBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void analyserCode() {
        try {
            String code = codeArea.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer du code.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Analyse lexicale
            lexer lex = new lexer(code);
            List<lexer.token> tokens = lex.tokeniser();

            StringBuilder sbTokens = new StringBuilder();
            for (lexer.token t : tokens) {
                sbTokens.append(t).append("\n");
            }
            tokensArea.setText(sbTokens.toString());

            // Analyse syntaxique
            parcer parser = new parcer(tokens);
            parcer.programmeNoeud programme = parser.parcerProgramme();

            StringBuilder sbAst = new StringBuilder();
            afficherAST(programme, 0, sbAst);
            astArea.setText(sbAst.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Analyse échouée", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fonction d’affichage de l’AST dans une String
    private static void afficherAST(parcer.noeud n, int indent, StringBuilder out) {
        String prefix = " ".repeat(indent);

        if (n instanceof parcer.programmeNoeud p) {
            out.append(prefix).append("Programme:\n");
            for (parcer.noeud child : p.mots) {
                afficherAST(child, indent + 2, out);
            }
        } else if (n instanceof parcer.noeud_si si) {
            out.append(prefix).append("Si:\n");
            out.append(prefix).append("  Condition:\n");
            afficherAST(si.condition, indent + 4, out);
            out.append(prefix).append("  Alors:\n");
            afficherAST(si.blockAlors, indent + 4, out);
            if (si.block_sinon != null) {
                out.append(prefix).append("  Sinon:\n");
                afficherAST(si.block_sinon, indent + 4, out);
            }
        } else if (n instanceof parcer.block_de_noeuds b) {
            out.append(prefix).append("Bloc:\n");
            for (parcer.noeud child : b.mots) {
                afficherAST(child, indent + 2, out);
            }
        } else if (n instanceof parcer.noeud_d_asignation a) {
            out.append(prefix).append("Assignation: ").append(a.identificateur).append(" =\n");
            afficherAST(a.valeur, indent + 4, out);
        } else if (n instanceof parcer.noeud_de_condition c) {
            out.append(prefix).append("Condition: ").append(c.gauche).append(" ").append(c.operateur).append(" ").append(c.droite).append("\n");
        } else if (n instanceof parcer.noeud_de_valeur v) {
            out.append(prefix).append("Valeur: ").append(v.valeur).append("\n");
        } else if (n instanceof parcer.noeud_d_operation_binaire op) {
            out.append(prefix).append("Operation: ").append(op.operateur).append("\n");
            afficherAST(op.gauche, indent + 4, out);
            afficherAST(op.droite, indent + 4, out);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(essaie::new);
    }
}
package com.mycompany.projet_compil_if_else_2;
*/
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.Spring.scale;

public class essaie extends JFrame {

    boolean eror = false;
    
private List<String> erreurs = new ArrayList<>();
private parcer dernierParser;


private final JTextArea codeArea;
private final JTextArea tokensArea;
private ArbrePanel arbrePanel; // Panneau pour afficher l’arbre
private final JButton boutonEreur = new JButton("Regarder les erreurs");

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
    
    FenetreErreurs fen = new FenetreErreurs(this, erreurs); // `erreurs` est ta liste
    fen.setVisible(true);
    if(erreurs.isEmpty()){
        JOptionPane.showMessageDialog(this, "Aucune erreur trouvée !");
    }
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
    erreurs.clear();
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

            StringBuilder sbTokens = new StringBuilder();
            for (lexer.token t : tokens) sbTokens.append(t).append("\n");
            tokensArea.setText(sbTokens.toString());

            // --- ANALYSE SYNTAXIQUE ---
            dernierParser = new parcer(tokens);
            parcer.programmeNoeud programme = dernierParser.parcerProgramme();
            
            // Récupère les erreurs du parser
            erreurs = dernierParser.getErreurs();

            // --- AFFICHAGE DU BOUTON ERREURS ---
            eror = !erreurs.isEmpty();
            
            boutonEreur.setVisible(eror);

            // --- AFFICHAGE ARBRE ---
            arbrePanel.setRacine(programme);
            arbrePanel.revalidate();
            arbrePanel.repaint();

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
    private void drawEdges(Graphics2D g, parcer.noeud n) {
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
    }

    // draw nodes (rectangles + label)
    /*private void drawNodes(Graphics2D g, parcer.noeud n) {
        Point p = nodePositions.get(n);
        if (p == null) return;

        int x = p.x;
        int y = p.y;
        int w = nodeWidth;
        int h = nodeHeight;

        // background
        Color fillColor;

        if (n instanceof parcer.noeud_si) {
            fillColor = new Color(255, 230, 180); // Orange clair pour les "if"
        } else if (n instanceof parcer.block_de_noeuds) {
            fillColor = new Color(200, 255, 200); // Vert clair pour les blocs
        } else if (n instanceof parcer.noeud_d_operation_binaire) {
            fillColor = new Color(180, 220, 255); // Bleu clair pour les opérations
        } else if (n instanceof parcer.noeud_d_asignation) {
            fillColor = new Color(255, 200, 200); // Rose clair pour les assignations
        } else if (n instanceof parcer.noeud_de_valeur) {
            fillColor = new Color(255, 255, 180); // Jaune clair pour les valeurs
        } else {
            fillColor = new Color(230, 230, 250); // Par défaut (lavande)
        }

        // Dessin du nœud avec bordure noire
        g.setColor(fillColor);
        g.fillRoundRect(x - 45, y - 15, 90, 30, 10, 10);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x - 45, y - 15, 90, 30, 10, 10);

        // label: use meaningful label (not only class name)
        String label = nodeLabel(n);
        FontMetrics fm = g.getFontMetrics();
        int textW = fm.stringWidth(label);
        // if label too long, clip
        String drawLabel = label;
        if (textW > w - 10) {
            // simple clipping with ellipsis
            while (drawLabel.length() > 0 && fm.stringWidth(drawLabel + "...") > w - 10) {
                drawLabel = drawLabel.substring(0, drawLabel.length() - 1);
            }
            drawLabel = drawLabel + "...";
        }
        g.drawString(drawLabel, x - fm.stringWidth(drawLabel) / 2, y + fm.getAscent() / 2 - 2);

        // recurse to draw children nodes
        for (parcer.noeud ch : getChildren(n)) {
            drawNodes(g, ch);
        }
    }

    // compute a readable label for a node (show important fields)
    private String nodeLabel(parcer.noeud n) {
        if (n instanceof parcer.programmeNoeud) return "Programme";
        if (n instanceof parcer.noeud_si si) return "Si";
        if (n instanceof parcer.block_de_noeuds) return "Bloc";
        if (n instanceof parcer.noeud_d_asignation a) return "Assign: " + a.identificateur;
        if (n instanceof parcer.noeud_de_condition c) return c.gauche + " " + c.operateur + " " + c.droite;
        if (n instanceof parcer.noeud_de_valeur v) return "Val: " + v.valeur;
        if (n instanceof parcer.noeud_d_operation_binaire op) return op.operateur.toString();
        return n.getClass().getSimpleName();
    }*/
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
    private String nodeLabel(parcer.noeud n) {
        if (n == null) return "❌ Erreur"; // 🔥 Protection anti-null

        try {
            if (n instanceof parcer.programmeNoeud) return "Programme";
            if (n instanceof parcer.noeud_si) return  "Si";
            if (n instanceof parcer.noeud_sinonSi) return  "Sinon si";
            if (n instanceof parcer.block_de_noeuds) return "Bloc" + " : " + ((parcer.block_de_noeuds) n).type;
            if (n instanceof parcer.noeud_d_asignation a) return "Assign: " + a.identificateur;
            if (n instanceof parcer.noeud_de_condition c)
                return (c.gauche != null ? c.gauche.toString() : "?") +
                       " " + (c.operateur != null ? c.operateur : "?") +
                       " " + (c.droite != null ? c.droite.toString() : "?");
            if (n instanceof parcer.noeud_de_valeur v)
                return "Val: " + (v.valeur != null ? v.valeur.toString() : "null");
            if (n instanceof parcer.noeud_d_operation_binaire op)
                return (op.operateur != null ? op.operateur.toString() : "op");
            if (n instanceof parcer.VariableNode)return n.getClass().getSimpleName() + " " + ((parcer.VariableNode) n).nom + " = " /*+ ((parcer.VariableNode) n).valeur*/;
            if (n instanceof parcer.ClasseNode)return ((parcer.ClasseNode) n).modificateur +" "+ n.getClass().getSimpleName() +" "+ ((parcer.ClasseNode) n).nom;
            if (n instanceof parcer.MethodeNode)return (((parcer.MethodeNode) n).modificateur != null ? ((parcer.MethodeNode) n).modificateur : "")+((parcer.MethodeNode) n).typeRetour +" "+ n.getClass().getSimpleName() +" "+ ((parcer.MethodeNode) n).nom;
            if (n instanceof parcer.noeud_parametre)return n.toString();
            if (n instanceof parcer.noeud_tafoukt)return ((parcer.noeud_tafoukt) n).toString() +" "+ n.getClass().getSimpleName() ;
            if (n instanceof parcer.noeud_zakaria)return ((parcer.noeud_zakaria) n).toString() +" "+ n.getClass().getSimpleName() ;
            
            return n.getClass().getSimpleName();
        } catch (Exception e) {
            return "⚠️ Erreur";
        }
    }


    // helper: unify all child extraction into a list (handles arrays and lists)
    private java.util.List<parcer.noeud> getChildren(parcer.noeud n) {
        java.util.List<parcer.noeud> list = new java.util.ArrayList<>();
        if (n instanceof parcer.programmeNoeud p) {
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
        }else if (n instanceof parcer.block_de_noeuds b) {
            if (b.mots != null) list.addAll(b.mots);
        } else if (n instanceof parcer.noeud_d_asignation a) {
            if (a.valeur != null) list.add(a.valeur);
        } else if (n instanceof parcer.noeud_d_operation_binaire op) {
            if (op.gauche != null) list.add(op.gauche);
            if (op.droite != null) list.add(op.droite);
        }else if (n instanceof parcer.ClasseNode c) {
            if (c.membres != null) list.addAll(c.membres);
        }else if (n instanceof parcer.MethodeNode m) {
            if (m.instructions != null) list.addAll(m.instructions);
            if (m.Pretour != null) list.addAll(m.Pretour);
        }else if (n instanceof parcer.VariableNode v) {
            if (v.valeur != null) list.add(v.valeur);
        }else if (n instanceof parcer.noeud_tafoukt v) {
            if (v.valeur != null) list.add(v.valeur);
        }else if (n instanceof parcer.noeud_zakaria v) {
            if (v.valeur != null) list.add(v.valeur);
        }else if (n instanceof parcer.noeud_return v) {
            if (v.valeur != null) list.add(v.valeur);
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



/*/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 *
package com.mycompany.projet_compil_if_else_2;

/**
 *
 * @author InfoPro
 *
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 *


/**
 *
 * @author InfoPro
 *
//package com.mycompany.projet_compil_if_else_2;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.List;

public class essaie extends JFrame {

    private JTextArea codeArea;
    private JTextArea tokensArea;
    private JTree astTree; // 🌳 arbre syntaxique
    private JScrollPane tokenScroll;
    private JScrollPane treeScroll;

    public essaie() {
        setTitle("Analyseur lexical et syntaxique");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // --- ZONE DE SAISIE CODE ---
        codeArea = new JTextArea();
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setBorder(BorderFactory.createTitledBorder("Code source"));
        codeArea.setText("if (x > 0) { y = 1 + 2 * 3; } else { y = -1; }");

        // --- ANALYSE LEXICALE ---
        tokensArea = new JTextArea();
        tokensArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        tokensArea.setEditable(false);
        tokensArea.setBorder(BorderFactory.createTitledBorder("Analyse lexicale"));

        tokenScroll = new JScrollPane(tokensArea);

        // --- ARBRE SYNTAXIQUE ---
        astTree = new JTree(new DefaultMutableTreeNode("Arbre syntaxique"));
        treeScroll = new JScrollPane(astTree);
        treeScroll.setBorder(BorderFactory.createTitledBorder("Arbre syntaxique (AST)"));

        // --- SPLIT PANE ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tokenScroll, treeScroll);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.3);
        add(splitPane, BorderLayout.CENTER);

        // --- BOUTON D’ANALYSE ---
        JButton analyserBtn = new JButton("Analyser");
        analyserBtn.addActionListener(e -> analyserCode());

        add(new JScrollPane(codeArea), BorderLayout.NORTH);
        add(analyserBtn, BorderLayout.SOUTH);

        setVisible(true);
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

            StringBuilder sbTokens = new StringBuilder();
            for (lexer.token t : tokens) {
                sbTokens.append(t).append("\n");
            }
            tokensArea.setText(sbTokens.toString());

            // --- ANALYSE SYNTAXIQUE ---
            parcer parser = new parcer(tokens);
            parcer.programmeNoeud programme = parser.parcerProgramme();

            // Construire un arbre graphique à partir du nœud racine
            DefaultMutableTreeNode root = buildTree(programme);
            astTree.setModel(new javax.swing.tree.DefaultTreeModel(root));

            // Tout déplier pour que l’utilisateur voie l’arbre complet
            for (int i = 0; i < astTree.getRowCount(); i++) {
                astTree.expandRow(i);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Analyse échouée", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------------
    // 🌳 Construction récursive du JTree à partir des nœuds du parseur
    // ------------------------------------------------------------------------
    private DefaultMutableTreeNode buildTree(parcer.noeud n) {
        if (n instanceof parcer.programmeNoeud p) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Programme");
            for (parcer.noeud child : p.mots) node.add(buildTree(child));
            return node;

        } else if (n instanceof parcer.noeud_si si) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Si");
            DefaultMutableTreeNode condNode = new DefaultMutableTreeNode("Condition: " + si.condition.gauche + " " + si.condition.operateur + " " + si.condition.droite);
            node.add(condNode);
            DefaultMutableTreeNode alorsNode = new DefaultMutableTreeNode("Alors");
            alorsNode.add(buildTree(si.blockAlors));
            node.add(alorsNode);
            if (si.block_sinon != null) {
                DefaultMutableTreeNode sinonNode = new DefaultMutableTreeNode("Sinon");
                sinonNode.add(buildTree(si.block_sinon));
                node.add(sinonNode);
            }
            return node;

        } else if (n instanceof parcer.block_de_noeuds b) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Bloc");
            for (parcer.noeud child : b.mots) node.add(buildTree(child));
            return node;

        } else if (n instanceof parcer.noeud_d_asignation a) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Assignation: " + a.identificateur);
            node.add(buildTree(a.valeur));
            return node;

        } else if (n instanceof parcer.noeud_d_operation_binaire op) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("Opération: " + op.operateur);
            node.add(buildTree(op.gauche));
            node.add(buildTree(op.droite));
            return node;

        } else if (n instanceof parcer.noeud_de_valeur v) {
            return new DefaultMutableTreeNode("Valeur: " + v.valeur);

        } else if (n instanceof parcer.noeud_de_condition c) {
            return new DefaultMutableTreeNode("Condition: " + c.gauche + " " + c.operateur + " " + c.droite);
        }

        return new DefaultMutableTreeNode("?");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(essaie::new);
    }
}
*/