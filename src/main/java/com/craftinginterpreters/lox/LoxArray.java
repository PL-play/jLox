package com.craftinginterpreters.lox;

import java.util.List;

public class LoxArray extends LoxInstance {
    private final Object[] elements;

    LoxArray(int size) {
        super(null);
        this.elements = new Object[size];
    }

    void setElements(List<Object> elements) {
        for (int i = 0; i < elements.size(); i++) {
            this.elements[i] = elements.get(i);
        }
    }

    Object getElementByIndex(int index) {
        return elements[index];
    }

    void setElementByIndex(int index, Object value) {
        elements[index] = value;
    }

    Object get(Token name) {
        // TODO resizeable array
        // TODO index out of bounds check.
        if (name.lexeme.equals("get")) {
            return new LoxCallable() {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Object index = arguments.get(0);
                    if (!(index instanceof Double)) {
                        throw new RuntimeError(name, "array index must be a int.");
                    }
                    return getElementByIndex((int) (double) index);
                }
            };
        } else if (name.lexeme.equals("set")) {
            return new LoxCallable() {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    Object index = arguments.get(0);
                    if (!(index instanceof Double)) {
                        throw new RuntimeError(name, "array index must be a int.");
                    }
                    Object value = arguments.get(1);
                    return elements[(int) (double) index] = value;
                }
            };
        } else if (name.lexeme.equals("length")) {
            return (double) elements.length;
        }
        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        for (int i = 0; i < elements.length; i++) {
            if (i != 0) buffer.append(", ");
            buffer.append(elements[i]);
        }
        buffer.append("]");
        return buffer.toString();
    }
}
