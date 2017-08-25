package android.app.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static android.app.DownloadManager.STATUS_RUNNING;

/**
 * Created by vinee_000 on 23-08-2017.
 */

public class Utils {

    public static String sendRequest(Context context , String name) {

        String query = name;
        final String[] imageUrl = new String[1];

        query = query.toLowerCase().replace("trip", "tour").toLowerCase().replace("holiday", "tour");
        if (!query.contains("tour")) {
            query = query + " tour";
        }


        String queryText = null;
        try {
            queryText = URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //    String url = "https://www.google.co.in/search?q="  "&source=lnms&tbm=isch";
        String url = "https://www.google.com/search?site=imghp&tbm=isch&source=hp&q="+ queryText +"&gws_rd=cr";


        // Volley
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Document doc = Jsoup.parse(response);
                            if (doc != null) {
                                Elements elems = doc.select("div.rg_meta");
                                if (!(elems == null || elems.isEmpty())) {
                                    Iterator it = elems.iterator();
                                    while (it.hasNext()) {
                                        JSONObject jSONObject = new JSONObject(((Element) it.next()).text());
                                        if (jSONObject.getInt("ow") > 500) {
                                            imageUrl[0] = jSONObject.getString("ou");
                                            Log.d("imageUrl",imageUrl[0]);
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


        return imageUrl[0];
    }



    public static String getImageUrl(String name) {// can only grab first 100 results
        String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";

        String query = name;
        String imageUrl=null;
        query = query.toLowerCase().replace("trip", "tour").toLowerCase().replace("holiday", "tour");
        if (!query.contains("tour")) {
            query = query + " tour";
        }
        try {
            String queryText = URLEncoder.encode(query,"UTF-8");
           String url = "https://www.google.co.in/search?q=" + queryText + "&source=lnms&tbm=isch";
         //   String url = "https://www.google.com/search?site=imghp&tbm=isch&source=hp&q="+ queryText +"&gws_rd=cr";

            Document doc = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .referrer("http://www.google.com/")
                    .get();

            if (doc != null) {
                Elements elems = doc.select("div.rg_meta");
                if (!(elems == null || elems.isEmpty())) {
                    Iterator it = elems.iterator();
                    while (it.hasNext()) {
                        try {
                            JSONObject jSONObject = new JSONObject(((Element) it.next()).text());
                            if (jSONObject.getInt("ow") > 500) {
                                imageUrl = jSONObject.getString("ou");
                                return imageUrl;
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e22) {
            e22.printStackTrace();
        }

        return imageUrl;
    }

}
