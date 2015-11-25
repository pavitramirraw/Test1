package com.google.android.tagmanager.examples.cuteanimals;

import org.json.JSONArray;

/**
 * Created by pavitra on 25/11/15.
 */
public class CustomizedJsonArray extends JSONArray {

    public CustomizedJsonArray remove(int index, CustomizedJsonArray array){
        CustomizedJsonArray array1 = new CustomizedJsonArray();
        try {
            for (int i = 0; i < array.length(); i++) {
                if (i != index) {
                    array1.put(array.get(i));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return array1;
    }
}
