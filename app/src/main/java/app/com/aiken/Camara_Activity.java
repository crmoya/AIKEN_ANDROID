package app.com.aiken;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import app.com.aiken.clases.AppSingleton;

public class Camara_Activity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    int capturasRealizadas = 0;
    private Uri imageUri;
    private ContentValues values;
    private Bitmap thumbnail;
    String imageurl;
    String rut;

    String url = AppSingleton.url + "cedula.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_);
        rut = getIntent().getExtras().getString("rut");
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            values = new ContentValues();
            imageUri = getContentResolver().insert(
                   MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK){
                if (requestCode == 1){
                    try {
                        Toast.makeText(getApplicationContext(), "Espere mientras se sube la imagen al servidor...", Toast.LENGTH_SHORT).show();
                        thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        String img = imageToSring(thumbnail);
                        save(img, rut, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    (new File(imageurl)).delete();

                }
            }

        }
    }


    public void save(final String img, final String rut,final int numero) {
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String responseString = jsonObject.getString("response");
                                if(responseString.equals("OK")){
                                    imageurl = getRealPathFromURI(imageUri);
                                    (new File(imageurl)).delete();
                                    Intent camara = new Intent(Camara_Activity.this,Camara2_Activity.class);
                                    camara.putExtra("rut", rut);
                                    Toast.makeText(getApplicationContext(), "Fotografía subida con éxito, por favor ahora repita con la cara posterior de la cédula.", Toast.LENGTH_SHORT).show();
                                    startActivity(camara);
                                    finish();
                                }
                                else{
                                    Intent camara = new Intent(Camara_Activity.this,Camara_Activity.class);
                                    camara.putExtra("rut", rut);
                                    Toast.makeText(getApplicationContext(), "Error al intentar subir la fotografía, por favor reintente.", Toast.LENGTH_SHORT).show();
                                    startActivity(camara);
                                    finish();
                                }

                            } catch (JSONException e) {
                                Intent camara = new Intent(Camara_Activity.this,Camara_Activity.class);
                                camara.putExtra("rut", rut);
                                Toast.makeText(getApplicationContext(), "Error al intentar subir la fotografía, por favor reintente.", Toast.LENGTH_SHORT).show();
                                startActivity(camara);
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Intent camara = new Intent(Camara_Activity.this,Camara_Activity.class);
                    camara.putExtra("rut", rut);
                    Toast.makeText(getApplicationContext(), "Error al intentar subir la fotografía, por favor reintente.", Toast.LENGTH_SHORT).show();
                    startActivity(camara);
                    finish();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("rut", rut.toString());
                    params.put("numero", numero + "");
                    params.put("image", img);
                    params.put("secret","CamaraActivity");
                    return params;
                }
            };
            AppSingleton.getInstance(Camara_Activity.this).addToRequestQue(stringRequest);

        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }
    }

    private  String imageToSring(Bitmap bitmap){
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            byte[] imgBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imgBytes,Base64.DEFAULT);
        }catch(Exception ex){
            Log.e("ERROR","ERROR: "+ex.getMessage());
        }
        return null;
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
