package com.example.qrrefresh;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String PREFIX = "E0254|";
    private static final String LABEL = "E0254";
    private static final int QR_SIZE = 720;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d H:mm:ss", Locale.US);

    private ImageView qrImageView;
    private TextView codeLabelView;
    private TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrImageView = findViewById(R.id.qr_image);
        codeLabelView = findViewById(R.id.code_label);
        contentView = findViewById(R.id.code_content);
        codeLabelView.setText(LABEL);
        qrImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (qrImageView.getWidth() > 0 && qrImageView.getHeight() > 0) {
            refreshQrCode();
        } else {
            qrImageView.post(this::refreshQrCode);
        }
    }

    private void refreshQrCode() {
        String content = PREFIX + formatter.format(new Date());
        contentView.setText(content);
        int size = Math.max(qrImageView.getWidth(), qrImageView.getHeight());
        if (size <= 0) {
            size = QR_SIZE;
        }

        try {
            qrImageView.setImageBitmap(createQrBitmap(content, size));
        } catch (WriterException exception) {
            qrImageView.setImageDrawable(null);
            contentView.setText(R.string.qr_error);
        }
    }

    private Bitmap createQrBitmap(String content, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MARGIN, 4);

        BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }
}
