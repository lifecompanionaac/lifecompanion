import subprocess
import os

regResult = subprocess.run(["reg", "query", "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Speech_OneCore\Voices\Tokens"], capture_output=True,text=True)
voicesKeys = regResult.stdout.split('\n')

os.makedirs('source', exist_ok=True)
os.makedirs('modified', exist_ok=True)

for voiceKey in voicesKeys:
    if len(voiceKey) > 1 :
        voiceName = voiceKey[voiceKey.rindex('\\')+1:len(voiceKey)]
        fileName = 'source/' + voiceName +'.reg'
        fileNameModified = 'modified/' + voiceName + '.reg'
        exportResult = subprocess.run(["reg","export",voiceKey,fileName,"/y"], capture_output=True)
        with open(fileName,'r', encoding='utf-16-le') as reader:
            lines = reader.readlines()
            with open(fileNameModified,'w', encoding='utf-16-le') as writer:
                for line in lines:
                    writer.write(line.replace("HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Speech_OneCore\Voices\Tokens\\","HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Speech\Voices\Tokens\\"))
                firstSkip = True
                for line in lines:
                    if firstSkip:
                        firstSkip = False
                    else:
                        writer.write(line.replace("HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Speech_OneCore\Voices\Tokens\\","HKEY_LOCAL_MACHINE\SOFTWARE\WOW6432Node\Microsoft\SPEECH\Voices\Tokens\\"))
        importResult = subprocess.run(["regedit", fileNameModified])
        print(importResult)
        print(fileNameModified+' = '+str(importResult.returncode))