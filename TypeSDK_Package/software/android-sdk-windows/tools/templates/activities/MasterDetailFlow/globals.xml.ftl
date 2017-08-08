<?xml version="1.0"?>
<globals>
    <global id="appCompat" type="boolean" value="${(minApiLevel lt 14)?string}" />
    <global id="Support" value="${(minApiLevel lt 14)?string('Support','')}" />
    <global id="projectOut" value="." />
    <global id="manifestOut" value="${manifestDir}" />
    <global id="srcOut" value="${srcDir}/${slashedPackageName(packageName)}" />
    <global id="resOut" value="${resDir}" />
    <global id="CollectionName" value="${extractLetters(objectKind)}List" />
    <global id="collection_name" value="${extractLetters(objectKind?lower_case)}_list" />
    <global id="DetailName" value="${extractLetters(objectKind)}Detail" />
    <global id="detail_name" value="${extractLetters(objectKind?lower_case)}_detail" />
    <global id="relativePackage" value="<#if relativePackage?has_content>${relativePackage}<#else>${packageName}</#if>" />
</globals>
