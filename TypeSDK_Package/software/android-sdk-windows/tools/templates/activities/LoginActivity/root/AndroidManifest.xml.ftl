<manifest xmlns:android="http://schemas.android.com/apk/res/android" >
<#if includeGooglePlus>
    <!-- To access Google+ APIs: -->
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- To retrieve OAuth 2.0 tokens or invalidate tokens to disconnect a user. This disconnect
     option is required to comply with the Google+ Sign-In developer policies -->
    <uses-permission android:name="android.permission.USE_CREDENTIALS" />

    <!-- To retrieve the account name (email) as part of sign-in: -->
    <uses-permission android:name="android.permission.GET_ACCOUNTS" /></#if>

    <!-- To auto-complete the email text field in the login form with the user's emails --><#if !includeGooglePlus>
    <uses-permission android:name="android.permission.GET_ACCOUNTS" /></#if>
    <uses-permission android:name="android.permission.READ_PROFILE" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />

    <application>
        <activity android:name=".${activityClass}"
            <#if isNewProject>
            android:label="@string/app_name"
            <#else>
            android:label="@string/title_${simpleName}"
            </#if>
            android:windowSoftInputMode="adjustResize|<#if includeGooglePlus>stateHidden<#else>stateVisible</#if>"
            <#if buildApi gte 16 && parentActivityClass != "">android:parentActivityName="${parentActivityClass}"</#if>>
            <#if parentActivityClass != "">
            <meta-data android:name="android.support.PARENT_ACTIVITY"
                android:value="${parentActivityClass}" />
            </#if>
        </activity>
<#if includeGooglePlus>
        <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />
</#if>
    </application>

</manifest>
