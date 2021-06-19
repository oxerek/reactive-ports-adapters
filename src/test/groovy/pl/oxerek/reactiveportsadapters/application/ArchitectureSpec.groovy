package pl.oxerek.reactiveportsadapters.application

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures
import spock.lang.Shared
import spock.lang.Specification

class ArchitectureSpec extends Specification {

    @Shared
    JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("pl.oxerek.reactiveportsadapters")

    def "should not domain classes be allowed to access other packages"() {
        given:
        def rule = ArchRuleDefinition.noClasses().that()
                .resideInAnyPackage("..domain..")
                .should().accessClassesThat()
                .resideInAnyPackage("..adapters..", "..application..")

        expect:
        rule.check(classes)
    }

    def "should adapters classes be only classes allowed to access ports package"() {
        given:
        def rule = ArchRuleDefinition.classes().that()
                .resideInAnyPackage("..ports..")
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage(
                        "..domain..",
                        "..adapters.inbound..",
                        "..adapters.outbound..",
                        "..application.."
                )

        expect:
        rule.check(classes)
    }

    def "should not inbounds and outbounds be allowed to access each other"() {
        given:
        def inboundOutbound = ArchRuleDefinition.noClasses().that()
                .resideInAnyPackage("..adapters.inbound..")
                .should().accessClassesThat()
                .resideInAnyPackage("..adapters.outbound..")

        def outboundInbound = ArchRuleDefinition.noClasses().that()
                .resideInAnyPackage("..adapters.outbound..")
                .should().accessClassesThat()
                .resideInAnyPackage("..adapters.inbound..")

        expect:
        inboundOutbound.check(classes)
        outboundInbound.check(classes)
    }

    def "should layers have proper accesses"() {
        given:
        def rule = Architectures.layeredArchitecture()
                .layer("Domain").definedBy("..domain..")
                .layer("Adapters").definedBy("..adapters..")
                .layer("Application").definedBy("..application..")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapters")
                .whereLayer("Adapters").mayOnlyBeAccessedByLayers("Application")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Adapters", "Application")

        expect:
        rule.check(classes)
    }
}
