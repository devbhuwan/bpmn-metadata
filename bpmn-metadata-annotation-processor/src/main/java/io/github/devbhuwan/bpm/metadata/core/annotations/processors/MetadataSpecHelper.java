package io.github.devbhuwan.bpm.metadata.core.annotations.processors;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.camunda.bpm.model.bpmn.instance.BaseElement;

import javax.lang.model.element.Modifier;

/**
 * <p> </p>
 *
 * @author Bhuwan Prasad Upadhyay
 */
public class MetadataSpecHelper {

    private static final String SUFFIX_ID = "_ID";

    public static TypeSpec.Builder innerClassSpec(String className) {
        return TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    }

    public static FieldSpec idSpec(BaseElement element) {
        return FieldSpec
                .builder(TypeName.get(String.class), element.getId() + SUFFIX_ID)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", element.getId())
                .build();
    }
}
