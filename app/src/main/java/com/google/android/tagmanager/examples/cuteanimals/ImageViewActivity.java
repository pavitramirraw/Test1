package com.google.android.tagmanager.examples.cuteanimals;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Activity to view an image.
 * <p/>
 * This activity is invoked by {@link CategoryViewActivity} which is expected to pass in
 * the image file name and the back button name.
 */
public class ImageViewActivity extends Activity {
    // The key of the back button name to be passed in.
    static final String BACK_BUTTON_NAME_KEY = "back_button_name";
    // The key of the image file name to be passed in.
    static final String IMAGE_NAME_KEY = "image_name";
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        ContainerHolderSingleton.getContainerHolder().refresh();
        imageName = extras.getString(IMAGE_NAME_KEY);

        // Modify the text for the backToCategory button.
        Button button = (Button) findViewById(R.id.back_to_category);
        button.setText(" << " + extras.getString(BACK_BUTTON_NAME_KEY));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Back to previous activity.
                finish();
            }
        });

        // Set the text of the title.
        TextView titleView = (TextView) findViewById(R.id.image_view_title);
        titleView.setText(imageName);

        // Draw the image.
        int imageId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        ImageView imageView = (ImageView) findViewById(R.id.animal_image);
        imageView.setImageDrawable(getResources().getDrawable(imageId));
        imageView.setContentDescription(imageName);


        if (TagManager.getInstance(this).getDataLayer().get(IMAGE_NAME_KEY) != null) {
            try {
                JSONObject jsonObj = new JSONObject(TagManager.getInstance(this).getDataLayer().get(IMAGE_NAME_KEY).toString());
                Toast.makeText(this, "GET" + jsonObj.toString(), Toast.LENGTH_LONG).show();
                JSONArray array = jsonObj.getJSONArray("Array");
                JSONObject innerObj = new JSONObject();
                int i = 0;
                for (i = 0; i < array.length(); i++) {
                    innerObj = new JSONObject(array.get(i).toString());
                    if (innerObj.getString("name").trim().equalsIgnoreCase(imageName)) {
                        //update only count and time
                        innerObj.put("count", innerObj.getInt("count") + 1);
                        innerObj.put("time", System.currentTimeMillis());
                        array = Utils.removeJsonObjectAtJsonArrayIndex(array, i);
                        array.put(innerObj);
                        break;
                    }

                    if ((System.currentTimeMillis() - innerObj.getLong("time")) > 60000) {
                        //Delete if lastClick time is greater than 1 minute
                        array = Utils.removeJsonObjectAtJsonArrayIndex(array, i);
                        i--;
                    }
                }
                if (i >= array.length()) {
                    //add a new obj in the jsonArray
                    innerObj.put("name", imageName);
                    innerObj.put("count", 1);
                    innerObj.put("time", System.currentTimeMillis());
                    array.put(innerObj);
                }

                jsonObj.put("Array", array);
                Toast.makeText(this, jsonObj.toString(), Toast.LENGTH_LONG).show();
                Log.v("JSON", "JSON: " + jsonObj.toString());
                TagManager.getInstance(this).getDataLayer().push(DataLayer.mapOf(IMAGE_NAME_KEY, jsonObj.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObj = new JSONObject();
                JSONArray array = new JSONArray();
                JSONObject innerObj = new JSONObject();
                innerObj.put("name", imageName);
                innerObj.put("count", 1);
                innerObj.put("time", System.currentTimeMillis());
                array.put(innerObj);
                jsonObj.put("Array", array);

                Toast.makeText(this, jsonObj.toString(), Toast.LENGTH_LONG).show();
                Log.v("JSON", "JSON: " + jsonObj.toString());
                TagManager.getInstance(this).getDataLayer().push(DataLayer.mapOf(IMAGE_NAME_KEY, jsonObj.toString()));
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        ContainerHolderSingleton.getContainerHolder().refresh();

        if(TagManager.getInstance(this).getDataLayer().get(IMAGE_NAME_KEY)!=null && TagManager.getInstance(this).getDataLayer().get("hello")!=null) {
            Toast.makeText(this,"ImageName1: " + TagManager.getInstance(this).getDataLayer().get(IMAGE_NAME_KEY)+TagManager.getInstance(this).getDataLayer().get("hello"),Toast.LENGTH_SHORT).show();
            Log.v("ImageName", "ImageName1: " + TagManager.getInstance(this).getDataLayer().get(IMAGE_NAME_KEY));
            TagManager.getInstance(this).getDataLayer().push(DataLayer.mapOf(IMAGE_NAME_KEY, Utils.getDeviceId(this), "hello", Integer.parseInt(TagManager.getInstance(this).getDataLayer().get("hello").toString())+1));
        }
        else {
            // Put the image_name into the data layer for future use.
            TagManager.getInstance(this).getDataLayer().push(DataLayer.mapOf(IMAGE_NAME_KEY, Utils.getDeviceId(this), "hello", 1));
        }

        Toast.makeText(this, "ImageName: " + TagManager.getInstance(this).getDataLayer().get(IMAGE_NAME_KEY)+TagManager.getInstance(this).getDataLayer().get("hello"), Toast.LENGTH_SHORT).show();
        Log.v("ImageName", "ImageName: " + TagManager.getInstance(this).getDataLayer().get(IMAGE_NAME_KEY));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.pushOpenScreenEvent(this, "ImageViewScreen");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utils.pushCloseScreenEvent(this, "ImageViewScreen");
    }
}
