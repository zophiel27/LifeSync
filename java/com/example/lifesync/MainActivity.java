package com.example.lifesync;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lifesync.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    ActivityMainBinding binding;
    int refresh = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new TaskManagerFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.tmItem:
                    replaceFragment(new TaskManagerFragment());
                    refresh=0;
                    break;
                case R.id.btItem:
                    replaceFragment(new BudgetTrackerFragment());
                    refresh=1;
                    break;
                case R.id.jItem:
                    replaceFragment(new JournalFragment());
                    refresh=2;
                    break;
                case R.id.signOut:
                    FirebaseAuth.getInstance().signOut();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());
                    googleSignInClient.signOut().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Redirect to login activity
                            startActivity(new Intent(MainActivity.this,SignIn.class));
                        }
                    });
                    //startActivity(new Intent(MainActivity.this,SignIn.class));
                    break;
            }

            return true;
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity();
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(MainActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000); // resets after 2 seconds
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        FloatingActionButton btn2=findViewById(R.id.refresh);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (refresh){
                    case 0:
                        replaceFragment(new TaskManagerFragment());
                        break;
                    case 1:
                        replaceFragment(new BudgetTrackerFragment());
                        break;
                    case 2:
                        replaceFragment(new JournalFragment());
                        break;
                }
            }
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
        }
    }
}
