<?xml version="1.0"?>
<recipe>

  <instantiate from="src/app_package/interface.aidl.ftl"
                 to="${escapeXmlAttribute(aidlOut)}/${slashedPackageName(packageName)}/${escapeXmlAttribute(interfaceName)}.aidl" />
  <open file="${escapeXmlAttribute(aidlOut)}/${slashedPackageName(packageName)}/${escapeXmlAttribute(interfaceName)}.aidl" />
</recipe>
