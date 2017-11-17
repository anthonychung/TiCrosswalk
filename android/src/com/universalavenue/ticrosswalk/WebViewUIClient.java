/**
	* Author: Anthony Chung
    * Implement alert and dialog support for crosswalk.
    * Implement override back key behaviour
	*/

package com.universalavenue.ticrosswalk;

import java.util.HashMap;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.TiC;

import android.app.Activity;

import org.xwalk.core.XWalkView;

import org.json.JSONArray; 
import org.json.JSONException; 
 
//import android.annotation.TargetApi; 
import android.app.AlertDialog; 
import android.content.Context; 
import android.content.DialogInterface; 
import android.content.Intent; 
import android.net.Uri; 
import android.view.Gravity; 
import android.view.KeyEvent; 
import android.view.View; 
import android.view.ViewGroup.LayoutParams; 
import android.webkit.ValueCallback; 
import android.widget.EditText; 
import android.widget.LinearLayout; 
import android.widget.ProgressBar; 
import android.widget.RelativeLayout; 

import org.xwalk.core.XWalkJavascriptResult; 
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class WebViewUIClient extends XWalkUIClient
{
    private WebViewProxy myproxy;
		
	private static final String LCAT = "WebViewUIClient";

	public static final int FILECHOOSER_RESULTCODE = 5173; 
    
    // File Chooser 
    public ValueCallback<Uri> mUploadMessage; 
 
    boolean isCurrentlyLoading; 
    private boolean doClearHistory = false; 

	WebViewUIClient(XWalkView view,WebViewProxy proxy) {
		super(view);
        myproxy = proxy;
	}

    
    @Override 
    public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url, 
            String message, String defaultValue, XWalkJavascriptResult result) { 
        switch(type) { 
            case JAVASCRIPT_ALERT: 
                return onJsAlert(view, url, message, result); 
            case JAVASCRIPT_CONFIRM: 
                return onJsConfirm(view, url, message, result); 
            case JAVASCRIPT_PROMPT: 
                return onJsPrompt(view, url, message, defaultValue, result); 
            case JAVASCRIPT_BEFOREUNLOAD: 
                // Reuse onJsConfirm to show the dialog. 
                return onJsConfirm(view, url, message, result); 
            default: 
                break; 
        } 
        assert(false); 
        return false; 
    } 

    /**
     * Tell the client to display a javascript alert dialog. 
     * 
     * @param view 
     * @param url 
     * @param message 
     * @param result 
     */ 
    private boolean onJsAlert(XWalkView view, String url, String message, 
            final XWalkJavascriptResult result) { 
        AlertDialog.Builder dlg = new AlertDialog.Builder(myproxy.getActivity()); 
        dlg.setMessage(message); 
        dlg.setTitle("Alert"); 
        //Don't let alerts break the back button 
        dlg.setCancelable(true); 
        dlg.setPositiveButton(android.R.string.ok, 
                new AlertDialog.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int which) { 
                        result.confirm(); 
                    } 
                }); 
        dlg.setOnCancelListener( 
                new DialogInterface.OnCancelListener() { 
                    public void onCancel(DialogInterface dialog) { 
                        result.cancel(); 
                    } 
                }); 
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() { 
            //DO NOTHING 
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) { 
                if (keyCode == KeyEvent.KEYCODE_BACK) 
                { 
                    result.confirm(); 
                    return false; 
                } 
                else 
                    return true; 
            } 
        }); 
        dlg.show(); 
        return true; 
    } 
 
    /**
     * Tell the client to display a confirm dialog to the user. 
     * 
     * @param view 
     * @param url 
     * @param message 
     * @param result 
     */ 
    private boolean onJsConfirm(XWalkView view, String url, String message, 
            final XWalkJavascriptResult result) { 
        AlertDialog.Builder dlg = new AlertDialog.Builder(myproxy.getActivity()); 
        dlg.setMessage(message); 
        dlg.setTitle("Confirm"); 
        dlg.setCancelable(true); 
        dlg.setPositiveButton(android.R.string.ok, 
                new DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int which) { 
                        result.confirm(); 
                    } 
                }); 
        dlg.setNegativeButton(android.R.string.cancel, 
                new DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int which) { 
                        result.cancel(); 
                    } 
                }); 
        dlg.setOnCancelListener( 
                new DialogInterface.OnCancelListener() { 
                    public void onCancel(DialogInterface dialog) { 
                        result.cancel(); 
                    } 
                }); 
        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() { 
            //DO NOTHING 
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) { 
                if (keyCode == KeyEvent.KEYCODE_BACK) 
                { 
                    result.cancel(); 
                    return false; 
                } 
                else 
                    return true; 
            } 
        }); 
        dlg.show(); 
        return true; 
    } 
 
    /**
     * Tell the client to display a prompt dialog to the user. 
     * If the client returns true, WebView will assume that the client will 
     * handle the prompt dialog and call the appropriate JsPromptResult method. 
     * 
     * Since we are hacking prompts for our own purposes, we should not be using them for 
     * this purpose, perhaps we should hack console.log to do this instead! 
     * 
     * @param view 
     * @param url 
     * @param message 
     * @param defaultValue 
     * @param result 
     */ 
    private boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, 
            XWalkJavascriptResult result) { 
 
        // Security check to make sure any requests are coming from the page initially 
        // loaded in webview and not another loaded in an iframe. 
        boolean reqOk = false; 
        if (url.startsWith("file://") ) { 
        	// || Config.isUrlWhiteListed(url)
            reqOk = true; 
        } 
 
      
		final XWalkJavascriptResult res = result; 
        AlertDialog.Builder dlg = new AlertDialog.Builder(myproxy.getActivity()); 
        dlg.setMessage(message); 
        final EditText input = new EditText(myproxy.getActivity()); 
        if (defaultValue != null) { 
            input.setText(defaultValue); 
        } 
        dlg.setView(input); 
        dlg.setCancelable(false); 
        dlg.setPositiveButton(android.R.string.ok, 
                new DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int which) { 
                        String usertext = input.getText().toString(); 
                        res.confirmWithResult(usertext); 
                    } 
                }); 
        dlg.setNegativeButton(android.R.string.cancel, 
                new DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog, int which) { 
                        res.cancel(); 
                    } 
                }); 
        dlg.show(); 
        return true; 
    } 
 

    @Override 
    public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadMsg, String acceptType, 
            String capture) { 
        this.openFileChooser(uploadMsg, "*/*"); 
    } 
 
    public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType ) { 
        this.openFileChooser(uploadMsg, acceptType, null); 
    } 
     
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) 
    { 
        mUploadMessage = uploadMsg; 
        Intent i = new Intent(Intent.ACTION_GET_CONTENT); 
        i.addCategory(Intent.CATEGORY_OPENABLE); 
        i.setType("*/*"); 
        myproxy.getActivity().startActivityForResult(Intent.createChooser(i, "File Browser"), 
                FILECHOOSER_RESULTCODE); 
    } 
     
    public ValueCallback<Uri> getValueCallback() { 
        return this.mUploadMessage; 
    } 

    @Override
    public boolean shouldOverrideKeyEvent(XWalkView view,  android.view.KeyEvent event){
        int keycode=event.getKeyCode();
        if(myproxy.getOverrideBackKey() && keycode==KeyEvent.KEYCODE_BACK){
            myproxy.getActivity().finish();
            return true;
        }
        return false;
    }

}
