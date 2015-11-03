package ru.cardamo.apt.ferrante.generator;

import org.jannocessor.collection.api.PowerList;
import org.jannocessor.model.ParentedElement;
import org.jannocessor.model.executable.JavaConstructor;
import org.jannocessor.model.executable.JavaMethod;
import org.jannocessor.model.modifier.FieldModifiers;
import org.jannocessor.model.modifier.MethodModifiers;
import org.jannocessor.model.modifier.value.ClassModifierValue;
import org.jannocessor.model.modifier.value.FieldModifierValue;
import org.jannocessor.model.modifier.value.MethodModifierValue;
import org.jannocessor.model.modifier.value.NestedClassModifierValue;
import org.jannocessor.model.structure.AbstractJavaStructure;
import org.jannocessor.model.structure.JavaClass;
import org.jannocessor.model.structure.JavaNestedClass;
import org.jannocessor.model.type.JavaType;
import org.jannocessor.model.type.JavaTypeKind;
import org.jannocessor.model.util.New;
import org.jannocessor.model.variable.JavaField;
import org.jannocessor.processor.api.CodeProcessor;
import org.jannocessor.processor.api.ProcessingContext;

import java.util.*;

public class MethodFunctionGenerator implements CodeProcessor<JavaMethod> {

    static final MethodModifiers STATIC_METHOD = New.methodModifiers(MethodModifierValue.STATIC);

    static final FieldModifiers PUBLIC_STATIC_FINAL = New.fieldModifiers(FieldModifierValue.PUBLIC, FieldModifierValue.STATIC, FieldModifierValue.FINAL);

    public void process(PowerList<JavaMethod> methods, ProcessingContext context) {
        Map<AbstractJavaStructure, List<JavaMethod>> byClass = new HashMap<>();

        for (JavaMethod method : methods) {

            AbstractJavaStructure parent = method.getParent();

            List<JavaMethod> classMethods = byClass.get(parent);
            if (classMethods == null) {
                classMethods = new ArrayList<>();
                byClass.put(parent, classMethods);
            }

            classMethods.add(method);
        }

        for (Map.Entry<AbstractJavaStructure, List<JavaMethod>> entry : byClass.entrySet()) {
            AbstractJavaStructure key = entry.getKey();
            JavaClass klz = New.classs(
                New.classModifiers(ClassModifierValue.PUBLIC),
                key.getName().copy().appendPart("_f").getText()
            );

            ((ParentedElement) klz).setParent(key.getParent());

            for (JavaMethod method : entry.getValue()) {

                JavaType argType;
                boolean isStatic = false;
                if (method.getModifiers().contains(STATIC_METHOD)) {
                    isStatic = true;
                    if (method.getParameters().size() == 1) {
                        argType = unbox(method.getParameters().get(0).getType());
                    } else {
                        // TODO: log? fail? insert comment?
                        continue;
                    }
                } else if (method.getParameters().isEmpty()) {
                    argType = key.getType();
                } else {
                    // TODO: log? fail? insert comment?
                    continue;
                }

                JavaType returnType = unbox(method.getReturnType());

                JavaMethod applyMethod = New.method(
                    New.methodModifiers(MethodModifierValue.PUBLIC),
                    returnType,
                    "apply",
                    New.parameter(argType, "arg")
                );

                String methodBody = isStatic
                    ? key.getName().getText() + "." + method.getName().getText() + "(arg);"
                    : "arg." + method.getName().getText() + "();";

                methodBody = method.getReturnType().getKind() == JavaTypeKind.VOID
                    ? methodBody + " return null;"
                    : "return " + methodBody;

                applyMethod.getBody().setHardcoded(methodBody);

                JavaType funInterface = New.type("com.google.common.base.Function<" + argType.getCanonicalName() + "," + returnType.getCanonicalName() + ">");
                String funClassName = method.getName().copy().appendPart("_f").getCapitalized();
                JavaNestedClass funClass = New.nestedClass(
                    New.nestedClassModifiers(NestedClassModifierValue.STATIC),
                    funClassName,
                    null,
                    Collections.singletonList(funInterface),
                    New.NO_FIELDS,
                    New.NO_CONSTRUCTORS,
                    Collections.singletonList(applyMethod)
                );

                JavaField function = New.field(
                    PUBLIC_STATIC_FINAL,
                    funInterface,
                    method.getName().getText(),
                    New.expression("new " + funClassName + "()")
                );

                klz.getNestedClasses().add(funClass);
                klz.getFields().add(function);
            }

            context.generateCode(klz, true);
        }
    }

    private JavaType unbox(JavaType type) {
        switch (type.getKind()) {
            case BOOLEAN:
            case BYTE:
            case SHORT:
            case LONG:
            case CHAR:
            case FLOAT:
            case DOUBLE:
            case VOID:
                return New.type(type.getSimpleName().getCapitalized());
            case INT:
                return New.type("Integer");
            default:
                return type;
        }
    }
}