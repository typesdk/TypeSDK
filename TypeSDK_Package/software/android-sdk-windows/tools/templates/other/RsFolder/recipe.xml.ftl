<?xml version="1.0"?>
<recipe>

  <#if remapFolder>
    <mkdir at="${escapeXmlAttribute(projectOut)}/${escapeXmlAttribute(newLocation)}" />
    <merge from="build.gradle.ftl"
             to="${escapeXmlAttribute(projectOut)}/build.gradle" />
  <#else>
    <mkdir at="${escapeXmlAttribute(manifestOut)}/rs/" />
  </#if>

</recipe>
