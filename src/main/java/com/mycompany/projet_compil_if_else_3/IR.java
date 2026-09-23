package com.mycompany.projet_compil_if_else_3;

import com.mycompany.projet_compil_if_else_3.lexer.TokenType;
import com.mycompany.projet_compil_if_else_3.lexer.token;
import com.mycompany.projet_compil_if_else_3.parcer.noeud;
import com.mycompany.projet_compil_if_else_3.parcer.*;
import com.mycompany.projet_compil_if_else_3.semantique.Contexte;
import com.mycompany.projet_compil_if_else_3.semantique.FonctionInfo;
import com.mycompany.projet_compil_if_else_3.semantique.InfoVariable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class IR {

    // ---- Générateur de labels uniques ----
    private static AtomicInteger labelCounter = new AtomicInteger(0);
    public static String nextLabel(String prefix) {
        return prefix + "_" + labelCounter.getAndIncrement();
    }

    // ---- Base IRNode ----
    public abstract class IRNode {
        public String type = null; // info sémantique
        public abstract void lower(IRBlock cible); // méthode uniformisée
        public abstract String toString();          // affichage pour debug
    }

    // ---- Constante ----
    public class IRConst extends IRNode {
        public final String valeur;
        public IRConst(String valeur) { this.valeur = valeur; }
        @Override
        public void lower(IRBlock cible) { cible.add(this); }
        @Override
        public String toString() { /*return Integer.toString(valeur);*/return valeur; }
    }

    // ---- Variable ----
    public class IRVar extends IRNode {
        public final String nom;
        public IRVar(String nom) { this.nom = nom; }
        @Override
        public void lower(IRBlock cible) { cible.add(this); }
        @Override
        public String toString() { return nom; }
    }

    // ---- Opération binaire ----
    public class IRBinOp extends IRNode {
        public final String op;
        public final IRNode gauche, droite;
        public IRBinOp(String op, IRNode gauche, IRNode droite) {
            this.op = op; this.gauche = gauche; this.droite = droite;
        }
        @Override
        public void lower(IRBlock cible) {
            gauche.lower(cible);
            droite.lower(cible);
            cible.add(this);
        }
        @Override
        public String toString() { return "(" + gauche + " " + op + " " + droite + ")"; }
    }

    // ---- Assignation ----
    public class IRAssign extends IRNode {
        public final String variable;
        public final IRNode valeur;
        public final IRNode cible;
        public IRAssign(String variable, IRNode cible, IRNode valeur) {
            this.variable = variable;this.cible = cible; this.valeur = valeur;
        }
        @Override
        public void lower(IRBlock cible) {
            valeur.lower(cible);
            cible.add(this);
        }
        @Override
        public String toString() { return variable + " = " + valeur; }
    }

    // ---- Bloc ----
    public class IRBlock extends IRNode {
        public final List<IRNode> instructions = new ArrayList<>();
        public void add(IRNode n) { instructions.add(n); }
        @Override
        public void lower(IRBlock cible) {
            for(IRNode n : instructions) {
                n.lower(cible);
            }
        }
        @Override
        public String toString() { return instructions.toString(); }
    }

    // ---- If / Else ----
    public class IRIf extends IRNode {
        public final IRNode condition;
        public final IRBlock alors, sinon;
        public final IRIf elseIf;
        public IRIf(IRNode condition, IRBlock alors, IRBlock sinon, IRIf elseIf) {
            this.condition = condition;
            this.alors = alors;
            this.sinon = sinon;
            this.elseIf = elseIf;
        }
        @Override
        public void lower(IRBlock cible) {
            String elseName = nextLabel("L_else");
            String endName  = nextLabel("L_end");

            IRLabel labelElse = new IRLabel(elseName);
            IRLabel labelEnd  = new IRLabel(endName);

            // Condition -> jump
            cible.add(new IRCondJump(condition, labelElse));

            // Alors
            alors.lower(cible);
            cible.add(new IRJump(labelEnd));

            // Sinon
            cible.add(labelElse);
            sinon.lower(cible);

            // Fin
            cible.add(labelEnd);
            
            /// att y a eu une mod donc regle la ok??--------------------------------------------------------
        }
        @Override
        public String toString() {
            return "if(" + condition + ") " + alors + " else " + sinon;
        }
    }

    // ---- Labels et jumps ----
    public class IRLabel extends IRNode {
        public final String nom;
        public IRLabel(String nom) { this.nom = nom; }
        @Override
        public void lower(IRBlock cible) { cible.add(this); }
        @Override
        public String toString() { return nom + ":"; }
    }

    public class IRJump extends IRNode {
        public final IRLabel cible;
        public IRJump(IRLabel cible) { this.cible = cible; }
        @Override
        public void lower(IRBlock cible) { cible.add(this); }
        @Override
        public String toString() { return "goto " + cible.nom; }
    }

    public class IRCondJump extends IRNode {
        public final IRNode condition;
        public final IRLabel cible;
        public IRCondJump(IRNode condition, IRLabel cible) {
            this.condition = condition; this.cible = cible;
        }
        @Override
        public void lower(IRBlock cible) {
            condition.lower(cible); // si condition complexe
            //cible.add(this);
        }
        @Override
        public String toString() {
            return "ifFalse " + condition + " goto " + cible.nom;
        }
    }
    
   
    public class IRWhile extends IRNode {
        public IRNode condition;
        public IRBlock bloc;

        public IRWhile(IRNode cond, IRBlock bloc) {
            this.condition = cond;
            this.bloc = bloc;
        }

        @Override
        public void lower(IRBlock cible) {
            // Crée un bloc temporaire pour la condition
            IRBlock condBlock = new IRBlock();
            condition.lower(condBlock);

            // Ajoute la condition au bloc cible
            cible.add(this); // on ajoute l'objet IRWhile pour savoir que c'est une boucle
            // puis ajoute le bloc du while
            bloc.lower(cible);
        }

        @Override
        public String toString() {
            return "IRWhile{" + "condition=" + condition + ", bloc=" + bloc + '}';
        }
    }

    public class IRDo extends IRNode {
        public IRBlock bloc;
        public IRWhile condition; // ici, condition est un IRWhile avec juste la condition

        public IRDo(IRBlock bloc, IRWhile cond) {
            this.bloc = bloc;
            this.condition = cond;
        }

        @Override
        public void lower(IRBlock cible) {
            // Do-While s'exécute au moins une fois
            // On commence par baisser le bloc du do
            bloc.lower(cible);
            // Puis on ajoute la condition sous forme de while
            condition.lower(cible);
        }

        @Override
        public String toString() {
            return "IRDo{" + "bloc=" + bloc + ", condition=" + condition + '}';
        }
    }

    // ===== FOR =====
    public class IRFor extends IRNode {
        public IRNode compteur; // IRCompteur
        public IRBlock bloc;

        public IRFor(IRNode compteur, IRBlock bloc) {
            this.compteur = compteur;
            this.bloc = bloc;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower le compteur (initialisation)
            compteur.lower(cible);

            // Lower le bloc principal
            bloc.lower(cible);

            // On ajoute l'incrément du compteur à la fin du bloc
            if (compteur instanceof IRCompteur ic && ic.pas != null) {
                ic.pas.lower(cible);
            }

            // Ici on pourrait ajouter un IR pour sauter ou tester la limite
            // cible.add(new IRTestLimite(ic));
            cible.add(this); // garder trace de la boucle
        }

        @Override
        public String toString() {
            return "IRFor{" + "compteur=" + compteur + ", bloc=" + bloc + '}';
        }
    }

    // ===== COMPTEUR =====
    public class IRCompteur extends IRNode {
        public String variable;
        public IRNode depart, limite, pas;

        public IRCompteur(String var, IRNode depart, IRNode limite, IRNode pas) {
            this.variable = var;
            this.depart = depart;
            this.limite = limite;
            this.pas = pas;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower la valeur de départ
            if (depart != null) depart.lower(cible);
            // Lower la limite
            if (limite != null) limite.lower(cible);
            // Lower le pas
            if (pas != null) pas.lower(cible);

            // Ajouter le compteur lui-même pour référence dans le IRFor
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRCompteur{" + "variable=" + variable + ", depart=" + depart + ", limite=" + limite + ", pas=" + pas + '}';
        }
    }

    // ===== RETURN =====
    public class IRReturn extends IRNode {
        public IRNode valeur;

        public IRReturn(IRNode val) {
            this.valeur = val;
        }

        @Override
        public void lower(IRBlock cible) {
            if (valeur != null) valeur.lower(cible);
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRReturn{" + "valeur=" + valeur + '}';
        }
    }


    // ===== APPELS =====
    // ===== APPEL DE FONCTION =====
    public class IRCall extends IRNode {
        public String nom;
        public List<IRNode> arguments;

        public IRCall(String nom, List<IRNode> args) {
            this.nom = nom;
            this.arguments = args;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower tous les arguments
            for (IRNode arg : arguments) {
                arg.lower(cible);
            }
            // Ajouter l'appel de fonction lui-même dans le bloc cible
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRCall{" + "nom='" + nom + '\'' + ", arguments=" + arguments + '}';
        }
    }

    // ===== APPEL DE METHODE SUR OBJET =====
    public class IRCallObjet extends IRNode {
        public IRNode objet;
        public String methode;
        public List<IRNode> arguments;

        public IRCallObjet(IRNode obj, String methode, List<IRNode> args) {
            this.objet = obj;
            this.methode = methode;
            this.arguments = args;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower l'objet (this)
            objet.lower(cible);

            // Lower tous les arguments
            for (IRNode arg : arguments) {
                arg.lower(cible);
            }

            // Ajouter l'appel de méthode dans le bloc
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRCallObjet{" + "objet=" + objet + ", methode='" + methode + '\'' + ", arguments=" + arguments + '}';
        }
    }

    // ===== CONSTRUCTEUR =====
    public class IRNew extends IRNode {
        public String classe;
        public List<IRNode> arguments;

        public IRNew(String cls, List<IRNode> args) {
            this.classe = cls;
            this.arguments = args;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower tous les arguments
            for (IRNode arg : arguments) {
                arg.lower(cible);
            }
            // Ajouter le constructeur lui-même dans le bloc
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRNew{" + "classe='" + classe + '\'' + ", arguments=" + arguments + '}';
        }
    }


    // ===== ACCES TABLEAU / CHAMP =====
    // ===== ACCES TABLEAU =====
    public class IRLoadArray extends IRNode {
        public String nom;
        public List<IRNode> indices;

        public IRLoadArray(String nom, List<IRNode> indices) {
            this.nom = nom;
            this.indices = indices;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower tous les indices
            for (IRNode idx : indices) {
                idx.lower(cible);
            }
            // Ajouter le loadArray lui-même
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRLoadArray{" + "nom='" + nom + '\'' + ", indices=" + indices + '}';
        }
    }
    
    public class IRStoreArray extends IRNode {
        public String nom;
        public List<IRNode> indices;
        public IRNode valeur;
        public IRStoreArray(String nom, List<IRNode> indices, IRNode val) {
            this.nom = nom; this.indices = indices; this.valeur = val;
        }
        @Override
        public String toString() {
            return nom + "[" + indices + "] = " + valeur;
        }

        @Override
        public void lower(IRBlock cible) {
            if(!indices.isEmpty()){
                for(IRNode indice :indices){
                    indice.lower(cible);
                }
            }
            if (valeur != null)valeur.lower(cible);
            cible.add(this);
        }
    }


    // ===== ACCES CHAMP =====
    public class IRLoadField extends IRNode {
        public IRNode objet;
        public String champ;

        public IRLoadField(IRNode obj, String champ) {
            this.objet = obj;
            this.champ = champ;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower l'objet
            objet.lower(cible);
            // Ajouter le loadField
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRLoadField{" + "objet=" + objet + ", champ='" + champ + '\'' + '}';
        }
    }

    // ===== CREATION TABLEAU =====
    public class IRNewArray extends IRNode {
        public String typeBase;
        public List<IRNode> tailles;

        public IRNewArray(String typeBase, List<IRNode> tailles) {
            this.typeBase = typeBase;
            this.tailles = tailles;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower toutes les tailles (expressions pour chaque dimension)
            for (IRNode taille : tailles) {
                taille.lower(cible);
            }
            // Ajouter le newArray
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRNewArray{" + "typeBase='" + typeBase + '\'' + ", tailles=" + tailles + '}';
        }
    }

    // ===== TABLEAU LITTERAL / INIT =====
    public class IRArray extends IRNode {
        public Object operateur; // peut être token ou autre info
        public List<IRNode> valeurs;

        public IRArray(Object operateur, List<IRNode> vals) {
            this.operateur = operateur;
            this.valeurs = vals;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower toutes les valeurs
            for (IRNode val : valeurs) {
                val.lower(cible);
            }
            // Ajouter le tableau lui-même
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRArray{" + "operateur=" + operateur + ", valeurs=" + valeurs + '}';
        }
    }


    // ===== TERNARY =====
    public class IRTernary extends IRNode {
        public IRNode condition, vraiExpr, fauxExpr;

        public IRTernary(IRNode cond, IRNode vrai, IRNode faux) {
            this.condition = cond;
            this.vraiExpr = vrai;
            this.fauxExpr = faux;
        }

        @Override
        public void lower(IRBlock cible) {
            if (condition != null) condition.lower(cible);
            if (vraiExpr != null) vraiExpr.lower(cible);
            if (fauxExpr != null) fauxExpr.lower(cible);
            // Ajouter le ternary lui-même
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRTernary{" + "condition=" + condition + ", vraiExpr=" + vraiExpr + ", fauxExpr=" + fauxExpr + '}';
        }
    }


    // ===== BREAK / CONTINUE =====
    // ===== BREAK / CONTINUE =====
    public class IRBreak extends IRNode {
        @Override
        public void lower(IRBlock cible) {
            // break n'a pas de sous-noeud, on l'ajoute directement
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRBreak";
        }
    }

    public class IRContinue extends IRNode {
        @Override
        public void lower(IRBlock cible) {
            // continue n'a pas de sous-noeud, on l'ajoute directement
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRContinue";
        }
    }

    // ===== TRY / CATCH =====
    public class IRTry extends IRNode {
        public IRNode condTry;
        public IRBlock blocTry;
        public List<IRNode> catches;

        public IRTry(IRNode cond, IRBlock blocTry, List<IRNode> catches) {
            this.condTry = cond;
            this.blocTry = blocTry;
            this.catches = catches;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower de la condition try si elle existe
            if (condTry != null) condTry.lower(cible);
            // Lower du bloc try
            if (blocTry != null) blocTry.lower(cible);
            // Lower de tous les catches
            for (IRNode c : catches) {
                c.lower(cible);
            }
            // Ajouter le try lui-même
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRTry{" + "condTry=" + condTry + ", blocTry=" + blocTry + ", catches=" + catches + '}';
        }
    }

    public class IRCatch extends IRNode {
        public IRNode condCatch;
        public IRBlock blocCatch;

        public IRCatch(IRNode cond, IRBlock bloc) {
            this.condCatch = cond;
            this.blocCatch = bloc;
        }

        @Override
        public void lower(IRBlock cible) {
            // Lower condition et bloc catch
            if (condCatch != null) condCatch.lower(cible);
            if (blocCatch != null) blocCatch.lower(cible);
            // Ajouter le catch lui-même
            cible.add(this);
        }

        @Override
        public String toString() {
            return "IRCatch{" + "condCatch=" + condCatch + ", blocCatch=" + blocCatch + '}';
        }
    }


    
    
    /*public IR.IRNode astToIR(noeud n) {
        if (n instanceof noeud_d_asignation) {
            noeud_d_asignation na = (noeud_d_asignation) n;
            IR.IRNode valeur = astToIR(na.valeur); // récursion sur l'expression
            return new IR.IRAssign(na.identificateur, valeur);
        }
        else if (n instanceof noeud_d_operation_binaire) {
            noeud_d_operation_binaire nb = (noeud_d_operation_binaire) n;
            IR.IRNode gauche = astToIR(nb.gauche);
            IR.IRNode droite = astToIR(nb.droite);
            return new IR.IRBinOp(nb.operateur.name(), gauche, droite);
        }
        else if (n instanceof noeud_ternaire) {
            noeud_ternaire nt = (noeud_ternaire) n;
            IR.IRNode cond = astToIR(nt.condition);
            IR.IRBlock vrai = new IR.IRBlock();
            vrai.add(astToIR(nt.vraiExpr));
            IR.IRBlock faux = new IR.IRBlock();
            faux.add(astToIR(nt.fauxExpr));
            return new IR.IRIf(cond, vrai, faux);
        }
        else if (n instanceof noeud_de_valeur) {
            noeud_de_valeur nv = (noeud_de_valeur) n;
            // ici on peut décider si c'est une variable ou une constante
            try {
                int val = Integer.parseInt(nv.valeur);
                return new IR.IRConst(val);
            } catch(Exception e) {
                return new IR.IRVar(nv.valeur);
            }
        }
        else if (n instanceof noeud_si) {
            noeud_si si = (noeud_si) n;

            // 1️⃣ Transformer la condition
            IR.IRNode cond = astToIR(si.condition);

            // 2️⃣ Transformer le bloc "alors"
            IR.IRBlock blocAlors = new IR.IRBlock();
            for (noeud ni : si.blockAlors.mots) { // j'imagine que blockAlors contient une liste 'noeuds'
                blocAlors.add(astToIR(ni));
            }

            // 3️⃣ Transformer le bloc "sinon"
            IR.IRBlock blocSinon = new IR.IRBlock();
            if (si.block_sinon != null) {
                for (noeud ni : si.block_sinon.mots) {
                    blocSinon.add(astToIR(ni));
                }
            }

            // 4️⃣ Retourner un IRIf
            return new IR.IRIf(cond, blocAlors, blocSinon);
        } else {
            throw new RuntimeException("Nœud non supporté : " + n.getClass().getSimpleName());
        }
    }*/
    
    /*public IR.IRNode astToIR(noeud n) {
        if (n == null) return null;

        // ---- Valeurs simples ----
        if (n instanceof noeud_de_valeur) {
            noeud_de_valeur nv = (noeud_de_valeur) n;
            // Si c'est un nombre, on fait IRConst
            try {
                int val = Integer.parseInt(nv.valeur);
                return new IRConst(val);
            } catch (NumberFormatException e) {
                // sinon, c'est une variable
                return new IRVar(nv.valeur);
            }
        }

        // ---- Assignation ----
        if (n instanceof noeud_d_asignation) {
            noeud_d_asignation na = (noeud_d_asignation) n;
            IR.IRNode irValeur = astToIR(na.valeur);
            return new IRAssign(na.identificateur, irValeur);
        }

        // ---- Opérations binaires ----
        if (n instanceof noeud_d_operation_binaire) {
            noeud_d_operation_binaire nb = (noeud_d_operation_binaire) n;
            IR.IRNode gauche = astToIR(nb.gauche);
            IR.IRNode droite = astToIR(nb.droite);
            return new IRBinOp(nb.operateur.name(), gauche, droite);
        }

        // ---- Block ----
        if (n instanceof block_de_noeuds) {
            block_de_noeuds b = (block_de_noeuds) n;
            IRBlock irBlock = new IRBlock();
            for (noeud ni : b.mots) {
                irBlock.add(astToIR(ni));
            }
            return irBlock;
        }

        // ---- If / Else ----
        if (n instanceof noeud_si) {
            noeud_si si = (noeud_si) n;
            IR.IRNode cond = astToIR(si.condition);
            IR.IRBlock blocAlors = (IR.IRBlock) astToIR(si.blockAlors);
            IR.IRBlock blocSinon = (IR.IRBlock) astToIR(si.block_sinon);
            return new IRIf(cond, blocAlors, blocSinon);
        }

        // ---- While (placeholder) ----
        if (n instanceof noeud_while) {
            noeud_while w = (noeud_while) n;
            IR.IRNode cond = astToIR(w.condition);
            IR.IRBlock bloc = (IR.IRBlock) astToIR(w.block);
            // Pour l'instant, juste un label + jump (à compléter pour IRWhile réel)
            IRLabel start = new IRLabel("L_while_start");
            IRLabel end = new IRLabel("L_while_end");
            IRBlock irBloc = new IRBlock();
            irBloc.add(start);
            irBloc.add(new IRCondJump(cond, end));
            for (IR.IRNode ni : bloc.instructions) irBloc.add(ni);
            irBloc.add(new IRJump(start));
            irBloc.add(end);
            return irBloc;
        }

        // ---- Return ----
        if (n instanceof noeud_return) {
            noeud_return ret = (noeud_return) n;
            IR.IRNode irVal = astToIR(ret.valeur);
            // Ici on n'a pas IRReturn donc on peut faire un assign vers "_return" par exemple
            return new IRAssign("_return", irVal);
        }

        // ---- Appel méthode ----
        if (n instanceof noeud_appel_methode) {
            noeud_appel_methode call = (noeud_appel_methode) n;
            IR.IRBlock irBloc = new IRBlock();
            List<IR.IRNode> args = new ArrayList<>();
            for (noeud arg : call.arguments) args.add(astToIR(arg));
            // Pour l'instant, juste afficher l'appel comme assignation à "_callResult"
            irBloc.add(new IRAssign("_callResult", new IRVar(call.nom + args)));
            return irBloc;
        }

        // ---- Incrémentation ----
        if (n instanceof noeud_incrementation) {
            noeud_incrementation inc = (noeud_incrementation) n;
            IR.IRNode val = new IRBinOp(
                inc.operateur == TokenType.INCREMENT ? "+" : "-",
                new IRVar(inc.identificateur),
                new IRConst(1)
            );
            return new IRAssign(inc.identificateur, val);
        }

        // ---- Par défaut ----
        System.out.println("AST -> IR: noeud non géré -> " + n.getClass().getSimpleName());
        return null;
    }*/
    
    public IR.IRNode astToIR(noeud n) {
        if (n == null) return null;
        
        if (n instanceof programmeNoeud pn) {
            IR.IRBlock bloc = new IR.IRBlock();
            for (noeud ni : pn.mots) { // ou pn.listeNoeuds selon ta définition
                IR.IRNode irNode = astToIR(ni);
                if (irNode != null) bloc.add(irNode);
            }
            return bloc;
        }


        // ===== VALEURS =====
        if (n instanceof noeud_de_valeur nv) {
            return new IR.IRConst(nv.valeur);
        }
        if (n instanceof NoeudValeurCondition nvc) {
            return new IR.IRVar(nvc.lexeme);
        }
        if (n instanceof VariableNode vn) {
            return vn.valeur != null ? astToIR(vn.valeur) : new IR.IRVar(vn.nom);
        }

        // ===== ASSIGNATIONS =====
        if (n instanceof noeud_d_asignation na) {
            IR.IRNode cible = na.cible != null ? astToIR(na.cible) : null;
            IR.IRNode valeur = astToIR(na.valeur);
            if (na.cible instanceof NoeudAccesTableau nat) {
                List<IR.IRNode> indices = new ArrayList<>();
                for (noeud ni : nat.indices) indices.add(astToIR(ni));
                return new IR.IRStoreArray(nat.nom, indices, astToIR(na.valeur));
            }
            return new IR.IRAssign(na.identificateur, cible, valeur);
        }
        
        /*if (n instanceof noeud_de_condition ndc) {
            IR.IRNode gauche = astToIR(ndc.gaucheExpr);
            IR.IRNode droite = astToIR(ndc.droiteExpr);

            if (ndc.operateur != null) {
                IR.IRNode cond = new IR.IRBinOp(ndc.operateur.name(), gauche, droite);
                if (ndc.suite != null) {
                    IR.IRNode suiteCond = astToIR(ndc.suite);
                    // si etOu est && ou ||, on peut enchaîner avec IRBinOp
                    cond = new IR.IRBinOp(ndc.etOu.name(), cond, suiteCond);
                }
                return cond;
            } else {
                // simple expression
                return gauche; // ou droite si gauche == null
            }
        }*/
        
        if (n instanceof noeud_de_condition ndc) {
            IR.IRNode gauche = astToIR(ndc.gaucheExpr);
            IR.IRNode droite = astToIR(ndc.droiteExpr);

            // si on a un opérateur, c'est une condition binaire
            if (ndc.operateur != null && gauche != null && droite != null) {
                IR.IRNode cond = new IR.IRBinOp(ndc.operateur.name(), gauche, droite);

                // si on a une suite (&& ou ||)
                if (ndc.suite != null && ndc.etOu != null) {
                    IR.IRNode suiteCond = astToIR(ndc.suite);
                    cond = new IR.IRBinOp(ndc.etOu.name(), cond, suiteCond);
                }

                return cond;
            }

            // sinon juste une expression simple
            if (gauche != null) return gauche;
            return droite; // ou null si vraiment vide
        }



        // ===== OPERATIONS BINAIRES =====
        if (n instanceof noeud_d_operation_binaire nb) {
            IR.IRNode gauche = astToIR(nb.gauche);
            IR.IRNode droite = astToIR(nb.droite);
            return new IR.IRBinOp(nb.operateur.name(), gauche, droite);
        }

        // ===== INCREMENTATION =====
        if (n instanceof noeud_incrementation ni) {
            return new IR.IRBinOp(
                (ni.operateur == TokenType.INCREMENT ? TokenType.PLUS : TokenType.MINUS).name(),
                new IR.IRVar(ni.identificateur),
                new IR.IRConst("1")
            );
        }

        // ===== BLOC =====
        if (n instanceof block_de_noeuds bn) {
            IR.IRBlock bloc = new IR.IRBlock();
            for (noeud ni : bn.mots) {
                bloc.add(astToIR(ni));
            }
            return bloc;
        }

        // ===== IF / ELSE / ELSEIF =====
        if (n instanceof noeud_si si) {
            IR.IRNode cond = astToIR(si.condition);
            IR.IRBlock blocAlors = (IR.IRBlock) astToIR(si.blockAlors);
            IR.IRBlock blocSinon = si.block_sinon != null ? (IR.IRBlock) astToIR(si.block_sinon) : null;

            IR.IRIf elseif = si.block_sinonSi != null ? (IR.IRIf) astToIR(si.block_sinonSi) : null;
            return new IR.IRIf(cond, blocAlors, blocSinon, elseif);
        }
        if (n instanceof noeud_sinonSi ssi) {
            IR.IRNode cond = astToIR(ssi.condition);
            IR.IRBlock blocAlors = (IR.IRBlock) astToIR(ssi.blockAlors);
            IR.IRBlock blocSinon = ssi.block_sinon != null ? (IR.IRBlock) astToIR(ssi.block_sinon) : null;
            IR.IRIf elseif = ssi.block_sinonSi != null ? (IR.IRIf) astToIR(ssi.block_sinonSi) : null;
            return new IR.IRIf(cond, blocAlors, blocSinon, elseif);
        }

        // ===== WHILE / DO =====
        if (n instanceof noeud_while nw) {
            IR.IRNode cond = astToIR(nw.condition);
            IR.IRBlock bloc = (IR.IRBlock) astToIR(nw.block);
            return new IR.IRWhile(cond, bloc);
        }
        if (n instanceof noeud_do nd) {
            IR.IRWhile cond = (IR.IRWhile) astToIR(nd.condition);
            IR.IRBlock bloc = (IR.IRBlock) astToIR(nd.block);
            return new IR.IRDo(bloc, cond);
        }

        // ===== FOR =====
        if (n instanceof noeud_for nf) {
            IR.IRNode compteur = astToIR(nf.compteur);
            IR.IRBlock bloc = (IR.IRBlock) astToIR(nf.block);
            return new IR.IRFor(compteur, bloc);
        }
        if (n instanceof noeud_compteur nc) {
            IR.IRNode valeurDepart = astToIR(nc.valeurDepart);
            IR.IRNode limite = astToIR(nc.limite);
            IR.IRNode pas = astToIR(nc.pas);
            return new IR.IRCompteur(nc.variable, valeurDepart, limite, pas);
        }

        // ===== RETURN =====
        if (n instanceof noeud_return nr) {
            return new IR.IRReturn(astToIR(nr.valeur));
        }

        // ===== APPEL DE METHODE =====
        if (n instanceof noeud_appel_methode nam) {
            List<IR.IRNode> args = new ArrayList<>();
            for (noeud ni : nam.arguments) args.add(astToIR(ni));
            return new IR.IRCall(nam.nom, args);
        }
        if (n instanceof NoeudAppelMethodeObjet namo) {
            List<IR.IRNode> args = new ArrayList<>();
            for (noeud ni : namo.arguments) args.add(astToIR(ni));
            return new IR.IRCallObjet(astToIR(namo.objet), namo.methode, args);
        }

        // ===== CONSTRUCTEUR =====
        if (n instanceof noeud_constructeur nc) {
            List<IR.IRNode> args = new ArrayList<>();
            for (noeud ni : nc.arguments) args.add(astToIR(ni));
            return new IR.IRNew(nc.classe, args);
        }

        // ===== ACCES TABLEAU / CHAMP =====
        if (n instanceof NoeudAccesTableau nat) {
            List<IR.IRNode> indices = new ArrayList<>();
            for (noeud ni : nat.indices) indices.add(astToIR(ni));
            return new IR.IRLoadArray(nat.nom, indices);
        }
        if (n instanceof NoeudAccesChamp nac) {
            return new IR.IRLoadField(astToIR(nac.objet), nac.champ);
        }

        // ===== TABLEAU =====
        if (n instanceof NoeudCreationTableau nct) {
            List<IR.IRNode> tailles = new ArrayList<>();
            for (noeud ni : nct.tailles) tailles.add(astToIR(ni));
            return new IR.IRNewArray(nct.typeBase, tailles);
        }
        if (n instanceof noeud_tableau nt) {
            List<IR.IRNode> vals = new ArrayList<>();
            for (noeud ni : nt.valeurs) vals.add(astToIR(ni));
            return new IR.IRArray(nt.operateur, vals);
        }

        // ===== TERNARY =====
        if (n instanceof noeud_ternaire nt) {
            IR.IRNode cond = astToIR(nt.condition);
            IR.IRNode vrai = astToIR(nt.vraiExpr);
            IR.IRNode faux = astToIR(nt.fauxExpr);
            return new IR.IRTernary(cond, vrai, faux);
        }

        // ===== CONDITIONS BINAIRES / UNAIRES =====
        /*if (n instanceof NoeudConditionBinaire ncb) {
            IR.IRNode gauche = astToIR(ncb.gauche);
            IR.IRNode droite = astToIR(ncb.droite);
            return new IR.IRBinOp(ncb.operateur.name(), gauche, droite);
        }
        if (n instanceof NoeudConditionUnaire ncu) {
            return new IR.IRUnaryOp(ncu.operateur, astToIR(ncu.expr));
        }*/

        // ===== DECLARATIONS MULTIPLES =====
        if (n instanceof noeud_declaration_multiple ndm) {
            IR.IRBlock bloc = new IR.IRBlock();
            for (noeud ni : ndm.variables) bloc.add(astToIR(ni));
            return bloc;
        }

        // ===== BREAK / CONTINUE =====
        if (n instanceof noeud_break) return new IR.IRBreak();
        if (n instanceof noeud_continue) return new IR.IRContinue();

        // ===== SWITCH / CASE / DEFAULT =====
        if (n instanceof noeud_switch ns) {
            IR.IRBlock bloc = new IR.IRBlock();
            IR.IRNode expr = astToIR(ns.expression);
            for (noeud_case nc : ns.cases) {
                bloc.add(astToIR(nc));
            }
            if (ns.defaul != null) bloc.add(astToIR(ns.defaul));
            return bloc; // temporaire
        }
        if (n instanceof noeud_case nc) {
            IR.IRBlock bloc = new IR.IRBlock();
            for (noeud ni : nc.instructions) bloc.add(astToIR(ni));
            return bloc;
        }
        if (n instanceof noeud_default nd) {
            IR.IRBlock bloc = new IR.IRBlock();
            for (noeud ni : nd.instructions) bloc.add(astToIR(ni));
            return bloc;
        }

        // ===== TRY / CATCH =====
        if (n instanceof noeud_try nt) {
            IR.IRBlock blocTry = (IR.IRBlock) astToIR(nt.blockTry);
            List<IR.IRNode> catches = new ArrayList<>();
            for (noeud_catch nc : nt.catches) catches.add(astToIR(nc));
            return new IR.IRTry(astToIR(nt.condTry), blocTry, catches);
        }
        if (n instanceof noeud_catch nc) {
            return new IR.IRCatch(astToIR(nc.condCatch), (IR.IRBlock) astToIR(nc.blockCatch));
        }

        // ===== INUTILE POUR IR =====
        if (n instanceof NoeudPackage || n instanceof NoeudImport || n instanceof noeud_enum) {
            return null; // ignored at IR level
        }

        // ===== AUTRES NOEUDS (Tafoukt / Zakaria) =====
        if (n instanceof noeud_tafoukt ntf) return astToIR(ntf.valeur);
        if (n instanceof noeud_zakaria nz) return astToIR(nz.valeur);

        // ===== PAR DEFAUT =====
        System.err.println("AST -> IR : noeud non géré : " + n.getClass().getSimpleName());
        return null;
    }



    
    
    
    
    /*public static void main(String[] args) {
        IR ir = new IR();

        // Crée un bloc principal
        IR.IRBlock mainBlock = ir.new IRBlock();

        // Définir une condition (x > 0)
        IR.IRVar x = ir.new IRVar("x");
        IR.IRConst zero = ir.new IRConst(0);
        IR.IRBinOp cond = ir.new IRBinOp(">", x, zero);

        // Bloc alors : y = 1
        IR.IRBlock alors = ir.new IRBlock();
        alors.add(ir.new IRAssign("y", ir.new IRConst(1)));

        // Bloc sinon : y = -1
        IR.IRBlock sinon = ir.new IRBlock();
        sinon.add(ir.new IRAssign("y", ir.new IRConst(-1)));

        // Crée le if
        IR.IRIf myIf = ir.new IRIf(cond, alors, sinon);

        // Ajoute le if au bloc principal
        mainBlock.add(myIf);

        // Crée un bloc linéaire pour le lowering
        IR.IRBlock linear = ir.new IRBlock();
        mainBlock.lower(linear);

        // Affichage du résultat du lowering
        System.out.println("=== IR linéaire ===");
        for(IR.IRNode node : linear.instructions) {
            System.out.println(node);
        }
    }*/
    
    /*public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StringBuilder code = new StringBuilder();

        System.out.println("Écris ton code Java simplifié (type if, assignation) :");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) break; // une ligne vide termine l'entrée
            code.append(line).append("\n");
        }

        // --- 1️⃣ Lexer ---
        lexer lex = new lexer(code.toString());
        List<token> tokens = lex.tokeniser();

        // --- 2️⃣ Parser ---
        parcer parser = new parcer(tokens);
        programmeNoeud programme = parser.parcerProgramme();

        // --- 3️⃣ Afficher les erreurs syntaxiques ---
        if (!parser.getErreurs().isEmpty()) {
            System.out.println("=== ERREURS SYNTAXIQUES ===");
            for (String e : parser.getErreurs()) {
                System.out.println(e);
            }
            return; // stop si erreurs
        }

        // --- 4️⃣ Analyse sémantique ---
        Stack<Map<String, InfoVariable>> pileScopes = new Stack<>();
        pileScopes.push(new HashMap<>()); // scope global
        Stack<Contexte> pileContextes = new Stack<>();
        Map<String, FonctionInfo> tableFonctions = new HashMap<>();
        Map<String, Map<String, InfoVariable>> classesVariables = new HashMap<>();
        Map<String, Map<String, FonctionInfo>> classesMethodes = new HashMap<>();

        semantique sem = new semantique();
        System.out.println("\n=== ANALYSE SEMANTIQUE ===");
        sem.analyserProgramme(programme, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);

        // --- 5️⃣ Construction de l'IR ---
        IR ir = new IR();
        IR.IRBlock mainBlock = ir.new IRBlock();

        // Ici il faudrait une fonction pour "convertir" ton AST en IR
        // Exemple (à implémenter) :
        // astToIR(programme, mainBlock);

        // --- 6️⃣ Lowering (linéarisation des if/else) ---
        // Pour chaque IRIf trouvé dans mainBlock, on appelle lower
        // Exemple naïf : on parcourt et on lower les if
        List<IR.IRNode> toLower = new ArrayList<>(mainBlock.instructions);
        for (IR.IRNode n : toLower) {
            if (n instanceof IR.IRIf) {
                ((IR.IRIf) n).lower(mainBlock);
                mainBlock.instructions.remove(n); // on remplace le if original par son lowering
            }
        }

        // --- 7️⃣ Affichage IR final ---
        System.out.println("\n=== INTERMEDIATE REPRESENTATION (IR) ===");
        for (IR.IRNode n : mainBlock.instructions) {
            System.out.println(n);
        }
    }*/
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StringBuilder code = new StringBuilder();

        System.out.println("Écris ton code Java simplifié (type if, assignation) :");
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) break;
            code.append(line).append("\n");
        }

        // --- 1️⃣ Lexer ---
        lexer lex = new lexer(code.toString());
        List<token> tokens = lex.tokeniser();

        // --- 2️⃣ Parser ---
        parcer parser = new parcer(tokens);
        programmeNoeud programme = parser.parcerProgramme();

        // --- 3️⃣ Afficher les erreurs syntaxiques ---
        if (!parser.getErreurs().isEmpty()) {
            System.out.println("=== ERREURS SYNTAXIQUES ===");
            for (String e : parser.getErreurs()) {
                System.out.println(e);
            }
            return;
        }

        // --- 4️⃣ Analyse sémantique ---
        Stack<Map<String, InfoVariable>> pileScopes = new Stack<>();
        pileScopes.push(new HashMap<>()); // scope global
        Stack<Contexte> pileContextes = new Stack<>();
        Map<String, FonctionInfo> tableFonctions = new HashMap<>();
        Map<String, Map<String, InfoVariable>> classesVariables = new HashMap<>();
        Map<String, Map<String, FonctionInfo>> classesMethodes = new HashMap<>();

        semantique sem = new semantique();
        System.out.println("\n=== ANALYSE SEMANTIQUE ===");
        sem.analyserProgramme(programme, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);

        // --- 5️⃣ Construction de l'IR ---
        IR ir = new IR();
        IR.IRBlock mainBlock = ir.new IRBlock();

        // Conversion AST → IR
        IR.IRNode programmeIR = ir.astToIR(programme); // retourne un IRNode ou IRBlock
        if (programmeIR instanceof IR.IRBlock b) {
            mainBlock.instructions.addAll(b.instructions);
        } else if (programmeIR != null) {
            mainBlock.add(programmeIR);
        }

        // --- 6️⃣ Lowering (linéarisation des if/else, while, do, for...) ---
        lowerAll(mainBlock);

        // --- 7️⃣ Affichage IR final ---
        System.out.println("\n=== INTERMEDIATE REPRESENTATION (IR) ===");
        for (IR.IRNode n : mainBlock.instructions) {
            System.out.println(n);
        }
    }

    // Fonction utilitaire pour faire le lowering sur tous les IRBlocks récursivement
    private static void lowerAll(IR.IRBlock bloc) {
        List<IR.IRNode> toProcess = new ArrayList<>(bloc.instructions);
        for (IR.IRNode n : toProcess) {
            if (n instanceof IR.IRWhile w) {
                w.lower(bloc);
                bloc.instructions.remove(n);
            } else if (n instanceof IR.IRDo d) {
                d.lower(bloc);
                bloc.instructions.remove(n);
            } else if (n instanceof IR.IRFor f) {
                f.lower(bloc);
                bloc.instructions.remove(n);
            } else if (n instanceof IR.IRTernary t) {
                t.lower(bloc);
                bloc.instructions.remove(n);
            } else if (n instanceof IR.IRBlock b) {
                lowerAll(b);
            }
        }
    }


}
