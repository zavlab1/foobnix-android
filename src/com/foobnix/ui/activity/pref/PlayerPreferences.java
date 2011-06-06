/* Copyright (c) 2011 Ivan Ivanenko <ivan.ivanenko@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */
package com.foobnix.ui.activity.pref;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.foobnix.ui.activity.VkCheckActivity;
import com.foobnix.ui.activity.pref.base.NotificationIconPreference;
import com.foobnix.ui.activity.pref.base.RandomModePreference;
import com.foobnix.ui.activity.pref.base.SleepTimerPreference;
import com.foobnix.util.C;
import com.foobnix.util.Conf;

public class PlayerPreferences extends PrefMenuActivity {
	private PreferenceScreen root;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setPreferenceScreen(createPreferenceHierarchy());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private PreferenceScreen createPreferenceHierarchy() {
		root = getPreferenceManager().createPreferenceScreen(this);
		root.setTitle("Foobnix Settings");

		// Base services
		PreferenceCategory category = new PreferenceCategory(this);
		category.setTitle("Services");
		root.addPreference(category);

		PreferenceScreen lastFm = getPreferenceManager().createPreferenceScreen(this);
		lastFm.setTitle("Last.Fm Scrobbler");
		category.addPreference(lastFm);

		if (C.get().lastFmEnable) {
			if (app.getLastFmService().isConnectedAndEnable()) {
				lastFm.setSummary("Enabled for " + C.get().lastFmUser);
			} else {
				lastFm.setSummary("Failed for " + C.get().lastFmUser);
			}
		} else {
			lastFm.setSummary("Disabled");
		}

		lastFm.setOnPreferenceClickListener(onLastfm);

		PreferenceScreen vk = getPreferenceManager().createPreferenceScreen(this);
		vk.setTitle("VKontakte Authorization");
		vk.setOnPreferenceClickListener(onVK);
		if (StringUtils.isNotEmpty(C.get().vkontakteToken)) {
			vk.setSummary("Success (please recheck if expired)");
		} else {
			vk.setSummary("Not configured");
		}
		category.addPreference(vk);

		// Config
		PreferenceCategory config = new PreferenceCategory(this);
		config.setTitle("Configs");
		root.addPreference(config);

		// config.addPreference(new AlarmPreference().Builder(this, app));
		config.addPreference(new NotificationIconPreference().Builder(this, app));
		config.addPreference(new SleepTimerPreference().Builder(this, app));
		config.addPreference(new RandomModePreference().Builder(this));

		// Other
		PreferenceCategory other = new PreferenceCategory(this);
		other.setTitle("Other");
		root.addPreference(other);

		PreferenceScreen version = getPreferenceManager().createPreferenceScreen(this);
		version.setTitle("Foobnix Player");
		version.setSummary(Conf.getFoobnixVersion(getApplicationContext()));
		other.addPreference(version);

		PreferenceScreen web = getPreferenceManager().createPreferenceScreen(this);
		web.setTitle("Web Site");

		web.setSummary("Visit http://android.foobnix.com");
		web.setOnPreferenceClickListener(onWeb);
		other.addPreference(web);

		PreferenceScreen about = getPreferenceManager().createPreferenceScreen(this);
		about.setTitle("About");
		about.setSummary("Ivan Ivanenko <ivan.ivanenko@gmail.com>");
		other.addPreference(about);
		return root;

	}

	OnPreferenceClickListener onWeb = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Locale locale = Locale.getDefault();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://android.foobnix.com/news?lang="
			        + locale.getLanguage())));
			return false;
		}
	};

	OnPreferenceClickListener onVK = new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			startActivity(new Intent(PlayerPreferences.this, VkCheckActivity.class));
			return false;
		}
	};

	OnPreferenceClickListener onLastfm = new OnPreferenceClickListener() {

		public boolean onPreferenceClick(Preference p) {
			startActivity(new Intent(getApplicationContext(), LastFmPreferences.class));
			return false;
		}
	};

}
