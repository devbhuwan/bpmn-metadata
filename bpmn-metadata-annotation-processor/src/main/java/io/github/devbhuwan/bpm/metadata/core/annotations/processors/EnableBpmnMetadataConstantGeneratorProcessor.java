package io.github.devbhuwan.bpm.metadata.core.annotations.processors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * <p> </p>
 *
 * @author Bhuwan Prasad Upadhyay
 */
@SupportedAnnotationTypes("io.github.devbhuwan.bpm.metadata.core.annotations.EnableBpmnMetadataConstantGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EnableBpmnMetadataConstantGeneratorProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
