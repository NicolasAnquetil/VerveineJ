// Automagically generated code, please do not change
package org.moosetechnology.model.famixjava.famixtraits;

import ch.akuhn.fame.MetaRepository;

public class FamixTraitsModel {

    public static MetaRepository metamodel() {
        MetaRepository metamodel = new MetaRepository();
        importInto(metamodel);
        return metamodel;
    }
    
    public static void importInto(MetaRepository metamodel) {
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.THasSignature.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TInvocationsReceiver.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithParameterizedTypeUsers.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithFileIncludes.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TMethodMetrics.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TThrownException.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithFiles.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TClassWithVisibility.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithExceptions.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TReference.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithImplicitVariables.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.AnnotationInstanceGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAccessible.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TReferenceable.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TCompilationUnit.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TPreprocessorIfdef.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithAnnotationInstanceAttributes.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithTypeAliases.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TDereferencedInvocation.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.GlobalVariableGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TFunction.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithMethods.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithDeclaredExceptions.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TFileInclude.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TSourceLanguage.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TImplicitVariable.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.THasKind.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithParameters.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TPackageable.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TPrimitiveType.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithDereferencedInvocations.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithAttributes.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTypedAnnotationInstanceAttribute.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTrait.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithTypes.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TIndexedFileNavigation.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TMultipleFileAnchor.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TInheritance.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TSourceEntity.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.AnnotationTypeGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithTraits.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.PackageGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TEnumValue.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TParameterizedType.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithHeaders.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithLocalVariables.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithAnnotationTypes.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAccess.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTemplate.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TFileSystemEntity.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTypeAlias.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TSourceAnchor.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TUnknownSourceLanguage.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAnnotationTypeAttribute.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAnnotationInstance.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TMethod.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.MethodGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TStructuralEntity.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TInvocable.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TClassMetrics.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithFunctions.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAssociation.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TFolder.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTemplateUser.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TDefinedInModule.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TInvocation.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TType.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithComments.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TGlobalVariable.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TFile.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithCompilationUnits.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TCanBeAbstract.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TException.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAttribute.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithParameterizedTypes.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithReferences.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.THeader.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TModule.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithClasses.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithAnnotationInstances.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TypeGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.THasImmediateSource.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TEnum.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TCohesionCouplingMetrics.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TPreprocessorDefine.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TCaughtException.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAnnotationType.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTypedAnnotationInstance.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TLCOMMetrics.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTraitUsage.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithThrownExceptions.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithCaughtExceptions.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.FileGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TDeclaredException.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TParameterizedTypeUser.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTypedEntity.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithStatements.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithInvocations.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.InvocationGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TRelativeSourceAnchor.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithEnumValues.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.NamespaceGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.THasModifiers.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TFileAnchor.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TParameter.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.ClassGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TTraitUser.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithGlobalVariables.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithPackages.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TCanBeFinal.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TUnknownVariable.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.FolderGroup.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithInheritances.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TClassHierarchyNavigation.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TPackage.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TAnnotationInstanceAttribute.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.THasVisibility.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TLocalVariable.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithAccesses.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TClass.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TFileNavigation.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithTemplates.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.FamixModel.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TNamedEntity.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TComment.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TWithSourceLanguages.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TParameterType.class);
		metamodel.with(org.moosetechnology.model.famixjava.famixtraits.TNamespace.class);

    }

}

