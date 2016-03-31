package free.org.murottal.gui.activities;

import android.app.Activity;
import android.os.Bundle;

import free.org.murottal.R;
import free.org.murottal.gui.businessobjects.BackActivity;

/**
 * An {@link Activity} that contains an instance of
 * {@link free.org.murottal.gui.fragments.YouTubePlayerFragment}.
 */
public class YouTubePlayerActivity extends BackActivity {

	public static final String YOUTUBE_VIDEO_OBJ = "YouTubePlayerActivity.yt_video_obj";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);
	}

}
