<?xml version="1.0"?>
<globals>
    <global id="manifestOut" value="${manifestDir}" />
    <global id="appCompat" type="boolean" value="${(minApiLevel lt 14)?string}" />
    <!-- e.g. getSupportActionBar vs. getActionBar -->
    <global id="Support" value="${(minApiLevel lt 14)?string('Support','')}" />
    <global id="ActionNamespace" value="${(minApiLevel lt 14)?string('app','android')}" />
    <global id="srcOut" value="${srcDir}/${slashedPackageName(packageName)}" />
    <global id="resOut" value="${resDir}" />
    <global id="menuName" value="${classToResource(activityClass)}" />
    <global id="relativePackage" value="<#if relativePackage?has_content>${relativePackage}<#else>${packageName}</#if>" />
</globals>
