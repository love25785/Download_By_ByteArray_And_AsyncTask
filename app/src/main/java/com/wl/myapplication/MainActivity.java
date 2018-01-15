//36改編31-1的做法來存取網路的圖檔，用ByteArray
package com.wl.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    ImageView img;
    TextView tv,tv2,tv3;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        pb=(ProgressBar)findViewById(R.id.progressBar);
    }
    public void click1(View v)
    {

        new Thread() {
            @Override
            public void run() {
                super.run();
                String str_url = "https://5.imimg.com/data5/UH/ND/MY-4431270/red-rose-flower-500x500.jpg";
                URL url;
                try
                {
                    url = new URL(str_url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();//用一個ByteArray來裝OutputStream
                    byte[] buf = new byte[1024];//設置一個只能裝1024byte的陣列，意味每次讀取1024byte
                    final int totalLength = conn.getContentLength();//總資料長度
                    int sum = 0;
                    int length;
                    while ((length = inputStream.read(buf)) != -1)//當讀近來沒資料 會回傳-1回來，所以有資料時會不等於-1，沒資料時會跳出while
                    {
                        sum += length;
                        final int tmp = sum;
                        bos.write(buf, 0, length);//第三個引數要寫length，是因為當最後一筆資料不會是1024byte，但也要寫入
                        runOnUiThread(new Runnable() //前面我們起一個thread負責跑這些，但這結果想呈現回主執行序時，就會用到runOnUiThread
                        {
                            @Override
                            public void run()
                            {
                                tv.setText(String.valueOf(tmp)+"/"+totalLength);
                                pb.setProgress(100*tmp/totalLength);
                            }
                        });
                    }
                    byte[] results = bos.toByteArray();//將資料取出toByteArray()
                    final Bitmap bmp = BitmapFactory.decodeByteArray(results, 0, results.length);//做出圖檔
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            img.setImageBitmap(bmp);
                        }
                    });
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (ProtocolException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }.start();
    }

///////////////////////////////////////////////////////////以下是使用    AsyncTask 來代替 thread的作法//////////////////////////////////////////

    public void click2(View v)
    {
        MyTask task=new MyTask();
        task.execute(10);
    }




    class MyTask extends AsyncTask<Integer,Integer,String> //AsyncTask是抽象類別，且有3個泛型參數，分別為傳入值，回報值，結果值。自己做個CLASS來繼承，而也有四大方法可以用
    {
        @Override //一.這個方法會在別的執行序做
        protected String doInBackground(Integer... integers) //這裡引數表示  是Integer型態  的 一個 "不特定數量"的陣列  命名為，  ...為不限制數量
        {
            int i;
            for(i=0;i<=integers[0];i++)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                Log.d("TASK","doInBackground, i:"+i);
                publishProgress(i);  //publishProgress是傳值給onProgressUpdate這個方法 ，便能在執行中 更新
            }

            return "okey";  //而在doInBackground最後回傳值 是會回傳到 onPostExecute這個方法

        }

        @Override //二.這會在作之前作?   在主執行序上作?
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override //三.這個會在執行時 更新  ，這個方法會在主執行序作
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            tv2.setText(String.valueOf(values[0]));
        }



        @Override //四.這會在doInBackground回傳return後 接收 動作，這個方法在主執行序上作
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            tv3.setText(s);
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void click3(View v)
    {

    }

    class MyTask2 extends AsyncTask<Integer,Integer,String>
    {
        @Override
        protected String doInBackground(Integer... integers)
        {
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(String s)
        {


        }
    }
}





