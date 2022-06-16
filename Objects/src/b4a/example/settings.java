package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class settings extends Activity implements B4AActivity{
	public static settings mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.settings");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (settings).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.settings");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.settings", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (settings) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (settings) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return settings.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (settings) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (settings) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            settings mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (settings) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public static b4a.example.httpjob _requestprofile = null;
public static String _url = "";
public anywheresoftware.b4a.objects.EditTextWrapper _txtid = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtlastname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtemail = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtgender = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtcell = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtaddr = null;
public anywheresoftware.b4a.objects.EditTextWrapper _txtpassword = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsave = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncancel = null;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.dashboard _dashboard = null;
public b4a.example.cart _cart = null;
public b4a.example.scancard _scancard = null;
public b4a.example.httputils2service _httputils2service = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 34;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 36;BA.debugLine="URL = $\"http://${Main.serverIPAddress}/SmartBaske";
_url = ("http://"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._main._serveripaddress /*String*/ ))+"/SmartBasket/WebAPI/user.php");
 //BA.debugLineNum = 39;BA.debugLine="requestProfile.Initialize(\"requestProfile\", Me)";
_requestprofile._initialize /*String*/ (processBA,"requestProfile",settings.getObject());
 //BA.debugLineNum = 41;BA.debugLine="Activity.LoadLayout(\"settings\")";
mostCurrent._activity.LoadLayout("settings",mostCurrent.activityBA);
 //BA.debugLineNum = 44;BA.debugLine="InitUserProfile";
_inituserprofile();
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _btncancel_click() throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Private Sub btnCancel_Click";
 //BA.debugLineNum = 92;BA.debugLine="CallSubDelayed(dashboard, Null)";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(mostCurrent._dashboard.getObject()),BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 93;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static void  _btnsave_click() throws Exception{
ResumableSub_btnSave_Click rsub = new ResumableSub_btnSave_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnSave_Click extends BA.ResumableSub {
public ResumableSub_btnSave_Click(b4a.example.settings parent) {
this.parent = parent;
}
b4a.example.settings parent;
String _name = "";
String _surname = "";
String _email = "";
String _cellnum = "";
String _addr = "";
String _pass = "";
String _poststr = "";
b4a.example.httpjob _job = null;
String _response = "";
Object _dialogres = null;
int _result = 0;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
try {

        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 58;BA.debugLine="Dim name As String = txtName.Text";
_name = parent.mostCurrent._txtname.getText();
 //BA.debugLineNum = 59;BA.debugLine="Dim surname As String = txtLastname.Text";
_surname = parent.mostCurrent._txtlastname.getText();
 //BA.debugLineNum = 60;BA.debugLine="Dim email As String = txtEmail.Text";
_email = parent.mostCurrent._txtemail.getText();
 //BA.debugLineNum = 61;BA.debugLine="Dim cellNum As String = txtCell.Text";
_cellnum = parent.mostCurrent._txtcell.getText();
 //BA.debugLineNum = 62;BA.debugLine="Dim addr As String = txtAddr.Text";
_addr = parent.mostCurrent._txtaddr.getText();
 //BA.debugLineNum = 63;BA.debugLine="Dim pass As String = txtPassword.Text";
_pass = parent.mostCurrent._txtpassword.getText();
 //BA.debugLineNum = 65;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 20;
this.catchState = 19;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 19;
 //BA.debugLineNum = 66;BA.debugLine="Dim postStr As String = $\"method=UPDATE_USER&fir";
_poststr = ("method=UPDATE_USER&firstName="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_name))+"&lastName="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_surname))+"&email="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_email))+"&cellNum="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_cellnum))+"&addr="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_addr))+"&password="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_pass))+"");
 //BA.debugLineNum = 67;BA.debugLine="requestProfile.PostString(URL, postStr)";
parent._requestprofile._poststring /*String*/ (parent._url,_poststr);
 //BA.debugLineNum = 68;BA.debugLine="Wait For (requestProfile) JobDone(job As HttpJob";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(parent._requestprofile));
this.state = 21;
return;
case 21:
//C
this.state = 4;
_job = (b4a.example.httpjob) result[0];
;
 //BA.debugLineNum = 69;BA.debugLine="If job.Success Then";
if (true) break;

case 4:
//if
this.state = 17;
if (_job._success /*boolean*/ ) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 70;BA.debugLine="Dim response As String = job.GetString";
_response = _job._getstring /*String*/ ();
 //BA.debugLineNum = 71;BA.debugLine="If response = \"UpdateSuccess\" Then";
if (true) break;

case 7:
//if
this.state = 16;
if ((_response).equals("UpdateSuccess")) { 
this.state = 9;
}else if((_response).equals("404")) { 
this.state = 15;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 72;BA.debugLine="Dim dialogRes As Object = xui.Msgbox2Async(\"Pr";
_dialogres = parent._xui.Msgbox2Async(processBA,BA.ObjectToCharSequence("Profile updated successfully"+anywheresoftware.b4a.keywords.Common.CRLF+"Would you like to revert to the dashboard?"),BA.ObjectToCharSequence("Update"),"Yes","Cancel","No",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)));
 //BA.debugLineNum = 75;BA.debugLine="Wait For (dialogRes) Msgbox_Result (Result As";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, _dialogres);
this.state = 22;
return;
case 22:
//C
this.state = 10;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 76;BA.debugLine="If Result = xui.DialogResponse_Positive Then";
if (true) break;

case 10:
//if
this.state = 13;
if (_result==parent._xui.DialogResponse_Positive) { 
this.state = 12;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 77;BA.debugLine="CallSubDelayed(dashboard, Null)";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(parent.mostCurrent._dashboard.getObject()),BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 78;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 13:
//C
this.state = 16;
;
 if (true) break;

case 15:
//C
this.state = 16;
 //BA.debugLineNum = 81;BA.debugLine="ToastMessageShow(\"Could not update profile\", T";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Could not update profile"),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 16:
//C
this.state = 17;
;
 if (true) break;

case 17:
//C
this.state = 20;
;
 if (true) break;

case 19:
//C
this.state = 20;
this.catchState = 0;
 //BA.debugLineNum = 85;BA.debugLine="xui.MsgboxAsync(LastException.Message, \"Exceptio";
parent._xui.MsgboxAsync(processBA,BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),BA.ObjectToCharSequence("Exception"));
 if (true) break;
if (true) break;

case 20:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
if (true) break;
}} 
       catch (Exception e0) {
			
if (catchState == 0)
    throw e0;
else {
    state = catchState;
processBA.setLastException(e0);}
            }
        }
    }
}
public static void  _jobdone(b4a.example.httpjob _job) throws Exception{
}
public static void  _msgbox_result(int _result) throws Exception{
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 20;BA.debugLine="Private txtID As EditText";
mostCurrent._txtid = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private txtName As EditText";
mostCurrent._txtname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private txtLastname As EditText";
mostCurrent._txtlastname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private txtEmail As EditText";
mostCurrent._txtemail = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private txtGender As EditText";
mostCurrent._txtgender = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private txtCell As EditText";
mostCurrent._txtcell = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private txtAddr As EditText";
mostCurrent._txtaddr = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private txtPassword As EditText";
mostCurrent._txtpassword = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private btnSave As Button";
mostCurrent._btnsave = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private btnCancel As Button";
mostCurrent._btncancel = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
public static String  _inituserprofile() throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Private Sub InitUserProfile()";
 //BA.debugLineNum = 99;BA.debugLine="txtID.Text = Main.userDetails(1).As(String)";
mostCurrent._txtid.setText(BA.ObjectToCharSequence((mostCurrent._main._userdetails /*String[]*/ [(int) (1)])));
 //BA.debugLineNum = 100;BA.debugLine="txtName.Text = Main.userDetails(2).As(String)";
mostCurrent._txtname.setText(BA.ObjectToCharSequence((mostCurrent._main._userdetails /*String[]*/ [(int) (2)])));
 //BA.debugLineNum = 101;BA.debugLine="txtLastname.Text = Main.userDetails(3)";
mostCurrent._txtlastname.setText(BA.ObjectToCharSequence(mostCurrent._main._userdetails /*String[]*/ [(int) (3)]));
 //BA.debugLineNum = 102;BA.debugLine="txtGender.Text = Main.userDetails(4)";
mostCurrent._txtgender.setText(BA.ObjectToCharSequence(mostCurrent._main._userdetails /*String[]*/ [(int) (4)]));
 //BA.debugLineNum = 103;BA.debugLine="txtCell.Text = Main.userDetails(5)";
mostCurrent._txtcell.setText(BA.ObjectToCharSequence(mostCurrent._main._userdetails /*String[]*/ [(int) (5)]));
 //BA.debugLineNum = 104;BA.debugLine="txtAddr.Text = Main.userDetails(6)";
mostCurrent._txtaddr.setText(BA.ObjectToCharSequence(mostCurrent._main._userdetails /*String[]*/ [(int) (6)]));
 //BA.debugLineNum = 105;BA.debugLine="txtEmail.Text = Main.userDetails(7)";
mostCurrent._txtemail.setText(BA.ObjectToCharSequence(mostCurrent._main._userdetails /*String[]*/ [(int) (7)]));
 //BA.debugLineNum = 106;BA.debugLine="txtPassword.Text = Main.userDetails(8)";
mostCurrent._txtpassword.setText(BA.ObjectToCharSequence(mostCurrent._main._userdetails /*String[]*/ [(int) (8)]));
 //BA.debugLineNum = 108;BA.debugLine="txtName.RequestFocus";
mostCurrent._txtname.RequestFocus();
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 11;BA.debugLine="Private requestProfile As HttpJob";
_requestprofile = new b4a.example.httpjob();
 //BA.debugLineNum = 12;BA.debugLine="Private URL As String";
_url = "";
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
}
