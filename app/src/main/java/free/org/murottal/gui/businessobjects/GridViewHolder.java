/*
 * SkyTube
 * Copyright (C) 2016  Ramon Mifsud
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation (version 3 of the License).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package free.org.murottal.gui.businessobjects;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import free.org.murottal.R;
import free.org.murottal.businessobjects.YouTubeVideo;
import free.org.murottal.gui.activities.ChannelBrowserActivity;

/**
 *
 */
public class GridViewHolder {
	private TextView titleTextView,
			channelTextView,
			thumbsUpPercentageTextView,
			videoDurationTextView,
			viewsTextView,
			publishDateTextView;
	private InternetImageView	thumbnailImageView;
	private RelativeLayout bottomLayout;
	/** YouTube video */
	private YouTubeVideo youTubeVideo = null;
	private Context context = null;

	private static final String TAG = GridViewHolder.class.getSimpleName();

	protected GridViewHolder(View row) {
		titleTextView				= (TextView) row.findViewById(R.id.title_text_view);
		channelTextView				= (TextView) row.findViewById(R.id.channel_text_view);
		thumbsUpPercentageTextView	= (TextView) row.findViewById(R.id.thumbs_up_text_view);
		videoDurationTextView		= (TextView) row.findViewById(R.id.video_duration_text_view);
		viewsTextView				= (TextView) row.findViewById(R.id.views_text_view);
		publishDateTextView			= (TextView) row.findViewById(R.id.publish_date_text_view);
		thumbnailImageView	= (InternetImageView) row.findViewById(R.id.thumbnail_image_view);
		bottomLayout		= (RelativeLayout) row.findViewById(R.id.cell_bottom_layout);
	}


	/**
	 * Updates the contents of this ViewHold such that the data of these views is equal to the
	 * given youTubeVideo.
	 *
	 * @param youTubeVideo		{@link YouTubeVideo} instance.
	 * @param showChannelInfo   True to display channel information (e.g. channel name) and allows
	 *                          user to open and browse the channel; false to hide such information.
	 */
	protected void updateInfo(YouTubeVideo youTubeVideo, Context context, boolean showChannelInfo) {
		this.youTubeVideo = youTubeVideo;
		this.context = context;
		updateViewsData(this.youTubeVideo, showChannelInfo);
	}


	/**
	 * This method will update the {@link View}s of this object reflecting the supplied video.
	 *
	 * @param video				{@link YouTubeVideo} instance.
	 * @param showChannelInfo   True to display channel information (e.g. channel name); false to
	 *                          hide such information.
	 */
	private void updateViewsData(YouTubeVideo video, boolean showChannelInfo) {
		titleTextView.setText(video.getTitle());
		channelTextView.setText(video.getChannelName());
		channelTextView.setVisibility(showChannelInfo ? View.VISIBLE : View.INVISIBLE);
		publishDateTextView.setText(video.getPublishDate());
		videoDurationTextView.setText(video.getDuration());
		viewsTextView.setText(video.getViewsCount());
		thumbnailImageView.setImageAsync(video.getThumbnailUrl());

		if (video.getThumbsUpPercentageStr() != null) {
			thumbsUpPercentageTextView.setVisibility(View.VISIBLE);
			thumbsUpPercentageTextView.setText(video.getThumbsUpPercentageStr());
		} else {
			thumbsUpPercentageTextView.setVisibility(View.INVISIBLE);
		}

		setupThumbnailOnClickListener();
		setupChannelOnClickListener(showChannelInfo);
	}


	private void setupThumbnailOnClickListener() {
		thumbnailImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View thumbnailView) {
				if (youTubeVideo != null) {
					YouTubePlayer.launch(youTubeVideo, context);
				}
			}
		});
	}



	private void setupChannelOnClickListener(boolean openChannelOnClick) {
		View.OnClickListener channelListener = null;

		if (openChannelOnClick) {
			channelListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, ChannelBrowserActivity.class);
					i.putExtra(ChannelBrowserActivity.CHANNEL_ID, youTubeVideo.getChannelId());
					context.startActivity(i);
				}
			};
		}

		channelTextView.setOnClickListener(channelListener);
		bottomLayout.setOnClickListener(channelListener);
	}

}
