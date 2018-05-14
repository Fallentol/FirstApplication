package com.example.oracle.firstapp;

import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textNote; // текстушка на экране
    private SoundPool mSoundPool; // пул звуков
    private int CBSound;
    private int mStreamID;
    private AssetManager mAssetManager; // обеспечивает доступ к файлам системы по их имени

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textNote = (TextView) findViewById(R.id.myTestText);
        mAssetManager = getAssets();
        createNewSoundPool();
        CBSound = loadSound("sounds/soul.ogg");

        ImageButton CBImageButton = (ImageButton) findViewById(R.id.CButton);
        CBImageButton.setOnClickListener(onClickListener);

        CBImageButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();
                if (eventAction == MotionEvent.ACTION_UP) {
                    // Отпускаем палец
                    if (mStreamID > 0)
                        mSoundPool.stop(mStreamID);
                }
                if (eventAction == MotionEvent.ACTION_DOWN) {
                    // Нажимаем на кнопку
                    mStreamID = playSound(CBSound);
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mSoundPool.stop(mStreamID);
                }
                return true;
            }
        });
    }

    /**
     * Кнопка с текстовым полем
     *
     * @param view
     */
    public void onClick(View view) {
        textNote.setText("Видал****");
        textNote.setTextColor(Color.RED);
    }

    public void changeColor(View view) {
        View helloTextView = findViewById(R.id.mainWindow);
        int x = (int)(Math.random()*250);
        int y = (int)(Math.random()*250);
        int z = (int)(Math.random()*250);
        helloTextView.setBackgroundColor(Color.rgb(x,y,z));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playSound(CBSound);
        }
    };

    private int playSound(int sound) {
        if (sound > 0) {
            mStreamID = mSoundPool.play(sound, 1, 1, 1, 0, 1);
        }
        return mStreamID;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    private void createOldSoundPool() {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    }

    /**
     * @param fileName имя звукового файла
     * @return номер этого файла в пуле звуков
     */
    private int loadSound(String fileName) {
        AssetFileDescriptor afd;
        try {
            afd = mAssetManager.openFd(fileName); // дескриптор файла получаем из менеджера файлов
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Не могу загрузить файл " + fileName,
                    Toast.LENGTH_SHORT).show();
            return -1;
        } catch (Exception er) {
            er.printStackTrace();
            Toast.makeText(getApplicationContext(), "Файла нет " + fileName + " -- " + er.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return -1;
        }

        return mSoundPool.load(afd, 1); // загружаем в пул звуков дискриптор звукового файла и получаем его идентификатор
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNewSoundPool();
        mAssetManager = getAssets();
        CBSound = loadSound("sounds/soul.ogg");
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();
        mSoundPool = null;
    }

}
