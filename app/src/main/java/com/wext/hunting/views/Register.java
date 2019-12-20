package com.wext.hunting.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wext.hunting.MainActivity;
import com.wext.hunting.R;
import com.wext.hunting.models.User;

public class Register extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private User user = new User("","","");
    private ClickRegistro clickRegistro;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public class ClickRegistro{

        private EditText email, password, cpassword, user;



        private ClickRegistro(EditText email, EditText password,EditText cpassword, EditText user){
            this.email = email;
            this.password = password;
            this.cpassword = cpassword;
            this.user = user;
        }

        public void regisIn(View view){
            final String correo = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String cpass = cpassword.getText().toString().trim();
            final String usuario = user.getText().toString().trim();

            if (TextUtils.isEmpty(correo)){
                Toast.makeText(Register.this, "Introduce un email",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                Toast.makeText(Register.this, "Introduce un email Valido",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else if (pass.length() <= 5){
                Toast.makeText(Register.this, "El password debe de tener al menos 6 caracteres",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!pass.equals(cpass)){
                Toast.makeText(Register.this, "El password no coincide",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                progressDialog.setTitle("Registrando usuario");
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                //Escritura a DataBase
                mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios");

                firebaseAuth.createUserWithEmailAndPassword(correo, pass)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    mDatabase.child(user.getUid()).child("user").setValue(usuario);
                                    mDatabase.child(user.getUid()).child("mail").setValue(correo);

                                    updateUI(user);
                                    progressDialog.dismiss();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressDialog.dismiss();
                                    Toast.makeText(Register.this, "Error de Comunicacion",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });


            }


        }
    }

    private void updateUI (FirebaseUser user){

        if(user != null){
            Toast.makeText(Register.this, "Usuario Registrado",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else {

        }
    }
}
