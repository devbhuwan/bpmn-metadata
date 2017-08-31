package io.github.devbhuwan.bpm.metadata.core.annotations.processors;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * <p> </p>
 *
 * @author Bhuwan Prasad Upadhyay
 */
public class EnableBpmnMetadataConstantGeneratorProcessorTest {

    @Test
    public void givenSourceFile_EnableBpmnMetadataConstantGenerator_thenCompileAndGenerateBpmnMetadataConstants() {
        Truth.assert_().about(javaSource())
                .that(JavaFileObjects.forResource("sources/GeneratorExample.java"))
                .processedWith(new EnableBpmnMetadataConstantGeneratorProcessor())
                .compilesWithoutError();
    }

}