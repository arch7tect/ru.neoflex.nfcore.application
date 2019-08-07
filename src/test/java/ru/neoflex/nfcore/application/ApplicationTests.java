package ru.neoflex.nfcore.application;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.neoflex.nfcore.base.services.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    Context context;
    @Test
    public void contextLoads() {
    }

    @Test
    public void generateLocModule() throws Exception {
        LocModule locModule = (LocModule) context.getGroovy().eval(ApplicationPackage.Literals.LOC_MODULE, "generatePackagesModule", Lists.emptyList());
        Assert.assertEquals(3, locModule.getChildren().size());
    }

}
