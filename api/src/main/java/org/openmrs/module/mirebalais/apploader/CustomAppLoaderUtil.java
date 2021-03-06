package org.openmrs.module.mirebalais.apploader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appui.AppUiExtensions;
import org.openmrs.module.metadatadeploy.descriptor.EncounterTypeDescriptor;
import org.openmrs.module.pihcore.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.AWAITING_ADMISSION_ACTIONS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.HOME_PAGE_APPS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.OVERALL_ACTIONS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_DATA_EXPORT_REPORTS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.REPORTING_OVERVIEW_REPORTS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.SYSTEM_ADMINISTRATION_APPS_ORDER;
import static org.openmrs.module.mirebalais.apploader.CustomAppLoaderConstants.VISIT_ACTIONS_ORDER;



public class CustomAppLoaderUtil {

    private static final Log log = LogFactory.getLog(CustomAppLoaderUtil.class);

    private static final String BASE_PREFIX = "web/module/resources/";

    static public AppDescriptor app(String id, String label, String icon, String url, String privilege, ObjectNode config) {

        AppDescriptor app = new AppDescriptor(id, id, label, url, icon, null, 0, privilege, null);

        if (config != null) {
            app.setConfig(config);
        }

        return app;
    }

    static public AppDescriptor findPatientTemplateApp(String id, String label, String icon, String privilege, String afterSelectedUrl, ArrayNode breadcrumbs) {

        AppDescriptor app = new AppDescriptor(id, id, label, "coreapps/findpatient/findPatient.page?app=" + id, icon, null, 0, privilege, null);

        app.setConfig(objectNode(
                "afterSelectedUrl", afterSelectedUrl,
                "label", label,
                "heading", label,
                "showLastViewedPatients", false,
                "breadcrumbs", breadcrumbs));

        return app;
    }

    static public AppDescriptor addToHomePage(AppDescriptor app, String require) {
        appExtension(app, app.getId() + ".appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                "pihcore/router/appEntryRouter.page?app=" + app.getId(),  // NOTE THAT WE ARE NOW ROUTING ALL HOME PAGE APPS VIA THE APP ENTRY ROUTER!
                app.getRequiredPrivilege(),
                require,
                HOME_PAGE_APPS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.HOME_PAGE);
        return app;
    }

    // there's a problem with redirecting when the provider is the same as the app name (which happens in mirebalais with the "mirebalais" provider)
    // so we have this legacy method that avoids the router
    static public AppDescriptor addToHomePageWithoutUsingRouter(AppDescriptor app, String require) {
        appExtension(app, app.getId() + ".appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                require,
                HOME_PAGE_APPS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.HOME_PAGE);
        return app;
    }

    static public AppDescriptor addToHomePage(AppDescriptor app) {
        return addToHomePage(app, null);
    }

    static public AppDescriptor addToHomePageWithoutUsingRouter(AppDescriptor app) {
        return addToHomePageWithoutUsingRouter(app, null) ;
    }

    static public AppDescriptor addToOverallActions(AppDescriptor app, String label, String require) {
        appExtension(app, app.getId() + ".overallActions.appLink",
                label,
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                require,
                OVERALL_ACTIONS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.OVERALL_ACTIONS);
        return app;
    }

    static public AppDescriptor addToOverallActions(AppDescriptor app, String label) {
        return addToOverallActions(app,label, null);
    }

    static public AppDescriptor addToSystemAdministrationPage(AppDescriptor app, String require) {
        appExtension(app, app.getId() + ".systemAdministration.appLink",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                require,
                SYSTEM_ADMINISTRATION_APPS_ORDER.indexOf(app.getId()),
                CustomAppLoaderConstants.ExtensionPoints.SYSTEM_ADMINISTRATION_PAGE);
        return app;
    }

    static public AppDescriptor addToSystemAdministrationPage(AppDescriptor app) {
        return addToSystemAdministrationPage(app, null);
    }


    static public AppDescriptor addToClinicianDashboardFirstColumn(AppDescriptor app, String provider, String fragment) {
        String appId = app.getId() + ".clinicianDashboardFirstColumn";
        appExtension(app, appId,
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                null,
                CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER.indexOf(appId),
                CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN)
                .setExtensionParams(map("provider", provider,
                        "fragment", fragment));
        return app;
    }

    static public AppDescriptor addToClinicianDashboardSecondColumn(AppDescriptor app, String provider, String fragment) {
        String appId = app.getId() + ".clinicianDashboardSecondColumn";
        appExtension(app, appId ,
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                null,
                CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER.indexOf(appId),
                CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN)
                .setExtensionParams(map("provider", provider,
                        "fragment", fragment));
        return app;
    }

    static public AppDescriptor addToRegistrationSummaryContent(AppDescriptor app, String provider, String fragment) {
        appExtension(app, app.getId() + ".registrationSummaryContent",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                null,
                1,  // TODO; create array to set order  like others in CustomAppLoaderConstants
                CustomAppLoaderConstants.ExtensionPoints.REGISTRATION_SUMMARY_CONTENT)
                .setExtensionParams(map("provider", provider,
                        "fragment", fragment));
        return app;
    }

    static public AppDescriptor addToRegistrationSummarySecondColumnContent(AppDescriptor app, String provider, String fragment) {
        appExtension(app, app.getId() + ".registrationSummaryContent",
                app.getLabel(),
                app.getIcon(),
                "link",
                app.getUrl(),
                app.getRequiredPrivilege(),
                null,
                1,  // TODO; create array to set order  like others in CustomAppLoaderConstants
                CustomAppLoaderConstants.ExtensionPoints.REGISTRATION_SUMMARY_SECOND_COLUMN_CONTENT)
                .setExtensionParams(map("provider", provider,
                        "fragment", fragment));
        return app;
    }

    static public Extension visitAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.VISIT_ACTIONS, VISIT_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension overallAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.OVERALL_ACTIONS, OVERALL_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension overallRegistrationAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.OVERALL_REGISTRATION_ACTIONS, OVERALL_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension awaitingAdmissionAction(String id, String label, String icon, String type, String urlOrScript, String privilege, String require) {
        return  extension(id, label, icon, type, urlOrScript, privilege, require,
                CustomAppLoaderConstants.ExtensionPoints.AWAITING_ADMISSION_ACTIONS, AWAITING_ADMISSION_ACTIONS_ORDER.indexOf(id), null);
    }

    static public Extension dashboardTab(String id, String label, String privilege, String provider, String fragment) {
        return new Extension(id, null, CustomAppLoaderConstants.ExtensionPoints.DASHBOARD_TAB, "link", label, null, 0,
                privilege, map("provider", provider, "fragment", fragment));
    }

    static public Extension encounterTemplate(String id, String templateProvider, String templateFragment) {
        return new Extension(id, null, CustomAppLoaderConstants.ExtensionPoints.ENCOUNTER_TEMPLATE, "fragment", null, null, 0, null,
                map("templateId", id, "templateFragmentProviderName", templateProvider, "templateFragmentId", templateFragment));
    }

    static public Extension header(String id, String logo) {
        Map<String, Object> configs = map("logo-link-url", "/index.htm", "logo-icon-url", logo);
        return new Extension(id, null, AppUiExtensions.HEADER_CONFIG_EXTENSION, "config", null, null, 0, null, configs);
    }

    static public Extension fragmentExtension(String id, String provider, String fragment, String privilege, String extensionPoint, Map<String, Object> config) {
        return new Extension(id, null, extensionPoint, "include-fragment", null, null, 0,
                privilege, map("provider", provider, "fragment", fragment, "fragmentConfig", config));
    }

    static public Extension overviewReport(String id, String label, String definitionUuid, String privilege, String linkId) {
        return report(id, label, "reportingui", "runReport", definitionUuid, privilege,
                CustomAppLoaderConstants.ExtensionPoints.REPORTING_OVERVIEW_REPORTS, REPORTING_OVERVIEW_REPORTS_ORDER.indexOf(id), linkId);
    }

    static public Extension dailyReport(String id, String label, String definitionUuid, String privilege, String linkId) {
        return report(id, label, "mirebalaisreports", "dailyReport", definitionUuid, privilege,
                CustomAppLoaderConstants.ExtensionPoints.REPORTING_OVERVIEW_REPORTS, REPORTING_OVERVIEW_REPORTS_ORDER.indexOf(id), linkId);
    }

    static public Extension dataExport(String id, String label, String definitionUuid, String privilege, String linkId) {
        // note the indexOf(id) + 100 to make sure that these reports are ranked below the others defined in mirebalais reports--to do, we want to fix this at some point
        return report(id, label, "reportingui", "runReport",definitionUuid, privilege,
                CustomAppLoaderConstants.ExtensionPoints.REPORTING_DATA_EXPORT, REPORTING_DATA_EXPORT_REPORTS_ORDER.indexOf(id) + 100, linkId);
    }

    static public Extension report(String id, String label, String provider, String fragment, String definitionUuid, String privilege, String extensionPoint, int order, String linkId) {
        return new Extension(id, null, extensionPoint, "link", label,provider + "/" + fragment + ".page?reportDefinition=" + definitionUuid,
                order, privilege, map("linkId", linkId));
    }

    static public Extension clinicianDashboardFirstColumn(String id, String label, String icon, String privilege, String require, String provider, String fragment, Map<String,Object> extensionParams) {
        return fragmentExtension(id, label, icon, privilege, require, CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_FIRST_COLUMN, provider, fragment, CLINICIAN_DASHBOARD_FIRST_COLUMN_ORDER, extensionParams);
    }

    static public Extension clinicianDashboardSecondColumn(String id, String label, String icon, String privilege, String require, String provider, String fragment, Map<String,Object> extensionParams) {
        return fragmentExtension(id, label, icon, privilege, require, CustomAppLoaderConstants.ExtensionPoints.CLINICIAN_DASHBOARD_SECOND_COLUMN, provider, fragment, CLINICIAN_DASHBOARD_SECOND_COLUMN_ORDER, extensionParams);
    }

    static public Extension fragmentExtension(String id, String label, String icon, String privilege, String require, String extensionPointId, String provider, String fragment, List<String> orderFromList, Map<String,Object> extensionParams) {
        if (extensionParams == null) {
            extensionParams = new HashMap<String, Object>();
        }
        extensionParams.put("provider", provider);
        extensionParams.put("fragment", fragment);
        return extension(id, label, icon, "fragment", null, privilege, require, extensionPointId, orderFromList == null ? Integer.MIN_VALUE : orderFromList.indexOf(id), extensionParams);
    }

    static public Extension extension(String id, String label, String icon, String type, String urlOrScript, String privilege, String require, String extensionPoint, int order, Map<String,Object> extensionParams) {
        Extension extension = new Extension(id, null,extensionPoint, type, label, null, order, privilege, null);
        extension.setIcon(icon);

        if (StringUtils.isNotBlank(require)) {
            extension.setRequire(require);
        }

        if (type.equals("link")) {
            extension.setUrl(urlOrScript);
        }
        else if (type.equals("script")) {
            extension.setScript(urlOrScript);
        }
        else if (type.equals("fragment")) {
            // don't use urlOrScript
        }
        else {
            throw new IllegalStateException("Invalid type: " + type);
        }

        if (extensionParams != null) {
            extension.setExtensionParams(extensionParams);
        }

        return extension;
    }


    static public Extension appExtension(AppDescriptor app, String id, String label, String icon, String type, String url,
                                         String requiredPrivilege, String require, int order, String extensionPoint) {

        Extension extension = new Extension(id, app.getId(), extensionPoint, type, label, url, order, requiredPrivilege, null);
        extension.setIcon(icon);

        if (StringUtils.isNotBlank(require)) {
            extension.setRequire(require);
        }

        if (app.getExtensions() == null) {
            app.setExtensions(new ArrayList<Extension>());
        }

        app.getExtensions().add(extension);
        return extension;
    }

    static public String enterSimpleHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String editSimpleHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/editHtmlFormWithSimpleUi.page?patientId={{patient.uuid}}&encounterId={{encounter.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String enterStandardHtmlFormLink(String definitionUiResource) {
        return "htmlformentryui/htmlform/enterHtmlFormWithStandardUi.page?patientId={{patient.uuid}}&visitId={{visit.id}}&definitionUiResource=" + definitionUiResource;
    }

    static public String andCreateVisit() {
        return "&createVisit=true";
    }

    static public void addFeatureToggleToApp(AppDescriptor app, String featureToggle) {
        app.setFeatureToggle(featureToggle);
    }

    static public void addFeatureToggleToExtension(Extension ext, String featureToggle) {
        ext.setFeatureToggle(featureToggle);
    }

    static public void registerTemplateForEncounterType(EncounterTypeDescriptor encounterType, Extension template, String icon) {
        registerTemplateForEncounterType(encounterType, template, icon, null, null, null, null);
    }

    static public void registerTemplateForEncounterType(EncounterTypeDescriptor encounterType, Extension template, String icon,
                                                        Boolean displayWithHtmlForm, Boolean editable,
                                                        String editUrl,  // note that if editUrl is null/empty, the standard Html Form link is used by default--that's why we don't specify this is most cases
                                                        String primaryEncounterRoleUuid) {

        Map<String,Object> extensionParams = template.getExtensionParams();

        if (!extensionParams.containsKey("supportedEncounterTypes")) {
            extensionParams.put("supportedEncounterTypes", new HashMap<String,Object>());
        }

        Map<String,Object> encounterTypeParams = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(icon)) {
            encounterTypeParams.put("icon", icon);
        }
        if (displayWithHtmlForm != null) {
            encounterTypeParams.put("displayWithHtmlForm", displayWithHtmlForm);
        }
        if (editable != null) {
            encounterTypeParams.put("editable", editable);
        }

        if (StringUtils.isNotBlank(editUrl)) {
            encounterTypeParams.put("editUrl", editUrl);
        }

        if (StringUtils.isNotBlank(primaryEncounterRoleUuid)) {
            encounterTypeParams.put("primaryEncounterRoleUuid", primaryEncounterRoleUuid);
        }

        ((Map<String,Object>) extensionParams.get("supportedEncounterTypes")).put(encounterType.uuid(), encounterTypeParams);
    }

    static public ObjectNode patientRegistrationConfig(String afterCreatedUrl, String patientDashboardLink, String registrationEncounterType, String registrationEncounterRole, ObjectNode ... sections) {
        return objectNode("afterCreatedUrl", afterCreatedUrl,
                "patientDashboardLink", patientDashboardLink,
                "allowRetrospectiveEntry", true,
                "allowUnknownPatients", true,
                "allowManualIdentifier", true,
                "registrationEncounter", objectNode("encounterType", registrationEncounterType,
                                                    "encounterRole", registrationEncounterRole),
                "sections", arrayNode(sections));
    }

    static public ObjectNode section(String sectionId, String sectionLabel, ObjectNode ... questions) {
        return objectNode("id", sectionId,
                "label", sectionLabel,
                "questions", arrayNode(questions));
    }

    static public ObjectNode question(String questionId, String questionLegend, ObjectNode ... fields) {
        return objectNode("id", questionId,
                "legend", questionLegend,
                "fields", arrayNode(fields));
    }

    static public ObjectNode question(String questionId, String questionLegend, String questionHeader, ObjectNode ... fields) {
        return objectNode("id", questionId,
                "legend", questionLegend,
                "header", questionHeader,
                "fields", arrayNode(fields));
    }

    static public ObjectNode field(String formFieldName, String label, String type, String uuid, String widgetProvider, String widgetFragment, String ... cssClasses) {

        return objectNode("formFieldName", formFieldName,
                        "label", label,
                        "type", type,
                        "uuid", uuid,
                        "cssClasses", arrayNode(cssClasses),
                        "widget", objectNode("providerName", widgetProvider,
                        "fragmentId", widgetFragment));


    }

    static public ObjectNode field(String formFieldName, String label, String type, String uuid, String widgetProvider, String widgetFragment, ObjectNode config, String ... cssClasses) {

        return objectNode("formFieldName", formFieldName,
                "label", label,
                "type", type,
                "uuid", uuid,
                "cssClasses", arrayNode(cssClasses),
                "widget", objectNode("providerName", widgetProvider,
                                    "fragmentId", widgetFragment,
                                    "config", config));
    }

    static public ObjectNode option(String label, String value) {
        return objectNode("label", label,
                            "value", value);
    }

    static public ArrayNode arrayNode(ObjectNode ... nodes) {
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        for (int i = 0; i < nodes.length; i++) {
            arrayNode.add(nodes[i]);
        }
        return arrayNode;
    }

    static public ArrayNode arrayNode(String ... nodes) {
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        for (int i = 0; i < nodes.length; i++) {
            arrayNode.add(nodes[i]);
        }
        return arrayNode;
    }

    static public ObjectNode objectNode(Object ... obj) {

        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];

            if (value instanceof Boolean) {
                objectNode.put(key, (Boolean) value);
            }
            else if (value instanceof String) {
                objectNode.put(key, (String) value);
            }
            else if (value instanceof Integer) {
                objectNode.put(key, (Integer) value);
            }
            else if (value instanceof ArrayNode) {
                objectNode.put(key, (ArrayNode) value);
            }
            else if (value instanceof ObjectNode) {
                objectNode.put(key, (ObjectNode) value);
            }
        }

        return objectNode;
    }

    static public Map<String,Object> map(Object ... obj) {

        Map<String,Object> map = new HashMap<String, Object>();

        for (int i = 0; i < obj.length; i=i+2) {
            String key = (String) obj[i];
            Object value = obj[i+1];
            map.put(key, value);
        }

        return map;
    }

    static public String determineHtmlFormPath(Config config, String formName) {
        return determineResourcePath(config, "pihcore", "htmlforms", formName + ".xml");
    }

    static public String determineResourcePath(Config config, String providerName, String prefix, String resource) {

        try {
            // kind of ugly, and I couldn't mock it properly
            ModuleClassLoader mcl = ModuleFactory.getModuleClassLoader(ModuleFactory.getStartedModuleById(providerName));

            // first try full path with country and site
            String resourcePath = prefix + "/" + config.getCountry().name().toLowerCase() + "/" + config.getSite().name().toLowerCase()
                    + "/" + resource;

            if (mcl.findResource(BASE_PREFIX + resourcePath) != null) {
                return providerName + ":" + resourcePath;
            }

            // now try just country path
            resourcePath = prefix + "/" + config.getCountry().name().toLowerCase() + "/" + resource;

            if (mcl.findResource(BASE_PREFIX + resourcePath) != null) {
                return providerName + ":" + resourcePath;
            }

            // now the base path
            resourcePath = prefix + "/" + resource;
            if (mcl.findResource(BASE_PREFIX + resourcePath) != null) {
                return providerName + ":" + resourcePath;
            }
        }
        // this catch and the return are kind of hacky, I did it this way to get the MirebalaisHospitalActivatorComponentTest to pass, but we should do this btter
        catch (Exception e) {
            log.error("Unable to find resource " + resource + " from provider " + providerName + " with prefix " + prefix + " - This error is expected when running tests.", e);
        }

        return "";
    }

    static public Boolean containsExtension(List<Extension> extensions, String extensionId) {
        for (Extension e : extensions) {
            if (e.getId().equals(extensionId)) {
                return true;
            }
        }
        return false;
    }

}
