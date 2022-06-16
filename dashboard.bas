B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=11.5
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: True
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Private request As HttpJob
	Private xui As XUI
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private btnCart As Button
	Private btnProfile As Button
	Private btnLogout As Button
	Private lblDashboard As Label
	
	Private URL As String = $"http://${Main.serverIPAddress}/SmartBasket/WebAPI/user.php"$
	Private logoutSucess As Boolean = False
	
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("dashboard")
	
	Try
		'Main.request.Initialize("readdata", Me)
		lblDashboard.Text = $"Hello ${Main.userDetails(2) & " " & Main.userDetails(3)}"$
		request.Initialize("readdata", Me)
	Catch
		xui.MsgboxAsync(LastException.Message, "Exception")
	End Try
	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Private Sub btnCart_Click
	CallSubDelayed(cart, Null)
	Activity.Finish
End Sub

Private Sub btnProfile_Click
	CallSubDelayed(settings, Null)
	Activity.Finish
End Sub

Private Sub btnLogout_Click
	
	request.PostString(URL, "method=LOGOUT")
	' Wait for server response
	Sleep(500)
	
	If(logoutSucess) Then
		CallSubDelayed(Main, Null)
		Activity.Finish
	End If
End Sub

Sub JobDone(job As HttpJob)
	
	If (job.Success) Then
		Dim res As String = job.GetString
		If (res = "LogoutSuccess") Then
			logoutSucess = True
		Else
			logoutSucess = False
		End If
	End If
	
End Sub

Private Sub btnAbort_Click
	request.PostString(URL, "method=DELETE_ITEM")
End Sub

