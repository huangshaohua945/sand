@echo off
:begin
echo =====================��ѡ������Ҫ�������Ŀ=====================
echo 0 �˳�ϵͳ
echo 1 backend
echo 3 graphic
set/p a=��ѡ���Ӧ������:
if "%a%"=="1" goto :backend
if "%a%"=="3" goto :graphic
if "%a%"=="0" goto :end
echo\
echo �����ֵ��������������
echo\ 
goto :begin

#backend��Ŀ
:backend
::��Ŀ��Ŀ¼
cd ../
::��Ŀ����ģ��
call mvn clean install -Dmaven.test.skip=true
::��������Ŀ¼
cd SAND-WebApplication/backendApp
::����1s������Linux��sleep 1s
ping -n 1 127.0.0.1>nul
::����ɹ�֮���targetĿ¼
call mvn clean install && explorer target
::�ص���Ŀ��Ŀ¼
cd ../
pause
goto :begin

#graphic��Ŀ
:graphic
::��Ŀ��Ŀ¼
cd ../
::��Ŀ����ģ��
call mvn clean install -Dmaven.test.skip=true
::��������Ŀ¼
cd SAND-WebApplication/graphicApp
::����1s������Linux��sleep 1s
ping -n 1 127.0.0.1>nul
::������ɹ�֮���targetĿ¼��cls�������ǰ����Ĵ����Ϣ��
call mvn clean install -Dmaven.test.skip=true && cls && explorer target 
::�ص���Ŀ��Ŀ¼
cd ../
pause
goto :begin
 
:end
exit
