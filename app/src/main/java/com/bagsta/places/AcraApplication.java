package com.bagsta.places;

import org.acra.annotation.ReportsCrashes;
import org.acra.ACRA;
import  org.acra.ReportingInteractionMode;

import com.bagsta.places.R;

import android.app.Application;

@ReportsCrashes (
	formKey = "", // will not be used
            mailTo = "reportsappcrash@gmail.com",
            mode = ReportingInteractionMode.TOAST,
            resToastText = R.string.crash_toast_text)

public class AcraApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
	}
}
