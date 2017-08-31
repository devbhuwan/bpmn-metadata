package io.github.devbhuwan.bpm.metadata.core.annotations.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static io.github.devbhuwan.bpm.metadata.core.annotations.processors.MetadataSpecHelper.idSpec;
import static io.github.devbhuwan.bpm.metadata.core.annotations.processors.MetadataSpecHelper.innerClassSpec;

/**
 * <p> </p>
 *
 * @author Bhuwan Prasad Upadhyay
 */
@SupportedAnnotationTypes("io.github.devbhuwan.bpm.metadata.core.annotations.EnableBpmnMetadataConstantGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
@SupportedOptions({"debug", "verify"})
public class EnableBpmnMetadataConstantGeneratorProcessor extends AbstractProcessor {


    private final PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.processImpl(annotations, roundEnv);
        return false;
    }

    private void processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Iterator<? extends Element> iterator = roundEnv.getRootElements().iterator();
            if (iterator.hasNext()) {
                Element rootElement = iterator.next();
                final String packageName = buildPackageName(rootElement);
                for (Resource resource : resourcePatternResolver.getResources("classpath*:**/*.bpmn"))
                    this.generateMetadataConstantSourceFile(packageName, resource);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String buildPackageName(Element rootElement) {
        final String elementQualifiedName = rootElement.asType().toString();
        Assert.notNull(elementQualifiedName, "elementQualifiedName must be not null");
        int lastIndexOf = elementQualifiedName.lastIndexOf(".");
        String packageName = "bpmn.metadata";
        if (lastIndexOf > 0)
            packageName = elementQualifiedName.substring(0, lastIndexOf) + "." + packageName;
        Assert.notNull(packageName, "packageName must be not null");
        return packageName;
    }

    private void generateMetadataConstantSourceFile(String packageName, Resource resource) {
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(resource.getFile());

            TypeSpec.Builder sourceSpecBuilder = TypeSpec.classBuilder(bpmnModelInstance.getDefinitions().getId())
                    .addModifiers(Modifier.PUBLIC);
            appendEventNotationSourceSpec(bpmnModelInstance, sourceSpecBuilder);
            appendTaskNotationSourceSpec(bpmnModelInstance, sourceSpecBuilder);
            JavaFile javaFile = JavaFile.builder(packageName, sourceSpecBuilder.build())
                    .build();
            javaFile.writeTo(new File("build"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void appendEventNotationSourceSpec(BpmnModelInstance bpmnModelInstance, TypeSpec.Builder sourceSpecBuilder) {
        TypeSpec.Builder eventSourceSpecBuilder = innerClassSpec("Event");

        Collection<Event> elementsByType = bpmnModelInstance.getModelElementsByType(Event.class);

        elementsByType.iterator().forEachRemaining(event -> {
            eventSourceSpecBuilder.addField(idSpec(event));
        });
        sourceSpecBuilder.addType(eventSourceSpecBuilder.build());
    }

    private void appendTaskNotationSourceSpec(BpmnModelInstance bpmnModelInstance, TypeSpec.Builder sourceSpecBuilder) {
        TypeSpec.Builder eventSourceSpecBuilder = innerClassSpec("Task");
        bpmnModelInstance.getModelElementsByType(Task.class).iterator().forEachRemaining(task -> {
            eventSourceSpecBuilder.addField(idSpec(task));
        });
        sourceSpecBuilder.addType(eventSourceSpecBuilder.build());
    }


}
