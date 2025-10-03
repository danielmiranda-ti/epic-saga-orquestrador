package com.arquitetura.epic.saga.orchestrator.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private static final String DOMAIN_PACKAGE = "com.arquitetura.epic.saga.orchestrator.core.domain..";
    private static final String SERVICE_PACKAGE = "com.arquitetura.epic.saga.orchestrator.core.service..";
    private static final String PORT_PACKAGE = "com.arquitetura.epic.saga.orchestrator.core.port..";

    @Test
    void domainClassesShouldNotDependOnServiceOrPort() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.arquitetura.epic.saga.orchestrator.core");

        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(DOMAIN_PACKAGE)
                .should().dependOnClassesThat().resideInAnyPackage(SERVICE_PACKAGE, PORT_PACKAGE)
                .check(importedClasses);
    }

//    @Test
//    void servicesShouldOnlyBeAccessedByPortsOrOtherServices() {
//        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.arquitetura.epic.saga.orchestrator.core");
//
//        ArchRuleDefinition.classes()
//                .that().resideInAPackage(SERVICE_PACKAGE)
//                .should().onlyBeAccessed().byAnyPackage(SERVICE_PACKAGE, PORT_PACKAGE)
//                .check(importedClasses);
//    }

    // Adicione outras regras conforme sua arquitetura
}