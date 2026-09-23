/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IR_v3;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author tafou
 */
public class HIRToLIR {
    
    private int tempCounter = 0;

    private int labelCounter = 0;
    
    private Stack<String> breakLabels = new Stack<>();

    private Stack<String> continueLabels = new Stack<>();
    
    private LIR.LIRTemp newTemp(){

        return new LIR.LIRTemp(

                "t"+(tempCounter++)

        );

    }
    
    private String newLabel(String label,boolean increment){
        
        if(increment){
            return "L_"+label+"_"+(labelCounter++);
        }

        return "L_"+label+"_"+(labelCounter);

    }
    
    public LIR.LIRBlock lower(HIR.HIRProgram program){

        LIR.LIRBlock out = new LIR.LIRBlock();
        
        // Variables globales
        if(program.globals != null){
            for(HIR.HIRVarDecl global : program.globals){
                lowerStmt(global, out);
            }
        }
        
        // ====== LE BLOC PRINCIPAL ======
        if(program.body != null){
            lowerBlock(program.body, out);
        }

        if(program.classes != null){
            for(HIR.HIRClass c : program.classes){
                lowerClass(c, out);
            }
        }
        
        if(program.functions != null){
            for(HIR.HIRFunction f : program.functions){
                lowerFunction(f, out);
            }
        }
        

        return out;
    }
    
    private void lowerClass(HIR.HIRClass cls,
                            LIR.LIRBlock out){
        
        if(cls.fields != null){

            for(HIR.HIRAssign field : cls.fields){

                lowerStmt(field, out);

            }

        }
        
        if(cls.methods != null){

            for(HIR.HIRFunction method : cls.methods){

                lowerFunction(method, out);

            }

        }

    }
    
    private void lowerFunction(HIR.HIRFunction fun,
                                  LIR.LIRBlock out){
        
        // Variables locales
        if(fun.locals != null){

            for(HIR.HIRVarDecl local : fun.locals){

                lowerStmt(local, out);

            }

        }

        // Corp
        lowerBlock(fun.body, out);

    }
    
    private void lowerBlock(HIR.HIRBlock blockHIR,
                                 LIR.LIRBlock out){

        for(HIR.HIRStmt stmt : blockHIR.statements){

            lowerStmt(stmt, out);

        }

    }
    
    private void lowerStmt(HIR.HIRStmt stmt,
                           LIR.LIRBlock out){
        
        /*if(stmt instanceof HIR.HIRAssign assign){

            LIR.LIRValue cible = lowerExpr(assign.target, out);

            LIR.LIRValue valeur = lowerExpr(assign.value, out);

            out.instructions.add(
                    new LIR.LIRAssign(cible, valeur)
            );

            return;
        }*/
        
        if(stmt instanceof HIR.HIRAssign assign){

            LIR.LIRValue valeur = lowerExpr(assign.value, out);

            if(assign.target instanceof HIR.HIRVar var){

                out.instructions.add(
                    new LIR.LIRAssign(
                        new LIR.LIRVariable(var.name),
                        valeur
                    )
                );

            }
            else if(assign.target instanceof HIR.HIRFieldAccess field){

                
                // LIRStoreField
                LIR.LIRValue object = lowerExpr(field.object, out);

                out.instructions.add(

                    new LIR.LIRStoreField(
                            object,
                            field.field,
                            valeur
                    )

                );

            }
            else if(assign.target instanceof HIR.HIRArrayAccess access){

                
                // LIRStoreArray
                LIR.LIRValue array = lowerExpr(access.array, out);

                List<LIR.LIRValue> indices = new ArrayList<>();

                for(HIR.HIRExpr e : access.indices){

                    indices.add(
                            lowerExpr(e, out)
                    );

                }

                out.instructions.add(

                    new LIR.LIRStoreArray(
                            array,
                            indices,
                            valeur
                    )

                );

            }

            return;
        }
        
        if(stmt instanceof HIR.HIRReturn ret){

            LIR.LIRValue value = null;

            if(ret.value != null){
                value = lowerExpr(ret.value, out);
            }

            out.instructions.add(

                new LIR.LIRReturn(value)

            );

            return;

        }
        
        /*if(stmt instanceof HIR.HIRIf iff){

            LIR.LIRValue condition = lowerExpr(iff.condition, out);

            if(iff.elseBlock != null){

                String elseLabel = newLabel("else", true);
                String endLabel  = newLabel("end", false);

                out.instructions.add(
                    new LIR.LIRCondJump(
                        condition,
                        elseLabel,
                        false      // si faux -> else
                    )
                );

                // THEN
                lowerBlock(iff.thenBlock, out);

                out.instructions.add(
                    new LIR.LIRJump(endLabel)
                );

                // ELSE
                out.instructions.add(
                    new LIR.LIRLabel(elseLabel)
                );

                lowerBlock(iff.elseBlock, out);

                // END
                out.instructions.add(
                    new LIR.LIRLabel(endLabel)
                );
            }
            else{

                String endLabel = newLabel("end", true);

                out.instructions.add(
                    new LIR.LIRCondJump(
                        condition,
                        endLabel,
                        false          // jump si condition == false
                    )
                );

                lowerBlock(iff.thenBlock, out);

                out.instructions.add(
                    new LIR.LIRLabel(endLabel)
                );

            }



            return;
        }*/
        
        if(stmt instanceof HIR.HIRIf iff){

            String thenLabel = newLabel("then", true);
            String elseLabel = newLabel("else", false);
            String endLabel  = newLabel("end", false);


            lowerCondition(
                    iff.condition,
                    thenLabel,
                    elseLabel,
                    out
            );


            // THEN
            out.instructions.add(
                    new LIR.LIRLabel(thenLabel)
            );

            lowerBlock(iff.thenBlock, out);


            out.instructions.add(
                    new LIR.LIRJump(endLabel)
            );


            // ELSE
            out.instructions.add(
                    new LIR.LIRLabel(elseLabel)
            );

            if(iff.elseBlock != null){
                lowerBlock(iff.elseBlock, out);
            }


            // END
            out.instructions.add(
                    new LIR.LIRLabel(endLabel)
            );


            return;
        }
        
        if(stmt instanceof HIR.HIRWhile wh){

            String condLabel = newLabel("while_cond", true);
            String bodyLabel = newLabel("while_body", false);
            String endLabel  = newLabel("while_end", false);
            
            continueLabels.push(condLabel);
            breakLabels.push(endLabel);

            // début de la boucle
            out.instructions.add(
                new LIR.LIRLabel(condLabel)
            );

            LIR.LIRValue condition = lowerExpr(wh.condition, out);

            // si vrai -> body
            out.instructions.add(
                new LIR.LIRCondJump(
                    condition,
                    bodyLabel,
                    true
                )
            );

            // sinon -> fin
            out.instructions.add(
                new LIR.LIRJump(endLabel)
            );

            // corps
            out.instructions.add(
                new LIR.LIRLabel(bodyLabel)
            );

            lowerBlock(wh.body, out);

            // retour au test
            out.instructions.add(
                new LIR.LIRJump(condLabel)
            );

            // fin
            out.instructions.add(
                new LIR.LIRLabel(endLabel)
            );
            
            continueLabels.pop();
            breakLabels.pop();

            return;
        }
        
        if(stmt instanceof HIR.HIRDoWhile dw){

            String bodyLabel = newLabel("do_body", true);
            String condLabel = newLabel("do_cond", false);
            String endLabel  = newLabel("do_end", false);

            // break et continue
            breakLabels.push(endLabel);
            continueLabels.push(condLabel);

            // corps
            out.instructions.add(
                new LIR.LIRLabel(bodyLabel)
            );

            lowerBlock(dw.body, out);

            // test
            out.instructions.add(
                new LIR.LIRLabel(condLabel)
            );

            LIR.LIRValue condition = lowerExpr(dw.condition, out);

            out.instructions.add(
                new LIR.LIRCondJump(
                    condition,
                    bodyLabel,
                    true
                )
            );

            // fin
            out.instructions.add(
                new LIR.LIRLabel(endLabel)
            );

            continueLabels.pop();
            breakLabels.pop();

            return;
        }
        
        if(stmt instanceof HIR.HIRFor fr){

            String condLabel = newLabel("for_cond", true);
            String endLabel  = newLabel("for_end", false);
            
            continueLabels.push(condLabel);
            breakLabels.push(endLabel);

            // initialisation
            if(fr.init != null){
                lowerStmt(fr.init, out);
            }

            // condition
            out.instructions.add(
                new LIR.LIRLabel(condLabel)
            );

            if(fr.condition != null){

                LIR.LIRValue condition = lowerExpr(fr.condition, out);

                out.instructions.add(
                    new LIR.LIRCondJump(
                        condition,
                        endLabel,
                        false      // jump si faux
                    )
                );
            }

            // corps
            lowerBlock(fr.body, out);

            // incrément
            if(fr.increment != null){
                lowerStmt(fr.increment, out);
            }

            // retour au test
            out.instructions.add(
                new LIR.LIRJump(condLabel)
            );

            // fin
            out.instructions.add(
                new LIR.LIRLabel(endLabel)
            );
            
            continueLabels.pop();
            breakLabels.pop();

            return;
        }
        
        if(stmt instanceof HIR.HIRBreak){

            out.instructions.add(

                new LIR.LIRJump(
                    breakLabels.peek()
                )

            );

            return;
        }
        
        if(stmt instanceof HIR.HIRContinue){

            out.instructions.add(

                new LIR.LIRJump(
                    continueLabels.peek()
                )

            );

            return;
        }
        
        if(stmt instanceof HIR.HIRExprStmt exprStmt){

            lowerExpr(exprStmt.expr, out);

            return;
        }
        
        if(stmt instanceof HIR.HIRVarDecl decl){

            if(decl.initializer != null){

                LIR.LIRValue value = lowerExpr(decl.initializer, out);

                out.instructions.add(
                    new LIR.LIRAssign(
                        new LIR.LIRVariable(decl.name),
                        value
                    )
                );
            }

            return;
        }

    }
    
    private LIR.LIRValue lowerExpr(HIR.HIRExpr expr,
                                   LIR.LIRBlock out){
        
        if(expr instanceof HIR.HIRVar var){

            return new LIR.LIRVariable(var.name);

        }
        
        
        
        if(expr instanceof HIR.HIRConst c){

            return new LIR.LIRConstant(c.value);

        }
        
        if(expr instanceof HIR.HIRBinary bin){
            
            if (bin.op.equals("AND")) {
                LIR.LIRValue left = lowerExpr(bin.left, out);

                String falseLabel = newLabel("and_false", true);
                String endLabel   = newLabel("and_end", false);

                LIR.LIRTemp result = newTemp(/*bin.type*/);
                
                out.instructions.add(
                    new LIR.LIRCondJump(
                        left,
                        falseLabel,
                        false
                    )
                );
                
                LIR.LIRValue right = lowerExpr(bin.right, out);

                out.instructions.add(
                    new LIR.LIRCondJump(
                        right,
                        falseLabel,
                        false
                    )
                );
                
                out.instructions.add(
                    new LIR.LIRAssign(
                        result,
                        new LIR.LIRConstant(true)
                    )
                );

                out.instructions.add(
                    new LIR.LIRJump(endLabel)
                );

                out.instructions.add(
                    new LIR.LIRLabel(falseLabel)
                );

                out.instructions.add(
                    new LIR.LIRAssign(
                        result,
                        new LIR.LIRConstant(false)
                    )
                );

                out.instructions.add(
                    new LIR.LIRLabel(endLabel)
                );

                return result;
            }

            if (bin.op.equals("OR")) {
                //...
            }

            LIR.LIRValue left = lowerExpr(bin.left, out);

            LIR.LIRValue right = lowerExpr(bin.right, out);

            LIR.LIRTemp temp = newTemp();

            out.instructions.add(

                new LIR.LIRBinary(
                        bin.op,
                        temp,
                        left,
                        right
                )

            );

            return temp;

        }
        
        if(expr instanceof HIR.HIRUnary unary){

            LIR.LIRValue operand = lowerExpr(unary.operand, out);

            LIR.LIRTemp temp = newTemp();

            out.instructions.add(

                new LIR.LIRUnary(
                    unary.op.name(),
                    temp,
                    operand
                )

            );

            return temp;
        }
        
        if(expr instanceof HIR.HIRCall call){

            List<LIR.LIRValue> args = new ArrayList<>();

            for(HIR.HIRExpr e : call.arguments){

                args.add(lowerExpr(e, out));

            }

            LIR.LIRTemp result = newTemp();

            out.instructions.add(

                new LIR.LIRCall(

                    result,

                    call.function,

                    args

                )

            );

            return result;

        }
        
        if(expr instanceof HIR.HIRMethodCall call){

            LIR.LIRValue object = lowerExpr(call.object, out);

            List<LIR.LIRValue> args = new ArrayList<>();

            for(HIR.HIRExpr e : call.arguments){
                args.add(lowerExpr(e, out));
            }

            LIR.LIRTemp result = newTemp();

            out.instructions.add(

                new LIR.LIRMethodCall(
                        result,
                        object,
                        call.method,
                        args
                )

            );

            return result;
        }
        
        if(expr instanceof HIR.HIRFieldAccess field){

            LIR.LIRValue object = lowerExpr(field.object, out);

            LIR.LIRTemp result = newTemp();

            out.instructions.add(

                new LIR.LIRLoadField(
                        result,
                        object,
                        field.field
                )

            );

            return result;
        }
        
        if(expr instanceof HIR.HIRArrayAccess access){

            LIR.LIRValue array = lowerExpr(access.array, out);

            List<LIR.LIRValue> indices = new ArrayList<>();

            for(HIR.HIRExpr e : access.indices){

                indices.add(lowerExpr(e, out));

            }

            LIR.LIRTemp result = newTemp();

            out.instructions.add(

                new LIR.LIRLoadArray(
                        result,
                        array,
                        indices
                )

            );

            return result;
        }
        
        if(expr instanceof HIR.HIRNewObject obj){

            List<LIR.LIRValue> args = new ArrayList<>();

            for(HIR.HIRExpr e : obj.arguments){

                args.add(lowerExpr(e, out));

            }

            LIR.LIRTemp result = newTemp();

            out.instructions.add(

                new LIR.LIRNewObject(
                        result,
                        obj.className,
                        args
                )

            );

            return result;

        }
        
        if(expr instanceof HIR.HIRNewArray arr){

            List<LIR.LIRValue> sizes = new ArrayList<>();

            for(HIR.HIRExpr e : arr.sizes)
                sizes.add(lowerExpr(e,out));

            LIR.LIRTemp result = newTemp();

            out.instructions.add(
                new LIR.LIRNewArray(
                    result,
                    arr.elementType,
                    sizes
                )
            );

            return result;
        }
        
        if(expr instanceof HIR.HIRTernary ternary){

            LIR.LIRValue condition =
                    lowerExpr(ternary.condition, out);

            String elseLabel =
                    newLabel("ternary_else", true);

            String endLabel =
                    newLabel("ternary_end", false);

            LIR.LIRTemp result = newTemp();

            out.instructions.add(

                new LIR.LIRCondJump(
                        condition,
                        elseLabel,
                        false
                )

            );

            LIR.LIRValue trueValue =
                    lowerExpr(ternary.trueExpr, out);

            out.instructions.add(

                new LIR.LIRAssign(
                        result,
                        trueValue
                )

            );

            out.instructions.add(

                new LIR.LIRJump(endLabel)

            );

            out.instructions.add(

                new LIR.LIRLabel(elseLabel)

            );

            LIR.LIRValue falseValue =
                    lowerExpr(ternary.falseExpr, out);

            out.instructions.add(

                new LIR.LIRAssign(
                        result,
                        falseValue
                )

            );

            out.instructions.add(

                new LIR.LIRLabel(endLabel)

            );

            return result;
        }

        return null;

    }
    
    private void lowerCondition(
        HIR.HIRExpr expr,
        String trueLabel,
        String falseLabel,
        LIR.LIRBlock out){

            if(expr instanceof HIR.HIRBinary bin){

            if(bin.op.equals("AND")){

                String midLabel = newLabel("and_right", false);


                // tester gauche
                lowerCondition(
                        bin.left,
                        midLabel,
                        falseLabel,
                        out
                );


                // endroit où on arrive si gauche == true
                out.instructions.add(
                        new LIR.LIRLabel(midLabel)
                );


                // maintenant seulement on teste droite
                lowerCondition(
                        bin.right,
                        trueLabel,
                        falseLabel,
                        out
                );


                return;
            }
            
            if(bin.op.equals("OR")){


                String midLabel = newLabel("or_right", false);


                lowerCondition(
                        bin.left,
                        trueLabel,
                        midLabel,
                        out
                );


                out.instructions.add(
                        new LIR.LIRLabel(midLabel)
                );


                lowerCondition(
                        bin.right,
                        trueLabel,
                        falseLabel,
                        out
                );


                return;
            }

        }
        
        LIR.LIRValue cond = lowerExpr(expr,out);

        out.instructions.add(
            new LIR.LIRCondJump(
                cond,
                trueLabel,
                true
            )
        );

        out.instructions.add(
            new LIR.LIRJump(falseLabel)
        );
    }
}
