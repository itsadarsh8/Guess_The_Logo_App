package com.example.moon.guessthelogo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> logoUrl=new ArrayList<String>();
    ArrayList<String> logoName=new ArrayList<String>();
    int chosenLogo;
    int locationOfCorrectAnswer=0,incorrectAnswerLocation;
    String[] answers= new String[4];
    ImageView imageView;
    Bitmap logoImage;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void buttonClick(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Wrong!It was "+logoName.get(chosenLogo),Toast.LENGTH_LONG).show();
        }
        createNewQuestion();
    }



    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {


                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in =urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data= reader.read();
                }
                return result;


            }
            catch(Exception e){
                e.printStackTrace();
            }




            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView)findViewById(R.id.logoView);
        button0=(Button)findViewById(R.id.option1);
        button1=(Button)findViewById(R.id.option2);
        button2=(Button)findViewById(R.id.option3);
        button3=(Button)findViewById(R.id.option4);



        DownloadTask task=new DownloadTask();
        String result=null;
        try {
            result = task.execute("https://www.logaster.com/blog/25-famous-brands-logos/").get();
            // Log.i("info",result);

            String[] splitResult = result.split("class=\"entry-content clearfix\"");

            Pattern p = Pattern.compile("\" src=\"(.*?)\" alt=");
            Matcher m = p.matcher(splitResult[1]);

            while (m.find()) {
              //  System.out.println(m.group(1));
                logoUrl.add(m.group(1));
            }
            p = Pattern.compile("<h2>(.*?)</h2>");
            m = p.matcher(splitResult[1]);
            while (m.find()) {
            //    System.out.println(m.group(1));
                logoName.add(m.group(1));
            }


        }
        catch(Exception e){
            e.printStackTrace();
        }
        createNewQuestion();
    }

    public void createNewQuestion(){
        chosenLogo= new Random().nextInt(25);
        ImageDownloader imageTask=new ImageDownloader();

       try {


           logoImage = imageTask.execute(logoUrl.get(chosenLogo)).get();
       }
       catch(Exception e){
           e.printStackTrace();
       }
        imageView.setImageBitmap(logoImage);

        locationOfCorrectAnswer=new Random().nextInt(4);
        for(int i=0;i<4;i++)
        {
            if(i==locationOfCorrectAnswer){
                answers[i]= logoName.get(chosenLogo);
            }
            else{
                incorrectAnswerLocation=new Random().nextInt(25);
                while(incorrectAnswerLocation==chosenLogo){
                    locationOfCorrectAnswer=new Random().nextInt(4);
                }
                answers[i]=logoName.get(incorrectAnswerLocation);

            }
        }
        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);
    }
}
