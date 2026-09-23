/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package IR_v3;

import java.util.ArrayList;
import java.util.List;
import com.mycompany.projet_compil_if_else_3.semantique.Type;

/**
 *
 * @author tafou
 */
public class LIR {
    public static abstract class LIRNode{}

    public static abstract class LIRInstruction extends LIRNode{}

    public static abstract class LIRValue extends LIRNode{}
    
    public static class LIRVariable extends LIRValue{

        public String name;

        public LIRVariable(String name){
            this.name=name;
        }

    }
    
    public static class LIRConstant extends LIRValue{

        public Object value;

        public LIRConstant(Object value){
            this.value=value;
        }

    }
    
    public static class LIRTemp extends LIRValue{

        public String name;

        public LIRTemp(String name){
            this.name=name;
        }

    }
    
    public static class LIRLabel extends LIRInstruction{

        public String name;

        public LIRLabel(String name){

            this.name=name;

        }

    }
    
    public static class LIRBlock{

        public List<LIRInstruction> instructions=new ArrayList<>();

        public void add(LIRInstruction i){

            instructions.add(i);

        }

    }
    
    public static class LIRAssign extends LIRInstruction{

        public LIRValue destination;

        public LIRValue source;

        public LIRAssign(LIRValue destination, LIRValue source){
            this.destination = destination;
            this.source = source;
        }

    }
    
    public static class LIRBinary extends LIRInstruction{

        public String op;

        public LIRTemp result;

        public LIRValue left;

        public LIRValue right;

        public LIRBinary(String op,
                         LIRTemp result,
                         LIRValue left,
                         LIRValue right){

            this.op = op;
            this.result = result;
            this.left = left;
            this.right = right;
        }

    }
    
    public static class LIRJump extends LIRInstruction{

        public String label;

        public LIRJump(String label){
            this.label = label;
        }

    }
    
    public static class LIRCondJump extends LIRInstruction{

        public LIRValue condition;

        public String label;
        
        public boolean jumpIfTrue;

        public LIRCondJump(LIRValue condition,
                           String label,
                           boolean jumpIfTrue){

            this.condition = condition;
            this.label = label;
            this.jumpIfTrue = jumpIfTrue;
        }

    }
    
    public static class LIRReturn extends LIRInstruction{

        public LIRValue value;

        public LIRReturn(LIRValue value){
            this.value = value;
        }

    }
    
    public static class LIRCall extends LIRInstruction{

        public LIRTemp result;

        public String function;

        public List<LIRValue> arguments;

        public LIRCall(LIRTemp result,
                       String function,
                       List<LIRValue> arguments){

            this.result = result;
            this.function = function;
            this.arguments = arguments;
        }

    }
    
    public static class LIRLoadField extends LIRInstruction{

        public LIRTemp result;

        public LIRValue object;

        public String field;

        public LIRLoadField(LIRTemp result,
                            LIRValue object,
                            String field){

            this.result = result;
            this.object = object;
            this.field = field;
        }

    }
    
    public static class LIRLoadArray extends LIRInstruction{

        public LIRTemp result;

        public LIRValue array;

        public List<LIRValue> indices;

        public LIRLoadArray(LIRTemp result,
                            LIRValue array,
                            List<LIRValue> indices){

            this.result = result;
            this.array = array;
            this.indices = indices;
        }

    }
    
    public static class LIRNewObject extends LIRInstruction{

        public LIRTemp result;

        public String className;

        public List<LIRValue> arguments;

        public LIRNewObject(LIRTemp result,
                            String className,
                            List<LIRValue> arguments){

            this.result = result;
            this.className = className;
            this.arguments = arguments;
        }

    }
    
    public static class LIRNewArray extends LIRInstruction{

        public LIRTemp result;

        public Type elementType;

        public List<LIRValue> sizes;

        public LIRNewArray(LIRTemp result,
                           Type elementType,
                           List<LIRValue> sizes){

            this.result = result;
            this.elementType = elementType;
            this.sizes = sizes;
        }

    }
    
    public static class LIRUnary extends LIRInstruction{

        public String op;

        public LIRTemp result;

        public LIRValue operand;

        public LIRUnary(String op,
                        LIRTemp result,
                        LIRValue operand){

            this.op = op;
            this.result = result;
            this.operand = operand;
        }
    }
    
    public static class LIRStoreField extends LIRInstruction{

        public LIRValue object;

        public String field;

        public LIRValue value;

        public LIRStoreField(LIRValue object,
                             String field,
                             LIRValue value){

            this.object = object;
            this.field = field;
            this.value = value;
        }
    }
    
    public static class LIRStoreArray extends LIRInstruction{

        public LIRValue array;

        public List<LIRValue> indices;

        public LIRValue value;

        public LIRStoreArray(LIRValue array,
                             List<LIRValue> indices,
                             LIRValue value){

            this.array = array;
            this.indices = indices;
            this.value = value;
        }
    }
    
    public static class LIRMethodCall extends LIRInstruction{

        public LIRTemp result;

        public LIRValue object;

        public String method;

        public List<LIRValue> arguments;

        public LIRMethodCall(
                LIRTemp result,
                LIRValue object,
                String method,
                List<LIRValue> arguments){

            this.result = result;
            this.object = object;
            this.method = method;
            this.arguments = arguments;
        }
    }
    
    
    
}
