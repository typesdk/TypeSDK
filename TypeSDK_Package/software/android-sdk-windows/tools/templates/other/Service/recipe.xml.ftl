<?xml version="1.0"?>
<recipe>
    <merge from="AndroidManifest.xml.ftl"
             to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />
    <instantiate from="src/app_package/Service.java.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${className}.java" />
    <open file="${escapeXmlAttribute(srcOut)}/${className}.java" />
</recipe>
