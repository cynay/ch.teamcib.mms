package ch.teamcib.mms.gui;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import ch.teamcib.mms.*;

public class ShowHelp extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("show help", "onCreate(): entered...");
		setContentView(R.layout.show_help);

		final WebView view = (WebView) findViewById(R.id.webview);
		view.getSettings().setJavaScriptEnabled(true);
		initialiseWebViewEasy(view, this);
		view.bringToFront();
	}

	/**
	 * Initialize WebKit with a HTML-Site out of the res-folder.
	 * 
	 * @param view
	 *            WebView for presentation of Website
	 * @param context
	 *            context of the application
	 */
	private void initialiseWebViewEasy(final WebView view,
			final Context context) {
		final String mimetype = "text/html";
		final String encoding = "UTF-8";
		String htmldata;

		final int contextMenueId = R.raw.help;
		final InputStream is = context.getResources().openRawResource(
				contextMenueId);

		try {
			if (is != null && is.available() > 0) {
				final byte[] bytes = new byte[is.available()];
				is.read(bytes);
				htmldata = new String(bytes);
				view.loadDataWithBaseURL(null, htmldata, mimetype, encoding,
						null);
			}
		} catch (IOException e) {
		}
	}

}
