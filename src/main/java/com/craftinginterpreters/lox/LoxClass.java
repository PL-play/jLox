package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
    final String name;
    final LoxClass superClass;

    private final Map<String, LoxFunction> methods;

    LoxClass(String name, LoxClass superClass, Map<String, LoxFunction> methods) {
        this.name = name;
        this.methods = methods;
        this.superClass = superClass;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            return initializer.arity();
        }
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    public LoxFunction findMethod(String name) {
        return methods.get(name);
    }
}
