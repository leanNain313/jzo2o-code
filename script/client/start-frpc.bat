@echo off
cd /d "C:\path\to\your\frp\directory"
start /min .\frpc.exe -c frpc.toml
exit