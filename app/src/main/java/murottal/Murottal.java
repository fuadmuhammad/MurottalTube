package murottal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import free.org.murottal.R;

/**
 * Created by muhammad.fuad on 3/23/2016.
 */
public class Murottal extends Thread{
    private Context myContext;
    private List playList;
    private int current;
    private SharedPreferences sharedPref;
    private boolean play_default;

    public Murottal(Context context){
        this.myContext = context;
        playList = new ArrayList<AssetFileDescriptor>();
        scanFile();
    }

    private void scanFile(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(myContext);
        play_default = sharedPref.getBoolean(myContext.getString(R.string.default_murottal_loc_key), true);
        try{
            if(play_default){
                for(String s :myContext.getAssets().list("murottal")){
                    if(s.toLowerCase().endsWith(".ogg") || s.toLowerCase().endsWith("mp3")) {
                        playList.add(myContext.getAssets().openFd("murottal/" + s));
                    }
                }
            }else {
                String custom_dir = sharedPref.getString(myContext.getString(R.string.custom_murottal_loc_key), "").trim();
                if (custom_dir.isEmpty()) {
                    //source_dir = myContext.getAssets().list("murottal");
                } else {
                    File rootFile = new File(custom_dir);
                    int i = 0;
                    for (File f : rootFile.listFiles()) {
                        if (!f.isDirectory()) {
                            if(f.getName().toLowerCase().endsWith(".ogg") || f.getName().toLowerCase().endsWith("mp3")) {
                                playList.add(f.getAbsolutePath());
                            }

                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        try {
            MediaPlayer mp = new MediaPlayer();
            current = 0;
            if(play_default){
                AssetFileDescriptor afd = (AssetFileDescriptor)playList.get(current);
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            }else{
                String s = (String)playList.get(current);
                mp.setDataSource(s);
            }
            mp.prepare(); // might take long! (for buffering, etc)
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    if(current == playList.size()-1){
                        current = 0;
                    }else{
                        current++;
                    }
                    try {
                        player.reset();
                        if(play_default){
                            AssetFileDescriptor fd = (AssetFileDescriptor)playList.get(current);
                            player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                        }else{
                            String pathFile = (String)playList.get(current);
                            player.setDataSource(pathFile);
                        }
                        player.prepare();
                        player.start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
