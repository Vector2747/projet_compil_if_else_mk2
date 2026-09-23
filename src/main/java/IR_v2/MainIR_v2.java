/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IR_v2;

/**
 *
 * @author InfoPro
 */
import IR_v2.LIR;
import IR_v2.HIR;
import IR_v2.HIR.*;
import IR_v2.LIR.*;
import com.mycompany.projet_compil_if_else_3.lexer;
import com.mycompany.projet_compil_if_else_3.parcer;
import com.mycompany.projet_compil_if_else_3.parcer.*;
import com.mycompany.projet_compil_if_else_3.semantique;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;


public class MainIR_v2 {

    /*public static void main(String[] args) {

        // ==== 1️⃣ Création du HIR ====
        HIR.Block programmeHIR = new HIR.Block();

        // x = 0;
        programmeHIR.add(new HIR.Assign("x", new HIR.Const(0)));

        // while (x < 3) { x = x + 1; }
        HIR.Block bodyWhile = new HIR.Block();
        bodyWhile.add(new HIR.Assign("x", new HIR.BinOp("PLUS", new HIR.Var("x"), new HIR.Const(1))));
        programmeHIR.add(new While(new HIR.BinOp("LT", new HIR.Var("x"), new HIR.Const(3)), bodyWhile));

        // ==== 2️⃣ Lower HIR → LIR ====
        LIR.Block mainLIR = new LIR.Block();
        programmeHIR.lower(mainLIR);

        // ==== 3️⃣ Affichage LIR ====
        System.out.println("=== LIR LINEARIZED ===");
        System.out.println(mainLIR);
    }*/
    
    
    /*public static void main(String[] args) {

        // ==== 1️⃣ Création du HIR ====
        HIR.Block programmeHIR = new HIR.Block();

        // x = 0;
        programmeHIR.add(new HIR.Assign("x", new HIR.Const(0)));

        // y = new Foo(10);
        List<HIR.Expr> argsFoo = new ArrayList<>();
        argsFoo.add(new HIR.Const(10));
        programmeHIR.add(new HIR.Assign("y", new HIR.NewObj("Foo", argsFoo)));

        // while (x < 3) { y.setVal(x * 2); x = x + 1; }
        HIR.Block bodyWhile = new HIR.Block();

        // y.setVal(x * 2)
        List<HIR.Expr> argsSetVal = new ArrayList<>();
        argsSetVal.add(new HIR.BinOp("MULT", new HIR.Var("x"), new HIR.Const(2)));
        bodyWhile.add(new HIR.Assign("_", new HIR.CallMethod(new HIR.Var("y"), "setVal", argsSetVal)));

        // x = x + 1
        bodyWhile.add(new HIR.Assign("x", new HIR.BinOp("PLUS", new HIR.Var("x"), new HIR.Const(1))));

        // while
        programmeHIR.add(new HIR.While(new HIR.BinOp("LT", new HIR.Var("x"), new HIR.Const(3)), bodyWhile));

        // z = (x < 5) ? x : 5;
        HIR.Ternary tern = new HIR.Ternary(
            new HIR.BinOp("LT", new HIR.Var("x"), new HIR.Const(5)),
            new HIR.Var("x"),
            new HIR.Const(5)
        );
        programmeHIR.add(new HIR.Assign("z", tern));

        // ==== 2️⃣ Lower HIR → LIR ====
        LIR.Block mainLIR = new LIR.Block();
        programmeHIR.lower(mainLIR);

        // ==== 3️⃣ Affichage LIR ====
        System.out.println("=== LIR LINEARIZED ===");
        for (Object n : mainLIR.code/*nodes*) {
            System.out.println(n);
        }
    }*/
    
    

    /*public static void main(String[] args) {

        // ==== 1️⃣ Création du HIR (High-level IR) ====
        HIR.Block programme = new HIR.Block();

        // x = 0
        programme.add(new HIR.Assign("x", new HIR.Const(0)));

        // y = new Obj()
        HIR.Var yVar = new HIR.Var("y");
        programme.add(new HIR.Assign("y", new HIR.NewObj("Obj", List.of())));

        // while (x < 3) { x = x + 1; if (x == 2) break; }
        HIR.Block bodyWhile = new HIR.Block();
        bodyWhile.add(new HIR.Assign("x", new HIR.BinOp("PLUS", new HIR.Var("x"), new HIR.Const(1))));
        HIR.Block ifBreak = new HIR.Block();
        ifBreak.add(new HIR.Break());
        bodyWhile.add(new HIR.If(new HIR.BinOp("EQ", new HIR.Var("x"), new HIR.Const(2)), ifBreak, null));
        programme.add(new HIR.While(new HIR.BinOp("LT", new HIR.Var("x"), new HIR.Const(3)), bodyWhile));

        // do { y.field = x; x = x - 1; } while (x > 0)
        HIR.Block bodyDo = new HIR.Block();
        bodyDo.add(new HIR.LoadField(new HIR.Var("y"), "field")); // juste pour test
        bodyDo.add(new HIR.Assign("x", new HIR.BinOp("MINUS", new HIR.Var("x"), new HIR.Const(1))));
        programme.add(new HIR.Do(bodyDo, new HIR.BinOp("GT", new HIR.Var("x"), new HIR.Const(0))));

        // for (i=0; i<2; i++) { x = x + i; continue; }
        HIR.Block bodyFor = new HIR.Block();
        bodyFor.add(new HIR.Assign("x", new HIR.BinOp("PLUS", new HIR.Var("x"), new HIR.Var("i"))));
        bodyFor.add(new HIR.Continue());
        HIR.For f = new HIR.For(
            new HIR.Assign("i", new HIR.Const(0)),
            new HIR.BinOp("LT", new HIR.Var("i"), new HIR.Const(2)),
            new HIR.Assign("i", new HIR.BinOp("PLUS", new HIR.Var("i"), new HIR.Const(1))),
            bodyFor
        );
        programme.add(f);

        // Ternary: z = (x < 5) ? x : 5
        programme.add(new HIR.Assign("z", new HIR.Ternary(new HIR.BinOp("LT", new HIR.Var("x"), new HIR.Const(5)),
                                                           new HIR.Var("x"), new HIR.Const(5))));

        // Call: r = foo(x, z)
        programme.add(new HIR.Assign("r", new HIR.Call("foo", List.of(new HIR.Var("x"), new HIR.Var("z")))));

        // CallMethod: s = y.bar(r)
        programme.add(new HIR.Assign("s", new HIR.CallMethod(new HIR.Var("y"), "bar", List.of(new HIR.Var("r")))));

        // LoadArray: a0 = arr[0]
        programme.add(new HIR.Assign("a0", new HIR.LoadArray(new HIR.Var("arr"), List.of(new HIR.Const(0)))));

        // Return s
        programme.add(new HIR.Return(new HIR.Var("s")));

        // ==== 2️⃣ Lower HIR → LIR ====
        LIR.Block mainLIR = new LIR.Block();
        programme.lower(mainLIR);

        // ==== 3️⃣ Affichage LIR final ====
        System.out.println("=== LIR LINEARIZED ===");
        System.out.println(mainLIR);
    }*/

    /*public static void main(String[] args) {

        // ==== 1️⃣ Création du HIR ====
        HIR.Block programmeHIR = new HIR.Block();

        // --- Variables ---
        programmeHIR.add(new HIR.Assign("x", new HIR.Const(0)));
        programmeHIR.add(new HIR.Assign("i", new HIR.Const(0)));

        // --- New Object ---
        HIR.Var tmpObj = new HIR.Var("t0");
        programmeHIR.add(new HIR.Assign("y", new HIR.NewObj("Obj", new ArrayList<>())));

        // --- While ---
        HIR.Block whileBody = new HIR.Block();
        whileBody.add(new HIR.Assign("x", new HIR.BinOp("PLUS", new HIR.Var("x"), new HIR.Const(1))));
        whileBody.add(new HIR.If(
            new HIR.BinOp("EQ", new HIR.Var("x"), new HIR.Const(2)),
            new HIR.Block(List.of(new HIR.Break())),
            new HIR.Block()
        ));
        programmeHIR.add(new HIR.While(new HIR.BinOp("LT", new HIR.Var("x"), new HIR.Const(3)), whileBody));

        // --- Do-While ---
        HIR.Block doBody = new HIR.Block();
        doBody.add(new HIR.Assign("x", new HIR.BinOp("MINUS", new HIR.Var("x"), new HIR.Const(1))));
        programmeHIR.add(new HIR.Do(doBody, new HIR.While(new HIR.BinOp("GT", new HIR.Var("x"), new HIR.Const(0)), null)));

        // --- For ---
        HIR.Block forBody = new HIR.Block();
        forBody.add(new HIR.Assign("x", new HIR.BinOp("PLUS", new HIR.Var("x"), new HIR.Var("i"))));
        forBody.add(new HIR.Continue());
        programmeHIR.add(new HIR.For(new HIR.Compteur("i", new HIR.Const(0), new HIR.Const(2), new HIR.Const(1)), forBody));

        // --- Ternary ---
        programmeHIR.add(new HIR.Assign("z", new HIR.Ternary(
            new HIR.BinOp("LT", new HIR.Var("x"), new HIR.Const(5)),
            new HIR.Var("x"),
            new HIR.Const(5)
        )));

        // --- Function Call ---
        programmeHIR.add(new HIR.Assign("r", new HIR.Call("foo", List.of(new HIR.Var("x"), new HIR.Var("z")))));

        // --- Method Call ---
        programmeHIR.add(new HIR.Assign("s", new HIR.CallMethod(new HIR.Var("y"), "bar", List.of(new HIR.Var("r")))));

        // --- Array access ---
        programmeHIR.add(new HIR.Assign("a0", new HIR.LoadArray(new HIR.Var("arr"), List.of(new HIR.Const(0)))));

        // --- Return ---
        programmeHIR.add(new HIR.Return(new HIR.Var("s")));

        // ==== 2️⃣ Lower HIR → LIR ====
        LIR.Block mainLIR = new LIR.Block();
        programmeHIR.lower(mainLIR);

        // ==== 3️⃣ Affichage LIR ====
        System.out.println("=== LIR LINEARIZED ===");
        System.out.println(mainLIR);
    }*/

/*public static void main(String[] args) {
    IR_v2.HIR.Block programme = new IR_v2.HIR.Block();

    // ===== Variables et assignations =====
    programme.add(new IR_v2.HIR.Assign("x", new IR_v2.HIR.Const(0)));
    programme.add(new IR_v2.HIR.Assign("i", new IR_v2.HIR.Const(0)));
    programme.add(new IR_v2.HIR.Assign("arr", new IR_v2.HIR.NewObj("Array", List.of())));
    programme.add(new IR_v2.HIR.Assign("y", new IR_v2.HIR.NewObj("Obj", List.of())));

    // ===== WHILE =====
    IR_v2.HIR.Block whileBody = new IR_v2.HIR.Block();
    whileBody.add(new IR_v2.HIR.Assign("x", new IR_v2.HIR.BinOp("PLUS", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(1))));
    // Exemple de break / continue
    whileBody.add(new IR_v2.HIR.If(
        new IR_v2.HIR.BinOp("EQ", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(2)),
        new IR_v2.HIR.Block(), // thenBlock vide
        null
    ));
    programme.add(new IR_v2.HIR.While(
        new IR_v2.HIR.BinOp("LT", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(3)),
        whileBody
    ));

    // ===== DO-WHILE =====
    IR_v2.HIR.Block doBody = new IR_v2.HIR.Block();
    doBody.add(new IR_v2.HIR.Assign("x", new IR_v2.HIR.BinOp("MINUS", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(1))));
    programme.add(new IR_v2.HIR.Do(
        doBody,
        new IR_v2.HIR.BinOp("GT", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(0))
    ));

    // ===== FOR =====
    IR_v2.HIR.Block forBody = new IR_v2.HIR.Block();
    forBody.add(new IR_v2.HIR.Assign("x", new IR_v2.HIR.BinOp("PLUS", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Var("i"))));
    forBody.add(new IR_v2.HIR.Continue("for_cond")); // exemple de continue
    programme.add(new IR_v2.HIR.For(
        new IR_v2.HIR.Assign("i", new IR_v2.HIR.Const(0)),
        new IR_v2.HIR.BinOp("LT", new IR_v2.HIR.Var("i"), new IR_v2.HIR.Const(2)),
        new IR_v2.HIR.Assign("i", new IR_v2.HIR.BinOp("PLUS", new IR_v2.HIR.Var("i"), new IR_v2.HIR.Const(1))),
        forBody
    ));

    // ===== IF / ELSE =====
    IR_v2.HIR.Block thenBlock = new IR_v2.HIR.Block();
    thenBlock.add(new IR_v2.HIR.Assign("x", new IR_v2.HIR.BinOp("PLUS", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(10))));
    IR_v2.HIR.Block elseBlock = new IR_v2.HIR.Block();
    elseBlock.add(new IR_v2.HIR.Assign("x", new IR_v2.HIR.BinOp("MINUS", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(5))));
    programme.add(new IR_v2.HIR.If(
        new IR_v2.HIR.BinOp("GT", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(0)),
        thenBlock,
        elseBlock
    ));

    // ===== TERNARY =====
    IR_v2.HIR.Ternary tern = new IR_v2.HIR.Ternary(
        new IR_v2.HIR.BinOp("LT", new IR_v2.HIR.Var("x"), new IR_v2.HIR.Const(5)),
        new IR_v2.HIR.Var("x"),
        new IR_v2.HIR.Const(5)
    );
    programme.add(new IR_v2.HIR.Assign("z", tern));

    // ===== APPEL DE FONCTION =====
    programme.add(new IR_v2.HIR.Assign("r", new IR_v2.HIR.Call("foo", List.of(new IR_v2.HIR.Var("x"), new IR_v2.HIR.Var("z")))));

    // ===== APPEL DE METHODE =====
    programme.add(new IR_v2.HIR.Assign("s", new IR_v2.HIR.CallMethod(new IR_v2.HIR.Var("y"), "bar", List.of(new IR_v2.HIR.Var("r")))));

    // ===== ACCES CHAMP / TABLEAU =====
    programme.add(new IR_v2.HIR.Assign("a0", new IR_v2.HIR.LoadArray(new IR_v2.HIR.Var("arr"), List.of(new IR_v2.HIR.Const(0)))));

    // ===== RETURN =====
    programme.add(new IR_v2.HIR.Return(new IR_v2.HIR.Var("s")));

    // ===== LOWER HIR → LIR =====
    LIR.Block mainLIR = new LIR.Block();
    programme.lower(mainLIR);

    // ===== AFFICHAGE =====
    System.out.println("=== LIR LINEARIZED ===");
    System.out.println(mainLIR);
}*/

    /*public static void main(String[] args) {
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
        List<lexer.token> tokens = lex.tokeniser();

        // --- 2️⃣ Parser ---
        parcer parser = new parcer(tokens);
        parcer.programmeNoeud programme = parser.parcerProgramme();

        // --- 3️⃣ Afficher les erreurs syntaxiques ---
        if (!parser.getErreurs().isEmpty()) {
            System.out.println("=== ERREURS SYNTAXIQUES ===");
            for (String e : parser.getErreurs()) {
                System.out.println(e);
            }
            return;
        }

        // --- 4️⃣ Analyse sémantique ---
        Stack<Map<String, semantique.InfoVariable>> pileScopes = new Stack<>();
        pileScopes.push(new HashMap<>()); // scope global
        Stack<semantique.Contexte> pileContextes = new Stack<>();
        Map<String, semantique.FonctionInfo> tableFonctions = new HashMap<>();
        Map<String, Map<String, semantique.InfoVariable>> classesVariables = new HashMap<>();
        Map<String, Map<String, semantique.FonctionInfo>> classesMethodes = new HashMap<>();

        semantique sem = new semantique();
        System.out.println("\n=== ANALYSE SEMANTIQUE ===");
        sem.analyserProgramme(programme, pileScopes, pileContextes, tableFonctions, classesVariables, classesMethodes);

        // --- 5️⃣ Construction de l'IR ---
        HIR ir = new HIR();
        HIR.Block mainBlock = new IR_v2.HIR.Block();

        // Conversion AST → IR
        com.mycompany.projet_compil_if_else_3.IR.IRNode programmeIR = ir.astToIR(programme); // retourne un IRNode ou IRBlock
        if (programmeIR instanceof com.mycompany.projet_compil_if_else_3.IR.IRBlock b) {
            mainBlock.instructions.addAll(b.instructions);
        } else if (programmeIR != null) {
            mainBlock.add(programmeIR);
        }

        // --- 6️⃣ Lowering (linéarisation des if/else, while, do, for...) ---
        //lowerAll(mainBlock);

        // --- 7️⃣ Affichage IR final ---
        System.out.println("\n=== INTERMEDIATE REPRESENTATION (IR) ===");
        for (com.mycompany.projet_compil_if_else_3.IR.IRNode n : mainBlock.instructions) {
            System.out.println(n);
        }
    }
    
    
    
    public com.mycompany.projet_compil_if_else_3.IR.IRNode astToIR(parcer.noeud n) {
        if (n == null) return null;
        
        if (n instanceof parcer.programmeNoeud pn) {
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = new com.mycompany.projet_compil_if_else_3.IR.IRBlock();
            for (parcer.noeud ni : pn.mots) { // ou pn.listeNoeuds selon ta définition
                com.mycompany.projet_compil_if_else_3.IR.IRNode irNode = astToIR(ni);
                if (irNode != null) bloc.add(irNode);
            }
            return bloc;
        }


        // ===== VALEURS =====
        if (n instanceof parcer.noeud_de_valeur nv) {
            return new com.mycompany.projet_compil_if_else_3.IR.IRConst(nv.valeur);
        }
        if (n instanceof parcer.NoeudValeurCondition nvc) {
            return new com.mycompany.projet_compil_if_else_3.IR.IRVar(nvc.lexeme);
        }
        if (n instanceof parcer.VariableNode vn) {
            return vn.valeur != null ? astToIR(vn.valeur) : new com.mycompany.projet_compil_if_else_3.IR.IRVar(vn.nom);
        }

        // ===== ASSIGNATIONS =====
        if (n instanceof parcer.noeud_d_asignation na) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode cible = na.cible != null ? astToIR(na.cible) : null;
            com.mycompany.projet_compil_if_else_3.IR.IRNode valeur = astToIR(na.valeur);
            if (na.cible instanceof parcer.NoeudAccesTableau nat) {
                List<com.mycompany.projet_compil_if_else_3.IR.IRNode> indices = new ArrayList<>();
                for (parcer.noeud ni : nat.indices) indices.add(astToIR(ni));
                return new com.mycompany.projet_compil_if_else_3.IR.IRStoreArray(nat.nom, indices, astToIR(na.valeur));
            }
            return new com.mycompany.projet_compil_if_else_3.IR.IRAssign(na.identificateur, cible, valeur);
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
        }*
        
        if (n instanceof parcer.noeud_de_condition ndc) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode gauche = astToIR(ndc.gaucheExpr);
            com.mycompany.projet_compil_if_else_3.IR.IRNode droite = astToIR(ndc.droiteExpr);

            // si on a un opérateur, c'est une condition binaire
            if (ndc.operateur != null && gauche != null && droite != null) {
                com.mycompany.projet_compil_if_else_3.IR.IRNode cond = new com.mycompany.projet_compil_if_else_3.IR.IRBinOp(ndc.operateur.name(), gauche, droite);

                // si on a une suite (&& ou ||)
                if (ndc.suite != null && ndc.etOu != null) {
                    com.mycompany.projet_compil_if_else_3.IR.IRNode suiteCond = astToIR(ndc.suite);
                    cond = new com.mycompany.projet_compil_if_else_3.IR.IRBinOp(ndc.etOu.name(), cond, suiteCond);
                }

                return cond;
            }

            // sinon juste une expression simple
            if (gauche != null) return gauche;
            return droite; // ou null si vraiment vide
        }



        // ===== OPERATIONS BINAIRES =====
        if (n instanceof parcer.noeud_d_operation_binaire nb) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode gauche = astToIR(nb.gauche);
            com.mycompany.projet_compil_if_else_3.IR.IRNode droite = astToIR(nb.droite);
            return new com.mycompany.projet_compil_if_else_3.IR.IRBinOp(nb.operateur.name(), gauche, droite);
        }

        // ===== INCREMENTATION =====
        if (n instanceof parcer.noeud_incrementation ni) {
            return new com.mycompany.projet_compil_if_else_3.IR.IRBinOp(
                (ni.operateur == lexer.TokenType.INCREMENT ? lexer.TokenType.PLUS : lexer.TokenType.MINUS).name(),
                new com.mycompany.projet_compil_if_else_3.IR.IRVar(ni.identificateur),
                new com.mycompany.projet_compil_if_else_3.IR.IRConst("1")
            );
        }

        // ===== BLOC =====
        if (n instanceof parcer.block_de_noeuds bn) {
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = new com.mycompany.projet_compil_if_else_3.IR.IRBlock();
            for (parcer.noeud ni : bn.mots) {
                bloc.add(astToIR(ni));
            }
            return bloc;
        }

        // ===== IF / ELSE / ELSEIF =====
        if (n instanceof parcer.noeud_si si) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode cond = astToIR(si.condition);
            com.mycompany.projet_compil_if_else_3.IR.IRBlock blocAlors = (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(si.blockAlors);
            com.mycompany.projet_compil_if_else_3.IR.IRBlock blocSinon = si.block_sinon != null ? (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(si.block_sinon) : null;

            com.mycompany.projet_compil_if_else_3.IR.IRIf elseif = si.block_sinonSi != null ? (com.mycompany.projet_compil_if_else_3.IR.IRIf) astToIR(si.block_sinonSi) : null;
            return new com.mycompany.projet_compil_if_else_3.IR.IRIf(cond, blocAlors, blocSinon, elseif);
        }
        if (n instanceof parcer.noeud_sinonSi ssi) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode cond = astToIR(ssi.condition);
            com.mycompany.projet_compil_if_else_3.IR.IRBlock blocAlors = (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(ssi.blockAlors);
            com.mycompany.projet_compil_if_else_3.IR.IRBlock blocSinon = ssi.block_sinon != null ? (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(ssi.block_sinon) : null;
            com.mycompany.projet_compil_if_else_3.IR.IRIf elseif = ssi.block_sinonSi != null ? (com.mycompany.projet_compil_if_else_3.IR.IRIf) astToIR(ssi.block_sinonSi) : null;
            return new com.mycompany.projet_compil_if_else_3.IR.IRIf(cond, blocAlors, blocSinon, elseif);
        }

        // ===== WHILE / DO =====
        if (n instanceof parcer.noeud_while nw) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode cond = astToIR(nw.condition);
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(nw.block);
            return new com.mycompany.projet_compil_if_else_3.IR.IRWhile(cond, bloc);
        }
        if (n instanceof parcer.noeud_do nd) {
            com.mycompany.projet_compil_if_else_3.IR.IRWhile cond = (com.mycompany.projet_compil_if_else_3.IR.IRWhile) astToIR(nd.condition);
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(nd.block);
            return new com.mycompany.projet_compil_if_else_3.IR.IRDo(bloc, cond);
        }

        // ===== FOR =====
        if (n instanceof parcer.noeud_for nf) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode compteur = astToIR(nf.compteur);
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(nf.block);
            return new com.mycompany.projet_compil_if_else_3.IR.IRFor(compteur, bloc);
        }
        if (n instanceof parcer.noeud_compteur nc) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode valeurDepart = astToIR(nc.valeurDepart);
            com.mycompany.projet_compil_if_else_3.IR.IRNode limite = astToIR(nc.limite);
            com.mycompany.projet_compil_if_else_3.IR.IRNode pas = astToIR(nc.pas);
            return new com.mycompany.projet_compil_if_else_3.IR.IRCompteur(nc.variable, valeurDepart, limite, pas);
        }

        // ===== RETURN =====
        if (n instanceof parcer.noeud_return nr) {
            return new com.mycompany.projet_compil_if_else_3.IR.IRReturn(astToIR(nr.valeur));
        }

        // ===== APPEL DE METHODE =====
        if (n instanceof parcer.noeud_appel_methode nam) {
            List<com.mycompany.projet_compil_if_else_3.IR.IRNode> args = new ArrayList<>();
            for (parcer.noeud ni : nam.arguments) args.add(astToIR(ni));
            return new com.mycompany.projet_compil_if_else_3.IR.IRCall(nam.nom, args);
        }
        if (n instanceof parcer.NoeudAppelMethodeObjet namo) {
            List<com.mycompany.projet_compil_if_else_3.IR.IRNode> args = new ArrayList<>();
            for (parcer.noeud ni : namo.arguments) args.add(astToIR(ni));
            return new com.mycompany.projet_compil_if_else_3.IR.IRCallObjet(astToIR(namo.objet), namo.methode, args);
        }

        // ===== CONSTRUCTEUR =====
        if (n instanceof parcer.noeud_constructeur nc) {
            List<com.mycompany.projet_compil_if_else_3.IR.IRNode> args = new ArrayList<>();
            for (parcer.noeud ni : nc.arguments) args.add(astToIR(ni));
            return new com.mycompany.projet_compil_if_else_3.IR.IRNew(nc.classe, args);
        }

        // ===== ACCES TABLEAU / CHAMP =====
        if (n instanceof parcer.NoeudAccesTableau nat) {
            List<com.mycompany.projet_compil_if_else_3.IR.IRNode> indices = new ArrayList<>();
            for (parcer.noeud ni : nat.indices) indices.add(astToIR(ni));
            return new com.mycompany.projet_compil_if_else_3.IR.IRLoadArray(nat.nom, indices);
        }
        if (n instanceof parcer.NoeudAccesChamp nac) {
            return new com.mycompany.projet_compil_if_else_3.IR.IRLoadField(astToIR(nac.objet), nac.champ);
        }

        // ===== TABLEAU =====
        if (n instanceof parcer.NoeudCreationTableau nct) {
            List<com.mycompany.projet_compil_if_else_3.IR.IRNode> tailles = new ArrayList<>();
            for (parcer.noeud ni : nct.tailles) tailles.add(astToIR(ni));
            return new com.mycompany.projet_compil_if_else_3.IR.IRNewArray(nct.typeBase, tailles);
        }
        if (n instanceof parcer.noeud_tableau nt) {
            List<com.mycompany.projet_compil_if_else_3.IR.IRNode> vals = new ArrayList<>();
            for (parcer.noeud ni : nt.valeurs) vals.add(astToIR(ni));
            return new com.mycompany.projet_compil_if_else_3.IR.IRArray(nt.operateur, vals);
        }

        // ===== TERNARY =====
        if (n instanceof parcer.noeud_ternaire nt) {
            com.mycompany.projet_compil_if_else_3.IR.IRNode cond = astToIR(nt.condition);
            com.mycompany.projet_compil_if_else_3.IR.IRNode vrai = astToIR(nt.vraiExpr);
            com.mycompany.projet_compil_if_else_3.IR.IRNode faux = astToIR(nt.fauxExpr);
            return new com.mycompany.projet_compil_if_else_3.IR.IRTernary(cond, vrai, faux);
        }

        // ===== CONDITIONS BINAIRES / UNAIRES =====
        /*if (n instanceof NoeudConditionBinaire ncb) {
            IR.IRNode gauche = astToIR(ncb.gauche);
            IR.IRNode droite = astToIR(ncb.droite);
            return new IR.IRBinOp(ncb.operateur.name(), gauche, droite);
        }
        if (n instanceof NoeudConditionUnaire ncu) {
            return new IR.IRUnaryOp(ncu.operateur, astToIR(ncu.expr));
        }*

        // ===== DECLARATIONS MULTIPLES =====
        if (n instanceof parcer.noeud_declaration_multiple ndm) {
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = new com.mycompany.projet_compil_if_else_3.IR.IRBlock();
            for (parcer.noeud ni : ndm.variables) bloc.add(astToIR(ni));
            return bloc;
        }

        // ===== BREAK / CONTINUE =====
        if (n instanceof parcer.noeud_break) return new com.mycompany.projet_compil_if_else_3.IR.IRBreak();
        if (n instanceof parcer.noeud_continue) return new com.mycompany.projet_compil_if_else_3.IR.IRContinue();

        // ===== SWITCH / CASE / DEFAULT =====
        if (n instanceof parcer.noeud_switch ns) {
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = new com.mycompany.projet_compil_if_else_3.IR.IRBlock();
            com.mycompany.projet_compil_if_else_3.IR.IRNode expr = astToIR(ns.expression);
            for (parcer.noeud_case nc : ns.cases) {
                bloc.add(astToIR(nc));
            }
            if (ns.defaul != null) bloc.add(astToIR(ns.defaul));
            return bloc; // temporaire
        }
        if (n instanceof parcer.noeud_case nc) {
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = new com.mycompany.projet_compil_if_else_3.IR.IRBlock();
            for (parcer.noeud ni : nc.instructions) bloc.add(astToIR(ni));
            return bloc;
        }
        if (n instanceof parcer.noeud_default nd) {
            com.mycompany.projet_compil_if_else_3.IR.IRBlock bloc = new com.mycompany.projet_compil_if_else_3.IR.IRBlock();
            for (parcer.noeud ni : nd.instructions) bloc.add(astToIR(ni));
            return bloc;
        }

        // ===== TRY / CATCH =====
        if (n instanceof parcer.noeud_try nt) {
            com.mycompany.projet_compil_if_else_3.IR.IRBlock blocTry = (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(nt.blockTry);
            List<com.mycompany.projet_compil_if_else_3.IR.IRNode> catches = new ArrayList<>();
            for (parcer.noeud_catch nc : nt.catches) catches.add(astToIR(nc));
            return new com.mycompany.projet_compil_if_else_3.IR.IRTry(astToIR(nt.condTry), blocTry, catches);
        }
        if (n instanceof parcer.noeud_catch nc) {
            return new com.mycompany.projet_compil_if_else_3.IR.IRCatch(astToIR(nc.condCatch), (com.mycompany.projet_compil_if_else_3.IR.IRBlock) astToIR(nc.blockCatch));
        }

        // ===== INUTILE POUR IR =====
        if (n instanceof parcer.NoeudPackage || n instanceof parcer.NoeudImport || n instanceof parcer.noeud_enum) {
            return null; // ignored at IR level
        }

        // ===== AUTRES NOEUDS (Tafoukt / Zakaria) =====
        if (n instanceof parcer.noeud_tafoukt ntf) return astToIR(ntf.valeur);
        if (n instanceof parcer.noeud_zakaria nz) return astToIR(nz.valeur);

        // ===== PAR DEFAUT =====
        System.err.println("AST -> IR : noeud non géré : " + n.getClass().getSimpleName());
        return null;
    }*/
    
    public static class ASTtoHIR {

        /*public HIR.Block convert(programmeNoeud ast) {
            HIR.Block block = new HIR.Block();
            for (noeud n : ast.mots) {
                block.add(convertNode(n));
            }
            return block;
        }

        private HIR.Node convertNode(noeud n) {
            if (n instanceof noeud_d_asignation) return convertAssign((noeud_d_asignation) n);
            if (n instanceof noeud_si)     return convertIf((noeud_si) n);
            if (n instanceof noeud_while)  return convertWhile((noeud_while) n);
            if (n instanceof noeud_return) return convertReturn((noeud_return) n);
            if (n instanceof block_de_noeuds)   return convertBlock((block_de_noeuds) n);

            throw new RuntimeException("Noeud AST non supporté : " + n.getClass());
        }

        private HIR.Node convertAssign(noeud_d_asignation n) {
            return new HIR.Assign(
                n.identificateur,
                convertExpr(n.)
            );
        }

        private HIR.Expr convertExpr(noeud_expr e) {

            if (e instanceof noeud_const)
                return new HIR.Const(((noeud_const) e).valeur);

            if (e instanceof noeud_var)
                return new HIR.Var(((noeud_var) e).nom);

            if (e instanceof noeud_d_operation_binaire) {
                noeud_d_operation_binaire b = (noeud_d_operation_binaire) e;
                return new HIR.BinOp(
                    b.operateur.name(),
                    convertExpr(b.gauche),
                    convertExpr(b.droite)
                );
            }

            throw new RuntimeException("Expression AST inconnue : " + e.getClass());
        }

        private HIR.Node convertIf(noeud_si n) {

            HIR.Block thenBlock = convertBlock(n.blockAlors);

            HIR.Block elseBlock = null;
            if (n.block_sinonSi != null)
                elseBlock = convertBlock(n.block_sinon);

            return new HIR.If(
                convertExpr(n.condition),
                thenBlock,
                elseBlock
            );
        }

        private HIR.Node convertWhile(noeud_while n) {
            return new HIR.While(
                convertExpr(n.condition),
                convertBlock(n.block)
            );
        }

        private HIR.Block convertBlock(block_de_noeuds b) {
            HIR.Block block = new HIR.Block();
            for (noeud n : b.mots) {
                block.add(convertNode(n));
            }
            return block;
        }

        private HIR.Node convertReturn(noeud_return n) {
            return new HIR.Return(convertExpr(n.valeur));
        }*/











        public static void main(String[] args) {

            try {
                // ============================
                // 1️⃣ Code source (test)
                // ============================
                String source = """
                    int x;
                    x = 0;

                    while (x < 3) {
                        x = x + 1;
                    }

                    if (x > 1) {
                        x = x + 10;
                    } else {
                        x = x - 5;
                    }

                    return x;
                    """;

                // ============================
                // 2️⃣ Lexer
                // ============================
                lexer lex = new lexer(source);
                var tokens = lex.tokeniser();

                // ============================
                // 3️⃣ Parser → AST
                // ============================
                parcer parser = new parcer(tokens);
                parcer.programmeNoeud ast = parser.parcerProgramme();

                System.out.println("=== AST ===");
                System.out.println(ast);

                // ============================
                // 4️⃣ Analyse sémantique
                // ============================
                semantique sem = new semantique();
                sem.analyser(ast);

                if (!sem.getErreurs().isEmpty()) {
                    System.err.println("❌ Erreurs sémantiques détectées");
                    sem.getErreurs();
                    return;
                }

                // ============================
                // 5️⃣ AST → HIR
                // ============================
                ASTtoHIR astToHIR = new ASTtoHIR();
                HIR.Block hir = null;//astToHIR.convert(ast);

                System.out.println("\n=== HIR ===");
                System.out.println(hir);

                // ============================
                // 6️⃣ HIR → LIR
                // ============================
                LIR.Block lir = new LIR.Block();
                hir.lower(lir);

                // ============================
                // 7️⃣ Affichage LIR
                // ============================
                System.out.println("\n=== LIR LINEARIZED ===");
                System.out.println(lir);

            } catch (Exception e) {
                System.err.println("💥 Erreur fatale du compilateur");
                e.printStackTrace();
            }
        }
    }


}

