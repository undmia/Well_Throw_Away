package com.unity.mynativeapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unity.mynativeapp.activity.MainActivity;
import com.unity.mynativeapp.R;

public class FragmentJoinModify extends Fragment {

    private FirebaseUser firebaseUser;
    private static final String TAG = "FragmentJoinModify";
    EditText userPw, userPwChecks, userEmail;
    Button user_modify;
    String pw, pw_check, email, set_email;
    TextView error_email, error_pw, error_pwcheck;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_modify, container, false);

        userEmail = view.findViewById(R.id.userEmail);
        userPw = view.findViewById(R.id.userPw);
        userPwChecks = view.findViewById(R.id.userPwCheck);

        firebaseUser =  FirebaseAuth.getInstance().getCurrentUser();
        email = firebaseUser.getEmail();
        userEmail.setText(email);
        error_email = view.findViewById(R.id.error_email);
        error_pw = view.findViewById(R.id.error_pw);
        error_pwcheck = view.findViewById(R.id.error_pwcheck);

        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()){
                    error_email.setText("이메일 형식으로 입력해주세요.");
                }
                else{
                    error_email.setText("");
                }
            }
        });
        userPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().getBytes().length < 6){
                    error_pw.setText("6자리이상 입력해주세요.");
                }
                else{
                    error_pw.setText("");
                }
            }
        });

        userPwChecks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!userPw.getText().toString().equals(userPwChecks.getText().toString())) {
                    error_pwcheck.setText("같은 비밀번호를 입력해주세요.");
                }
                else{
                    error_pwcheck.setText("");
                }
            }
        });
        //회원정보 수정 버튼
        user_modify = view.findViewById(R.id.user_modify);
        user_modify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // 비밀번호 입력이 없으면
                if (userPw.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 입력값 저장
                set_email = userEmail.getText().toString().trim();
                pw = userPw.getText().toString().trim();
                pw_check = userPwChecks.getText().toString().trim();

                // 두 비밀번호 입력 값이 같고
                if(pw.equals(pw_check)){

                    // 비밀번호가 6자 이상일 때
                    if(pw.length()>5){
                        Log.d(TAG, "등록 버튼 : " + set_email + " , " + pw);

                        // 이메일 변경이 있을 경우
                        if(!(email.equals(set_email))){
                            // 파이어베이스 데이터베이스에서 사용자 이메일 재설정
                            firebaseUser.updateEmail(set_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(), "이메일 변경", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        // 파이어베이스 데이터베이스에 사용자 비밀번호 변경
                        firebaseUser.updatePassword(pw).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(getActivity(), "비밀번호 변경", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(), "비밀번호를 6자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else{   // 비밀번호 오류 시
                    Toast.makeText(getActivity(), "비밀번호가 틀렸습니다.\n다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        return view;
    }
}


