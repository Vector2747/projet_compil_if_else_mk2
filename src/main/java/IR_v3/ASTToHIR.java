/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IR_v3;

import com.mycompany.projet_compil_if_else_3.parcer.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tafou
 */
public class ASTToHIR {
    private boolean estNombre(String s){
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean estString(String s){
        return s.length() >= 2 &&
               s.startsWith("\"") &&
               s.endsWith("\"");
    }

    private boolean estChar(String s){
        return s.length() >= 3 &&
               s.startsWith("'") &&
               s.endsWith("'");
    }

    private boolean estBoolean(String s){
        return s.equals("true") || s.equals("false");
    }
    
    
    public HIR.HIRProgram convert(programmeNoeud program) {
        List<HIR.HIRClass> classes = new ArrayList<>();
        List<HIR.HIRFunction> functions = new ArrayList<>();
        List<HIR.HIRVarDecl> globals = new ArrayList<>();
        List<HIR.HIRStmt> stmts = new ArrayList<>();
        HIR.HIRBlock body = new HIR.HIRBlock(null);

        // on parcourra le programme ici
        for(noeud n : program.mots){

            if(n instanceof ClasseNode){
                classes.add(convertClass((ClasseNode)n));
            }

            else if(n instanceof MethodeNode){
                functions.add(convertMethod((MethodeNode)n));
            }

            else if(n instanceof VariableNode){
                globals.add(convertVariable((VariableNode)n));
            }
            
            if (n instanceof noeud_declaration_multiple decl) {

                for (noeud var : decl.variables) {

                    if (var instanceof VariableNode v) {
                        globals.add(convertVariable(v));
                    }
                }

                continue;
            }
            
            else{
                stmts.add(convertStmt(n));
            }

        }
        
        body.statements = stmts;

        return new HIR.HIRProgram(classes, functions, globals, body);
    }
    
    
    
    private HIR.HIRClass convertClass(ClasseNode cls){

        List<HIR.HIRFunction> methods = new ArrayList<>();
        List<HIR.HIRAssign> fields = new ArrayList<>();

        for(noeud n : cls.membres){

            if(n instanceof MethodeNode){

                methods.add(convertFunction((MethodeNode)n));

            }

            else if(n instanceof VariableNode){

                VariableNode v=(VariableNode)n;

                HIR.HIRExpr init =

                        v.valeur==null

                        ? null

                        : convertExpr(v.valeur);

                fields.add(

                    new HIR.HIRAssign(

                        new HIR.HIRVar(v.nom,v.type),

                        init

                    )

                );

            }

        }

        return new HIR.HIRClass(

                cls.nom,

                methods,

                fields

        );

    }
    
    private HIR.HIRFunction convertFunction(MethodeNode m){

        List<HIR.HIRParameter> params = new ArrayList<>();

        for(noeud_parametre p : m.Pretour){

            params.add(

                new HIR.HIRParameter(

                        p.nom,

                        p.type

                )

            );

        }

        List<HIR.HIRStmt> stmts = new ArrayList<>();

        for(noeud n : m.instructions){

            stmts.add(convertStmt(n));

        }

        return new HIR.HIRFunction(

                m.nom,

                m.typeInfered,

                params,

                new HIR.HIRBlock(stmts)

        );

    }
    
    private HIR.HIRFunction convertMethod(MethodeNode node){
        return null;
    }
    
    private HIR.HIRVarDecl convertVariable(VariableNode node){
        HIR.HIRExpr initializer = null;

    if(node.valeur != null){
        initializer = convertExpr(node.valeur);
    }

    return new HIR.HIRVarDecl(
            node.nom,
            node.type,
            initializer,
            node.immutable
    );
    }
    
    private HIR.HIRExpr convertExpr(noeud node){
        
        System.out.println("convert expression :"+ node);

        if(node == null){
            return null;
        }

        // ici on ajoutera les cas

        if(node instanceof noeud_de_valeur val){

            // ----- constantes -----

            if(estNombre(val.valeur))
                return new HIR.HIRConst(Integer.parseInt(val.valeur), val.typeInfered);

            if(estString(val.valeur))
                return new HIR.HIRConst(
                        val.valeur.substring(1, val.valeur.length()-1),
                        val.typeInfered);

            if(estChar(val.valeur))
                return new HIR.HIRConst(
                        val.valeur.charAt(1),
                        val.typeInfered);

            if(estBoolean(val.valeur))
                return new HIR.HIRConst(
                        Boolean.parseBoolean(val.valeur),
                        val.typeInfered);

            if(val.valeur.equals("null"))
                return new HIR.HIRConst(null, val.typeInfered);


            // ----- sinon c'est une variable -----

            return new HIR.HIRVar(val.valeur, val.typeInfered);
        }

        
        if(node instanceof noeud_d_operation_binaire op){

            return new HIR.HIRBinary(

                    op.operateur.name(),

                    convertExpr(op.gauche),

                    convertExpr(op.droite),

                    op.typeInfered

            );

        }
        
        if(node instanceof noeud_appel_methode call){

            List<HIR.HIRExpr> args = new ArrayList<>();

            for(noeud arg : call.arguments){
                args.add(convertExpr(arg));
            }

            return new HIR.HIRCall(

                    call.nom,

                    args,

                    call.typeInfered

            );

        }
        
        if(node instanceof noeud_constructeur obj){

            List<HIR.HIRExpr> args = new ArrayList<>();

            for(noeud arg : obj.arguments){
                args.add(convertExpr(arg));
            }

            return new HIR.HIRNewObject(

                    obj.classe,

                    args,

                    obj.typeInfered

            );

        }
        
        if(node instanceof NoeudAppelMethodeObjet call){

            List<HIR.HIRExpr> args = new ArrayList<>();

            for(noeud arg : call.arguments){
                args.add(convertExpr(arg));
            }

            return new HIR.HIRMethodCall(

                    convertExpr(call.objet),

                    call.methode,

                    args,

                    call.typeInfered

            );

        }
        
        if(node instanceof NoeudAccesChamp champ){

            return new HIR.HIRFieldAccess(

                    convertExpr(champ.objet),

                    champ.champ,

                    champ.typeInfered

            );

        }
        
        if(node instanceof NoeudAccesTableau tab){

            List<HIR.HIRExpr> indices = new ArrayList<>();

            for(noeud n : tab.indices){
                indices.add(convertExpr(n));
            }

            return new HIR.HIRArrayAccess(

                    new HIR.HIRVar(tab.nom, tab.typeInfered),

                    indices,

                    tab.typeInfered

            );

        }
        
        if(node instanceof NoeudCreationTableau arr){

            List<HIR.HIRExpr> tailles = new ArrayList<>();

            for(noeud n : arr.tailles){
                tailles.add(convertExpr(n));
            }

            return new HIR.HIRNewArray(

                    arr.typeInfered,

                    tailles,

                    arr.typeInfered

            );

        }
        
        if(node instanceof noeud_ternaire t){

            return new HIR.HIRTernary(

                    convertExpr(t.condition),

                    convertExpr(t.vraiExpr),

                    convertExpr(t.fauxExpr),

                    t.typeInfered

            );

        }
        
        /*if(node instanceof noeud_d_operation_binaire op){

            HIR.HIRExpr left  = convertExpr(op.gauche);
            HIR.HIRExpr right = convertExpr(op.droite);

            return new HIR.HIRBinary(
                    op.operateur.name(),
                    left,
                    right,
                    op.typeInfered
            );
        }*/
        
        if(node instanceof noeud_de_condition cond){
            return convertCondition(cond);
        }
        

        throw new RuntimeException(
            "Expression non supportée : "
            + node.getClass().getSimpleName()
        );
    }
    
    private HIR.HIRStmt convertStmt(noeud node){
        
        System.out.println("convert statmenets :"+ node);

        if(node instanceof noeud_d_asignation assign){

            HIR.HIRExpr target = convertExpr(assign.cible);
            HIR.HIRExpr value  = convertExpr(assign.valeur);

            return new HIR.HIRAssign(target, value);
        }
        
        if (node instanceof noeud_declaration_multiple decl) {

            HIR.HIRBlock block = new HIR.HIRBlock(new ArrayList<>());

            for (noeud var : decl.variables) {
                block.statements.add(convertStmt(var));
            }

            return block;
        }
        
        if(node instanceof VariableNode var){

            HIR.HIRExpr init = null;

            if(var.valeur != null){
                init = convertExpr(var.valeur);
            }

            return new HIR.HIRVarDecl(
                    var.nom,
                    var.type,
                    init,
                    var.immutable
            );
        }
        
        if(node instanceof noeud_si si){

            HIR.HIRExpr condition = convertExpr(si.condition);

            HIR.HIRBlock thenBlock = convertBlock(si.blockAlors);

            HIR.HIRBlock elseBlock = null;

            if(si.block_sinon != null){
                elseBlock = convertBlock(si.block_sinon);
            }

            return new HIR.HIRIf(
                    condition,
                    thenBlock,
                    elseBlock
            );
        }
        
        if(node instanceof noeud_while wh){

            return new HIR.HIRWhile(
                    convertExpr(wh.condition),
                    convertBlock(wh.block)
            );

        }
        
        if(node instanceof noeud_do d){

            return new HIR.HIRDoWhile(

                    convertBlock(d.block),

                    convertExpr(d.condition.condition)

            );

        }
        
        if(node instanceof noeud_for fr){
            HIR.HIRVarDecl init =
                new HIR.HIRVarDecl(

                        fr.compteur.variable,

                        fr.compteur.type,

                        convertExpr(fr.compteur.valeurDepart),
                                
                        false

                );
            
            HIR.HIRExpr cond =
            convertExpr(fr.compteur.limite);
            
            HIR.HIRStmt inc =
            convertStmt(fr.compteur.pas);
            
            HIR.HIRBlock body =
                convertBlock(fr.block);

                return new HIR.HIRFor(

                init,

                cond,

                inc,

                body

            );
        }
        
        if(node instanceof noeud_return ret){

            return new HIR.HIRReturn(

                    ret.valeur == null
                        ? null
                        : convertExpr(ret.valeur)

            );

        }
        
        if(node instanceof noeud_break){

            return new HIR.HIRBreak();

        }
        
        if(node instanceof noeud_continue){

            return new HIR.HIRContinue();

        }
        
        
    

        return null;
    }
    
    private HIR.HIRBlock convertBlock(block_de_noeuds block){

        List<HIR.HIRStmt> statements = new ArrayList<>();

        for(noeud n : block.mots){

            HIR.HIRStmt stmt = convertStmt(n);

            if(stmt != null){
                statements.add(stmt);
            }

        }

        return new HIR.HIRBlock(statements);
    }
    
    private HIR.HIRExpr convertCondition(noeud_de_condition cond){

        switch(cond.operateur){

            // ----------------------------
            // Comparaisons
            // ----------------------------

            case GT:
                return new HIR.HIRBinary(
                        "GT",
                        convertExpr(cond.gaucheExpr),
                        convertExpr(cond.droiteExpr),
                        cond.typeInfered
                );

            case LT:
                return new HIR.HIRBinary(
                        "LT",
                        convertExpr(cond.gaucheExpr),
                        convertExpr(cond.droiteExpr),
                        cond.typeInfered
                );

            case GTE:
                return new HIR.HIRBinary(
                        "GTE",
                        convertExpr(cond.gaucheExpr),
                        convertExpr(cond.droiteExpr),
                        cond.typeInfered
                );

            case LTE:
                return new HIR.HIRBinary(
                        "LTE",
                        convertExpr(cond.gaucheExpr),
                        convertExpr(cond.droiteExpr),
                        cond.typeInfered
                );

            case EQEQ:
                return new HIR.HIRBinary(
                        "EQEQ",
                        convertExpr(cond.gaucheExpr),
                        convertExpr(cond.droiteExpr),
                        cond.typeInfered
                );

            case NOTEQ:
                return new HIR.HIRBinary(
                        "NOTEQ",
                        convertExpr(cond.gaucheExpr),
                        convertExpr(cond.droiteExpr),
                        cond.typeInfered
                );

            // ----------------------------
            // ET logique
            // ----------------------------

            case AND:
                return new HIR.HIRBinary(
                        "AND",
                        convertCondition((noeud_de_condition)cond.gaucheExpr),
                        convertCondition((noeud_de_condition)cond.droiteExpr),
                        cond.typeInfered
                );

            // ----------------------------
            // OU logique
            // ----------------------------

            case OR:
                return new HIR.HIRBinary(
                        "OR",
                        convertCondition((noeud_de_condition)cond.gaucheExpr),
                        convertCondition((noeud_de_condition)cond.droiteExpr),
                        cond.typeInfered
                );

            // ----------------------------
            // !
            // ----------------------------

            case NOT:
                return new HIR.HIRUnary(
                        HIR.BinaryOp.NOT,
                        convertCondition((noeud_de_condition)cond.droiteExpr),
                        cond.typeInfered
                );

            // ----------------------------
            // booléen simple
            // ----------------------------

            case ZAKARIA:
                return convertExpr(cond.gaucheExpr);

            default:
                throw new RuntimeException(
                        "Condition non supportée : " + cond.operateur
                );
        }
    }
}
