/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_3;

/**
 *
 * @author InfoPro
 */
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FenetreErreurs extends JDialog {

    public FenetreErreurs(JFrame parent, List<String> erreursLexicales, List<String> erreursSyntaxiques, List<String> erreursSemantiques) {
        super(parent, "Liste des erreurs", true); // true = fenêtre modale
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JTextArea erreurArea = new JTextArea();
        erreurArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        erreurArea.setEditable(false);

        if (erreursLexicales.isEmpty()&& erreursSyntaxiques.isEmpty() && erreursSemantiques.isEmpty()) {
            erreurArea.setText("Aucune erreur détectée ✅");
        } else {
            StringBuilder sb = new StringBuilder();
            if(!erreursLexicales.isEmpty()){
                sb.append("").append("=== ERREURS LEXICALES ===").append("\n");
                for (String e : erreursLexicales) {
                    sb.append("• ").append(e).append("\n");
                }
                erreurArea.setText(sb.toString());
            }
            
            if(!erreursSyntaxiques.isEmpty()){
                sb.append("").append("\n=== ERREURS SYNTAXIQUES ===").append("\n");
                for (String e : erreursSyntaxiques) {
                    sb.append("• ").append(e).append("\n");
                }
                erreurArea.setText(sb.toString());
            }
            
            if(!erreursSemantiques.isEmpty()){
                sb.append("").append("\n=== ERREURS SEMANTIQUES ===").append("\n");
                for (String e : erreursSemantiques) {
                    sb.append("• ").append(e).append("\n");
                }
                erreurArea.setText(sb.toString());
            }
            /*StringBuilder sb = new StringBuilder();
            for (String e : erreurs) {
                sb.append("• ").append(e).append("\n");
            }*/
            erreurArea.setText(sb.toString());
        }

        JScrollPane scroll = new JScrollPane(erreurArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Détails des erreurs"));

        JButton fermer = new JButton("Fermer");
        fermer.addActionListener(e -> dispose());

        JPanel bas = new JPanel();
        bas.add(fermer);

        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(bas, BorderLayout.SOUTH);
    }
}

