package com.arquitetura.epic.saga.orchestrator.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


@AnalyzeClasses(
        packages = "com.arquitetura.epic.saga.orchestrator",
        importOptions = { ImportOption.DoNotIncludeTests.class } // ignora classes de teste
 )
public class HexagonalLayersTest {

    @ArchTest
    static final ArchRule hexagonal_layers_must_be_respected = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("com.arquitetura.epic.saga.orchestrator.core.domain..")
            .layer("Core").definedBy("com.arquitetura.epic.saga.orchestrator.core..")
            .layer("AdaptersIn").definedBy("com.arquitetura.epic.saga.orchestrator.adapter.in..")
            .layer("AdaptersOut").definedBy("com.arquitetura.epic.saga.orchestrator.adapter.out..")
            .layer("Config").definedBy("com.arquitetura.epic.saga.orchestrator.infraestrutura.config..")
            .layer("InfraUtil").definedBy("com.arquitetura.epic.saga.orchestrator.infraestrutura.util..")
            // Dependências permitidas
            .whereLayer("Core").mayOnlyAccessLayers("Domain", "InfraUtil")
            .whereLayer("AdaptersIn").mayOnlyAccessLayers("Core", "Domain", "InfraUtil")
            .whereLayer("AdaptersOut").mayOnlyAccessLayers("Core", "Domain", "InfraUtil")
            .whereLayer("Config").mayOnlyAccessLayers("Core", "Domain")
            .whereLayer("InfraUtil").mayOnlyBeAccessedByLayers("AdaptersIn", "AdaptersOut", "Core")
            // Quem pode acessar quem
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Core", "AdaptersIn", "AdaptersOut")
            .whereLayer("Core").mayOnlyBeAccessedByLayers("AdaptersIn", "AdaptersOut", "Config")
            .whereLayer("AdaptersOut").mayOnlyBeAccessedByLayers("Core", "InfraUtil");

//            .consideringOnlyDependenciesInLayers()
//                    .layer("Domain").definedBy("com.arquitetura.epic.saga.orchestrator.core.domain..")
//                    .layer("Core").definedBy("com.arquitetura.epic.saga.orchestrator.core.(*)..")
//                    .layer("AdaptersIn").definedBy("com.arquitetura.epic.saga.orchestrator.adapter.in..")
//                    .layer("AdaptersOut").definedBy("com.arquitetura.epic.saga.orchestrator.adapter.out..")
//                    .layer("Config").definedBy("com.arquitetura.epic.saga.orchestrator.infraestrutura.config..")
//                    .layer("InfraUtil").definedBy("com.arquitetura.epic.saga.orchestrator.infraestrutura.util..")
//
//                    // Dependências permitidas
//                    .whereLayer("Core").mayOnlyAccessLayers("Domain")
//                    .whereLayer("AdaptersIn").mayOnlyAccessLayers("Core", "Domain")
//                    .whereLayer("AdaptersOut").mayOnlyAccessLayers("Core", "Domain")
//                    .whereLayer("Config").mayOnlyAccessLayers("Core", "Domain")
//                    .whereLayer("InfraUtil").mayOnlyBeAccessedByLayers("AdaptersIn", "AdaptersOut", "Core") // utilitários podem ser usados por qualquer Adapter e Core
//
//                    // Quem pode acessar quem
//                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Core", "AdaptersIn", "AdaptersOut")
//                    .whereLayer("Core").mayOnlyBeAccessedByLayers("AdaptersIn", "AdaptersOut", "Config")
//                    .whereLayer("AdaptersOut").mayOnlyBeAccessedByLayers("Core", "InfraUtil")
//                    //.whereLayer("AdaptersIn").mayOnlyBeAccessedByLayers("Config")
////                    .whereLayer("Config").mayOnlyBeAccessedByLayers() // ninguém depende de Config
////                    .whereLayer("InfraUtil").mayOnlyAccessLayers() // InfraUtil não acessa ninguém
//                    .ignoreDependency(".*\\$.*", ".*\\$.*");


}
