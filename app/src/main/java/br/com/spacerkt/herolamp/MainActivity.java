package br.com.spacerkt.herolamp;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //these constants represent each theme
    static final int HULK_THEME = 0;
    static final int IRON_THEME = 1;
    static final int CAPTAIN_THEME = 2;
    static final int THOR_THEME = 3;

    private ImageButton imgbLampSwitch;//Our switch to turn on or turn off the flashlight
    private ImageButton imgbHulk;
    private ImageButton imgbIron;
    private ImageButton imgbCaptain;
    private ImageButton imgbThor;
    private Camera cam;
    private boolean isFlashOn;
    private boolean hasFlash;
    private int lastHero;
    @SuppressWarnings("deprecation")
    Parameters params;
    MediaPlayer mp;
    RelativeLayout myLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find the elements on screen
        imgbLampSwitch = (ImageButton) findViewById(R.id.imgbLampSwitch);
        imgbHulk = (ImageButton) findViewById(R.id.imgbHulk);
        imgbIron = (ImageButton) findViewById(R.id.imgbIron);
        imgbCaptain = (ImageButton) findViewById(R.id.imgbCaptain);
        imgbThor = (ImageButton) findViewById(R.id.imgbThor);
        myLayout = (RelativeLayout) findViewById(R.id.mainActivityLayout);
        /**
         * Checking if the device has a flashlight, if doesn't finish the app
         */
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            //the device doesn't have support
            //so give the advise and close the application
            Toast.makeText(this, getApplicationContext()
                    .getString(R.string.error_message_flash), Toast.LENGTH_SHORT).show();
            //close the application
            finish();
        }

        imgbLampSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    turnOffFlash();
                }else{
                    turnOnFlash();
                }
            }
        });

        imgbHulk.setOnClickListener(this);
        imgbCaptain.setOnClickListener(this);
        imgbIron.setOnClickListener(this);
        imgbThor.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("lastHeroTheme", lastHero);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        lastHero = savedInstanceState.getInt("lastHeroTheme");
        switch(lastHero) {
            case HULK_THEME:
                hulkStyle();
                break;
            case IRON_THEME:
                ironStyle();
                break;
            case THOR_THEME:
                thorStyle();
                break;
            case CAPTAIN_THEME:
                captainStyle();
                break;
            default:
                defaultTheme();
                break;
        }
    }

    /**
     * Get the click event on imgb hero icon
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgbHulk:
                hulkStyle();
                lastHero = HULK_THEME;
                break;
            case R.id.imgbIron:
                ironStyle();
                lastHero = IRON_THEME;
                break;
            case R.id.imgbCaptain:
                captainStyle();
                lastHero = CAPTAIN_THEME;
                break;
            case R.id.imgbThor:
                thorStyle();
                lastHero = THOR_THEME;
                break;
        }
    }

    /**
     * Change the activity style to Hulk Style
     */
    public void hulkStyle(){
        setDefaultImages();
        imgbHulk.setImageResource(R.drawable.ic_hulk_active);
        myLayout.setBackgroundColor(getResources().getColor(R.color.colorHulk));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorHulkStatus));
        }
        clickSound();
    }

    /**
     * Change the activity style to Iron Style
     */
    public void ironStyle(){
        setDefaultImages();
        imgbIron.setImageResource(R.drawable.ic_iron_active);
        myLayout.setBackgroundColor(getResources().getColor(R.color.colorIron));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorIronStatus));
        }
        clickSound();
    }

    /**
     * Change the activity style to Captain Style
     */
    public void captainStyle(){
        setDefaultImages();
        imgbCaptain.setImageResource(R.drawable.ic_captain_active);
        myLayout.setBackgroundColor(getResources().getColor(R.color.colorCaptain));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorCaptainStatus));
        }
        clickSound();
    }

    /**
     * Change the activity style to Thor Style
     */
    public void thorStyle(){
        setDefaultImages();
        imgbThor.setImageResource(R.drawable.ic_thor_active);
        myLayout.setBackgroundColor(getResources().getColor(R.color.colorThor));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorThorStatus));
        }
        clickSound();
    }

    /**
     * Set default theme
     */
    public void defaultTheme(){
        setDefaultImages();
        myLayout.setBackgroundColor(getResources().getColor(R.color.colorDefault));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    /**
     * Set the default images to hero icon
     */
     public void setDefaultImages(){
         imgbHulk.setImageResource(R.drawable.ic_hulk_off);
         imgbIron.setImageResource(R.drawable.ic_iron_off);
         imgbCaptain.setImageResource(R.drawable.ic_captain_off);
         imgbThor.setImageResource(R.drawable.ic_thor_off);
     }

    /**
     * getting the cam
     */
    public void getCamera() {
        if (cam == null) {
            try {
                cam = Camera.open();
                params = cam.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera error: ", e.getMessage());
            }
        }
    }

    /**
     * turn the flash on
     */
    public void turnOnFlash() {
        if (!isFlashOn) {
            if (cam == null || params == null) {
                return;
            }
            //play the sound
            playSound();
            params = cam.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            cam.setParameters(params);
            isFlashOn = true;
            //change the button switch image
            toggleButtonImage();
        }
    }

    /**
     * Turning Off flash
     */
    private void turnOffFlash() {
        if (isFlashOn) {
            if (cam == null || params == null) {
                return;
            }
            // play sound
            playSound();
            params = cam.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            cam.setParameters(params);
            cam.stopPreview();
            isFlashOn = false;
            // changing button/switch image
            toggleButtonImage();
        }
    }

    /**
     * play the swith sound
     */
    public void playSound() {
        if (isFlashOn) {
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
        } else {
            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    /**
     * click sound
     */
    public void clickSound(){
        mp = MediaPlayer.create(MainActivity.this, R.raw.changetheme);
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    /**
     * Change the switch image
     */
    public void toggleButtonImage() {
        if (isFlashOn) {
            imgbLampSwitch.setImageResource(R.drawable.lamp_switch_on);
        } else {
            imgbLampSwitch.setImageResource(R.drawable.lamp_switch_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if (hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (cam != null) {
            cam.release();
            cam = null;
        }
    }

}
