<?xml version="1.0"?>
<recipe>
    <dependency mavenUrl="com.google.android.gms:play-services:4.2.42" />
    <dependency mavenUrl="com.android.support:appcompat-v7:19.+" />

    <merge from="AndroidManifest.xml.ftl"
             to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />

    <merge from="res/values/dimens.xml"
             to="${escapeXmlAttribute(resOut)}/values/dimens.xml" />

    <instantiate from="res/layout/activity_login.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />

    <instantiate from="res/values/strings.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/values/strings_${simpleName}.xml" />

    <instantiate from="src/app_package/LoginActivity.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${activityClass}.java" />

    <#if includeGooglePlus>
        <instantiate from="src/app_package/PlusBaseActivity.java.ftl"
                       to="${escapeXmlAttribute(srcOut)}/PlusBaseActivity.java" />
    </#if>

    <open file="${escapeXmlAttribute(srcOut)}/${activityClass}.java" />

</recipe>
