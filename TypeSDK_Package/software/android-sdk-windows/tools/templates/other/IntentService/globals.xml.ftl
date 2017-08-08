<?xml version="1.0"?>
<globals>
    <global id="srcOut" value="${srcDir}/${slashedPackageName(packageName)}" />
    <global id="manifestOut" value="${manifestDir}" />
    <global id="relativePackage" value="<#if relativePackage?has_content>${relativePackage}<#else>${packageName}</#if>" />
</globals>
