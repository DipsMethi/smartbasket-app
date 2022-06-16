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

	Private tmr As Timer
	Private requestCart As HttpJob
	Private requestTotal As HttpJob
	Private deleteCart As HttpJob
	Private requestCheckout As HttpJob
	
	Private audio As Beeper
	
	Private totalAmount As Double
	
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Private URL As String = $"http://${Main.serverIPAddress}/SmartBasket/WebAPI/cart.php"$
	
	Private itemNames As List
	Private itemPrices As List
	
	Private cSize As Int
	Private pSize As Int
	
	Private lstItems As ListView
	Private btnCheckout As Button
	Private btnAbort As Button
	Private payLoad As String
	
	Private totalAmt As Double = 0
	Private isCartEmpty As Boolean = True
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	
	Activity.LoadLayout("cart")
	
	itemNames.Initialize
	itemPrices.Initialize

	requestCart.Initialize("requestCart", Me)
	requestTotal.Initialize("requestTotal", Me)
	deleteCart.Initialize("deleteCart", Me)
	requestCheckout.Initialize("requestCheckout", Me)
	
	tmr.Initialize("tmr", 1000)
	tmr.Enabled = True
	
	audio.Initialize(50, 500)
	
	cSize = lstItems.Size
	pSize = cSize
	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Private Sub btnBack_Click
	CallSubDelayed(dashboard, Null)
	Activity.Finish
End Sub

Private Sub AddCart(message As String)
	
	Dim cartList As List
	Dim price As Double
	
	lstItems.Clear()
	itemNames.Clear()
	itemPrices.Clear()
	
	Dim message As String = message.Trim()
	
	If(message.EqualsIgnoreCase("No data")) Then
		ToastMessageShow("Cart is empty!", False)
		btnAbort.Enabled = False
	Else
		cartList = Regex.Split("-", message)
		
		For n = 0 To cartList.Size - 1
			Dim item(2) As String = Regex.Split( "&", cartList.Get(n).As( String ) )
			itemNames.Add( item(0).As(String) )
			price = item(1).As(Double)
			itemPrices.Add("R " & price.As(String))
				
			lstItems.AddTwoLines2(itemNames.Get(n), itemPrices.Get(n), n)
		Next

		btnAbort.Color = Colors.Red
		btnCheckout.Color = Colors.Green
		
		btnAbort.Enabled = True
	End If
End Sub

Sub GetTotal(res As String)
	
	Dim amt As Double = res.As(Double)
	totalAmount = Round2(amt, 2)
	btnCheckout.Text = "Checkout R " & totalAmount.As(String)
	
End Sub

Sub tmr_Tick
	
		GetCart
		If(pSize <> cSize) Then
			audio.Beep
			pSize = cSize
		End If
		RequesTotal
	
End Sub

Private Sub btnAbort_Click
	
	Try
		Dim sf As Object = xui.Msgbox2Async("Are you sure you want to abort cart?", "Abort", "Yes", "Cancel", "No", Null)
		Wait For (sf) Msgbox_Result (Result As Int)
		If Result = xui.DialogResponse_Positive Then
			AbortCart
		End If
	Catch
		xui.MsgboxAsync(LastException.Message, "Exception")
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

Private Sub GetCart
	Try
		requestCart.PostString(URL, "method=GET_CART")
		Wait For (requestCart) JobDone(job As HttpJob)
		If job.Success Then
			Dim res As String = job.GetString
			pSize = lstItems.Size
			AddCart(res)
			cSize = lstItems.Size
		End If
	Catch
		Log(LastException)
	End Try
End Sub

Private Sub RequesTotal
	
	Try
		requestTotal.PostString(URL, "method=GET_TOTAL")
		Wait For (requestTotal) JobDone(j As HttpJob)
		If j.Success Then
			Dim res As String = j.GetString
			GetTotal(res)
		End If
	Catch
		Log(LastException)
	End Try
		
End Sub

Private Sub btnCheckout_Click
	
	Try
		requestCheckout.PostString(URL, $"method=CHECKOUT&total=${totalAmount}"$)
		Wait For (requestCheckout) JobDone(job As HttpJob)
		If job.Success Then
			Dim res As String = job.GetString.Trim
			If( res = "CheckoutSuccess") Then
				CallSubDelayed(scancard, Null)
				Activity.Finish
			End If
		End If
	Catch
		xui.MsgboxAsync(LastException.Message, "Exception")
	End Try
	
End Sub