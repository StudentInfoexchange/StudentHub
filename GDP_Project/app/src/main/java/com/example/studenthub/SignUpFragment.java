package com.example.studenthub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpFragment extends Fragment {

    private EditText sidEditText, emailEditText, passwordEditText;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        //usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        sidEditText=view.findViewById(R.id.signup_sid);
        emailEditText = view.findViewById(R.id.signup_email);
        passwordEditText = view.findViewById(R.id.signup_password);
        signUpButton = view.findViewById(R.id.signup_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        return view;
    }

    private void signUpUser() {
        final String username = sidEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(requireContext(), "Signup successful", Toast.LENGTH_SHORT).show();
                            ViewPager2 viewPager = getParentViewPager();
                            if (viewPager != null) {
                                viewPager.setCurrentItem(0);
                            }

                        } else {
                            Toast.makeText(requireContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private ViewPager2 getParentViewPager() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            return mainActivity.getViewPager();
        }
        return null;
    }
}
