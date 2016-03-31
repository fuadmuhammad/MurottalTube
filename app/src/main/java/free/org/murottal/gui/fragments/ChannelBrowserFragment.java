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

package free.org.murottal.gui.fragments;


import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.io.IOException;

import free.org.murottal.R;
import free.org.murottal.businessobjects.VideoCategory;
import free.org.murottal.businessobjects.YouTubeChannel;
import free.org.murottal.gui.activities.ChannelBrowserActivity;
import free.org.murottal.gui.businessobjects.FragmentEx;
import free.org.murottal.gui.businessobjects.VideoGridAdapter;
import free.org.murottal.gui.businessobjects.InternetImageView;

/**
 * A Fragment that displays information about a channel.
 */
public class ChannelBrowserFragment extends FragmentEx {

	private YouTubeChannel	channel = null;
	private GridView		gridView;
	private VideoGridAdapter videoGridAdapter;

	private InternetImageView	channelThumbnailImage = null;
	private InternetImageView	channelBannerImage = null;
	private TextView			channelSubscribersTextView = null;
	private GetChannelInfoTask	task = null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final String channelId = getActivity().getIntent().getStringExtra(ChannelBrowserActivity.CHANNEL_ID);

		// inflate the layout for this fragment
		View fragment = inflater.inflate(R.layout.fragment_channel_browser, container, false);

		channelBannerImage = (InternetImageView) fragment.findViewById(R.id.channel_banner_image_view);
		channelThumbnailImage = (InternetImageView) fragment.findViewById(R.id.channel_thumbnail_image_view);
		channelSubscribersTextView = (TextView) fragment.findViewById(R.id.channel_subs_text_view);

		if (channel == null) {
			if (task == null) {
				task = new GetChannelInfoTask();
				task.execute(channelId);
			}
		} else {
			initViews();
		}

		gridView = (GridView) fragment.findViewById(R.id.grid_view);

		if (videoGridAdapter == null) {
			videoGridAdapter = new VideoGridAdapter(getActivity(), false /*hide channel name*/);
			videoGridAdapter.setVideoCategory(VideoCategory.CHANNEL_VIDEOS, channelId);
		}

		this.gridView.setAdapter(this.videoGridAdapter);

		return fragment;
	}


	private void initViews() {
		if (channel != null) {
			channelThumbnailImage.setImageAsync(channel.getThumbnailNormalUrl());
			channelBannerImage.setImageAsync(channel.getBannerUrl());
			channelSubscribersTextView.setText(channel.getTotalSubscribers());

			ActionBar actionBar = getActionBar();
			if (actionBar != null) {
				actionBar.setTitle(channel.getTitle());
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////

	private class GetChannelInfoTask extends AsyncTask<String, Void, YouTubeChannel> {

		private final String TAG = GetChannelInfoTask.class.getSimpleName();

		@Override
		protected YouTubeChannel doInBackground(String... channelId) {
			YouTubeChannel chn = new YouTubeChannel();

			try {
				chn.init(channelId[0]);
			} catch (IOException e) {
				Log.e(TAG, "Unable to get channel info.  ChannelID=" + channelId[0], e);
				chn = null;
			}

			return chn;
		}

		@Override
		protected void onPostExecute(YouTubeChannel youTubeChannel) {
			ChannelBrowserFragment.this.channel = youTubeChannel;
			initViews();
		}

	}

}
