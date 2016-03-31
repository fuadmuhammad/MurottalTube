/*
 * SkyTube
 * Copyright (C) 2015  Ramon Mifsud
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

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.annotation.NonNull;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import free.org.murottal.BuildConfig;
import free.org.murottal.R;
import free.org.murottal.businessobjects.VideoStream.VideoResolution;

/**
 * A fragment that allows the user to change the settings of this app.  This fragment is called by
 * {@link free.org.murottal.gui.activities.PreferencesActivity}
 */
public class PreferencesFragment extends PreferenceFragment{

	private static final String TAG = PreferencesFragment.class.getSimpleName();
	private static final int REQUEST_DIRECTORY = 0;
    private Preference FilePicker;
    private DirectoryChooserFragment mDialog;
	private SharedPreferences sharedPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		ListPreference resolutionPref = (ListPreference) findPreference(getString(R.string.pref_key_preferred_res));
		resolutionPref.setEntries(VideoResolution.getAllVideoResolutionsNames());
		resolutionPref.setEntryValues(VideoResolution.getAllVideoResolutionsIds());
		FilePicker = (Preference) findPreference(getString(R.string.custom_murottal_loc_key));
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		FilePicker.setSummary(sharedPref.getString(getString(R.string.custom_murottal_loc_key), ""));

		// if the user clicks on the license, then open the display the actual license
		Preference licensePref = findPreference(getString(R.string.pref_key_license));
		licensePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				displayAppLicense();
				return true;
			}
		});

		// if the user clicks on the website link, then open it using an external browser
		Preference websitePref = findPreference(getString(R.string.pref_key_website));
		websitePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// view the app's website in a web browser
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.pref_summary_website)));
				startActivity(browserIntent);
				return true;
			}
		});

		// remove the 'use official player' checkbox if we are running an OSS version
		if (BuildConfig.FLAVOR.equals("oss")) {
			PreferenceCategory videoPlayerCategory = (PreferenceCategory) findPreference(getString(R.string.pref_key_video_player_category));
			Preference useOfficialPlayer = findPreference(getString(R.string.pref_key_use_offical_player));
			videoPlayerCategory.removePreference(useOfficialPlayer);
		}

		// set the app's version number
		Preference versionPref = findPreference(getString(R.string.pref_key_version));
		versionPref.setSummary(BuildConfig.VERSION_NAME);

		final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
				.newDirectoryName("DialogSample")
				.allowReadOnlyDirectory(true)
				.allowNewDirectoryNameModification(false)
				.build();
		mDialog = DirectoryChooserFragment.newInstance(config);
		mDialog.setDirectoryChooserListener(new DirectoryChooserFragment.OnFragmentInteractionListener() {
			@Override
			public void onSelectDirectory(@NonNull final String path) {
				FilePicker.setSummary(path);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.custom_murottal_loc_key), path);
				editor.commit();
				mDialog.dismiss();
			}

			@Override
			public void onCancelChooser() {
				mDialog.dismiss();
			}
		});

        final FragmentManager fm = getFragmentManager();
		FilePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
                mDialog.show(getFragmentManager(), null);
                return true;
            }
        });
	}

	/**
	 * Displays the app's license in an AlertDialog.
	 */
	private void displayAppLicense() {
		new AlertDialog.Builder(getActivity())
				.setMessage(R.string.app_license)
				.setNeutralButton(R.string.i_agree, null)
				.setCancelable(false)	// do not allow the user to click outside the dialog or press the back button
				.show();
	}

}
