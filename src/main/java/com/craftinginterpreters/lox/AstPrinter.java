package com.craftinginterpreters.lox;

public class AstPrinter implements Expr.Visitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return null;
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return null;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr == null) {
            return "nil";
        }
        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return null;
    }

    @Override
    public String visitSetExpr(Expr.Set expr) {
        return null;
    }

    @Override
    public String visitSuperExpr(Expr.Super expr) {
        return null;
    }

    @Override
    public String visitThisExpr(Expr.This expr) {
        return null;
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {

        return null;
    }

    @Override
    public String visitFunctionExpr(Expr.Function expr) {
        return null;
    }

    @Override
    public String visitTernaryExpr(Expr.Ternary expr) {
        return null;
    }

    @Override
    public String visitArrayExpr(Expr.Array expr) {
        return null;
    }

    @Override
    public String visitArrayGetExpr(Expr.ArrayGet expr) {
        return null;
    }

    @Override
    public String visitArraySetExpr(Expr.ArraySet expr) {
        return null;
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(name);
        for (var expr : exprs) {
            sb.append(" ");
            sb.append(expr.accept(this));
        }
        sb.append(")");
        return sb.toString();
    }

}
