/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_3;

import com.mycompany.projet_compil_if_else_3.lexer.TokenType;
import com.mycompany.projet_compil_if_else_3.lexer.token;
import com.mycompany.projet_compil_if_else_3.semantique.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author InfoPro
 */



public class parcer{
    boolean avecEtOu = true;
    boolean sansEtOu = false;
    boolean avecSemicolone = true;
    boolean sansSemicolone = false;
    boolean avecBlock = true;
    boolean sansBlock = false;
    boolean avecTraitementVirgule = true;
    boolean sansTraitementVirgule = false;
    boolean avecTraitementBlock = true;
    boolean sansTraitementBlock = false;
    boolean avecTraitementConstante = true;
    boolean sansTraitementConstante = false;
    int i[][] = {{1,2}};
    
    
    

    
    
    // pour tout metre dedans (tout les noeuds de l'arbre)
    /*public interface noeud{
        public Type typeInfered = null;   // rempli par l'analyseur sémantique
        public boolean erreur = false;     // vrai si ce nœud a une erreur
        public String messageErreur = "";  // description de l'erreur si erreur
    }*/
    
    public abstract class noeud {
        public Type typeInfered;          // assignable par l'analyseur
        public boolean erreur;            // assignable par l'analyseur
        public String messageErreur = ""; // assignable par l'analyseur
    }
    
    public class programmeNoeud extends noeud {
        public final NoeudPackage pkg;           // null si pas de package
        public final List<NoeudImport> imports;  // vide si pas d'import
        public  List<noeud> mots;           // classes, fonctions, variables

        private final int position;

        public programmeNoeud(NoeudPackage pkg, List<NoeudImport> imports, List<noeud> mots) {
            this.pkg = pkg;
            this.imports = imports;
            this.mots = mots;
            this.position = 0;
        }

        @Override
        public String toString() {
            return "Programme{" +
                    "package=" + pkg +
                    ", imports=" + imports +
                    ", mots=" + mots +
                    '}';
        }
        
        
    }

    
    public void reinitialiser(List<lexer.token> nouveauxTokens) {
        this.tokens = nouveauxTokens;
        this.pos = 0;
        this.erreurs.clear();
    }
    
    // c'est pour les noeuds de type "if","then" et "else"
    public  class noeud_si extends noeud{
        //public final noeud_de_condition condition;
        public final noeud condition;
        public final block_de_noeuds blockAlors;
        public final block_de_noeuds block_sinon;
        public final noeud_sinonSi block_sinonSi;

        public noeud_si(/*noeud_de_condition*/ noeud condition, block_de_noeuds blockAlors, block_de_noeuds block_sinon, noeud_sinonSi block_sinonSi) {
            this.condition = condition;
            this.blockAlors = blockAlors;
            this.block_sinon = block_sinon;
            this.block_sinonSi = block_sinonSi;
        }

        @Override
        public String toString() {
            return "noeud_si{" + "condition=" + condition + ", blockAlors=" + blockAlors + ", block_sinon=" + block_sinon + ", block_sinonSi=" + block_sinonSi + '}';
        }
        
        
        
    }
    
    // c'est pour les noeuds de type "if","then" et "else"
    public  class noeud_sinonSi extends noeud{
        //public final noeud_de_condition condition;
        public final noeud condition;
        public final block_de_noeuds blockAlors;
        public final block_de_noeuds block_sinon;
        public final noeud_sinonSi block_sinonSi;

        public noeud_sinonSi(/*noeud_de_condition*/noeud condition, block_de_noeuds blockAlors, block_de_noeuds block_sinon, noeud_sinonSi block_sinonSi) {
            this.condition = condition;
            this.blockAlors = blockAlors;
            this.block_sinon = block_sinon;
            this.block_sinonSi = block_sinonSi;
        }

        @Override
        public String toString() {
            return "noeud_sinonSi{" + "condition=" + condition + ", blockAlors=" + blockAlors + ", block_sinon=" + block_sinon + ", block_sinonSi=" + block_sinonSi + '}';
        }
        
        
        
    }
    
    // c'est pour les noeuds de type "(tout ce qu'on peut ecrire dans un block)"
    public  class block_de_noeuds extends noeud{
        public List<noeud> mots;
        public String type;

        public block_de_noeuds(List<noeud> mots, String TypeBlock) {
            this.mots = mots;
            this.type = TypeBlock;
        }
        
        
    }
    
    // c'est pour les noeuds de type "d=3 (ou en algo : d<-3)"
    public  class noeud_d_asignation extends noeud{
        public String identificateur;
        public final noeud cible;
        public final noeud valeur;

        public noeud_d_asignation(String identificateur, noeud cible, noeud valeur) {
            this.identificateur = identificateur;
            this.cible = cible;
            this.valeur = valeur;
        }
        
        
    }
    
    // c'est pour les noeuds de type "x<0"
    /*public static class noeud_de_condition implements noeud{
        public final String gauche;
        public final TokenType operateur;
        public final String droite;
        

        public noeud_de_condition(String gauche, TokenType operateur, String droite, TokenType typeEnPlus, noeud_de_condition reste) {
            this.gauche = gauche;
            this.operateur = operateur;
            if(reste==null) {
                this.droite = droite;
            }else{
                this.droite = droite +"   \""+ typeEnPlus +"\"   "+ reste.toElString();
            }
        }
        
        
        public String toElString(){
            return gauche + " " + operateur + " " + droite;
        }
    }*/
    
    public  class noeud_de_valeur extends noeud{
        public final String valeur;

        public noeud_de_valeur(String valeur) {
            this.valeur = valeur;
        }
        
        
    }
    
    public  class noeud_d_operation_binaire extends noeud{
        public final TokenType operateur;
        public final noeud gauche, droite;

        public noeud_d_operation_binaire(TokenType operateur, noeud gauche, noeud droite) {
            this.operateur = operateur;
            this.gauche = gauche;
            this.droite = droite;
        }
        
        
    }
    
    public  class noeud_incrementation extends noeud {
        public final String identificateur;
        public final TokenType operateur; // INCREMENT ou DECREMENT

        public noeud_incrementation(String identificateur, TokenType operateur) {
            this.identificateur = identificateur;
            this.operateur = operateur;
        }
        
        
    }
    
    public class ClasseNode extends noeud {
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
    
    public class VariableNode extends noeud {
        public final boolean initialisation;
        public final boolean immutable;
        public final Type type;
        public final String nom;
        //public final int dimention;
        //public final noeud taille;
        public final noeud valeur; // expression simple ou null

        public VariableNode(boolean initialisation, boolean immutable, Type type, String nom, noeud valeur) {
            this.initialisation = initialisation;
            this.immutable = immutable;
            this.type = type;
            this.nom = nom;
            this.valeur = valeur;
        }

        @Override
        public String toString() {
            return "Variable: " + type + " " + nom + (valeur != null ? " = " + valeur : "");
        }
        
        
    }
    
    public class noeud_constante extends noeud {
        public final String chose;
        //public final VariableNode valeur;
        public final noeud_declaration_multiple valeur;

        public noeud_constante(String chose, /*VariableNode*/ noeud_declaration_multiple valeur) {
            this.chose = chose;
            this.valeur = valeur;
        }

        @Override
        public String toString() {
            return "noeud_constante{" + "chose=" + chose + ", valeur=" + valeur + '}';
        }
        
        
    }

    
    public class MethodeNode extends noeud {
        public final String modificateur;
        public final String typeRetour;
        public final int dimention;
        public final String nom;
        public final List<noeud_parametre> Pretour;
        public final List<noeud> instructions;

        public MethodeNode(String modificateur, String typeRetour, int dimention, String nom, List<noeud_parametre> Pretour, List<noeud> instructions) {
            this.modificateur = modificateur;
            this.typeRetour = typeRetour;
            this.dimention = dimention;
            this.nom = nom;
            this.Pretour = Pretour;
            this.instructions = instructions;
        }

        @Override
        public String toString() {
            return "Méthode: " + (modificateur != null ? modificateur + " " : "") + typeRetour + " " + dimention + " " + nom + "()";
        }
        
        
    }
    
    public class noeud_parametre extends noeud{
        public final String type;
        public final int dimension; 
        public final String nom;

        public noeud_parametre(String type, int dimension, String nom) {
            this.type = type;
            this.dimension = dimension;
            this.nom = nom;
        }

        @Override
        public String toString() {
            return "Parametre : "+type+" "+nom;
        }
        
        
    }
    
    public class noeud_AIgnorer extends noeud{
        private final String nom;

        public noeud_AIgnorer(String nom) {
            this.nom = nom;
        }
        
        @Override
        public String toString() {
            return "bloc a ignorer : " + nom;
        }

        
    }
    
    public class noeud_while extends noeud{
        //public noeud_de_condition condition;
        public noeud condition;
        public block_de_noeuds block;

        public noeud_while(/*noeud_de_condition*/noeud condition, block_de_noeuds block) {
            this.condition = condition;
            this.block = block;
        }
        
        
    }
    
    public class noeud_do extends noeud{
        public block_de_noeuds block;
        public noeud_while condition;

        public noeud_do(block_de_noeuds block, noeud_while condition) {
            this.block = block;
            this.condition = condition;
        }

        @Override
        public String toString() {
            return "noeud_do{" + "block=" + block + ", condition=" + condition + '}';
        }
        
        
    }
    
    public class noeud_for extends noeud {
        public noeud_compteur compteur;
        public block_de_noeuds block;

        public noeud_for(noeud_compteur compteur, block_de_noeuds block) {
            this.compteur = compteur;
            this.block = block;
        }

        @Override
        public String toString() {
            return "noeud_for{" + "compteur=" + compteur + ", block=" + block + '}';
        }
        
        
    }
    
    public class noeud_compteur extends noeud {
        public Type type;
        public String variable;
        public noeud valeurDepart;
        //public noeud_de_condition limite;
        public noeud limite;
        public noeud_d_asignation pas;

        public noeud_compteur(Type type, String variable, noeud valeurDepart, /*noeud_de_condition*/noeud limite, noeud_d_asignation pas) {
            this.type = type;
            this.variable = variable;
            this.valeurDepart = valeurDepart;
            this.limite = limite;
            this.pas = pas;
        }
        
        
    }

    public class noeud_return extends noeud {
        public noeud valeur;

        public noeud_return(noeud valeur) {
            this.valeur = valeur;
        }

        @Override
        public String toString() {
            return "return " + valeur;
        }
        
        
    }
    
    public  class noeud_appel_methode extends noeud {
        public final String nom;
        public final List<noeud> arguments;

        public noeud_appel_methode(String nom, List<noeud> arguments) {
            this.nom = nom;
            this.arguments = arguments;
        }

        @Override
        public String toString() {
            return "appel " + nom + arguments;
        }
        
        
    }

    
    public class noeud_tafoukt extends noeud{
        public noeud valeur;
        
        public noeud_tafoukt(noeud valeur){
            this.valeur = valeur;
        }
        
        @Override
        public String toString() {
            return "return tafoukt";
        }
        
        
    }
    
    public class noeud_zakaria extends noeud{
        public noeud valeur;
        
        public noeud_zakaria(noeud valeur){
            this.valeur = valeur;
        }
        
        @Override
        public String toString() {
            return "zakaria = zakaria+1";
        }
        
        
    }

    
    
    //--- OVERTIME
    
    public class noeud_declaration_constructeur extends noeud {
        public final String modificateur;
        public final String nom;
        public final List<noeud_parametre> parametres;
        public final List<noeud> block;

        public noeud_declaration_constructeur(String modificateur, String nom, List<noeud_parametre> parametres, List<noeud> block) {
            this.modificateur = modificateur;
            this.nom = nom;
            this.parametres = parametres;
            this.block = block;
        }

        @Override
        public String toString() {
            return "noeud_declaration_constructeur{" + "nom=" + nom + ", parametres=" + parametres + "block=" + block +'}';
        }
        
        
    }
    
    public class noeud_constructeur extends noeud {
        public final String classe;          // Nom de la classe
        public final List<noeud> arguments; // Arguments passés au constructeur

        public noeud_constructeur(String classe, List<noeud> arguments) {
            this.classe = classe;
            this.arguments = arguments;
        }

        @Override
        public String toString() {
            return "new " + classe + "(" + arguments + ")";
        }
        
        
    }
    
    public class noeud_declaration_multiple extends noeud {
        public final List<noeud> variables;

        public noeud_declaration_multiple(List<noeud> variables) {
            this.variables = variables;
        }

        @Override
        public String toString() {
            return "Déclaration multiple : " + variables;
        }
        
        
    }
    
    public class noeud_tableau_init extends noeud {
        public final Type type;
        public final String nom;
        List<noeud> valeurs;

        public noeud_tableau_init(Type type, String nom, List<noeud> valeurs) {
            this.type = type;
            this.nom = nom;
            this.valeurs = valeurs;
        }
        
        
    }
    
    public class noeud_break extends noeud {
        @Override
        public String toString() { return "break"; }
        
        
    }

    public class noeud_continue extends noeud {
        @Override
        public String toString() { return "continue"; }
        
        
    }
    
    public class noeud_case extends noeud {
        public final noeud valeur;       // expression du case
        public final List<noeud> instructions;

        public noeud_case(noeud valeur, List<noeud> instructions) {
            this.valeur = valeur;
            this.instructions = instructions;
        }

        @Override
        public String toString() {
            return "case " + valeur + ": " + instructions;
        }
        
        
    }

    public class noeud_default extends noeud {
        public final List<noeud> instructions;

        public noeud_default(List<noeud> instructions) {
            this.instructions = instructions;
        }

        @Override
        public String toString() {
            return "default: " + instructions;
        }
        
        
    }

    public class noeud_switch extends noeud {
        public final noeud expression;         // expression à comparer
        public final List<noeud_case> cases;        // liste de noeud_case ou noeud_default
        public final noeud_default defaul;

        public noeud_switch(noeud expression, List<noeud_case> cases, noeud_default defaul) {
            this.expression = expression;
            this.cases = cases;
            this.defaul = defaul;
        }

        @Override
        public String toString() {
            return "switch(" + expression + ") {" + cases + ") {" + defaul + "}";
        }
        
        
    }
    
    public class noeud_ternaire extends noeud {
        public final noeud condition;
        public final noeud vraiExpr;
        public final noeud fauxExpr;

        public noeud_ternaire(noeud condition, noeud vraiExpr, noeud fauxExpr) {
            this.condition = condition;
            this.vraiExpr = vraiExpr;
            this.fauxExpr = fauxExpr;
        }

        @Override
        public String toString() {
            return "(" + condition + " ? " + vraiExpr + " : " + fauxExpr + ")";
        }
        
        
    }
    
    public class NoeudPackage extends noeud {
        public final List<String> nom; // ex: ["com","zaki","lang"]

        public NoeudPackage(List<String> nom) {
            this.nom = nom;
        }
        
        
    }
    
    public class NoeudImport extends noeud {
        public final List<String> chemin; // ex: ["java","util","List"]
        public final boolean wildcard;    // true si *
        public final boolean isStatic;

        public NoeudImport(List<String> chemin, boolean wildcard, boolean isStatic) {
            this.chemin = chemin;
            this.wildcard = wildcard;
            this.isStatic = isStatic;
        }
        
        
    }
    
    public class noeud_enum extends noeud {
        public final String nom;
        public final List<noeud> valeurs;

        public noeud_enum(String nom, List<noeud> valeurs) {
            this.nom = nom;
            this.valeurs = valeurs;
        }

        @Override
        public String toString() {
            return "noeud_enum{" + "valeurs=" + valeurs + '}';
        }
        
        
    }
    
    
    public class noeud_try extends noeud {
        public final noeud condTry;
        public final noeud blockTry;
        public final List<noeud_catch> catches;

        public noeud_try(noeud condTry, noeud blockTry, List<noeud_catch> catches) {
            this.condTry = condTry;
            this.blockTry = blockTry;
            this.catches = catches;
        }

        @Override
        public String toString() {
            return "noeud_try{" + "condTry=" + condTry + ", blockTry=" + blockTry + ", catches=" + catches + '}';
        }
        
        
    }
    
    public class noeud_catch extends noeud {
        public final noeud condCatch;
        public final noeud blockCatch;

        public noeud_catch(noeud condCatch, noeud blockCatch) {
            this.condCatch = condCatch;
            this.blockCatch = blockCatch;
        }

        @Override
        public String toString() {
            return "noeud_catch{" + "condCatch=" + condCatch + ", blockCatch=" + blockCatch + '}';
        }
        
        
    }
    
    public class noeud_tableau extends noeud{
        public final token operateur;
        public final List<noeud> valeurs;

        public noeud_tableau(token operateur, List<noeud> valeurs) {
            this.operateur = operateur;
            this.valeurs = valeurs;
        }

        
        
        @Override
        public String toString() {
            return "noeud_tableau{" + "operateur=" + operateur + ", valeurs=" + valeurs + '}';
        }
        
        
        
    }
    
    public class NoeudAccesTableau extends noeud {
        public final String nom;
        public final List<noeud> indices; // expressions des indices

        public NoeudAccesTableau(String nom, List<noeud> indices) {
            this.nom = nom;
            this.indices = indices;
        }

        @Override
        public String toString() {
            return "NoeudAccesTableau{" + "nom=" + nom + ", indices=" + indices + ", typeInfered=" + typeInfered + ", erreur=" + erreur + ", messageErreur=" + messageErreur + '}';
        }
        
        
    }
    
    public class NoeudCreationTableau extends noeud {
        public final String typeBase;
        public final List<noeud> tailles; // expression pour chaque dimension

        public NoeudCreationTableau(String typeBase, List<noeud> tailles) {
            this.typeBase = typeBase;
            this.tailles = tailles;
        }

        @Override
        public String toString() {
            return "NoeudCreationTableau{" + "typeBase=" + typeBase + ", tailles=" + tailles + '}';
        }
        
        
    }
    
    public class NoeudConditionBinaire extends noeud {
        noeud gauche;       // opérande gauche (variable ou constante)
        TokenType operateur; // >, <, >=, <=, ==, !=, &&, ||
        noeud droite;       // opérande droite (variable ou constante)

        public NoeudConditionBinaire(noeud gauche, TokenType operateur, noeud droite) {
            this.gauche = gauche;
            this.operateur = operateur;
            this.droite = droite;
        }

        @Override
        public String toString() {
            return "NoeudConditionBinaire{" + "gauche=" + gauche + ", operateur=" + operateur + ", droite=" + droite + '}';
        }
        
        
    }
    
    public class NoeudConditionUnaire extends noeud {
        TokenType operateur; // NOT
        noeud expr;          // la condition sur laquelle s'applique '!'

        public NoeudConditionUnaire(TokenType operateur, noeud expr) {
            this.operateur = operateur;
            this.expr = expr;
        }

        @Override
        public String toString() {
            return "NoeudConditionUnaire{" + "operateur=" + operateur + ", expr=" + expr + '}';
        }
        
        
    }
    
    public class NoeudValeurCondition extends noeud {
        String lexeme;  // nom de variable ou valeur littérale
        Type type;      // int, bool, string, etc.

        public NoeudValeurCondition(String lexeme, Type type) {
            this.lexeme = lexeme;
            this.type = type;
        }

        @Override
        public String toString() {
            return "NoeudValeurCondition{" + "lexeme=" + lexeme + ", type=" + type + '}';
        }
        
        
    }
    
    public class noeud_de_condition extends noeud{
        public noeud gaucheExpr;
        public noeud droiteExpr;
        public TokenType operateur;
        public TokenType etOu; // && ou ||
        public noeud_de_condition suite;

        public noeud_de_condition(noeud gaucheExpr, TokenType operateur,
                                  noeud droiteExpr, TokenType etOu,
                                  noeud_de_condition suite) {
            this.gaucheExpr = gaucheExpr;
            this.operateur = operateur;
            this.droiteExpr = droiteExpr;
            this.etOu = etOu;
            this.suite = suite;
        }

        @Override
        public String toString() {
            return "noeud_de_condition{" + "gaucheExpr=" + gaucheExpr + ", droiteExpr=" + droiteExpr + ", operateur=" + operateur + ", etOu=" + etOu + ", suite=" + suite + '}';
        }
        
        
    }
    
    public class NoeudAccesChamp extends noeud {
        public noeud objet;
        public String champ;

        public NoeudAccesChamp(noeud objet, String champ) {
            this.objet = objet;
            this.champ = champ;
        }

        @Override
        public String toString() {
            return "NoeudAccesChamp{" + "objet=" + objet + ", champ=" + champ + '}';
        }
        
        
    }

    public class NoeudAppelMethodeObjet extends noeud {
        public noeud objet;
        public String methode;
        public List<noeud> arguments;

        public NoeudAppelMethodeObjet(noeud objet, String methode, List<noeud> arguments) {
            this.objet = objet;
            this.methode = methode;
            this.arguments = arguments;
        }

        @Override
        public String toString() {
            return "NoeudAppelMethodeObjet{" + "objet=" + objet + ", methode=" + methode + ", arguments=" + arguments + '}';
        }
        
        
    }


    
    















    
    // ------------------------------------------------------------------------
    
    private List<token> tokens;
    private int pos;
    
    private final int dernierTokenErreur = -1;
    public List<String> erreurs = new ArrayList<>();
    public List<String> getErreurs() {
        return erreurs;
    }


    
    public parcer(List<token> tokens){
        this.tokens = tokens;
    }
    
    public programmeNoeud parcerProgramme(){
        System.out.println("dans le parcerPregrame");
        List<noeud> mots = new ArrayList();
        /*while(!fin()){
            mots.add(parcerMot());
        }*/
        
        NoeudPackage pkg = null;
        List<NoeudImport> imports = new ArrayList<>();

        if (verifier(TokenType.PACKAGE)) {
            pkg = parsePackage();
        }

        while (verifier(TokenType.IMPORT)) {
            imports.add(parseImport());
        }
        
        while (!fin()) {
        if (verifier(TokenType.CLASS) || verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE) || verifier(TokenType.PROTECTED)) {
            mots.add(parseClasse());
        } else {
            mots.add(parcerMot(""));
        }
    }
        
        return new programmeNoeud(pkg, imports, mots);
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
                if(examiner(1).lexeme.equals(nomClasse.lexeme)){
                    membres.add(parcerConstructeur());
                }else{
                    membres.add(parseMethode());
                }
            }
            // Variable ou méthode sans modificateur
            else if (verifier(TokenType.INT) || verifier(TokenType.DOUBLE) || verifier(TokenType.CHAR)
                    || verifier(TokenType.BOOLEAN) || verifier(TokenType.VOID) || verifier(TokenType.STRING) || verifier(TokenType.FLOAT) || verifier(TokenType.LONG) || verifier(TokenType.SHORT)) {

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
                    List<noeud> membre = parseDeclarationMultiple(false);
                    for(noeud v : membre){
                        membres.add(v);
                    }
                    
                }
            }else if(verifier(TokenType.ENUM)){
                membres.add(parcerEnum());
            }else if(verifier(TokenType.IDENTIFIER)&& verifier(TokenType.IDENTIFIER, 1)){
                List<noeud> membre = parseDeclarationMultiple(false);
                    for(noeud v : membre){
                        membres.add(v);
                    }
            }
            // Autres instructions (if, assignation)
            else {
                noeud instruction = parcerMot("");
                if (instruction != null) membres.add(instruction);
            }
        }

        // Étape 6 : ferme la classe
        consomer(TokenType.RBRACE, "'}' attendu à la fin de la classe.");

        return new ClasseNode(nomClasse.lexeme, modificateur, membres);
    }

    
    public noeud parcerMot(String methode_courrante){
        System.out.println("dans le mot :"+examiner());
        
        if(verifier(TokenType.LPAREN)){
            avancer();
            return null;//parcerCompteur();
        }
        
        if(verifier(TokenType.ENUM)){
            return parcerEnum();
        }
        
        if(verifier(TokenType.TRY)){
            return parcerTry(methode_courrante);
        }
        
        if(verifier(TokenType.WHILE)){
            return parcerWhile(methode_courrante, avecBlock);
        }else if(verifier(TokenType.FOR)){
            return parcerFor(methode_courrante);
        }else if(verifier(TokenType.DO)){
            return parcerDo(methode_courrante);
        }else if(verifier(TokenType.TAFOUKT)){
            return parcerTafoukt();
        }else if(verifier(TokenType.ZAKARIA)){
            return parcerZakaria();
        }else if (verifier(TokenType.RETURN)) {
            System.out.println("avant de rentrer dans parcerReturn (dans parcerMot) " + methode_courrante);
            return parcerReturn(methode_courrante);
        }
        
        if (verifier(TokenType.BREAK)) {
            avancer(); // consomme break
            consomer(TokenType.SEMICOLON, "';' attendu après break");
            return new noeud_break();
        }

        if (verifier(TokenType.CONTINUE)) {
            avancer(); // consomme continue
            consomer(TokenType.SEMICOLON, "';' attendu après continue");
            return new noeud_continue();
        }
        
        if (verifier(TokenType.SWITCH)) {
            return parcerSwitch(methode_courrante);
        }


        
        if (verifier(TokenType.LBRACE) || verifier(TokenType.RBRACE)) {
            // Ignorer les accolades hors contexte
            avancer();
            System.out.println("sorti de parcerMot :" +examiner());
            return null;
        }
        
        if (verifier(TokenType.CONST) || verifier(TokenType.FINAL)) {
            return parcerConstante();
        }
        
        if (correspond(TokenType.NEW)) {
            token classeTok = consomer(TokenType.IDENTIFIER, "Nom de classe attendu après 'new'.");
            consomer(TokenType.LPAREN, "'(' attendu après le nom de classe.");

            List<noeud> args = new ArrayList<>();
            if (!verifier(TokenType.RPAREN)) {
                while (true) {
                    args.add(parcerExpression(sansTraitementVirgule));
                    if (verifier(TokenType.COMMA)) {
                        consomer(TokenType.COMMA, "Attendu une virgule entre les arguments.");
                    } else {
                        break;
                    }
                }
            }

            consomer(TokenType.RPAREN, "')' attendu après les arguments.");
            consomer(TokenType.SEMICOLON, "Attendu ';' après l'incrémentation ou la décrémentation.");
            return new noeud_constructeur(classeTok.lexeme, args);
        }
        
        if(verifier(TokenType.THIS) && verifier(TokenType.POINT, 1)){

            noeud n = parcerExpression(avecTraitementVirgule);

            if (verifier(TokenType.INCREMENT)) {
                avancer();
                consomer(TokenType.SEMICOLON, "Attendu ';' après '++'.");
                return n;// new noeud_de_valeur(n);
            }

            if (verifier(TokenType.DECREMENT)) {
                avancer();
                consomer(TokenType.SEMICOLON, "Attendu ';' après '--'.");
                return n;// new noeud_post_decrement(n);
            }

            // assignation normale
            if (verifier(TokenType.ASSIGN)) {
                avancer();
                noeud valeur = parcerExpression(false);
                consomer(TokenType.SEMICOLON, "Attendu ';'.");
                return new noeud_d_asignation("", n, valeur);
            }

            consomer(TokenType.SEMICOLON, "Attendu ';'.");
            return n;
        }

        
        if (correspond(TokenType.IF)){
            return parcerSi(methode_courrante);
        }else{
            /*if(verifier(TokenType.IDENTIFIER) && verifier(TokenType.LPAREN, 1)){
                token meth = examiner();
                noeud n = parcerAppelMethode(meth.lexeme);
                consomer(TokenType.SEMICOLON, "Attendu ';' apres l'appelle de la methode : "+meth.lexeme+" .");
                return n;
            }else if(verifier(TokenType.IDENTIFIER) && verifier(TokenType.IDENTIFIER, 1)){
                return new noeud_declaration_multiple(parseDeclarationMultiple(avecEtOu));
            }else if (verifier(TokenType.IDENTIFIER)) {
                return parcerAssignation(avecSemicolone);
            }*/
            if(verifier(TokenType.IDENTIFIER) && verifier(TokenType.POINT, 1)){
                
                noeud n = parcerExpression(avecTraitementVirgule);

                if (verifier(TokenType.INCREMENT)) {
                    avancer();
                    consomer(TokenType.SEMICOLON, "Attendu ';' après '++'.");
                    return n;// new noeud_de_valeur(n);
                }

                if (verifier(TokenType.DECREMENT)) {
                    avancer();
                    consomer(TokenType.SEMICOLON, "Attendu ';' après '--'.");
                    return n;// new noeud_post_decrement(n);
                }

                // assignation normale
                if (verifier(TokenType.ASSIGN)) {
                    avancer();
                    noeud valeur = parcerExpression(false);
                    consomer(TokenType.SEMICOLON, "Attendu ';'.");
                    return new noeud_d_asignation("", n, valeur);
                }

                consomer(TokenType.SEMICOLON, "Attendu ';'.");
                return n;
            }
            else if(verifier(TokenType.IDENTIFIER) && verifier(TokenType.LPAREN, 1)){
                token meth = examiner();
                noeud n = parcerAppelMethode(meth.lexeme);
                consomer(TokenType.SEMICOLON, "Attendu ';' apres l'appelle de la methode : "+meth.lexeme+" .");
                return n;
            }else if(verifier(TokenType.IDENTIFIER) && verifier(TokenType.IDENTIFIER, 1)){
                return new noeud_declaration_multiple(parseDeclarationMultiple(avecEtOu));
            }else if (verifier(TokenType.IDENTIFIER)) {
                return parcerAssignation(avecSemicolone);
            }
        }
        
        // Si on tombe sur un mot-clé connu d’un autre niveau, on arrête sans erreur
        // Appel de méthode ou autre instruction reconnue
        // NE PAS IGNORER les types ou void → parse les méthodes ou variables
        if (verifier(TokenType.VOID) || verifier(TokenType.INT) || verifier(TokenType.DOUBLE)
                || verifier(TokenType.CHAR) || verifier(TokenType.BOOLEAN) || verifier(TokenType.STRING) || verifier(TokenType.FLOAT) || verifier(TokenType.LONG) || verifier(TokenType.SHORT)
                || verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE)
                || verifier(TokenType.PROTECTED)) {

            // si c’est suivi d’un identifiant et '(' → méthode
            if (examiner(1).type == TokenType.IDENTIFIER && examiner(2).type == TokenType.LPAREN) {
                return parseMethode();
            } else if(verifier(TokenType.IDENTIFIER) && verifier(TokenType.IDENTIFIER, 1)){
                return new noeud_declaration_multiple((List<noeud>)parseDeclarationMultiple(sansTraitementConstante));
            }else {
                //return parseDeclarationVariable();
                return new noeud_declaration_multiple((List<noeud>)parseDeclarationMultiple(sansTraitementConstante));
            }
        }
        
        erreur("Instruction attendue (if ou assignation).");
        return null;
    }
    
    public noeud_si parcerSi(String methode_courrante) {
        
        
        System.out.println("dans parcerSi" + methode_courrante);
        consomer(TokenType.LPAREN, "Attendu '(' après 'if'.");

        //noeud_de_condition condition = parcerCondition(/*avecEtOu*/);
        noeud condition = parcerCondition(/*avecEtOu*/);

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
            
            alors = parcerBlock(methode_courrante, avecTraitementBlock);
        } else {
            // On saute le bloc sans l'analyser pour éviter des erreurs parasites
            avancerJusqua("else", ";");
        }
        System.out.println("dans le if :"+examiner());
        // === Bloc 'else' ===
        block_de_noeuds sinon = null;
        noeud_sinonSi sinonsi = null;
        if (correspond(TokenType.ELSE)) {
            if (!verifier(TokenType.LBRACE)) {
                erreur("Attendu '{' pour commencer le bloc else.");
                avancerJusqua("{", ";");
            }
            sinon = parcerBlock(methode_courrante, avecTraitementBlock);
        }else if (correspond(TokenType.ELSEIF)) {
            
            sinonsi = parcerSinonSi(methode_courrante);
        }
    System.out.println("dans le if :"+condition+ alors+ sinon);
        return new noeud_si(condition, alors, sinon, sinonsi);
    }
    
    public noeud_sinonSi parcerSinonSi(String methode_courrante) {
        System.out.println("dans parcerSi");
        consomer(TokenType.LPAREN, "Attendu '(' après 'if'.");

        //noeud_de_condition condition = parcerCondition(/*avecEtOu*/);
        noeud condition = parcerCondition(/*avecEtOu*/);

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
            alors = parcerBlock(methode_courrante, avecTraitementBlock);
        } else {
            // On saute le bloc sans l'analyser pour éviter des erreurs parasites
            avancerJusqua("else", ";");
        }
        System.out.println("dans le if :"+examiner());
        // === Bloc 'else' ===
        block_de_noeuds sinon = null;
        noeud_sinonSi sinonsi = null;
        if (correspond(TokenType.ELSE)) {
            if (!verifier(TokenType.LBRACE)) {
                erreur("Attendu '{' pour commencer le bloc else.");
                avancerJusqua("{", ";");
            }
            sinon = parcerBlock(methode_courrante, avecTraitementBlock);
        }else if (correspond(TokenType.ELSEIF)) {
            
            sinonsi = parcerSinonSi(methode_courrante);
        }
    System.out.println("dans le if :"+condition+ alors+ sinon);
        return new noeud_sinonSi(condition, alors, sinon, sinonsi);
    }





    
    public block_de_noeuds parcerBlock(String methode_courrante, boolean blockS){
        System.out.println("dans parcerBlock");
        String TypeBlock = null;
        if(TermeAvant().type== TokenType.IF){
            TypeBlock = "then";
        }else if(TermeAvant().type == TokenType.ELSE){
            TypeBlock = "else";
        }else {
            TypeBlock = "then";
        }
        if(blockS){
            System.out.println("dans parcerBlock (avant consomer le premier '{')" + examiner());
            consomer(TokenType.LBRACE, "Attendu '{' pour commencer le bloc.");
        }
        
        List<noeud> mots = new ArrayList<>();
        System.out.println("dans le block :"+examiner());
        if(blockS){
            while (!verifier(TokenType.RBRACE) && !fin()) {
                mots.add(parcerMot(methode_courrante));
            }
        }else{
            while (!verifier(TokenType.RPAREN) && !fin()) {
                mots.add(parcerMot(methode_courrante));
            }
        }
        
        if(blockS){
            consomer(TokenType.RBRACE, "Attendu '}' pour fermer le bloc.");
        }
        
        return new block_de_noeuds(mots,TypeBlock);
    }
    
    /*public noeud_de_condition parcerCondition(boolean etOu) {
        
        
        System.out.println("dans parcerCondition");
        boolean tr= false;
        if(verifier(TokenType.LPAREN)){
            tr = true;
            consomer(TokenType.LPAREN, "Attendu une parenthese ouvrante '('.");
        }
        
        token gauche =null;
        TokenType operateur = null;
        token droite=null;
        
        //le cas du non 
        if(verifier(TokenType.NOT)){
            operateur = consomer(TokenType.NOT, "Attendu '!' .").type;
            
            if(verifier(TokenType.STRINGVAL)){
                droite = consomer(TokenType.STRINGVAL,"Attendu un stringval '\" valeur \"'.");
            }else if(verifier(TokenType.CHARVAL)){
                droite = consomer(TokenType.CHARVAL,"Attendu un stringval '\' valeur \''.");
            }else if(verifier(TokenType.TRUE)){
                droite = consomer(TokenType.TRUE,"Attendu un stringval '\' valeur \''.");
            }else if(verifier(TokenType.FALSE)){
                droite = consomer(TokenType.FALSE,"Attendu un stringval '\' valeur \''.");
            }else if(verifier(TokenType.NULL)){
                droite = consomer(TokenType.NULL,"Attendu un stringval '\' valeur \''.");
            }else{
                droite = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu une valeur à droite de la condition.");
            }
            
            if (tr){
                consomer(TokenType.RPAREN, "Attendu une parenthese fermante ')'.");
            }
            noeud_de_condition reste=null;
            TokenType typeEnPlus = null;
            
            if (verifier(TokenType.GT)) { avancer(); typeEnPlus = TokenType.GT; }
            else if (verifier(TokenType.LT)) { avancer(); typeEnPlus = TokenType.LT; }
            else if (verifier(TokenType.GTE)) { avancer(); typeEnPlus = TokenType.GTE; }
            else if (verifier(TokenType.LTE)) { avancer(); typeEnPlus = TokenType.LTE; }
            else if (verifier(TokenType.EQEQ)) { avancer(); typeEnPlus = TokenType.EQEQ; }
            else if (verifier(TokenType.NOTEQ)) { avancer(); typeEnPlus = TokenType.NOTEQ; }
            /*else {
                // opérateur manquant : erreur mais on garde le flux stable
                erreur("Opérateur relationnel attendu (>, <, >=, <=, ==, !=).");
                // On saute juste jusqu’à la parenthèse fermante ou au début du bloc
                avancerJusqua(")", /*"{",* ";");
                System.out.println("dans la condition :"+examiner());
                return new noeud_de_condition(gauche.lexeme, TokenType.EQEQ, "ERREUR",null,null);
            }*
            if(verifier(TokenType.STRINGVAL)){
                droite = consomer(TokenType.STRINGVAL,"Attendu un stringval '\" valeur \"'.");
            }else if(verifier(TokenType.CHARVAL)){
                droite = consomer(TokenType.CHARVAL,"Attendu un stringval '\' valeur \''.");
            }else if(verifier(TokenType.TRUE)){
                droite = consomer(TokenType.TRUE,"Attendu un stringval '\' valeur \''.");
            }else if(verifier(TokenType.FALSE)){
                droite = consomer(TokenType.FALSE,"Attendu un stringval '\' valeur \''.");
            }else if(verifier(TokenType.NULL)){
                droite = consomer(TokenType.NULL,"Attendu un stringval '\' valeur \''.");
            }else{
                droite = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu une valeur à droite de la condition.");
            }
            if(etOu){
                if(verifier(TokenType.AND)){
                    typeEnPlus = TokenType.AND;
                    consomer(TokenType.AND, "Attendu une condition ET '&&'.");
                    reste = parcerCondition(avecEtOu);
                }else if (verifier(TokenType.OR)){
                    typeEnPlus = TokenType.OR;
                    consomer(TokenType.OR, "Attendu une condition OU '||'.");
                    reste = parcerCondition(avecEtOu);
                }
            }






            return new noeud_de_condition(null, operateur, droite.lexeme,typeEnPlus,reste);
            
        }
        
        if(verifier(TokenType.STRINGVAL)){
            gauche = consomer(TokenType.STRINGVAL,"Attendu un stringval '\" valeur \"'.");
        }else if(verifier(TokenType.CHARVAL)){
            gauche = consomer(TokenType.CHARVAL,"Attendu un stringval '\' valeur \''.");
        }else if(verifier(TokenType.TRUE)){
            gauche = consomer(TokenType.TRUE,"Attendu un stringval '\' valeur \''.");
        }else if(verifier(TokenType.FALSE)){
            gauche = consomer(TokenType.FALSE,"Attendu un stringval '\' valeur \''.");
        }else if(verifier(TokenType.NULL)){
            gauche = consomer(TokenType.NULL,"Attendu un stringval '\' valeur \''.");
        }else{
            gauche = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu une valeur à droite de la condition.");
        }
        //token gauche = consomer(TokenType.IDENTIFIER, "Attendu un identificateur à gauche de la condition.");
        

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
            avancerJusqua(")", /*"{",* ";");
            System.out.println("dans la condition :"+examiner());
            return new noeud_de_condition(gauche.lexeme, TokenType.EQEQ, "ERREUR",null,null);
        }

        // lire la partie droite
        
        if(verifier(TokenType.STRINGVAL)){
            droite = consomer(TokenType.STRINGVAL,"Attendu un stringval '\" valeur \"'.");
        }else if(verifier(TokenType.CHARVAL)){
            droite = consomer(TokenType.CHARVAL,"Attendu un stringval '\' valeur \''.");
        }else if(verifier(TokenType.TRUE)){
            droite = consomer(TokenType.TRUE,"Attendu un stringval '\' valeur \''.");
        }else if(verifier(TokenType.FALSE)){
            droite = consomer(TokenType.FALSE,"Attendu un stringval '\' valeur \''.");
        }else if(verifier(TokenType.NULL)){
            droite = consomer(TokenType.NULL,"Attendu un stringval '\' valeur \''.");
        }else{
            droite = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu une valeur à droite de la condition.");
        }
        
        if (tr){
            consomer(TokenType.RPAREN, "Attendu une parenthese fermante ')'.");
        }
        noeud_de_condition reste=null;
        TokenType typeEnPlus = null;
        if(etOu){
            if(verifier(TokenType.AND)){
                typeEnPlus = TokenType.AND;
                consomer(TokenType.AND, "Attendu une condition ET '&&'.");
                reste = parcerCondition(avecEtOu);
            }else if (verifier(TokenType.OR)){
                typeEnPlus = TokenType.OR;
                consomer(TokenType.OR, "Attendu une condition OU '||'.");
                reste = parcerCondition(avecEtOu);
            }
        }
        
        
        
        
        
        
        return new noeud_de_condition(gauche.lexeme, operateur, droite.lexeme,typeEnPlus,reste);
    }*/

    /*public noeud_de_condition parcerCondition(boolean etOu) {
        System.out.println("dans parcerCondition");

        // 1️⃣ Gestion du '!' unaire
        if (verifier(TokenType.NOT)) {
            TokenType operateur = consomer(TokenType.NOT, "Attendu '!'").type;
            noeud_de_condition expr = parcerCondition(false); // récursion pour expr après '!'
            return new noeud_de_condition(null, operateur, expr.toElString(), null, null);
        }

        // 2️⃣ Parenthèses
        boolean paren = false;
        if (verifier(TokenType.LPAREN)) {
            paren = true;
            consomer(TokenType.LPAREN, "Attendu '('");
        }

        // 3️⃣ Partie gauche : valeurs simples
        token gauche = null;
        if(verifier(TokenType.STRINGVAL)) gauche = consomer(TokenType.STRINGVAL, "Attendu un string");
        else if(verifier(TokenType.CHARVAL)) gauche = consomer(TokenType.CHARVAL, "Attendu un char");
        else if(verifier(TokenType.TRUE)) gauche = consomer(TokenType.TRUE, "Attendu 'true'");
        else if(verifier(TokenType.FALSE)) gauche = consomer(TokenType.FALSE, "Attendu 'false'");
        else if(verifier(TokenType.NULL)) gauche = consomer(TokenType.NULL, "Attendu 'null'");
        else gauche = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu une valeur");

        // 4️⃣ Opérateur relationnel
        TokenType operateur = null;
        if (verifier(TokenType.GT)) operateur = consomer(TokenType.GT, "Attendu '>'").type;
        else if (verifier(TokenType.LT)) operateur = consomer(TokenType.LT, "Attendu '<'").type;
        else if (verifier(TokenType.GTE)) operateur = consomer(TokenType.GTE, "Attendu '>='").type;
        else if (verifier(TokenType.LTE)) operateur = consomer(TokenType.LTE, "Attendu '<='").type;
        else if (verifier(TokenType.EQEQ)) operateur = consomer(TokenType.EQEQ, "Attendu '=='").type;
        else if (verifier(TokenType.NOTEQ)) operateur = consomer(TokenType.NOTEQ, "Attendu '!='").type;

        // 5️⃣ Partie droite
        token droite = null;
        if (operateur != null) {
            if(verifier(TokenType.STRINGVAL)) droite = consomer(TokenType.STRINGVAL, "Attendu un string");
            else if(verifier(TokenType.CHARVAL)) droite = consomer(TokenType.CHARVAL, "Attendu un char");
            else if(verifier(TokenType.TRUE)) droite = consomer(TokenType.TRUE, "Attendu 'true'");
            else if(verifier(TokenType.FALSE)) droite = consomer(TokenType.FALSE, "Attendu 'false'");
            else if(verifier(TokenType.NULL)) droite = consomer(TokenType.NULL, "Attendu 'null'");
            else droite = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu une valeur à droite");
        }

        // 6️⃣ Fermeture parenthèse
        if (paren) consomer(TokenType.RPAREN, "Attendu ')'");

        // 7️⃣ Gestion des opérateurs logiques && et ||
        noeud_de_condition reste = null;
        TokenType typeEnPlus = null;
        if (etOu) {
            if (verifier(TokenType.AND)) {
                typeEnPlus = consomer(TokenType.AND, "Attendu '&&'").type;
                reste = parcerCondition(etOu);
            } else if (verifier(TokenType.OR)) {
                typeEnPlus = consomer(TokenType.OR, "Attendu '||'").type;
                reste = parcerCondition(etOu);
            }
        }

        return new noeud_de_condition(gauche != null ? gauche.lexeme : null, operateur,
                                      droite != null ? droite.lexeme : null, typeEnPlus, reste);
    }*/
    
    public noeud_de_condition parcerCondition() {
        return parcerOrExpr();
    }

    // --- OR ---
    private noeud_de_condition parcerOrExpr() {
        noeud_de_condition gauche = parcerAndExpr();
        while (verifier(TokenType.OR)) {
            TokenType op = consomer(TokenType.OR, "Attendu '||'").type;
            noeud_de_condition droite = parcerAndExpr();
            //gauche = new noeud_de_condition(null, op, null, null, new noeud_de_condition(gauche, op, null, null, droite));
            gauche = new noeud_de_condition(gauche, op, droite, null, null);
        }
        return gauche;
    }

    // --- AND ---
    private noeud_de_condition parcerAndExpr() {
        noeud_de_condition gauche = parcerRelExpr();
        while (verifier(TokenType.AND)) {
            TokenType op = consomer(TokenType.AND, "Attendu '&&'").type;
            noeud_de_condition droite = parcerRelExpr();
            //gauche = new noeud_de_condition(null, op, null, null, new noeud_de_condition(gauche, op, null, null, droite));
            gauche = new noeud_de_condition(gauche, op, droite, null, null);
        }
        return gauche;
    }

    // --- Relation / NOT / Parenthèses ---
    /*private noeud_de_condition parcerRelExpr() {
        if (verifier(TokenType.NOT)) {
            TokenType op = consomer(TokenType.NOT, "Attendu '!'").type;
            noeud_de_condition expr = parcerRelExpr();
            return new noeud_de_condition(null, op, expr.toElString(), null, null);
        }

        if (verifier(TokenType.LPAREN)) {
            consomer(TokenType.LPAREN, "Attendu '('");
            noeud_de_condition expr = parcerCondition();
            consomer(TokenType.RPAREN, "Attendu ')'");
            return expr;
        }

        // Valeur de gauche
        token gauche = lireValeur();
/
        // Opérateur relationnel
        TokenType operateur = null;
        if (verifier(TokenType.GT)) operateur = consomer(TokenType.GT, "Attendu '>'").type;
        else if (verifier(TokenType.LT)) operateur = consomer(TokenType.LT, "Attendu '<'").type;
        else if (verifier(TokenType.GTE)) operateur = consomer(TokenType.GTE, "Attendu '>='").type;
        else if (verifier(TokenType.LTE)) operateur = consomer(TokenType.LTE, "Attendu '<='").type;
        else if (verifier(TokenType.EQEQ)) operateur = consomer(TokenType.EQEQ, "Attendu '=='").type;
        else if (verifier(TokenType.NOTEQ)) operateur = consomer(TokenType.NOTEQ, "Attendu '!='").type;

        // Valeur de droite si opérateur
        token droite = null;
        if (operateur != null) droite = lireValeur();

        return new noeud_de_condition(gauche.lexeme, operateur,
                                      droite != null ? droite.lexeme : null,
                                      null, null);
    }*/
    private noeud_de_condition parcerRelExpr() {
        // Cas NOT
        if (verifier(TokenType.NOT)) {
            TokenType op = consomer(TokenType.NOT, "Attendu '!'").type;
            noeud_de_condition expr = parcerRelExpr();
            return new noeud_de_condition(null, op, expr, null, null);
        }

        // Parenthèses
        if (verifier(TokenType.LPAREN)) {
            consomer(TokenType.LPAREN, "Attendu '('");
            noeud_de_condition expr = parcerCondition();
            consomer(TokenType.RPAREN, "Attendu ')'");
            return expr;
        }

        // Expression à gauche
        noeud gaucheExpr = parcerExpression(true);

        // Vérifier opérateur relationnel
        TokenType operateur = null;
        if (verifier(TokenType.GT)) operateur = consomer(TokenType.GT, "Attendu '>'").type;
        else if (verifier(TokenType.LT)) operateur = consomer(TokenType.LT, "Attendu '<'").type;
        else if (verifier(TokenType.GTE)) operateur = consomer(TokenType.GTE, "Attendu '>='").type;
        else if (verifier(TokenType.LTE)) operateur = consomer(TokenType.LTE, "Attendu '<='").type;
        else if (verifier(TokenType.EQEQ)) operateur = consomer(TokenType.EQEQ, "Attendu '=='").type;
        else if (verifier(TokenType.NOTEQ)) operateur = consomer(TokenType.NOTEQ, "Attendu '!='").type;

        // Expression à droite si opérateur
        noeud droiteExpr = null;
        if (operateur != null) droiteExpr = parcerExpression(true);
        
        if (operateur == null) {
            // pas une relation, juste une expression booléenne
            
            return new noeud_de_condition(gaucheExpr, TokenType.ZAKARIA, null, null, null);
        }

        return new noeud_de_condition(gaucheExpr, operateur, droiteExpr, null, null);
    }


    // --- Lire une valeur ---
    private token lireValeur() {
        if (verifier(TokenType.NUMBER)) return consomer(TokenType.NUMBER, "Attendu un nombre");
        if (verifier(TokenType.IDENTIFIER)) return consomer(TokenType.IDENTIFIER, "Attendu un identificateur");
        if (verifier(TokenType.STRINGVAL)) return consomer(TokenType.STRINGVAL, "Attendu une chaîne");
        if (verifier(TokenType.CHARVAL)) return consomer(TokenType.CHARVAL, "Attendu un caractère");
        if (verifier(TokenType.TRUE)) return consomer(TokenType.TRUE, "Attendu 'true'");
        if (verifier(TokenType.FALSE)) return consomer(TokenType.FALSE, "Attendu 'false'");
        if (verifier(TokenType.NULL)) return consomer(TokenType.NULL, "Attendu 'null'");

        // erreur si aucune valeur
        erreur("Attendu une valeur (identificateur, nombre, string, char, true, false, null)");
        return new token(null, "ERREUR", 0);
    }
    
    /*public noeud parcerCondition(boolean etOu) {
        // Vérifie si la condition est entourée de parenthèses
        boolean parenthese = false;
        if (verifier(TokenType.LPAREN)) {
            parenthese = true;
            consomer(TokenType.LPAREN, "Attendu '(' au début de la condition.");
        }

        noeud condition = null;

        // --- Cas du NOT ---
        if (verifier(TokenType.NOT)) {
            TokenType op = consomer(TokenType.NOT, "Attendu '!'").type;
            noeud expr = parcerCondition(false); // NOT n'a pas de ET/OU ici
            condition = new NoeudConditionUnaire(op, expr);
        } else {
            // --- Cas opérandes gauche ---
            noeud gauche = null;
            if (verifier(TokenType.TRUE)) {
                gauche = new NoeudValeurCondition(consomer(TokenType.TRUE, "Attendu 'true'").lexeme, new Type("BOOLEAN", 0));
            } else if (verifier(TokenType.FALSE)) {
                gauche = new NoeudValeurCondition(consomer(TokenType.FALSE, "Attendu 'false'").lexeme, new Type("BOOLEAN", 0));
            } else if (verifier(TokenType.NULL)) {
                gauche = new NoeudValeurCondition(consomer(TokenType.NULL, "Attendu 'null'").lexeme, new Type("NULL", 0));
            } else if (verifier(TokenType.STRINGVAL)) {
                gauche = new NoeudValeurCondition(consomer(TokenType.STRINGVAL, "Attendu une chaîne").lexeme, new Type("STRING", 0));
            } else if (verifier(TokenType.CHARVAL)) {
                gauche = new NoeudValeurCondition(consomer(TokenType.CHARVAL, "Attendu un char").lexeme, new Type("CHAR",0));
            } else {
                gauche = new NoeudValeurCondition(consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, 
                            "Attendu variable ou nombre").lexeme, new Type("INCONNU", 0));
            }

            // --- Opérateur relationnel ---
            TokenType operateur = null;
            if (verifier(TokenType.GT)) { operateur = consomer(TokenType.GT, "Attendu '>'").type; }
            else if (verifier(TokenType.LT)) { operateur = consomer(TokenType.LT, "Attendu '<'").type; }
            else if (verifier(TokenType.GTE)) { operateur = consomer(TokenType.GTE, "Attendu '>='").type; }
            else if (verifier(TokenType.LTE)) { operateur = consomer(TokenType.LTE, "Attendu '<='").type; }
            else if (verifier(TokenType.EQEQ)) { operateur = consomer(TokenType.EQEQ, "Attendu '=='").type; }
            else if (verifier(TokenType.NOTEQ)) { operateur = consomer(TokenType.NOTEQ, "Attendu '!='").type; }
            else { 
                erreur("Opérateur relationnel attendu."); 
                // Pour garder le flux stable, on retourne une erreur
                return new NoeudConditionBinaire(gauche, TokenType.EQEQ, new NoeudValeurCondition("ERREUR", new Type("ERREUR", 0)));
            }

            // --- Opérande droite ---
            noeud droite = null;
            if (verifier(TokenType.TRUE)) {
                droite = new NoeudValeurCondition(consomer(TokenType.TRUE, "Attendu 'true'").lexeme, new Type("BOOLEAN", 0));
            } else if (verifier(TokenType.FALSE)) {
                droite = new NoeudValeurCondition(consomer(TokenType.FALSE, "Attendu 'false'").lexeme, new Type("BOOLEAN", 0));
            } else if (verifier(TokenType.NULL)) {
                droite = new NoeudValeurCondition(consomer(TokenType.NULL, "Attendu 'null'").lexeme, new Type("NULL", 0));
            } else if (verifier(TokenType.STRINGVAL)) {
                droite = new NoeudValeurCondition(consomer(TokenType.STRINGVAL, "Attendu une chaîne").lexeme, new Type("STRING", 0));
            } else if (verifier(TokenType.CHARVAL)) {
                droite = new NoeudValeurCondition(consomer(TokenType.CHARVAL, "Attendu un char").lexeme, new Type("CHAR",0));
            } else {
                droite = new NoeudValeurCondition(consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, 
                            "Attendu variable ou nombre").lexeme, new Type("INCONNU", 0));
            }

            condition = new NoeudConditionBinaire(gauche, operateur, droite);

            // --- Gestion du AND / OR ---
            if (etOu) {
                noeud reste = null;
                TokenType typeEnPlus = null;
                if (verifier(TokenType.AND)) {
                    typeEnPlus = consomer(TokenType.AND, "Attendu '&&'").type;
                    reste = parcerCondition(true);
                } else if (verifier(TokenType.OR)) {
                    typeEnPlus = consomer(TokenType.OR, "Attendu '||'").type;
                    reste = parcerCondition(true);
                }
                if (reste != null) {
                    // on enveloppe la condition dans un nouveau binaire AND/OR
                    condition = new NoeudConditionBinaire(condition, typeEnPlus, reste);
                }
            }
        }

        if (parenthese) {
            consomer(TokenType.RPAREN, "Attendu ')' à la fin de la condition.");
        }

        return condition;
    }*/




    
    /*public noeud_d_asignation parcerAssignation(boolean semicolon){
        System.out.println("dans parcerAssignation");
        token identifiant = consomer(TokenType.IDENTIFIER,"attendu un identifiant pour lancer l'assignation");
        //consomer(TokenType.ASSIGN, "Attendu le signe d'assignation.");
        
        if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
            token dernier = TermeAvant();
            if(semicolon){
                consomer(TokenType.SEMICOLON, "Attendu ';' après l'incrémentation ou la décrémentation.");
            }System.out.println("sorti de parcerAssignation :" +examiner());
            return new noeud_d_asignation(
                identifiant.lexeme,
                null,
                new noeud_d_operation_binaire(
                    dernier.type == TokenType.INCREMENT ? TokenType.PLUS : TokenType.MINUS,
                    new noeud_de_valeur(identifiant.lexeme),
                    new noeud_de_valeur("1")
                )
            );
        
        }
        
        
        List<noeud> indices = new ArrayList<>();
        while (verifier(TokenType.LBRACKET)) {
            consomer(TokenType.LBRACKET, "[ attendu");
            indices.add(parcerExpression(avecTraitementVirgule));
            consomer(TokenType.RBRACKET, "] attendu");
        }
        
        noeud cible=null;
        if (!indices.isEmpty()) {
            cible = new NoeudAccesTableau(identifiant.lexeme, indices);
        } else {
            //cible = new noeud_de_valeur(identifiant.lexeme);
        }

        
        
        
        consomer(TokenType.ASSIGN, "Attendu le signe d'assignation.");
        noeud expression = null;
        if (verifier(TokenType.NEW)) {
            avancer(); // consommer 'new'
            token typeTok = avancer();//consomer(TokenType.IDENTIFIER, "Type attendu après 'new'");
            List<noeud> tailles = new ArrayList<>();
            while (verifier(TokenType.LBRACKET)) {
                consomer(TokenType.LBRACKET, "[ attendu");
                tailles.add(parcerExpression(avecTraitementVirgule));
                consomer(TokenType.RBRACKET, "] attendu");
            }
            expression = new NoeudCreationTableau(typeTok.lexeme, tailles);
        }else{
            expression = parcerExpression(sansTraitementVirgule);
        }
        
        //noeud 
        System.out.println("dans l'asignation(avant consomer) :"+examiner());
        System.out.println("dans l'expression :"+verifier(examiner(1).type,1));
        if(semicolon){
            if(!verifier(examiner(1).type, 1)){
                consomer(TokenType.SEMICOLON, "Attendu un ';' pour fin d'instruction .");reculer();
            }else{
                consomer(TokenType.SEMICOLON, "Attendu un ';' pour fin d'instruction .");
            }
        }
        System.out.println("dans l'asignation :"+examiner());
        if(examiner().type==TokenType.ELSE){
            reculer();
        }
        System.out.println("sorti de parcerAssignation :" +examiner());
        return new noeud_d_asignation(identifiant.lexeme, cible, expression);
    }*/
    
    public noeud_d_asignation parcerAssignationCompteur(boolean semicolon){
        System.out.println("dans parcerAssignation  : " +examiner());

        // 1) parser le côté gauche comme une expression
        noeud cible = new noeud_de_valeur(avancer().lexeme);   // peut être d, d[i], d.fld, d.a.b[i], etc.

        // 2) gérer ++ et --
        if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
            token op = TermeAvant();
            if(semicolon) consomer(TokenType.SEMICOLON, "attendu ';' .");

            return new noeud_d_asignation(
                "",
                cible,
                new noeud_d_operation_binaire(
                    op.type == TokenType.INCREMENT ? TokenType.PLUS : TokenType.MINUS,
                    cible,
                    new noeud_de_valeur("1")
                )
            );
        }

        // 3) =
        consomer(TokenType.ASSIGN, "Attendu '='");

        // 4) expression droite
        noeud valeur;
        if (verifier(TokenType.NEW)) {
            avancer();
            token typeTok = consomer(TokenType.IDENTIFIER, "Type attendu");
            List<noeud> tailles = new ArrayList<>();
            while (verifier(TokenType.LBRACKET)) {
                consomer(TokenType.LBRACKET, "Attendu '[' .");
                tailles.add(parcerExpression(false));
                consomer(TokenType.RBRACKET, "Attendu ']' .");
            }
            valeur = new NoeudCreationTableau(typeTok.lexeme, tailles);
        } else {
            valeur = parcerExpression(false);
        }

        if (semicolon) consomer(TokenType.SEMICOLON, "Attendu ';' .");

        return new noeud_d_asignation("",cible, valeur);
    }
    
    public noeud_d_asignation parcerAssignation(boolean semicolon){
        System.out.println("dans parcerAssignation  : " +examiner());

        // 1) parser le côté gauche comme une expression
        
        noeud cible = parcerFacteur(false);   // peut être d, d[i], d.fld, d.a.b[i], etc.
        System.out.println("dans parcerAssignation apres la cible :" + examiner() + "     " + cible);
        if(verifier(TokenType.SEMICOLON) && cible instanceof noeud_d_asignation assi){
            if(semicolon) consomer(TokenType.SEMICOLON, "attendu ';' .");
            return assi;
        }

        // 2) gérer ++ et --
        if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
            token op = TermeAvant();
            if(semicolon) consomer(TokenType.SEMICOLON, "attendu ';' .");

            return new noeud_d_asignation(
                "",
                cible,
                new noeud_d_operation_binaire(
                    op.type == TokenType.INCREMENT ? TokenType.PLUS : TokenType.MINUS,
                    cible,
                    new noeud_de_valeur("1")
                )
            );
        }

        // 3) =
        consomer(TokenType.ASSIGN, "Attendu '='");

        // 4) expression droite
        noeud valeur;
        if (verifier(TokenType.NEW)) {
            avancer();
            token typeTok = consomer(TokenType.IDENTIFIER, "Type attendu");
            List<noeud> tailles = new ArrayList<>();
            while (verifier(TokenType.LBRACKET)) {
                consomer(TokenType.LBRACKET, "Attendu '[' .");
                tailles.add(parcerExpression(false));
                consomer(TokenType.RBRACKET, "Attendu ']' .");
            }
            valeur = new NoeudCreationTableau(typeTok.lexeme, tailles);
        } else {
            valeur = parcerExpression(false);
        }

        if (semicolon) consomer(TokenType.SEMICOLON, "Attendu ';' .");

        return new noeud_d_asignation("",cible, valeur);
    }

    
    public noeud parcerExpressionAR(boolean traitementVirgule){
        System.out.println("dans parcerExpression");
        
        noeud gauche = parcerTerme(traitementVirgule);
        while(correspond(TokenType.PLUS) || correspond(TokenType.MINUS)){
            token operateur = TermeAvant();
            noeud droite = parcerTerme(traitementVirgule);
            if (droite == null) break; // fin de l'expression multiple
            gauche = new noeud_d_operation_binaire(operateur.type, gauche, droite);
        }System.out.println("dans parcerExpression avant le retour");
        
         // ----- TERNAIRE -----
        if (correspond(TokenType.QUESTION)) { // ?
            noeud vraiExpr = parcerExpression(sansTraitementVirgule);
            consomer(TokenType.COLON, "':' attendu dans l'expression ternaire");
            noeud fauxExpr = parcerExpression(sansTraitementVirgule);
            return new noeud_ternaire(gauche, vraiExpr, fauxExpr);
        }
    // --------------------
        
        return gauche;
    }
    
    public noeud parcerTerme(boolean traitementVirgule){
        System.out.println("dans parcerTerme");
        noeud gauche = parcerFacteur(traitementVirgule);
        while(correspond(TokenType.MULT) || correspond(TokenType.DIV) || correspond(TokenType.MOD)){
            token operateur = TermeAvant();
            noeud droite = parcerFacteur(traitementVirgule);
            if (droite == null) break; // fin de l'expression multiple
            System.out.println("(dans terme a l'interieur du while) dans le terme :"+examiner());
            gauche = new noeud_d_operation_binaire(operateur.type, gauche, droite);
        }
        System.out.println("(dans terme avant le retour) dans le terme :"+examiner());
        return gauche;
    }
    
   /* public noeud parcerFacteur(boolean traitementVirgule) {
        /*if (traitementVirgule) {
            // Compter les parenthèses imbriquées
            int parenCount = 0;
            token t = examiner();
            int i = 0;
            while (t != null && examiner(i+1).type!=TokenType.EOF) {
                if (t.type == TokenType.LPAREN) parenCount++;
                else if (t.type == TokenType.RPAREN) parenCount--;
                else if ((t.type == TokenType.COMMA || t.type == TokenType.SEMICOLON) && parenCount == 0) {
                    return null; // stoppe l'expression à la virgule ou point-virgule si pas dans parenthèse
                }
                t = examiner(i++); // regarde le token suivant sans consommer
            }
        }*
        if (traitementVirgule) {
            if (verifier(TokenType.COMMA) || verifier(TokenType.SEMICOLON)) {
                return null;
            }
        }
        
        // Nombre
        if (correspond(TokenType.NUMBER)) {
            System.out.println("dans le facteur (devant un nombre):"+examiner());
            return new noeud_de_valeur(TermeAvant().lexeme);
        }else if(correspond(TokenType.STRINGVAL)){
            System.out.println("dans le facteur (devant un nombre):"+examiner());
            return new noeud_de_valeur(TermeAvant().lexeme);
        }else if(correspond(TokenType.CHARVAL)){
            System.out.println("dans le facteur (devant un nombre):"+examiner());
            return new noeud_de_valeur(TermeAvant().lexeme);
        }
        
        // Booléens
        if (correspond(TokenType.TRUE)) {
            token t = TermeAvant();
            return new noeud_de_valeur("true");
        }
        if (correspond(TokenType.FALSE)) {
            token t = TermeAvant();
            return new noeud_de_valeur("false");
        }

        // Null
        if (correspond(TokenType.NULL)) {
            token t = TermeAvant();
            return new noeud_de_valeur("null");
        }
        
        if (correspond(TokenType.NEW)) {
            token classeTok = consomer(TokenType.IDENTIFIER, "Nom de classe attendu après 'new'.");
            consomer(TokenType.LPAREN, "'(' attendu après le nom de classe.");

            List<noeud> args = new ArrayList<>();
            if (!verifier(TokenType.RPAREN)) {
                while (true) {
                    args.add(parcerExpression(sansTraitementVirgule));
                    if (verifier(TokenType.COMMA)) {
                        consomer(TokenType.COMMA, "Attendu une virgule entre les arguments.");
                    } else {
                        break;
                    }
                }
            }

            consomer(TokenType.RPAREN, "')' attendu après les arguments.");
            return new noeud_constructeur(classeTok.lexeme, args);
        }

/
        // Identifiant (avec gestion du post ++/--)
        if (correspond(TokenType.IDENTIFIER)) {
            
            token id = TermeAvant();
            
            if(verifier(TokenType.IDENTIFIER, -1) && verifier(TokenType.LPAREN)){
                token meth = examiner(-1);
                reculer();
                return parcerAppelMethode(meth.lexeme);
            }
            //Au cas ou c'est un tableau
            List<noeud> indices = new ArrayList<>();
            
            if(verifier(TokenType.LBRACKET)){
                /*consomer(TokenType.LBRACKET, "Attendu le nomcommun ouvrant '['.");
                if(verifier(TokenType.IDENTIFIER) || verifier(TokenType.NUMBER)){parcerExpression(sansTraitementVirgule);}
                consomer(TokenType.RBRACKET, "Attendu le nomcommun ferment ']'.");*
                while (verifier(TokenType.LBRACKET)) {
                    consomer(TokenType.LBRACKET, "[ attendu");
                    indices.add(parcerExpression(sansTraitementVirgule));
                    consomer(TokenType.RBRACKET, "] attendu");
                }

                if (!indices.isEmpty()) {
                    return new NoeudAccesTableau(id.lexeme, indices);
                }
            }
            // Vérifie s’il y a un post-incrément ou post-décrément juste après
            if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
                token oper = TermeAvant();
                TokenType opType = (oper.type == TokenType.INCREMENT) ? TokenType.PLUS : TokenType.MINUS;

                // Représente : x = x + 1
                return new noeud_d_asignation(
                    id.lexeme,
                    null,
                    new noeud_d_operation_binaire(
                        opType,
                        new noeud_de_valeur(id.lexeme),
                        new noeud_de_valeur("1")
                    )
                );
            }System.out.println("dans le facteur (devant l'identificateur):"+examiner());
expr
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
            noeud expression = parcerExpression(sansTraitementVirgule);
            consomer(TokenType.RPAREN, "Attendu une ')' pour fermer l'expression .");
            return expression;
        }System.out.println("dans le facteur :"+examiner());

        if(verifier(TokenType.PLUS)|| verifier(TokenType.MINUS)){
            avancer();
            return parcerExpression(sansTraitementVirgule);
        }
        erreur("Attendu un facteur arithmetique (nombre, identifiant, ou expression sous '()')");
        reculer();
        return null;
    }*/
    
    public noeud parcerFacteur(boolean traitementVirgule) {
        System.out.println("Dans parcerFacteur" + examiner());
        
        if (traitementVirgule) {
            if (verifier(TokenType.COMMA) || verifier(TokenType.SEMICOLON)) {
                return null;
            }
        }

        noeud expr = null;

        // --- LITTERAUX ---
        if (correspond(TokenType.NUMBER) || correspond(TokenType.STRINGVAL)
                || correspond(TokenType.CHARVAL)) {
            return new noeud_de_valeur(TermeAvant().lexeme);
        }

        if (correspond(TokenType.TRUE)) return new noeud_de_valeur("true");
        if (correspond(TokenType.FALSE)) return new noeud_de_valeur("false");
        if (correspond(TokenType.NULL)) return new noeud_de_valeur("null");
        
        if(verifier(TokenType.MINUS) && (verifier(TokenType.IDENTIFIER, 1) || verifier(TokenType.NUMBER, 1)) && verifier(TokenType.SEMICOLON,2)){
            consomer(TokenType.MINUS, "Attendu un signe negatif '-' .");
            token blud = consomerAUnDes(TokenType.IDENTIFIER, TokenType.NUMBER, "Attendu un identificateur ou un nombre .");
            return new noeud_d_operation_binaire(TokenType.MINUS, new noeud_de_valeur("0"), new noeud_de_valeur(blud.lexeme));
        }

        // --- NEW CLASSE ---
        if (correspond(TokenType.NEW)) {
            token classeTok = consomer(TokenType.IDENTIFIER, "Nom de classe attendu après new");
            consomer(TokenType.LPAREN, "(");

            List<noeud> args = new ArrayList<>();
            if (!verifier(TokenType.RPAREN)) {
                do {
                    args.add(parcerExpression(false));
                } while (correspond(TokenType.COMMA));
            }
            consomer(TokenType.RPAREN, ")");

            expr = new noeud_constructeur(classeTok.lexeme, args);
        }

        // --- IDENTIFIANT ---
        else if (correspond(TokenType.IDENTIFIER)) {
            token id = TermeAvant();
            expr = new noeud_de_valeur(id.lexeme);
            //Au cas ou c'est un tableau
            List<noeud> indices = new ArrayList<>();

            // tableau : a[i][j]
            if (verifier(TokenType.LBRACKET)){
                while (verifier(TokenType.LBRACKET)) {
                    consomer(TokenType.LBRACKET, "[ attendu");
                    indices.add(parcerExpression(sansTraitementVirgule));
                    consomer(TokenType.RBRACKET, "] attendu");
                }

                if (!indices.isEmpty()) {
                    return new NoeudAccesTableau(id.lexeme, indices);
                }
            }
            

            // post ++ / --
            if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
                token op = TermeAvant();
                TokenType t = (op.type == TokenType.INCREMENT) ? TokenType.PLUS : TokenType.MINUS;
                return new noeud_d_asignation(
                    id.lexeme,
                    null,
                    new noeud_d_operation_binaire(t, expr, new noeud_de_valeur("1"))
                );
            }
        }

        // --- PRE ++ / --
        else if (correspond(TokenType.INCREMENT) || correspond(TokenType.DECREMENT)) {
            token op = TermeAvant();
            token id = consomer(TokenType.IDENTIFIER, "identifiant attendu");
            TokenType t = (op.type == TokenType.INCREMENT) ? TokenType.PLUS : TokenType.MINUS;
            return new noeud_d_operation_binaire(t, new noeud_de_valeur(id.lexeme), new noeud_de_valeur("1"));
        }

        // --- PARENTHESES ---
        else if (correspond(TokenType.LPAREN)) {
            expr = parcerExpression(false);
            consomer(TokenType.RPAREN, "Attendu une parenthese fermente ')' .");
        }
        
        // --- THIS (C'EST MOI QUI L'AI AJOUTES) ---
        else if(correspond(TokenType.THIS)){
            //expr = new noeud_de_valeur(examiner(1).lexeme);
        }

        else {
            erreur("Facteur invalide");
            return null;
        }

        // =====================================================
        // ========   CHAÎNAGE OBJET :  a.b.c().d   ============
        // =====================================================
        while (verifier(TokenType.POINT)) {
            consomer(TokenType.POINT, "Attendu un point '.' .");
            String nom = consomer(TokenType.IDENTIFIER, "champ ou méthode attendu").lexeme;

            if (verifier(TokenType.LPAREN)) {
                // appel méthode
                consomer(TokenType.LPAREN, "Attendu une parenthese ouvrente '(' .");
                List<noeud> args = new ArrayList<>();
                if (!verifier(TokenType.RPAREN)) {
                    do {
                        args.add(parcerExpression(false));
                    } while (correspond(TokenType.COMMA));
                }
                consomer(TokenType.RPAREN, "Attendu une parenthese fermente ')' .");

                expr = new NoeudAppelMethodeObjet(expr, nom, args);
            } else {
                // accès champ
                //erreur(expr.toString());
                expr = new NoeudAccesChamp(expr, nom);
            }
            // gérer post-incrément / post-décrément
            if (correspond(TokenType.INCREMENT)) {
                //avancer(); // consommer '++'
                expr = new noeud_d_operation_binaire(TokenType.PLUS,expr, new noeud_de_valeur("1"));
            } else if (correspond(TokenType.DECREMENT)) {
                //avancer(); // consommer '--'
                expr = new noeud_d_operation_binaire(TokenType.MINUS,expr, new noeud_de_valeur("1"));
            }
        }

        return expr;
    }

    
    private noeud parseDeclarationVariable() {
        System.out.println("dans parcerDeclarationVariable");
        // Lire le type
        token typeTok = avancer(); // int, double, etc.
        
        if(verifier(TokenType.LBRACKET)){
            consomer(TokenType.LBRACKET, "Attendu le nomcommun ouvrant '['.");
            consomer(TokenType.RBRACKET, "Attendu le nomcommun ferment ']'.");
        }
        
        

        // Lire le nom
        token nomTok = consomer(TokenType.IDENTIFIER, "Nom de variable attendu.");

        noeud valeur = null;

        // Vérifie s'il y a une affectation
        if (verifier(TokenType.ASSIGN)){
            avancer(); // saute "="
            valeur = parcerExpression(sansTraitementVirgule); // ou parseValeurSimple()
        }

        // Fin d’instruction obligatoire
        consomer(TokenType.SEMICOLON, "';' attendu à la fin de la déclaration.");

        return new VariableNode(true, sansTraitementConstante, new Type(typeTok.lexeme, 0), nomTok.lexeme, valeur);
    }
    
    public noeud parcerConstante() {
        System.out.println("dans parcerConstante");
        //consomer(TokenType.CONST, "Mot-clé 'const' attendu.");
        consomerAUnDes(TokenType.CONST, TokenType.FINAL, "Mot-clé 'const'/'final' attendu.");

        /*token typeTok = consomerUnDes(
            TokenType.INT, TokenType.DOUBLE, TokenType.STRING,
            TokenType.BOOLEAN, TokenType.CHAR, TokenType.FLOAT,
            TokenType.LONG, TokenType.SHORT,
            "Type de constante attendu."
        );*/
        
        token typeTok = null;
        
        if(verifier(TokenType.INT) || verifier(TokenType.DOUBLE) || verifier(TokenType.STRING) ||
           verifier(TokenType.BOOLEAN) || verifier(TokenType.CHAR) || verifier(TokenType.FLOAT) ||
           verifier(TokenType.LONG) || verifier(TokenType.SHORT) ) {
            typeTok = examiner();
        }else{
            erreur("Attendu un type de variable .");
        }
        System.out.println("typeTok : " + typeTok);
        System.out.println("le mot courrant" + examiner());
        //VariableNode varNoeud = (VariableNode)parseDeclarationVariable();
        noeud_declaration_multiple varNoeud = (noeud_declaration_multiple)new noeud_declaration_multiple(parseDeclarationMultiple(avecTraitementConstante));

        /*token nomTok = consomer(TokenType.IDENTIFIER, "Nom de constante attendu.");

        consomer(TokenType.ASSIGN, "'=' attendu pour initialiser une constante.");

        noeud valeur = parcerExpression();

        consomer(TokenType.SEMICOLON, "';' attendu après la constante.");*/

        return new noeud_constante(typeTok.lexeme,varNoeud);
    }

    private noeud parcerConstructeur(){
        String modificateur = null;
        if (verifier(TokenType.PUBLIC) || verifier(TokenType.PRIVATE) || verifier(TokenType.PROTECTED) ) {
            modificateur = examiner().lexeme;
            avancer();
        }
        
        String nomClass = consomer(TokenType.IDENTIFIER, "Attendu le nom du constructeur .").lexeme;
        
        List<noeud_parametre> Pretour = new ArrayList();
        consomer(TokenType.LPAREN, "'(' attendu.");
        if(verifier(TokenType.STRING) || verifier(TokenType.BOOLEAN) || verifier(TokenType.DOUBLE) || verifier(TokenType.INT) || verifier(TokenType.FLOAT) || verifier(TokenType.LONG) || verifier(TokenType.SHORT) || verifier(TokenType.CHAR)){
            while(!fin() && !verifier(TokenType.RPAREN)){
                if(verifier(TokenType.STRING) || verifier(TokenType.BOOLEAN) || verifier(TokenType.DOUBLE) || verifier(TokenType.INT) || verifier(TokenType.FLOAT) || verifier(TokenType.LONG) || verifier(TokenType.SHORT) || verifier(TokenType.CHAR)){
                    Pretour.add(parcerParametres());
                    if(verifier(TokenType.COMMA)){
                        consomer(TokenType.COMMA, "Attendu une virgule (erreur relic).");
                    }else{
                        if(!verifier(TokenType.RPAREN)){
                            erreur("Attendu une virgule entre les deux parametres");
                        }
                        break;
                    }
                }else{
                    erreur("Op ,op, op, reveiw your code lil vro (attendu un vrai type.) ");
                }
                
            }
            
        }
        consomer(TokenType.RPAREN, "')' attendu.");
        
        consomer(TokenType.LBRACE, "'{' attendu avant le corps de la méthode.");

        List<noeud> instructions = new ArrayList<>();
        while (!verifier(TokenType.RBRACE) && !fin()) {
            /*instructions.add(parcerMot());
            System.out.println("dans parcerMethode (apres instructions.add(parcerMot());)");*/
            noeud instr = parcerMot(nomClass);
            if (instr != null) {
                instructions.add(instr);
                System.out.println("instruction ajoutée :" + instr);
            } else {
                // pour éviter boucle infinie si parcerMot() retourne null
                System.out.println("parcerMot() a retourné null pour :" + examiner());
                avancer();
            }
        }

        if(verifier(TokenType.RBRACE)){
            consomer(TokenType.RBRACE, "'}' attendu à la fin de la méthode.");
            return new noeud_declaration_constructeur(modificateur, nomClass, Pretour, instructions);
        }
        
        
        return null;
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
        
        // 2.1) Dimention du Type (au cas ou)
        int dim = 0;
        
        while(verifier(TokenType.LBRACKET)){
            consomer(TokenType.LBRACKET, "Attendu '[' . ");
            consomer(TokenType.RBRACKET, "Attendu ']' . ");
            dim++;
        }

        System.out.println("type de retour :"+typeTok+"   nom de la methode :"+examiner(1));
        // 3) nom de la méthode
        token nomTok;
         if (!verifier(TokenType.IDENTIFIER)){nomTok = consomer(TokenType.IDENTIFIER, "Nom de méthode attendu.");reculer();}else{nomTok = consomer(TokenType.IDENTIFIER, "Nom de méthode attendu.");}
        System.out.println("type de retour :"+typeTok+"   nom de la methode (apres consomation):"+examiner(1));
        
        // 4) parenthèses
        List<noeud_parametre> Pretour = new ArrayList();
        consomer(TokenType.LPAREN, "'(' attendu.");
        if(verifier(TokenType.STRING) || verifier(TokenType.BOOLEAN) || verifier(TokenType.DOUBLE) || verifier(TokenType.INT) || verifier(TokenType.FLOAT) || verifier(TokenType.LONG) || verifier(TokenType.SHORT) || verifier(TokenType.CHAR)){
            while(!fin() && !verifier(TokenType.RPAREN)){
                if(verifier(TokenType.STRING) || verifier(TokenType.BOOLEAN) || verifier(TokenType.DOUBLE) || verifier(TokenType.INT) || verifier(TokenType.FLOAT) || verifier(TokenType.LONG) || verifier(TokenType.SHORT) || verifier(TokenType.CHAR)){
                    Pretour.add(parcerParametres());
                    if(verifier(TokenType.COMMA)){
                        consomer(TokenType.COMMA, "Attendu une virgule (erreur relic).");
                    }else{
                        if(!verifier(TokenType.RPAREN)){
                            erreur("Attendu une virgule entre les deux parametres");
                        }
                        break;
                    }
                }else{
                    erreur("Op ,op, op, reveiw your code lil vro (attendu un vrai type.) ");
                }
                
            }
            
        }
        consomer(TokenType.RPAREN, "')' attendu.");

        // 5) bloc
        consomer(TokenType.LBRACE, "'{' attendu avant le corps de la méthode.");

        List<noeud> instructions = new ArrayList<>();
        while (!verifier(TokenType.RBRACE) && !fin()) {
            /*instructions.add(parcerMot());
            System.out.println("dans parcerMethode (apres instructions.add(parcerMot());)");*/
            noeud instr = parcerMot(typeTok.lexeme);
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

        return new MethodeNode(modificateur, typeTok.lexeme, dim, nomTok.lexeme, Pretour, instructions);
    }
    
    public noeud_parametre parcerParametres(){
        System.out.println("dans parcerParametres");
        // Lire le type
        token typeTok =null;
        if(!fin()){
            typeTok = avancer(); // int, double, etc.
        }
        
        
        String crochets = null;
        int dim = 0;
        while(verifier(TokenType.LBRACKET)){
            consomer(TokenType.LBRACKET, "Attendu le nomcommun ouvrant '['.");
            crochets="[ ";
            consomer(TokenType.RBRACKET, "Attendu le nomcommun ferment ']'.");
            crochets+=']';
            dim++;
        }
        if(verifier(TokenType.LBRACKET)){
            consomer(TokenType.LBRACKET, "Attendu le nomcommun ouvrant '['.");
            crochets="[ ";
            consomer(TokenType.RBRACKET, "Attendu le nomcommun ferment ']'.");
            crochets+=']';
        }
        
        

        // Lire le nom
        token nomTok = consomer(TokenType.IDENTIFIER, "Nom de variable attendu.");

        
        /*if(verifier(TokenType.COMMA)){
            consomer(TokenType.COMMA, "Attendu une virgule (erreur relic).");
        }*/
        

        
        if(crochets==null){
            return new noeud_parametre(typeTok.lexeme, dim, nomTok.lexeme);
        }
        if(typeTok==null){
            return new noeud_parametre(typeTok.lexeme+crochets, dim, nomTok.lexeme);
        }
        return new noeud_parametre("nUll", 0, nomTok.lexeme);
    }
    
    
    public noeud parcerReturn(String currentMethodReturnType) {
        if(!verifierReturn(currentMethodReturnType))return null;
        consomer(TokenType.RETURN, "Attendu 'return'.");

        // si la ligne est juste "return;"
        if (verifier(TokenType.SEMICOLON)) {
            avancer();
            return new noeud_return(null);
        }
        
        if(!veriturn(currentMethodReturnType))return null;

        // sinon : return expression;
        noeud expr = parcerExpression(sansTraitementVirgule);

        consomer(TokenType.SEMICOLON, "Attendu ';' après return.");

        return new noeud_return(expr);
    }
    
    private boolean verifierReturn(String currentMethodReturnType) {
        /*if ("void".equals(currentMethodReturnType)) {
            erreur("Erreur : return utilisé dans une méthode void.");
            return false;
        }*/

        if (!"void".equals(currentMethodReturnType)) {
            // la méthode doit retourner quelque chose
            if (verifier(TokenType.SEMICOLON, 1)) {
                erreur("Erreur : return dans une méthode non-void sans valeur.");
                return false;
            }
        }
        return true;
    }
    
    private boolean veriturn(String currentMethodReturnType) {
        if ("void".equals(currentMethodReturnType)) {
            erreur("Erreur : return utilisé dans une méthode void.");
            return false;
        }

        
        return true;
    }
    
    public noeud_while parcerWhile(String methode_courrante, boolean bloque){
        
        consomer(TokenType.WHILE, "Attendu 'While' (Erreur tres rare).");
        //noeud_de_condition condition = parcerCondition(/*avecEtOu*/);
        consomer(TokenType.LPAREN, "Attendu '(' après 'if'.");
        noeud condition = parcerCondition(/*avecEtOu*/);
        consomer(TokenType.RPAREN, "Attendu ')' après 'if'.");
        
        block_de_noeuds block = null;
        
        if(bloque){
            block = parcerBlock(methode_courrante, avecTraitementBlock);
        }else{
            if(verifier(TokenType.LBRACE)){
                erreur("le block qui a etait ouvers n'a rien a faire ici (car il y a un do avant le while)");
            }
            
        }
        
        return new noeud_while(condition, block);
    }
    
    public noeud parcerFor(String methode_courrante){
        consomer(TokenType.FOR, "Attendu 'for' (Erreur tres rare).");
        
        noeud_compteur compteur = parcerCompteur();
        
        block_de_noeuds block = parcerBlock(methode_courrante ,avecTraitementBlock);
        
        return new noeud_for(compteur, block);
    }
    
    public noeud_compteur parcerCompteur(){
        System.out.println("dans parcerCompteur  : " + examiner());
        consomer(TokenType.LPAREN, "Attendu une perenthese ouvrente '('.");
        
        token type = null;
        if(verifier(TokenType.STRING) || verifier(TokenType.BOOLEAN) || verifier(TokenType.DOUBLE) || verifier(TokenType.INT) || verifier(TokenType.FLOAT) || verifier(TokenType.LONG) || verifier(TokenType.SHORT) || verifier(TokenType.CHAR)){
            type = examiner();
            avancer();
        }
        
        token identificateur = consomer(TokenType.IDENTIFIER, "Nom de variable attendu.");
        
        noeud valeurDepart = null;
        
        // Vérifie s'il y a une affectation
        if (verifier(TokenType.ASSIGN)){
            avancer(); // saute "="
            valeurDepart = parcerExpression(sansTraitementVirgule); // ou parseValeurSimple()
        }
        
        consomer(TokenType.SEMICOLON, "Attendu ';' apres la variable ");
        
        //noeud_de_condition limite = parcerCondition(/*sansEtOu*/);
        System.out.println("sur le point d'entrer dans la condition du compteur  : " + examiner());
        noeud limite = parcerCondition(/*sansEtOu*/);
        
        consomer(TokenType.SEMICOLON, "Attendu ';' apres la confition du compteur ");
        System.out.println("sur le point de rentrer dans l'assignation du pas du compteur  : " + examiner());
        noeud_d_asignation pas = parcerAssignationCompteur(sansSemicolone);
        
        if(verifier(TokenType.RPAREN)){
            consomer(TokenType.RPAREN, "Attendu une perenthese fermente ')'.");
            if(type==null){
                return new noeud_compteur(new Type("", 0), identificateur.lexeme, valeurDepart, limite, pas);
            }else{
                return new noeud_compteur(new Type(type.lexeme, 0), identificateur.lexeme, valeurDepart, limite, pas);
            }
            
        }
        
        consomer(TokenType.RPAREN, "Attendu une perenthese fermente ')'.");
        
        return null;
    }
    
    public noeud parcerDo(String methode_courrante){
        consomer(TokenType.DO, "Attendu 'do' (Erreur tres rare).");
        
        block_de_noeuds block = parcerBlock(methode_courrante, avecTraitementBlock);
        
        noeud_while condition = parcerWhile(methode_courrante, sansBlock);
        
        return new noeud_do(block, condition);
    }
    
    public noeud_appel_methode parcerAppelMethode(String nomMethode) {
        System.out.println("Appel de méthode : " + nomMethode);
        avancer();
        consomer(TokenType.LPAREN, "Attendu '(' pour l'appel de méthode.");

        List<noeud> arguments = new ArrayList<>();
        if (!verifier(TokenType.RPAREN)) {
            do {
                arguments.add(parcerExpression(sansTraitementVirgule));
            } while (verifier(TokenType.COMMA) && avancer() != null); // gérer les virgules
        }

        consomer(TokenType.RPAREN, "Attendu ')' après l'appel de méthode.");
        //consomer(TokenType.SEMICOLON, "Attendu ';' apres l'appelle de la methode : "+nomMethode+" .");

        return new noeud_appel_methode(nomMethode, arguments);
    }
    
    public List<noeud> parseDeclarationMultiple(boolean imutable) {
        System.out.println("dans parcerDeclarationMultiple");
        
        token typeTok = avancer(); // int, double, etc.
        boolean tab=false;
        int dim = 0;
        while (verifier(TokenType.LBRACKET)) {
            consomer(TokenType.LBRACKET, "[ attendu");
            consomer(TokenType.RBRACKET, "] attendu");
            dim++;
            tab = true;
        }
        
        String typeFinal = typeTok.lexeme;
        //for (int i = 0; i < dim; i++) typeFinal += "[]";

        
        List<noeud> vars = new ArrayList<>();
        System.out.println("typeTok : " + typeTok);
        int i=dim;

        while (true) {
            token nomTok = consomer(TokenType.IDENTIFIER, "Nom de variable attendu.");
            noeud valeur = null;
            
            
            if (verifier(TokenType.ASSIGN) && examiner(1).type == TokenType.LBRACE) {
                List<noeud> valeurs = new ArrayList<>();
                
                avancer(); // =
                consomer(TokenType.LBRACE, "{ attendu");
                
                if(dim>1){
                    for(int is = 0; is < dim; is++){
                        consomer(TokenType.LBRACE, "{ attendu");
                        do {
                            valeurs.add(parcerExpression(sansTraitementVirgule));
                        } while (correspond(TokenType.COMMA));

                        consomer(TokenType.RBRACE, "} attendu");
                        if(verifier(TokenType.COMMA)){
                            consomer(TokenType.COMMA, ", attendu");
                        }
                    }
                }else{
                    do {
                        valeurs.add(parcerExpression(sansTraitementVirgule));
                    } while (correspond(TokenType.COMMA));
                }
                

                //List<noeud> valeurs = new ArrayList<>();
                

                consomer(TokenType.RBRACE, "} attendu");
                //consomer(TokenType.SEMICOLON, "; attendu");

                vars.add(new noeud_tableau_init(new Type(typeFinal, dim), nomTok.lexeme, valeurs));
            }else if (verifier(TokenType.ASSIGN)) {
                avancer();
                valeur = parcerExpression(avecTraitementVirgule);
                boolean initialisation = false;
                if(valeur!=null){
                    initialisation = true;
                }
                //avancer();
                vars.add(new VariableNode(initialisation, imutable, new Type(typeFinal, dim), nomTok.lexeme, valeur));
            } else {
                vars.add(new VariableNode(false, imutable, new Type(typeFinal, dim), nomTok.lexeme, null));
            }

            /*if(!tab){
                
            }*/
            
            
            System.out.println("le truc courrant : " + examiner());

            
            if (verifier(TokenType.COMMA)) {
                avancer(); // passe la virgule et continue
            } else if (verifier(TokenType.SEMICOLON)) {
                consomer(TokenType.SEMICOLON, "';' attendu à la fin de la déclaration.");
                break;
            } else {
                erreur("Attendu ',' ou ';' après la variable.");
                break;
            }
        }

        return vars;
    }
    
    public noeud parcerSwitch(String methode_courrante) {
        consomer(TokenType.SWITCH, "'switch' attendu");
        consomer(TokenType.LPAREN, "'(' attendu après switch");
        noeud expr = parcerExpression(sansTraitementVirgule);
        consomer(TokenType.RPAREN, "')' attendu");

        consomer(TokenType.LBRACE, "'{' attendu après switch");

        List<noeud_case> cases = new ArrayList<>();

        noeud_default defaul = null;
        boolean def = false;
        while (!verifier(TokenType.RBRACE)) {
            if (verifier(TokenType.CASE)) {
                consomer(TokenType.CASE, "'case' attendu");
                noeud valeur = parcerExpression(sansTraitementVirgule);
                
                List<noeud> instructions = new ArrayList<>();
                if(verifier(TokenType.RARROW)){
                    consomer(TokenType.RARROW, "'->' attendu apres case");
                    consomer(TokenType.LBRACE, "'{' attendu après la fleche de droite");
                    while (!verifier(TokenType.CASE) && !verifier(TokenType.DEFAULT) && !verifier(TokenType.RBRACE)) {
                        instructions.add(parcerMot(methode_courrante));
                    }
                    consomer(TokenType.RBRACE, "'}' attendu pour fermer le case");
                }else{
                    consomer(TokenType.COLON, "':' attendu après case");
                    
                    instructions.add(parcerExpression(sansTraitementVirgule));
                    
                    consomer(TokenType.SEMICOLON, "Attendu un point-virgule ';' .");
                }
                

                
                

                cases.add(new noeud_case(valeur, instructions));
            } else if (verifier(TokenType.DEFAULT) && def==false) {
                def=true;
                consomer(TokenType.DEFAULT, "'default' attendu");
                List<noeud> instructions = new ArrayList<>();
                if(verifier(TokenType.RARROW)){
                    consomer(TokenType.RARROW, "'->' attendu apres case");
                    consomer(TokenType.LBRACE, "'{' attendu après la fleche de droite");
                    while (!verifier(TokenType.CASE) && !verifier(TokenType.RBRACE)) {
                        instructions.add(parcerMot(methode_courrante));
                    }
                    consomer(TokenType.RBRACE, "'}' attendu pour fermer le case");
                }else{
                    consomer(TokenType.COLON, "':' attendu après case");
                    
                    instructions.add(parcerExpression(sansTraitementVirgule));
                    
                    consomer(TokenType.SEMICOLON, "Attendu un point-virgule ';' .");
                }
                /*consomer(TokenType.COLON, "':' attendu après default");

                List<noeud> instructions = new ArrayList<>();
                while (!verifier(TokenType.CASE) && !verifier(TokenType.RBRACE)) {
                    instructions.add(parcerMot(null));
                }*/

                defaul = new noeud_default(instructions);
                //cases.add(new noeud_default(instructions));
            } else if (verifier(TokenType.DEFAULT) && def==true){
                erreur("default dupliques (alors qu'un switch ne peut en avoir qu'un)");
            } else {
                erreur("case, default ou '}' attendu dans switch");
            }
        }

        consomer(TokenType.RBRACE, "'}' attendu pour fermer le switch");

        return new noeud_switch(expr, cases, defaul);
    }
    
    public noeud parcerComparaison(boolean traitementVirgule) {
        noeud gauche = parcerExpressionAR(traitementVirgule);

        while (correspond(TokenType.GT) || correspond(TokenType.LT)
            || correspond(TokenType.GTE) || correspond(TokenType.LTE)
            || correspond(TokenType.EQEQ) || correspond(TokenType.NOTEQ)) {

            token op = TermeAvant();
            noeud droite = parcerExpressionAR(traitementVirgule);
            gauche = new noeud_d_operation_binaire(op.type, gauche, droite);
        }
        return gauche;
    }
    
    public noeud parcerExpression(boolean traitementVirgule) {
        noeud condition = parcerComparaison(traitementVirgule);

        if (correspond(TokenType.QUESTION)) {
            noeud vraiExpr = parcerExpressionAR(sansTraitementVirgule);
            consomer(TokenType.COLON, "':' attendu dans l'expression ternaire");
            noeud fauxExpr = parcerExpressionAR(sansTraitementVirgule);
            return new noeud_ternaire(condition, vraiExpr, fauxExpr);
        }

        return condition;
    }
    
    private NoeudPackage parsePackage() {
        consomer(TokenType.PACKAGE, "package attendu");

        List<String> noms = new ArrayList<>();
        noms.add(consomer(TokenType.IDENTIFIER, "nom attendu").lexeme);

        while (correspond(TokenType.POINT)) {
            noms.add(consomer(TokenType.IDENTIFIER, "nom attendu").lexeme);
        }

        consomer(TokenType.SEMICOLON, "; attendu après package");
        return new NoeudPackage(noms);
    }
    
    private NoeudImport parseImport() {
        consomer(TokenType.IMPORT, "import attendu");

        boolean isStatic = correspond(TokenType.STATIC);

        List<String> chemin = new ArrayList<>();
        chemin.add(consomer(TokenType.IDENTIFIER, "nom attendu").lexeme);

        while (correspond(TokenType.POINT)) {
            if (correspond(TokenType.MULT)) {
                consomer(TokenType.SEMICOLON, "; attendu");
                return new NoeudImport(chemin, true, isStatic);
            }
            chemin.add(consomer(TokenType.IDENTIFIER, "nom attendu").lexeme);
        }

        consomer(TokenType.SEMICOLON, "; attendu");
        return new NoeudImport(chemin, false, isStatic);
    }
    
    public noeud parcerEnum (){
        System.out.println("Dans  parcerEnum");
        
        consomer(TokenType.ENUM, " Mot-clef 'enum' attendu .");
        
        String nom = consomer(TokenType.IDENTIFIER, " Identificateur attendu .").lexeme;
        
        List<noeud> valeurs = new ArrayList<>();
        
        consomer(TokenType.LBRACE, "Attendu '{' de l'enum .");
        
        while(!verifier(TokenType.RBRACE)){
            valeurs.add(new noeud_de_valeur(consomer(TokenType.IDENTIFIER, " Identificateur attendu .").lexeme));
            if(verifier(TokenType.COMMA)){
                consomer(TokenType.COMMA, "Attendu une virgule ',' .");
            }
        }
        
        consomer(TokenType.RBRACE, "Attendu '}' de l'enum .");
        
        return new noeud_enum(nom, valeurs);
    }
    
    public noeud parcerTry(String methode_courrante){
        System.out.println("dans parcerTry");
        
        consomer(TokenType.TRY, "Attendu le mot-clef 'try' .");
        
        noeud condTry = null;
        
        if(verifier(TokenType.LPAREN)){
            consomer(TokenType.LPAREN, "Attendu une parenthese ouvrente '(' .");
            
            condTry = parcerBlock(methode_courrante, sansTraitementBlock);
            
            consomer(TokenType.RPAREN, "Attendu une parenthese fermente ')' .");
        }
        
        //consomer(TokenType.LBRACE, "Attendu '{' du block de Try .");
        noeud blockTry = parcerBlock(methode_courrante, avecTraitementBlock);
        System.out.println("sorti de yes :" +examiner());
        //consomer(TokenType.RBRACE, "Attendu '}' du block de try .");
        
        List<noeud_catch> catches = new ArrayList<>();
        
        while(verifier(TokenType.CATCH)){
            catches.add(parcerCatch(methode_courrante));
        }
        if(catches.isEmpty()){
            erreur("Attendu au moins un element 'catch' pour l'element 'try' .");
        }
        
        return new noeud_try(condTry, blockTry, catches);
    }
    
    public noeud_catch parcerCatch(String methode_courrante){
        System.out.println("dans parcerCatch");
        
        if(verifier(TokenType.CATCH)){
            consomer(TokenType.CATCH, "Attendu le mot-clef 'catch' .");
        }
        noeud condCatch = null;
        consomer(TokenType.LPAREN, "Attendu une parenthese ouvrente '(' .");

        condCatch = parcerParametres();

        consomer(TokenType.RPAREN, "Attendu une parenthese fermente ')' .");
        noeud blockCatch = parcerBlock(methode_courrante, avecTraitementBlock);
        
        return new noeud_catch(condCatch, blockCatch);
    }







    
    
    public noeud parcerTafoukt(){
        consomer(TokenType.TAFOUKT, "Attendu 'tafoukt' (erreur legendaire)");
        consomer(TokenType.SEMICOLON, "Attendu une semicolonne ';'");
        return new noeud_tafoukt(new noeud_d_asignation(
                    "67",
                    null,
                    new noeud_d_operation_binaire(
                        TokenType.TAFOUKT,
                        new noeud_de_valeur("6"),
                        new noeud_de_valeur("7")
                    )));
    }
    
    public noeud parcerZakaria(){
        consomer(TokenType.ZAKARIA, "Attendu 'tafoukt' (erreur legendaire)");
        consomer(TokenType.SEMICOLON, "Attendu une semicolonne ';'");
        
        return new noeud_zakaria(new noeud_d_asignation(
                    "27",
                    null,
                    new noeud_d_operation_binaire(
                        TokenType.TAFOUKT,
                        new noeud_de_valeur("strength"),
                        new noeud_de_valeur("patience")
                    )));
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
    
    
    
    private void erreur(String message) {
        token courant = examiner();
        erreurs.add("Erreur près de '" + courant.lexeme + "' (pos " + courant.position + "): " + message);
        System.out.println("Erreur près de '" + courant.lexeme + "' (pos " + courant.position + "): " + message);
        // --- très important : consommer pour éviter boucle infinie
        if(!fin()){
            avancer();
        }


        // --- option : synchroniser jusqu’à un séparateur connu
        while (!fin()) {
            token t = examiner();
            if (t.lexeme.equals(";") || t.lexeme.equals("}") || t.lexeme.equals("{") || t.lexeme.equals("(") || t.lexeme.equals(")") || t.lexeme.equals("[") || t.lexeme.equals("]") || t.lexeme.equals("if") || t.lexeme.equals("else")  || t.lexeme.equals("else if") || verifier(TokenType.EOF,1)) break;
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
            System.out.println("pass : " + type);
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
            //System.out.println(prefix + "Condition: " + c.gauche + " " + c.operateur + " " + c.droite);
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

/*
1️⃣ Appels de constructeur (new)              cool

Exemple : Point p = new Point(3, 5);

Parser doit créer un NoeudAppelConstructeur ou similaire.

Vérifier juste la syntaxe new IDENTIFIER '(' [paramètres] ')'.

2️⃣ Déclarations multiples dans une seule ligne (optionnel mais pratique)    cool

Exemple : int a = 1, b = 2, c;

Le parser doit créer plusieurs VariableNode à partir d’une seule déclaration.

3️⃣ Gestion des tableaux complète       cool

Tu gères déjà [] pour déclarer et accéder (t[i]), mais :

Déclaration avec initialisation : int[] t = {1, 2, 3};

Vérifier multiples dimensions : int[][] mat;

Pas super urgent, mais si tu veux un compilateur complet, ça fait partie.

4️⃣ Gestion des littéraux “composés”      cool

Tu as les nombres, chaînes, caractères, booléens ✅

Mais il manque peut-être les null et true/false constants

5️⃣ Gestion de break / continue (optionnel)        cool

Pour boucles for, while, do-while

6️⃣ Gestion de switch / case / default (optionnel)     cool

Si tu veux un vrai compilateur Java-like

7️⃣ Expressions ternaires             cool

Exemple : x = a > b ? a : b;

Pas obligatoire mais standard

8️⃣ Gestion des import / package (optionnel)         cool

Si ton compilateur doit gérer plusieurs fichiers
*/