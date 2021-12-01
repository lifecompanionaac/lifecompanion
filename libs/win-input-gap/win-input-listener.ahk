#NoTrayIcon
#SingleInstance Force

OnChar(InputHook, Char) {
	SendRequest("char", Char)
}

OnKeyUp(InputHook, VK, SC) {
	SendRequest("keyup", VK)
}

OnKeyDown(InputHook, VK, SC) {
	SendRequest("keydown", VK)
}

OnKeyDownBlocked(InputHook, VK, SC) {
	SendRequest("keydownblocked", VK)
}

; ihBlock := InputHook("VI10")
; ihBlock.OnKeyDown := Func("OnKeyDownBlocked")
; ihBlock.KeyOpt(A_Args[2],"NS")
; ihBlock.Start()


; Launch input hook : send keys to active windows and allow non text keys
ihListener := InputHook("VI10")
ihListener.OnChar := Func("OnChar")
ihListener.OnKeyUp := Func("OnKeyUp")
ihListener.OnKeyDown := Func("OnKeyDown")
ihListener.NotifyNonText := True
ihListener.BackspaceIsUndo := False ; handle directly as non text keys
ihListener.KeyOpt(A_Args[2],"NS")
ihListener.Start()

; Try to send to input server (ignore errors)
SendRequest(Path, Data) {
	try {
		whr := ComObjCreate("WinHttp.WinHttpRequest.5.1")
		callUrl := "http://localhost:" . A_Args[1] . "/input-hook/" . Path
		whr.Open("POST", callUrl, true)
		whr.Send(Data)
		whr.WaitForResponse()
	} catch e {
		Return
	}
}