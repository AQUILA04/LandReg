package com.lesadrax.registrationclient.from.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;


import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import morpho.morphoKit.api.Matcher;
import morpho.morphoKit.api.Record;

public class Utils {

	 //MorphoKit Matcher
	public static Matcher matcher;
	public static Record record;
	
	public static void showMessage(Context context, String title, String message) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
		dlgAlert.setMessage(message);
		dlgAlert.setTitle(title);
		dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//dismiss the dialog
				}
			});
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}

	public static ByteArrayOutputStream ReadFile(ContentResolver contentResolver, Uri file) throws IOException {

		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios =contentResolver.openInputStream(file);
			//ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1) {
				ous.write(buffer, 0, read);
			}
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
			}

			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}
		return ous;
	}

	public static byte[] convertGrayscaleToRGBA(byte[] pixels) {
		byte[] data = new byte[pixels.length*4];
		for (int i = 0; i < pixels.length; i++) {
			data[i*4] = data[i*4+1] = data[i*4+2] = pixels[i];
			data[i*4+3] = (byte)0xFF; // No Alpha
		}
		return data;
	}

	public static byte[] convertGrayscaleToRGBA(byte[] pixels, int width, int height) {
		byte[] data = new byte[pixels.length*4];
		int cnt = 0;
		for (int i = height-1; i >= 0; i--) {
			for (int j = 0; j < width; j++) {
				data[cnt*4] = data[cnt*4+1] = data[cnt*4+2] = pixels[i*width + j];
				data[cnt*4+3] = (byte)0xFF;
				cnt++;
			}
		}
		return data;
	}

	public static Bitmap convertRAWToBitmap(byte[] pixels, int width, int height, boolean inverse) {
		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		if (inverse) {
			bm.copyPixelsFromBuffer(ByteBuffer.wrap(Utils.convertGrayscaleToRGBA(pixels, width, height)));
		} else {
			bm.copyPixelsFromBuffer(ByteBuffer.wrap(Utils.convertGrayscaleToRGBA(pixels)));
		}
		return bm;

		/*IntBuffer intBuf = ByteBuffer.wrap(pixels).asIntBuffer();
		int[] int_argb_raw_image = new int[intBuf.remaining()];
		intBuf.get(int_argb_raw_image);
		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bm.setPixels(int_argb_raw_image, 0, width, 0, 0, width, height);
		return bm;*/
	}


	public static Drawable convertRAWToDrawable(Resources resources, byte[] pixels, int width, int height, boolean inverse) {
		return new BitmapDrawable(resources, convertRAWToBitmap(pixels, width, height, inverse));
	}

	public static Drawable convertRAWToDrawable(Resources resources, byte[] pixels, int width, int height) {
		return new BitmapDrawable(resources, convertRAWToBitmap(pixels, width, height, false));
	}

	public static String convertBitmapToFile(Bitmap bitmap, String fileName) {
		// Create a directory in external storage
		File storageDir = new File(Environment.getExternalStorageDirectory(), "Fingers");
		if (!storageDir.exists()) {
			storageDir.mkdirs();
		}

		// Create a file inside the directory
		File imageFile = new File(storageDir, fileName + ".png");
		FileOutputStream fileOutputStream = null;

		try {
			// Open a file output stream
			fileOutputStream = new FileOutputStream(imageFile);

			// Compress the bitmap and write it to the file
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

			// Flush and close the output stream
			fileOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return null; // Return null in case of an error
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// Return the absolute path of the file
		return imageFile.getAbsolutePath();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public static int calculateAge(String dateString) {
		try {
			// Définir le format de la date (YYYY-dd-MM)
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM");

			// Convertir la chaîne en LocalDate
			LocalDate birthDate = LocalDate.parse(dateString, formatter);

			// Obtenir la date actuelle
			LocalDate currentDate = LocalDate.now();

			// Calculer la différence en années (âge)
			return Period.between(birthDate, currentDate).getYears();
		} catch (DateTimeParseException e) {
			// Gérer une chaîne de date mal formatée
			System.err.println("Date invalide : " + dateString);
			return -1; // Retourne -1 pour indiquer une erreur
		}
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	public static String getDateFromAge(int age) {
		if (age < 0) {
			throw new IllegalArgumentException("L'âge doit être un entier positif.");
		}

		// Obtenir l'année actuelle
		int anneeActuelle = LocalDate.now().getYear();

		// Calculer l'année de naissance
		int anneeDeNaissance = anneeActuelle - age;

		// Retourner la date au format "YYYY-mm-dd"
		return anneeDeNaissance + "-01-01";
	}

	public static boolean isBase64Regex(String input) {
		return input.matches("^[A-Za-z0-9+/=]+$") && input.length() % 4 == 0;
	}
}