package io.github.devbhuwan.bpm.metadata.core.annotations.processors.util;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Element;

/**
 * @author Bhuwan Prasad Upadhyay
 */
public class JavaSourceFileHelper {

    public static final String BPMN_METADATA_ANNOTATION_MODE = "BPMN_METADATA_ANNOTATION_MODE";
    public static final String TEST_MODE = "TEST";

    public static String buildPackageName(Element rootElement) {
        final String elementQualifiedName = rootElement.asType().toString();
        Assert.notNull(elementQualifiedName, "elementQualifiedName must be not null");
        int lastIndexOf = elementQualifiedName.lastIndexOf(".");
        String packageName = "bpmn.metadata";
        if (lastIndexOf > 0)
            packageName = elementQualifiedName.substring(0, lastIndexOf) + "." + packageName;
        Assert.notNull(packageName, "packageName must be not null");
        return packageName;
    }

    public static String buildClassName(Resource resource) {
        String filename = resource.getFilename();
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf > 0)
            filename = filename.substring(0, lastIndexOf);
        return convertToJavaClassNameConvention(filename) + "Constants";
    }

    private static String convertToJavaClassNameConvention(String filename) {
        String underScoreFilename = filename.replaceAll("\\s+", "_");
        StringBuilder javaFileName = new StringBuilder();
        for (String word : underScoreFilename.split("_")) {
            javaFileName.append(StringUtils.capitalize(word));
        }
        return javaFileName.toString();
    }

    private static boolean isTestMode() {
        return TEST_MODE.equals(System.getProperty(BPMN_METADATA_ANNOTATION_MODE));
    }

    public static String getDefaultLocation() {
        return isTestMode() ? "classpath*:**/*.bpmn" : "file:./src/main/**/*.bpmn";
    }
}
