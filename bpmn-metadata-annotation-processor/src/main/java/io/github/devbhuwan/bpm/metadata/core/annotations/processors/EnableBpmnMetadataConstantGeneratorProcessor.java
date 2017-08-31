package io.github.devbhuwan.bpm.metadata.core.annotations.processors;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
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
@AutoService(Processor.class)
public class EnableBpmnMetadataConstantGeneratorProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
