package io.github.higherkt.processors

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.higherkt.annotations.Higher
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.type.DeclaredType

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_15)
@Suppress("EXPERIMENTAL_API_USAGE")
class HigherProcessor : AbstractGenerativeProcessor() {
    override fun generate(element: Element): FileSpec? {
        val packageName = processingEnv.elementUtils.getPackageOf(element).toString()
        val type = element.asType() as? DeclaredType ?: return null

        val typeArguments = type.typeArguments.map { it.asTypeName() }
        val typeVariables = typeArguments.map { TypeVariableName(it.toString()) }

        val simpleName = element.simpleName.toString()

        val brandName = "${simpleName}Kind"
        val wrapperName = "${simpleName}KindWrapper"

        val kind = kindOf(typeArguments, packageName, brandName)
        val brand = brandOf(brandName)

        val typeName = type.asTypeName()
        val wrapper = wrapperOf(wrapperName, typeVariables, typeName, kind)

        val companion = ClassName(packageName, simpleName, "Companion")

        val inj = buildInjection(companion, typeVariables, kind, typeName, wrapper)
        val prj = buildProjection(companion, typeVariables, kind, typeName, wrapper)

        val alternativeTypeVariables = typeVariables.map { TypeVariableName(it.name + "2") }

        val lambdaTypeName = LambdaTypeName.get(
            parameters = arrayOf(typeName),
            returnType = ClassName(packageName, simpleName).parameterizedBy(alternativeTypeVariables)
        )

        val liftedLambdaTypeName = LambdaTypeName.get(
            parameters = arrayOf(kind),
            returnType = kindOf(alternativeTypeVariables, packageName, brandName)
        )

        val lift = buildLift(companion, typeVariables, alternativeTypeVariables, lambdaTypeName, liftedLambdaTypeName)
        val unlift = buildUnlift(companion, typeVariables, alternativeTypeVariables, liftedLambdaTypeName, lambdaTypeName)

        return FileSpec.builder(packageName, "${simpleName}Higher")
            .addType(brand)
            .addType(wrapper)
            .addFunction(inj)
            .addFunction(lift)
            .addFunction(prj)
            .addFunction(unlift)
            .build()
    }

    private fun buildUnlift(
        companion: ClassName,
        typeVariables: List<TypeVariableName>,
        alternativeTypeVariables: List<TypeVariableName>,
        liftedLambdaTypeName: LambdaTypeName,
        lambdaTypeName: LambdaTypeName
    ) = FunSpec.builder("unlift")
        .receiver(companion)
        .addTypeVariables(typeVariables)
        .addTypeVariables(alternativeTypeVariables)
        .addParameter("f", liftedLambdaTypeName)
        .returns(lambdaTypeName)
        .addStatement("return { prj(f(inj(it))) }")
        .build()

    private fun buildLift(
        companion: ClassName,
        typeVariables: List<TypeVariableName>,
        alternativeTypeVariables: List<TypeVariableName>,
        lambdaTypeName: LambdaTypeName,
        liftedLambdaTypeName: LambdaTypeName
    ) = FunSpec.builder("lift")
        .receiver(companion)
        .addTypeVariables(typeVariables)
        .addTypeVariables(alternativeTypeVariables)
        .addParameter("f", lambdaTypeName)
        .returns(liftedLambdaTypeName)
        .addStatement("return { inj(f(prj(it))) }")
        .build()

    private fun buildProjection(
        companion: ClassName,
        typeVariables: List<TypeVariableName>,
        kind: ParameterizedTypeName,
        typeName: TypeName,
        wrapper: TypeSpec
    ) = FunSpec.builder("prj")
        .receiver(companion)
        .addModifiers(KModifier.PUBLIC)
        .addTypeVariables(typeVariables)
        .returns(typeName)
        .addParameter("wrapper", kind)
        .addStatement("return (wrapper as %N).it", wrapper)
        .build()

    private fun buildInjection(
        companion: ClassName,
        typeVariables: List<TypeVariableName>,
        kind: ParameterizedTypeName,
        typeName: TypeName,
        wrapper: TypeSpec
    ) = FunSpec.builder("inj")
        .receiver(companion)
        .addModifiers(KModifier.PUBLIC)
        .addTypeVariables(typeVariables)
        .returns(kind)
        .addParameter("it", typeName)
        .addStatement("return %N(it)", wrapper)
        .build()

    private fun wrapperOf(
        wrapperName: String,
        typeVariables: List<TypeVariableName>,
        typeName: TypeName,
        kind: ParameterizedTypeName
    ) = TypeSpec.classBuilder(wrapperName)
        .addModifiers(KModifier.PRIVATE, KModifier.VALUE)
        .addAnnotation(JvmInline::class)
        .addTypeVariables(typeVariables)
        .addProperty(PropertySpec.builder("it", typeName).initializer("it").build())
        .primaryConstructor(FunSpec.constructorBuilder().addParameter("it", typeName).build())
        .addSuperinterface(kind)
        .addFunction(
            FunSpec.builder("toString")
            .addModifiers(KModifier.OVERRIDE)
            .returns(String::class)
            .addStatement("return it.toString()")
            .build())
        .build()

    private fun brandOf(brandName: String) = TypeSpec.objectBuilder(brandName)
        .addModifiers(KModifier.PUBLIC)
        .build()

    private fun kindOf(
        typeArguments: List<TypeName>,
        packageName: String,
        brandName: String
    ) = ClassName(HIGHER_ROOT_PACKAGE, "Ap${typeArguments.size}")
        .parameterizedBy(ClassName(packageName, brandName), *typeArguments.toTypedArray())

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Higher::class.java.name)

    companion object {
        const val HIGHER_ROOT_PACKAGE = "io.github.higherkt"
    }
}
