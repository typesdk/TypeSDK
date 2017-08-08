<?xml version="1.0"?>
<recipe>

    <merge from="AndroidManifest.xml.ftl"
             to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />
    <merge from="res/values/strings.xml.ftl"
             to="${escapeXmlAttribute(resOut)}/values/strings.xml" />

    <copy from="res/layout-v17/dream.xml"
          to="${escapeXmlAttribute(resOut)}/layout-v17/${class_name}.xml" />

    <instantiate from="src/app_package/DreamService.java.ftl"
                 to="${escapeXmlAttribute(srcOut)}/${className}.java" />

<#if configurable>
    <copy from="res/xml/dream_prefs.xml"
          to="${escapeXmlAttribute(resOut)}/xml/${prefs_name}.xml" />

    <instantiate from="src/app_package/SettingsActivity.java.ftl"
                 to="${escapeXmlAttribute(srcOut)}/${settingsClassName}.java" />

    <instantiate from="res/xml/xml_dream.xml.ftl"
                 to="${escapeXmlAttribute(resOut)}/xml/${info_name}.xml" />
</#if>

    <open file="${escapeXmlAttribute(srcOut)}/${className}.java" />

</recipe>
