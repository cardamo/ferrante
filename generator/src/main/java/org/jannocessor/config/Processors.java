package org.jannocessor.config;

import org.jannocessor.model.executable.JavaMethod;
import org.jannocessor.processor.annotation.Annotated;
import org.jannocessor.processor.annotation.Types;
import ru.cardamo.apt.ferrante.api.GuavaFunction;
import ru.cardamo.apt.ferrante.generator.MethodFunctionGenerator;

public class Processors {
    @Annotated(GuavaFunction.class)
    @Types(JavaMethod.class)
    public MethodFunctionGenerator guavaFunctionGenerator() {
        return new MethodFunctionGenerator();
    }
}
