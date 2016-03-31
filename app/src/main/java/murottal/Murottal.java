package murottal;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.fuad on 3/23/2016.
 */
public class Murottal extends Thread{
    private Context myContext;
    private List<AssetFileDescriptor> playList;
    private int current;

    public Murottal(Context context){
        this.myContext = context;
        playList = new ArrayList<AssetFileDescriptor>();
        scanOgg();
    }

    private void scanOgg(){
        try {
            for(String s :myContext.getAssets().list("murottal")){
                //Log.i("murottal",s);
                if(s.endsWith(".ogg")) {
                    playList.add(myContext.getAssets().openFd("murottal/" + s));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        try {
            //AssetFileDescriptor afd = myContext.getAssets().openFd("murottal/001-Al-fatiha.ogg");
            MediaPlayer mp = new MediaPlayer();
            current = 0;
            AssetFileDescriptor afd = playList.get(current);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
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
                    AssetFileDescriptor fd = playList.get(current);
                    try {
                        player.reset();
                        player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
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
