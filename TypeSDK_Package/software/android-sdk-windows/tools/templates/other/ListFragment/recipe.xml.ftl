<?xml version="1.0"?>
<recipe>

    <#if useSupport><dependency mavenUrl="com.android.support:support-v4:19.+"/></#if>
<#if switchGrid == true>
    <merge from="res/values/refs.xml.ftl"
             to="${escapeXmlAttribute(resOut)}/values/refs.xml" />
    <merge from="res/values/refs_lrg.xml.ftl"
           to="${escapeXmlAttribute(resOut)}/values-large/refs.xml" />
    <merge from="res/values/refs_lrg.xml.ftl"
           to="${escapeXmlAttribute(resOut)}/values-sw600dp/refs.xml" />

    <instantiate from="res/layout/fragment_grid.xml"
                 to="${escapeXmlAttribute(resOut)}/layout/${fragment_layout}_grid.xml" />

    <instantiate from="res/layout/fragment_list.xml"
                 to="${escapeXmlAttribute(resOut)}/layout/${fragment_layout}_list.xml" />
</#if>

    <instantiate from="src/app_package/ListFragment.java.ftl"
                 to="${escapeXmlAttribute(srcOut)}/${className}.java" />

    <instantiate from="src/app_package/dummy/DummyContent.java.ftl"
                 to="${escapeXmlAttribute(srcOut)}/dummy/DummyContent.java" />

    <open file="${escapeXmlAttribute(srcOut)}/${className}.java" />

</recipe>
