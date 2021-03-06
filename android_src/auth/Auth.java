/**
 * Copyright 2017 FrogSquare. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.godotengine.godot.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;
import org.json.JSONException;

import org.godotengine.godot.Utils;

public class Auth {

	public static final int GOOGLE_AUTH 	= 0x0003;
	public static final int FACEBOOK_AUTH	= 0x0004;
	public static final int TWITTER_AUTH	= 0x0005;
	public static final int EMAIL_AUTH	    = 0x0006;
	public static final int ANONYMOUS_AUTH	= 0x0007;

	public static Auth getInstance (Activity p_activity) {
		if (mInstance == null) {
			mInstance = new Auth(p_activity);
		}

		return mInstance;
	}

	public Auth(Activity p_activity) {
		activity = p_activity;
	}

	public void init (FirebaseApp firebaseApp) {
		mFirebaseApp = firebaseApp;
	}

	public void configure (final String configData) {
		try { config = new JSONObject(configData); }
		catch (JSONException e) { Utils.d("JSONException, parse error: " + e.toString()); }

		//AuthGoogle++
		if (config.optBoolean("Google", false)) {
			GoogleSignIn.getInstance(activity).init();
		}
		//AuthGoogle--

		//AuthFacebook++
		if (config.optBoolean("Facebook", false)) {
			FacebookSignIn.getInstance(activity).init();
		}
		//AuthFacebook--

		//AuthTwitter++
		if (config.optBoolean("Twitter", false)) {
			TwitterSignIn.getInstance(activity).init();
		}
		//AuthTwitter--
        
        //AuthAnonymous++
        if (config.optBoolean("Anonymous", false)) {
            AnonymousSignIn.getInstance(activity).init();
        }
        //AuthAnonymous--
        
        //AuthEmailPassword++
        if (config.optBoolean("EmailPassword", false)) {
            EmailPasswordSignIn.getInstance(activity).init();
        }
        //AuthEmailPassword++
	}

	public void sign_in (final int type_id, final String... args) {
		if (!isInitialized()) { return; }

		Utils.d("Auth:SignIn:TAG:" + type_id);

		switch (type_id) {
			//AuthGoogle++
			case GOOGLE_AUTH:
				Utils.d("Auth:Google:SignIn");
				GoogleSignIn.getInstance(activity).signIn();
				break;
			//AuthGoogle--
			//AuthFacebook++
			case FACEBOOK_AUTH:
				Utils.d("Auth:Facebook:SignIn");
				FacebookSignIn.getInstance(activity).signIn();
				break;
			//AuthFacebook--
			//AuthTwitter++
			case TWITTER_AUTH:
				Utils.d("Auth:Twitter:SignIn");
				TwitterSignIn.getInstance(activity).signIn();
				break;
			//AuthTwitter--
            //AuthAnonymous++
			case ANONYMOUS_AUTH:
				Utils.d("Auth:Anonymous:SignIn");
				AnonymousSignIn.getInstance(activity).signIn();
				break;
            //AuthAnonymous--
            //AuthEmailPassword++ 
            case EMAIL_AUTH:
                Utils.d("Auth:EmailPassword:SignIn");
                EmailPasswordSignIn.getInstance(activity).signIn(args[0], args[1]);
                break;
            //AuthEmailPassword--
			default:
				Utils.d("Auth:Type:NotAvailable");
				break;
		}
	}
    
	public void sign_out (final int type_id) {
		if (!isInitialized()) { return; }

		Utils.d("Auth:SignOut:TAG:" + type_id);

		switch (type_id) {
			//AuthGoogle++
			case GOOGLE_AUTH:
				Utils.d("Auth:Google:SignOut");
				GoogleSignIn.getInstance(activity).signOut();
				break;
			//AuthGoogle--
			//AuthFacebook++
			case FACEBOOK_AUTH:
				Utils.d("Auth:Facebook:SignOut");
				FacebookSignIn.getInstance(activity).signOut();
				break;
			//AuthFacebook--
			//AuthTwitter++
			case TWITTER_AUTH:
				Utils.d("Auth:Twitter:SignOut");
				TwitterSignIn.getInstance(activity).signOut();
				break;
			//AuthTwitter--
			//AuthAnonymous++
			case ANONYMOUS_AUTH:
				Utils.d("Auth:Anonymous:SignOut");
				AnonymousSignIn.getInstance(activity).signOut();
				break;
            //AuthAnonymous--
            //AuthEmailPassword++
            case EMAIL_AUTH:
                Utils.d("Auth:Anonymous:SignOut");
				EmailPasswordSignIn.getInstance(activity).signOut();
				break;
            //AuthEmailPassword--
			default:
				Utils.d("Auth:Type:NotAvailable.");
				break;
		}
	}
    
    public void create_account (final int type_id, final String... args) {
        if (!isInitialized()) { return; }
        
        Utils.d("Auth:CreateAccount:TAG:" + type_id);
        
        EmailPasswordSignIn.getInstance(activity).createAccount(args[0], args[1]);
    }

	public void revoke(final int type_id) {
		if (!isInitialized()) { return; }

		Utils.d("FB:Auth:Revoke:" + type_id);

		switch (type_id) {
			//AuthGoogle++
			case GOOGLE_AUTH:
				Utils.d("FB:Revoke:Google");
				GoogleSignIn.getInstance(activity).revokeAccess();
				break;
			//AuthGoogle--
			//AuthFacebook++
			case FACEBOOK_AUTH:
				Utils.d("FB:Revoke:Facebook");
				FacebookSignIn.getInstance(activity).revokeAccess();
				break;
			//AuthFacebook--
			default:
				Utils.d("FB:Auth:Type:NotFound");
		}

	}

	public void signUp(final int type_id) {
		if (!isInitialized()) { return; }

		Utils.d("Auth:Linking:" + type_id);
/**
		TODO: Signup/LinkAccount from Anonymous account.
**/
	}


	@Nullable
	public FirebaseUser getCurrentUser() {
		if (!isInitialized()) { return null; }

		FirebaseUser ret = FirebaseAuth.getInstance().getCurrentUser();
		if (ret == null) { Utils.d("Auth:UserNotSignedIn"); }

		return ret;
	}

	public String getUserDetails(final int type_id) {
		if (!isInitialized()) { return "NULL"; }

		Utils.d("UserDetails:TAG:" + type_id);

		//AuthGoogle++
		if (type_id == GOOGLE_AUTH && GoogleSignIn.getInstance(activity).isConnected()) {
			Utils.d("Getting Google user details");
			return GoogleSignIn.getInstance(activity).getUserDetails();
		}
		//AuthGoogle--

		//AuthFacebook++
		if (type_id == FACEBOOK_AUTH && FacebookSignIn.getInstance(activity).isConnected()) {
			Utils.d("Getting Facebook user details");
			return FacebookSignIn.getInstance(activity).getUserDetails();
		}
		//AuthFacebook--
        
        //AuthEmailPassword++
		if (type_id == EMAIL_AUTH && EmailPasswordSignIn.getInstance(activity).isConnected()) {
			Utils.d("Getting Email user details");
			return EmailPasswordSignIn.getInstance(activity).getUserDetails();
		}
		//AuthEmailPassword--

		return "NULL";
	}

	//AuthFacebook++
	public String getFacebookPermissions() {
		return FacebookSignIn.getInstance(activity).getUserPermissions();
	}

	public boolean isPermissionGiven(final String permission) {
		return FacebookSignIn.getInstance(activity).isPermissionGiven(permission);
	}

	public void revokeFacebookPermission(final String permission) {
		if (!isInitialized() && !isConnected(FACEBOOK_AUTH)) { return; }

		Utils.d("Auth:Ask:RevokePermission: " + permission);
		FacebookSignIn.getInstance(activity).revokePermission(permission);
	}

	public void askForPermission(
	final String title, final String message, final String permission, final boolean read) {
		if (!isInitialized() && !isConnected(FACEBOOK_AUTH)) { return; }

		Utils.d("Auth:Ask:Permission: " + permission);
		FacebookSignIn.getInstance(activity)
		.askForPermission(title, message, permission, read);
	}
	//AuthFacebook--

	public boolean isConnected(final int type_id) {
		Utils.d("Auth:Getting:Status");

		switch (type_id) {
			//AuthGoogle++
			case GOOGLE_AUTH:
				Utils.d("Auth:Status:Google");
				return GoogleSignIn.getInstance(activity).isConnected();
			//AuthGoogle--
			//AuthFacebook++
			case FACEBOOK_AUTH:
				Utils.d("Auth:Status:Facebook");
				return FacebookSignIn.getInstance(activity).isConnected();
			//AuthFacebook--
			//AuthAnonymous++
			case ANONYMOUS_AUTH:
				Utils.d("Auth:Status:Anonymous");
				return AnonymousSignIn.getInstance(activity).isConnected();
            //AuthAnonymous--
            //AuthEmailPassword++
            case EMAIL_AUTH:
                Utils.d("Auth:Status:EmailPassword");
				return EmailPasswordSignIn.getInstance(activity).isConnected();
            //AuthEmailPassword--
			default:
				Utils.d("Auth:Type:NotAvailable");
				break;
		}

		Utils.d("Auth:Status:False");
		return false;
	}

	private boolean isInitialized() {
		if (mFirebaseApp == null) {
			Utils.d("FireBase Auth, not initialized");
			return false;
		}

		if (config == null) {
			Utils.d("FireBase Auth, not Configured");
			return false;
		}

		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!isInitialized()) { return; }

		//AuthGoogle++
		if (config.optBoolean("Google", false)) {
			GoogleSignIn.getInstance(activity)
			.onActivityResult(requestCode, resultCode, data);
		}
		//AuthGoogle--

		//AuthFacebook++
		if (config.optBoolean("Facebook", false)) {
			FacebookSignIn.getInstance(activity)
			.onActivityResult(requestCode, resultCode, data);
		}
		//AuthFacebook--

		//AuthTwitter++
		if (config.optBoolean("Twitter", false)) {
			TwitterSignIn.getInstance(activity)
			.onActivityResult(requestCode, resultCode, data);
		}
		//AuthTwitter--
	}

	public void onStart() {
		if (!isInitialized()) { return; }

		//AuthGoogle++
		if (config.optBoolean("Google", false)) {
			GoogleSignIn.getInstance(activity).onStart();
		}
		//AuthGoogle--

		//AuthFacebook++
		if (config.optBoolean("Facebook", false)) {
			FacebookSignIn.getInstance(activity).onStart();
		}
		//AuthFacebook--

		//AuthTwitter++
		if (config.optBoolean("Twitter", false)) {
			TwitterSignIn.getInstance(activity).onStart();
		}
		//AuthTwitter--
        
        //AuthAnonymous++
		if (config.optBoolean("Anonymous", false)) {
			AnonymousSignIn.getInstance(activity).onStart();
		}
		//AuthAnonymous--
        
        //AuthEmailPassword++
		if (config.optBoolean("EmailPassword", false)) {
			EmailPasswordSignIn.getInstance(activity).onStart();
		}
		//AuthEmailPassword--

	}

	public void onPause() {

	}

	public void onResume () {

	}

	public void onStop() {
		if (!isInitialized()) { return; }

		//AuthGoogle++
		if (config.optBoolean("Google", false)) {
			GoogleSignIn.getInstance(activity).onStop();
		}
		//AuthGoogle--

		//AuthFacebook++
		if (config.optBoolean("Facebook", false)) {
			FacebookSignIn.getInstance(activity).onStop();
		}
		//AuthFacebook--

		//AuthTwitter++
		if (config.optBoolean("Twitter", false)) {
			TwitterSignIn.getInstance(activity).onStop();
		}
		//AuthTwitter--
        
        //AuthAnonymous++
		if (config.optBoolean("Anonymous", false)) {
			AnonymousSignIn.getInstance(activity).onStop();
		}
		//AuthAnonymous--
        
        //AuthEmailPassword++
		if (config.optBoolean("EmailPassword", false)) {
			EmailPasswordSignIn.getInstance(activity).onStop();
		}
		//AuthEmailPassword--
	}

	private static Activity activity = null;
	private static Auth mInstance = null;

	private static JSONObject config = null;

	private FirebaseApp mFirebaseApp = null;
}


