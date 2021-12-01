#NoTrayIcon
#SingleInstance Force
SendLevel 5

mode := A_Args[1]
secondArg := A_Args[2]

if mode = SendRaw
    SendRaw %secondArg%
else if mode = SendUnique
    Send %secondArg%
else if mode = SendMulti
{
    keyCount := A_Args.Length() - 1
    Loop, %keyCount%
    {
        currentKey := A_Args[A_Index + 1]
        Send %currentKey%
    }
}