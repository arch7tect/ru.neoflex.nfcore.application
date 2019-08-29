package ru.neoflex.nfcore.application.impl

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.util.EcoreUtil
import ru.neoflex.nfcore.application.ApplicationFactory
import ru.neoflex.nfcore.application.ApplicationPackage
import ru.neoflex.nfcore.base.services.Context
import ru.neoflex.nfcore.base.util.DocFinder
import ru.neoflex.nfcore.reports.ReportsPackage

class ApplicationInit {
    static def findOrCreateEObject(EClass eClass, String name, boolean replace = false) {
        def rs = DocFinder.create(Context.current.store, eClass, [name: name])
                .execute().resourceSet
        while (replace && !rs.resources.empty) {
            Context.current.store.deleteResource(rs.resources.remove(0).getURI())
        }
        if (rs.resources.empty) {
            def eObject = EcoreUtil.create(eClass)
            eObject.eSet(eClass.getEStructuralFeature("name"), name)
            rs.resources.add(Context.current.store.createEObject(eObject))
        }
        return rs.resources.get(0).contents.get(0)
    }

    static def recreateApplication(String name) {
        def rs = DocFinder.create(Context.current.store, ApplicationPackage.Literals.APPLICATION, [name: name])
                .execute().resourceSet
        while (!rs.resources.empty) {
            Context.current.store.deleteResource(rs.resources.remove(0).getURI())
        }
        if (rs.resources.empty) {
            def eObject = ApplicationFactory.eINSTANCE.createApplication()
            eObject.name = name
            def referenceTree = ApplicationFactory.eINSTANCE.createCatalogNode()
            for (i in 1..5) {
                def catalog = ApplicationFactory.eINSTANCE.createCatalogNode()
                catalog.name = 'Catalog' + i
                referenceTree.children.add(catalog)
            }
            def reportNode = ApplicationFactory.eINSTANCE.createEObjectNode()
            reportNode.name = 'report1'
            def report1 = findOrCreateEObject(ReportsPackage.Literals.REPORT, "report1", true)
            reportNode.EObject = report1
            referenceTree.children[0].children.add(reportNode)
            eObject.setReferenceTree(referenceTree)
            rs.resources.add(Context.current.store.createEObject(eObject))
        }
        return rs.resources.get(0).contents.get(0)
    }
    {
        recreateApplication("ReportsApp")
    }
}
