/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_3;

import IR_v3.HIR;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.*;

/**
 *
 * @author tafou
 */

public class FenetreIR extends JFrame {

    private JTabbedPane tabs;

    private HIR.HIRNode racine;
    private ArbreHIRPanel hirPanel;
    private JTextArea lirArea;
    private JTextPane lirPane;

    public FenetreIR(HIR.HIRNode racine) {
        
        this.racine = racine;
        
        System.out.println("Constructeur : " + this);

        setTitle("Visualiseur IR");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabs = new JTabbedPane();

        //hirArea = new JTextArea();
        lirArea = new JTextArea();

        //hirArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        lirArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        
        lirPane = new JTextPane();
        lirPane.setText(lirArea.getText());
        lirPane.setEditable(false);
        lirPane.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        lirPane.setBackground(new Color(18,18,18));
        lirPane.setForeground(new Color(220,220,220));
        lirPane.setCaretColor(Color.WHITE);
        lirPane.setSelectionColor(new Color(70,90,150));

        hirPanel = new ArbreHIRPanel(racine);

        tabs.addTab(
            "HIR",
            new JScrollPane(hirPanel)
        );
        tabs.addTab("LIR", new JScrollPane(lirArea));
        tabs.addTab("LIR", new JScrollPane(lirPane));

        add(tabs);
    }
    
    public void setHIR(HIR.HIRNode root){
        hirPanel.setRacine(root);
    }

    public void setLIR(String texte){
        System.out.println("setLir test :"+this);
        System.out.println("SET LIR");
        System.out.println(texte);
        lirArea.setText(texte);
        lirPane.setText(texte);
        
        System.out.println("Longueur = " + lirArea.getText().length());
    }
    
    // place this inside your Essaie class (replace existing ArbrePanel)
    static class ArbreHIRPanel extends JPanel {
        private HIR.HIRNode racine;

        // layout parameters (tweak to taste)
        private final int nodeWidth = 120;
        private final int nodeHeight = 34;
        private final int vGap = 80;   // vertical gap between levels
        private final int hGap = 30;   // minimal horizontal gap between sibling subtrees

        private double zoom = 1.0; // ← facteur de zoom (1.0 = normal)

        // caches
        private final java.util.Map<HIR.HIRNode, Integer> subtreeWidth = new java.util.HashMap<>();
        private final java.util.Map<HIR.HIRNode, Point> nodePositions = new java.util.HashMap<>();

        public ArbreHIRPanel(HIR.HIRNode racine) {
            this.racine = racine;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(1200, 800));
        }

        public void setRacine(HIR.HIRNode racine) {
            
             System.out.println("setRacine sur : " + this);

            this.racine = racine;
            
            System.out.println("setRacine appelé");
            System.out.println(racine);
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
            System.out.println("paintComponent");

            if (racine == null) {
                System.out.println("racine null");
                return;
            }
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
        private int computeSubtreeWidth(HIR.HIRNode n) {
            if (subtreeWidth.containsKey(n)) return subtreeWidth.get(n);

            java.util.List<HIR.HIRNode> children = getChildren(n);
            int w;
            if (children.isEmpty()) {
                w = nodeWidth;
            } else {
                int sum = 0;
                for (HIR.HIRNode ch : children) {
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
        private void layoutNode(HIR.HIRNode n, int leftX, int y) {
            int w = subtreeWidth.getOrDefault(n, nodeWidth);
            java.util.List<HIR.HIRNode> children = getChildren(n);

            if (children.isEmpty()) {
                // center the leaf inside its allocated width
                int centerX = leftX + w / 2;
                nodePositions.put(n, new Point(centerX, y));
            } else {
                // layout children sequentially inside [leftX, leftX + w]
                int curX = leftX;
                for (int i = 0; i < children.size(); i++) {
                    HIR.HIRNode ch = children.get(i);
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

        private void drawEdges(Graphics2D g, HIR.HIRNode n) {
            Point p = nodePositions.get(n);
            if (p == null) return;

            java.util.List<HIR.HIRNode> children = getChildren(n);
            for (HIR.HIRNode ch : children) {
                Point c = nodePositions.get(ch);
                if (c != null) {
                    // --- couleur selon l'état du nœud fils ---
                    /*if (ch.erreur) {
                        g.setColor(Color.RED); // rouge pour erreur
                    } else if (ch.messageErreur != null && !ch.messageErreur.isEmpty()) {
                        g.setColor(Color.ORANGE); // orange pour warning ou info
                    } else {*/
                        g.setColor(Color.GRAY); // normal
                    //}

                    // --- dessiner la ligne ---
                    g.drawLine(p.x, p.y + nodeHeight / 2, c.x, c.y - nodeHeight / 2);

                    // --- dessiner le message parallèle à la ligne ---
                    /*if (ch.erreur || (ch.messageErreur != null && !ch.messageErreur.isEmpty())) {
                        String msg = ch.messageErreur != null ? ch.messageErreur : "⚠️ Erreur";
                        int midX = (p.x + c.x) / 2;
                        int midY = (p.y + nodeHeight / 2 + c.y - nodeHeight / 2) / 2;

                        g.setColor(Color.BLACK); // couleur texte
                        g.drawString(msg, midX + 5, midY); // légèrement décalé à droite
                    }*/

                    // --- récursion ---
                    drawEdges(g, ch);
                }
            }
        }



        // --- DESSIN DES NOEUDS (rectangles + label) ---
        private void drawNodes(Graphics2D g, HIR.HIRNode n) {
            if (n == null) return;

            Point p = nodePositions.get(n);
            if (p == null) return;

            int x = p.x;
            int y = p.y;
            int baseW = nodeWidth;
            int h = nodeHeight;

            // --- COULEUR SELON TYPE DE NOEUD ---
            Color fillColor;
            /*if (n instanceof parcer.noeud_si) {
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
            } else {* /
                fillColor = new Color(230, 230, 250);
            }*/
            if (n instanceof HIR.HIRIf) {
                fillColor = new Color(170, 40, 40);          // Rouge sombre
            }
            else if (n instanceof HIR.HIRWhile
                  || n instanceof HIR.HIRDoWhile
                  || n instanceof HIR.HIRFor) {
                fillColor = new Color(130, 30, 30);          // Rouge foncé
            }
            else if (n instanceof HIR.HIRBlock) {
                fillColor = new Color(55, 55, 55);           // Gris anthracite
            }
            else if (n instanceof HIR.HIRAssign) {
                fillColor = new Color(110, 60, 20);          // Marron/cuivre
            }
            else if (n instanceof HIR.HIRBinary) {
                fillColor = new Color(70, 20, 120);          // Violet profond
            }
            else if (n instanceof HIR.HIRUnary) {
                fillColor = new Color(100, 40, 130);         // Violet
            }
            else if (n instanceof HIR.HIRVar) {
                fillColor = new Color(20, 90, 120);          // Bleu pétrole
            }
            else if (n instanceof HIR.HIRVarDecl) {
                fillColor = new Color(30, 120, 90);          // Vert foncé
            }
            else if (n instanceof HIR.HIRConst) {
                fillColor = new Color(150, 150, 30);         // Jaune olive
            }
            else if (n instanceof HIR.HIRReturn) {
                fillColor = new Color(180, 80, 20);          // Orange brûlé
            }
            else if (n instanceof HIR.HIRBreak) {
                fillColor = new Color(180, 20, 20);          // Rouge vif
            }
            else if (n instanceof HIR.HIRContinue) {
                fillColor = new Color(20, 140, 180);         // Cyan foncé
            }
            else if (n instanceof HIR.HIRCall
                  || n instanceof HIR.HIRMethodCall) {
                fillColor = new Color(30, 100, 150);         // Bleu acier
            }
            else if (n instanceof HIR.HIRNewObject
                  || n instanceof HIR.HIRNewArray) {
                fillColor = new Color(80, 110, 20);          // Vert olive
            }
            else if (n instanceof HIR.HIRFieldAccess
                  || n instanceof HIR.HIRArrayAccess) {
                fillColor = new Color(90, 90, 90);           // Gris métal
            }
            else if (n instanceof HIR.HIRTernary) {
                fillColor = new Color(150, 60, 100);         // Rose sombre
            }
            else if (n instanceof HIR.HIRFunction) {
                fillColor = new Color(25, 60, 140);          // Bleu marine
            }
            else if (n instanceof HIR.HIRClass) {
                fillColor = new Color(40, 90, 40);           // Vert militaire
            }
            else if (n instanceof HIR.HIRProgram) {
                fillColor = new Color(40, 40, 40);           // Noir/gris
            }
            else {
                fillColor = new Color(80, 80, 80);
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
            for (HIR.HIRNode ch : getChildren(n)) {
                if (ch != null) drawNodes(g, ch);
            }
        }





        // --- LABEL D’UN NOEUD ---
        private String nodeLabels(HIR.HIRNode n) {
            if (n == null) return "❌ Erreur"; // 🔥 Protection anti-null

            try {/*
                if (n instanceof parcer.programmeNoeud) return "Programme";
                if (n instanceof parcer.noeud_si) return  "Si";
                if (n instanceof parcer.noeud_sinonSi) return  "Sinon si";
                if (n instanceof parcer.block_de_noeuds) return "Bloc" + " : " + ((parcer.block_de_noeuds) n).type;
                if (n instanceof parcer.noeud_d_asignation a) return "Assign: " + a.identificateur;
                if (n instanceof parcer.noeud_de_condition c)
                    return c.operateur.name();/*(c.gauche != null ? c.gauche.toString() : "?") +
                           " " + (c.operateur != null ? c.operateur : "?") +
                           " " + (c.droite != null ? c.droite.toString() : "?");* /
                if (n instanceof parcer.noeud_de_valeur v)
                    return "Val: " + (v.valeur != null ? v.valeur.toString() : "null");
                if (n instanceof parcer.noeud_d_operation_binaire op)
                    return (op.operateur != null ? op.operateur.toString() : "op");
                if (n instanceof parcer.VariableNode)return n.getClass().getSimpleName() + " " + ((parcer.VariableNode) n).nom + " = " /*+ ((parcer.VariableNode) n).valeur* /;
                if (n instanceof parcer.ClasseNode)return ((parcer.ClasseNode) n).modificateur +" "+ n.getClass().getSimpleName() +" "+ ((parcer.ClasseNode) n).nom;
                if (n instanceof parcer.MethodeNode)return (((parcer.MethodeNode) n).modificateur != null ? ((parcer.MethodeNode) n).modificateur : "")+((parcer.MethodeNode) n).typeRetour +" "+ n.getClass().getSimpleName() +" "+ ((parcer.MethodeNode) n).nom;
                if (n instanceof parcer.noeud_parametre)return n.toString();
                if (n instanceof parcer.NoeudAccesChamp ac){return n.getClass().getSimpleName() + "  " + ((parcer.NoeudAccesChamp) n).champ;}
                if (n instanceof parcer.NoeudAppelMethodeObjet ac){return n.getClass().getSimpleName() + "   " + ((parcer.NoeudAppelMethodeObjet) n).methode;}
                if (n instanceof parcer.noeud_tafoukt)return ((parcer.noeud_tafoukt) n).toString() +" "+ n.getClass().getSimpleName() ;
                if (n instanceof parcer.noeud_zakaria)return ((parcer.noeud_zakaria) n).toString() +" "+ n.getClass().getSimpleName() ;*/
                
                if (n instanceof HIR.HIRProgram)
                    return "Programme";

                if (n instanceof HIR.HIRClass c)
                    return "Classe : " + c.name;

                if (n instanceof HIR.HIRFunction f)
                    return "Fonction : " + f.name;

                if (n instanceof HIR.HIRBlock)
                    return "Bloc";

                if (n instanceof HIR.HIRIf)
                    return "If";

                if (n instanceof HIR.HIRWhile)
                    return "While";

                if (n instanceof HIR.HIRDoWhile)
                    return "DoWhile";

                if (n instanceof HIR.HIRFor)
                    return "For";

                if (n instanceof HIR.HIRReturn)
                    return "Return";

                if (n instanceof HIR.HIRBreak)
                    return "Break";

                if (n instanceof HIR.HIRContinue)
                    return "Continue";

                if (n instanceof HIR.HIRAssign)
                    return "Assign";

                if (n instanceof HIR.HIRVarDecl v)
                    return "Var : " + v.name;

                if (n instanceof HIR.HIRVar v)
                    return "Var : " + v.name;

                if (n instanceof HIR.HIRConst c)
                    return "Const : " + c.value;

                if (n instanceof HIR.HIRBinary b)
                    return b.op+ b.type;

                if (n instanceof HIR.HIRUnary u)
                    return u.op.toString();

                if (n instanceof HIR.HIRCall c)
                    return "Call : " + c.function;

                if (n instanceof HIR.HIRMethodCall c)
                    return "Call : " + c.method;

                if (n instanceof HIR.HIRNewObject o)
                    return "new " + o.className;

                if (n instanceof HIR.HIRFieldAccess f)
                    return "." + f.field;

                if (n instanceof HIR.HIRArrayAccess)
                    return "[]";

                if (n instanceof HIR.HIRTernary)
                    return "? :";

                if (n instanceof HIR.HIRNewArray)
                    return "new[]";

                return n.getClass().getSimpleName();
            } catch (Exception e) {
                return "⚠️ Erreur";
            }
        }

        private String nodeLabel(HIR.HIRNode n) {
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
        private java.util.List<HIR.HIRNode> getChildren(HIR.HIRNode n) {
            java.util.List<HIR.HIRNode> list = new java.util.ArrayList<>();
            /*if (n instanceof parcer.programmeNoeud p) {
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
            }*/
            if (n instanceof HIR.HIRProgram pr){
                if(pr.classes != null) list.addAll(pr.classes);
                if(pr.functions != null) list.addAll(pr.functions);
                if(pr.globals != null) list.addAll(pr.globals);
                if(pr.body != null) list.add(pr.body);
            }
            
            else if (n instanceof HIR.HIRClass c) {

                if (c.fields != null) list.addAll(c.fields);
                if (c.methods != null) list.addAll(c.methods);

            }

            else if (n instanceof HIR.HIRFunction f) {

                if (f.locals != null) list.addAll(f.locals);

                if (f.body != null)
                    list.add(f.body);

            }

            else if (n instanceof HIR.HIRBlock b) {

                if (b.statements != null)
                    list.addAll(b.statements);

            }

            else if (n instanceof HIR.HIRAssign a) {

                if (a.target != null)
                    list.add(a.target);

                if (a.value != null)
                    list.add(a.value);

            }

            else if (n instanceof HIR.HIRIf i) {

                if (i.condition != null)
                    list.add(i.condition);

                if (i.thenBlock != null)
                    list.add(i.thenBlock);

                if (i.elseBlock != null)
                    list.add(i.elseBlock);

            }

            else if (n instanceof HIR.HIRWhile w) {

                if (w.condition != null)
                    list.add(w.condition);

                if (w.body != null)
                    list.add(w.body);

            }

            else if (n instanceof HIR.HIRDoWhile d) {

                if (d.body != null)
                    list.add(d.body);

                if (d.condition != null)
                    list.add(d.condition);

            }

            else if (n instanceof HIR.HIRFor f) {

                if (f.init != null)
                    list.add(f.init);

                if (f.condition != null)
                    list.add(f.condition);

                if (f.increment != null)
                    list.add(f.increment);

                if (f.body != null)
                    list.add(f.body);

            }

            else if (n instanceof HIR.HIRReturn r) {

                if (r.value != null)
                    list.add(r.value);

            }

            else if (n instanceof HIR.HIRExprStmt e) {

                if (e.expr != null)
                    list.add(e.expr);

            }

            else if (n instanceof HIR.HIRVarDecl v) {

                if (v.initializer != null)
                    list.add(v.initializer);

            }

            else if (n instanceof HIR.HIRBinary b) {

                if (b.left != null)
                    list.add(b.left);

                if (b.right != null)
                    list.add(b.right);

            }
            
            else if (n instanceof HIR.HIRUnary u) {

                if (u.operand != null)
                    list.add(u.operand);

            }

            else if (n instanceof HIR.HIRCall c) {

                if (c.arguments != null)
                    list.addAll(c.arguments);

            }

            else if (n instanceof HIR.HIRMethodCall c) {

                if (c.object != null)
                    list.add(c.object);

                if (c.arguments != null)
                    list.addAll(c.arguments);

            }

            else if (n instanceof HIR.HIRNewObject o) {

                if (o.arguments != null)
                    list.addAll(o.arguments);

            }

            else if (n instanceof HIR.HIRFieldAccess f) {

                if (f.object != null)
                    list.add(f.object);

            }

            else if (n instanceof HIR.HIRArrayAccess a) {

                if (a.array != null)
                    list.add(a.array);

                if (a.indices != null)
                    list.addAll(a.indices);

            }

            else if (n instanceof HIR.HIRTernary t) {

                if (t.condition != null)
                    list.add(t.condition);

                if (t.trueExpr != null)
                    list.add(t.trueExpr);

                if (t.falseExpr != null)
                    list.add(t.falseExpr);

            }

            else if (n instanceof HIR.HIRNewArray a) {

                if (a.sizes != null)
                    list.addAll(a.sizes);

            }

            // valeur and condition leafs will return empty list
            return list;
        }

        // compute tree depth (levels) to estimate preferred height
        private int computeDepth(HIR.HIRNode n) {
            if (n == null) return 0;
            int max = 0;
            for (HIR.HIRNode ch : getChildren(n)) {
                max = Math.max(max, computeDepth(ch));
            }
            return 1 + max;
        }
    }
}

