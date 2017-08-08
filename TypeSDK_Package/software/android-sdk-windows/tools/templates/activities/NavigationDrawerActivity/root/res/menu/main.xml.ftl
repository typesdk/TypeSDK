<menu xmlns:android="http://schemas.android.com/apk/res/android"<#if appCompat>
    xmlns:app="http://schemas.android.com/apk/res-auto"</#if>
    xmlns:tools="http://schemas.android.com/tools"
    tools:context="${relativePackage}.${activityClass}" >
    <item android:id="@+id/action_example"
        android:title="@string/action_example"
        ${ActionNamespace}:showAsAction="withText|ifRoom" />
    <item android:id="@+id/action_settings"
        android:title="@string/action_settings"
        android:orderInCategory="100"
        ${ActionNamespace}:showAsAction="never" />
</menu>
