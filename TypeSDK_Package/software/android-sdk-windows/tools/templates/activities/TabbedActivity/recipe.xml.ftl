<?xml version="1.0"?>
<recipe>

    <#if appCompat><dependency mavenUrl="com.android.support:appcompat-v7:19.+"/></#if>
    <#if !appCompat && hasViewPager><dependency mavenUrl="com.android.support:support-v13:19.+"/></#if>

    <merge from="AndroidManifest.xml.ftl"
             to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />

    <instantiate from="res/menu/main.xml.ftl"
            to="${escapeXmlAttribute(resOut)}/menu/${menuName}.xml" />

    <merge from="res/values/strings.xml.ftl"
             to="${escapeXmlAttribute(resOut)}/values/strings.xml" />

    <merge from="res/values/dimens.xml.ftl"
             to="${escapeXmlAttribute(resOut)}/values/dimens.xml" />
    <merge from="res/values-w820dp/dimens.xml"
             to="${escapeXmlAttribute(resOut)}/values-w820dp/dimens.xml" />

    <!-- Decide what kind of layout(s) to add -->
    <#if hasViewPager>
        <instantiate from="res/layout/activity_pager.xml.ftl"
                       to="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />

    <#else>
        <instantiate from="res/layout/activity_fragment_container.xml.ftl"
                       to="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />
    </#if>

        <instantiate from="res/layout/fragment_simple.xml.ftl"
                       to="${escapeXmlAttribute(resOut)}/layout/${fragmentLayoutName}.xml" />

    <!-- Decide which activity code to add -->
    <#if features == "tabs" || features == "pager">
        <instantiate from="src/app_package/TabsAndPagerActivity.java.ftl"
                       to="${escapeXmlAttribute(srcOut)}/${activityClass}.java" />

    <#elseif features == "spinner">
        <instantiate from="src/app_package/DropdownActivity.java.ftl"
                       to="${escapeXmlAttribute(srcOut)}/${activityClass}.java" />

    </#if>

    <open file="${escapeXmlAttribute(srcOut)}/${activityClass}.java" />
    <open file="${escapeXmlAttribute(resOut)}/layout/${fragmentLayoutName}.xml" />
</recipe>
