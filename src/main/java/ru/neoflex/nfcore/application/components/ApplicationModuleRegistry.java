package ru.neoflex.nfcore.application.components;

import org.eclipse.emf.ecore.EPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import ru.neoflex.nfcore.application.ApplicationPackage;
import ru.neoflex.nfcore.base.components.IModuleRegistry;
import ru.neoflex.nfcore.datasource.DatasourcePackage;
import ru.neoflex.nfcore.locales.LocalesPackage;
import ru.neoflex.nfcore.reports.ReportsPackage;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan("ru.neoflex.nfcore")
@Component
public class ApplicationModuleRegistry implements IModuleRegistry {
    @Override
    public List<EPackage> getEPackages() {
        List<EPackage> result = new ArrayList<>();
        result.add(ApplicationPackage.eINSTANCE);
        result.add(DatasourcePackage.eINSTANCE);
        result.add(LocalesPackage.eINSTANCE);
        result.add(ReportsPackage.eINSTANCE);
        return result;
    }
}
