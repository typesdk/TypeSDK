<?xml version="1.0"?>
<recipe>

    <merge from="AndroidManifest.xml.ftl"
             to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />

    <copy from="res/drawable-nodpi/example_appwidget_preview.png"
            to="${escapeXmlAttribute(resOut)}/drawable-nodpi/example_appwidget_preview.png" />
    <instantiate from="res/layout/appwidget.xml"
                   to="${escapeXmlAttribute(resOut)}/layout/${class_name}.xml" />

    <#if configurable>
    <instantiate from="res/layout/appwidget_configure.xml"
                   to="${escapeXmlAttribute(resOut)}/layout/${class_name}_configure.xml" />
    </#if>

    <instantiate from="res/xml/appwidget_info.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/xml/${class_name}_info.xml" />
    <merge from="res/values/strings.xml.ftl"
           to="${escapeXmlAttribute(resOut)}/values/strings.xml" />
    <merge from="res/values-v14/dimens.xml"
           to="${escapeXmlAttribute(resOut)}/values-v14/dimens.xml" />
    <merge from="res/values/dimens.xml"
           to="${escapeXmlAttribute(resOut)}/values/dimens.xml" />

    <instantiate from="src/app_package/AppWidget.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${className}.java" />

    <#if configurable>
    <instantiate from="src/app_package/AppWidgetConfigureActivity.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${className}ConfigureActivity.java" />
    </#if>

    <open file="${escapeXmlAttribute(srcOut)}/${className}.java" />
</recipe>
