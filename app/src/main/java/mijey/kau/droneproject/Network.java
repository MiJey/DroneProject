package mijey.kau.droneproject;

/**
 * Created by mac on 2018. 5. 19..
 */

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class Network extends AsyncTask<Void, Void, String> {
    //172.30.1.38:8080/
    //http://localhost:8080/DroneServer/myProtocol
    private String url = "";
    private String output;
    private ContentValues values;

    public Network(int num, String ip){
        this.url = "http://" + ip + ":8080";
        Log.d("NetworkTest", "url: " + url);
        values = new ContentValues();
        values.put("key",""+num);

    }
    @Override
    protected String doInBackground(Void... params) {

        String result;
        HttpRequest HttpRequest = new HttpRequest();
        result = HttpRequest.request(url, values);


        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        output = s;

        System.out.println(output);

        //do something


    }

}