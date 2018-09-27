#include <ESP8266WiFi.h>
#include "ThingSpeak.h"
#include <Servo.h>
Servo servo;

char* apiWriteKey="8V5SWGIOHT22VJVE";
//char* ReadAPIKey="6BDV50VGDQ2AECCQ";
const char* ssid="hello";   //wifi-ssid
const char* password="1234567890";  // ssid password
char* server="api.thinkspeak.com";
unsigned long channelid = 577841;
//const String SMSapiKey="5YM3MNH0AERWQLJ3";
//const String sendNumber="7036299791";

 WiFiClient client;

//unsigned long lastUpdate;
float GAS_AO = 0; 
float STATE = 0;
int data2=0;
int data3=0;

void setup() {
  Serial.begin(9600);
  connectWifi();
  pinMode(D2,OUTPUT);
  pinMode(BUILTIN_LED,OUTPUT);
  digitalWrite(BUILTIN_LED,HIGH);
  pinMode(GAS_AO,INPUT);
  servo.attach(D4);
  servo.write(0);
  
}

int connectWifi(){
     WiFi.begin(ssid, password);
    while (WiFi.status()!= WL_CONNECTED) {
       
       delay(2500);
       Serial.println("Connecting to WiFi");
       Serial.print(ssid);
       Serial.println(password);
       
    }
    
    Serial.println("Connected");
    ThingSpeak.begin(client);
  }

int writeTSData(long channelid,unsigned int TSfield1,float data1,unsigned int TSfield2,int data2,unsigned int TSfield3,int data3,char* ReadAPIKey){
  //int writeSuccess=ThingSpeak.writeField(channelid,TSfield1,data1,apiWriteKey);  //for single value
  ThingSpeak.setField(TSfield1,data1);
  ThingSpeak.setField(TSfield2,data2);
  ThingSpeak.setField(TSfield3,data3);
  int writeSuccess=ThingSpeak.writeFields(channelid,apiWriteKey);
  return writeSuccess;
}
void loop() {
    STATE = analogRead(GAS_AO); 
     Serial.println(STATE);
     if(STATE>670)
     {
      digitalWrite(D2,HIGH);
      data2=90;
      servo.write(90);
      data3=90;
      delay(1000);
      servo.write(0);
      delay(1000);
      // sendSMS(sendNumber, "Alert!! Gas Leakage in your house");
     }
     else{
       digitalWrite(D2,LOW);
       data2=0;
       data3=0;
     }

    if(WiFi.status() != WL_CONNECTED)
    {
       connectWifi();  
    }
     writeTSData(channelid, 1,STATE,2,data2,3,data3, apiWriteKey);
  Serial.println("Waiting atmost 3 seconds to upload next reading...");
  Serial.println();
    delay(100);
  
}


