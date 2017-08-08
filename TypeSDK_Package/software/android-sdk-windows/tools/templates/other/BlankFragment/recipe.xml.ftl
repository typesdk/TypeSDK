<?xml version="1.0"?>
<recipe>

    <#if useSupport><dependency mavenUrl="com.android.support:support-v4:19.+"/></#if>
    <merge from="res/values/strings.xml" to="${escapeXmlAttribute(resOut)}/values/strings.xml" />

    <#if includeLayout>
        <instantiate from="res/layout/fragment_blank.xml.ftl"
                       to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(fragmentName)}.xml" />

        <open file="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(fragmentName)}.xml" />
    </#if>

    <open file="${escapeXmlAttribute(srcOut)}/${className}.java" />

    <instantiate from="src/app_package/BlankFragment.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${className}.java" />

</recipe>
