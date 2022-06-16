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
	
	Private xui As XUI
	Private requestProfile As HttpJob
	Private URL As String
	
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private txtID As EditText
	Private txtName As EditText
	Private txtLastname As EditText
	Private txtEmail As EditText
	Private txtGender As EditText
	Private txtCell As EditText
	Private txtAddr As EditText
	Private txtPassword As EditText
	
	Private btnSave As Button
	Private btnCancel As Button

End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Assign url
	URL = $"http://${Main.serverIPAddress}/SmartBasket/WebAPI/user.php"$
	
	' Init httpjob
	requestProfile.Initialize("requestProfile", Me)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("settings")
	
	' Init user details in profile form
	InitUserProfile
	
End Sub

Sub Activity_Resume
	
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Private Sub btnSave_Click
	
	Dim name As String = txtName.Text
	Dim surname As String = txtLastname.Text
	Dim email As String = txtEmail.Text
	Dim cellNum As String = txtCell.Text
	Dim addr As String = txtAddr.Text
	Dim pass As String = txtPassword.Text
	
	Try
		Dim postStr As String = $"method=UPDATE_USER&firstName=${name}&lastName=${surname}&email=${email}&cellNum=${cellNum}&addr=${addr}&password=${pass}"$
		requestProfile.PostString(URL, postStr)
		Wait For (requestProfile) JobDone(job As HttpJob)
		If job.Success Then
			Dim response As String = job.GetString
			If response = "UpdateSuccess" Then
				Dim dialogRes As Object = xui.Msgbox2Async("Profile updated successfully" & CRLF & "Would you like to revert to the dashboard?", "Update", _
		 											"Yes", "Cancel", "No", Null)
		
				Wait For (dialogRes) Msgbox_Result (Result As Int)
				If Result = xui.DialogResponse_Positive Then
					CallSubDelayed(dashboard, Null)
					Activity.Finish
				End If
			Else If response = "404" Then
				ToastMessageShow("Could not update profile", True)
			End If
		End If
	Catch
		xui.MsgboxAsync(LastException.Message, "Exception")	
	End Try

End Sub

Private Sub btnCancel_Click
	
	CallSubDelayed(dashboard, Null)
	Activity.Finish
	
End Sub

Private Sub InitUserProfile()
	
	txtID.Text = Main.userDetails(1).As(String)
	txtName.Text = Main.userDetails(2).As(String)
	txtLastname.Text = Main.userDetails(3)
	txtGender.Text = Main.userDetails(4)
	txtCell.Text = Main.userDetails(5)
	txtAddr.Text = Main.userDetails(6)
	txtEmail.Text = Main.userDetails(7)
	txtPassword.Text = Main.userDetails(8)
	
	txtName.RequestFocus
End Sub