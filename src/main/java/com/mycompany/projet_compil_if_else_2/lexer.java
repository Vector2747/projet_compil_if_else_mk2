/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author InfoPro
 */
//    c'est le fichier qui contiens l'analyseur lexical
public class lexer {
    // c'est ici qu'on fait les tokens 
        // en commencant par leurs type
    
    public enum TokenType{
        IF,ELSE,   //C'EST les types de trucs principales
        LPAREN,RPAREN, // pour les parentheses
        LBRACE,RBRACE, // ca s'appele comme ca en anglais
        SEMICOLON, // ;
        IDENTIFIER, // pour les vars
        NUMBER, // les valeurs en nombre
        ASSIGN, // les operateurs
        INCREMENT,DECREMENT, // ++, --
        GT, LT, EQEQ, NOTEQ, GTE, LTE,// >, <, ==, !=, >=, <=,
        PLUS, MINUS, MULT, DIV, MOD,// +, -, *, /, %,
        EOF // fin du fichier
        ,
        PUBLIC,CLASS,
        STATIC,PRIVATE,PROTECTED,VOID,
        
        INT,DOUBLE,FLOAT,LONG,CHAR,STRING,BOOLEAN
    }
    
    // cette c'asse s'ocupe de donner ces valeurs a un token (elle est en statique pour et en final pour eviter le faite qu'ells soient touches apres (precotion))
    public static class token{
        public final TokenType type;
        public final String lexeme;
        public final int position;
        
        public token(TokenType type, String lexeme, int position){
            this.type = type;
            this.lexeme = lexeme;
            this.position = position;
        }
        
        // juste pour la beautes et facilites pour le reste (pour l'affichage et le debuging)
        @Override
        public String toString() {
            return "<" + type + ", \"" + lexeme + "\">";
        }
    }
    
    // nous allos commencer le lexer par ici
    private final String input;    // ce qu'on fait rentrer
    private final int lenght;      // la taille de ce qu'on fait rentrer
    private int pos = 0;           // la position de ce qu'on fait rentrer
    private final List<token> tokens = new ArrayList<>();  // la liste de tout les tokens
    
    
    public lexer (String input){
        this.input = input;
        this.lenght = input.length();
    }
    
    public List<token> tokeniser(){
        while(!fin()){
            sauter_espaces();
            
            if(fin()){break;}
            int start = pos;
            char c = avancer();
            
            switch(c){
                case '(' -> tokens.add(new token(TokenType.LPAREN, "(", start));
                
                case ')' -> tokens.add(new token(TokenType.RPAREN, ")", start));
                
                case '{' -> tokens.add(new token(TokenType.LBRACE, "{", start));
                
                case '}' -> tokens.add(new token(TokenType.RBRACE, "}", start));
                
                case ';' -> tokens.add(new token(TokenType.SEMICOLON, ";", start));
                
                case '=' -> {if (est_conforme('=')){tokens.add(new token(TokenType.EQEQ, "==", start));pos++;}
                            else{tokens.add(new token(TokenType.ASSIGN, "=", start));}
                            }
                
                case '<' -> {if (est_conforme('=')){tokens.add(new token(TokenType.LTE, "<=", start));pos++;}
                            else{tokens.add(new token(TokenType.LT, "<", start));}
                            }
                
                case '>' -> {if (est_conforme('=')){tokens.add(new token(TokenType.GTE, ">=", start));pos++;}
                            else{tokens.add(new token(TokenType.GT, ">", start));}
                            }
                
                case '!' -> {if (est_conforme('=')){tokens.add(new token(TokenType.NOTEQ, "!=", start));pos++;}
                            else{erreur("Caractere '!' non suportes seul", start);}
                            }
                
                case '+' -> {if (est_conforme('+')){tokens.add(new token(TokenType.INCREMENT, "++", start));pos++;}
                            else{tokens.add(new token(TokenType.PLUS, "+", start));}
                            }
                
                case '-' -> {if(testCharPrecedent()){if (est_conforme('-')){tokens.add(new token(TokenType.DECREMENT, "--", start));pos++;}
                            else{tokens.add(new token(TokenType.MINUS, "-", start));}
                            }
                            }
                            
                
                case '*' -> tokens.add(new token(TokenType.MULT, "*", start));
                
                case '/' -> tokens.add(new token(TokenType.DIV, "/", start));
                
                case '%' -> tokens.add(new token(TokenType.MOD, "%", start));
                
                default -> {
                    if(est_alpha(c)){
                        while (!fin()&& est_alpha_num(examiner())){
                            avancer();
                        }
                        String subMot = input.substring(start, pos);
                        if (subMot.equals("if")) {
                            tokens.add(new token(TokenType.IF, "if", start));
                        } else if (subMot.equals("else")) {
                            tokens.add(new token(TokenType.ELSE, "else", start));
                        } else if (subMot.equals("class")) {
                            tokens.add(new token(TokenType.CLASS, "class", start));
                        } else if (subMot.equals("public")) {
                            tokens.add(new token(TokenType.PUBLIC, "public", start));
                        } else if (subMot.equals("private")) {
                            tokens.add(new token(TokenType.PRIVATE, "private", start));
                        } else if (subMot.equals("protected")) {
                            tokens.add(new token(TokenType.PROTECTED, "protected", start));
                        } else if (subMot.equals("static")) {
                            tokens.add(new token(TokenType.STATIC, "static", start));
                        } else if (subMot.equals("void")) {
                            tokens.add(new token(TokenType.VOID, "void", start));
                        } else if (subMot.equals("int")) {
                            tokens.add(new token(TokenType.INT, "int", start));
                        } else if (subMot.equals("double")) {
                            tokens.add(new token(TokenType.DOUBLE, "double", start));
                        } else if (subMot.equals("float")) {
                            tokens.add(new token(TokenType.FLOAT, "float", start));
                        } else if (subMot.equals("long")) {
                            tokens.add(new token(TokenType.LONG, "long", start));
                        } else if (subMot.equals("char")) {
                            tokens.add(new token(TokenType.CHAR, "char", start));
                        } else if (subMot.equals("boolean")) {
                            tokens.add(new token(TokenType.BOOLEAN, "boolean", start));
                        } else if (subMot.equals("String")) {
                            tokens.add(new token(TokenType.STRING, "String", start));
                        } else {
                            tokens.add(new token(TokenType.IDENTIFIER, subMot, start));
                        }

                    }else{
                        if((est_num(c))||((c == '-')&&(est_num(examiner()))&&(!fin()))){
                            while(est_num(examiner())){
                                avancer();
                            }
                            tokens.add(new token(TokenType.NUMBER, input.substring(start, pos), start));
                        }else{
                            erreur("Caractere incorect : " + c, pos);
                        }
                    }
                }
            }
        }
        tokens.add(new token(TokenType.EOF, "johnatan", pos));
        return tokens;
        
    }
    
    // les methodes utilitaires 
    
    // pour savoir si on est a la fin du input
    private boolean fin(){
        if(pos >=lenght){
            return true;
        }
        return false;
    }
    
    // pour avancer
    private char avancer(){
        return input.charAt(pos++);
    }
    
    private char charPrecedent(){
        System.out.print(input.charAt(pos-2));
        return input.charAt(pos-2);
    }
    
    private char deuxCharPrecedent(){
        System.out.print(input.charAt(pos-3));
        return input.charAt(pos-3);
    }
    
    private boolean testCharPrecedent(){
        if (est_alpha_num(charPrecedent())|| est_alpha_num(deuxCharPrecedent())){
            return true;
        }
        return false;
    }
    
    // pour jeter un coup d'oeil
    private char examiner(){
        return fin() ? '\0' : input.charAt(pos);
    }
    
    private char examiner(int i){
        System.out.println(input.charAt(pos+i));
        return fin() ? '\0' : input.charAt(pos+i);
    }
    
    private token examiner(boolean r){
        return tokens.get(pos);
    }
    
    // pour jeter un coup d'oeil specifique
    private boolean est_conforme(char prevue){
        char c = examiner();
        if (c == prevue){
            return true;
        }else{return false;}
    }
    
    // sauter les espaces
    private void sauter_espaces(){
        while(!fin()){
            char c = examiner();
            
            if((c ==' ')||(c=='\n')||(c=='\t')||(c=='\r')){
                avancer();
            }else if (c == '/' && examiner(1) == '/') {
            while (!fin() && examiner() != '\n') avancer();
        } else if (c == '/' && examiner(1) == '*') {
            avancer(); avancer();
            while (!fin() && !(examiner() == '*' && examiner(1) == '/')) avancer();
            if (!fin()) { avancer(); avancer(); }
        } else{
                break;
            }
        }
    }
    public List<String> erreurs = new ArrayList<>();
    // pour arreter le prog si erreur 
    private void erreur(String messageDerreur, int position){
        //throw new RuntimeException("Erreur lexical a la position : "+position+"   :   "+messageDerreur);
    token tokee = examiner(true);
        String msg ="Erreur lexical a la position : "+position+"   :   "+messageDerreur;
        
        erreurs.add(msg);
        
        synchroniser();
    }
    
    private void synchroniser(){
        
        while (!fin()){
            TokenType exam = examiner(true).type;
            if(exam==TokenType.RBRACE || exam==TokenType.RPAREN || exam==TokenType.SEMICOLON){
                avancer();
                break;
            }
            avancer();
        }
    }
    
    // pour verifier si c'est un alphabet
    private boolean est_alpha(char c){
        if (((c>='A')&&(c<='Z'))||((c>='a')&&(c<='z'))){
            return true;
        }else{
            return false;
        }
    }
    
    // pour verifier si c'est un numero
    private boolean est_num(char c){
        if ((c>='0')&&(c<='9')){
            return true;
        }else{
            return false;
        }
    }
    
    // pour verifier si c'est un alphanumerique
    private boolean est_alpha_num(char c){
        if (est_alpha(c) || est_num(c)){
            return true;
        }else{
            return false;
        }
    }
    
    
    
    // 🧪 Petit test rapide
    public static void main(String[] args) {
        String code = /*"if (x > 0) { y = 1; } else { y = -1; }"*/"if (x > 0) { y = 1 + 2 * 3; } else { if(d=1{ y = -1; }else{d= ((sr+4)-2)*g}  f= c++;}";
        lexer lexor = new lexer(code);
        List<token> tokens = lexor.tokeniser();
        tokens.forEach(System.out::println);
    }
    
    
    
}
