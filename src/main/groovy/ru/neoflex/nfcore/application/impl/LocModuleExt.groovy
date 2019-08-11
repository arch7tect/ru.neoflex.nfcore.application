package ru.neoflex.nfcore.application.impl

import org.apache.commons.lang3.StringUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.impl.EAnnotationImpl
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.util.ExtendedMetaData
import ru.neoflex.nfcore.application.ApplicationFactory
import ru.neoflex.nfcore.application.ApplicationPackage
import ru.neoflex.nfcore.application.Lang
import ru.neoflex.nfcore.application.LocContainer
import ru.neoflex.nfcore.application.LocModule
import ru.neoflex.nfcore.application.LocNS
import ru.neoflex.nfcore.base.services.Context
import ru.neoflex.nfcore.base.util.DocFinder

class LocModuleExt extends LocModuleImpl {


    public static final String E_STRUCTURAL_FEATURES = "eStructuralFeatures"
    public static final String E_OPERATIONS = "eOperations"
    public static final String E_PARAMETERS = "eParameters"
    public static final String E_CLASSES = "eClasses"

    static def captionFromCamel(String s) {
        return StringUtils.capitalize(s).split("(?<=\\p{javaLowerCase})(?=\\p{javaUpperCase})").join(" ");
    }

    static def addCaptions(LocNS ns, ResourceSet langs, String name) {
        def nameCaption = captionFromCamel(name)
        langs.resources.each {it.contents.each {Lang lang->
            def caption = ns.captions.find {it.lang.name == lang.name}
            if (caption == null) {
                caption = ApplicationFactory.eINSTANCE.createLocCaption()
                caption.lang = lang
                caption.caption = nameCaption
                ns.captions.add(caption)
            }
        }}
    }

    static def createLocModuleIfNotExists(String name) {
        def rs = DocFinder.create(Context.current.store, ApplicationPackage.Literals.LOC_MODULE, [name: name])
                .execute().resourceSet
        if (rs.resources.empty) {
            def eObject = ApplicationFactory.eINSTANCE.createLocModule()
            eObject.name = name
            rs.resources.add(Context.current.store.createEObject(eObject))
        }
        return rs.resources.get(0).contents.get(0) as LocModule
    }

    static def generatePackagesModule() {
        Map<EClass, LocContainer> eClassesMap = [:]
        def langsRS = DocFinder.create(Context.current.store, ApplicationPackage.Literals.LANG)
                .execute().resourceSet
        def locModule = createLocModuleIfNotExists("packages")
        Context.current.registry.EPackages.each {ePackage->
            def ePackageNS = locModule.children.find {it.name == ePackage.nsPrefix}
            if (ePackageNS == null) {
                ePackageNS = ApplicationFactory.eINSTANCE.createLocNS()
                ePackageNS.name = ePackage.nsPrefix
                locModule.children.add(ePackageNS)
            }
            addCaptions(ePackageNS, langsRS, ePackage.nsPrefix)
            if (ePackage.EClassifiers.findAll {c->c instanceof EClass}.size() > 0) {
                def eClassesNS = ePackageNS.children.find {it.name == E_CLASSES }
                if (eClassesNS == null) {
                    eClassesNS = ApplicationFactory.eINSTANCE.createLocNS()
                    eClassesNS.name = E_CLASSES
                    ePackageNS.children.add(eClassesNS)
                }
                ePackage.EClassifiers.findAll {c->c instanceof EClass}.each {EClass eClass->
                    def eClassNS = eClassesNS.children.find {it.name == eClass.name}
                    if (eClassNS == null) {
                        eClassNS = ApplicationFactory.eINSTANCE.createLocNS()
                        eClassNS.name = eClass.name
                        eClassesNS.children.add(eClassNS)
                    }
                    eClassesMap[eClass] = eClassNS
                    addCaptions(eClassNS, langsRS, eClass.name)
                    if (eClass.EAllStructuralFeatures.size() > 0) {
                        def eStructuralFeaturesNS = eClassNS.children.find {it.name == E_STRUCTURAL_FEATURES }
                        if (eStructuralFeaturesNS == null) {
                            eStructuralFeaturesNS = ApplicationFactory.eINSTANCE.createLocNS()
                            eStructuralFeaturesNS.name = E_STRUCTURAL_FEATURES
                            eClassNS.children.add(eStructuralFeaturesNS)
                        }
                        eClass.getEStructuralFeatures().each {eStructuralFeature->
                            def sfNS = eStructuralFeaturesNS.children.find {it.name == eStructuralFeature.name}
                            if (sfNS == null) {
                                sfNS = ApplicationFactory.eINSTANCE.createLocNS()
                                sfNS.name = eStructuralFeature.name
                                eStructuralFeaturesNS.children.add(sfNS)
                            }
                            addCaptions(sfNS, langsRS, eStructuralFeature.name)
                        }
                    }
                    if (eClass.EAllOperations.size() > 0) {
                        def eOperationsNS = eClassNS.children.find {it.name == E_OPERATIONS }
                        if (eOperationsNS == null) {
                            eOperationsNS = ApplicationFactory.eINSTANCE.createLocNS()
                            eOperationsNS.name = E_OPERATIONS
                            eClassNS.children.add(eOperationsNS)
                        }
                        eClass.getEOperations().each {eOperation->
                            def eOperationNS = eOperationsNS.children.find {it.name == eOperation.name}
                            if (eOperationNS == null) {
                                eOperationNS = ApplicationFactory.eINSTANCE.createLocNS()
                                eOperationNS.name = eOperation.name
                                eOperationsNS.children.add(eOperationNS)
                            }
                            addCaptions(eOperationNS, langsRS, eOperation.name)
                            if (eOperation.EParameters.size() > 0) {
                                def eParametersNS = eOperationNS.children.find {it.name == E_PARAMETERS }
                                if (eParametersNS == null) {
                                    eParametersNS = ApplicationFactory.eINSTANCE.createLocNS()
                                    eParametersNS.name = E_PARAMETERS
                                    eOperationNS.children.add(eParametersNS)
                                }
                                eOperation.EParameters.each {eParameter->
                                    def eParameterNS = eParametersNS.children.find {it.name == eParameter.name}
                                    if (eParameterNS == null) {
                                        eParameterNS = ApplicationFactory.eINSTANCE.createLocNS()
                                        eParameterNS.name = eParameter.name
                                        eParametersNS.children.add(eParameterNS)
                                    }
                                    addCaptions(eParameterNS, langsRS, eParameter.name)
                                }
                            }
                        }
                    }
                }
            }
        }
        eClassesMap.keySet().each {eClass->
            def eClassNS = eClassesMap.get(eClass)
            def eStructuralFeaturesNS = eClassNS.children.find {it.name == E_STRUCTURAL_FEATURES}
            def eOperationsNS = eClassNS.children.find {it.name == E_OPERATIONS}
            eClass.getESuperTypes().each {eSuperClass->
                def eSuperClassNS = eClassesMap.get(eSuperClass)
                if (eStructuralFeaturesNS != null) {
                    def eSuperStructuralFeaturesNS = eSuperClassNS.children.find {it.name == E_STRUCTURAL_FEATURES}
                    if (eSuperStructuralFeaturesNS != null) {
                        eStructuralFeaturesNS.inherits.add(eSuperStructuralFeaturesNS)
                    }
                }
                if (eOperationsNS != null) {
                    def eSuperOperationsNS = eSuperClassNS.children.find {it.name == E_OPERATIONS}
                    if (eSuperOperationsNS != null) {
                        eOperationsNS.inherits.add(eSuperOperationsNS)
                    }
                }
            }
        }
        Context.current.store.updateEObject(locModule)
        return locModule
    }

    static def generateLocales() {
        def locModulesResources = DocFinder.create(Context.current.store, ApplicationPackage.Literals.LOC_MODULE)
                .execute().resourceSet.resources.findAll {true}
        def langsResources = DocFinder.create(Context.current.store, ApplicationPackage.Literals.LANG)
                .execute().resourceSet.resources.findAll {true}
        langsResources.each {lgrs->
            Lang lang = lgrs.contents[0]
            locModulesResources.each {lmrs->
                LocModule locModule = lmrs.contents[0]
                String json = Context.current.epsilon.generate("org/eclipse/epsilon/LocModule2json.egl", [lang: lang.name], locModule)
                File out = Context.current.workspace.getFile("public/locales/${lang.name}/${locModule.name}.json")
                out.parentFile.mkdirs()
                out.write(json)
            }
        }
    }

    {
        LocModule.metaClass.static.generatePackagesModule = {->
            LocModuleExt.generatePackagesModule()
        }
        LocModule.metaClass.static.generateLocales = {->
            LocModuleExt.generateLocales()
        }
        LocModuleExt.generatePackagesModule()
        LocModuleExt.generateLocales()
    }
}
