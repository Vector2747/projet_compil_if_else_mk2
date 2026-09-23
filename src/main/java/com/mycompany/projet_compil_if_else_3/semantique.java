/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_compil_if_else_3;

import com.mycompany.projet_compil_if_else_3.lexer.TokenType;
import com.mycompany.projet_compil_if_else_3.lexer.token;
/**
 *
 * @author InfoPro
 */import java.util.*;
import com.mycompany.projet_compil_if_else_3.parcer.*;
public class semantique {
    
    parcer parcer;
    public static class Type {
        public String base;      // "int", "boolean", "String", nom de classe
        public int dimension;    // 0 = simple, 1 = [], 2 = [][]
        public boolean immutable = false;
        boolean isPrimitive;
        
        boolean isNullable;

        boolean isFinal;

        boolean isEnum;

        public Type(String base, int dimension) {
            this.base = base;
            this.dimension = dimension;
        }

        public boolean estTableau() {
            return dimension > 0;
        }

        public boolean estSimple() {
            return dimension == 0;
        }

        public boolean estInt() {
            return base.equals("int") && dimension == 0;
        }

        public boolean estBoolean() {
            return base.equals("boolean") && dimension == 0;
        }

        public Type elementType() {
            if (dimension == 0) return null;
            return new Type(base, dimension - 1);
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Type)) return false;
            Type t = (Type) o;
            return base.equals(t.base) && dimension == t.dimension;
        }


        @Override
        public String toString() {
            return base + "[]".repeat(dimension);
        }
    }

    

    // Classe principale pour l'analyse sémantique


    // -----------------------------
    // Pile de scopes (chaque Map = variables locales d'un bloc)
    private Stack<Map<String, InfoVariable>> pileScopes = new Stack<>();

    // Table des fonctions : nom -> [typeRetour, listeTypesParamètres]
    private Map<String, FonctionInfo> tableFonctions = new HashMap<>();
    
    // pile de scopes de contexte(pour les trucs qui ne doivent etre qu'a l'interieur de seulement sertaines choses)
    public enum Contexte {
        BOUCLE,   // while, for, do
        SWITCH
    }

    Stack<Contexte> pileContextes = new Stack<>();


    // Liste pour stocker toutes les erreurs sémantiques
    private List<String> erreurs = new ArrayList<>();

    // Classe interne pour représenter une fonction
    public static class FonctionInfo {
        Type typeRetour;
        List<Type> typesParam;

        FonctionInfo(Type typeRetour, List<Type> typesParam) {
            this.typeRetour = typeRetour;
            this.typesParam = typesParam;
        }

        @Override
        public String toString() {
            return "FonctionInfo{" + "typeRetour=" + typeRetour + ", typesParam=" + typesParam + '}';
        }
        
        
    }
    
    public static class InfoVariable {
        Type type;
        boolean initialisee;

        public InfoVariable(Type type, boolean initialisee) {
            this.type = type;
            this.initialisee = initialisee;
        }

        @Override
        public String toString() {
            return "InfoVariable{" + "type=" + type + ", initialisee=" + initialisee + '}';
        }
        
        
    }
    
    // Pour chaque classe déclarée : nomClasse -> variables membres
    private Map<String, Map<String, InfoVariable>> classesVariables = new HashMap<>();

    // Pour chaque classe déclarée : nomClasse -> méthodes
    private Map<String, Map<String, FonctionInfo>> classesMethodes = new HashMap<>();



    // -----------------------------
    // Fonction principale : analyser un noeud AST
    public void analyser(noeud n) {
        if (n == null) return;

        if (n instanceof programmeNoeud p) {
            // Scope global
            pileScopes.push(new HashMap<>());
            for (noeud child : p.mots) {
                analyser(child);
            }
            pileScopes.pop();
        }
        else if (n instanceof block_de_noeuds b) {
            // Nouveau scope pour le bloc
            pileScopes.push(new HashMap<>());
            for (noeud child : b.mots) {
                analyser(child);
            }
            pileScopes.pop();
        }
        else if (n instanceof noeud_d_asignation a) {
            // Vérifier que la variable existe ou créer si c'est une déclaration
            analyserAssignation(a);
        }
        else if (n instanceof noeud_de_condition c) {
            analyserCondition(c);
        }
        else if (n instanceof noeud_si si) {
            // Analyser condition et blocs
            analyser(si.condition);
            analyser(si.blockAlors);
            if (si.block_sinon != null)
                analyser(si.block_sinon);
        }
        else if (n instanceof MethodeNode m) {
            analyserMethode(m);
        }
        else if (n instanceof noeud_d_operation_binaire op) {
            analyser(op.gauche);
            analyser(op.droite);
        }
        else if (n instanceof noeud_while w) {
            analyser(w.condition);
            analyser(w.block);
        }
        else if (n instanceof noeud_for f) {
            analyser(f.compteur);
            analyser(f.block);
        }
        // autres types de noeuds...
    }

    // -----------------------------
    // Analyse assignation
    private void analyserAssignation(noeud_d_asignation a) {
        String nomVar = a.identificateur;
        String typeVariable = chercherTypeVariable(nomVar).type.base;
        if (typeVariable == null) {
            erreurs.add("Variable '" + nomVar + "' utilisée sans déclaration.");
            // On suppose "int" par défaut pour ne pas bloquer le reste
            typeVariable = "int";
            //pileScopes.peek().put(nomVar, typeVariable);
        }

        String typeExpression = getTypeExpression(a.valeur);
        if (!typeVariable.equals(typeExpression)) {
            erreurs.add("Type incompatible pour '" + nomVar + "': attendu " + typeVariable + ", trouvé " + typeExpression);
        }
    }

    // -----------------------------
    // Analyse condition
    private void analyserCondition(noeud_de_condition c) {
        String typeGauche = getTypeExpression(parcer.new noeud_de_valeur(/*c.gauche*/"s"));
        String typeDroite = getTypeExpression(parcer.new noeud_de_valeur(/*c.droite*/"s"));

        if (!typeGauche.equals(typeDroite)) {
            erreurs.add("Types incompatibles dans la condition: " + typeGauche + " " + c.operateur + " " + typeDroite);
        }

        /*if (c.reste != null) {
            analyserCondition(c.reste);
        }*/
    }

    // -----------------------------
    // Analyse méthode
    private void analyserMethode(MethodeNode m) {
        // Ajouter signature fonction
        List<Type> typesParams = new ArrayList<>();
        pileScopes.push(new HashMap<>()); // scope paramètres
        for (noeud_parametre p : m.Pretour) {
            //pileScopes.peek().put(p.nom, p.type);
            typesParams.add(new Type(p.type, p.dimension));
        }
        tableFonctions.put(m.nom, new FonctionInfo(new Type(m.typeRetour, m.dimention), typesParams));

        // Analyser instructions du corps
        for (noeud instr : m.instructions) {
            analyser(instr);
        }

        pileScopes.pop();
    }

    // -----------------------------
    // Chercher une variable dans tous les scopes (du plus local au global)
    public InfoVariable chercherTypeVariable(String nom) {
        for (int i = pileScopes.size() - 1; i >= 0; i--) {
            System.out.println(" Dans getTypeVariable, trouvé " + pileScopes.get(i));
            System.out.println("et on cherche :" + nom);
            if (pileScopes.get(i).containsKey(nom)) {
                return pileScopes.get(i).get(nom);
            }
        }
        return null;
    }
    
    public static InfoVariable chercherTypeVariable(String nom, Stack<Map<String,InfoVariable>> pileScopes) {
        for (int i = pileScopes.size() - 1; i >= 0; i--) {
            System.out.println(" Dans getTypeVariable, trouvé " + pileScopes.get(i));
            System.out.println("et on cherche :" + nom);
            if (pileScopes.get(i).containsKey(nom)) {
                System.out.println("Trouves. : " + pileScopes.get(i).get(nom));
                return pileScopes.get(i).get(nom);
            }
        }
        return null;
    }

    // -----------------------------
    // Obtenir le type d'une expression (simplifié)
    private String getTypeExpression(noeud expr) {
        if (expr instanceof noeud_de_valeur v) {
            // Détection basique du type : si c’est un nombre → int, sinon string
            if (v.valeur.matches("-?\\d+")) return "int";
            if (v.valeur.startsWith("\"") && v.valeur.endsWith("\"")) return "string";
            return chercherTypeVariable(v.valeur).type.base; // peut être variable
        }
        else if (expr instanceof noeud_d_operation_binaire op) {
            String typeG = getTypeExpression(op.gauche);
            String typeD = getTypeExpression(op.droite);
            if (!typeG.equals(typeD)) {
                erreurs.add("Types incompatibles dans l'opération: " + typeG + " " + op.operateur + " " + typeD);
            }
            return typeG; // suppose que le résultat a le type du côté gauche
        }
        else if (expr instanceof noeud_d_asignation a) {
            analyserAssignation(a);
            return chercherTypeVariable(a.identificateur).type.base;
        }
        return "int"; // valeur par défaut pour ne pas bloquer
    }
    
    static Type getTypeVariable(String nom, Stack<Map<String,InfoVariable>> pileScopes) {
        for (int i = pileScopes.size() - 1; i >= 0; i--) {
            System.out.println(" Dans getTypeVariable, trouvé " + pileScopes.get(i));
            System.out.println("et on cherche :" + nom);
            if (pileScopes.get(i).containsKey(nom)) {
                System.out.println("Trouves.");
                return pileScopes.get(i).get(nom).type;
            }
        }
        return null; // variable non déclarée
    }
    
    private static Map<String, InfoVariable> getScopeClasse(String nomClasse, Stack<Map<String, InfoVariable>> pileScopes, Map<String, Map<String, InfoVariable>> classesVariables, Map<String, Map<String, FonctionInfo>> classesMethodes) {
        // Parcours du scope global pour trouver la classe
        // Ici tu dois avoir une Map globale pour chaque classe déclarée
        // Exemple :
        // Map<String, Map<String, InfoVariable>> classes = ...;
        return classesVariables.getOrDefault(nomClasse, new HashMap<>());
    }

    private static Map<String, FonctionInfo> getTableFonctionsClasse(String nomClasse, Map<String, Map<String, InfoVariable>> classesVariables, Map<String, Map<String, FonctionInfo>> classesMethodes) {
        // Pareil pour les méthodes d'une classe
        return classesMethodes.getOrDefault(nomClasse, new HashMap<>());
    }



    // -----------------------------
    public List<String> getErreurs() {
        return erreurs;
    }

    
    

    public static void main(String[] args) {
        /*Scanner sc = new Scanner(System.in);

        System.out.println("Écris ton code Java simplifié (type if, for, assignation, méthode) :");
        String code = sc.nextLine();

        // --- 1️⃣ Lexer ---
        lexer lex = new lexer(code);
        List<token> tokens = lex.tokeniser();

        // --- 2️⃣ Parser ---
        parcer parser = new parcer(tokens);
        programmeNoeud programme = parser.parcerProgramme();*/
        
        Scanner sc = new Scanner(System.in);
        StringBuilder code = new StringBuilder();
        System.out.println("Écris ton code Java simplifié (type if, for, assignation, méthode) :");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) break; // une ligne vide pour terminer l'entrée
            code.append(line).append("\n");
        }

        lexer lex = new lexer(code.toString());
        List<token> tokens = lex.tokeniser();
        parcer parser = new parcer(tokens);
        programmeNoeud programme = parser.parcerProgramme();

        // --- 3️⃣ Afficher les erreurs syntaxiques ---
        if (!parser.getErreurs().isEmpty()) {
            System.out.println("=== ERREURS SYNTAXIQUES ===");
            for (String e : parser.getErreurs()) {
                System.out.println(e);
            }
        }

        // --- 4️⃣ Analyse sémantique ---
        Stack<Map<String, InfoVariable>> pileScopes = new Stack<>();
        pileScopes.push(new HashMap<>()); // scope global
        
        Stack<Contexte> pileContextes = new Stack<>(); // contexte
        
        Map<String, FonctionInfo> tableFonctions = new HashMap<>();
        
        // Pour chaque classe déclarée : nomClasse -> variables membres
        Map<String, Map<String, InfoVariable>> classesVariables = new HashMap<>();

        // Pour chaque classe déclarée : nomClasse -> méthodes
        Map<String, Map<String, FonctionInfo>> classesMethodes = new HashMap<>();

        semantique sem = new semantique();
        System.out.println("\n=== ANALYSE SEMANTIQUE ===");
        sem.analyserProgramme(programme, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);
    }

    // --- 5️⃣ Fonction pour analyser tout le programme ---
    public  programmeNoeud analyserProgramme(programmeNoeud n, Stack<Map<String,InfoVariable>> pileScopes, Stack<Contexte> pileContextes, Map<String, FonctionInfo> tableFonctions, Map<String, Map<String, InfoVariable>> classesVariables, Map<String, Map<String, FonctionInfo>> classesMethodes) {
        if (n instanceof programmeNoeud p) {
            for (noeud instr : p.mots) {
                System.out.println("Pile avant analyserinstruction de programme : " + pileScopes);
                
                
                analyserInstruction(instr, pileScopes, pileContextes, null, tableFonctions, classesVariables, classesMethodes, false);
            }
        }
        return n;
    }

    // --- 6️⃣ Analyser une instruction (assignation simple) ---
    public  void analyserInstruction(noeud instr,
                                        Stack<Map<String,InfoVariable>> pileScopes,
                                        Stack<Contexte> pileContextes,
                                        Type typeRetourAttendu,
                                        Map<String, FonctionInfo> tableFonctions,
                                        Map<String, Map<String, InfoVariable>> classesVariables, 
                                        Map<String, Map<String, FonctionInfo>> classesMethodes,
                                        boolean dansBloc) {
        
        if (instr instanceof noeud_return r) {
            
            
            if (typeRetourAttendu == null) {
                r.erreur = true;
                r.messageErreur = "dehors";
                System.out.println("Erreur : 'return' en dehors d'une méthode");
                erreurs.add("Erreur : 'return' en dehors d'une méthode");
                return;
            }
            Type typeRetourReel = analyserExpression(r.valeur, pileScopes, tableFonctions, classesVariables, classesMethodes);
            r.typeInfered =typeRetourReel;
            
            if (!typeRetourReel.equals(typeRetourAttendu)) {
                r.erreur = true;
                r.messageErreur = "" 
                    + typeRetourReel + " mais type déclaré " 
                    + typeRetourAttendu;
                System.out.println("Erreur : la méthode retourne " 
                    + typeRetourReel + " mais le type déclaré est " 
                    + typeRetourAttendu);
                erreurs.add("Erreur : la méthode retourne " 
                    + typeRetourReel + " mais le type déclaré est " 
                    + typeRetourAttendu);
            }
        }
        else if (instr instanceof noeud_d_asignation a) {
            
            System.out.println("dans noeud_d_assignation");
            Type typeGauche = null;
            
            // temporaire juste pour regarder si ca arrange un bug : update true
            analyserExpression(a.cible, pileScopes, tableFonctions, classesVariables, classesMethodes);
            
            if(a.cible==null){
                typeGauche = getTypeVariable(a.identificateur, pileScopes);
                if(a.identificateur ==null){
                    a.identificateur = typeGauche.base;
                }
                if(typeGauche!=null){
                    if(typeGauche.immutable){
                        a.erreur = true;
                        a.messageErreur = "constante";
                        System.out.println("Erreur tentatif de changer une constante");
                        erreurs.add("Erreur tentatif de changer une constante");
                    }
                }
                
            } else if(a.cible instanceof NoeudAccesTableau t){
                // Cas tableau : accéder à un élément
                System.out.println("le nom du tableau est :   " +t.nom);
                if("".equals(a.identificateur)){
                    a.identificateur = t.nom;
                }
                Type tTableau = getTypeVariable(a.identificateur, pileScopes);
                if(a.identificateur ==null){
                    a.identificateur = tTableau.base;
                }
                //System.out.println("Erreur tentatif de changer une constante");
                if(tTableau!=null){
                    if(tTableau.immutable){
                        a.erreur = true;
                        a.messageErreur = "constante";
                        System.out.println("Erreur tentatif de changer une constante");
                        erreurs.add("Erreur tentatif de changer une constante");
                    }
                }
                
                if (tTableau == null) {
                    a.erreur = true;
                    a.messageErreur = "tableau non déclaré";
                    System.out.println("Erreur : tableau non déclaré → " + a.identificateur);
                    erreurs.add("Erreur : tableau non déclaré → " + a.identificateur);
                    typeGauche = new Type("ERREUR", 0);
                } else {
                    if (t.indices.size() > tTableau.dimension) {
                        a.erreur = true;
                        a.messageErreur = "trop d'indices " + a.identificateur;
                        System.out.println("Erreur : trop d'indices pour " + a.identificateur);
                        erreurs.add("Erreur : trop d'indices pour " + a.identificateur);
                        typeGauche = new Type("ERREUR", 0);
                    } else {
                        // Vérifier que tous les indices sont des int
                        for (noeud idx : t.indices) {
                            Type tIdx = analyserExpression(idx, pileScopes, tableFonctions, classesVariables, classesMethodes);
                            if (!tIdx.base.equals("int") || tIdx.dimension != 0) {
                                a.erreur = true;
                                a.messageErreur = "index tableau " + tIdx;
                                System.out.println("Erreur : index de tableau doit être int, trouvé " + tIdx);
                                erreurs.add("Erreur : index de tableau doit être int, trouvé " + tIdx);
                            }
                        }
                        // Type de l'élément ciblé dans le tableau
                        int dimRestante = tTableau.dimension - t.indices.size();
                        if (dimRestante < 0) dimRestante = 0; // sécurité
                        typeGauche = new Type(tTableau.base, dimRestante);
                    }
                }
            } else if (a.cible instanceof noeud_de_valeur v){
                System.out.println("blud");
                a.identificateur = v.valeur;
                typeGauche = getTypeVariable(a.identificateur, pileScopes);
            }
            InfoVariable blud = chercherTypeVariable(a.identificateur, pileScopes);
            
            Type typeDroite = analyserExpression(a.valeur, pileScopes, tableFonctions, classesVariables, classesMethodes);
            if (typeGauche == null) {
                a.erreur = true;
                a.messageErreur = "variable non déclarée " + a.identificateur;
                erreurs.add("Erreur : variable non déclarée → " + a.identificateur);
                System.out.println("TS  :" + a.identificateur + "  and  " + a.cible +"  is");
                System.out.println("all good   :  " + typeGauche + "  ;  "+ typeDroite);
                System.out.println("Erreur : variable non déclarée → " + a.identificateur);
            } else if (!typeGauche.equals(typeDroite)) {
                a.erreur = true;
                a.messageErreur =  typeGauche + " mais " + typeDroite;
                System.out.println("Erreur de type : " + a.identificateur + " est " + typeGauche + " mais affecté avec " + typeDroite);
                erreurs.add("Erreur de type : " + a.identificateur + " est " + typeGauche + " mais affecté avec " + typeDroite);
            } else if(!blud.initialisee){
                
            }else {
                System.out.println("TS  :" + a.identificateur + "  and  " + a.cible +"  is");
                System.out.println("all good   :  " + typeGauche + "  ;  "+ typeDroite);
            }
        } 
        else if (instr instanceof MethodeNode m) {
            
            // On crée une liste des types des paramètres
            List<Type> typesParams = new ArrayList<>();
            for (parcer.noeud_parametre p : m.Pretour) {
                typesParams.add(new Type(p.type, p.dimension)); // dimension = 0 pour les simples
            }
            // Ajouter à tableFonctions
            tableFonctions.put(m.nom, new FonctionInfo(new Type(m.typeRetour, m.dimention), typesParams));

            // Ajouter les paramètres au scope
            pileScopes.push(new HashMap<>());
            for (parcer.noeud_parametre p : m.Pretour) {
                pileScopes.peek().put(p.nom, new InfoVariable(new Type(p.type, p.dimension), false));
            }
            // Analyser le corps
            for (noeud i : m.instructions) {
                analyserInstruction(i, pileScopes, pileContextes, new Type(m.typeRetour, m.dimention), tableFonctions, classesVariables, classesMethodes, true);
            }
            pileScopes.pop();
            System.out.println("yippy!!   " );
        }
        else if (instr instanceof VariableNode decl) {
            // Ajouter la variable dans le scope
            
            System.out.println("donc    : " + dansBloc);
            System.out.println("et    : " + decl.type.toString());
            if (dansBloc) {
                // Déclaration locale → dans le nouveau scope
                //pileScopes.push(new HashMap<>());
                pileScopes.peek().put(decl.nom, new InfoVariable(decl.type, decl.initialisation) );
                //pileScopes.pop();
            } else {
                // Déclaration globale → ajouter directement au scope global
                pileScopes.firstElement().put(decl.nom, new InfoVariable(decl.type, decl.initialisation));
            }
            
            if(decl.immutable){
                decl.type.immutable = true;
            }
            
            System.out.println("en plus de     : " + decl.type.immutable);
            
            
            
            
            //pileScopes.peek().put(decl.nom, decl.type);
            //getTypeVariable(typeRetourAttendu.base, pileScopes);
            System.out.println("Déclaration : " + decl.nom + " de type " + decl.type + " ajoutée au scope.");

            System.out.println("\nPile après declaration : " + pileScopes);
            decl.typeInfered = decl.type;
            
            // --- Vérifier le type si une valeur initiale est donnée ---
            if (decl.valeur != null) {
                Type typeValeur = analyserExpression(decl.valeur, pileScopes, tableFonctions, classesVariables, classesMethodes);
                System.out.println("Pile après analyserExpression de variablenode: " + pileScopes);
                if (!typeValeur.equals(decl.type)) {
                    
                    decl.erreur =true;
                    decl.messageErreur = "" + decl.type + " + " + typeValeur;
                    System.out.println("Erreur de type : " + decl.nom + " est " + decl.type + " mais affecté avec " + typeValeur);
                    erreurs.add("Erreur de type : " + decl.nom + " est " + decl.type + " mais affecté avec " + typeValeur);
                }
            }
        }
        else if (instr instanceof noeud_si siNode) {
            analyserSi(siNode, typeRetourAttendu, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);
            
        } 
        else if (instr instanceof noeud_while tant){
            Type typeCond = analyserExpression(tant.condition, pileScopes, tableFonctions, classesVariables, classesMethodes);
            if (!typeCond.estBoolean()) {
                tant.erreur = true;
                tant.messageErreur = "doit être boolean" + typeCond;
                System.out.println("Erreur : condition du while doit être boolean, trouvé " + typeCond);
                erreurs.add("Erreur : condition du while doit être boolean, trouvé " + typeCond);
            }
            
            pileContextes.push(Contexte.BOUCLE);
            pileScopes.push(new HashMap<>());
            for(noeud instrThen : tant.block.mots){
                analyserInstruction(instrThen, pileScopes, pileContextes,typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
            }
            pileScopes.pop();
            pileContextes.pop();
            
        } 
        else if (instr instanceof noeud_do dos){
            pileContextes.push(Contexte.BOUCLE);
            pileScopes.push(new HashMap<>());
            for(noeud instrThen : dos.block.mots){
                analyserInstruction(instrThen, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
            }
            pileScopes.pop();
            pileContextes.pop();
            
            Type typeCond = analyserExpression(dos.condition.condition, pileScopes, tableFonctions, classesVariables, classesMethodes);
            if (!typeCond.estBoolean()) {
                dos.erreur = true;
                dos.messageErreur = "doit être boolean" + typeCond;
                System.out.println("Erreur : condition du do-while doit être boolean, trouvé " + typeCond);
                erreurs.add("Erreur : condition du do-while doit être boolean, trouvé " + typeCond);
            }
        } 
        else if (instr instanceof noeud_for form){

            // Nouveau scope du for
            pileContextes.push(Contexte.BOUCLE);
            pileScopes.push(new HashMap<>());

            noeud_compteur c = form.compteur;

            // 1️⃣ Déclarer la variable compteur
            pileScopes.peek().put(c.variable, new InfoVariable(c.type, c.valeurDepart ==null ? false : true));

            // 2️⃣ Vérifier type valeur de départ
            Type typeDepart = analyserExpression(c.valeurDepart, pileScopes,tableFonctions, classesVariables, classesMethodes);
            if (!typeDepart.equals(c.type)) {
                c.erreur = true;
                c.messageErreur = c.type + " mais valeur initiale " + typeDepart;
                System.out.println("Erreur : compteur '" + c.variable +
                    "' est de type " + c.type + " mais valeur initiale est " + typeDepart);
                erreurs.add("Erreur : compteur '" + c.variable +
                    "' est de type " + c.type + " mais valeur initiale est " + typeDepart);
            }

            // 3️⃣ Vérifier condition (limite)
            Type typeCond = analyserExpression(c.limite, pileScopes,tableFonctions, classesVariables, classesMethodes);
            if (!typeCond.estBoolean()) {
                c.erreur = true;
                c.messageErreur = "booleen";
                System.out.println("Erreur : condition du compteur de for doit être boolean, trouvé " + typeCond);
                erreurs.add("Erreur : condition du compteur de for doit être boolean, trouvé " + typeCond);
            }

            // 4️⃣ Vérifier le pas (incrémentation)
            analyserInstruction(c.pas, pileScopes, pileContextes,typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);

            // 5️⃣ Analyser le bloc
            for(noeud instrThen : form.block.mots){
                analyserInstruction(instrThen, pileScopes, pileContextes,typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
            }

            pileScopes.pop();
            pileContextes.pop();
        } 
        else if (instr instanceof ClasseNode cls) { // en faite c'est pas un probleme ici c'est juste que ca purge pas l'ancien classesvariable alors fait le 
            if (classesVariables.containsKey(cls.nom)) {
                cls.erreur = true;
                cls.messageErreur = "déjà déclarée → " + cls.nom;
                System.out.println("Erreur : classe déjà déclarée → " + cls.nom);
                erreurs.add("Erreur : classe déjà déclarée → " + cls.nom);
                return;
            }

            cls.typeInfered = new Type(cls.nom, 0);
            // Création d'un scope propre pour la classe
            Map<String, InfoVariable> champs = new HashMap<>();
            Map<String, FonctionInfo> methodes = new HashMap<>();

            // Analyser les membres de la classe
            for (noeud membre : cls.membres) {
                if (membre instanceof VariableNode v) {
                    champs.put(v.nom, new InfoVariable(v.type, v.initialisation));
                } else if (membre instanceof MethodeNode m) {
                    List<Type> typesParams = new ArrayList<>();
                    for (parcer.noeud_parametre p : m.Pretour) {
                        typesParams.add(new Type(p.type, p.dimension));
                    }
                    methodes.put(m.nom, new FonctionInfo(new Type(m.typeRetour, m.dimention), typesParams));
                }
            }

            // Stocker dans les maps globales
            System.out.println("on a ajoutes la classe ici ->==========================================================================");
            classesVariables.put(cls.nom, champs);
            classesMethodes.put(cls.nom, methodes);

            // Ajouter le nom de la classe dans le scope global pour qu’on puisse déclarer des variables de type classe
            pileScopes.firstElement().put(cls.nom, new InfoVariable(new Type("classe", 0), false));
        }

        /*else if (instr instanceof ClasseNode cls) {
            // Nouveau scope pour la classe
            //pileScopes.push(new HashMap<>());
            
             Map<String, InfoVariable> global = pileScopes.firstElement();

            if (global.containsKey(cls.nom)) {
                System.out.println("Erreur : classe déjà déclarée → " + cls.nom);
                return;
            }

            // Enregistrer la classe
            //global.put(cls.nom, "classe");

            // Ajouter le nom de la classe dans le scope global
            pileScopes.firstElement().put(cls.nom, new InfoVariable(new Type("classe", 0), false));

            // Analyser toutes les méthodes et variables membres
            for (noeud membre : cls.membres) { // membres = List<noeud> contenant VariableNode et MethodeNode
                analyserInstruction(membre, pileScopes, pileContextes,typeRetourAttendu, tableFonctions, true);
            }

            //pileScopes.pop();
        }*/
        else if (instr instanceof noeud_declaration_multiple mulVar){
            pileScopes.push(new HashMap<>());
            for (noeud instrThen : mulVar.variables) {
                analyserInstruction(instrThen, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, dansBloc);
            }
            pileScopes.pop();
        }
        else if (instr instanceof noeud_switch swit) {

            // 1) Type de l'expression du switch
            Type typeSwitch = analyserExpression(swit.expression, pileScopes, tableFonctions, classesVariables, classesMethodes);

            // 2) Nouveau scope pour le switch
            pileContextes.push(Contexte.SWITCH);
            pileScopes.push(new HashMap<>());

            for (noeud_case c : swit.cases) {

                // 3) Vérifier que le type du case correspond
                Type typeCase = analyserExpression(c.valeur, pileScopes, tableFonctions, classesVariables, classesMethodes);
                if (!typeSwitch.equals(typeCase)) {
                    c.erreur = true;
                    c.messageErreur = typeCase + " incompatible " + typeSwitch;
                    System.out.println("Erreur : case de type " + typeCase +
                                       " incompatible avec switch de type " + typeSwitch);
                    erreurs.add("Erreur : case de type " + typeCase +
                                       " incompatible avec switch de type " + typeSwitch);
                }

                // 4) Scope du case
                pileScopes.push(new HashMap<>());

                for (noeud instrCase : c.instructions) {
                    analyserInstruction(instrCase, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
                }

                pileScopes.pop();
            }

            // 5) default
            if (swit.defaul != null) {
                pileScopes.push(new HashMap<>());
                for (noeud instrDef : swit.defaul.instructions) {
                    analyserInstruction(instrDef, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
                }
                pileScopes.pop();
            }

            pileScopes.pop(); // fin du switch
            pileContextes.pop();
        }
        else if (instr instanceof noeud_break brk) {

            if (pileContextes.isEmpty()) {
                brk.erreur = true;
                brk.messageErreur = "hors boucle";
                System.out.println("Erreur : 'break' hors d'une boucle ou d'un switch");
                erreurs.add("Erreur : 'break' hors d'une boucle ou d'un switch");
            } else {
                // OK, break valide
            }
        }
        else if (instr instanceof noeud_continue ctn){
            
            if (pileContextes.isEmpty()) {
                ctn.erreur = true;
                ctn.messageErreur = "hors boucle";
                System.out.println("Erreur : 'continue' hors d'une boucle ou d'un switch");
                erreurs.add("Erreur : 'continue' hors d'une boucle ou d'un switch");
            }
            else {
                // OK, break valide
            }
        }
        else if (instr instanceof noeud_constante cnst){
            
                analyserInstruction(cnst.valeur, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, dansBloc);
            
        }
        else if (instr instanceof noeud_appel_methode apm){
            Type tr = analyserExpression(instr, pileScopes, tableFonctions, classesVariables, classesMethodes);
            System.out.println("yeeehaw    :" + tr);
        }
        else if (instr instanceof NoeudAccesChamp || instr instanceof NoeudAppelMethodeObjet) {
            analyserExpression((noeud) instr, pileScopes, tableFonctions, classesVariables, classesMethodes);
        }

        /*else if (expr instanceof NoeudAccesTableau t) {
            Type typeVar = chercherVariable(t.nom, pileScopes);

            if (typeVar == null) {
                erreur("Variable non déclarée : " + t.nom);
                return Type.ERREUR;
            }

            // Vérifier dimensions
            if (t.indices.size() > typeVar.dimension) {
                erreur("Trop d'indices pour le tableau " + t.nom);
                return Type.ERREUR;
            }

            // Vérifier que chaque indice est int
            for (noeud idx : t.indices) {
                Type ti = analyserExpression(idx, pileScopes);
                if (!ti.base.equals("int") || ti.dimension != 0) {
                    erreur("Indice de tableau doit être int");
                }
            }

            // Type résultant
            return new Type(typeVar.base, typeVar.dimension - t.indices.size());
        }*/
        else{
        System.out.println("pas passes   : " +instr );}


        // Tu peux ajouter if, for, while ici aussi
    }
    
    /*public static void analyserInstruction(noeud instr,
                                        Stack<Map<String,String>> pileScopes,
                                        String typeRetourAttendu) {

        if (instr instanceof noeud_return r) {
            String typeRetourReel = analyserExpression(r.valeur, pileScopes);

            if (!typeRetourAttendu.equals(typeRetourReel)) {
                System.out.println("Erreur : la méthode retourne " 
                    + typeRetourReel + " mais le type déclaré est " 
                    + typeRetourAttendu);
            }
        }

        // les autres cas (if, for, assignation, etc.)
    }*/

    
    private  void analyserSi(noeud_si siNode, Type typeRetourAttendu, Stack<Map<String,InfoVariable>> pileScopes, Stack<Contexte> pileContextes, Map<String, FonctionInfo> tableFonctions, Map<String, Map<String, InfoVariable>> classesVariables, Map<String, Map<String, FonctionInfo>> classesMethodes) {
        // Condition du if
//        System.out.println("sur le point d'entrer dans la condision" + siNode.condition.toElString());
        System.out.println("Pile avant entrer dans noeud_condition de analyserExpression: " + pileScopes);
        Type typeCond = analyserExpression(siNode.condition, pileScopes, tableFonctions, classesVariables, classesMethodes);
        if (!typeCond.estBoolean()) {
            siNode.erreur = true;
            siNode.messageErreur = "condition doit être boolean";
            System.out.println("Erreur : condition du if doit être boolean, trouvé " + typeCond);
            erreurs.add("Erreur : condition du if doit être boolean, trouvé " + typeCond);
        }

        // Bloc then
        pileContextes.push(Contexte.BOUCLE);
        pileScopes.push(new HashMap<>());
        for (noeud instrThen : siNode.blockAlors.mots) {
            analyserInstruction(instrThen, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
        }
        pileScopes.pop();
        
        /*Map<String, String> nouveauScope = new HashMap<>(pileScopes.peek());
        
        nouveauScope.put("x", "int");
        
        pileScopes.push(nouveauScope);
        
        System.out.println("essaie de voir si c'est vraiment rentres " + getTypeVariable("x", pileScopes));
        System.out.println("Pile avant bloc: " + pileScopes);

        for (noeud instrThen : siNode.blockAlors.mots) {
            analyserInstruction(instrThen, pileScopes, null, true);
        }
        System.out.println("Pile après bloc: " + pileScopes);

        pileScopes.pop();*/

        // Else if
        if (siNode.block_sinonSi != null) {
            analyserSinonSi(siNode.block_sinonSi, typeRetourAttendu, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);
        }

        // Else final
        if (siNode.block_sinon != null) {
            pileScopes.push(new HashMap<>());
            for (noeud instrElse : siNode.block_sinon.mots) {
                analyserInstruction(instrElse, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
            }
            pileScopes.pop();
        }
        pileContextes.pop();
    }

    private  void analyserSinonSi(noeud_sinonSi n, Type typeRetourAttendu, Stack<Map<String,InfoVariable>> pileScopes, Stack<Contexte> pileContextes, Map<String, FonctionInfo> tableFonctions, Map<String, Map<String, InfoVariable>> classesVariables, Map<String, Map<String, FonctionInfo>> classesMethodes) {
        Type typeCond = analyserExpression(n.condition, pileScopes, tableFonctions, classesVariables, classesMethodes);
        if (!typeCond.estBoolean()) {
            n.erreur = true;
            n.messageErreur = "doit être boolean";
            System.out.println("Erreur : condition du else if doit être boolean, trouvé " + typeCond);
            erreurs.add("Erreur : condition du else if doit être boolean, trouvé " + typeCond);
        }

        pileScopes.push(new HashMap<>());
        for (noeud instrThen : n.blockAlors.mots) {
            analyserInstruction(instrThen, pileScopes, pileContextes, typeRetourAttendu, tableFonctions, classesVariables, classesMethodes, true);
        }
        pileScopes.pop();

        if (n.block_sinonSi != null) {
            analyserSinonSi(n.block_sinonSi, typeRetourAttendu, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);
        }

        if (n.block_sinon != null) {
            pileScopes.push(new HashMap<>());
            for (noeud instrElse : n.block_sinon.mots) {
                analyserInstruction(instrElse, pileScopes, pileContextes, null, tableFonctions, classesVariables, classesMethodes, true);
            }
            pileScopes.pop();
        }
    }


    // --- 7️⃣ Analyse d'expressions simples ---
    public  Type analyserExpression(noeud expr, Stack<Map<String,InfoVariable>> pileScopes, Map<String, FonctionInfo> tableFonctions, Map<String, Map<String, InfoVariable>> classesVariables, Map<String, Map<String, FonctionInfo>> classesMethodes) {
        if (expr instanceof noeud_de_valeur v) {
            String val = v.valeur;
            //v.typeInfered = new Type("blud", 0);
            if (val==null)return new Type("null", 0);
            if (val.startsWith("\"") && val.endsWith("\"")) return new Type("String", 0);
            if (val.equals("true") || val.equals("false")) return new Type("boolean", 0);
            try { Integer.parseInt(val); return new Type("int", 0); } catch (Exception e) {}
            try { Double.parseDouble(val); return new Type("double", 0); } catch (Exception e) {}
            /*if (pileScopes.peek().containsKey(v.valeur)) {
                return pileScopes.peek().get(v.valeur);
            } else {
                System.out.println("Erreur : variable non déclarée " + v.valeur);
                return "ERREUR";
            }*/
            System.out.println("Pile dans le noeud_variable dans analyserExpression : " + pileScopes);

            InfoVariable blud = chercherTypeVariable(v.valeur, pileScopes);
            if(blud!=null){
                if(!blud.initialisee){
                    v.erreur = true;
                    v.messageErreur= "non inicialisée " + v.valeur;
                    System.out.println("Erreur : variable non inicialisée " + v.valeur);
                    erreurs.add("Erreur : variable non inicialisée " + v.valeur);
                }
            }
            
            Type t = getTypeVariable(v.valeur, pileScopes);
            v.typeInfered = t;
            if (t != null) {
                return t;
            } else {
                v.erreur = true;
                v.messageErreur= "non déclarée " + v.valeur;
                System.out.println("Erreur : variable non déclarée " + v.valeur);
                erreurs.add("Erreur : variable non déclarée " + v.valeur);
                return new Type("ERREUR", 0);
            }
        } 
        else if (expr instanceof noeud_d_operation_binaire op) {
            Type tG = analyserExpression(op.gauche, pileScopes, tableFonctions, classesVariables, classesMethodes);
            Type tD = analyserExpression(op.droite, pileScopes, tableFonctions, classesVariables, classesMethodes);
            if (op.operateur == TokenType.GT || op.operateur == TokenType.LT 
                || op.operateur == TokenType.GTE || op.operateur == TokenType.LTE
                || op.operateur == TokenType.EQEQ || op.operateur == TokenType.NOTEQ) {
                if (!tG.base.equals(tD.base) || tG.dimension != tD.dimension) {
                    op.erreur = true;
                    op.messageErreur = tG + " vs " + tD;
                    System.out.println("Erreur de type dans la condition : " + tG + " vs " + tD);
                    erreurs.add("Erreur de type dans la condition : " + tG + " vs " + tD);
                    return new Type("ERREUR", 0);
                }
                return new Type("boolean", 0); // le résultat d'une comparaison est toujours boolean
            }  else if (op.operateur == TokenType.AND || op.operateur == TokenType.OR) {
                if (!tG.estBoolean() || !tD.estBoolean()) {
                    //erreurs.add("Erreur : opérandes logiques doivent être boolean, trouvé " + tG + " et " + tD);
                    op.erreur = true;
                    op.messageErreur = tG + " vs " + tD;
                    System.out.println("Erreur : opérandes logiques doivent être boolean, trouvé " + tG + " et " + tD);
                    erreurs.add("Erreur : opérandes logiques doivent être boolean, trouvé " + tG + " et " + tD);
                    return new Type("ERREUR", 0);
                }
                return new Type("boolean", 0);

            // Arithmétiques
            } else {
                // opération arithmétique classique
                if (!tG.base.equals(tD.base) || tG.dimension != tD.dimension) {
                    op.erreur = true;
                    op.messageErreur = tG + " vs " + tD;
                    System.out.println("Erreur de type dans l'opération : " + tG + " vs " + tD);
                    erreurs.add("Erreur de type dans l'opération : " + tG + " vs " + tD);
                    return new Type("ERREUR", 0);
                }
                return tG;
            }
        }
        /* else if (expr instanceof noeud_de_condition cond) {
            // Analyser la condition : on suppose ici qu'elle contient gauche, droite et opérateur
            Type tG = analyserExpression(new noeud_de_valeur("cond.gauche"), pileScopes);
            Type tD = analyserExpression(new noeud_de_valeur("cond.droite"), pileScopes);

            if (!tG.base.equals(tD.base) || tG.dimension != tD.dimension) {
                System.out.println("Erreur de type dans la condition : " + tG + " vs " + tD);
                return new Type("ERREUR", 0);
            }

            // Comparaison → toujours boolean
            return new Type("boolean", 0);
        }*//*else if (expr instanceof noeud_de_condition cond) {
            // --- Cas NOT ---
            if (cond.operateur == TokenType.NOT) {
                Type t = analyserExpression(cond.droiteExpr, pileScopes);
                if (!t.estBoolean()) {
                    System.out.println("il's sont : " + cond.gaucheExpr + "  et  " + cond.droiteExpr);
                    System.out.println("Erreur : opérande de '!' doit être boolean, trouvé " + t);
                    return new Type("ERREUR", 0);
                }
                return new Type("boolean", 0);
            }

            // --- Cas AND / OR ---
            if (cond.operateur == TokenType.AND || cond.operateur == TokenType.OR) {
                Type tG = analyserExpression(cond.gaucheExpr, pileScopes);
                Type tD = analyserExpression(cond.droiteExpr, pileScopes);

                if (!tG.estBoolean() || !tD.estBoolean()) {
                    System.out.println("il's sont : " + cond.gaucheExpr + "  et  " + cond.droiteExpr);
                    System.out.println("Erreur : opérandes de " + cond.operateur + " doivent être boolean, trouvés " + tG + " et " + tD);
                    return new Type("ERREUR", 0);
                }

                return new Type("boolean", 0);
            }

            // --- Cas comparaisons ---
            if (cond.operateur == TokenType.EQEQ || cond.operateur == TokenType.NOTEQ ||
                cond.operateur == TokenType.GT || cond.operateur == TokenType.LT ||
                cond.operateur == TokenType.GTE || cond.operateur == TokenType.LTE) {

                Type tG = analyserExpression(cond.gaucheExpr, pileScopes);
                Type tD = analyserExpression(cond.droiteExpr, pileScopes);

                // On compare seulement des types compatibles et même dimension
                if (!tG.base.equals(tD.base) || tG.dimension != tD.dimension) {
                    System.out.println("il's sont : " + cond.gaucheExpr + "  et  " + cond.droiteExpr);
                    System.out.println("Erreur de type dans la condition : " + tG + " vs " + tD);
                    return new Type("ERREUR", 0);
                }

                return new Type("boolean", 0);
            }

            // --- Cas simple : juste une expression booléenne ---
            Type t = analyserExpression(cond.gaucheExpr, pileScopes);
            if (!t.estBoolean()) {
                System.out.println("Erreur : expression doit être boolean, trouvé " + t);
                return new Type("ERREUR", 0);
            }
            return new Type("boolean", 0);
        }*/
        else if (expr instanceof noeud_de_condition cond) {
            Type tG = null;
            Type tD = null;

            switch (cond.operateur) {
                case NOT -> {
                    // NOT n'a que la droite
                    tD = analyserExpression(cond.droiteExpr, pileScopes, tableFonctions, classesVariables, classesMethodes); // si c'est un noeud_de_condition, on descend dedans
                    if (tD == null || !tD.estBoolean()) {
                        cond.erreur = true;
                        cond.messageErreur = "doit être boolean" + tD;
                        System.out.println("il's sont : " + cond.gaucheExpr + "  et  " + cond.droiteExpr + "    et   " + cond.suite);
                        System.out.println("Erreur : opérande de '!' doit être boolean, trouvé " + tD);
                        erreurs.add("Erreur : opérande de '!' doit être boolean, trouvé " + tD);
                        return new Type("ERREUR", 0);
                    }cond.typeInfered = new Type("boolean", 0);
                    return new Type("boolean", 0);
                }
                case AND, OR -> {
                    tG = analyserExpression(cond.gaucheExpr, pileScopes, tableFonctions, classesVariables, classesMethodes);
                    tD = analyserExpression(cond.droiteExpr, pileScopes, tableFonctions, classesVariables, classesMethodes);
                    if (tG == null || tD == null || !tG.estBoolean() || !tD.estBoolean()) {
                        cond.erreur = true;
                        cond.messageErreur = "doit être boolean" + tG + " et " + tD;
                        System.out.println("il's sont : " + cond.gaucheExpr + "  et  " + cond.droiteExpr + "    et   " + cond.suite);
                        System.out.println("Erreur : opérandes de " + cond.operateur + " doivent être boolean, trouvés " + tG + " et " + tD);
                        erreurs.add("Erreur : opérandes de " + cond.operateur + " doivent être boolean, trouvés " + tG + " et " + tD);
                        return new Type("ERREUR", 0);
                    }cond.typeInfered = new Type("boolean", 0);
                    return new Type("boolean", 0);
                }
                case EQEQ, NOTEQ, GT, LT, GTE, LTE -> {
                    tG = analyserExpression(cond.gaucheExpr, pileScopes, tableFonctions, classesVariables, classesMethodes);
                    tD = analyserExpression(cond.droiteExpr, pileScopes, tableFonctions, classesVariables, classesMethodes);
                    if (tG == null || tD == null || !tG.base.equals(tD.base) || tG.dimension != tD.dimension) {
                        cond.erreur = true;
                        cond.messageErreur = "doit être boolean" + tG + " et " + tD;
                        System.out.println("il's sont : " + cond.gaucheExpr + "  et  " + cond.droiteExpr + "    et   " + cond.suite);
                        System.out.println("Erreur de type dans la condition : " + tG + " vs " + tD);
                        erreurs.add("Erreur de type dans la condition : " + tG + " vs " + tD);
                        return new Type("ERREUR", 0);
                    }cond.typeInfered = new Type("boolean", 0);
                    return new Type("boolean", 0);
                }
                default -> {
                    // Expression simple : si gaucheExpr est un noeud_de_condition, on descend dedans
                    if (cond.gaucheExpr != null){
                        return analyserExpression(cond.gaucheExpr, pileScopes, tableFonctions, classesVariables, classesMethodes);
                    }else if (cond.droiteExpr != null){
                        return analyserExpression(cond.droiteExpr, pileScopes, tableFonctions, classesVariables, classesMethodes);
                    }else{
                        cond.erreur = true;
                        cond.messageErreur = "doit être boolean";
                        erreurs.add("doit être boolean");
                        return new Type("ERREUR", 0);
                    }
                }
            }
        }
        else if (expr instanceof NoeudAccesTableau t) {
            Type typeVar = getTypeVariable(t.nom, pileScopes);

            if (typeVar == null) {
                //erreur("Variable non déclarée : " + t.nom);
                t.erreur = true;
                t.messageErreur = "Variable non déclarée : " + t.nom;
                System.out.println("Variable non déclarée : " + t.nom);
                erreurs.add("Variable non déclarée : " + t.nom);
                return new Type("ERREUR", 0);
            }

            // Vérifier dimensions
            if (t.indices.size() > typeVar.dimension) {
                //erreur("Trop d'indices pour le tableau " + t.nom);
                t.erreur = true;
                t.messageErreur = "Trop d'indices pour : " + t.nom;
                System.out.println("Trop d'indices pour le tableau " + t.nom);
                erreurs.add("Trop d'indices pour le tableau " + t.nom);
                return new Type("ERREUR", 0);
            }

            // Vérifier que chaque indice est int
            for (noeud idx : t.indices) {
                Type ti = analyserExpression(idx, pileScopes, tableFonctions, classesVariables, classesMethodes);
                if (!ti.base.equals("int") || ti.dimension != 0) {
                    t.erreur = true;
                    t.messageErreur = "Indice tableau doit être int";
                    //erreur("Indice de tableau doit être int");
                    erreurs.add("Indice de tableau doit être int");
                    System.out.println("Indice de tableau doit être int");
                }
            }

            // Type résultant
            return new Type(typeVar.base, typeVar.dimension - t.indices.size());
        } 
        else if (expr instanceof NoeudCreationTableau t) {
            // Le type est le type de base avec la dimension = tailles.size()
            return new Type(t.typeBase, t.tailles.size());
        }
        else if (expr instanceof noeud_appel_methode appel) {
            // 1️⃣ Vérifier que la méthode existe
            if (!tableFonctions.containsKey(appel.nom)) {
                appel.erreur = true;
                appel.messageErreur = "méthode '" + appel.nom + "' non déclarée.";
                System.out.println("Erreur : méthode '" + appel.nom + "' non déclarée.");
                erreurs.add("Erreur : méthode '" + appel.nom + "' non déclarée.");
                return new Type("ERREUR", 0);
            }

            FonctionInfo fInfo = tableFonctions.get(appel.nom);

            // 2️⃣ Vérifier le nombre d’arguments
            if (appel.arguments.size() != fInfo.typesParam.size()) {
                appel.erreur = true;
                appel.messageErreur = "nombre d'arguments incorrect pour " + appel.nom +
                                   ". Attendu " + fInfo.typesParam.size() +
                                   ", trouvé " + appel.arguments.size();
                System.out.println("Erreur : nombre d'arguments incorrect pour " + appel.nom +
                                   ". Attendu " + fInfo.typesParam.size() +
                                   ", trouvé " + appel.arguments.size());
                erreurs.add("Erreur : nombre d'arguments incorrect pour " + appel.nom +
                                   ". Attendu " + fInfo.typesParam.size() +
                                   ", trouvé " + appel.arguments.size());
                return new Type("ERREUR", 0);
            }

            // 3️⃣ Vérifier les types
            for (int i = 0; i < appel.arguments.size(); i++) {
                Type tArg = analyserExpression(appel.arguments.get(i), pileScopes, tableFonctions, classesVariables, classesMethodes);
                Type tParam = fInfo.typesParam.get(i);

                if (!tArg.base.equals(tParam.base) || tArg.dimension != tParam.dimension) {
                    appel.erreur = true;
                    appel.messageErreur = "type argument " + (i+1) + " pour " + appel.nom +
                                       " attendu " + tParam + ", trouvé " + tArg;
                    System.out.println("Erreur : type de l'argument " + (i+1) + " pour " + appel.nom +
                                       " attendu " + tParam + ", trouvé " + tArg);
                    erreurs.add("Erreur : type de l'argument " + (i+1) + " pour " + appel.nom +
                                       " attendu " + tParam + ", trouvé " + tArg);
                    
                    return new Type("ERREUR", 0);
                }
            }

            // 4️⃣ Retourner le type de retour de la méthode
            return new Type(fInfo.typeRetour.base, fInfo.typeRetour.dimension);
        }
        /*else if (expr instanceof NoeudAccesChamp nac) {
            // 1) Vérifier le type de l'objet
            Type tObjet = analyserExpression(nac.objet, pileScopes, tableFonctions);

            if (tObjet == null || tObjet.base.equals("ERREUR")) {
                return new Type("ERREUR", 0);
            }

            // 2) Vérifier que c'est bien une classe
            if (!pileScopes.firstElement().containsKey(tObjet.base)) {
                System.out.println("Erreur : classe non déclarée → " + tObjet.base);
                return new Type("ERREUR", 0);
            }

            // 3) Vérifier que le champ existe
            // Ici, on peut simuler la recherche du champ dans la classe
            Map<String, InfoVariable> classeScope = getScopeClasse(tObjet.base, pileScopes);
            if (!classeScope.containsKey(nac.champ)) {
                System.out.println("Erreur : champ '" + nac.champ + "' non déclaré dans " + tObjet.base);
                return new Type("ERREUR", 0);
            }

            return classeScope.get(nac.champ).type;
        }

        else if (expr instanceof NoeudAppelMethodeObjet namo) {
            // 1) Vérifier le type de l'objet
            Type tObjet = analyserExpression(namo.objet, pileScopes, tableFonctions);
            if (tObjet == null || tObjet.base.equals("ERREUR")) {
                return new Type("ERREUR", 0);
            }

            // 2) Vérifier que c'est bien une classe
            if (!pileScopes.firstElement().containsKey(tObjet.base)) {
                System.out.println("Erreur : classe non déclarée → " + tObjet.base);
                return new Type("ERREUR", 0);
            }

            // 3) Vérifier que la méthode existe
            Map<String, FonctionInfo> tableMethodeClasse = getTableFonctionsClasse(tObjet.base);
            if (!tableMethodeClasse.containsKey(namo.methode)) {
                System.out.println("Erreur : méthode '" + namo.methode + "' non déclarée dans " + tObjet.base);
                return new Type("ERREUR", 0);
            }

            FonctionInfo fInfo = tableMethodeClasse.get(namo.methode);

            // 4) Vérifier le nombre et type des arguments
            if (namo.arguments.size() != fInfo.typesParam.size()) {
                System.out.println("Erreur : nombre d'arguments incorrect pour " + namo.methode +
                                   ". Attendu " + fInfo.typesParam.size() +
                                   ", trouvé " + namo.arguments.size());
                return new Type("ERREUR", 0);
            }
            for (int i = 0; i < namo.arguments.size(); i++) {
                Type tArg = analyserExpression(namo.arguments.get(i), pileScopes, tableFonctions);
                Type tParam = fInfo.typesParam.get(i);
                if (!tArg.base.equals(tParam.base) || tArg.dimension != tParam.dimension) {
                    System.out.println("Erreur : type de l'argument " + (i+1) + " pour " + namo.methode +
                                       " attendu " + tParam + ", trouvé " + tArg);
                    return new Type("ERREUR", 0);
                }
            }

            // 5) Retourner le type de retour
            return fInfo.typeRetour;
        }*/
        else if (expr instanceof NoeudAccesChamp nac) {
            Type tObjet = analyserExpression(nac.objet, pileScopes, tableFonctions, classesVariables, classesMethodes);
            if (!classesVariables.containsKey(tObjet.base)) {
                nac.erreur = true;
                nac.messageErreur = "classe non déclarée";
                System.out.println("Erreur : classe non déclarée → " + tObjet.base);
                erreurs.add("Erreur : classe non déclarée → " + tObjet.base);
                return new Type("ERREUR", 0);
            }
            Map<String, InfoVariable> champs = classesVariables.get(tObjet.base);
            if (!champs.containsKey(nac.champ)) {
                nac.erreur = true;
                nac.messageErreur = "champ '" + nac.champ + "' non déclaré " + tObjet.base;
                System.out.println("Erreur : champ '" + nac.champ + "' non déclaré dans " + tObjet.base);
                erreurs.add("Erreur : champ '" + nac.champ + "' non déclaré dans " + tObjet.base);
                return new Type("ERREUR", 0);
            }
            return champs.get(nac.champ).type;
        }
        else if (expr instanceof NoeudAppelMethodeObjet namo) {
            Type tObjet = analyserExpression(namo.objet, pileScopes, tableFonctions, classesVariables, classesMethodes);
            if (!classesMethodes.containsKey(tObjet.base)) {
                namo.erreur = true;
                namo.messageErreur = "classe non déclarée";
                System.out.println("Erreur : classe non déclarée → " + tObjet.base);
                erreurs.add("Erreur : classe non déclarée → " + tObjet.base);
                return new Type("ERREUR", 0);
            }
            Map<String, FonctionInfo> methodes = classesMethodes.get(tObjet.base);
            if (!methodes.containsKey(namo.methode)) {
                namo.erreur = true;
                namo.messageErreur = "méthode '" + namo.methode + "' non déclarée " + tObjet.base;
                System.out.println("Erreur : méthode '" + namo.methode + "' non déclarée dans " + tObjet.base);
                erreurs.add("Erreur : méthode '" + namo.methode + "' non déclarée dans " + tObjet.base);
                return new Type("ERREUR", 0);
            }

            FonctionInfo fInfo = methodes.get(namo.methode);

            // Vérification des arguments
            if (namo.arguments.size() != fInfo.typesParam.size()) {
                namo.erreur = true;
                namo.messageErreur = "nombre d'arguments incorrect pour " + namo.methode;
                System.out.println("Erreur : nombre d'arguments incorrect pour " + namo.methode);
                erreurs.add("Erreur : nombre d'arguments incorrect pour " + namo.methode);
                return new Type("ERREUR", 0);
            }
            for (int i = 0; i < namo.arguments.size(); i++) {
                Type tArg = analyserExpression(namo.arguments.get(i), pileScopes, tableFonctions, classesVariables, classesMethodes);
                Type tParam = fInfo.typesParam.get(i);
                if (!tArg.base.equals(tParam.base) || tArg.dimension != tParam.dimension) {
                    namo.erreur = true;
                    namo.messageErreur = "type argument " + (i+1) + " pour " + namo.methode +
                                       " attendu " + tParam + ", trouvé " + tArg;
                    System.out.println("Erreur : type de l'argument " + (i+1) + " pour " + namo.methode +
                                       " attendu " + tParam + ", trouvé " + tArg);
                    erreurs.add("Erreur : type de l'argument " + (i+1) + " pour " + namo.methode +
                                       " attendu " + tParam + ", trouvé " + tArg);
                    return new Type("ERREUR", 0);
                }
            }

            return fInfo.typeRetour;
        }
        else if (expr instanceof noeud_constructeur nc){
            if (!classesMethodes.containsKey(nc.classe)) {
                nc.erreur = true;
                nc.messageErreur = "constructeur classe non déclarée: " + nc.classe;
                System.out.println("Erreur : constructeur d'une classe non déclarée → " + nc.classe);
                return new Type("ERREUR", 0);
            }
            return new Type(nc.classe, 0);
        }
        else if (expr == null){
            return new Type("void",0);
        }


        
        return new Type("ERREUR", 0);
        
    }
    
    

}
/*Étape 4/6 :
👉 soit l’optimisation sémantique (constantes, portées, shadowing)
👉 soit l’interpréteur (exécuter ton langage)
👉 soit la génération de code (pseudo-assembleur / Java)



Ce qu’il reste en sémantique (le vrai travail maintenant) :

Table des symboles par scope (tu as déjà commencé)

Types des expressions

Compatibilité des opérateurs (int + int, pas int + string)

Vérification des constantes

Vérification des paramètres de méthodes

Vérification du type de retour

Détection des variables non initialisées

Shadowing (masquage de variables)

Portée des blocs {}



🔴 1. Table des symboles & portées (SCOPES)

Variables déclarées une seule fois dans un même scope

Masquage autorisé dans les sous-scopes

Résolution correcte (pile de scopes)

Constantes non modifiables

Paramètres de méthodes dans le scope local

🔴 2. Vérification des types

Pour chaque expression :

/ % : types compatibles

Comparaisons : int vs int, double vs double, etc.

Logiques : bool uniquement (&& || !)

Affectation : type gauche == type droite

Tableaux : int[] ≠ int, int[][] ≠ int[]

Casts (si tu les ajoutes plus tard)

🔴 3. Constantes (final / const)

Interdire toute affectation après initialisation

Vérifier qu’une constante est initialisée

Propagation du type constant

🔴 4. Vérification des conditions

if / while / for / do / switch → condition doit être boolean

Opérateurs relationnels retournent boolean

Ternaires : condition boolean, branches de même type

🔴 5. Fonctions / Méthodes

Appel avec bon nombre de paramètres

Types des arguments compatibles

Type de retour respecté

Tous les chemins d’une méthode non-void retournent une valeur

Interdire return en dehors d’une méthode

🔴 6. Return

return expr compatible avec typeRetour

void → pas de valeur

Non-void → valeur obligatoire

🔴 7. Boucles

break uniquement dans boucle ou switch

continue uniquement dans boucle

For : compteur déclaré, modifié, condition bool

Do/While idem

🔴 8. Tableaux

Indices de type int

Accès dans un tableau déclaré

Affectation compatible (int[] ≠ int)

Initialisation cohérente avec dimension

🔴 9. Switch / Case

Type du switch compatible avec les case

Pas de doublons de case

break géré

default unique

🔴 10. Constructeurs & new

Classe existante

Bon nombre de paramètres

Types compatibles

🔴 11. Null / Littéraux

null compatible seulement avec références

true/false = boolean

Interdire opérations arithmétiques sur null

🔴 12. Ternaires

Condition bool

Branche true et false de même type

Type résultant bien calculé

🔴 13. Imports / Packages (si multi-fichiers)

Résolution des symboles

Détection de conflits

Visibilité public/private

🔴 14. Vérification finale de cohérence

Aucune variable utilisée sans déclaration

Aucune méthode inconnue

Aucun type "ERREUR" restant dans l’AST

Aucun scope non fermé




Étape 1 : Table des symboles & Scopes

Objectif : gérer correctement les variables, constantes et shadowing.

Pile de scopes

Chaque bloc {} crée un nouveau scope (Stack<Map<String, Type>>)

À la sortie du bloc, pop du scope

Déclaration des variables

Interdire les doublons dans le même scope

Autoriser le shadowing dans les sous-scopes

Constantes (final)

Stocker si la variable est const/immutable

Empêcher toute affectation après initialisation

Paramètres de méthodes

Ajouter les paramètres dans le scope local de la méthode

Étape 2 : Vérification des types

Objectif : que chaque expression et assignation soit cohérente.

Opérateurs arithmétiques

+, -, *, /, % → vérifier type gauche et droit compatibles (int, double)

Opérateurs logiques

&&, ||, ! → seulement boolean

Comparaisons

==, !=, <, >, <=, >= → types compatibles

Assignations

type gauche == type droite

Tableaux : int[] ≠ int, int[][] ≠ int[]

Casts (si ajoutés)

Étape 3 : Vérification des constantes

Interdire toute affectation après initialisation

Vérifier que la constante est initialisée

Propager le type constant dans les expressions

Étape 4 : Vérification des conditions et boucles

Conditions if, while, for, do → type booléen

for : compteur déclaré et type correct, condition bool, incrément correct

break / continue → uniquement dans boucle ou switch

Détection des return impossibles (hors fonction)

Étape 5 : Méthodes / fonctions

Vérification du nombre et type des paramètres à l’appel

Type de retour respecté

Tous les chemins d’une méthode non-void retournent une valeur

Interdire return en dehors d’une méthode

Étape 6 : Tableaux

Indices de type int

Vérification dimension correcte

Affectation compatible (ex. int[] vs int)

Initialisation cohérente avec la dimension

Étape 7 : Switch / Case

Type du switch compatible avec case

Pas de doublons de case

break obligatoire et default unique

Étape 8 : Constructeurs et new

Classe ou type existant

Nombre et type des paramètres corrects

Vérifier la compatibilité avec le type attendu

Étape 9 : Null / Littéraux

null → uniquement pour références

true / false → uniquement pour boolean

Interdire opérations arithmétiques sur null

Étape 10 : Ternaires

Condition → bool

Branches true / false → même type

Calcul correct du type résultant

Étape 11 : Imports / Packages (si multi-fichiers)

Résolution des symboles

Détection des conflits

Visibilité public / private

Étape 12 : Vérification finale

Aucune variable utilisée sans déclaration

Aucune méthode inconnue

Aucun type ERREUR dans l’AST

Tous les scopes fermés correctement*/