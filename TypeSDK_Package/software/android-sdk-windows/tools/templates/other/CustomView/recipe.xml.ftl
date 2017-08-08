<?xml version="1.0"?>
<recipe>
    <merge from="res/values/attrs.xml.ftl"
             to="${escapeXmlAttribute(resOut)}/values/attrs_${view_class}.xml" />
    <instantiate from="res/layout/sample.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/sample_${view_class}.xml" />

    <instantiate from="src/app_package/CustomView.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${viewClass}.java" />

    <open file="${escapeXmlAttribute(srcOut)}/${viewClass}.java" />
    <open file="${escapeXmlAttribute(resOut)}/layout/sample_${view_class}.xml" />
</recipe>
