package com.github.javaparser.ast.type;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.validator.Java5Validator;
import org.junit.Test;

import static com.github.javaparser.JavaParser.parseVariableDeclarationExpr;
import static com.github.javaparser.ParseStart.VARIABLE_DECLARATION_EXPR;
import static com.github.javaparser.Providers.provider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeTest {
    @Test
    public void asString() {
        assertEquals("int", typeAsString("int x;"));
        assertEquals("List<Long>", typeAsString("List<Long> x;"));
        assertEquals("String", typeAsString("@A String x;"));
        assertEquals("List<? extends Object>", typeAsString("List<? extends Object> x;"));
    }

    @Test(expected = ParseProblemException.class)
    public void primitiveTypeArgumentDefaultValidator() {
        typeAsString("List<long> x;");
    }

    @Test
    public void primitiveTypeArgumentLenientValidator() {
        ParserConfiguration config = new ParserConfiguration();
        config.setValidator(new Java5Validator() {{
            remove(noPrimitiveGenericArguments);
        }});

        ParseResult<VariableDeclarationExpr> result = new JavaParser(config).parse(
                VARIABLE_DECLARATION_EXPR, provider("List<long> x;"));
        assertTrue(result.isSuccessful());

        VariableDeclarationExpr decl = result.getResult().get();
        assertEquals("List<long>", decl.getVariable(0).getType().asString());
    }

    private String typeAsString(String s) {
        return parseVariableDeclarationExpr(s).getVariable(0).getType().asString();
    }
}
