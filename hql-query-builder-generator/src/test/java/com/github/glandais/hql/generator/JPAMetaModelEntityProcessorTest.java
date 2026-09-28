package com.github.glandais.hql.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class JPAMetaModelEntityProcessorTest {

    @TempDir
    Path outputDir;

    private record CompilationResult(boolean success, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
    }

    private CompilationResult compileSamples(String... sourceNames) throws IOException, URISyntaxException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                diagnosticCollector, Locale.getDefault(), StandardCharsets.UTF_8)) {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(outputDir.toFile()));
            fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, List.of(outputDir.toFile()));

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(sampleSourceFiles(sourceNames));

            List<String> options = List.of("-classpath", System.getProperty("java.class.path"));

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null, fileManager, diagnosticCollector, options, null, compilationUnits);
            task.setProcessors(List.of(new JPAMetaModelEntityProcessor()));

            boolean success = task.call();
            return new CompilationResult(success, diagnosticCollector.getDiagnostics());
        }
    }

    private List<File> sampleSourceFiles(String... names) {
        return Arrays.stream(names).map(this::resolveSampleSource).toList();
    }

    private File resolveSampleSource(String name) {
        String resourcePath = "com/github/glandais/hql/generator/sample/" + name;
        URL resource = getClass().getClassLoader().getResource("sample-sources/" + resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing test resource: sample-sources/" + resourcePath);
        }
        try {
            return Paths.get(resource.toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void compilesEntitySourcesWithoutErrors() throws IOException, URISyntaxException {
        CompilationResult result = compileSamples("SampleBase.java", "SampleChild.java", "SampleAddress.java", "SampleRoot.java");

        String errors = result.diagnostics().stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
                .map(Object::toString)
                .reduce("", (a, b) -> a + "\n" + b);

        assertThat(result.success()).withFailMessage("Compilation failed: %s", errors).isTrue();
    }

    @Test
    void generatesEntityBuilderForSampleRoot() throws IOException, URISyntaxException {
        compileSamples("SampleBase.java", "SampleChild.java", "SampleAddress.java", "SampleRoot.java");

        Path generated = outputDir.resolve("com/github/glandais/hql/generator/sample/SampleRoot_.java");
        assertThat(generated).exists();

        String content = Files.readString(generated);
        assertThat(content).contains("class SampleRoot_");
        assertThat(content).contains("HQLSafePathBasicString");
        assertThat(content).contains("name()");
        assertThat(content).contains("SampleChild_");
        assertThat(content).contains("child()");
        assertThat(content).contains("children()");
        assertThat(content).contains("SampleAddress_");
        assertThat(content).contains("address()");
    }

    @Test
    void generatesEntityBuilderForSampleChildExtendingMappedSuperclass() throws IOException, URISyntaxException {
        compileSamples("SampleBase.java", "SampleChild.java", "SampleAddress.java", "SampleRoot.java");

        Path generated = outputDir.resolve("com/github/glandais/hql/generator/sample/SampleChild_.java");
        assertThat(generated).exists();

        String content = Files.readString(generated);
        assertThat(content).contains("class SampleChild_");
        assertThat(content).contains("extends com.github.glandais.hql.generator.sample.SampleBase_");
        assertThat(content).contains("label()");

        Path mappedSuperclassGenerated = outputDir.resolve("com/github/glandais/hql/generator/sample/SampleBase_.java");
        assertThat(mappedSuperclassGenerated).exists();
        assertThat(Files.readString(mappedSuperclassGenerated)).contains("id()");
    }

    @Test
    void generatesEntityBuilderForEmbeddableAddress() throws IOException, URISyntaxException {
        compileSamples("SampleBase.java", "SampleChild.java", "SampleAddress.java", "SampleRoot.java");

        Path generated = outputDir.resolve("com/github/glandais/hql/generator/sample/SampleAddress_.java");
        assertThat(generated).exists();
        assertThat(Files.readString(generated)).contains("city()");
    }

    @Test
    void generatesCompiledClassFilesAlongsideSources() throws IOException, URISyntaxException {
        compileSamples("SampleBase.java", "SampleChild.java", "SampleAddress.java", "SampleRoot.java");

        Path compiledClass = outputDir.resolve("com/github/glandais/hql/generator/sample/SampleRoot_.class");
        assertThat(compiledClass).exists();
    }

    @Test
    void generatesProviderForHqlQueriesInterface() throws IOException, URISyntaxException {
        // This compilation is expected to fail overall: the generated Provider's template
        // hardcodes legacy javax.enterprise/javax.inject imports that aren't on this
        // project's classpath. Source generation still happens before that compile error,
        // so the generated Provider source is verified directly rather than via a successful
        // compile.
        compileSamples("SampleBase.java", "SampleChild.java", "SampleAddress.java", "SampleRoot.java", "SampleQueries.java");

        Path generated = outputDir.resolve("com/github/glandais/hql/generator/sample/SampleQueriesProvider.java");
        assertThat(generated).exists();

        String content = Files.readString(generated);
        assertThat(content).contains("public class SampleQueriesProvider implements SampleQueries");
        assertThat(content).contains("sampleroot()");
        assertThat(content).contains("samplechild()");
    }
}
