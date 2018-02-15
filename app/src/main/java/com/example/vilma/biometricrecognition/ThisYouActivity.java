package com.example.vilma.biometricrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jclew on 1/31/2018.
 */

public class ThisYouActivity extends BaseActivity{
    ImageView preview;
    Button yes;
    Button no;
    TextView thisYou;
    TextView usernameTxtView;
    String txtFirstname;
    String txtLastname;
    String txtUsername;
    String previewPhotoPath;
    String target;
    String source;
    String username;
    Float result;

    @Override  //calls this method immediately when this activity is called
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thisyou);
        initUI();
    }

    private void initUI() {
        preview = (ImageView)findViewById(R.id.previewImageView);
        yes = findViewById(R.id.btnYes);
        no = findViewById(R.id.btnNo);
        thisYou = findViewById(R.id.txtIsThisYou);
        usernameTxtView = findViewById(R.id.txtUsernameCheck);

        Intent intentExtras = getIntent();
        Bundle extraBundle = intentExtras.getExtras();
        txtFirstname = extraBundle.getString("FirstName");
        txtLastname = extraBundle.getString("LastName");
        txtUsername = extraBundle.getString("Username");
        previewPhotoPath = extraBundle.getString("PhotoPath");
        target = extraBundle.getString("Target");
        source = extraBundle.getString("Source");
        username = extraBundle.getString("Username");
        usernameTxtView.setText(username);
        setPic();

        yes.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //this method does like pretty much all the work....like seriously-_-
                comparePic();
            }
        });

        no.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //this method does like pretty much all the work....like seriously-_-
                Intent login = new Intent(ThisYouActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });


    }

    private void comparePic() {
        ComparePictures j = new ComparePictures(this, source, target, this);
        j.execute();
    }

    void compareFinish(Float result){
        this.result = result;
        if(result != null) {
            if (result > 80) {
                Toast.makeText(this, "You made a match of " + this.result.toString() + "!"
                        , Toast.LENGTH_LONG).show();

                Intent registerIntent = new Intent(ThisYouActivity.this, HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("FirstName", txtFirstname);
                bundle.putString("LastName", txtLastname);
                bundle.putString("Username", txtUsername);
                registerIntent.putExtras(bundle);
                startActivity(registerIntent);
            } else {
                Toast.makeText(this, "Match of only " + this.result.toString(), Toast.LENGTH_LONG)
                        .show();
                Intent failedIntent = new Intent(ThisYouActivity.this, LoginActivity.class);
                startActivity(failedIntent);
            }
        }else{
            Toast.makeText(this, "Picture doesn't match Prime", Toast.LENGTH_LONG).show();
            Intent failedIntent = new Intent(ThisYouActivity.this, LoginActivity.class);
            startActivity(failedIntent);
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 350;
        int targetH = 200;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(previewPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(previewPhotoPath, bmOptions);
        bitmap = RotateBitmap(bitmap,270);
        preview.setImageBitmap(bitmap);
    }


}
