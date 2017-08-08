<?xml version="1.0"?>
<recipe>

  <instantiate from="res/values.xml.ftl"
                 to="${escapeXmlAttribute(resOut)}/values/${escapeXmlAttribute(fileName)}.xml" />
  <open file="${escapeXmlAttribute(resOut)}/values/${escapeXmlAttribute(fileName)}.xml" />
</recipe>
