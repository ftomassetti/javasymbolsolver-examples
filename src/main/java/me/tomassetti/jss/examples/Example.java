package me.tomassetti.jss.examples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.ReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceType;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileNotFoundException;

public class Example {

    private static void printAncestors(ReferenceTypeDeclaration referenceTypeDeclaration) {
        System.out.println("Ancestors for " + referenceTypeDeclaration.getQualifiedName());
        for (ReferenceType ancestor : referenceTypeDeclaration.getAllAncestors()) {
            System.out.println(" * " + ancestor.describe());
        }
        System.out.println();
    }

    public static void main(String[] args) throws FileNotFoundException {
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        typeSolver.add(new JavaParserTypeSolver(new File("src/main/resources")));

        CompilationUnit aThirdClassCU = JavaParser.parse(new File("src/main/resources/my/packagez/AThirdClass.java"));

        ClassOrInterfaceDeclaration aThirdClass = aThirdClassCU.getClassByName("AThirdClass").get();
        ReferenceTypeDeclaration aThirdClassJss = JavaParserFacade.get(typeSolver).getTypeDeclaration(aThirdClass);
        ReferenceTypeDeclaration anotherClassJSS = typeSolver.solveType("my.packagez.AnotherClass").asReferenceType();
        ReferenceTypeDeclaration stringJSS = typeSolver.solveType("java.lang.String").asReferenceType();

        printAncestors(aThirdClassJss);
        printAncestors(anotherClassJSS);
        printAncestors(stringJSS);

        // Let's calculate the type of all expressions
        for (Expression expression : aThirdClassCU.getChildNodesByType(Expression.class)) {
            System.out.println("Expression: " + expression);
            System.out.println("  class: " + expression.getClass().getSimpleName());
            System.out.println("  type: " + JavaParserFacade.get(typeSolver).getType(expression).describe());
            System.out.println();
        }

        for (MethodCallExpr methodCall : aThirdClassCU.getChildNodesByType(MethodCallExpr.class)) {
            System.out.println("MethodCall: " + methodCall);
            System.out.println("  line: " + methodCall.getRange().get().begin.line);
            MethodDeclaration methodDeclaration = JavaParserFacade.get(typeSolver).solve(methodCall).getCorrespondingDeclaration();
            System.out.println("  method being called: " + methodDeclaration.getQualifiedSignature());
            System.out.println();
        }

    }
}
