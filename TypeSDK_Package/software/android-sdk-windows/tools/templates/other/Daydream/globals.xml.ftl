<?xml version="1.0"?>
<globals>
    <global id="manifestOut" value="${manifestDir}" />
    <global id="resOut" value="${resDir}" />
    <global id="srcOut" value="${srcDir}/${slashedPackageName(packageName)}" />
    <global id="class_name" value="${classToResource(className)}" />
    <global id="info_name" value="${classToResource(className)}_info" />
    <global id="settingsClassName"  value="${className}SettingsActivity" />
    <global id="prefs_name" value="${classToResource(className)}_prefs" />
    <global id="relativePackage" value="<#if relativePackage?has_content>${relativePackage}<#else>${packageName}</#if>" />
</globals>
