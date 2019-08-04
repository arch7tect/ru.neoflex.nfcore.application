package ru.neoflex.nfcore.application.components;

import org.eclipse.emf.ecore.EPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import ru.neoflex.nfcore.application.ApplicationPackage;
import ru.neoflex.nfcore.base.components.IModuleRegistry;
import ru.neoflex.nfcore.base.components.PackageRegistry;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationModuleRegistry implements IModuleRegistry {
    @Override
    public List<EPackage> getEPackages() {
        List<EPackage> result = new ArrayList<>();
        result.add(ApplicationPackage.eINSTANCE);
        return result;
    }
}
