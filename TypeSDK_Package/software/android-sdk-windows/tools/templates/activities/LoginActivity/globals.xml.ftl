<?xml version="1.0"?>
<globals>
    <global id="projectOut" value="." />
    <global id="manifestOut" value="${manifestDir}" />
    <global id="srcOut" value="${srcDir}/${slashedPackageName(packageName)}" />
    <global id="resOut" value="${resDir}" />
    <global id="menuName" value="${classToResource(activityClass)}" />
    <global id="simpleName" value="${activityToLayout(activityClass)}" />
    <global id="relativePackage" value="<#if relativePackage?has_content>${relativePackage}<#else>${packageName}</#if>" />
</globals>
