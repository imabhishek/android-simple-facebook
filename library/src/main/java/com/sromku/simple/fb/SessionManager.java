package com.sromku.simple.fb;

import android.app.Activity;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnNewPermissionsListener;
import com.sromku.simple.fb.utils.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SessionManager {

	private final static Class<?> TAG = SessionManager.class;
    public static Activity activity;
	static SimpleFacebookConfiguration configuration;
    private final LoginManager mLoginManager;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private LoginCallback mLoginCallback = new LoginCallback();

    class LoginCallback implements FacebookCallback<LoginResult> {

        private OnLoginListener mOnLoginListener;
        private boolean mIsAllPermissionsAtOnce;

        public void setLoginListener(OnLoginListener listener) {
            mOnLoginListener = listener;
        }

        public void setAllPermissionsAtOnce(boolean enabled) {
            mIsAllPermissionsAtOnce = enabled;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (mOnLoginListener != null) {
                mOnLoginListener.onLogin();
                // TODO - check what was denied..
            }
        }

        @Override
        public void onCancel() {
            mOnLoginListener.onFail("User canceled the permissions dialog");
        }

        @Override
        public void onError(FacebookException e) {
            // TODO - resolve the error
            mOnLoginListener.onException(e);
        }
    };

	public SessionManager(SimpleFacebookConfiguration configuration) {
		SessionManager.configuration = configuration;
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, mLoginCallback);
        mLoginManager.setDefaultAudience(configuration.getDefaultAudience());
        mLoginManager.setLoginBehavior(configuration.getLoginBehavior());
        mLoginCallback.setAllPermissionsAtOnce(configuration.isAllPermissionsAtOnce());
	}

	/**
	 * Login to Facebook
	 * 
	 * @param onLoginListener
	 */
	public void login(OnLoginListener onLoginListener) {
		if (onLoginListener == null) {
			Logger.logError(TAG, "OnLoginListener can't be null in -> 'login(OnLoginListener onLoginListener)' method.");
			return;
		}

        if (isLogin(true)) {
			Logger.logInfo(TAG, "You were already logged in before calling 'login()' method.");
			onLoginListener.onLogin();
			return;
		}

		if (hasPendingRequest()) {
			Logger.logWarning(TAG, "You are trying to login one more time, before finishing the previous login call");
			return;
		}

        mLoginCallback.setLoginListener(onLoginListener);
        Set<String> permissions = null;
        AccessToken accessToken = getAccessToken();
        if (accessToken != null) {
            permissions = getAccessToken().getPermissions();
        }
        List<String> readPermissions = configuration.getReadPermissions();
        List<String> publishPermissions = configuration.getPublishPermissions();
        if (permissions == null || !permissions.containsAll(readPermissions)) {
            mLoginManager.logInWithReadPermissions(activity, readPermissions);
        } else if (!permissions.containsAll(publishPermissions)) {
            mLoginManager.logInWithPublishPermissions(activity, publishPermissions);
        } else {
            onLoginListener.onLogin();
        }
	}

	/**
	 * Logout from Facebook
	 */
	public void logout(OnLogoutListener onLogoutListener) {
//		if (onLogoutListener == null) {
//			Logger.logError(TAG, "OnLogoutListener can't be null in -> 'logout(OnLogoutListener onLogoutListener)' method");
//			return;
//		}
//		Session session = getActiveSession();
//		if (session != null) {
//			if (session.isClosed()) {
//				Logger.logInfo(SessionManager.class, "You were already logged out before calling 'logout()' method");
//				onLogoutListener.onLogout();
//			} else {
//				mSessionStatusCallback.onLogoutListener = onLogoutListener;
//				session.closeAndClearTokenInformation();
//				session.removeCallback(mSessionStatusCallback);
//				onLogoutListener.onLogout();
//			}
//		} else {
//			onLogoutListener.onLogout();
//		}
	}

	/**
	 * Indicate if you are logged in or not.
	 * 
	 * @return <code>True</code> if you is logged in, otherwise return
	 *         <code>False</code>
	 */
	public boolean isLogin(boolean reopenIfPossible) {

        AccessToken accessToken = getAccessToken();
        if (accessToken == null) {
            return false;
        }

        return !accessToken.isExpired();

//		Session session = getActiveSession();
//		if (session == null) {
//			if (activity == null) {
//				return false;
//			}
//			session = new Session.Builder(activity.getApplicationContext()).setApplicationId(configuration.getAppId()).build();
//			Session.setActiveSession(session);
//		}
//		if (session.isOpened()) {
//			return true;
//		}
//		if (reopenIfPossible && canReopenSession(session)) {
//			reopenSession();
//			return true;
//		}

	}

	/**
	 * Get access token of open session
	 *
	 */
	public AccessToken getAccessToken() {
		return AccessToken.getCurrentAccessToken();
	}

	/**
	 * Get permissions that are accepted by user for current token
	 *
	 * @return the list of accepted permissions
	 */
	public List<String> getActiveSessionPermissions() {
//		return getActiveSession() != null ? getActiveSession().getPermissions() : new ArrayList<String>();
		return null;
	}

	/**
	 * Return true if there is no pending request like: asking for permissions..
	 *
	 * @return
	 */
	public boolean canMakeAdditionalRequest() {
//		Session session = Session.getActiveSession();
//		if (session != null) {
//			return !hasPendingRequest(session);
//		}
		return true;
	}

	/**
	 * Return true if current session contains all publish permissions.
	 *
	 * @return
	 */
	public boolean containsAllPublishPermissions() {
		if (getActiveSessionPermissions().containsAll(configuration.getPublishPermissions())) {
			return true;
		}
		return false;
	}

	/**
	 * Extend and ask user for PUBLISH permissions
	 *
	 */
	public void extendPublishPermissions() {
//		Session session = Session.getActiveSession();
//		if (hasPendingRequest(session)) {
//			Logger.logWarning(TAG, "You are trying to ask for publish permission one more time, before finishing the previous login call");
//		}
//		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(activity, configuration.getPublishPermissions());
//		session.addCallback(mSessionStatusCallback);
//		session.requestNewPublishPermissions(request);
	}

	/**
	 * Extend and ask user for READ permissions
	 */
	public void extendReadPermissions() {
//		Session session = Session.getActiveSession();
//		if (hasPendingRequest(session)) {
//			Logger.logWarning(TAG, "You are trying to ask for publish permission one more time, before finishing the previous login call");
//		}
//		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(activity, configuration.getReadPermissions());
//		session.addCallback(mSessionStatusCallback);
//		session.requestNewReadPermissions(request);
	}

	public void openSession(/*Session session, */boolean isRead) {
//		Session.OpenRequest request = new Session.OpenRequest(activity);
//		if (request != null) {
//			request.setDefaultAudience(configuration.getSessionDefaultAudience());
//			request.setLoginBehavior(configuration.getSessionLoginBehavior());
//
//			if (isRead) {
//				request.setPermissions(configuration.getReadPermissions());
//
//				/*
//				 * In case there are also PUBLISH permissions, then we would ask
//				 * for these permissions second time (after, user accepted the
//				 * read permissions)
//				 */
//				if (configuration.hasPublishPermissions() && configuration.isAllPermissionsAtOnce()) {
//					mSessionStatusCallback.setAskPublishPermissions(true);
//				}
//
//				// Open session with read permissions
//				session.openForRead(request);
//			} else {
//				request.setPermissions(configuration.getPublishPermissions());
//				session.openForPublish(request);
//			}
//		}
	}

	/**
	 * Requests any new permission in a runtime. <br>
	 * <br>
	 *
	 * Useful when you just want to request the action and won't be publishing
	 * at the time, but still need the updated <b>access token</b> with the
	 * permissions (possibly to pass back to your backend).
	 *
	 * <br>
	 * <b>Must be logged to use.</b>
	 *
	 * @param permissions
	 *            New permissions you want to have. This array can include READ
	 *            and PUBLISH permissions in the same time. Just ask what you
	 *            need.<br>
	 * @param onNewPermissionListener
	 *            The callback listener for the requesting new permission
	 *            action.
	 */
	public void requestNewPermissions(final Permission[] permissions, final OnNewPermissionsListener onNewPermissionListener) {
//		int flag = configuration.addNewPermissions(permissions);
//		flag |= getNotGrantedReadPermissions().size() > 0 ? 1 : 0;
//		flag |= getNotGrantedPublishPermissions().size() > 0 ? 2 : 0;
//		if (flag == 0) {
//			onNewPermissionListener.onFail("There is no new permissions in your request");
//			return;
//		}
//		mSessionStatusCallback.onLoginListener = new OnLoginListener() {
//
//			@Override
//			public void onFail(String reason) {
//				onNewPermissionListener.onFail(reason);
//			}
//
//			@Override
//			public void onException(Throwable throwable) {
//				onNewPermissionListener.onException(throwable);
//			}
//
//			@Override
//			public void onThinking() {
//				onNewPermissionListener.onThinking();
//			}
//
//			@Override
//			public void onNotAcceptingPermissions(Type type) {
//				onNewPermissionListener.onNotAcceptingPermissions(type);
//			}
//
//			@Override
//			public void onLogin() {
//				/*
//				 * Facebook has issue permissions dialog. If user presses (X) to
//				 * decline the dialog, then it behaves as expected, but if user
//				 * clicks on 'Not Now' button, then the response is possitive.
//				 * Thus, we need to check it ourself.
//				 */
//				List<Permission> declinedPermissions = getDeclinedPermissions(permissions, getActiveSessionPermissions());
//				if (declinedPermissions.size() == permissions.length) {
//					onFail("User canceled the permissions dialog");
//				} else {
//					onNewPermissionListener.onSuccess(getAccessToken(), declinedPermissions);
//				}
//			}
//		};
//
//		if (flag == 1 || flag == 3) {
//			extendReadPermissions();
//		} else if (flag == 2) {
//			extendPublishPermissions();
//		}
	}

	/**
	 * Return list with declined permissions
	 *
	 * @param permissions
	 *            - The new requested permissions by user
	 * @param activeSessionPermissions
	 *            - The already accepted permissions by user
	 * @return
	 */
	private List<Permission> getDeclinedPermissions(Permission[] permissions, List<String> activeSessionPermissions) {
		List<Permission> declinedPermissions = new ArrayList<Permission>();
		for (Permission permission : permissions) {
			if (!activeSessionPermissions.contains(permission.getValue())) {
				declinedPermissions.add(permission);
			}
		}
		return declinedPermissions;
	}

	/**
	 * Call this method only if session really needs to be reopened for read or
	 * for publish. <br>
	 * <br>
	 *
	 * <b>Important:</b><br>
	 * Any open method must be called at most once, and cannot be called after
	 * the Session is closed. Calling the method at an invalid time will result
	 * in {@link UnsupportedOperationException}.
	 */
	public void reopenSession() {
//		Session session = Session.getActiveSession();
//		if (session != null && session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
//			List<String> permissions = session.getPermissions();
//			List<String> publishPermissions = configuration.getPublishPermissions();
//			if (publishPermissions != null && publishPermissions.size() > 0 && permissions.containsAll(publishPermissions)) {
//				openSession(session, false);
//			} else if (permissions.containsAll(configuration.getReadPermissions())) {
//				openSession(session, true);
//			}
//		}
	}

	public void trackFacebookDialogPendingCall(/*PendingCall pendingCall, FacebookDialog.Callback callback*/) {
//		mFacebookDialogCallback = callback;
//		uiLifecycleHelper.trackPendingDialogCall(pendingCall);
	}

	public void untrackPendingCall() {
//		mFacebookDialogCallback = null;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Check if we already have Active session. If such exists, then return it,
	 * otherwise create and set new Active session. <br>
	 * <br>
	 *
	 * <b>Note:</b><br>
	 * <li>'Active' session doesn't meant that it's ready for running requests
	 * on facebook side. For being able to run requests we need this session to
	 * be 'Open'.</li> <li>You can't create a session if the activity/context
	 * hasn't been initialized This is now possible because the library can be
	 * started without context.</li><br>
	 *
	 * @return Active session or <code>null</code> if activity wasn't
	 *         initialized in SimpleFacebook class.
	 */
//	private Session getOrCreateActiveSession() {
//		if (activity == null) {
//			Logger.logError(TAG, "You must initialize the SimpleFacebook instance with you current Activity.");
//			return null;
//		}
//
//		if (getActiveSession() == null || getActiveSession().isClosed()) {
//			Session session = new Session.Builder(activity.getApplicationContext()).setApplicationId(configuration.getAppId()).build();
//			Session.setActiveSession(session);
//		}
//		return getActiveSession();
//		return null;
//	}

	/**
	 * @return <code>True</code> if is possible to relive and reopen the current
	 *         active session. Otherwise return <code>False</code>.
	 */
	private boolean canReopenSession(/*Session session*/) {
//		if (activity == null) {
//			Logger.logError(TAG, "You must initialize the SimpleFacebook instance with you current Activity.");
//			return false;
//		}
//
//		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
//			List<String> permissions = getActiveSessionPermissions();
//			if (permissions.containsAll(configuration.getReadPermissions())) {
//				return true;
//			}
//		}
		return false;
	}

	private boolean hasPendingRequest() {
		try {
			Field f = mLoginManager.getClass().getDeclaredField("pendingLoginRequest");
			f.setAccessible(true);
            Object request = f.get(mLoginManager);
            if (request != null) {
				return true;
			}
		} catch (Exception e) {
			// do nothing
		}
		return false;
	}

	private List<String> getNotGrantedReadPermissions() {
		List<String> grantedPermissions = getActiveSessionPermissions();
		List<String> readPermissions = new ArrayList<String>(configuration.getReadPermissions());
		readPermissions.removeAll(grantedPermissions);
		return readPermissions;
	}

	private List<String> getNotGrantedPublishPermissions() {
		List<String> grantedPermissions = getActiveSessionPermissions();
		List<String> publishPermissions = new ArrayList<String>(configuration.getPublishPermissions());
		publishPermissions.removeAll(grantedPermissions);
		return publishPermissions;
	}

	public boolean isAllPermissionsGranted() {
		if (getNotGrantedReadPermissions().size() > 0 || getNotGrantedPublishPermissions().size() > 0) {
			return false;
		}
		return true;
	}

//	public class SessionStatusCallback implements Session.StatusCallback {
//		private boolean askPublishPermissions = false;
//		private boolean doOnLogin = false;
//		private OnReopenSessionListener onReopenSessionListener = null;
//		OnLoginListener onLoginListener = null;
//		OnLogoutListener onLogoutListener = null;
//
//		public void setOnReopenSessionListener(OnReopenSessionListener onReopenSessionListener) {
//			this.onReopenSessionListener = onReopenSessionListener;
//		}
//
//		@Override
//		public void call(Session session, SessionState state, Exception exception) {
//			List<String> permissions = getActiveSessionPermissions();
//			if (exception != null) {
//				if (exception instanceof FacebookOperationCanceledException && !SessionState.OPENED_TOKEN_UPDATED.equals(state)) {
//					if (permissions.size() == 0) {
//						notAcceptedPermission(Type.READ);
//					} else {
//						notAcceptedPermission(Type.PUBLISH);
//					}
//				} else {
//					if (onLoginListener != null) {
//						onLoginListener.onException(exception);
//					}
//				}
//				return;
//			}
//
//			switch (state) {
//			case CLOSED:
//				if (onLogoutListener != null) {
//					onLogoutListener.onLogout();
//				}
//				break;
//
//			case CLOSED_LOGIN_FAILED:
//				break;
//
//			case CREATED:
//				break;
//
//			case CREATED_TOKEN_LOADED:
//				break;
//
//			case OPENING:
//				if (onLoginListener != null) {
//					onLoginListener.onThinking();
//				}
//				break;
//
//			case OPENED:
//
//				/*
//				 * Check if we came from publishing actions where we ask again
//				 * for publish permissions
//				 */
//				if (onReopenSessionListener != null) {
//					onReopenSessionListener.onNotAcceptingPermissions(Type.PUBLISH);
//					onReopenSessionListener = null;
//				}
//
//				/*
//				 * Check if WRITE permissions were also defined in the
//				 * configuration. If so, then ask in another dialog for WRITE
//				 * permissions.
//				 */
//				else if (askPublishPermissions && session.getState().equals(SessionState.OPENED)) {
//					if (doOnLogin) {
//						/*
//						 * If user didn't accept the publish permissions, we
//						 * still want to notify about complete
//						 */
//						doOnLogin = false;
//						onLoginListener.onLogin();
//					} else {
//						doOnLogin = true;
//						extendPublishPermissions();
//						askPublishPermissions = false;
//					}
//				} else {
//					if (onLoginListener != null) {
//						onLoginListener.onLogin();
//					}
//				}
//				break;
//
//			case OPENED_TOKEN_UPDATED:
//
//				/*
//				 * Check if came from publishing actions and we need to re-ask
//				 * for publish permissions
//				 */
//				if (onReopenSessionListener != null) {
//					if ((exception != null && exception instanceof FacebookOperationCanceledException) || (!containsAllPublishPermissions())) {
//						onReopenSessionListener.onNotAcceptingPermissions(Type.PUBLISH);
//					} else {
//						onReopenSessionListener.onSuccess();
//					}
//					onReopenSessionListener = null;
//				} else if (doOnLogin) {
//					doOnLogin = false;
//
//					if (onLoginListener != null) {
//						onLoginListener.onLogin();
//					}
//				} else if (askPublishPermissions) {
//					doOnLogin = true;
//					extendPublishPermissions();
//					askPublishPermissions = false;
//				} else {
//					if (onLoginListener != null) {
//						onLoginListener.onLogin();
//					}
//				}
//
//				break;
//
//			default:
//				break;
//			}
//		}
//
//		/**
//		 * If we want to open another dialog with publish permissions just after
//		 * showing read permissions, then this method should be called
//		 */
//		public void setAskPublishPermissions(boolean ask) {
//			askPublishPermissions = ask;
//		}
//
//		private void notAcceptedPermission(Type type) {
//			if (onLoginListener != null) {
//				onLoginListener.onNotAcceptingPermissions(type);
//			}
//		}
//	}

}