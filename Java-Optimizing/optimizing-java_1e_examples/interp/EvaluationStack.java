package optjava.interp;

import java.util.Stack;

/**
 *
 * @author ben
 */
public class EvaluationStack extends Stack<EvalValue> {

    void iconst(int i) {
        push(new EvalValue(JVMType.I, i));
    }

    void iadd() {
        EvalValue ev1 = pop();
        EvalValue ev2 = pop();
        // For a runtime checking interpreter - type checks would go here...
        int add = (int) ev1.value + (int) ev2.value;
        push(new EvalValue(JVMType.I, add));
    }

    void idiv() {
        EvalValue ev1 = pop();
        EvalValue ev2 = pop();
        // For a runtime checking interpreter - type checks would go here...
        int div = (int) ev1.value / (int) ev2.value;
        push(new EvalValue(JVMType.I, div));
    }

    void imul() {
        EvalValue ev1 = pop();
        EvalValue ev2 = pop();
        // For a runtime checking interpreter - type checks would go here...
        int mul = (int) ev1.value * (int) ev2.value;
        push(new EvalValue(JVMType.I, mul));
    }

    void isub() {
        EvalValue ev1 = pop();
        EvalValue ev2 = pop();
        // For a runtime checking interpreter - type checks would go here...
        int sub = (int) ev1.value - (int) ev2.value;
        push(new EvalValue(JVMType.I, sub));
    }

}
