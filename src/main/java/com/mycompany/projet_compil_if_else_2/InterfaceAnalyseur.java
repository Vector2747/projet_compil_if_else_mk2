/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_2;

/**
 *
 * @author InfoPro
 */
import com.mycompany.projet_compil_if_else_2.lexer.token;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**public class InterfaceAnalyseur extends JFrame {

    private JTextArea codeArea;
    private JTextArea tokensArea;
    private JTextArea astArea;

    public InterfaceAnalyseur() {
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

        astArea = new JTextArea();
        astArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        astArea.setEditable(false);
        astArea.setBorder(BorderFactory.createTitledBorder("Arbre syntaxique"));

        // --- PANELS ---
        JButton analyserBtn = new JButton("Analyser");
        analyserBtn.addActionListener(e -> analyserCode());

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(new JScrollPane(tokensArea));
        centerPanel.add(new JScrollPane(astArea));

        add(new JScrollPane(codeArea), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
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
            List<token> tokens = lex.tokeniser();

            StringBuilder sbTokens = new StringBuilder();
            for (token t : tokens) {
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
        SwingUtilities.invokeLater(InterfaceAnalyseur::new);
    }
}
**/