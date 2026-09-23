/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IR_v3;

import java.util.List;
import com.mycompany.projet_compil_if_else_3.semantique.Type;

/**
 *
 * @author tafou
 */
public class HIR {
    
    /*public enum Type {INT,

DOUBLE,

BOOLEAN,

STRING,

CHAR,

NULL}* /
    public static class Type{
        String baseType;

        boolean isArray;

        int dimensions;

        boolean isPrimitive;
        
        boolean isNullable;

        boolean isFinal;

        boolean isEnum;
    }*/
    
    public enum BinaryOp{
        ADD,
        SUB,
        MUL,
        DIV,
        MOD,

        GT,
        LT,
        EQ,
        NE,
        GE,
        LE,

        AND,
        OR,
        
        NEG,    // -
        NOT     // !
    }
    
    public static abstract class HIRNode{
        public int id;
    }
    
    public static abstract class HIRStmt extends HIRNode{}
    
    public static abstract class HIRExpr extends HIRNode{
        public Type type;

        
        
    }
    
    public static class HIRBlock extends HIRStmt{
        public List<HIRStmt> statements;

        public HIRBlock(List<HIRStmt> statements) {
            this.statements = statements;
        }
    }
    
    public static class HIRAssign extends HIRStmt{
        public HIRExpr target;
        public HIRExpr value;

        public HIRAssign(HIRExpr target, HIRExpr value) {
            this.target = target;
            this.value = value;
        }
    }
    
    public static class HIRIf extends HIRStmt{
        public HIRExpr condition;

        public HIRBlock thenBlock;

        public HIRBlock elseBlock;

        public HIRIf(HIRExpr condition, HIRBlock thenBlock, HIRBlock elseBlock) {
            this.condition = condition;
            this.thenBlock = thenBlock;
            this.elseBlock = elseBlock;
        }
    }
    
    public static class HIRWhile extends HIRStmt{
        public HIRExpr condition;

        public HIRBlock body;

        public HIRWhile(HIRExpr condition, HIRBlock body) {
            this.condition = condition;
            this.body = body;
        }
        
    }
    
    public static class HIRDoWhile extends HIRStmt{
        public HIRBlock body;

        public HIRExpr condition;

        public HIRDoWhile(HIRBlock body, HIRExpr condition) {
            this.body = body;
            this.condition = condition;
        }
    }
    
    public static class HIRFor extends HIRStmt{
        public HIRStmt init;

        public HIRExpr condition;

        public HIRStmt increment;

        public HIRBlock body;
        
        public HIRFor(HIRStmt init, HIRExpr condition, HIRStmt increment, HIRBlock body) {
            this.init = init;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }
    }
    
    public static class HIRReturn extends HIRStmt{
        public HIRExpr value;

        public HIRReturn(HIRExpr value) {
            this.value = value;
        }
    }
    
    public static class HIRBreak extends HIRStmt{}
    
    public static class HIRContinue extends HIRStmt{}
    
    public static class HIRExprStmt extends HIRStmt{
        public HIRExpr expr;

        public HIRExprStmt(HIRExpr expr) {
            this.expr = expr;
        }
    }
    
    public static class HIRVar extends HIRExpr{
        public String name;
        
        public HIRVar(String name, Type typeinfered) {
            super.type = typeinfered;
            this.name = name;
        }
    }
    
    public static class HIRConst extends HIRExpr{
        public Object value;

        public HIRConst(Object value, Type typeinfered) {
            super.type = typeinfered;
            this.value = value;
            
            
        }
        
    }
    
    public static class HIRBinary extends HIRExpr{
        public String op;

        public HIRExpr left;

        public HIRExpr right;

        public HIRBinary(String op, HIRExpr left, HIRExpr right, Type typeinfered) {
            this.op = op;
            this.left = left;
            this.right = right;
            super.type = typeinfered;
        }
        
    }
    
    public static class HIRUnary extends HIRExpr{
        public BinaryOp op;

        public HIRExpr operand;

        public Type type;

        public HIRUnary(BinaryOp op, HIRExpr operand, Type type) {
            this.op = op;
            this.operand = operand;
            this.type = type;
        }
        
    }
    
    public static class HIRCall extends HIRExpr{
        public String function;

        public List<HIRExpr> arguments;

        public Type type;

        public HIRCall(String function, List<HIRExpr> arguments, Type type) {
            this.function = function;
            this.arguments = arguments;
            this.type = type;
        }
        
    }
    
    public static class HIRMethodCall extends HIRExpr{
        public HIRExpr object;

        public String method;

        public List<HIRExpr> arguments;

        public Type type;

        public HIRMethodCall(HIRExpr object, String method, List<HIRExpr> arguments, Type type) {
            this.object = object;
            this.method = method;
            this.arguments = arguments;
            this.type = type;
        }
        
    }
    
    public static class HIRNewObject extends HIRExpr{
        public String className;

        public List<HIRExpr> arguments;

        public Type type;

        public HIRNewObject(String className, List<HIRExpr> arguments, Type type) {
            this.className = className;
            this.arguments = arguments;
            this.type = type;
        }
        
        
    }
    
    public static class HIRFieldAccess extends HIRExpr{
        public HIRExpr object;

        public String field;

        public Type type;

        public HIRFieldAccess(HIRExpr object, String field, Type type) {
            this.object = object;
            this.field = field;
            this.type = type;
        }
        
    }
    
    public static class HIRArrayAccess extends HIRExpr{
        public HIRExpr array;

        public List<HIRExpr> indices;

        public Type type;

        public HIRArrayAccess(HIRExpr array, List<HIRExpr> indices, Type type) {
            this.array = array;
            this.indices = indices;
            this.type = type;
        }
        
        
    }
    
    public static class HIRTernary extends HIRExpr{
        public HIRExpr condition;

        public HIRExpr trueExpr;

        public HIRExpr falseExpr;

        public Type type;

        public HIRTernary(HIRExpr condition, HIRExpr trueExpr, HIRExpr falseExpr, Type type) {
            this.condition = condition;
            this.trueExpr = trueExpr;
            this.falseExpr = falseExpr;
            this.type = type;
        }
        
    }
    
    public static class HIRFunction extends HIRNode{
        public String name;

        public Type returnType;

        public List<HIRParameter> parameters;
        public List<HIRVarDecl> locals;

        public HIRBlock body;

        public HIRFunction(String name, Type returnType, List<HIRParameter> parameters, HIRBlock body) {
            this.name = name;
            this.returnType = returnType;
            this.parameters = parameters;
            this.body = body;
        }
        
    }
    
    public static class HIRParameter{
        public String name;

        public String type;

        public HIRParameter(String name, String type) {
            this.name = name;
            this.type = type;
        }
        
    }
    
    public static class HIRClass extends HIRNode{
        public String name;

        public List<HIRFunction> methods;

        public List<HIRAssign> fields;

        public HIRClass(String name, List<HIRFunction> methods, List<HIRAssign> fields) {
            this.name = name;
            this.methods = methods;
            this.fields = fields;
        }
        
    }
    
    public static class HIRProgram extends HIRNode{
        public List<HIRClass> classes;

        public List<HIRFunction> functions;
        
        public List<HIRVarDecl> globals;
        
        public HIRBlock body;      // <-- NOUVEAU

        public HIRProgram(List<HIRClass> classes, List<HIRFunction> functions, List<HIRVarDecl> globals, HIRBlock body) {
            this.classes = classes;
            this.functions = functions;
            this.globals = globals;
            this.body = body;
        }
        
    }
    
    public static class HIRVarDecl extends HIRStmt{
        public String name;
        public Type type;
        public HIRExpr initializer;
        public boolean immutable;

        public HIRVarDecl(String name, Type type, HIRExpr initializer, boolean immutable) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
            this.immutable = immutable;
        }
        
        
    }
    
    public static class HIRNewArray extends HIRExpr{

        public Type elementType;

        public List<HIRExpr> sizes;

        public Type type;

        public HIRNewArray(Type elementType,
                           List<HIRExpr> sizes,
                           Type type){

            this.elementType = elementType;
            this.sizes = sizes;
            this.type = type;
        } 

    }
}
