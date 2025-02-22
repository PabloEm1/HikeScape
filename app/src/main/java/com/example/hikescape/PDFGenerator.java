package com.example.hikescape;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFGenerator {

    public static void createPdf(Post post, Context context) {
        // Crear un nuevo documento PDF
        PdfDocument document = new PdfDocument();

        // Obtener detalles del post
        String postName = post.getPostName();
        String postDescription = post.getPostDescription();
        String username = post.getUserName();  // Obtener el nombre de usuario

        // Crear una página con un tamaño de 600x800 píxeles
        PageInfo pageInfo = new PageInfo.Builder(600, 800, 1).create();

        // Comenzar una nueva página
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);

        // Dibujar texto en el PDF
        int yPosition = 25;
        canvas.drawText("Usuario: " + username, 10, yPosition, paint);  // Agregar nombre de usuario
        yPosition += 20;
        canvas.drawText("Nombre: " + postName, 10, yPosition, paint);
        yPosition += 20;
        canvas.drawText("Descripción: " + postDescription, 10, yPosition, paint);
        yPosition += 40;

        // Mostrar el número de likes
        canvas.drawText("Likes: " + post.getLikes(), 10, yPosition, paint);

        // Finalizar la página
        document.finishPage(page);

        // Guardar el archivo en el almacenamiento interno
        File file = new File(context.getFilesDir(), post.getPostName() + ".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Log.d("PDF_GENERATOR", "PDF Guardado: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Cerrar el documento
        document.close();
    }
    public static void openPdf(Context context, String postName) {
        // Ruta del archivo PDF guardado
        File file = new File(context.getFilesDir(), postName + ".pdf");

        // Usar FileProvider para acceder al archivo
        Uri pdfUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        // Crear un Intent para abrir el archivo con una aplicación compatible
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  // Permitir acceso al archivo

        // Comprobar si hay una aplicación que pueda abrir el archivo
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);  // Iniciar el Intent
        } else {
            Toast.makeText(context, "No se puede abrir el archivo PDF", Toast.LENGTH_SHORT).show();
        }
    }

}
