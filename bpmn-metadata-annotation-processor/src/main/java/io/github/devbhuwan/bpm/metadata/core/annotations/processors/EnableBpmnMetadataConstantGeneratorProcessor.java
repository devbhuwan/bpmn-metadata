package io.github.devbhuwan.bpm.metadata.core.annotations.processors;

import com.google.auto.service.AutoService;
import com.google.common.base.Throwables;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.github.devbhuwan.bpm.metadata.core.annotations.processors.util.JavaSourceFileHelper;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.squareup.javapoet.JavaFile.builder;
import static io.github.devbhuwan.bpm.metadata.core.annotations.processors.util.JavaSourceFileHelper.buildClassName;
import static io.github.devbhuwan.bpm.metadata.core.annotations.processors.util.JavaSourceFileHelper.buildPackageName;
import static io.github.devbhuwan.bpm.metadata.core.annotations.processors.util.MetadataSpecHelper.*;

/**
 * @author Bhuwan Prasad Upadhyay
 */
@SupportedAnnotationTypes("io.github.devbhuwan.bpm.metadata.core.annotations.EnableBpmnMetadataConstantGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
@SupportedOptions({"debug", "verify"})
public class EnableBpmnMetadataConstantGeneratorProcessor extends AbstractProcessor {


    private static final String IDS = "IDS";
    private static final String VARIABLE_KEYS = "VARIABLE_KEYS";
    private final PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private final Map<String, Set<String>> idVariableKeysMap = new HashMap<>();

    public EnableBpmnMetadataConstantGeneratorProcessor() {
        idVariableKeysMap.put(IDS, new TreeSet<>());
        idVariableKeysMap.put(VARIABLE_KEYS, new TreeSet<>());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.processImpl(annotations, roundEnv);
        return true;
    }

    private void processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Iterator<? extends Element> iterator = roundEnv.getRootElements().iterator();
            if (iterator.hasNext()) {
                Element rootElement = iterator.next();
                final String packageName = buildPackageName(rootElement);
                for (Resource resource : resourcePatternResolver.getResources(JavaSourceFileHelper.getDefaultLocation()))
                    this.generateMetadataConstantSourceFile(packageName, resource);
            }
        } catch (Exception ex) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR,
                            ex.getMessage() + "\n\n" + Throwables.getStackTraceAsString(ex));
        }
    }


    private void generateMetadataConstantSourceFile(String packageName, Resource resource) {
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(resource.getFile());
            TypeSpec.Builder classSpec = TypeSpec.classBuilder(buildClassName(resource))
                    .addModifiers(Modifier.PUBLIC);
            TypeSpec.Builder idsClassSpec = innerClassSpec("Ids");
            TypeSpec.Builder variableKeysClassSpec = innerClassSpec("VariableKeys");

            collectsByProcess(bpmnModelInstance.getModelElementsByType(Process.class));
            collectsByNode(bpmnModelInstance.getModelElementsByType(Event.class));
            collectsByNode(bpmnModelInstance.getModelElementsByType(Task.class));
            collectsByFLow(bpmnModelInstance.getModelElementsByType(SequenceFlow.class));

            idVariableKeysMap.get(IDS)
                    .forEach(key -> idsClassSpec.addField(fieldSpec(key)));
            idVariableKeysMap.get(VARIABLE_KEYS)
                    .forEach(key -> variableKeysClassSpec.addField(fieldSpec(key)));
            classSpec.addType(idsClassSpec.build());
            classSpec.addType(variableKeysClassSpec.build());
            JavaFile javaFile = builder(packageName, classSpec.build()).build();
            javaFile.writeTo(new File(JavaSourceFileHelper.getDefaultPath()));
        } catch (IOException ex) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR,
                            ex.getMessage() + "\n\n" + Throwables.getStackTraceAsString(ex));
        }
    }


    private void collectsByProcess(Collection<Process> elements) {
        elements.forEach(node -> {
            idVariableKeysMap.get(IDS).add(node.getId());
        });
    }


    private void collectsByNode(Collection<? extends FlowNode> elements) {
        elements.forEach(node -> {
            idVariableKeysMap.get(IDS).add(node.getId());
            idVariableKeysMap.get(VARIABLE_KEYS).addAll(variableKeys(node));
        });
    }

    private void collectsByFLow(Collection<SequenceFlow> elements) {
        elements.forEach(node -> {
            idVariableKeysMap.get(IDS).add(node.getId());
            idVariableKeysMap.get(VARIABLE_KEYS).addAll(variableKeys(node.getSource()));
            idVariableKeysMap.get(VARIABLE_KEYS).addAll(variableKeys(node.getTarget()));
        });
    }


}
