package app.com.aiken;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class LectorPDF_Activity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Button btnScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_pdf_);

        // Metodo para abrir la camara y escanear pdf417
        mScannerView = new ZXingScannerView(LectorPDF_Activity.this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(LectorPDF_Activity.this);
        mScannerView.startCamera();

    }

    @Override
    public void handleResult(Result result) {
        //Log.v("HandleResult",result.getText());
        //dialogo(result.getText());
        Intent firma = new Intent(LectorPDF_Activity.this,Firma_Activity.class);
        firma.putExtra("rut", result.getText());
        startActivity(firma);
        finish();

    }

};
