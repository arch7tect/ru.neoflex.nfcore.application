package ru.neoflex.nfcore.application.impl

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.resource.ResourceSet
import ru.neoflex.nfcore.application.ApplicationFactory
import ru.neoflex.nfcore.application.ApplicationPackage
import ru.neoflex.nfcore.application.Lang
import ru.neoflex.nfcore.application.LocContainer
import ru.neoflex.nfcore.application.LocModule
import ru.neoflex.nfcore.application.LocNS
import ru.neoflex.nfcore.base.services.Context
import ru.neoflex.nfcore.base.util.DocFinder

class LocModuleExt extends LocModuleImpl {

    static def captionFromCamel(String s) {
        if (!s.charAt(0).upperCase) {
            s = s.charAt(0).toUpperCase().toString() + s.substring(1)
        }
        return s.split("(?<=\\p{javaLowerCase})(?=\\p{javaUpperCase})").join(" ");
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

    static def createLangIfNotExists(String lang) {
        def rs = DocFinder.create(Context.current.store, ApplicationPackage.Literals.LANG, [name: lang])
                .execute().resourceSet
        if (rs.resources.empty) {
            def eObject = ApplicationFactory.eINSTANCE.createLang()
            eObject.name = lang
            rs.resources.add(Context.current.store.createEObject(eObject))
        }
        return rs.resources.get(0).contents.get(0)
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
        createLangIfNotExists("en")
        createLangIfNotExists("ru")
        def langsRS = DocFinder.create(Context.current.store, ApplicationPackage.Literals.LANG)
                .execute().resourceSet
        def locModule = createLocModuleIfNotExists("packages")
        Context.current.registry.EPackages.each {ePackage->
            def packageNS = locModule.children.find {it.name == ePackage.nsPrefix}
            if (packageNS == null) {
                packageNS = ApplicationFactory.eINSTANCE.createLocNS()
                packageNS.name = ePackage.nsPrefix
                locModule.children.add(packageNS)
            }
            addCaptions(packageNS, langsRS, ePackage.nsPrefix)
            ePackage.EClassifiers.findAll {c->c instanceof EClass}.each {EClass eClass->
                def classNS = packageNS.children.find {it.name == eClass.name}
                if (classNS == null) {
                    classNS = ApplicationFactory.eINSTANCE.createLocNS()
                    classNS.name = eClass.name
                    packageNS.children.add(classNS)
                }
                eClassesMap[eClass] = classNS
                addCaptions(classNS, langsRS, eClass.name)
                def structuralFeaturesNS = classNS.children.find {it.name == "structuralFeatures"}
                if (structuralFeaturesNS == null) {
                    structuralFeaturesNS = ApplicationFactory.eINSTANCE.createLocNS()
                    structuralFeaturesNS.name = "structuralFeatures"
                    classNS.children.add(structuralFeaturesNS)
                }
                eClass.getEStructuralFeatures().each {eStructuralFeature->
                    def sfNS = structuralFeaturesNS.children.find {it.name == eStructuralFeature.name}
                    if (sfNS == null) {
                        sfNS = ApplicationFactory.eINSTANCE.createLocNS()
                        sfNS.name = eStructuralFeature.name
                        structuralFeaturesNS.children.add(sfNS)
                    }
                    addCaptions(sfNS, langsRS, eStructuralFeature.name)
                }
            }
        }
        eClassesMap.keySet().each {eClass->
            def container = eClassesMap.get(eClass)
            def structuralFeatures = container.children.find {it.name == "structuralFeatures"}
            eClass.getESuperTypes().each {eSuperClass->
                def superContainer = eClassesMap.get(eSuperClass)
                def superStructuralFeatures = superContainer.children.find {it.name == "structuralFeatures"}
                structuralFeatures.inherits.add(superStructuralFeatures)
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
                String json = Context.current.epsilon.generate("org/eclipse/epsilon/GenPackagesLocales.egl", [lang: lang.name], locModule)
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
