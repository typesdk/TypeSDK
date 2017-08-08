<manifest xmlns:android="http://schemas.android.com/apk/res/android" >

    <application>
        <receiver android:name="${relativePackage}.${className}"
            android:exported="${isExported?string}"
            android:enabled="${isEnabled?string}" >
        </receiver>
    </application>

</manifest>
