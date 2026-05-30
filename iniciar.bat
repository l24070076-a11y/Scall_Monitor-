@echo off
echo Iniciando servidor y ngrok...
start cmd /k "cd /d C:\Users\Angel2427F\Documents\servidor-lluvia && node index.js"
timeout /t 2
start cmd /k "ngrok http --domain=crane-commerce-foam.ngrok-free.dev 3000"