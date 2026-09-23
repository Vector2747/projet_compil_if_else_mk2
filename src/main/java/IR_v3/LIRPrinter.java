/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IR_v3;

/**
 *
 * @author tafou
 */
public class LIRPrinter {

    private static final StringBuilder sb = new StringBuilder();

    public static String print(LIR.LIRBlock block) {

        sb.setLength(0);

        visitBlock(block);

        return sb.toString();
    }

    private static void visitBlock(LIR.LIRBlock block){

        for(LIR.LIRInstruction inst : block.instructions){
            visitInstruction(inst);
        }
    }

    private static void visitInstruction(LIR.LIRInstruction inst){

        if(inst instanceof LIR.LIRLabel l){

            println(l.name + ":");
            return;
        }

        if(inst instanceof LIR.LIRAssign a){

            println(
                value(a.destination)
                + " = "
                + value(a.source)
            );
            return;
        }

        if(inst instanceof LIR.LIRBinary b){

            println(
                value(b.result)
                + " = "
                + b.op
                + " "
                + value(b.left)
                + ", "
                + value(b.right)
            );
            return;
        }

        if(inst instanceof LIR.LIRUnary u){

            println(
                value(u.result)
                + " = "
                + u.op
                + " "
                + value(u.operand)
            );
            return;
        }

        if(inst instanceof LIR.LIRJump j){

            println("JUMP " + j.label);
            return;
        }

        if(inst instanceof LIR.LIRCondJump j){

            println(
                "IF "
                + value(j.condition)
                + (j.jumpIfTrue ? " GOTO " : " IF_FALSE GOTO ")
                + j.label
            );

            return;
        }

        if(inst instanceof LIR.LIRReturn r){

            println("RETURN " + value(r.value));
            return;
        }

        if(inst instanceof LIR.LIRCall c){

            StringBuilder args = new StringBuilder();

            for(int i=0;i<c.arguments.size();i++){

                if(i>0) args.append(", ");

                args.append(value(c.arguments.get(i)));
            }

            println(
                value(c.result)
                + " = CALL "
                + c.function
                + "("
                + args
                + ")"
            );

            return;
        }

        if(inst instanceof LIR.LIRMethodCall c){

            StringBuilder args = new StringBuilder();

            for(int i=0;i<c.arguments.size();i++){

                if(i>0) args.append(", ");

                args.append(value(c.arguments.get(i)));
            }

            println(
                value(c.result)
                + " = CALL "
                + value(c.object)
                + "."
                + c.method
                + "("
                + args
                + ")"
            );

            return;
        }

        if(inst instanceof LIR.LIRLoadField f){

            println(
                value(f.result)
                + " = "
                + value(f.object)
                + "."
                + f.field
            );

            return;
        }

        if(inst instanceof LIR.LIRStoreField f){

            println(
                value(f.object)
                + "."
                + f.field
                + " = "
                + value(f.value)
            );

            return;
        }

        if(inst instanceof LIR.LIRLoadArray a){

            StringBuilder idx = new StringBuilder();

            for(int i=0;i<a.indices.size();i++){

                idx.append("[");
                idx.append(value(a.indices.get(i)));
                idx.append("]");
            }

            println(
                value(a.result)
                + " = "
                + value(a.array)
                + idx
            );

            return;
        }

        if(inst instanceof LIR.LIRStoreArray a){

            StringBuilder idx = new StringBuilder();

            for(int i=0;i<a.indices.size();i++){

                idx.append("[");
                idx.append(value(a.indices.get(i)));
                idx.append("]");
            }

            println(
                value(a.array)
                + idx
                + " = "
                + value(a.value)
            );

            return;
        }

        if(inst instanceof LIR.LIRNewObject n){

            StringBuilder args = new StringBuilder();

            for(int i=0;i<n.arguments.size();i++){

                if(i>0) args.append(", ");

                args.append(value(n.arguments.get(i)));
            }

            println(
                value(n.result)
                + " = NEW "
                + n.className
                + "("
                + args
                + ")"
            );

            return;
        }

        if(inst instanceof LIR.LIRNewArray n){

            StringBuilder sizes = new StringBuilder();

            for(LIR.LIRValue s : n.sizes){

                sizes.append("[");
                sizes.append(value(s));
                sizes.append("]");
            }

            println(
                value(n.result)
                + " = NEWARRAY "
                + n.elementType
                + sizes
            );

            return;
        }

        println("<?> " + inst.getClass().getSimpleName());
    }

    private static String value(LIR.LIRValue v){

        if(v == null)
            return "null";

        if(v instanceof LIR.LIRTemp t)
            return t.name;

        if(v instanceof LIR.LIRVariable var)
            return var.name;

        if(v instanceof LIR.LIRConstant c)
            return String.valueOf(c.value);

        return "<?>";
    }

    private static void println(String s){
        sb.append(s).append('\n');
    }

}
