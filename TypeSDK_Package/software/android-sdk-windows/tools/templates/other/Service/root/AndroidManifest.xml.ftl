<manifest xmlns:android="http://schemas.android.com/apk/res/android" >

    <application>
        <service android:name="${relativePackage}.${className}"
            android:exported="${isExported?string}"
            android:enabled="${isEnabled?string}" >
        </service>
    </application>

</manifest>
