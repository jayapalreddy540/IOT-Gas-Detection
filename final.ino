#include <ESP8266WiFi.h>
#include "ThingSpeak.h"
char* apiWriteKey="8V5SWGIOHT22VJVE";
//char* ReadAPIKey="6BDV50VGDQ2AECCQ";
const char* ssid="hello";   //wifi-ssid
const char* password="1234567890";  // ssid password
char* server="api.thinkspeak.com";
unsigned long channelid = 577841;

 WiFiClient client;

//unsigned long lastUpdate;
float GAS_AO = 0; 
float STATE = 0;

void setup() {
  Serial.begin(9600);
  connectWifi();
  pinMode(BUILTIN_LED,OUTPUT);
  digitalWrite(BUILTIN_LED,HIGH);
  pinMode(GAS_AO,INPUT);
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

int writeTSData(long channelid,unsigned int TSfield1,float data1,char* ReadAPIKey){
  int writeSuccess=ThingSpeak.writeField(channelid,TSfield1,data1,apiWriteKey);
  return writeSuccess;
}
void loop() {
    STATE = analogRead(GAS_AO); 
     Serial.println(STATE);

    if(WiFi.status() != WL_CONNECTED){
    
    connectWifi();  
    }
     writeTSData(channelid, 1,STATE, apiWriteKey);
  Serial.println("Waiting 1 seconds to upload next reading...");
  Serial.println();
    delay(100);
  
}
