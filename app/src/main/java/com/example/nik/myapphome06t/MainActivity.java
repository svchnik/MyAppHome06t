package com.example.nik.myapphome06t;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    public  static  final String LOG = "LOG";

    public static final  int STATUS_NONE = 0;
    public static final  int STATUS_CONNECTING = 1;
    public static final  int STATUS_CONNECTED = 2;
    public static final  int STATUS_DAWNLOAD_START = 3;
    public static final  int STATUS_DAWNLOAD_FILE = 4;  //файл загружен
    public static final  int STATUS_DAWNLOAD_END = 5;
    public static final  int STATUS_DAWNLOAD_NONE = 6;

    Handler handler;

    TextView textView;
    Button button;
    ProgressBar progressBar;

    ArrayList<ItemParser> mArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textViev);
        button = (Button) findViewById(R.id.btnConnect);
        progressBar = (ProgressBar) findViewById(R.id.pbConnect);

        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                switch (msg.what){
                    case STATUS_NONE:
                        button.setEnabled(true);
                        textView.setText("Notconnected");
                        progressBar.setVisibility(View.GONE);
                        break;
                    case STATUS_CONNECTING:
                        button.setEnabled(false);
                        textView.setText("Connecting...");
                        break;
                    case STATUS_CONNECTED:
                        textView.setText("Connected");
                        break;
                    case STATUS_DAWNLOAD_START:
                        textView.setText("Start download " + msg.arg1 + " files");
                        progressBar.setMax(msg.arg1);
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case STATUS_DAWNLOAD_FILE:
                        textView.setText("Downloading. left " + msg.arg2 + " files");
                        progressBar.setProgress(msg.arg1);
                        saveFile((byte[]) msg.obj);
                        break;
                    case STATUS_DAWNLOAD_END:
                        textView.setText("Download complete!");
                        break;
                    case STATUS_DAWNLOAD_NONE:
                        textView.setText("No files for download");
                        break;
                }
            };
        };


        handler.sendEmptyMessage(STATUS_NONE);

    }


    public void OnClick(View view){
        Thread thread = new Thread(new Runnable() {
            Message msg;
            //byte[] file;
            Random rand = new Random();

            @Override
            public void run() {
                try{
                    //подключаемся к серверу
                    handler.sendEmptyMessage(STATUS_CONNECTING);
                    TimeUnit.SECONDS.sleep(1);

                    //подключено
                    handler.sendEmptyMessage(STATUS_CONNECTED);

                    //работа
                    downloadFile();

                    //определяем к-во файлов
                    TimeUnit.SECONDS.sleep(1);
                    int filesCount = mArr.size();

                    if(filesCount == 0) {
                        //нет файлов для загрузки
                        handler.sendEmptyMessage(STATUS_DAWNLOAD_NONE);
                        //отключаемся
                        TimeUnit.MILLISECONDS.sleep(1500);
                        handler.sendEmptyMessage(STATUS_NONE);
                        return;
                    }

                    //начало загрузки файлов
                    //создаём сообщение о кол-ве файлов
                    msg = handler.obtainMessage(STATUS_DAWNLOAD_START, filesCount, 0, mArr);
                    //отправляем
                    handler.sendMessage(msg);

                    //downloadFile();
                    for(int i=1; i <= filesCount; i++){
                        //загрузка файла
                        TimeUnit.MILLISECONDS.sleep(100);
                        //создаём сообщение о порядковом номере файла, к-вом оставшихся
                        msg = handler.obtainMessage(STATUS_DAWNLOAD_FILE, i, filesCount - i);
                        //отправляем
                        handler.sendMessage(msg);
                    }

                    //загрузка завершена
                    handler.sendEmptyMessage(STATUS_DAWNLOAD_END);

                    //отключаемся
                    TimeUnit.MILLISECONDS.sleep(1500);
                    handler.sendEmptyMessage(STATUS_NONE);

                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    //емулирует загрузку файлов
    public void downloadFile() throws InterruptedException{
        mArr = new FlickDWNL().fetchItems();
    }


    //метод сохранения файлов на диск
    void saveFile(byte[] file){
        //mArr

    }




}
