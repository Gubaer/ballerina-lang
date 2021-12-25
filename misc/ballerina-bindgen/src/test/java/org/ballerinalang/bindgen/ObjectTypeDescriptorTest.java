package org.ballerinalang.bindgen;

import io.ballerina.compiler.syntax.tree.FunctionSignatureNode;
import io.ballerina.compiler.syntax.tree.MethodDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.ObjectTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDefinitionNode;
import io.ballerina.tools.text.TextDocuments;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ObjectTypeDescriptorTest {

    @Test(description = "tests the structure of an object type descriptor in a module")
    public void testObjectTypeDescriptor() {
        final String source =
              "public type Foo object {"
            + "    public function foo();"
            + "    public function bar(int value) returns string;"
            + "};";
        SyntaxTree tree = SyntaxTree.from(TextDocuments.from(source));
        assertEquals("MODULE_PART", tree.rootNode().kind().toString());

        ModulePartNode module = (ModulePartNode) tree.rootNode();
        assertEquals(1, module.members().size(), "expected only one member in the module");

        TypeDefinitionNode typeDefinition = (TypeDefinitionNode) module.members().get(0);
        assertEquals("Foo", typeDefinition.typeName().text());
        assertEquals("type", typeDefinition.typeKeyword().text());
        assertEquals("public", typeDefinition.visibilityQualifier().get().text());

        ObjectTypeDescriptorNode objectType = (ObjectTypeDescriptorNode)typeDefinition.typeDescriptor();
        assertEquals("object", objectType.objectKeyword().text());
        assertEquals(2, objectType.members().size(), "expected two members in the object type");

        MethodDeclarationNode method = (MethodDeclarationNode) objectType.members().get(0);
        assertEquals("foo", method.methodName().text());
        assertEquals(1, method.qualifierList().size());
        assertEquals("public", method.qualifierList().get(0).text());

        FunctionSignatureNode signature = method.methodSignature();
        assertEquals(0, signature.parameters().size());
        assertTrue(signature.returnTypeDesc().isEmpty());
    }
}
