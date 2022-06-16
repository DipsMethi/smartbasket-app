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
	
	Private tts As TTS
	Private requestCardScan, deleteCart As HttpJob
	Private tmr As Timer
	Private xui As XUI
	
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	
	Private lblCard As Label
	Private URL As String = $"http://${Main.serverIPAddress}/SmartBasket/WebAPI/cart.php"$
	
End Sub

Sub Activity_Create(FirstTime As Boolean)
	' Load layout
	Activity.LoadLayout( "scan_card" )
	' Init cardScan
	requestCardScan.Initialize("requestCardScan", Me)
	' Init deleteCART
	deleteCart.Initialize("deleteCart", Me)
	' Init tts
	tts.Initialize( "tts" )
	' Init timer
	tmr.Initialize("tmr", 1000)
	tmr.Enabled = True
	' Set TTS lang
	tts.SetLanguage("en", "us")
	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)
	
End Sub

Sub tts_READY(success As Boolean)
	
	If ( success ) Then
		tts.Speak("Please scan loyalty points card", True )
	End If
	
End Sub

Sub tmr_Tick
	
	Try
		requestCardScan.PostString(URL, "method=GET_CARD_STAT")
		Wait For (requestCardScan) JobDone(job As HttpJob)
		If job.Success Then
			Dim response As String = job.GetString.Trim
			If (response = "0") Then
				lblCard.TextColor = Colors.Green
				tts.Speak("Checkout successful", True)
				xui.MsgboxAsync("Checkout successful.", "Success")				
				AbortCart				
				CallSubDelayed(Main, Null)
				Activity.Finish
				
				tmr.Enabled = False
				Sleep(10000)
				
			End If
		End If
	Catch
		Log(LastException.Message)
	End Try
	
End Sub

Private Sub AbortCart()
	
	Try
		deleteCart.PostString(URL, "method=ABORT_CART")
		Wait For (deleteCart) JobDone(job As HttpJob)
		If job.Success Then
			' Do nothing
		End If
	Catch
		xui.MsgboxAsync(LastException.Message, "Exception")
	End Try
	
End Sub

