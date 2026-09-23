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
import java.util.*;

public class HIR {

    // ===== BASE =====
    public interface Node {
        void lower(LIR.Block out);
    }

    public interface Expr {
        LIR.Expr lowerExpr(LIR.Block out);
    }

    // ===== BLOCK =====
    public static class Block implements Node {
        public List<Node> nodes = new ArrayList<>();

        public void add(Node n) { nodes.add(n); }

        @Override
        public void lower(LIR.Block out) {
            for (Node n : nodes) n.lower(out);
        }
    }

    // ===== EXPRESSIONS =====
    public static class Var implements Expr {
        public String name;
        public Var(String n) { name = n; }

        public LIR.Expr lowerExpr(LIR.Block out) {
            return new LIR.Var(name);
        }
    }

    public static class Const implements Expr {
        public int value;
        public Const(int v) { value = v; }

        public LIR.Expr lowerExpr(LIR.Block out) {
            return new LIR.Const(value);
        }
    }

    public static class BinOp implements Expr {
        public String op;
        public Expr l, r;

        public BinOp(String op, Expr l, Expr r) {
            this.op = op; this.l = l; this.r = r;
        }

        public LIR.Expr lowerExpr(LIR.Block out) {
            LIR.Expr le = l.lowerExpr(out);
            LIR.Expr re = r.lowerExpr(out);

            LIR.Var tmp = LIR.Var.temp();
            out.add(new LIR.Assign(tmp, new LIR.BinOp(op, le, re)));
            return tmp;
        }
    }

    // ===== ASSIGN =====
    public static class Assign implements Node {
        public String name;
        public Expr value;

        public Assign(String n, Expr v) {
            name = n; value = v;
        }

        public void lower(LIR.Block out) {
            LIR.Expr rhs = value.lowerExpr(out);
            out.add(new LIR.Assign(new LIR.Var(name), rhs));
        }
    }

    // ===== WHILE =====
    public static class While implements Node {
        public Expr cond;
        public Block body;

        public While(Expr c, Block b) {
            cond = c; body = b;
        }

        public void lower(LIR.Block out) {
            String Lcond = LIR.Label.next("while_cond");
            String Lend  = LIR.Label.next("while_end");

            out.add(new LIR.Label(Lcond));

            LIR.Expr c = cond.lowerExpr(out);
            out.add(new LIR.CondJump(c, Lend));

            body.lower(out);

            out.add(new LIR.Jump(Lcond));
            out.add(new LIR.Label(Lend));
        }
    }
    
    // ===== IF / ELSE =====
    public static class If implements Node {
        public Expr cond;
        public Block thenBlock;
        public Block elseBlock;

        public If(Expr c, Block t, Block e) {
            cond = c; thenBlock = t; elseBlock = e;
        }

        @Override
        public void lower(LIR.Block out) {
            String Lelse = LIR.Label.next("if_else");
            String Lend  = LIR.Label.next("if_end");

            LIR.Expr c = cond.lowerExpr(out);
            out.add(new LIR.CondJump(c, Lelse));

            thenBlock.lower(out);
            out.add(new LIR.Jump(Lend));

            out.add(new LIR.Label(Lelse));
            if (elseBlock != null) elseBlock.lower(out);

            out.add(new LIR.Label(Lend));
        }
    }

    // ===== DO-WHILE =====
    public static class Do implements Node {
        public Block body;
        public Expr cond;

        public Do(Block b, Expr c) {
            body = b; cond = c;
        }

        @Override
        public void lower(LIR.Block out) {
            String Lstart = LIR.Label.next("do_start");
            String Lend   = LIR.Label.next("do_end");

            out.add(new LIR.Label(Lstart));
            body.lower(out);

            LIR.Expr c = cond.lowerExpr(out);
            out.add(new LIR.CondJump(c, Lstart)); // boucle tant que vrai
            out.add(new LIR.Label(Lend));
        }
    }

    // ===== FOR =====
    public static class For implements Node {
        public Node init;
        public Expr cond;
        public Node incr;
        public Block body;

        public For(Node i, Expr c, Node inc, Block b) {
            init = i; cond = c; incr = inc; body = b;
        }

        @Override
        public void lower(LIR.Block out) {
            String Lcond = LIR.Label.next("for_cond");
            String Lend  = LIR.Label.next("for_end");

            if (init != null) init.lower(out);

            out.add(new LIR.Label(Lcond));
            if (cond != null) {
                LIR.Expr c = cond.lowerExpr(out);
                out.add(new LIR.CondJump(c, Lend));
            }

            body.lower(out);

            if (incr != null) incr.lower(out);

            out.add(new LIR.Jump(Lcond));
            out.add(new LIR.Label(Lend));
        }
    }

    // ===== BREAK / CONTINUE =====
    public static class Break implements Node {
        public String labelEnd; // label de fin de boucle
        public Break(String endLabel) { labelEnd = endLabel; }

        @Override
        public void lower(LIR.Block out) {
            out.add(new LIR.Jump(labelEnd));
        }
    }

    public static class Continue implements Node {
        public String labelCond; // label de condition
        public Continue(String condLabel) { labelCond = condLabel; }

        @Override
        public void lower(LIR.Block out) {
            out.add(new LIR.Jump(labelCond));
        }
    }

    // ===== RETURN =====
    public static class Return implements Node {
        public Expr value;

        public Return(Expr v) { value = v; }

        @Override
        public void lower(LIR.Block out) {
            LIR.Expr val = value != null ? value.lowerExpr(out) : null;
            out.add(new LIR.Return(val));
        }
    }


    // ===== APPEL DE FONCTION =====
    public static class Call implements Expr {
        public String name;
        public List<Expr> args;

        public Call(String n, List<Expr> a) { name = n; args = a; }

        @Override
        public LIR.Expr lowerExpr(LIR.Block out) {
            List<LIR.Expr> loweredArgs = new ArrayList<>();
            for (Expr e : args) loweredArgs.add(e.lowerExpr(out));

            LIR.Var tmp = LIR.Var.temp();
            out.add(new LIR.Call(tmp, name, loweredArgs));
            return tmp;
        }
    }

    // ===== APPEL DE METHODE SUR OBJET =====
    public static class CallMethod implements Expr {
        public Expr obj;
        public String method;
        public List<Expr> args;

        public CallMethod(Expr o, String m, List<Expr> a) { obj = o; method = m; args = a; }

        @Override
        public LIR.Expr lowerExpr(LIR.Block out) {
            LIR.Expr objL = obj.lowerExpr(out);
            List<LIR.Expr> loweredArgs = new ArrayList<>();
            for (Expr e : args) loweredArgs.add(e.lowerExpr(out));

            LIR.Var tmp = LIR.Var.temp();
            out.add(new LIR.CallMethod(tmp, objL, method, loweredArgs));
            return tmp;
        }
    }

    // ===== CREATION D’OBJET =====
    public static class NewObj implements Expr {
        public String cls;
        public List<Expr> args;

        public NewObj(String c, List<Expr> a) { cls = c; args = a; }

        @Override
        public LIR.Expr lowerExpr(LIR.Block out) {
            List<LIR.Expr> loweredArgs = new ArrayList<>();
            for (Expr e : args) loweredArgs.add(e.lowerExpr(out));

            LIR.Var tmp = LIR.Var.temp();
            out.add(new LIR.NewObj(tmp, cls, loweredArgs));
            return tmp;
        }
    }

    // ===== ACCES CHAMP / TABLEAU =====
    public static class LoadField implements Expr {
        public Expr obj;
        public String field;

        public LoadField(Expr o, String f) { obj = o; field = f; }

        @Override
        public LIR.Expr lowerExpr(LIR.Block out) {
            LIR.Expr objL = obj.lowerExpr(out);
            LIR.Var tmp = LIR.Var.temp();
            out.add(new LIR.LoadField(tmp, objL, field));
            return tmp;
        }
    }

    public static class LoadArray implements Expr {
        public Expr array;
        public List<Expr> indices;

        public LoadArray(Expr a, List<Expr> idx) { array = a; indices = idx; }

        @Override
        public LIR.Expr lowerExpr(LIR.Block out) {
            LIR.Expr arrayL = array.lowerExpr(out);
            List<LIR.Expr> loweredIdx = new ArrayList<>();
            for (Expr i : indices) loweredIdx.add(i.lowerExpr(out));
            LIR.Var tmp = LIR.Var.temp();
            out.add(new LIR.LoadArray(tmp, arrayL, loweredIdx));
            return tmp;
        }
    }

    // ===== TERNARY (?:) =====
    public static class Ternary implements Expr {
        public Expr cond, ifTrue, ifFalse;

        public Ternary(Expr c, Expr t, Expr f) { cond = c; ifTrue = t; ifFalse = f; }

        @Override
        public LIR.Expr lowerExpr(LIR.Block out) {
            LIR.Expr condL = cond.lowerExpr(out);
            LIR.Var tmp = LIR.Var.temp();

            String Lelse = LIR.Label.next("ternary_else");
            String Lend  = LIR.Label.next("ternary_end");

            out.add(new LIR.CondJump(condL, Lelse));
            out.add(new LIR.Assign(tmp, ifTrue.lowerExpr(out)));
            out.add(new LIR.Jump(Lend));

            out.add(new LIR.Label(Lelse));
            out.add(new LIR.Assign(tmp, ifFalse.lowerExpr(out)));

            out.add(new LIR.Label(Lend));
            return tmp;
        }
    }
    
   /* // ===== IF / ELSE =====
public static class If implements Node {
    public Expr cond;
    public Block thenBlock;
    public Block elseBlock;

    public If(Expr c, Block t, Block e) {
        cond = c; thenBlock = t; elseBlock = e;
    }

    public void lower(LIR.Block out) {
        String Lelse = LIR.Label.next("if_else");
        String Lend  = LIR.Label.next("if_end");

        LIR.Expr c = cond.lowerExpr(out);
        out.add(new LIR.CondJump(c, Lelse));

        thenBlock.lower(out);
        out.add(new LIR.Jump(Lend));

        out.add(new LIR.Label(Lelse));
        if (elseBlock != null) elseBlock.lower(out);

        out.add(new LIR.Label(Lend));
    }
}

// ===== RETURN =====
public static class Return implements Node {
    public Expr value;
    public Return(Expr v) { value = v; }

    public void lower(LIR.Block out) {
        LIR.Expr v = value.lowerExpr(out);
        out.add(new LIR.Return(v));
    }
}

// ===== FOR =====
public static class For implements Node {
    public Node init;
    public Expr cond;
    public Node incr;
    public Block body;

    public For(Node i, Expr c, Node inc, Block b) {
        init = i; cond = c; incr = inc; body = b;
    }

    public void lower(LIR.Block out) {
        String Lcond = LIR.Label.next("for_cond");
        String Lend  = LIR.Label.next("for_end");

        if (init != null) init.lower(out);
        out.add(new LIR.Label(Lcond));

        if (cond != null) {
            LIR.Expr c = cond.lowerExpr(out);
            out.add(new LIR.CondJump(c, Lend));
        }

        body.lower(out);

        if (incr != null) incr.lower(out);
        out.add(new LIR.Jump(Lcond));
        out.add(new LIR.Label(Lend));
    }
}

// ===== DO-WHILE =====
public static class Do implements Node {
    public Block body;
    public Expr cond;

    public Do(Block b, Expr c) { body = b; cond = c; }

    public void lower(LIR.Block out) {
        String Lstart = LIR.Label.next("do_start");
        String Lend   = LIR.Label.next("do_end");

        out.add(new LIR.Label(Lstart));
        body.lower(out);

        LIR.Expr c = cond.lowerExpr(out);
        out.add(new LIR.CondJump(c, Lstart));
        out.add(new LIR.Label(Lend));
    }
}

// ===== TERNARY =====
public static class Ternary implements Expr {
    public Expr cond, trueExpr, falseExpr;
    public Ternary(Expr c, Expr t, Expr f) { cond = c; trueExpr = t; falseExpr = f; }

    public LIR.Expr lowerExpr(LIR.Block out) {
        LIR.Expr c = cond.lowerExpr(out);
        LIR.Var tmp = LIR.Var.temp();
        String Lelse = LIR.Label.next("ternary_else");
        String Lend  = LIR.Label.next("ternary_end");

        out.add(new LIR.CondJump(c, Lelse));
        LIR.Expr t = trueExpr.lowerExpr(out);
        out.add(new LIR.Assign(tmp, t));
        out.add(new LIR.Jump(Lend));

        out.add(new LIR.Label(Lelse));
        LIR.Expr f = falseExpr.lowerExpr(out);
        out.add(new LIR.Assign(tmp, f));

        out.add(new LIR.Label(Lend));
        return tmp;
    }
}*/
/*
// ===== BREAK / CONTINUE =====
public static class Break implements Node {
    public void lower(LIR.Block out) { out.add(new LIR.Break()); }
}

public static class Continue implements Node {
    public void lower(LIR.Block out) { out.add(new LIR.Continue()); }
}

// ===== LOAD / STORE FIELD / ARRAY =====
public static class LoadField implements Node {
    public Expr obj;
    public String field;
    public LoadField(Expr o, String f) { obj = o; field = f; }

    public LIR.Expr lowerExpr(LIR.Block out) {
        LIR.Var tmp = LIR.Var.temp();
        LIR.Expr o = obj.lowerExpr(out);
        out.add(new LIR.LoadField(tmp, o, field));
        return tmp;
    }

        @Override
        public void lower(LIR.Block out) {
            return ;//throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

public static class LoadArray implements Expr {
    public Expr array;
    public List<Expr> indices;
    public LoadArray(Expr a, List<Expr> idx) { array = a; indices = idx; }

    public LIR.Expr lowerExpr(LIR.Block out) {
        LIR.Var tmp = LIR.Var.temp();
        List<LIR.Expr> idxs = new ArrayList<>();
        for (Expr e : indices) idxs.add(e.lowerExpr(out));
        out.add(new LIR.LoadArray(tmp, array.lowerExpr(out), idxs));
        return tmp;
    }
}*/



}


