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

public class cart extends Activity implements B4AActivity{
	public static cart mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.cart");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (cart).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.cart");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.cart", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (cart) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (cart) Resume **");
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
		return cart.class;
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
            BA.LogInfo("** Activity (cart) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (cart) Pause event (activity is not paused). **");
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
            cart mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (cart) Resume **");
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
public static anywheresoftware.b4a.objects.Timer _tmr = null;
public static b4a.example.httpjob _requestcart = null;
public static b4a.example.httpjob _requesttotal = null;
public static b4a.example.httpjob _deletecart = null;
public static b4a.example.httpjob _requestcheckout = null;
public static anywheresoftware.b4a.audio.Beeper _audio = null;
public static double _totalamount = 0;
public static String _url = "";
public anywheresoftware.b4a.objects.collections.List _itemnames = null;
public anywheresoftware.b4a.objects.collections.List _itemprices = null;
public static int _csize = 0;
public static int _psize = 0;
public anywheresoftware.b4a.objects.ListViewWrapper _lstitems = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncheckout = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnabort = null;
public static String _payload = "";
public static double _totalamt = 0;
public static boolean _iscartempty = false;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.dashboard _dashboard = null;
public b4a.example.scancard _scancard = null;
public b4a.example.settings _settings = null;
public b4a.example.httputils2service _httputils2service = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static void  _abortcart() throws Exception{
ResumableSub_AbortCart rsub = new ResumableSub_AbortCart(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_AbortCart extends BA.ResumableSub {
public ResumableSub_AbortCart(b4a.example.cart parent) {
this.parent = parent;
}
b4a.example.cart parent;
b4a.example.httpjob _job = null;

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
 //BA.debugLineNum = 146;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 10;
this.catchState = 9;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 9;
 //BA.debugLineNum = 147;BA.debugLine="deleteCart.PostString(URL, \"method=ABORT_CART\")";
parent._deletecart._poststring /*String*/ (parent.mostCurrent._url,"method=ABORT_CART");
 //BA.debugLineNum = 148;BA.debugLine="Wait For (deleteCart) JobDone(job As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(parent._deletecart));
this.state = 11;
return;
case 11:
//C
this.state = 4;
_job = (b4a.example.httpjob) result[0];
;
 //BA.debugLineNum = 149;BA.debugLine="If job.Success Then";
if (true) break;

case 4:
//if
this.state = 7;
if (_job._success /*boolean*/ ) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 if (true) break;

case 7:
//C
this.state = 10;
;
 if (true) break;

case 9:
//C
this.state = 10;
this.catchState = 0;
 //BA.debugLineNum = 153;BA.debugLine="xui.MsgboxAsync(LastException.Message, \"Exceptio";
parent._xui.MsgboxAsync(processBA,BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),BA.ObjectToCharSequence("Exception"));
 if (true) break;
if (true) break;

case 10:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
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
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 46;BA.debugLine="Activity.LoadLayout(\"cart\")";
mostCurrent._activity.LoadLayout("cart",mostCurrent.activityBA);
 //BA.debugLineNum = 48;BA.debugLine="itemNames.Initialize";
mostCurrent._itemnames.Initialize();
 //BA.debugLineNum = 49;BA.debugLine="itemPrices.Initialize";
mostCurrent._itemprices.Initialize();
 //BA.debugLineNum = 51;BA.debugLine="requestCart.Initialize(\"requestCart\", Me)";
_requestcart._initialize /*String*/ (processBA,"requestCart",cart.getObject());
 //BA.debugLineNum = 52;BA.debugLine="requestTotal.Initialize(\"requestTotal\", Me)";
_requesttotal._initialize /*String*/ (processBA,"requestTotal",cart.getObject());
 //BA.debugLineNum = 53;BA.debugLine="deleteCart.Initialize(\"deleteCart\", Me)";
_deletecart._initialize /*String*/ (processBA,"deleteCart",cart.getObject());
 //BA.debugLineNum = 54;BA.debugLine="requestCheckout.Initialize(\"requestCheckout\", Me)";
_requestcheckout._initialize /*String*/ (processBA,"requestCheckout",cart.getObject());
 //BA.debugLineNum = 56;BA.debugLine="tmr.Initialize(\"tmr\", 1000)";
_tmr.Initialize(processBA,"tmr",(long) (1000));
 //BA.debugLineNum = 57;BA.debugLine="tmr.Enabled = True";
_tmr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 59;BA.debugLine="audio.Initialize(50, 500)";
_audio.Initialize((int) (50),(int) (500));
 //BA.debugLineNum = 61;BA.debugLine="cSize = lstItems.Size";
_csize = mostCurrent._lstitems.getSize();
 //BA.debugLineNum = 62;BA.debugLine="pSize = cSize";
_psize = _csize;
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 70;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _addcart(String _message) throws Exception{
anywheresoftware.b4a.objects.collections.List _cartlist = null;
double _price = 0;
int _n = 0;
String[] _item = null;
 //BA.debugLineNum = 79;BA.debugLine="Private Sub AddCart(message As String)";
 //BA.debugLineNum = 81;BA.debugLine="Dim cartList As List";
_cartlist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 82;BA.debugLine="Dim price As Double";
_price = 0;
 //BA.debugLineNum = 84;BA.debugLine="lstItems.Clear()";
mostCurrent._lstitems.Clear();
 //BA.debugLineNum = 85;BA.debugLine="itemNames.Clear()";
mostCurrent._itemnames.Clear();
 //BA.debugLineNum = 86;BA.debugLine="itemPrices.Clear()";
mostCurrent._itemprices.Clear();
 //BA.debugLineNum = 88;BA.debugLine="Dim message As String = message.Trim()";
_message = _message.trim();
 //BA.debugLineNum = 90;BA.debugLine="If(message.EqualsIgnoreCase(\"No data\")) Then";
if ((_message.equalsIgnoreCase("No data"))) { 
 //BA.debugLineNum = 91;BA.debugLine="ToastMessageShow(\"Cart is empty!\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Cart is empty!"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 92;BA.debugLine="btnAbort.Enabled = False";
mostCurrent._btnabort.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 94;BA.debugLine="cartList = Regex.Split(\"-\", message)";
_cartlist = anywheresoftware.b4a.keywords.Common.ArrayToList(anywheresoftware.b4a.keywords.Common.Regex.Split("-",_message));
 //BA.debugLineNum = 96;BA.debugLine="For n = 0 To cartList.Size - 1";
{
final int step12 = 1;
final int limit12 = (int) (_cartlist.getSize()-1);
_n = (int) (0) ;
for (;_n <= limit12 ;_n = _n + step12 ) {
 //BA.debugLineNum = 97;BA.debugLine="Dim item(2) As String = Regex.Split( \"&\", cartL";
_item = anywheresoftware.b4a.keywords.Common.Regex.Split("&",(BA.ObjectToString(_cartlist.Get(_n))));
 //BA.debugLineNum = 98;BA.debugLine="itemNames.Add( item(0).As(String) )";
mostCurrent._itemnames.Add((Object)((_item[(int) (0)])));
 //BA.debugLineNum = 99;BA.debugLine="price = item(1).As(Double)";
_price = ((double)(Double.parseDouble(_item[(int) (1)])));
 //BA.debugLineNum = 100;BA.debugLine="itemPrices.Add(\"R \" & price.As(String))";
mostCurrent._itemprices.Add((Object)("R "+(BA.NumberToString(_price))));
 //BA.debugLineNum = 102;BA.debugLine="lstItems.AddTwoLines2(itemNames.Get(n), itemPri";
mostCurrent._lstitems.AddTwoLines2(BA.ObjectToCharSequence(mostCurrent._itemnames.Get(_n)),BA.ObjectToCharSequence(mostCurrent._itemprices.Get(_n)),(Object)(_n));
 }
};
 //BA.debugLineNum = 105;BA.debugLine="btnAbort.Color = Colors.Red";
mostCurrent._btnabort.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 106;BA.debugLine="btnCheckout.Color = Colors.Green";
mostCurrent._btncheckout.setColor(anywheresoftware.b4a.keywords.Common.Colors.Green);
 //BA.debugLineNum = 108;BA.debugLine="btnAbort.Enabled = True";
mostCurrent._btnabort.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
public static void  _btnabort_click() throws Exception{
ResumableSub_btnAbort_Click rsub = new ResumableSub_btnAbort_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnAbort_Click extends BA.ResumableSub {
public ResumableSub_btnAbort_Click(b4a.example.cart parent) {
this.parent = parent;
}
b4a.example.cart parent;
Object _sf = null;
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
 //BA.debugLineNum = 133;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 10;
this.catchState = 9;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 9;
 //BA.debugLineNum = 134;BA.debugLine="Dim sf As Object = xui.Msgbox2Async(\"Are you sur";
_sf = parent._xui.Msgbox2Async(processBA,BA.ObjectToCharSequence("Are you sure you want to abort cart?"),BA.ObjectToCharSequence("Abort"),"Yes","Cancel","No",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)));
 //BA.debugLineNum = 135;BA.debugLine="Wait For (sf) Msgbox_Result (Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, _sf);
this.state = 11;
return;
case 11:
//C
this.state = 4;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 136;BA.debugLine="If Result = xui.DialogResponse_Positive Then";
if (true) break;

case 4:
//if
this.state = 7;
if (_result==parent._xui.DialogResponse_Positive) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 137;BA.debugLine="AbortCart";
_abortcart();
 if (true) break;

case 7:
//C
this.state = 10;
;
 if (true) break;

case 9:
//C
this.state = 10;
this.catchState = 0;
 //BA.debugLineNum = 140;BA.debugLine="xui.MsgboxAsync(LastException.Message, \"Exceptio";
parent._xui.MsgboxAsync(processBA,BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),BA.ObjectToCharSequence("Exception"));
 if (true) break;
if (true) break;

case 10:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 142;BA.debugLine="End Sub";
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
public static void  _msgbox_result(int _result) throws Exception{
}
public static String  _btnback_click() throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Private Sub btnBack_Click";
 //BA.debugLineNum = 75;BA.debugLine="CallSubDelayed(dashboard, Null)";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(mostCurrent._dashboard.getObject()),BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 76;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static void  _btncheckout_click() throws Exception{
ResumableSub_btnCheckout_Click rsub = new ResumableSub_btnCheckout_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnCheckout_Click extends BA.ResumableSub {
public ResumableSub_btnCheckout_Click(b4a.example.cart parent) {
this.parent = parent;
}
b4a.example.cart parent;
b4a.example.httpjob _job = null;
String _res = "";

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
 //BA.debugLineNum = 190;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 14;
this.catchState = 13;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 13;
 //BA.debugLineNum = 191;BA.debugLine="requestCheckout.PostString(URL, $\"method=CHECKOU";
parent._requestcheckout._poststring /*String*/ (parent.mostCurrent._url,("method=CHECKOUT&total="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent._totalamount))+""));
 //BA.debugLineNum = 192;BA.debugLine="Wait For (requestCheckout) JobDone(job As HttpJo";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(parent._requestcheckout));
this.state = 15;
return;
case 15:
//C
this.state = 4;
_job = (b4a.example.httpjob) result[0];
;
 //BA.debugLineNum = 193;BA.debugLine="If job.Success Then";
if (true) break;

case 4:
//if
this.state = 11;
if (_job._success /*boolean*/ ) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 194;BA.debugLine="Dim res As String = job.GetString.Trim";
_res = _job._getstring /*String*/ ().trim();
 //BA.debugLineNum = 195;BA.debugLine="If( res = \"CheckoutSuccess\") Then";
if (true) break;

case 7:
//if
this.state = 10;
if (((_res).equals("CheckoutSuccess"))) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 196;BA.debugLine="CallSubDelayed(scancard, Null)";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(parent.mostCurrent._scancard.getObject()),BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 197;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 10:
//C
this.state = 11;
;
 if (true) break;

case 11:
//C
this.state = 14;
;
 if (true) break;

case 13:
//C
this.state = 14;
this.catchState = 0;
 //BA.debugLineNum = 201;BA.debugLine="xui.MsgboxAsync(LastException.Message, \"Exceptio";
parent._xui.MsgboxAsync(processBA,BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage()),BA.ObjectToCharSequence("Exception"));
 if (true) break;
if (true) break;

case 14:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 204;BA.debugLine="End Sub";
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
public static void  _getcart() throws Exception{
ResumableSub_GetCart rsub = new ResumableSub_GetCart(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_GetCart extends BA.ResumableSub {
public ResumableSub_GetCart(b4a.example.cart parent) {
this.parent = parent;
}
b4a.example.cart parent;
b4a.example.httpjob _job = null;
String _res = "";

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
 //BA.debugLineNum = 159;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 10;
this.catchState = 9;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 9;
 //BA.debugLineNum = 160;BA.debugLine="requestCart.PostString(URL, \"method=GET_CART\")";
parent._requestcart._poststring /*String*/ (parent.mostCurrent._url,"method=GET_CART");
 //BA.debugLineNum = 161;BA.debugLine="Wait For (requestCart) JobDone(job As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(parent._requestcart));
this.state = 11;
return;
case 11:
//C
this.state = 4;
_job = (b4a.example.httpjob) result[0];
;
 //BA.debugLineNum = 162;BA.debugLine="If job.Success Then";
if (true) break;

case 4:
//if
this.state = 7;
if (_job._success /*boolean*/ ) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 163;BA.debugLine="Dim res As String = job.GetString";
_res = _job._getstring /*String*/ ();
 //BA.debugLineNum = 164;BA.debugLine="pSize = lstItems.Size";
parent._psize = parent.mostCurrent._lstitems.getSize();
 //BA.debugLineNum = 165;BA.debugLine="AddCart(res)";
_addcart(_res);
 //BA.debugLineNum = 166;BA.debugLine="cSize = lstItems.Size";
parent._csize = parent.mostCurrent._lstitems.getSize();
 if (true) break;

case 7:
//C
this.state = 10;
;
 if (true) break;

case 9:
//C
this.state = 10;
this.catchState = 0;
 //BA.debugLineNum = 169;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("710551307",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 10:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 171;BA.debugLine="End Sub";
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
public static String  _gettotal(String _res) throws Exception{
double _amt = 0;
 //BA.debugLineNum = 112;BA.debugLine="Sub GetTotal(res As String)";
 //BA.debugLineNum = 114;BA.debugLine="Dim amt As Double = res.As(Double)";
_amt = ((double)(Double.parseDouble(_res)));
 //BA.debugLineNum = 115;BA.debugLine="totalAmount = Round2(amt, 2)";
_totalamount = anywheresoftware.b4a.keywords.Common.Round2(_amt,(int) (2));
 //BA.debugLineNum = 116;BA.debugLine="btnCheckout.Text = \"Checkout R \" & totalAmount.As";
mostCurrent._btncheckout.setText(BA.ObjectToCharSequence("Checkout R "+(BA.NumberToString(_totalamount))));
 //BA.debugLineNum = 118;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 26;BA.debugLine="Private URL As String = $\"http://${Main.serverIPA";
mostCurrent._url = ("http://"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._main._serveripaddress /*String*/ ))+"/SmartBasket/WebAPI/cart.php");
 //BA.debugLineNum = 28;BA.debugLine="Private itemNames As List";
mostCurrent._itemnames = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 29;BA.debugLine="Private itemPrices As List";
mostCurrent._itemprices = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 31;BA.debugLine="Private cSize As Int";
_csize = 0;
 //BA.debugLineNum = 32;BA.debugLine="Private pSize As Int";
_psize = 0;
 //BA.debugLineNum = 34;BA.debugLine="Private lstItems As ListView";
mostCurrent._lstitems = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private btnCheckout As Button";
mostCurrent._btncheckout = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private btnAbort As Button";
mostCurrent._btnabort = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private payLoad As String";
mostCurrent._payload = "";
 //BA.debugLineNum = 39;BA.debugLine="Private totalAmt As Double = 0";
_totalamt = 0;
 //BA.debugLineNum = 40;BA.debugLine="Private isCartEmpty As Boolean = True";
_iscartempty = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 11;BA.debugLine="Private tmr As Timer";
_tmr = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 12;BA.debugLine="Private requestCart As HttpJob";
_requestcart = new b4a.example.httpjob();
 //BA.debugLineNum = 13;BA.debugLine="Private requestTotal As HttpJob";
_requesttotal = new b4a.example.httpjob();
 //BA.debugLineNum = 14;BA.debugLine="Private deleteCart As HttpJob";
_deletecart = new b4a.example.httpjob();
 //BA.debugLineNum = 15;BA.debugLine="Private requestCheckout As HttpJob";
_requestcheckout = new b4a.example.httpjob();
 //BA.debugLineNum = 17;BA.debugLine="Private audio As Beeper";
_audio = new anywheresoftware.b4a.audio.Beeper();
 //BA.debugLineNum = 19;BA.debugLine="Private totalAmount As Double";
_totalamount = 0;
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
public static void  _requestotal() throws Exception{
ResumableSub_RequesTotal rsub = new ResumableSub_RequesTotal(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_RequesTotal extends BA.ResumableSub {
public ResumableSub_RequesTotal(b4a.example.cart parent) {
this.parent = parent;
}
b4a.example.cart parent;
b4a.example.httpjob _j = null;
String _res = "";

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
 //BA.debugLineNum = 175;BA.debugLine="Try";
if (true) break;

case 1:
//try
this.state = 10;
this.catchState = 9;
this.state = 3;
if (true) break;

case 3:
//C
this.state = 4;
this.catchState = 9;
 //BA.debugLineNum = 176;BA.debugLine="requestTotal.PostString(URL, \"method=GET_TOTAL\")";
parent._requesttotal._poststring /*String*/ (parent.mostCurrent._url,"method=GET_TOTAL");
 //BA.debugLineNum = 177;BA.debugLine="Wait For (requestTotal) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(parent._requesttotal));
this.state = 11;
return;
case 11:
//C
this.state = 4;
_j = (b4a.example.httpjob) result[0];
;
 //BA.debugLineNum = 178;BA.debugLine="If j.Success Then";
if (true) break;

case 4:
//if
this.state = 7;
if (_j._success /*boolean*/ ) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 179;BA.debugLine="Dim res As String = j.GetString";
_res = _j._getstring /*String*/ ();
 //BA.debugLineNum = 180;BA.debugLine="GetTotal(res)";
_gettotal(_res);
 if (true) break;

case 7:
//C
this.state = 10;
;
 if (true) break;

case 9:
//C
this.state = 10;
this.catchState = 0;
 //BA.debugLineNum = 183;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("711337738",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 if (true) break;
if (true) break;

case 10:
//C
this.state = -1;
this.catchState = 0;
;
 //BA.debugLineNum = 186;BA.debugLine="End Sub";
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
public static String  _tmr_tick() throws Exception{
 //BA.debugLineNum = 120;BA.debugLine="Sub tmr_Tick";
 //BA.debugLineNum = 122;BA.debugLine="GetCart";
_getcart();
 //BA.debugLineNum = 123;BA.debugLine="If(pSize <> cSize) Then";
if ((_psize!=_csize)) { 
 //BA.debugLineNum = 124;BA.debugLine="audio.Beep";
_audio.Beep();
 //BA.debugLineNum = 125;BA.debugLine="pSize = cSize";
_psize = _csize;
 };
 //BA.debugLineNum = 127;BA.debugLine="RequesTotal";
_requestotal();
 //BA.debugLineNum = 129;BA.debugLine="End Sub";
return "";
}
}
