@ECHO OFF
setlocal DISABLEDELAYEDEXPANSION
SET BIN_TARGET=%~dp0/../wsdl2phpgenerator/wsdl2phpgenerator/wsdl2php
php "%BIN_TARGET%" %*
