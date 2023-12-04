package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginTabFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordET;
    private Button loginButton;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);


        mAuth = FirebaseAuth.getInstance();

        emailEditText = view.findViewById(R.id.login_email);
        passwordEditText = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        forgotPasswordET=view.findViewById(R.id.forgot_password);

        forgotPasswordET.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        return view;
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter email and password", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getActivity(), HomePage.class);
                                startActivity(intent);
                            }  else {
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                Log.e("LoginActivity", "Authentication failed: " + task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    public ViewPager2 getParentViewPager() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            return mainActivity.getViewPager();
        }
        return null;
    }

}
