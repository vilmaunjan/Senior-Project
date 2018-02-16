package com.example.vilma.biometricrecognition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends BaseActivity {

    String previewPhotoPath;
    ImageView preview;
    TextView similarity;
    Float mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Use frame layout instead of setContentView to include the menu and toolbar
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_account, contentFrameLayout);

        initUI();
    }

    private void initUI() {
        similarity = (TextView) findViewById(R.id.lblSimilarity);
        preview = (ImageView)findViewById(R.id.previewImageView);

        SharedPreferences prefs = this.getSharedPreferences("MyPref",MODE_PRIVATE);
        mResult = prefs.getFloat("Similarity",-1);
        previewPhotoPath = prefs.getString("Imagepath",null);

        similarity.setText("Similarity of: "+mResult+"% ");
        setPic();

    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 210;
        int targetH = 320;

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

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
