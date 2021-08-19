package io.github.higherkt.processors

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.higherkt.annotations.Higher
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

@SupportedOptions(AbstractGenerativeProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
abstract class AbstractGenerativeProcessor : AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val annotated = roundEnv?.getElementsAnnotatedWith(Higher::class.java)
        val generatedSourcesRoot = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()

        if (generatedSourcesRoot.isEmpty()) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        if (annotated == null) {
            return true
        }

        for (element in annotated) {
            val fileSpec = generate(element) ?: continue

            val file = File(generatedSourcesRoot)
            file.mkdir()

            fileSpec.writeTo(file)
        }

        return true
    }

    abstract fun generate(element: Element): FileSpec?

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}