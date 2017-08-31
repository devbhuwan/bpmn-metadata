package io.github.devbhuwan.bpm.metadata.core.annotations.processors;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static io.github.devbhuwan.bpm.metadata.core.annotations.processors.util.JavaSourceFileHelper.BPMN_METADATA_ANNOTATION_MODE;
import static io.github.devbhuwan.bpm.metadata.core.annotations.processors.util.JavaSourceFileHelper.TEST_MODE;

/**
 * <p> </p>
 *
 * @author Bhuwan Prasad Upadhyay
 */
public class EnableBpmnMetadataConstantGeneratorProcessorTest {

    @BeforeClass
    public static void beforeClass() {
        System.getProperties().put(BPMN_METADATA_ANNOTATION_MODE, TEST_MODE);
    }

    @Test
    public void givenSourceFile_EnableBpmnMetadataConstantGenerator_thenCompileAndGenerateBpmnMetadataConstants() {
        Truth.assert_().about(javaSource())
                .that(JavaFileObjects.forResource("sources/GeneratorExample.java"))
                .processedWith(new EnableBpmnMetadataConstantGeneratorProcessor())
                .compilesWithoutError();
    }

}