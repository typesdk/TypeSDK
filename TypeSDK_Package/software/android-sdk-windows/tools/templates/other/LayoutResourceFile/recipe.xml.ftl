<?xml version="1.0"?>
<recipe>

  <instantiate from="res/layout.xml.ftl"
                 to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(layoutName)}.xml" />
  <open file="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(layoutName)}.xml" />
</recipe>
