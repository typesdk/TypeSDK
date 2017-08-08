<?xml version="1.0"?>
<recipe>

    <dependency mavenUrl="com.android.support:support-v4:19.+"/>
    <merge from="AndroidManifest.xml.ftl"
             to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />

    <#if expandedStyle == "picture">
    <copy from="res/drawable-nodpi/example_picture_large.png"
            to="${escapeXmlAttribute(resOut)}/drawable-nodpi/example_picture.png" />
    <#else>
    <copy from="res/drawable-nodpi/example_picture_small.png"
            to="${escapeXmlAttribute(resOut)}/drawable-nodpi/example_picture.png" />
    </#if>

    <#if moreActions>
    <copy from="res/drawable-hdpi"
            to="${escapeXmlAttribute(resOut)}/drawable-hdpi" />
    <copy from="res/drawable-mdpi"
            to="${escapeXmlAttribute(resOut)}/drawable-mdpi" />
    <copy from="res/drawable-xhdpi"
            to="${escapeXmlAttribute(resOut)}/drawable-xhdpi" />
    </#if>

    <merge from="res/values/strings.xml.ftl"
             to="${escapeXmlAttribute(resOut)}/values/strings.xml" />

    <instantiate from="src/app_package/NotificationHelper.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${className}.java" />
    <open file="${escapeXmlAttribute(srcOut)}/${className}.java" />
</recipe>
