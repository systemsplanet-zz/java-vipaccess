
This Maven project builds java vipaccess.jar which 
creates an E*Trade Security Token you can 
store in a Yubikey Neo hardware key and
register with E*Trade for Two-Factor Login.

For a single-html-file browser-based port of this project, see:
  See https://github.com/systemsplanet/javascript-vipaccess                         


Build (one time)
    Edit install.cmd to configure Java and Apache Maven:

      set "JAVA_HOME=c:\Program Files\Java\jdk1.8.0_121"
      set "M2_HOME=c:\Program Files\Maven3.2.2"

      
Runtime (one time)
    Execute Run.cmd from a windows command prompt.
    
    Run.cmd builds and runs vipaccess.jar which generates 
    an E*Trade Credential ID (eg VSMT12345689) and 
    displays the location of a generated png image file (open with mspaint) 
    containing a QR Code with the generated TOTP Secret Key.
    
Yubikey Neo Configuration (one time)
    To add the generated E*Trade Secret Key to your Neo hardware key:
        Open the QR Code image file in mspaint using Windows Explorer.
        Open the Yubikey Phone App, select the + at the top right.
        Scan the QR Code from your screen.
    
E*Trade Configuration (one time)
    Login to E*Trade 
        https://us.etrade.com/e/t/user/login
        
    Configure your account (My Profile / Security & Passwords/ Manage Security ID/ SET UP A NEW SECURE ID)
        https://us.etrade.com/e/t/accounts/mfaactivatetoken?FirstTime=YES
            Mobile Phone#:  Enter your phone number    
            Credential ID:  Enter output from Run.cmd (eg VSTM1234566)
            Security Code:  Run the Yubikey App, Scan your Neo key using NFC, enter the displayed 6 digit OTP VIP Access code
            Click Activate

E*Trade Login (each time)
    To Login at E*Trade with Two Factor Auth:
        Open a browser to
            https://us.etrade.com/e/t/user/login
            Enter your User Id
            Enter your Password 
            Open the Yubikey app on your phone
            Scan your Neo Token            
            Add the displayed 6 digit VIP Access code to the end of your Password            
            Click LOG ON
    
  

Example vipaccess.jar Output
       
    02:08:09.318 INFO  com.systemsplanet.vipaccess.cmd.CmdConfig          handleArg       debug enabled from commandline
    02:08:09.318 INFO  com.systemsplanet.vipaccess.cmd.CmdConfig          handleArg       mode:[VSMT]
    02:08:09.318 INFO  com.systemsplanet.vipaccess.cmd.CmdConfig          handleArg       proxyHost:[localhost]
    02:08:09.318 INFO  com.systemsplanet.vipaccess.cmd.CmdConfig          handleArg       proxyPort:[8888]
    02:08:09.318 INFO  com.systemsplanet.vipaccess.VipAccessMain          main            arguments:[-debug false -silent false -mode mobile -proxyHost localhost -proxyPort 8888] isSilent:false isDebug:false mode:VSMT
    02:08:11.056 INFO  com.systemsplanet.vipaccess.VipAccessMain          main            VipToken[status=Success,salt64=xxxxx=,iteration_count=50,iteration_count_int=50,iv64=xxx==,expiry=2020-02-01T07:08:11.026Z,id=VSMTnnnnnnnn,cipher64=xxx=,digest64=xxx=,salt={},iv={},cipher={},digest={}]
    02:08:12.766 INFO  com.systemsplanet.vipaccess.VipAccessMain          main            BE AWARE that this new credential expires on this date: 2020-02-01T07:08:11.026Z
    02:08:12.766 INFO  com.systemsplanet.vipaccess.VipAccessMain          main            E*Trade Id: VSMTnnnnnnnn
    02:08:12.768 INFO  com.systemsplanet.vipaccess.VipQrCode              writeQRCode     otpauth://totp/VIP_Access:VSMTnnnnnnnn?issuer=Symantec&secret=xxxxx&
    02:08:12.769 INFO  com.systemsplanet.vipaccess.VipQrCode              writeQRCode     QR Code image file: C:\Users\xxx\AppData\Local\Temp\qrcode4851386043412303757.png
   

Maven Modules
    app\oath-otp                    fork of https://github.com/johnnymongiat/oath/tree/master/oath-otp                  modified to allow Issuer And Label Account Name to differ
    app\oath-otp-keyprovisioning    fork of https://github.com/johnnymongiat/oath/tree/master/oath-otp-keyprovisioning  modified to allow Issuer And Label Account Name to differ
    app\vipaccess                   main java program


Important Files
    install.cmd                 Setup dev environment variables
    run.cmd                     build and run the app
    
    app\vipaccess\src\main\java\com\systemsplanet\vipaccess\
        VipAccessMain.java      Main Java Entry
        VipConst.java           Constants
        VipEnc.java             Encoding/Encryption methods
        VipQrCode.java          QR Code methods
                

LINKS
    https://www.yubico.com/products/yubikey-hardware/yubikey-neo/
    https://us.etrade.com/security-center
    https://github.com/cyrozap/python-vipaccess/blob/master/vipaccess/utils.py      - Original Python Script this app was ported from
    https://github.com/systemsplanet/javascript-vipaccess                           - single html file browser version 
