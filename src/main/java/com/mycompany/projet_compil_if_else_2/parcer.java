/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_2;

import com.mycompany.projet_compil_if_else_2.lexer.TokenType;
import com.mycompany.projet_compil_if_else_2.lexer.token;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author InfoPro
 */


/**import java.util.ArrayList;
import java.util.List;

import com.mycompany.projet_compil_if_else_2.lexer.token;
import com.mycompany.projet_compil_if_else_2.lexer.TokenType;

public class parcer {

    // ------------------- AST -------------------
    public interface Node {}

    public static class ProgramNode implements Node {
        public final List<Node> statements;
        public ProgramNode(List<Node> statements) { this.statements = statements; }
    }

    public static class IfNode implements Node {
        public final ConditionNode condition;
        public final BlockNode thenBlock;
        public final BlockNode elseBlock; // peut être null
        public IfNode(ConditionNode cond, BlockNode thenBlock, BlockNode elseBlock) {
            this.condition = cond; this.thenBlock = thenBlock; this.elseBlock = elseBlock;
        }
    }

    public static class BlockNode implements Node {
        public final List<Node> statements;
        public BlockNode(List<Node> statements) { this.statements = statements; }
    }

    public static class AssignNode implements Node {
        public final String identifier;
        public final String value;
        public AssignNode(String id, String val) { this.identifier = id; this.value = val; }
    }

    public static class ConditionNode implements Node {
        public final String left;
        public final TokenType operator;
        public final String right;
        public ConditionNode(String left, TokenType operator, String right) {
            this.left = left; this.operator = operator; this.right = right;
        }
    }

    // ------------------- PARSER -------------------
    private final List<token> tokens;
    private int pos = 0;

    public parcer(List<token> tokens) {
        this.tokens = tokens;
    }

    public ProgramNode parseProgram() {
        List<Node> stmts = new ArrayList<>();
        while (!isAtEnd()) {
            stmts.add(parseStatement());
        }
        return new ProgramNode(stmts);
    }

    private Node parseStatement() {
        if (match(TokenType.IF)) {
            return parseIf();
        } else if (check(TokenType.IDENTIFIER)) {
            return parseAssign();
        }
        error("Instruction attendue (if ou assignation).");
        return null; // unreachable
    }

    private IfNode parseIf() {
        consume(TokenType.LPAREN, "Attendu '(' après 'if'.");
        ConditionNode condition = parseCondition();
        consume(TokenType.RPAREN, "Attendu ')' après la condition.");
        BlockNode thenBlock = parseBlock();
        BlockNode elseBlock = null;
        if (match(TokenType.ELSE)) {
            elseBlock = parseBlock();
        }
        return new IfNode(condition, thenBlock, elseBlock);
    }

    private BlockNode parseBlock() {
        consume(TokenType.LBRACE, "Attendu '{' pour commencer le bloc.");
        List<Node> stmts = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            stmts.add(parseStatement());
        }
        consume(TokenType.RBRACE, "Attendu '}' pour fermer le bloc.");
        return new BlockNode(stmts);
    }

    private AssignNode parseAssign() {
        token id = consume(TokenType.IDENTIFIER, "Identifiant attendu.");
        consume(TokenType.ASSIGN, "Attendu '='.");
        token val =null;
        if (match(TokenType.IDENTIFIER)) val = previous();
        else if (match(TokenType.NUMBER)) val = previous();
        else error("Valeur attendue (identifiant ou nombre).");
        consume(TokenType.SEMICOLON, "Attendu ';' à la fin de l’assignation.");
        return new AssignNode(id.lexeme, val.lexeme);
    }

    private ConditionNode parseCondition() {
        token left = consume(TokenType.IDENTIFIER, "Identifiant attendu à gauche de la condition.");
        TokenType op = null;
        if (match(TokenType.GT)) op = TokenType.GT;
        else if (match(TokenType.LT)) op = TokenType.LT;
        else if (match(TokenType.GTE)) op = TokenType.GTE;
        else if (match(TokenType.LTE)) op = TokenType.LTE;
        else if (match(TokenType.EQEQ)) op = TokenType.EQEQ;
        else if (match(TokenType.NOTEQ)) op = TokenType.NOTEQ;
        else error("Opérateur relationnel attendu (>, <, >=, <=, ==, !=).");
        token right = consumeAnyOf(TokenType.IDENTIFIER, TokenType.NUMBER, "Valeur attendue à droite de la condition.");
        return new ConditionNode(left.lexeme, op, right.lexeme);
    }

    // ------------------- OUTILS -------------------
    private boolean match(TokenType type) {
        if (check(type)) { pos++; return true; }
        return false;
    }

    private token consume(TokenType type, String msg) {
        if (check(type)) return tokens.get(pos++);
        error(msg);
        return null;
    }

    private token consumeAnyOf(TokenType t1, TokenType t2, String msg) {
        if (check(t1) || check(t2)) return tokens.get(pos++);
        error(msg);
        return null;
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type == type;
    }

    private token peek() { return tokens.get(pos); }
    private token previous() { return tokens.get(pos - 1); }

    private boolean isAtEnd() { return peek().type == TokenType.EOF; }

    private void error(String msg) {
        token token = peek();
        throw new RuntimeException("Erreur syntaxique près de '" + token.lexeme + "' (pos " + token.position + "): " + msg);
    }

    // ------------------- MAIN TEST -------------------
    public static void main(String[] args) {
        String code = "if (x > 0) { y = 1; } else { y = -2; }";
        lexer lexer = new lexer(code);
        List<token> tokens = lexer.tokeniser();
        parcer parser = new parcer(tokens);
        ProgramNode prog = parser.parseProgram();
        printAST(prog, 0);
    }

    // Simple affichage de l’arbre syntaxique
    public static void printAST(Node node, int indent) {
        String pad = "  ".repeat(indent);
        if (node instanceof ProgramNode p) {
            System.out.println(pad + "Program");
            for (Node stmt : p.statements) printAST(stmt, indent + 1);
        } else if (node instanceof IfNode i) {
            System.out.println(pad + "If");
            System.out.println(pad + "  Condition:");
            printAST(i.condition, indent + 2);
            System.out.println(pad + "  Then:");
            printAST(i.thenBlock, indent + 2);
            if (i.elseBlock != null) {
                System.out.println(pad + "  Else:");
                printAST(i.elseBlock, indent + 2);
            }
        } else if (node instanceof BlockNode b) {
            System.out.println(pad + "Block");
            for (Node stmt : b.statements) printAST(stmt, indent + 1);
        } else if (node instanceof AssignNode a) {
            System.out.println(pad + "Assign: " + a.identifier + " = " + a.value);
        } else if (node instanceof ConditionNode c) {
            System.out.println(pad + "Condition: " + c.left + " " + c.operator + " " + c.right);
        }
    }
}**/
public class parcer{
    // pour tout metre dedans (tout les noeuds de l'arbre)
    public interface noeud{}
    
    public static class programmeNoeud implements noeud{
        public final List<noeud> mots;
        private int position;
        public programmeNoeud(List<noeud> mots){
            this.mots = mots;
            this.position = 0;
        }
    }
    
    public void reinitialiser(List<lexer.token> nouveauxTokens) {
    this.tokens = nouveauxTokens;
    this.pos = 0;
    this.erreurs.clear();
}
    
    // c'est pour les noeuds de type "if","then" et "else"
    public static class noeud_si implements noeud{
        public final noeud_de_condition condition;
        public final block_de_noeuds blockAlors;
        public final block_de_noeuds block_sinon;

        public noeud_si(noeud_de_condition condition, block_de_noeuds blockAlors, block_de_noeuds block_sinon) {
            this.condition = condition;
            this.blockAlors = blockAlors;
            this.block_sinon = block_sinon;
        }
        
    }
    
    // c'est pour les noeuds de type "(tout ce qu'on peut ecrire dans un block)"
    public static class block_de_noeuds implements noeud{
        public List<noeud> mots;
        public String type;

        public block_de_noeuds(List<noeud> mots, String TypeBlock) {
            this.mots = mots;
            this.type = TypeBlock;
        }
    }
    
    // c'est pour les noeuds de type "d=3 (ou en algo : d<-3)"
    public static class noeud_d_asignation implements noeud{
        public final String identificateur;
        public final noeud valeur;

        public noeud_d_asignation(String identificateur, noeud valeur) {
            this.identificateur = identificateur;
            this.valeur = valeur;
        }
    }
    
    // c'est pour les noeuds de type "x<0"
    public static class noeud_de_condition implements noeud{
        public final String gauche;
        public final TokenType operateur;
        public final String droite;

        public noeud_de_condition(String gauche, TokenType operateur, String droite) {
            this.gauche = gauche;
            this.operateur = operateur;
            this.droite = droite;
        }
    }
    
    public static class noeud_de_valeur implements noeud{
        public final String valeur;

        public noeud_de_valeur(String valeur) {
            this.valeur = valeur;
        }
    }
    
    public static class noeud_d_operation_binaire implements noeud{
        public final TokenType operateur;
        public final noeud gauche, droite;

        public noeud_d_operation_binaire(TokenType operateur, noeud gauche, noeud droite) {
            this.operateur = operateur;
            this.gauche = gauche;
            this.droite = droite;
        }
        
    }
    
    public static class noeud_incrementation implements noeud {
        public final String identificateur;
        public final TokenType operateur; // INCREMENT ou DECREMENT

        public noeud_incrementation(String identificateur, TokenType operateur) {
            this.identificateur = identificateur;
            this.operateur = operateur;
        }
    }
    
    public class ClasseNode implements noeud {
        public final String nom;
        public final String modificateur;
        public final List<noeud> membres;

        public ClasseNode(String nom, String modificateur, List<noeud> membres) {
            this.nom = nom;
            this.modificateur = modificateur;
            this.membres = membres;
        }

        @Override
        public String toString() {
            return "Classe: " + nom + (modificateur != null ? " [" + modificateur + "]" : "");
        }
    }
    
    public class VariableNode implements noeud {
        public final String type;
        public final String nom;
        public final noeud valeur; // expression simple ou null

        public VariableNode(String type, String nom, noeud valeur) {
            this.type = type;
            this.nom = nom;
            this.valeur = valeur;
        }

        @Override
        public String toString() {
            return "Variable: " + type + " " + nom + (valeur != null ? " = " + valeur : "");
        }
    }

    
    public class MethodeNode implements noeud {
        public final String modificateur;
        public final String typeRetour;
        public final String nom;
        public final List<noeud> instructions;

        public MethodeNode(String modificateur, String typeRetour, String nom, List<noeud> instructions) {
            this.modificateur = modificateur;
            this.typeRetour = typeRetour;
            this.nom = nom;
            this.instructions = instructions;
        }

        @Override
        public String toString() {
            return "Méthode: " + (modificateur != null ? modificateur + " " : "") + typeRetour + " " + nom + "()";
        }
    }


    
    // ------------------------------------------------------------------------
    
    private List<token> tokens;
    private int pos;
    
    private int dernierTokenErreur = -1;
    public List<String> erreurs = new ArrayList<>();
    public List<String> getErreurs() {
        return erreurs;
    }


    
    public parcer(List<token> tokens){
        this.tokens = tokens;
    }
    
    public programmeNoeud parcerProgramme(){
        List<noeud> mots = new ArrayList();
        /*while(!fin()){
            mots.add(parcerMot());
        }*/
        
        while (!fin()) {
        if (verifier(TokenType.CLASS) || verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE) || verifier(TokenType.PROTECTED)) {
            mots.add(parseClasse());
        } else {
            mots.add(parcerMot());
        }
    }
        
        return new programmeNoeud(mots);
    }
    
    private noeud parseClasse() {
        System.out.println("dans parcer classe");
        
        String modificateur = null;

        // Étape 1 : modificateur optionnel
        if (verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE) || verifier(TokenType.PROTECTED)) {
            modificateur = examiner().lexeme;
            avancer();
        }

        // Étape 2 : mot-clé class
        consomer(TokenType.CLASS, "Mot-clé 'class' attendu.");

        // Étape 3 : identifiant
        token nomClasse = consomer(TokenType.IDENTIFIER, "Nom de classe attendu.");

        // Étape 4 : ouvre la classe
        consomer(TokenType.LBRACE, "'{' attendu après le nom de classe.");

        // Étape 5 : contenu
        List<noeud> membres = new ArrayList<>();
        while (!verifier(TokenType.RBRACE) && !fin()) {
            // Méthode avec modificateur
            if (verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE) || verifier(TokenType.PROTECTED)) {
                membres.add(parseMethode());
            }
            // Variable ou méthode sans modificateur
            else if (verifier(TokenType.INT) || verifier(TokenType.DOUBLE) || verifier(TokenType.CHAR)
                    || verifier(TokenType.BOOLEAN) || verifier(TokenType.VOID)) {

                // Si le prochain token est un identifiant et que le suivant est '(' → méthode
                if (examiner(1).type == TokenType.IDENTIFIER && examiner(2).type == TokenType.LPAREN) {
                    membres.add(parseMethode());
                }
                // Si le prochain token est '(' → méthode anonyme (rare)
                else if (examiner(1).type == TokenType.LPAREN) {
                    membres.add(parseMethode());
                }
                // Sinon → variable
                else {
                    membres.add(parseDeclarationVariable());
                }
            }
            // Autres instructions (if, assignation)
            else {
                noeud instruction = parcerMot();
                if (instruction != null) membres.add(instruction);
            }
        }

        // Étape 6 : ferme la classe
        consomer(TokenType.RBRACE, "'}' attendu à la fin de la classe.");

        return new ClasseNode(nomClasse.lexeme, modificateur, membres);
    }

    
    public noeud parcerMot(){
        System.out.println("dans le mot :"+examiner());
        
        if (verifier(TokenType.LBRACE) || verifier(TokenType.RBRACE)) {
            // Ignorer les accolades hors contexte
            avancer();
            return null;
        }
        
        if (correspond(TokenType.IF)){
            return parcerSi();
        }else{
            if(verifier(TokenType.IDENTIFIER)){
                return parcerAssignation();
            }
        }
        
        // Si on tombe sur un mot-clé connu d’un autre niveau, on arrête sans erreur
        // Appel de méthode ou autre instruction reconnue
        // NE PAS IGNORER les types ou void → parse les méthodes ou variables
        if (verifier(TokenType.VOID) || verifier(TokenType.INT) || verifier(TokenType.DOUBLE)
                || verifier(TokenType.CHAR) || verifier(TokenType.BOOLEAN)
                || verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE)
                || verifier(TokenType.PROTECTED)) {

            // si c’est suivi d’un identifiant et '(' → méthode
            if (examiner(1).type == TokenType.IDENTIFIER && examiner(2).type == TokenType.LPAREN) {
                return parseMethode();
            } else {
                return parseDeclarationVariable();
            }
        }
        
        erreur("Instruction attendue (if ou assignation).");
        return null;
    }
    
    public noeud_si parcerSi() {
        System.out.println("dans parcerSi");
        consomer(TokenType.LPAREN, "Attendu '(' après 'if'.");

        noeud_de_condition condition = parcerCondition();

        boolean conditionValide = true;//(/*condition != null && condition.operateur != null || */condition.droite!="ERREUR");
       System.out.println(conditionValide);
       System.out.println(examiner());
        // === Si condition invalide ===
        if (!conditionValide) {
            erreur("Condition invalide après 'if'.");
            avancerJusqua(")", "{", "else", ";");
            if (verifier(TokenType.RPAREN)) {
                System.out.println("on a avances");
                avancer();
            }
        }
        // === Si condition valide ===

        else {
            if (verifier(TokenType.RPAREN)) {
                System.out.println("on a avances2");
                avancer();
            } else {
                System.out.println(examiner());
                erreur("attendu ')' après la condition.");
                avancerJusqua("{", "else", ";");

            }
        }

        // === Bloc principal ===
        block_de_noeuds alors = null;
        if (conditionValide) {
            alors = parcerBlock();
        } else {
            // On saute le bloc sans l'analyser pour éviter des erreurs parasites
            avancerJusqua("else", ";");
        }
        System.out.println("dans le if :"+examiner());
        // === Bloc 'else' ===
        block_de_noeuds sinon = null;
        if (correspond(TokenType.ELSE)) {
            if (!verifier(TokenType.LBRACE)) {
                erreur("Attendu '{' pour commencer le bloc else.");
                avancerJusqua("{", ";");
            }
            sinon = parcerBlock();
        }
    System.out.println("dans le if :"+condition+ alors+ sinon);
        return new noeud_si(condition, alors, sinon);
    }





    
    public block_de_noeuds parcerBlock(){
        System.out.println("dans parcerBlock");
        String TypeBlock = null;
        if(TermeAvant().type== TokenType.IF){
            TypeBlock = "then";
        }else if(TermeAvant().type == TokenType.ELSE){
            TypeBlock = "else";
        }else {
            TypeBlock = "then";
        }
        consomer(TokenType.LBRACE, "Attendu '{' pour commencer le bloc.");
        List<noeud> mots = new ArrayList<>();
        System.out.println("dans le block :"+examiner());
        while (!verifier(TokenType.RBRACE) && !fin()) {
            mots.add(parcerMot());
        }
        consomer(TokenType.RBRACE, "Attendu '}' pour fermer le bloc.");
        return new block_de_noeuds(mots,TypeBlock);
    }
    
    public noeud_de_condition parcerCondition() {
        System.out.println("dans parcerCondition");
        token gauche = consomer(TokenType.IDENTIFIER, "Attendu un identificateur à gauche de la condition.");
        TokenType operateur = null;

        // si on a un opérateur relationnel
        if (verifier(TokenType.GT)) { avancer(); operateur = TokenType.GT; }
        else if (verifier(TokenType.LT)) { avancer(); operateur = TokenType.LT; }
        else if (verifier(TokenType.GTE)) { avancer(); operateur = TokenType.GTE; }
        else if (verifier(TokenType.LTE)) { avancer(); operateur = TokenType.LTE; }
        else if (verifier(TokenType.EQEQ)) { avancer(); operateur = TokenType.EQEQ; }
        else if (verifier(TokenType.NOTEQ)) { avancer(); operateur = TokenType.NOTEQ; }
        else {
            // opérateur manquant : erreur mais on garde le flux stable
            erreur("Opérateur relationnel attendu (>, <, >=, <=, ==, !=).");
            // On saute juste jusqu’à la parenthèse fermante ou au début du bloc
            avancerJusqua(")", /*"{",*/ ";");
            System.out.println("dans la condition :"+examiner());
            return new noeud_de_condition(gauche.lexeme, TokenType.EQEQ, "ERREUR");
        }

        // lire la partie droite
        token droite = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu une valeur à droite de la condition.");
        return new noeud_de_condition(gauche.lexeme, operateur, droite.lexeme);
    }

    
    public noeud_d_asignation parcerAssignation(){
        System.out.println("dans parcerAssignation");
        token identifiant = consomer(TokenType.IDENTIFIER,"attendu un identifiant pour lancer l'assignation");
        //consomer(TokenType.ASSIGN, "Attendu le signe d'assignation.");
        
        if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
            token dernier = TermeAvant();
            consomer(TokenType.SEMICOLON, "Attendu ';' après l'incrémentation ou la décrémentation.");
            return new noeud_d_asignation(
                identifiant.lexeme,
                new noeud_d_operation_binaire(
                    dernier.type == TokenType.INCREMENT ? TokenType.PLUS : TokenType.MINUS,
                    new noeud_de_valeur(identifiant.lexeme),
                    new noeud_de_valeur("1")
                )
            );
        }
        consomer(TokenType.ASSIGN, "Attendu le signe d'assignation.");
        noeud expression = parcerExpression();
        System.out.println("dans l'asignation(avant consomer) :"+examiner());
        System.out.println("dans l'expression :"+verifier(examiner(1).type,1));
        if(!verifier(examiner(1).type, 1)){
            consomer(TokenType.SEMICOLON, "Attendu un ';' pour fin d'instruction .");reculer();
        }else{
            consomer(TokenType.SEMICOLON, "Attendu un ';' pour fin d'instruction .");
        }
        System.out.println("dans l'asignation :"+examiner());
        if(examiner().type==TokenType.ELSE){
            reculer();
        }
        
        return new noeud_d_asignation(identifiant.lexeme, expression);
    }
    
    public noeud parcerExpression(){
        System.out.println("dans parcerExpression");
        
        noeud gauche = parcerTerme();
        while(correspond(TokenType.PLUS) || correspond(TokenType.MINUS)){
            token operateur = TermeAvant();
            noeud droite = parcerTerme();
            gauche = new noeud_d_operation_binaire(operateur.type, gauche, droite);
        }System.out.println("dans parcerExpression avant le retour");
        
        return gauche;
    }
    
    public noeud parcerTerme(){
        System.out.println("dans parcerTerme");
        noeud gauche = parcerFacteur();
        while(correspond(TokenType.MULT) || correspond(TokenType.DIV) || correspond(TokenType.MOD)){
            token operateur = TermeAvant();
            noeud droite = parcerFacteur();
            System.out.println("(dans terme a l'interieur du while) dans le terme :"+examiner());
            gauche = new noeud_d_operation_binaire(operateur.type, gauche, droite);
        }
        System.out.println("(dans terme avant le retour) dans le terme :"+examiner());
        return gauche;
    }
    
    public noeud parcerFacteur() {
        // Nombre
        if (correspond(TokenType.NUMBER)) {
            System.out.println("dans le facteur (devant un nombre):"+examiner());
            return new noeud_de_valeur(TermeAvant().lexeme);
        }

        // Identifiant (avec gestion du post ++/--)
        if (correspond(TokenType.IDENTIFIER)) {
            token id = TermeAvant();

            // Vérifie s’il y a un post-incrément ou post-décrément juste après
            if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
                token oper = TermeAvant();
                TokenType opType = (oper.type == TokenType.INCREMENT) ? TokenType.PLUS : TokenType.MINUS;

                // Représente : x = x + 1
                return new noeud_d_asignation(
                    id.lexeme,
                    new noeud_d_operation_binaire(
                        opType,
                        new noeud_de_valeur(id.lexeme),
                        new noeud_de_valeur("1")
                    )
                );
            }System.out.println("dans le facteur (devant l'identificateur):"+examiner());

            // Sinon, c’est juste un identifiant normal
            return new noeud_de_valeur(id.lexeme);
        }
        

    // Pré-incrément ++x / --x
    if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
        token oper = TermeAvant();
        token id = consomer(TokenType.IDENTIFIER, "Attendu un identifiant après ++/--.");
        TokenType opType = (oper.type == TokenType.INCREMENT) ? TokenType.PLUS : TokenType.MINUS;
        return new noeud_d_operation_binaire(opType, new noeud_de_valeur(id.lexeme), new noeud_de_valeur("1"));
        }
    
    System.out.println("dans le facteur (avant de savoir si c'est faux ou vrai) :"+examiner());
    
    // Parenthèses
    if (correspond(TokenType.LPAREN)) {
        noeud expression = parcerExpression();
        consomer(TokenType.RPAREN, "Attendu une ')' pour fermer l'expression .");
        return expression;
    }System.out.println("dans le facteur :"+examiner());

    erreur("Attendu un facteur arithmetique (nombre, identifiant, ou expression sous '()')");
    reculer();
    return null;
    }
    
    private noeud parseDeclarationVariable() {
        System.out.println("dans parcerDeclarationVariable");
        // Lire le type
        token typeTok = avancer(); // int, double, etc.

        // Lire le nom
        token nomTok = consomer(TokenType.IDENTIFIER, "Nom de variable attendu.");

        noeud valeur = null;

        // Vérifie s'il y a une affectation
        if (verifier(TokenType.ASSIGN)){
            avancer(); // saute "="
            valeur = parcerExpression(); // ou parseValeurSimple()
        }

        // Fin d’instruction obligatoire
        consomer(TokenType.SEMICOLON, "';' attendu à la fin de la déclaration.");

        return new VariableNode(typeTok.lexeme, nomTok.lexeme, valeur);
    }
    
    private noeud parseMethode() {
        System.out.println("dans parcerMethode");
        String modificateur = null;

        // 1) modificateur optionnel
        if (verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE) || verifier(TokenType.PROTECTED) ) {
            modificateur = examiner().lexeme;
            avancer();
        }

        // 2) type de retour
        token typeTok = avancer(); // int, double, void, etc.

        System.out.println("type de retour :"+typeTok+"   nom de la methode :"+examiner(1));
        // 3) nom de la méthode
        token nomTok;
         if (!verifier(TokenType.IDENTIFIER)){nomTok = consomer(TokenType.IDENTIFIER, "Nom de méthode attendu.");reculer();}else{nomTok = consomer(TokenType.IDENTIFIER, "Nom de méthode attendu.");}
        System.out.println("type de retour :"+typeTok+"   nom de la methode (apres consomation):"+examiner(1));
        
        // 4) parenthèses
        consomer(TokenType.LPAREN, "'(' attendu.");
        consomer(TokenType.RPAREN, "')' attendu.");

        // 5) bloc
        consomer(TokenType.LBRACE, "'{' attendu avant le corps de la méthode.");

        List<noeud> instructions = new ArrayList<>();
        while (!verifier(TokenType.RBRACE) && !fin()) {
            /*instructions.add(parcerMot());
            System.out.println("dans parcerMethode (apres instructions.add(parcerMot());)");*/
            noeud instr = parcerMot();
            if (instr != null) {
                instructions.add(instr);
                System.out.println("instruction ajoutée :" + instr);
            } else {
                // pour éviter boucle infinie si parcerMot() retourne null
                System.out.println("parcerMot() a retourné null pour :" + examiner());
                avancer();
            }
        }

        consomer(TokenType.RBRACE, "'}' attendu à la fin de la méthode.");

        return new MethodeNode(modificateur, typeTok.lexeme, nomTok.lexeme, instructions);
    }



    
    
    
    
    
    
    // ________________________________________________________________________________
    
    private boolean fin(){
        if (examiner().type == TokenType.EOF){
            return true;
        }else{
            return false;
        }
    }
    
    private token examiner(){
        return tokens.get(pos);
    }
    
    private token examiner(int i){
        System.out.println(tokens.get(pos+i));
        return tokens.get(pos+i);
    }
    
    private boolean verifier(TokenType type){
        if(!fin() && examiner().type == type){
            return true;
        }else{
            return false;
        }
    }
    
    private boolean verifier(TokenType type, int nbr){
        if(!fin() && examiner(nbr).type == type){
            return true;
        }else{
            return false;
        }
    }
    
    private boolean correspond (TokenType type){
        if(verifier(type)){
            pos++;
            return true;
        }else{
            return false;
        }
    }
    
    // pour avancer
    private token avancer(){
        return tokens.get(pos++);
    }
    
    private token reculer(){
        return tokens.get(pos--);
    }
    
    /*private void erreur (String message){
        token tokee = examiner();
        String msg ="Erreur syntaxique près de '" + tokee.lexeme + "' (pos " + tokee.position + "): " + message;
        
        // éviter de répéter la même erreur sur le même token
        if (tokee.position == dernierTokenErreur) {
            synchroniser();
            return;
        }

        dernierTokenErreur = tokee.position;
        
        erreurs.add(msg);
        
        
        
        synchroniser();
    }*/
    
    private void erreur(String message) {
    token courant = examiner();
    erreurs.add("Erreur près de '" + courant.lexeme + "' (pos " + courant.position + "): " + message);
    System.out.println("Erreur près de '" + courant.lexeme + "' (pos " + courant.position + "): " + message);
    // --- très important : consommer pour éviter boucle infinie
    avancer();
    
    // --- option : synchroniser jusqu’à un séparateur connu
    while (!fin()) {
        token t = examiner();
        if (t.lexeme.equals(";") || t.lexeme.equals("}") || t.lexeme.equals("{") || t.lexeme.equals("(") || t.lexeme.equals(")") || t.lexeme.equals("if") || t.lexeme.equals("else")) break;
        avancer();
    }
}
    
    private void avancerJusqua(String... lexemesCibles) {
    while (!fin()) {
        token t = examiner();
        for (String cible : lexemesCibles) {
            if (t.lexeme.equals(cible)) return; // on s'arrête dès qu'on atteint un token cible
        }
        avancer();
    }
}


    
    private void synchroniser(){
        
        while (!fin()){
            TokenType exam = examiner().type;
            if(exam==TokenType.RBRACE || exam==TokenType.RPAREN || exam==TokenType.SEMICOLON){
                
                break;
                
            }
            avancer();
            
        }
        // Si on est arrivé à la fin, on sort quand même
            if (fin()) pos = tokens.size();
    }
    
    private token consomer (TokenType type, String message){
        if(verifier(type)){
            System.out.println("pass");
            return tokens.get(pos++);
        }else{
            erreur(message);
            return new token(type, "ERREUR", pos);
        }
    }
    
    private token consomerAUnDes(TokenType type, TokenType type2, String message){
        if(verifier(type) || verifier(type2)){
            return tokens.get(pos++);
        }else{
            erreur(message);
            return new token(type, "ERREUR", pos);
        }
    }
    
    private token TermeAvant(){
        return tokens.get(pos-1);
    }
    
    
        // ----------------------------------------------------------------------
    // Fonction pour afficher joliment l'arbre syntaxique
    public static void afficherAST(noeud n, int indent) {
        String prefix = " ".repeat(indent);

        if (n instanceof programmeNoeud p) {
            System.out.println(prefix + "Programme:");
            for (noeud child : p.mots) {
                afficherAST(child, indent + 2);
            }
        }
        else if (n instanceof noeud_si si) {
            System.out.println(prefix + "Si:");
            System.out.println(prefix + "  Condition:");
            afficherAST(si.condition, indent + 4);
            System.out.println(prefix + "  Alors:");
            afficherAST(si.blockAlors, indent + 4);
            if (si.block_sinon != null) {
                System.out.println(prefix + "  Sinon:");
                afficherAST(si.block_sinon, indent + 4);
            }
        }
        else if (n instanceof block_de_noeuds b) {
            System.out.println(prefix + "Bloc:");
            for (noeud child : b.mots) {
                afficherAST(child, indent + 2);
            }
        }
        else if (n instanceof noeud_d_asignation a) {
            System.out.println(prefix + "Assignation: " + a.identificateur + " =");
            afficherAST(a.valeur, indent + 4);
        }
        else if (n instanceof noeud_de_condition c) {
            System.out.println(prefix + "Condition: " + c.gauche + " " + c.operateur + " " + c.droite);
        }
        else if (n instanceof noeud_de_valeur v) {
            System.out.println(prefix + "Valeur: " + v.valeur);
        }
        else if (n instanceof noeud_d_operation_binaire op) {
            System.out.println(prefix + "Operation: " + op.operateur);
            afficherAST(op.gauche, indent + 4);
            afficherAST(op.droite, indent + 4);
        }
        else {
            System.out.println(prefix + "(Type de nœud inconnu)");
        }
    }



    
    public static void main(String[] args) {
        Scanner c1 = new Scanner(System.in);
        
        // Exemple de code à analyser
        String code = "if (x > ) { y = 1 + 2 * 3; } else { y = -1; }";
        
        System.out.print("Entrer un code : ");
        code = c1.nextLine();

        // On crée d'abord le lexer pour transformer le texte en tokens
        lexer lex = new lexer(code);
        List<token> tokens = lex.tokeniser();

        // On passe les tokens au parser
        parcer parser = new parcer(tokens);
        programmeNoeud programme = parser.parcerProgramme();
        

if (!parser.getErreurs().isEmpty()) {
    System.out.println("==== ERREURS DETECTÉES ====");
    for (String e : parser.getErreurs()) {
        System.out.println(e);
    }
}

        // Afficher l’arbre syntaxique obtenu
        afficherAST(programme, 0);
    }

}

