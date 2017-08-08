<?xml version="1.0"?>
<recipe>

    <#if appCompat><dependency mavenUrl="com.android.support:appcompat-v7:19.+"/></#if>
    <#if !appCompat><dependency mavenUrl="com.android.support:support-v4:19.+"/></#if>

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

    <!-- TODO: switch on Holo Dark v. Holo Light -->
    <copy from="res/drawable-hdpi"
            to="${escapeXmlAttribute(resOut)}/drawable-hdpi" />
    <copy from="res/drawable-mdpi"
            to="${escapeXmlAttribute(resOut)}/drawable-mdpi" />
    <copy from="res/drawable-xhdpi"
            to="${escapeXmlAttribute(resOut)}/drawable-xhdpi" />
    <copy from="res/drawable-xxhdpi"
            to="${escapeXmlAttribute(resOut)}/drawable-xxhdpi" />

    <instantiate from="res/menu/global.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/menu/global.xml" />

    <instantiate from="res/layout/activity_drawer.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />
    <instantiate from="res/layout/fragment_navigation_drawer.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${navigationDrawerLayout}.xml" />

    <instantiate from="res/layout/fragment_simple.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${fragmentLayoutName}.xml" />

    <instantiate from="src/app_package/DrawerActivity.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${activityClass}.java" />
    <instantiate from="src/app_package/NavigationDrawerFragment.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/NavigationDrawerFragment.java" />

    <open file="${escapeXmlAttribute(srcOut)}/${activityClass}.java" />
    <open file="${escapeXmlAttribute(resOut)}/layout/${fragmentLayoutName}.xml" />
</recipe>
