package com.netwokz.mystaticip;


import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private CancellationSignal cancellationSignal = null;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Boolean isAuthenticated = false;

    String authenticationKey = "userIsAuthenticated";
    String themeKey = "theme";

    String[] mType = {"Server", "Desktop", "Laptop", "Camera", "Phone", "RaspberryPi", "AP", "TV", "Console", "IOT"};

    SharedPreferences prefs;
    SharedPreferences.Editor edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        edit = prefs.edit();
        getSystemTheme();
        String theme = prefs.getString(themeKey, "MODE_NIGHT_FOLLOW_SYSTEM");
        isAuthenticated = prefs.getBoolean(authenticationKey, false);
        changeTheme(theme);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                edit.putBoolean(authenticationKey, true);
                edit.apply();
                Intent intent = new Intent(MainActivity.this, MainListView.class);
                startActivity(intent);
                finish();
//                switchFragment(SuccessFragment.class);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        if (checkBiometricSupport()) {
            runBiometricPrompt();
        }

    }

    private List<StaticIpRecord> getStaticIpList() {
        List<StaticIpRecord> mList = StaticIpRecord.listAll(StaticIpRecord.class);
        return mList;
    }

    private void getSystemTheme() {
        int temp = AppCompatDelegate.getDefaultNightMode();
        switch (temp) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                edit.putString("theme", "MODE_NIGHT_YES");
                edit.apply();
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                edit.putString("theme", "MODE_NIGHT_NO");
                edit.apply();
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                edit.putString("theme", "MODE_NIGHT_FOLLOW_SYSTEM");
                edit.apply();
                break;
        }
    }

    public void changeTheme(String theme) {
        switch (theme) {
            case "MODE_NIGHT_YES":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "MODE_NIGHT_NO":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "MODE_NIGHT_FOLLOW_SYSTEM":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void switchFragment(Class fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setReorderingAllowed(true).replace(R.id.frame, fragment, null).commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
////                adapter.notifyDataSetChanged();
//                break;
//            case R.id.action_settings:
//                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intent);
////                switchFragment(SettingsFragment.class);
//                break;
//            case R.id.action_add_generated_car:
////                data.add(generateRandomEntry());
////                adapter.notifyDataSetChanged();
//                break;
//            case R.id.action_clear_db:
//                StaticIpRecord.deleteAll(StaticIpRecord.class);
////                data.clear();
////                adapter.notifyDataSetChanged();
//            default:
//                break;
//        }
//        return true;
//    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        if (!isAuthenticated) {
//            if (checkBiometricSupport()) {
//                runBiometricPrompt();
//            }
//        } else {
//            Intent intent = new Intent(this, MainListView.class);
//            startActivity(intent);
//        }
    }

    public void runBiometricPrompt() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign In")
                .setNegativeButtonText("Cancel")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
//        Button biometricLoginButton = findViewById(R.id.biometric_login);
//        biometricLoginButton.setOnClickListener(view -> {
        biometricPrompt.authenticate(promptInfo);
//        });
    }

    private Boolean checkBiometricSupport() {
        boolean isSuccessful;
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                isSuccessful = true;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                isSuccessful = false;
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                isSuccessful = false;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
//                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
//                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
//                startActivityForResult(enrollIntent, REQUEST_CODE);
                isSuccessful = false;
                break;
            default:
                isSuccessful = false;
        }
        return isSuccessful;
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(authenticationKey)) {
            isAuthenticated = sharedPreferences.getBoolean(authenticationKey, false);
        }

        if (key.equals(themeKey)) {
            changeTheme(sharedPreferences.getString(themeKey, "MODE_NIGHT_FOLLOW_SYSTEM"));
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        edit.putBoolean("userIsAuthenticated", false);
//        edit.apply();
//    }
}