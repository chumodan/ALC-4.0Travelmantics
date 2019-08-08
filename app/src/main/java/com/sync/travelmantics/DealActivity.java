package com.sync.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText mTextTitle;
    private EditText mTextPrice;
    private ImageView imageView;

    private static final int PICTURE_RESULT=42;
    private EditText mTextDescription;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        mTextTitle = findViewById(R.id.txtTitle);
        mTextPrice = findViewById(R.id.txtPrice);
        mTextDescription = findViewById(R.id.txtDescription);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal==null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        showImage(deal.getImgURL());
        mTextTitle.setText(deal.getTitle());
        mTextDescription.setText(deal.getDescription());
        mTextPrice.setText(deal.getPrice());

        Button btnSave = findViewById(R.id.btnSaveImage);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadDataIntent = new Intent(Intent.ACTION_GET_CONTENT);
                uploadDataIntent.setType("image/jpeg");
                uploadDataIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(uploadDataIntent,
                        "Insert picture"), PICTURE_RESULT);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();

                    while(!uri.isComplete());
                    Uri url = uri.getResult();

                    String downloadURL = url.toString();
                    String pictureName = taskSnapshot.getStorage().getPath();
                    deal.setImgURL(downloadURL);
                    deal.setImageName(pictureName);
                    Log.d("Url: ", downloadURL);
                    Log.d("Name", pictureName);
                    showImage(downloadURL);
                }
            });

        }

//
//        if (requestCode == PICTURE_RESULT && resultCode ==RESULT_OK){
//
//            Uri imageUri = data.getData();
//
//            final StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
//
//           ref.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return ref.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                        String downloadURL = downloadUri.toString();
//
//                        String pictureName = task.getStorage().getPath();
//                        deal.setImgURL(downloadURL);
//                        deal.setImageName(pictureName);
//                        Log.d("Url: ", downloadURL);
//                        Log.d("Name", pictureName);
//                        showImage(downloadURL);
//
//
//
//
//                        FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUri.toString());
//                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
//                    } else {
//                        Toast.makeText(MainActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_save){

            saveDeal();

            Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
            clean();
            backToList();

            return true;
        }

        else if(id==R.id.action_delete){

            deleteDeal();
            Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
            backToList();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        if(FirebaseUtil.isAdmin==true){
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(true);
            enableEditText(true);
            findViewById(R.id.btnSaveImage).setEnabled(true);
        }

        else {

            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
            enableEditText(false);
            findViewById(R.id.btnSaveImage).setEnabled(false);
        }

        return true;
    }


    private void saveDeal() {
        deal.setTitle(mTextTitle.getText().toString());
        deal.setDescription(mTextDescription.getText().toString());
        deal.setPrice(mTextPrice.getText().toString());
        if(deal.getId()==null) {
            mDatabaseReference.push().setValue(deal);
        }
        else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }
    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();
        Log.d("image name", deal.getImageName());
        if(deal.getImageName() != null && deal.getImageName().isEmpty() == false) {
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image Successfully Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }

    }
    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clean() {

        mTextTitle.setText("");
        mTextDescription.setText("");
        mTextPrice.setText("");
        mTextTitle.requestFocus();


    }

    private void enableEditText(boolean isEnabled){
        mTextTitle.setEnabled(isEnabled);
        mTextDescription.setEnabled(isEnabled);
        mTextPrice.setEnabled(isEnabled);

    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }

}
