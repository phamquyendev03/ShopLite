package io.ionic.starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.getcapacitor.BridgeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BridgeActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    myRef.setValue("Hello, quyen!");

    myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        String value = snapshot.getValue(String.class);
        Toast.makeText(getBaseContext(), "Value is: " + value, Toast.LENGTH_LONG).show();
        Log.d("MainActivity", "Value is: " + value);
      }

      @Override
      public void onCancelled(DatabaseError error) {
        Toast.makeText(getBaseContext(), "Failed to read value.", Toast.LENGTH_LONG ).show();
        Log.w("MainActivity", "Failed to read value.", error.toException());
      }
    });
  }
}
