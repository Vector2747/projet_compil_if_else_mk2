/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IR_v2;

/**
 *
 * @author InfoPro
 */


import java.util.*;

public class LIR {

    // ===== BASE =====
    public interface Node {}

    public interface Expr extends Node {}

    // ===== BLOCK =====
    public static class Block {
        public List<Node> code = new ArrayList<>();
        public void add(Node n) { code.add(n); }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Node n : code) sb.append(n).append("\n");
            return sb.toString();
        }
    }

    // ===== EXPRESSIONS =====
    public static class Var implements Expr {
        public String name;
        public Var(String n) { name = n; }

        public static Var temp() {
            return new Var("t" + Temp.next());
        }

        public String toString() { return name; }
    }

    public static class Const implements Expr {
        public int value;
        public Const(int v) { value = v; }
        public String toString() { return Integer.toString(value); }
    }

    public static class BinOp implements Expr {
        public String op;
        public Expr l, r;

        public BinOp(String o, Expr l, Expr r) {
            op = o; this.l = l; this.r = r;
        }

        public String toString() {
            return l + " " + op + " " + r;
        }
    }

    // ===== INSTRUCTIONS =====
    public static class Assign implements Node {
        public Var dst;
        public Expr src;

        public Assign(Var d, Expr s) {
            dst = d; src = s;
        }

        public String toString() {
            return dst + " = " + src;
        }
    }

    public static class Label implements Node {
        public String name;
        public Label(String n) { name = n; }

        public static String next(String base) {
            return base + "_" + Temp.next();
        }

        public String toString() { return name + ":"; }
    }

    public static class Jump implements Node {
        public String label;
        public Jump(String l) { label = l; }
        public String toString() { return "goto " + label; }
    }

    public static class CondJump implements Node {
        public Expr cond;
        public String label;

        public CondJump(Expr c, String l) {
            cond = c; label = l;
        }

        public String toString() {
            return "ifFalse " + cond + " goto " + label;
        }
    }

    // ===== TEMP =====
    static class Temp {
        static int id = 0;
        static int next() { return id++; }
    }
    
    /*public static class Assign implements LIR.Node {
        public LIR.Var lhs;
        public LIR.Expr rhs;
        public Assign(LIR.Var l, LIR.Expr r) { lhs = l; rhs = r; }
        @Override
        public void lower(LIR.Block out) { out.add(this); }
    }*/

    public static class Return implements LIR.Node {
        public LIR.Expr val;
        public Return(LIR.Expr v) { val = v; }
        /*@Override*/
        public void lower(LIR.Block out) { out.add(this); }

        @Override
        public String toString() {
            return "Return{" + "val=" + val + '}';
        }
        
    }

    /*public static class Jump implements LIR.Node {
        public String label;
        public Jump(String l) { label = l; }
        @Override
        public void lower(LIR.Block out) { out.add(this); }
    }*/

    /*public static class CondJump implements LIR.Node {
        public LIR.Expr cond;
        public String label;
        public CondJump(LIR.Expr c, String l) { cond = c; label = l; }
        @Override
        public void lower(LIR.Block out) { out.add(this); }
    }*/

    /*public static class Label implements LIR.Node {
        public String name;
        private static int counter = 0;
        public Label(String n) { name = n; }
        public static String next(String prefix) { return prefix + "_" + (counter++); }
        @Override
        public void lower(LIR.Block out) { out.add(this); }
    }*/


    // ===== APPEL =====
    public static class Call implements Node {
        public LIR.Var result;
        public String name;
        public List<LIR.Expr> args;
        public Call(LIR.Var r, String n, List<LIR.Expr> a) { result = r; name = n; args = a; }
        /*@Override*/ public void lower(LIR.Block out) { out.add(this); }

        @Override
        public String toString() {
            return "Call{" + "result=" + result + ", name=" + name + ", args=" + args + '}';
        }
        
    }

    public static class CallMethod implements Node {
        public LIR.Var result;
        public LIR.Expr obj;
        public String method;
        public List<LIR.Expr> args;
        public CallMethod(LIR.Var r, LIR.Expr o, String m, List<LIR.Expr> a) { result = r; obj = o; method = m; args = a; }
        /*@Override*/ public void lower(LIR.Block out) { out.add(this); }

        @Override
        public String toString() {
            return "CallMethod{" + "result=" + result + ", obj=" + obj + ", method=" + method + ", args=" + args + '}';
        }
        
    }

    public static class NewObj implements Node {
        public LIR.Var result;
        public String cls;
        public List<LIR.Expr> args;
        public NewObj(LIR.Var r, String c, List<LIR.Expr> a) { result = r; cls = c; args = a; }
        /*@Override*/ public void lower(LIR.Block out) { out.add(this); }

        @Override
        public String toString() {
            return "NewObj{" + "result=" + result + ", cls=" + cls + ", args=" + args + '}';
        }
        
    }

    // ===== ACCES CHAMP / TABLEAU =====
    public static class LoadField implements Node {
        public LIR.Var result;
        public LIR.Expr obj;
        public String field;
        public LoadField(LIR.Var r, LIR.Expr o, String f) { result = r; obj = o; field = f; }
        /*@Override*/ public void lower(LIR.Block out) { out.add(this); }

        @Override
        public String toString() {
            return "LoadField{" + "result=" + result + ", obj=" + obj + ", field=" + field + '}';
        }
        
    }

    public static class LoadArray implements Node {
        public LIR.Var result;
        public LIR.Expr array;
        public List<LIR.Expr> indices;
        public LoadArray(LIR.Var r, LIR.Expr a, List<LIR.Expr> idx) { result = r; array = a; indices = idx; }
        /*@Override*/ public void lower(LIR.Block out) { out.add(this); }

        @Override
        public String toString() {
            return "LoadArray{" + "result=" + result + ", array=" + array + ", indices=" + indices + '}';
        }
        
    }
    
    // BREAK / CONTINUE
    public static class Break implements Node { public String toString() { return "break"; } }
    public static class Continue implements Node { public String toString() { return "continue"; } }

/*// LOAD / STORE FIELD / ARRAY
public static class LoadField implements Node {
    public Var result; public Expr obj; public String field;
    public LoadField(Var r, Expr o, String f) { result=r; obj=o; field=f; }
    public String toString() { return result + " = " + obj + "." + field; }
}

public static class LoadArray implements Node {
    public Var result; public Expr array; public List<Expr> indices;
    public LoadArray(Var r, Expr a, List<Expr> idx) { result=r; array=a; indices=idx; }
    public String toString() { return result + " = " + array + indices; }
}*/


}


