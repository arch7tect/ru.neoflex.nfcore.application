package ru.neoflex.nfcore.application.impl

import ru.neoflex.nfcore.application.ApplicationFactory
import ru.neoflex.nfcore.application.ApplicationPackage
import ru.neoflex.nfcore.application.Lang
import ru.neoflex.nfcore.base.services.Context
import ru.neoflex.nfcore.base.util.DocFinder

class ApplicationPackageExt {
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

    {
        Lang.metaClass.static.createLangIfNotExists = {String lang->
            createLangIfNotExists(lang)
        }
        Lang.createLangIfNotExists("en")
        Lang.createLangIfNotExists("ru")
    }
    ApplicationPackageExt() {}
}
