package ru.neoflex.nfcore.application.impl

import ru.neoflex.nfcore.application.ApplicationFactory
import ru.neoflex.nfcore.application.ApplicationPackage
import ru.neoflex.nfcore.base.services.Context
import ru.neoflex.nfcore.base.util.DocFinder
import ru.neoflex.nfcore.locales.LocalesFactory
import ru.neoflex.nfcore.locales.LocalesPackage

class ApplicationInit {
    static def createApplicationIfNotExists(String name) {
        def rs = DocFinder.create(Context.current.store, ApplicationPackage.Literals.APPLICATION, [name: name])
                .execute().resourceSet
        if (rs.resources.empty) {
            def eObject = ApplicationFactory.eINSTANCE.createApplication()
            eObject.name = name
            rs.resources.add(Context.current.store.createEObject(eObject))
        }
        return rs.resources.get(0).contents.get(0)
    }
    {
        createApplicationIfNotExists("EcoreApp")
    }
}
